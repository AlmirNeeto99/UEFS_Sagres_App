package com.forcetower.uefs.sagres.tasks;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Pair;

import com.forcetower.uefs.content.MediatorResource;
import com.forcetower.uefs.database.AppDatabase;
import com.forcetower.uefs.database.entities.ACalendarItem;
import com.forcetower.uefs.database.entities.AScrap;
import com.forcetower.uefs.sagres.SagresGeneral;
import com.forcetower.uefs.sagres.connectors.SagresPages;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;
import com.forcetower.uefs.sagres_sdk.parsers.SagresCalendarParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresClassParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresFullClassParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresGradesParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresMessagesParser;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;


/**
 * Created by João Paulo on 08/01/2018.
 */

public class DownloadSimpleInformation implements Runnable, LoginDictionary {
    private final MutableLiveData<MediatorResource<Integer>> liveData = new MutableLiveData<>();
    private final AppDatabase database;

    protected String username;
    protected String password;
    protected String studentName;
    protected String studentScore;
    protected List<SagresClassDetails> details;
    protected List<AScrap> messages;
    protected List<ACalendarItem> calendar;
    protected Pair<SagresSemester, List<SagresGrade>> currentGrades;
    protected HashMap<String, List<SagresClassDay>> classesPerDay;

    public DownloadSimpleInformation(AppDatabase database, String username, String password) {
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        //Code 0 -> Just Started the work
        liveData.postValue(MediatorResource.loading(STARTED));

        for (int i = 0; i < 3; i++) {
            Document login = SagresPages.login(username, password);
            if (login != null) {
                connectedProcedure(login);
                return;
            }

            liveData.postValue(MediatorResource.loading(ATT_LOGIN, Integer.toString(i + 1)));
        }

        liveData.postValue(MediatorResource.error("Unable to connect to login", FAILED_CONNECT_LOGIN));
    }

    private void connectedProcedure(Document document) {
        if (!SagresGeneral.isLoginSuccessful(document)) {
            liveData.postValue(MediatorResource.error("Login Failed", INCORRECT_LOGIN));
            return;
        }

        liveData.postValue(MediatorResource.loading(CORRECT_LOGIN));

        studentName = SagresGeneral.getName(document);
        studentScore = SagresGeneral.getScore(document);

        liveData.postValue(MediatorResource.loading(FOUND_NAME, studentName));

        for (int i = 0; i < 3; i++) {
            Document page = SagresPages.getStudentPage();
            if (page != null) {
                onStudentPageProcedures(page);
                return;
            }
            liveData.postValue(MediatorResource.loading(ATT_STUDENT_PAGE, Integer.toString(i + 1)));
        }

        liveData.postValue(MediatorResource.error("Unable to connect to the Sagres Student Page", FAILED_CONNECT_STUDENT));
    }

    private void onStudentPageProcedures(Document document) {
        details = SagresFullClassParser.getClassesDetails(document, null, null, null, true);
        messages = SagresMessagesParser.getStartPageScraps(document.html());
        calendar = SagresCalendarParser.getCalendar(document);
        classesPerDay = SagresClassParser.getCompleteSchedule(document);
        liveData.postValue(MediatorResource.loading(CONNECTED_STUDENT_PAGE, null));

        Document gradesPage = SagresPages.getGradesPage();
        onDownloadGrades(gradesPage);
        onFinishTask();
    }

    protected void onDownloadGrades(Document document) {
        currentGrades = SagresGradesParser.extractGrades(document);
        liveData.postValue(MediatorResource.loading(CONNECTED_GRADES_PAGE, null));
    }

    protected void onFinishTask() {}

    public LiveData<MediatorResource<Integer>> getLiveData() {
        return liveData;
    }
}
