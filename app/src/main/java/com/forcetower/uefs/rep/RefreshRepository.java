package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.resources.FetchEnrollCertResource;
import com.forcetower.uefs.sgrs.parsers.SagresLinkFinder;

import org.jsoup.nodes.Document;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import timber.log.Timber;

import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestForEnrollmentCertificate;
import static com.forcetower.uefs.rep.helper.RequestCreator.makeRequestForURL;

/**
 * Created by João Paulo on 08/03/2018.
 */
@Singleton
public class RefreshRepository {
    private final AppDatabase database;
    private final AppExecutors executors;
    private final LoginRepository loginRepository;
    private final OkHttpClient client;
    private final File externalCacheDir;

    @Inject
    RefreshRepository(AppDatabase database, AppExecutors executors, LoginRepository loginRepository,
                      OkHttpClient client, Context context) {
        this.database = database;
        this.executors = executors;
        this.loginRepository = loginRepository;
        this.client = client;
        this.externalCacheDir = context.getCacheDir();
    }

    public LiveData<Resource<Integer>> refreshData() {
        MediatorLiveData<Resource<Integer>> refresh = new MediatorLiveData<>();
        LiveData<Access> accessSrc = database.accessDao().getAccess();
        refresh.addSource(accessSrc, access -> {
            refresh.removeSource(accessSrc);
            //noinspection ConstantConditions
            if (access == null) {
                refresh.postValue(Resource.error("User is disconnected", 401, R.string.disconnected));
                return;
            }
            LiveData<Resource<Integer>> loginRes = loginRepository.login(access.getUsername(), access.getPassword());
            refresh.addSource(loginRes, integerResource -> {
                //noinspection ConstantConditions
                if (integerResource.status == Status.SUCCESS) {
                    refresh.removeSource(loginRes);
                    refresh.postValue(Resource.success(R.string.updated));
                } else if (integerResource.status == Status.ERROR) {
                    refresh.removeSource(loginRes);
                    int code = integerResource.code;
                    if (code == 401) {
                        Timber.d("User disconnected");
                        //Disconnects the user because validation failed
                        executors.diskIO().execute(() -> database.accessDao().deleteAllAccesses());
                    }
                } else {
                    refresh.postValue(integerResource);
                }
            });
        });
        return refresh;
    }

    public LiveData<Resource<Integer>> loginAndDownloadEnrollmentCertificate() {
        MediatorLiveData<Resource<Integer>> progress = new MediatorLiveData<>();
        LiveData<Access> accessSrc = database.accessDao().getAccess();
        progress.addSource(accessSrc, access -> {
            progress.removeSource(accessSrc);
            if (access == null) {
                progress.postValue(Resource.error("User is disconnected", 401, R.string.disconnected));
                return;
            }

            LiveData<Resource<Integer>> loginSrc = loginRepository.loginOnly(access.getUsername(), access.getPassword());
            progress.addSource(loginSrc, resource -> {
                //noinspection ConstantConditions
                if (resource.status == Status.LOADING) {
                    progress.postValue(resource);
                } else {
                    progress.removeSource(loginSrc);
                    if (resource.status == Status.ERROR) {
                        progress.postValue(resource);
                    } else {
                        progress.postValue(Resource.loading(R.string.going_to_enrollment_page));
                        LiveData<Resource<Integer>> enrollSrc = fetchEnrollmentCertificate();
                        progress.addSource(enrollSrc, enrollRsc -> {
                            //noinspection ConstantConditions
                            if (enrollRsc.status != Status.LOADING) {
                                progress.removeSource(enrollSrc);
                            }
                            progress.postValue(enrollRsc);
                        });
                    }
                }
            });
        });

        return progress;
    }

    private LiveData<Resource<Integer>> fetchEnrollmentCertificate() {
        return new FetchEnrollCertResource(executors) {

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            protected boolean downloadFile(Response response) {
                File downloadedFile = new File(externalCacheDir, Constants.ENROLLMENT_CERTIFICATE_FILE_NAME);
                try {
                    if (downloadedFile.exists()) downloadedFile.delete();
                    downloadedFile.createNewFile();

                    BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                    sink.writeAll(response.body().source());
                    sink.close();
                    return true;
                } catch (Exception e) {
                    downloadedFile.delete();
                    return false;
                }
            }

            @Override
            protected Call createDownloadCall(String link) {
                Request request = makeRequestForURL(link);
                return client.newCall(request);
            }

            @Override
            protected String findEnrollmentLink(@NonNull Document document) {
                String link = SagresLinkFinder.findForEnrollment(document);
                Timber.d("Found link: %s", link);
                if (URLUtil.isHttpUrl(link) || URLUtil.isHttpsUrl(link))
                    return link;
                return null;
            }

            @Override
            protected Call createEnrollmentCertificateCall() {
                Request request = makeRequestForEnrollmentCertificate();
                return client.newCall(request);
            }
        }.asLiveData();
    }
}
