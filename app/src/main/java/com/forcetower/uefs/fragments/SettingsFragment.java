package com.forcetower.uefs.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.LoginActivity;
import com.forcetower.uefs.helpers.SyncUtils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Preference logoff = findPreference("logoff_key");
        if (logoff != null) logoff.setOnPreferenceClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Preference logoff = findPreference("logoff_key");
        if (logoff != null) logoff.setOnPreferenceClickListener(this);

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference notification = findPreference("show_message_notification");
        Preference syncFrequency = findPreference("sync_frequency");

        if (key.equalsIgnoreCase("sync_frequency")) {
            String newValue = sharedPreferences.getString(key, "");
            if (!newValue.trim().isEmpty()) {
                int frequency = Integer.parseInt(newValue);

                if (frequency == -1) {
                    syncFrequency.setSummary(R.string.pref_sync_frequency_never);
                    notification.setEnabled(false);
                    SyncUtils.stopPeriodicSync();
                } else if (frequency > 0) {
                    syncFrequency.setSummary(R.string.pref_sync_frequency_enabled);
                    notification.setEnabled(true);
                    SyncUtils.setNewPeriodForSync(frequency);
                }
            }
        }

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        SagresPortalSDK.logout();
        SyncUtils.stopPeriodicSync();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ActivityCompat.finishAffinity(getActivity());
        return true;
    }
}