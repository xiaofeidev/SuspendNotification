package com.github.xiaofei_dev.suspensionnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MyReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean isCheckedBoot = mSharedPreferences.getBoolean("IS_CHECKED_BOOT",true);
        if (intent.getAction().equals(ACTION) && isCheckedBoot) {
            Intent mainActivityIntent = new Intent(context,MainActivity.class);
            mainActivityIntent.putExtra("moveTaskToBack",true);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(mainActivityIntent);
        }
    }
}
