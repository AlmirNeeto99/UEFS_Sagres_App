package com.forcetower.uefs.view.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import butterknife.BindView;

public class AboutActivity extends UBaseActivity {
    @BindView(R.id.version_info)
    TextView versionInfo;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_about, savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.about_app_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String version = "0.0u";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {}
        versionInfo.setText(getString(R.string.creator, version));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void thirdParty(View view) {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .start(this);
    }
}
