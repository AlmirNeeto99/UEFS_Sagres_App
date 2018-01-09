package com.forcetower.uefs.sagres.tasks;

import android.util.Log;

import com.forcetower.uefs.database.AppDatabase;

import org.jsoup.nodes.Document;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 08/01/2018.
 */

public class DownloadFullInformation extends DownloadSimpleInformation {
    public DownloadFullInformation(AppDatabase database, String username, String password) {
        super(database, username, password);
    }

    @Override
    protected void onDownloadGrades(Document document) {
        Log.d(APP_TAG, "On Download Grades Full information");
    }

    @Override
    protected void onFinishTask() {
        super.onFinishTask();
        Log.d(APP_TAG, "Full Information task on Finish");
    }
}
