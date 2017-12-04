package com.github.xiaofei_dev.suspensionnotification.backstage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.xiaofei_dev.suspensionnotification.ui.activity.MainActivity;
import com.github.xiaofei_dev.suspensionnotification.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public final class MyService extends Service {
    /**
     * 控制悬浮图标
     */
    private LinearLayout iconFloatView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private String text = "" ;
    private boolean isAddView;
    private Handler mHandler;
    private Runnable mAutoRemoveView;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        iconFloatView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.floating_icon,null);
        iconFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String text = intent.getStringExtra("TEXT");
                Intent mainActivityIntent = MainActivity.newIntent(MyService.this,text);
                mainActivityIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(mainActivityIntent);
                removeView();
                //已主动调用移除悬浮窗方法的情况下，取消自动延时移除悬浮窗
                mHandler.removeCallbacks(mAutoRemoveView);
            }
        });

        mWindowManager = (WindowManager)(getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        mLayoutParams = new WindowManager.LayoutParams();

//        Point point = new Point();
//        mWindowManager.getDefaultDisplay().getSize(point);
//        int screenWidth = point.x;
//        int screenHeight = point.y;
//        mLayoutParams.x = screenWidth;
//        mLayoutParams.y = screenHeight/2;
        mLayoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.END;
        //mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.width = iconFloatView.findViewById(R.id.floating_icon).getLayoutParams().width;
        mLayoutParams.height = iconFloatView.findViewById(R.id.floating_icon).getLayoutParams().height;
//        mWindowManager.addView(iconFloatView,mLayoutParams);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent != null){
            text = intent.getStringExtra("TEXT");
        }
        //mWindowManager.removeView(iconFloatView);
        if(isAddView){
            removeView();
            //已主动调用移除悬浮窗的情况下，取消自动延时移除悬浮窗
            mHandler.removeCallbacks(mAutoRemoveView);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getApplication().startService(intent);
                }
            },510);
        }else {
            addView();
        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                addView();
//            }
//        },600);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private synchronized void addView(){
        if(!isAddView){

            iconFloatView.clearAnimation();
            iconFloatView.setAlpha(0);
            iconFloatView.setVisibility(View.VISIBLE);
            iconFloatView.animate().alpha(1).setDuration(500)
                    .start();
            mWindowManager.addView(iconFloatView,mLayoutParams);
            isAddView = true;
            mHandler.postDelayed(mAutoRemoveView = new Runnable() {
                @Override
                public void run() {
                    if(isAddView){
                        removeView();
                    }
                }
            },2500);
        }

    }

    private synchronized void removeView(){
        if(isAddView){
            iconFloatView.clearAnimation();

            iconFloatView.setAlpha(1);
//            iconFloatView.setOnTouchListener(null);
//            iconFloatView.setScaleX(0);
//            iconFloatView.setScaleY(0);
            iconFloatView.setVisibility(View.VISIBLE);
            iconFloatView.animate().alpha(0)/*.scaleX(1.0f).scaleY(1.0f)*/.setDuration(500)
//                .setInterpolator(new AnticipateOvershootInterpolator())
//                .setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mWindowManager.removeView(iconFloatView);
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })
                    .start();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isAddView){
                        mWindowManager.removeView(iconFloatView);
                        isAddView = false;
                    }
                }
            },500);

        }
    }
}
