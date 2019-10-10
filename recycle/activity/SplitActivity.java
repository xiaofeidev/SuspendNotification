package com.github.xiaofei_dev.suspensionnotification.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.GridLayoutManager;
import androidx.appcompat.widget.LinearLayoutManager;
import androidx.appcompat.widget.RecyclerView;
import androidx.appcompat.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.github.xiaofei_dev.suspensionnotification.R;
import com.github.xiaofei_dev.suspensionnotification.ui.adapter.CharacterAdapter;
import com.github.xiaofei_dev.suspensionnotification.util.RegexUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author：xiaofei_dev
 * time：2017/5/11:15:02
 * e-mail：xiaofei.dev@gmail.com
 * desc：coding
 */
public class SplitActivity extends AppCompatActivity {


    private List<String> mCharactersList;
    private RecyclerView mSplitListView;
    private CharacterAdapter mCharacterAdapter;
    public static final String TAG = "SplitActivity";
    private static final String DEVIDER="__DEVIDER___DEVIDER__";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        String str = getIntent().getStringExtra(TAG);
//        mCharactersList = getCharactersList(str);
        mCharactersList = Arrays.asList(str.split(""));

        mSplitListView = (RecyclerView) findViewById(R.id.split_list);
//        int wide = mCharactersList.size()/8;
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
//                (14,StaggeredGridLayoutManager.VERTICAL);
//        Log.d(TAG, "onCreate: " + wide);
        mSplitListView.setLayoutManager(new GridLayoutManager(this,10));
        mCharacterAdapter = new CharacterAdapter(mCharactersList);
        mSplitListView.setAdapter(mCharacterAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Intent newIntent(Context context, String text) {
        Intent i = new Intent(context,SplitActivity.class);
        i.putExtra(TAG,text);
        return i;
    }

//    private List<String> getCharactersList(String str) {
//        List<String> txts = new ArrayList<String>();
//        String s = "";
//        for (int i = 0; i < str.length(); i++) {
//            char first = str.charAt(i);
//            //当到达末尾的时候
//            if (i + 1 >= str.length()) {
//                s = s + first;
//                break;
//            }
//            char next = str.charAt(i + 1);
//            if ((RegexUtil.isChinese(first) && !RegexUtil.isChinese(next))
//                    || (!RegexUtil.isChinese(first) && RegexUtil.isChinese(next))
//                    || (Character.isLetter(first) && !Character.isLetter(next))
//                    || (Character.isDigit(first) && !Character.isDigit(next))) {
//                s = s + first + DEVIDER;
//            } else if (RegexUtil.isSymbol(first)) {
//                s = s /*+ DEVIDER */+ first + DEVIDER;
//            } else {
////                s = s + first;
//                s = s + first + DEVIDER;
//            }
//        }
//        str = s;
//        str.replace("\n", DEVIDER+"\n"+DEVIDER);
//        String[] texts = str.split(DEVIDER);
//        for (String text : texts) {
//            if (text.equals(DEVIDER))
//                continue;
//            //当首字母是英文字母时，默认该字符为英文
//            if (RegexUtil.isEnglish(text)) {
//                txts.add(text);
//                continue;
//            }
//            if (RegexUtil.isNumber(text)) {
//                txts.add(text);
//                continue;
//            }
//            for (int i = 0; i < text.length(); i++) {
//                txts.add(text.charAt(i) + "");
//            }
//        }
//        return txts;
//    }
}
