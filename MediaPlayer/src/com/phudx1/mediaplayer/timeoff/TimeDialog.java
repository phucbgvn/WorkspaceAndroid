package com.phudx1.mediaplayer.timeoff;

import android.app.Dialog;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.phudx1.common.CommonUtil;
import com.phudx1.event.EventCheckBox;
import com.phudx1.event.EventOnSeekBarChangeListener;
import com.phudx1.mediaplayer.MediaActivity;
import com.phudx1.mediaplayer.R;

public class TimeDialog extends Dialog {
    private static final String TITEL_DIALOG = "Time Dialog";
    private MediaActivity mMediaActivity;
    public CheckBox mCheckBox;
    public SeekBar mTimeSeekbar;
    private TextView mTextTime;
    private EventCheckBox mEventCheckBox;
    private EventOnSeekBarChangeListener mEventSeekBar;
    private static final int START_TIME = 0;

    public TimeDialog(Context context) {
        super(context, android.R.style.Theme_DeviceDefault_Dialog);
        setContentView(R.layout.dialog_time);
        getWindow().getAttributes().dimAmount = 0;
        setTitle(TITEL_DIALOG);
        if (context instanceof MediaActivity) {
            mMediaActivity = (MediaActivity) context;
        }
        mEventCheckBox = new EventCheckBox(this);
        mEventSeekBar = new EventOnSeekBarChangeListener(this);
        mTimeSeekbar = (SeekBar) findViewById(R.id.seek_time);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox_time);
        mTextTime = (TextView) findViewById(R.id.text_time);
        mTimeSeekbar.setOnSeekBarChangeListener(mEventSeekBar);
        mCheckBox.setOnClickListener(mEventCheckBox);
        mCheckBox.setChecked(true);
        mTimeSeekbar.setEnabled(false);
    }

    /**
     * event touch listener on seekbar
     */
    public void startSeekbar(int mProgress) {
        mMediaActivity.stopSeekbar();
        mTextTime.setText(CommonUtil.getTimeFromSecon(mProgress));
        mMediaActivity.startSeekbar(mProgress);
    }

    /**
     * event checkbox
     */
    public void onClickCheckBox() {
        boolean mChek = mCheckBox.isChecked();
        if (mChek) {
            mCheckBox.setText(R.string.txt_check_enable);
            mTimeSeekbar.setProgress(START_TIME);
            mTextTime.setText("");
            mMediaActivity.stopSeekbar();
        } else {
            mCheckBox.setText(R.string.txt_check_disable);
        }
        mTimeSeekbar.setEnabled(!mChek);
        if (!mTimeSeekbar.isEnabled()) {
            mMediaActivity.setBackGroundTime();
        }
    }

    /**
     * set progress of seekbar
     * @param mNumber
     */
    public void setProgress(int mNumber) {
        mTimeSeekbar.setProgress(mNumber);
    }

    /**
     * display value of seekbar
     * @param mTime
     */
    public void setTextTime(String mTime) {
        mTextTime.setText(mTime);
    }

    @Override
    public void dismiss() {
        mMediaActivity.setSelectionView();
        super.dismiss();
    }
}
