package com.github.xiaofei_dev.suspensionnotification.ui.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.xiaofei_dev.suspensionnotification.R;
import com.github.xiaofei_dev.suspensionnotification.util.OpenUtil;

public final class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView)findViewById(R.id.textView2)).setText(getString(R.string.app_version, getPackageVersion(this)));
    }

    public void onAbout(View view) {
        switch (view.getId()) {
            case R.id.itemOpenSource:
                String url = getString(R.string.openSourceLink);
                OpenUtil.openLink(view.getContext(), null, url, false);
                finish();
                break;
            case R.id.itemScoreAndFeedback:
                OpenUtil.openApplicationMarket(getPackageName(), "com.coolapk.market",
                        view.getContext());
                finish();
                break;
            case R.id.itemDonate:
                OpenUtil.alipayDonate(this);
                finish();
                break;
            case R.id.close:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前App的版本
     * @param context
     * @return
     */
    private String getPackageVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
}
