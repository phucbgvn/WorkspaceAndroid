package com.phudx1.data.dao;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phudx1.mediaplayer.R;

public class Holder {
    public ImageView imageViewSong;
    public TextView textTitle;
    public TextView textArtist;
    public TextView textId;
    public TextView textPath;
    public TextView textDuration;

    public Holder(View view) {
        imageViewSong = (ImageView) view.findViewById(R.id.imageviewSong);
        textArtist = (TextView) view.findViewById(R.id.txtArtist);
        textTitle = (TextView) view.findViewById(R.id.txtTitle);
        textId = (TextView) view.findViewById(R.id.txt_id);
        textPath = (TextView) view.findViewById(R.id.txtfullpath);
        textDuration = (TextView) view.findViewById(R.id.txtduration);
    }
}
