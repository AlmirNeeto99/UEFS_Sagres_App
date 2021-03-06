package com.forcetower.uefs.sync;

import android.accounts.Account;
import android.arch.lifecycle.LiveData;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.dao.GradeInfoDao;
import com.forcetower.uefs.db.dao.MessageDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.LoginRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;

import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import timber.log.Timber;

import static com.forcetower.uefs.util.SyncUtils.syncCheckMainUpdater;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class SagresSyncAdapter extends AbstractThreadedSyncAdapter {
    private final LoginRepository repository;
    private final AppDatabase database;
    private final Context context;
    private final AppExecutors executors;
    private final OkHttpClient client;
    private LiveData<Resource<Integer>> call;

    @Inject
    public SagresSyncAdapter(Context context, LoginRepository repository, AppDatabase database, AppExecutors executors, OkHttpClient client) {
        super(context, true);
        this.context = context;
        this.repository = repository;
        this.database = database;
        this.executors = executors;
        this.client = client;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.d("Perform update called");

        executors.networkIO().execute(() -> {
            if (!BuildConfig.DEBUG && !syncCheckMainUpdater(client)) {
                Timber.d("Application is paused");
                return;
            }

            Timber.d("Application is running");

            executors.diskIO().execute(() -> {
                Access access = database.accessDao().getAccessDirect();
                if (access != null) {
                    Timber.d("Performing update");
                    database.profileDao().setLastSyncAttempt(System.currentTimeMillis());
                    repository.deleteAllMessagesNotifications();
                    repository.deleteAllGradesNotifications();
                    executors.mainThread().execute(() -> {
                        call = repository.login(access.getUsername(), access.getPassword());
                        call.observeForever(this::onSyncProgress);
                    });
                } else {
                    Timber.d("Sync stopped due to no access");
                    NotificationCreator.createNotConnectedNotification(context);
                }
            });
        });

    }

    private void onSyncProgress(Resource<Integer> resource) {
        if (resource == null) {
            Timber.d("Progress is null... Funny");
            return;
        }

        if (resource.status == Status.LOADING) {
            //noinspection ConstantConditions
            Timber.d("Progress Update: %s", context.getString(resource.data));
        } else if (resource.status == Status.SUCCESS) {
            Timber.d("Sync finished");
            if (call != null) {
                Timber.d("Removed observer");
                call.removeObserver(this::onSyncProgress);
                createNotifications();
            }
        } else {
            Timber.d("Sync failed");
            Timber.d("Error: %s", resource.message);
            if (call != null) {
                Timber.d("Removed observer");
                call.removeObserver(this::onSyncProgress);
                createNotifications();
            }
        }
    }

    private void createNotifications() {
        executors.diskIO().execute(() -> {
            messagesNotifications();
            gradesNotifications();
        });
    }

    private void messagesNotifications() {
        MessageDao messageDao = database.messageDao();
        Timber.d("Generate message notifications");
        List<Message> messages = messageDao.getAllUnnotifiedMessages();
        for (Message message : messages) {
            boolean notified = NotificationCreator.createMessageNotification(context, message);
            if (notified) {
                message.setNotified(1);
            }
        }
        if (!messages.isEmpty()) {
            Message[] array = new Message[messages.size()];
            messages.toArray(array);
            messageDao.insertMessages(array);
        }
    }

    private void gradesNotifications() {
        List<Semester> semesters = database.semesterDao().getAllSemestersDirect();
        Semester semester = Semester.getCurrentSemester(semesters);
        String name = semester.getName();

        Timber.d("Those are the notified for the semester: %s", database.gradeInfoDao().getNotifiedGradesFromSemester(name));

        GradeInfoDao infoDao = database.gradeInfoDao();
        Timber.d("Generate grades notifications for grades posted");
        gradesNotificationHandler(infoDao, infoDao.getUnnotifiedGrades(name), 1);
        Timber.d("Generate grades notifications for recently created grades");
        gradesNotificationHandler(infoDao, infoDao.getRecentlyCreatedGrades(name), 2);
        Timber.d("Generate grades notifications for changed grades");
        gradesNotificationHandler(infoDao, infoDao.getAvDateChangedGrades(name), 3);
    }

    private void gradesNotificationHandler(GradeInfoDao infoDao, List<GradeInfo> infos, int type) {
        Timber.d("Number of notifications: %d", infos.size());
        for (GradeInfo info : infos) {
            findClass(info);
            boolean notified = NotificationCreator.createGradeNotification(context, info, type);
            if (notified) info.setNotified(0);
        }

        if (!infos.isEmpty()) {
            Timber.d("This should be the same error: %s", infos);
            GradeInfo[] array = new GradeInfo[infos.size()];
            infos.toArray(array);
            infoDao.insertGradeInfo(array);
        }
    }

    private void findClass(GradeInfo info) {
        GradeSection section = database.gradeSectionDao().getSectionByIdDirect(info.getSection());
        Discipline discipline = database.disciplineDao().getDisciplinesByIdDirect(section.getDiscipline());
        info.setClassName(discipline.getName());
    }
}
