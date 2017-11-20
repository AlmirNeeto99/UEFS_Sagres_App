package com.forcetower.uefs.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.services.GenericAccountService;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class SyncUtils {
    private static final String AUTHORITY = "com.forcetower.uefs.providers";
    private static final long SYNC_FREQUENCY = 60L;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PrefUtils.get(context, PREF_SETUP_COMPLETE, false);

        Account account = GenericAccountService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        assert accountManager != null;

        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY, SYNC_FREQUENCY);
            Log.i(Constants.APP_TAG, "Account set");
            newAccount = true;
        }

        if (newAccount || !setupComplete) {
            triggerRefresh();
            PrefUtils.save(context, PREF_SETUP_COMPLETE, true);
        }
    }

    private static void triggerRefresh() {
        Log.i(Constants.APP_TAG, "Trigger Refresh");
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(GenericAccountService.getAccount(), AUTHORITY, b);
    }
}
