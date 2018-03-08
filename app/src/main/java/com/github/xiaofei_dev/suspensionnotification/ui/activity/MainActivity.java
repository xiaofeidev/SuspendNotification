package com.github.xiaofei_dev.suspensionnotification.ui.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.github.xiaofei_dev.suspensionnotification.R;
import com.github.xiaofei_dev.suspensionnotification.backstage.MyService;
import com.github.xiaofei_dev.suspensionnotification.util.RegexText;
import com.github.xiaofei_dev.suspensionnotification.util.ToastUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MainActivity extends AppCompatActivity {
    private EditText title;
    private EditText content;
    private NotificationManagerCompat manager;
    private int id = 1;
    private int notifID;
    private boolean isChecked;
    private boolean isCheckedBoot;
    private boolean isCheckedHideIcon;
    private boolean isCheckedHideNew;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final List<Integer> positions = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private static final String IS_CHECKED = "IS_CHECKED";
    private static final String IS_CHECKED_BOOT = "IS_CHECKED_BOOT";
    private static final String IS_CHECKED_HIDE_ICON = "IS_CHECKED_HIDE_ICON";
    private static final String IS_CHECKED_HIDE_NEW = "IS_CHECKED_HIDE_NEW";
    public static int OVERLAY_PERMISSION_REQ_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initChannels();
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        title = (EditText)findViewById(R.id.title);
        content = (EditText)findViewById(R.id.content);
        manager = NotificationManagerCompat.from(this);
        setIsChecked();
        setIsCheckedBoot();
        setCheckedHideIcon();
        setCheckedHideNew();
        clipBoardMonitor();
        Log.d(TAG, "onCreate: ");
        boolean back = getIntent().getBooleanExtra("moveTaskToBack",false);
        if(back){
            moveTaskToBack(true);
            //Log.i(TAG, "onCreate: veTaskToBack");
        }
        //当前活动被销毁后再重建时保证调用onNewIntent(）方法
        onNewIntent(getIntent());
        if (!isCheckedHideNew){
            notifAddNew();
        }

        //权限自检
        if(Build.VERSION.SDK_INT >= 23){
            if(Settings.canDrawOverlays(this)){
//                //有悬浮窗权限则开启服务
//                clipBoardMonitor();
//                ToastUtil.showToast(this,getString(R.string.begin));
                //有悬浮窗权限则只弹出提示消息
//                ToastUtil.showShort(getString(R.string.begin));
            }else {
                //没有悬浮窗权限,去开启悬浮窗权限
                ToastUtils.showShort("您需要授予应用在其他应用上层显示的权限才可正常使用");
                try{
                    Intent  intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if(Build.VERSION.SDK_INT>=23) {
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtils.showShort("获取权限失败，应用将无法工作");
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"获取权限成功！应用可以正常使用了",Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         *如果从图标打开此活动，则传进来的 Intent 将不携带任何 Extra ，
         *若是这样，获取到的文本将都为 "" ，notifID 将产生一个新的值,即 id++
         */
        String head = intent.getStringExtra("title");
        String cont = intent.getStringExtra("content");
        notifID = intent.getIntExtra("id",id++);
        title.setText(head);
        content.setText(cont);
        Log.d(TAG, "onNewIntent: "+ head + " "+cont +" "+ notifID);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this,MyService.class);
        stopService(intent);
        //撤销发出的所有通知
        //manager.cancelAll();
    }

    public static Intent newIntent(Context context,String text){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("content",text);
        //此标志将导致已启动的活动被引导到任务的历史堆栈的前面（如果它已经在运行）
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    /**
     *desc：主界面按钮点击方法
     */
    public final void onClick(View v){
        switch (v.getId()){
            case R.id.negative:
                //manager.cancelAll();
                manager.cancel(notifID);
                moveTaskToBack(true);
                break;
            case R.id.positive:
                String head = title.getText().toString();
                String cont = content.getText().toString();
                if(head.equals("") && cont.equals("")){
                    break;
                }
                head = (head.equals("")) ? "待办事项":head;
                cont = (cont.equals("")) ? "":cont;

                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("title",head);
                intent.putExtra("content",cont);
                intent.putExtra("id",notifID);
                PendingIntent pi = PendingIntent.getActivity(this,notifID,intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                //manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBulider = new NotificationCompat.Builder(this, "default")
                        .setContentTitle(head)
                        .setContentText(cont)
                        .setSmallIcon(R.drawable.ic_more)
                        .setContentIntent(pi)
                        .setOngoing(true)
                        //将通知的 Priority 设置为 PRIORITY_MIN 后，通知的小图标将不在状态栏显示,而且锁屏界面也会无法显示
                        //.setPriority(NotificationCompat.PRIORITY_MIN)
                        //.setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(cont))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                        if(isCheckedHideIcon){
                            notificationBulider.setPriority(NotificationCompat.PRIORITY_MIN);
                        }else {
                            notificationBulider.setPriority(NotificationCompat.PRIORITY_HIGH);

                        }
                Notification notifications = notificationBulider.build();
                manager.notify(notifID,notifications);
                moveTaskToBack(true);
                break;
            case R.id.regex:
                final List<String> list = RegexText.regexText(content.getText().toString());
                if(list == null||list.size() == 0){
                    ToastUtils.showShort(R.string.alert);
                    return;
                }
                AlertDialog dialog = new AlertDialog.Builder(this,R.style.SplitDialog)
                        .setTitle(getString(R.string.hint))
                        .setPositiveButton(getString(R.string.select_done),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Collections.sort(positions);
                                        StringBuilder contents = new StringBuilder();
                                        for(int position : positions){
                                            contents.append(list.get(position));
                                        }
                                        content.setText(contents.toString());
                                        positions.clear();
                                        dialog.cancel();
                                    }
                                })
                        .create();
                dialog.setView(getListView(list));
                dialog.show();
//                String text = content.getText().toString();
//                if (!text.equals("")){
//                    Intent splitActivityIntent = SplitActivity.newIntent(this,text);
//                    startActivity(splitActivityIntent);
//                }
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
    /**
     *desc：返回分句对话框列表视图
     */
    private View getListView(final List<String> list){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.dialog_list, null);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.list_item, list);
        ListView listView = (ListView)rootView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
//                if(view.getBackground() == getResources().getDrawable(R.drawable.button_default)){
//                    view.setBackgroundResource(R.drawable.button_pressed);
//                }else {
//                    view.setBackgroundResource(R.drawable.button_default);
//                }
                if(positions.contains(position)){
                    //因为 positions 列表存储的是 Integer 类型 ，所以需要想办法避免自动拆箱
                    Object object = Integer.valueOf(position);
                    positions.remove(object);
                    view.setBackgroundResource(R.color.transparent);
                }else {
                    positions.add(position);
                    view.setBackgroundResource(R.color.white_translucent);
                }

//                content.setText(list.get(position));
//                dialog.cancel();

            }
        });
        return rootView;
    }

    /**
     *desc：返回设置视图
     */
    private View getSettingView(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_setting, null);
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_box);
        checkBox.setChecked(isChecked);
        CheckBox checkBoxBoot = (CheckBox)view.findViewById(R.id.check_box_boot);
        checkBoxBoot.setChecked(isCheckedBoot);
        CheckBox checkHideIcon = (CheckBox)view.findViewById(R.id.check_hide_icon);
        checkHideIcon.setChecked(isCheckedHideIcon);
        CheckBox checkHideNew = (CheckBox)view.findViewById(R.id.check_hide_new);
        checkHideNew.setChecked(isCheckedHideNew);
        return view;
    }

    /**
     *desc：设置对话框按钮方法
     */
    public void onSetting(View view){
        switch (view.getId()){
            case R.id.check_box:
                boolean checked = ((CheckBox) view).isChecked();
                editor.putBoolean(IS_CHECKED,checked);
                editor.apply();
                setIsChecked();
                break;
            case R.id.check_box_boot:
                boolean checkedBoot= ((CheckBox) view).isChecked();
                editor.putBoolean(IS_CHECKED_BOOT,checkedBoot);
                editor.apply();
                setIsCheckedBoot();
                break;
            case R.id.check_hide_icon:
                boolean checkedHideIcon= ((CheckBox) view).isChecked();
                editor.putBoolean(IS_CHECKED_HIDE_ICON,checkedHideIcon);
                editor.apply();
                setCheckedHideIcon();
                if(checkedHideIcon){
                    ToastUtils.showLong(R.string.hide_icon_alert);
                }
                break;
            case R.id.check_hide_new:
                boolean checkedHideNew= ((CheckBox) view).isChecked();
                editor.putBoolean(IS_CHECKED_HIDE_NEW,checkedHideNew);
                editor.apply();
                setCheckedHideNew();
                if (checkedHideNew){
                    manager.cancel(-1);
                }else {
                    notifAddNew();
                }
                break;
            case R.id.cancel_all:
                manager.cancelAll();
                if (!isCheckedHideNew){
                    notifAddNew();
                }
                ToastUtils.showShort(R.string.cancel_all_done);
                break;
            case R.id.about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     *desc：此方法发出一条快捷添加新通知的通知
     */
    private void  notifAddNew(){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,-1,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBulider = new NotificationCompat.Builder(this, "default")
                .setContentTitle(getString(R.string.add_new_title))
                .setContentText(getString(R.string.add_new_content))
                .setSmallIcon(R.drawable.ic_more)
                .setColor(Color.parseColor("#00838F"))
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentIntent(pi)
                .setOngoing(true)
                //将通知的 Priority 设置为 PRIORITY_MIN 后，通知的小图标将不在状态栏显示，而且锁屏界面也会无法显示
                .setPriority(NotificationCompat.PRIORITY_MIN);
        Notification notification = notificationBulider.build();
        manager.notify(-1,notification);
    }

    /**
     *desc：此方法设置监听剪贴板变化，如有新的剪贴内容就启动主活动
     */
    private void clipBoardMonitor(){
        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if(!isChecked){
                    return;
                }else {
                    ClipData clipData = clipBoard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    //String text = item.getText().toString();//万一复制到剪贴板的不是纯文本，此方法将导致程序崩溃
                    String text = item.coerceToText(MainActivity.this).toString();//
                    //下面的条件判断是为了防止与淘宝淘口令的冲突问题。
                    if(text.equals("")){
                        return;
                    }

                    Intent intent = new Intent(MainActivity.this,MyService.class);
                    intent.putExtra("TEXT",text);
                    MainActivity.this.startService(intent);
//                    Intent intent = newIntent(MainActivity.this,text);
//                    Log.d(TAG, "onPrimaryClipChanged: ");
//                    startActivity(intent);
                }
            }
        });
    }

    /**
     *desc：从首选项获取相应 checkBox 的 bollean 值
     */
    private void setIsChecked(){
        isChecked = sharedPreferences.getBoolean(IS_CHECKED,true);
    }
    private void setIsCheckedBoot(){
        isCheckedBoot= sharedPreferences.getBoolean(IS_CHECKED_BOOT,true);
    }
    private void setCheckedHideIcon() {
        isCheckedHideIcon = sharedPreferences.getBoolean(IS_CHECKED_HIDE_ICON,false);
    }
    private void setCheckedHideNew() {
        isCheckedHideNew = sharedPreferences.getBoolean(IS_CHECKED_HIDE_NEW,false);
    }

    private void initChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("default",
                "VibrateChannel",
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(false);
        channel.setDescription("随手一记默认通知渠道");
        channel.setShowBadge(false);
        channel.setSound(null, null);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
