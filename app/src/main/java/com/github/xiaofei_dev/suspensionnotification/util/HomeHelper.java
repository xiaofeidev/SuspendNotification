package com.github.xiaofei_dev.suspensionnotification.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.github.xiaofei_dev.suspensionnotification.util.bean.HomeData;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by pandajoy on 17-1-12.
 */

public class HomeHelper extends Observable {

    private static final Object sLock = new Object();
    private static HomeHelper sInstance;
    private Context mContext;
    private HomeReceiver mReceiver = new HomeReceiver();
    private HomeData data = new HomeData();

    private HomeHelper(Context context) {
        mContext = context.getApplicationContext();// 一定要取ApplicationContext，避免资源无法释放
    }

    public static HomeHelper getInstance(Context context) {
        synchronized (sLock) {
            if (null == sInstance) {
                sInstance = new HomeHelper(context);
            }
            return sInstance;
        }
    }

    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
        if (countObservers() > 0 && !mReceiver.isRegisted()) {
            mReceiver.register(mContext);
        }
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
        if (countObservers() == 0 && mReceiver.isRegisted())
            mReceiver.unregister(mContext);
    }

    class HomeReceiver extends BroadcastReceiver {

        private final String SYSTEM_DIALOG_REASON_KEY = "reason";

        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        private boolean mRegisted = false;

        public boolean isRegisted() {
            return mRegisted;
        }

        public void unregister(Context context) {
            if (context != null) {
                context.unregisterReceiver(this);
                mRegisted = false;
            }
        }

        public void register(Context context) {
            if (context != null) {
                IntentFilter homeFilter = new IntentFilter();
                homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.registerReceiver(this, homeFilter);
                mRegisted = true;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    data.setClickHome(reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY));
                    data.setClickMenu(reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS));
                    setChanged();
                    notifyObservers(data);
                }
            }
        }
    }
}
