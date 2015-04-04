package com.phudx1.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.phudx1.common.CommonUtil;
import com.phudx1.data.dao.AdapterMediaPlay;
import com.phudx1.data.dao.AudioPlayParcelable;
import com.phudx1.data.dao.Holder;
import com.phudx1.event.ListViewClickListener;
import com.phudx1.mediaplayer.timeoff.TimeDialog;
import com.phudx1.service.ConvertService;

import java.util.ArrayList;

public class MediaActivity extends ActionBarActivity {

    private static boolean isBoundService;
    private static Messenger mMessenger;
    private ArrayList<AudioPlayParcelable> mArrayList;
    private AdapterMediaPlay mAdapterMediaPlay;
    private ListView mListView;
    private ListViewClickListener eventListView;
    private Button mButtonPlay;
    private Button mButtonPause;
    private Button mButtonSuf;
    private Button mButtonRepeat;
    private Button mButtonTime;
    private Button mNextPage;
    private Button mBackPage;
    private Button mNext;
    private Button mPrev;
    private TextView mText_page;
    private TimeDialog mTimeDialog;
    private Intent mIntent;
    private int mCurrentPage;
    private int mTotalPage;
    private static int mCurPlay;
    private boolean uSelectedList;
    private View mViewItem;

    // private float mStartX;
    // private float mEndX;
    // private float mStartY;
    // private float mEndY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MediaActivity", "--------onCreate---------");
        setContentView(R.layout.activity_media);
        mButtonPlay = (Button) findViewById(R.id.bt_play);
        mButtonPause = (Button) findViewById(R.id.bt_pause);
        mButtonSuf = (Button) findViewById(R.id.bt_suf);
        mButtonRepeat = (Button) findViewById(R.id.bt_repeatmost);
        mButtonTime = (Button) findViewById(R.id.bt_time);
        mNextPage = (Button) findViewById(R.id.next_page);
        mBackPage = (Button) findViewById(R.id.back_page);
        mNext = (Button) findViewById(R.id.bt_next);
        mPrev = (Button) findViewById(R.id.bt_prev);
        mText_page = (TextView) findViewById(R.id.text_page);
        mListView = (ListView) findViewById(R.id.listmediaplay);
        // mListView.setOnTouchListener(new View.OnTouchListener() {
        //
        // @Override
        // public boolean onTouch(View v, MotionEvent event) {
        // if (event.getAction() == MotionEvent.ACTION_DOWN) {
        // mStartY = event.getY();
        // mStartX = event.getX();
        // mEndX = mStartX;
        // mEndY = mStartY;
        // }
        // if (event.getAction() == MotionEvent.ACTION_UP) {
        // mEndX = event.getX();
        // mEndY = event.getY();
        // }
        // if (mEndX == mStartX && mEndY == mStartY) {
        // return false;
        // }
        // if (mEndY > mStartY + 80 || mEndY + 80 < mStartY) {
        // return false;
        // }
        // if (mEndX > mStartX + 80) {// Next Page
        // int mCurPage = mCurrentPage + 1;
        // if (mCurPage > mTotalPage) {
        // mCurPage = mTotalPage;
        // }
        // sendNextPage(mCurPage);
        // }
        // if (mEndX + 80 < mStartX) {// Back Page
        // int mCurPage1 = mCurrentPage - 1;
        // if (mCurPage1 < 1) {
        // mCurPage1 = 1;
        // }
        // sendBackPage(mCurPage1);
        // }
        // return false;
        // }
        // });
        eventListView = new ListViewClickListener(this);
        mListView.setOnItemClickListener(eventListView);
        mTimeDialog = new TimeDialog(this);
        MediaApplication application = (MediaApplication) getApplication();
        application.setMediaActivity(this);
    }

    private void setEnableComponent(boolean enabled) {
        mButtonPause.setEnabled(enabled);
        mButtonPlay.setEnabled(enabled);
        mButtonRepeat.setEnabled(enabled);
        mButtonSuf.setEnabled(enabled);
        mButtonTime.setEnabled(enabled);
        mBackPage.setEnabled(enabled);
        mNextPage.setEnabled(enabled);
        mNext.setEnabled(enabled);
        mPrev.setEnabled(enabled);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("MediaActivity", "--------onServiceDisconnected---------");
            mMessenger = null;
            isBoundService = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mMessenger = new Messenger(binder);

            // /**
            // * register callback
            // */
            // Message msg = Message.obtain();
            // msg.what = ConvertService.REQUEST_CLIENT_REGISTER;
            // msg.replyTo = mCallbackMsg;
            // try {
            // mMessenger.send(msg);
            // } catch (RemoteException e) {
            // e.printStackTrace();
            // }
            isBoundService = true;
            setListSong();
            checkIsSuf();
            checkLoopMediaPlayer();
            Log.i("MediaActivity", "--------onServiceConnected---------");
        }
    };

    /**
     * change background button from play to pause
     */
    public void changeBackGroundPlayOrPause() {
        mButtonPlay.setVisibility(View.VISIBLE);
        mButtonPause.setVisibility(View.GONE);
    }

    public void setProgressSeekbar(int mNumber) {
        if (mNumber > 0) {
            mButtonTime.setBackgroundResource(R.drawable.hourglass_);
            mTimeDialog.mCheckBox.setChecked(false);
            mTimeDialog.mTimeSeekbar.setEnabled(true);
        } else {
            mButtonTime.setBackgroundResource(R.drawable.time);
            mTimeDialog.mCheckBox.setChecked(true);
            mTimeDialog.mTimeSeekbar.setEnabled(false);
        }
        if (mTimeDialog.isShowing()) {
            mTimeDialog.setProgress(mNumber);
            String mTime = CommonUtil.getTimeFromSecon(mNumber);
            mTimeDialog.setTextTime(mTime);
        }
    }

    /**
     * check mediaplayer is loop or not loop
     */
    private void checkLoopMediaPlayer() {
        sendMessage(CommonUtil.CHECK_REPEAT);
    }

    /**
     * check MediaPlayer is shuffle or not shuffle
     */
    private void checkIsSuf() {
        sendMessage(CommonUtil.CHECK_SUF);
    }

    /**
     * event button
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_prev:
                mListView.requestFocusFromTouch();
                uSelectedList = true;
                mButtonPlay.setVisibility(View.GONE);
                mButtonPause.setVisibility(View.VISIBLE);
                setVisibleImageItem(mCurPlay, View.GONE);
                sendMessage(CommonUtil.PREV);
                break;
            case R.id.bt_suf:
                setSelectionView();
                sendMessage(CommonUtil.SUF);
                break;
            case R.id.bt_play:
                clickButtonPlay();
                setVisibleImageItem(mCurPlay, View.VISIBLE);
                break;
            case R.id.bt_pause:
                setVisibleImageItem(mCurPlay, View.GONE);
                clickButtonPause();
                break;
            case R.id.bt_repeatmost:
                setSelectionView();
                sendMessage(CommonUtil.REPEAT);
                break;
            case R.id.bt_next:
                mListView.requestFocusFromTouch();
                uSelectedList = true;
                mButtonPlay.setVisibility(View.GONE);
                mButtonPause.setVisibility(View.VISIBLE);
                setVisibleImageItem(mCurPlay, View.GONE);
                sendMessage(CommonUtil.NEXT);
                break;
            case R.id.bt_time:
                setSelectionView();
                mTimeDialog.show();
                break;
            case R.id.next_page:
                if (uSelectedList) {
                    mViewItem = mListView.getChildAt(mCurPlay
                            - mListView.getFirstVisiblePosition());
                }
                int mCurPage = mCurrentPage + 1;
                if (mCurPage > mTotalPage) {
                    mCurPage = mTotalPage;
                }
                sendNextPage(mCurPage);
                break;
            case R.id.back_page:
                int mCurPage1 = mCurrentPage - 1;
                if (mCurPage1 < 1) {
                    mCurPage1 = 1;
                }
                sendBackPage(mCurPage1);
                break;
            default:
                break;
        }
    }

    private void sendNextPage(int mCurPage) {
        Message mMessage = Message
                .obtain(null, CommonUtil.NEXT_PAGE);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean(CommonUtil.SELECTED_LIST, uSelectedList);
        mBundle.putInt(CommonUtil.SEND_CHANGE_PAGE, mCurPage);
        mBundle.putInt(CommonUtil.SEND_OLD_PAGE, mCurrentPage);
        mMessage.setData(mBundle);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendBackPage(int mCurPage) {
        Message mMessage = Message
                .obtain(null, CommonUtil.BACK_PAGE);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean(CommonUtil.SELECTED_LIST, uSelectedList);
        mBundle.putInt(CommonUtil.SEND_CHANGE_PAGE, mCurPage);
        mBundle.putInt(CommonUtil.SEND_OLD_PAGE, mCurrentPage);
        mMessage.setData(mBundle);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * send message to sevice with key special
     * @param mKeySend
     */
    private void sendMessage(int mKeySend) {
        Message mMessage = Message.obtain(null, mKeySend);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * event when click button pause, or click item ListView
     */
    private void clickButtonPause() {
        mButtonPlay.setVisibility(View.VISIBLE);
        mButtonPause.setVisibility(View.GONE);
        uSelectedList = true;
        setSelectionViewPlay();
        Message mMessage = Message.obtain(null, CommonUtil.PAUSE);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * event when click button play or click item ListView
     * @param mSelectListView
     */
    private void clickButtonPlay() {
        mButtonPlay.setVisibility(View.GONE);
        mButtonPause.setVisibility(View.VISIBLE);
        uSelectedList = true;
        setSelectionViewPlay();
        Message mMessage = Message.obtain(null, CommonUtil.PLAY);
        mMessage.replyTo = new Messenger(new ResponHandel());
        Bundle mBundle = new Bundle();
        mCurPlay = mListView.getSelectedItemPosition();
        mBundle.putInt(CommonUtil.CUR_PLAY, mCurPlay);
        mMessage.setData(mBundle);
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * start seekbar time
     * @param mNumberTime
     */
    public void startSeekbar(int mNumberTime) {
        Message mMessage = Message.obtain(null, CommonUtil.START_SEEKBAR);
        mMessage.replyTo = new Messenger(new ResponHandel());
        Bundle mBundle = new Bundle();
        mBundle.putInt(CommonUtil.NUMBER_TIME_KEY, mNumberTime);
        mMessage.setData(mBundle);
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * set list song from client to sevice
     */
    private void setListSong() {
        Message mMessage = Message.obtain(null, CommonUtil.SET_LIST_AUDIO);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * event ListView list song
     * @param postsision
     */
    public void onClickListView(int postsision) {
        Log.i("MediaActivity", "--------onClickListView---------" +
                postsision);
        // long mTime = SystemClock.uptimeMillis();
        // do {
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // } while (SystemClock.uptimeMillis() < mTime + 1000);
        if (mCurPlay == postsision) {
            Log.i("MediaActivity", "--------mCurPlay == postsision---------");
            switch (mButtonPlay.getVisibility()) {
                case View.GONE:
                    setVisibleImageItem(mCurPlay, View.GONE);
                    clickButtonPause();
                    break;
                case View.VISIBLE:
                    setVisibleImageItem(mCurPlay, View.VISIBLE);
                    clickButtonPlay();
                    break;
                default:
                    break;
            }
        } else {
            setVisibleImageItem(mCurPlay, View.GONE);
            mCurPlay = postsision;
            Log.i("MediaActivity", "--------mCurPlay != postsision---------:"
                    + mCurPlay);
            setVisibleImageItem(mCurPlay, View.VISIBLE);
            clickButtonPlay();
        }
    }

    private void setVisibleImageItem(int mCurPlays, int mVisble) {
        View mViewItem = mListView.getChildAt(mCurPlays
                - mListView.getFirstVisiblePosition());
        Holder mHol = (Holder) mViewItem.getTag();
        mHol.imageViewSong.setVisibility(mVisble);
    }

    // private Messenger mCallbackMsg = new Messenger(new ResponHandel());
    public void setSelectionView() {
        mListView.requestFocusFromTouch();
        Log.i("MediaActivity", "--------setSelectionView---------"
                + uSelectedList);
        if (uSelectedList) {
            mListView.setSelection(mCurPlay);
        }
    }

    private void setSelectionViewPlay() {
        mListView.requestFocusFromTouch();
        mListView.setSelection(mCurPlay);
        uSelectedList = true;
    }

    /**
     * handler get message from service to client
     * @author PhucDX1
     */
    private class ResponHandel extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonUtil.NEXT:
                    mCurPlay = msg.getData().getInt(CommonUtil.NUMBER_PLAY);
                    setVisibleImageItem(mCurPlay, View.VISIBLE);
                    mListView.setSelection(mCurPlay);
                    break;
                case CommonUtil.PREV:
                    mCurPlay = msg.getData().getInt(CommonUtil.NUMBER_PLAY);
                    setVisibleImageItem(mCurPlay, View.VISIBLE);
                    mListView.setSelection(mCurPlay);
                    break;
                case CommonUtil.CLICK_SUF:
                    boolean mSuf = msg.getData().getBoolean(
                            CommonUtil.SUF_RECIVER_KEY);
                    if (mSuf) {
                        mButtonSuf
                                .setBackgroundResource(R.drawable.suff);
                    } else {
                        mButtonSuf
                                .setBackgroundResource(R.drawable.dis_suff);
                    }
                    break;
                case CommonUtil.REPEAT_RECIVE:
                    boolean mRepeat = msg.getData().getBoolean(
                            CommonUtil.REPEAT_RECIVER_KEY);
                    if (mRepeat) {
                        mButtonRepeat.setBackgroundResource(R.drawable.loop);
                    } else {
                        mButtonRepeat
                                .setBackgroundResource(R.drawable.dis_loop);
                    }
                    break;
                case CommonUtil.CHECK_SUF:
                    boolean mSufs = msg.getData().getBoolean(
                            CommonUtil.CHECK_SUF_KEY);
                    if (mSufs) {
                        mButtonSuf
                                .setBackgroundResource(R.drawable.suff);
                    } else {
                        mButtonSuf
                                .setBackgroundResource(R.drawable.dis_suff);
                    }
                    break;
                case CommonUtil.CHECK_REPEAT:
                    boolean mRepeats = msg.getData().getBoolean(
                            CommonUtil.CHECK_REPEAT_KEY);
                    if (mRepeats) {
                        mButtonRepeat.setBackgroundResource(R.drawable.loop);
                    } else {
                        mButtonRepeat
                                .setBackgroundResource(R.drawable.dis_loop);
                    }
                    break;
                case CommonUtil.SET_LIST_AUDIO_RECIVER:
                    boolean isPlay = msg.getData().getBoolean(
                            CommonUtil.IS_PLAY_KEY);
                    if (isPlay) {
                        mButtonPlay.setVisibility(View.GONE);
                        mButtonPause.setVisibility(View.VISIBLE);
                    }
                    mArrayList = msg.getData().getParcelableArrayList(
                            CommonUtil.SET_LIST_KEY);
                    uSelectedList = msg.getData().getBoolean(
                            CommonUtil.SELECTED_LIST);
                    mCurrentPage = msg.getData().getInt(
                            CommonUtil.SEND_CHANGE_PAGE);
                    mTotalPage = msg.getData().getInt(
                            CommonUtil.SEND_TOTAL_PAGE);
                    if (mTotalPage != 0) {
                        mText_page.setText(mCurrentPage + "/" + mTotalPage);
                        setEnableComponent(true);
                        mAdapterMediaPlay = new AdapterMediaPlay(
                                getApplicationContext(), mArrayList);
                        mListView.setAdapter(mAdapterMediaPlay);
                        Log.i("MediaActivity.ResponHandel",
                                "--------uSelectedList---------"
                                        + uSelectedList);
                        if (uSelectedList) {
                            mListView.requestFocusFromTouch();
                            mListView.setSelection(mCurPlay);
                        }
                        mAdapterMediaPlay.notifyDataSetChanged();
                    } else {
                        mText_page.setText("--/--");
                        setEnableComponent(false);
                    }
                    break;
                case CommonUtil.CHANGE_PAGE:
                    mArrayList = msg.getData().getParcelableArrayList(
                            CommonUtil.SET_LIST_KEY);
                    uSelectedList = msg.getData().getBoolean(
                            CommonUtil.SELECTED_LIST);
                    mCurrentPage = msg.getData().getInt(
                            CommonUtil.SEND_CHANGE_PAGE);
                    mTotalPage = msg.getData().getInt(
                            CommonUtil.SEND_TOTAL_PAGE);
                    if (mTotalPage != 0) {
                        mText_page.setText(mCurrentPage + "/" + mTotalPage);
                        setEnableComponent(true);
                        mAdapterMediaPlay = new AdapterMediaPlay(
                                getApplicationContext(), mArrayList);
                        mListView.setAdapter(mAdapterMediaPlay);
                        if (uSelectedList) {
                            mListView.requestFocusFromTouch();
                            mListView.setSelection(mCurPlay);
                        }
                        mAdapterMediaPlay.notifyDataSetChanged();
                    } else {
                        mText_page.setText("--/--");
                        setEnableComponent(false);
                    }
                    break;
                case CommonUtil.GET_NUMBER_PLAY:
                    mCurPlay = msg.getData().getInt(
                            CommonUtil.NUMBER_PLAY);
                    uSelectedList = msg.getData().getBoolean(
                            CommonUtil.SELECTED_LIST);
                    setSelectionView();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MediaActivity", "--------onStart---------");
        if (!isBoundService) {
            mIntent = new Intent(this, ConvertService.class);
            bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MediaActivity", "--------onStop---------");
    }

    protected void onRestart() {
        super.onRestart();
        Log.i("MediaActivity", "--------onRestart---------");
        Message mMessage = Message.obtain(null, CommonUtil.GET_NUMBER_PLAY);
        mMessage.replyTo = new Messenger(new ResponHandel());
        try {
            mMessenger.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MediaActivity", "--------onDestroy---------");
        if (isBoundService) {
            unbindService(mServiceConnection);
            isBoundService = false;
        }
    }

    /**
     * stop seek bar time
     */
    public void stopSeekbar() {
        sendMessage(CommonUtil.STOP_SEEKBAR);
    }

    public void setBackGroundTime() {
        mButtonTime.setBackgroundResource(R.drawable.time_machine);
    }
}
