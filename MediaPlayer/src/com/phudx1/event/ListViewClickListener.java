package com.phudx1.event;

import android.view.View;
import android.widget.AdapterView;

import com.phudx1.mediaplayer.MediaActivity;

public class ListViewClickListener implements AdapterView.OnItemClickListener {
	private MediaActivity mMediaActivity;

	public ListViewClickListener(MediaActivity mMediaActivitys) {
		mMediaActivity = mMediaActivitys;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mMediaActivity.onClickListView(arg2);
	}

}
