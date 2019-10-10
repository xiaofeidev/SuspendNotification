package com.github.xiaofei_dev.suspensionnotification.backstage;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.github.xiaofei_dev.suspensionnotification.R;
import com.github.xiaofei_dev.suspensionnotification.ui.activity.MainActivity;

public class ActiveService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,-1,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBulider = new NotificationCompat.Builder(this, "default")
                .setContentTitle(getString(R.string.add_new_title))
                .setContentText(getString(R.string.add_new_content))
                .setSmallIcon(R.drawable.ic_more)
                .setColor(Color.parseColor("#00838F"))
                .setContentIntent(pi)
                .setOngoing(true)
                //将通知的 Priority 设置为 PRIORITY_MIN 后，通知的小图标将不在状态栏显示，而且锁屏界面也会无法显示
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = notificationBulider.build();
        startForeground(-1,notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
