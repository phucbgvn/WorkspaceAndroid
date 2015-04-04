package com.phudx1.event;

import android.view.View;

import com.phudx1.mediaplayer.timeoff.TimeDialog;

public class EventCheckBox implements View.OnClickListener {
	private TimeDialog mTimeDialog;

	public EventCheckBox(TimeDialog mTimeDialogs) {
		mTimeDialog = mTimeDialogs;
	}

	@Override
	public void onClick(View v) {
		mTimeDialog.onClickCheckBox();
	}

}
