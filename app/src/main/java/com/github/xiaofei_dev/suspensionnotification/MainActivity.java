package com.github.xiaofei_dev.suspensionnotification;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.xiaofei_dev.suspensionnotification.util.RegexText;

import java.util.List;



public class MainActivity extends Activity {

    private EditText title;
    private EditText content;
    private NotificationManager manager;
    private static final String TAG = "MainActivity";
    private int id;
    private int notifID;
    private boolean isChecked;
    private boolean isCheckedBoot;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        title = (EditText)findViewById(R.id.title);
        content = (EditText)findViewById(R.id.content);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setIsChecked();
        setIsCheckedBoot();
        clipBoardMonitor();
        Log.d(TAG, "onCreate: ");
        boolean back = getIntent().getBooleanExtra("moveTaskToBack",false);
        if(back){
            moveTaskToBack(true);
            Log.i(TAG, "onCreate: moveTaskToBack2");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*boolean back = getIntent().getBooleanExtra("moveTaskToBack",false);
        if(back){
            moveTaskToBack(true);
        }*/
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String head = intent.getStringExtra("title");
        String cont = intent.getStringExtra("content");
        notifID = intent.getIntExtra("id",id++);
        title.setText(head);
        content.setText(cont);
        Log.d(TAG, "onNewIntent: "+ head + " "+cont +" "+ notifID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //撤销发出的所有通知
        //manager.cancelAll();
        Log.d(TAG, "onDestroy: ");
    }

    public static Intent newIntent(Context context,String text){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("content",text);
        //此标志将导致已启动的活动被引导到任务的历史堆栈的前面（如果它已经在运行）
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    //按钮点击方法
    public void onClick(View v){
        switch (v.getId()){
            case R.id.negative:
                //manager.cancelAll();
                moveTaskToBack(true);
                break;
            case R.id.positive:
                String head = title.getText().toString();
                String cont = content.getText().toString();
                if(head.equals("") && cont.equals("")){
                    break;
                }
                head = (head.equals("")) ? "无标题":head;
                cont = (cont.equals("")) ? "无内容":cont;

                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("title",head);
                intent.putExtra("content",cont);
                intent.putExtra("id",notifID);
                PendingIntent pi = PendingIntent.getActivity(this,notifID,intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                //manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBulider = new NotificationCompat.Builder(this)
                        .setContentTitle(head)
                        .setContentText(cont)
                        .setSmallIcon(R.drawable.ic_more)
                        .setContentIntent(pi)
                        //.setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(cont));
                        if(android.os.Build.VERSION.SDK_INT >= 21){
                            notificationBulider.setVisibility(Notification.VISIBILITY_PUBLIC);
                        }
                Notification notifications = notificationBulider.build();
                manager.notify(notifID,notifications);
                moveTaskToBack(true);
                break;
            case R.id.regex:
                AlertDialog dialog = new AlertDialog.Builder(this,R.style.AppTheme)
                        .setTitle(getString(R.string.hint))
                        .setPositiveButton(getString(R.string.close),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .create();
                List<String> list = RegexText.regexText(content.getText().toString());
                if(list == null||list.size() == 0){
                    Toast toast = Toast.makeText(this, getString(R.string.alert),
                            Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.TOP,0,0);
                    toast.show();
                    return;
                }
                dialog.setView(getListView(list,dialog));
                dialog.show();
                break;
            case R.id.setting:
                AlertDialog settingDialog = new AlertDialog.Builder(this,R.style.Dialog)
                        .setTitle(getString(R.string.setting))
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .create();
                settingDialog.setView(getSettingView());
                settingDialog.show();
                break;
            default:
                break;
        }
    }

    private View getSettingView(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_setting, null);
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
        checkBox.setChecked(isChecked);
        CheckBox checkBoxBoot = (CheckBox)view.findViewById(R.id.checkBoxBoot);
        checkBoxBoot.setChecked(isCheckedBoot);
        return view;
    }

    public void onSetting(View view){
        switch (view.getId()){
            case R.id.checkBox:
                boolean checked = ((CheckBox) view).isChecked();
                editor.putBoolean("IS_CHECKED",checked);
                editor.apply();
                setIsChecked();
                break;
            case R.id.checkBoxBoot:
                boolean checkedBoot= ((CheckBox) view).isChecked();
                editor.putBoolean("IS_CHECKED_BOOT",checkedBoot);
                editor.apply();
                setIsCheckedBoot();
                break;
            case R.id.about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private View getListView(final List<String> list, final Dialog dialog){
       /* if(list.size() == 0){
            Toast.makeText(this, getString(R.string.alert), Toast.LENGTH_SHORT).show();
            return;
        }*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog, null);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.list_item, list);
        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                content.setText(list.get(position));
                dialog.cancel();

            }
        });
       return rootView;
    }

    private void clipBoardMonitor(){
        //此方法设置监听剪贴板变化，如有新的剪贴内容就启动主活动
        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if(!isChecked){
                    return;
                }else {
                    ClipData clipData = clipBoard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    String text = item.getText().toString();
                    //下面的条件判断是为了兼容与淘宝淘口令的冲突问题。
                    if(text.equals("")){
                        return;
                    }
                    Intent intent = newIntent(MainActivity.this,text);
                    Log.d(TAG, "onPrimaryClipChanged: ");
                    startActivity(intent);
                }
            }
        });
    }
    private void setIsChecked(){
        isChecked = sharedPreferences.getBoolean("IS_CHECKED",true);
    }
    private void setIsCheckedBoot(){
        isCheckedBoot= sharedPreferences.getBoolean("IS_CHECKED_BOOT",true);
    }
}
