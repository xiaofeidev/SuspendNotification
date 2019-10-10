package com.github.xiaofei_dev.suspensionnotification.ui.adapter;


import android.content.Context;
import androidx.appcompat.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.xiaofei_dev.suspensionnotification.R;

import java.util.List;

/**
 * author：xiaofei_dev
 * time：2017/5/11:16:40
 * e-mail：xiaofei.dev@gmail.com
 * desc：coding
 */
public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

    private List<String> mCharacterList;

    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView characterItem;

        public ViewHolder(View view) {
            super(view);
            characterItem = (TextView) view.findViewById(R.id.character_item);
        }
    }

    public CharacterAdapter(List<String> characterList) {
        mCharacterList = characterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mContext == null) {mContext = parent.getContext();}
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.character_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.characterItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.characterItem.setBackgroundResource(R.drawable.button_pressed);
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String character = mCharacterList.get(position);
        holder.characterItem.setText(character);
    }

    @Override
    public int getItemCount() {
        return mCharacterList.size();
    }
}
