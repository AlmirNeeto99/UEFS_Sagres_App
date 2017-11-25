package com.forcetower.uefs.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClass;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;
import com.forcetower.uefs.sagres_sdk.parsers.SagresParser;
import com.forcetower.uefs.sagres_sdk.parsers.SagresScheduleParser;

import java.util.Calendar;
import java.util.HashMap;

import static com.forcetower.uefs.Constants.APP_TAG;

public class ParsingActivity extends UEFSBaseActivity {
    private TextView tv_information;
    private boolean delayed;

    public static void startActivity(Context context, String html, boolean delayed) {
        Intent intent = new Intent(context, ParsingActivity.class);
        intent.putExtra("html", html);
        intent.putExtra("delayed", delayed);

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);

        tv_information = findViewById(R.id.tv_information);
        tv_information.setText(R.string.processing_info);
        Utils.fadeIn(tv_information, this);

        delayed = getIntent().getBooleanExtra("delayed", false);
        startLoadHtml(getIntent().getStringExtra("html"));
    }

    private void startLoadHtml(final String html) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (delayed) Thread.sleep(500);
                    fadeOut(tv_information);

                    String name = SagresParser.getUserName(html);

                    if (delayed) Thread.sleep(200);
                    updateInformation(getString(R.string.welcome, name));
                    fadeIn(tv_information);

                    if (delayed) Thread.sleep(1000);
                    fadeOut(tv_information);
                    if (delayed) Thread.sleep(100);
                    updateInformation(getString(R.string.finding_class_structure));
                    fadeIn(tv_information);

                    SagresScheduleParser.findSchedule(html);
                    updateInformation(getString(R.string.finding_class_details));

                    HashMap<String, SagresClass> classHashMap = SagresScheduleParser.findDetails(html);

                    updateInformation(getString(R.string.completed));


                    completeActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setupNotifications(HashMap<String, SagresClass> classHashMap) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(APP_TAG, "Alarm Manager is null");
            Toast.makeText(ParsingActivity.this, R.string.alarm_manager_null, Toast.LENGTH_SHORT).show();
        } else {
            for (SagresClass sagresClass : classHashMap.values()) {
                for (SagresClassDay classDay : sagresClass.getDays()) {
                    Calendar start = classDay.getStart();
                    Intent myIntent = new Intent("Class Notification");
                    myIntent.putExtra("name", sagresClass.getName());
                    myIntent.putExtra("room", classDay.getAllocatedRoom());
                    myIntent.putExtra("modulo", classDay.getModulo());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ParsingActivity.this, 0, myIntent, 0);
                    alarmManager.set(AlarmManager.RTC, start.getTimeInMillis(), pendingIntent);
                }
            }

            Calendar next = Calendar.getInstance();
            next.add(Calendar.MINUTE, 1);
            Intent test = new Intent("Class Notification");
            test.putExtra("name", "Testing the App");
            test.putExtra("room", "PAV99");
            test.putExtra("modulo", "Modulo 0");
            Log.i(APP_TAG, "Default Test Notification fired");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(ParsingActivity.this, 0, test, 0);
            alarmManager.set(AlarmManager.RTC, next.getTimeInMillis(), pendingIntent);
        }
    }

    private void completeActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConnectedActivity.startActivity(ParsingActivity.this);
                finish();
            }
        });
    }

    private void fadeOut(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeOut(view, ParsingActivity.this);
            }
        });
    }

    private void fadeIn(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeIn(view, ParsingActivity.this);
            }
        });
    }

    private void updateInformation(final String information) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_information.setText(information);
            }
        });
    }
}
