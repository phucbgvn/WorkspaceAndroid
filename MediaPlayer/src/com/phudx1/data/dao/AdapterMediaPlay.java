package com.phudx1.data.dao;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.phudx1.common.CommonUtil;
import com.phudx1.mediaplayer.R;

import java.util.ArrayList;

public class AdapterMediaPlay extends ArrayAdapter<AudioPlayParcelable> {

    public AdapterMediaPlay(Context context,
            ArrayList<AudioPlayParcelable> resource) {
        super(context, R.layout.item_view_playlist, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(),
                    R.layout.item_view_playlist, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        try {
            AudioPlayParcelable mAudioPlayParcelable = getItem(position);
            AudioPlay mAudioPlay = mAudioPlayParcelable.getmAudioPlay();
            holder.textTitle.setText(mAudioPlay.title);
            holder.textArtist.setText(mAudioPlay.artist);
            if (mAudioPlay.imageAlbum != null) {
                holder.imageViewSong.setImageBitmap(BitmapFactory
                        .decodeFile(mAudioPlay.imageAlbum));
            }
            holder.textPath.setText(mAudioPlay.mPath);
            holder.textDuration.setText(CommonUtil
                    .getTimeFromMinisecon(mAudioPlay.duration));
        } catch (IndexOutOfBoundsException ex) {
            Log.i("AdapterMediaPlay",
                    "--------IndexOutOfBoundsException---------");
        }
        return convertView;
    }
}
