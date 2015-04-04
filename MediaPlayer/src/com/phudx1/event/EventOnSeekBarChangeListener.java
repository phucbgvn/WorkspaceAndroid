package com.phudx1.event;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.phudx1.mediaplayer.timeoff.TimeDialog;

public class EventOnSeekBarChangeListener implements OnSeekBarChangeListener {
	private TimeDialog mTimeDialog;
	private int mProgress;

	public EventOnSeekBarChangeListener(TimeDialog mTimeDialogs) {
		mTimeDialog = mTimeDialogs;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		mProgress = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mTimeDialog.startSeekbar(mProgress);
	}
}
