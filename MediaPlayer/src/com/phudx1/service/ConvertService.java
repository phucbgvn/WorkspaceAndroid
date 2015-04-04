package com.phudx1.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.phudx1.common.CommonUtil;
import com.phudx1.data.dao.AudioPlay;
import com.phudx1.data.dao.AudioPlayParcelable;
import com.phudx1.mediaplayer.MediaActivity;
import com.phudx1.mediaplayer.MediaApplication;

import java.util.ArrayList;
import java.util.Random;

public class ConvertService extends Service {
    private Handler mHandler = new Handler();
    private ArrayList<AudioPlayParcelable> mArrayList;
    private MediaPlayer mMediaPlayer;
    private Handler mHandlerLoopSong = new Handler();
    private static boolean isStartService;
    private static int mCurPlay;
    private boolean mSuf;
    private boolean mRepeat;
    private int mNumberTime;
    private static int mTotalPage;
    private static int mCurrentPage = 1;
    private static final int ROWPERPAGE = 10;
    private static boolean uInit;
    private static int mCurTimePlay;
    // private Messenger mClient;
    // private static final Uri MUSIC_URI_EXTERNAL_SONG =
    // MediaStore.Files.getContentUri("external");
    private static final Uri MUSIC_URI_EXTERNAL_SONG =
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final Uri MUSIC_URI_EXTENAL_ALBUM = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    // private static final Uri MUSIC_URI_INTERNAL_SONG =
    // MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
    // private static final Uri MUSIC_URI_INTERNAL_ALBUM =
    // MediaStore.Audio.Albums.INTERNAL_CONTENT_URI;
    private static boolean uSelectedList;
    private static int mPageHasSelectedList = 1;
    private String mFillterMimeType = MediaStore.Files.FileColumns.MIME_TYPE
            + "=?";
    private String mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(
                    "mp3");
    private String[] mFillterArgsMp3 = new String[] {
            mimeType
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ConvertService", "--------onCreate---------");
        mMediaPlayer = new MediaPlayer();
        mArrayList = new ArrayList<AudioPlayParcelable>();
        getSongList();
        // getSongList(MUSIC_URI_INTERNAL_SONG, MUSIC_URI_INTERNAL_ALBUM);
    }

    private Messenger mMessenger = new Messenger(new ConvertHandler());

    class ConvertHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            // switch (msg.what) {
            // case REQUEST_CLIENT_REGISTER:
            // mClient = msg.replyTo;
            // break;
            // default:
            // break;
            // }
            responseData(msg);
            receverData(msg);
        }

    }

    /**
     * response data from client
     * @param msg
     */
    private void responseData(Message msg) {
        switch (msg.what) {
            case CommonUtil.PLAY:
                mCurPlay = msg.getData().getInt(CommonUtil.CUR_PLAY);
                if (mCurPlay < 0) {
                    mCurPlay = CommonUtil.START_AUDIO;
                }
                setDataSource(getCurFullPath());
                playAudio();
                mPageHasSelectedList = mCurrentPage;
                mHandlerLoopSong.removeCallbacks(mRunnableLoop);
                mHandlerLoopSong.postDelayed(mRunnableLoop, 1000);
                break;
            case CommonUtil.PAUSE:
                pauseAudio();
                mHandlerLoopSong.removeCallbacks(mRunnableLoop);
                break;
            case CommonUtil.SET_LIST_AUDIO:
                sendList(msg);
                break;
            case CommonUtil.SET_DATA_SOURCE:
                String mFullPath = msg.getData().getString(
                        CommonUtil.SET_DATA_SOURCE_KEY);
                setDataSource(mFullPath);
                break;
            case CommonUtil.NEXT:
                mPageHasSelectedList = mCurrentPage;
                next();
                sendNextPrew(CommonUtil.NEXT, CommonUtil.NUMBER_PLAY, mCurPlay,
                        msg);
                mHandlerLoopSong.removeCallbacks(mRunnableLoop);
                mHandlerLoopSong.postDelayed(mRunnableLoop, 1000);
                break;
            case CommonUtil.PREV:
                mPageHasSelectedList = mCurrentPage;
                prew();
                sendNextPrew(CommonUtil.PREV, CommonUtil.NUMBER_PLAY, mCurPlay,
                        msg);
                mHandlerLoopSong.removeCallbacks(mRunnableLoop);
                mHandlerLoopSong.postDelayed(mRunnableLoop, 1000);
                break;
            case CommonUtil.START_SEEKBAR:
                mNumberTime = msg.getData().getInt(CommonUtil.NUMBER_TIME_KEY);
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 1000);
                break;
            case CommonUtil.STOP_SEEKBAR:
                mHandler.removeCallbacks(mRunnable);
                break;
            case CommonUtil.GET_NUMBER_PLAY:
                try {
                    Message mMessage = Message.obtain(null,
                            CommonUtil.GET_NUMBER_PLAY);
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(CommonUtil.SELECTED_LIST, uSelectedList);
                    mBundle.putInt(CommonUtil.NUMBER_PLAY, mCurPlay);
                    mMessage.setData(mBundle);
                    msg.replyTo.send(mMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case CommonUtil.NEXT_PAGE:
                mCurrentPage = msg.getData()
                        .getInt(CommonUtil.SEND_CHANGE_PAGE);
                uSelectedList = msg.getData()
                        .getBoolean(CommonUtil.SELECTED_LIST);
                if (uSelectedList) {
                    mPageHasSelectedList = mCurrentPage - 1;
                }
                getSongList();
                sendListChangePage(msg);
                break;
            case CommonUtil.BACK_PAGE:
                mCurrentPage = msg.getData()
                        .getInt(CommonUtil.SEND_CHANGE_PAGE);
                uSelectedList = msg.getData()
                        .getBoolean(CommonUtil.SELECTED_LIST);
                if (uSelectedList) {
                    mPageHasSelectedList = msg.getData()
                            .getInt(CommonUtil.SEND_OLD_PAGE);
                }
                getSongList();
                sendListChangePage(msg);
                break;
            case CommonUtil.CHANGE_SELECTED_ITEM:
                uSelectedList = msg.getData()
                        .getBoolean(CommonUtil.SELECTED_LIST);
                if (uSelectedList) {
                    mPageHasSelectedList = msg.getData()
                            .getInt(CommonUtil.SEND_OLD_PAGE);
                }
                break;
            default:
                break;
        }
    }

    private void sendListChangePage(Message msg) {
        try {
            Message mMessage = Message.obtain(null,
                    CommonUtil.CHANGE_PAGE);
            Bundle mBundle = new Bundle();
            if (mPageHasSelectedList == mCurrentPage) {
                uSelectedList = true;
                mBundle.putBoolean(CommonUtil.SELECTED_LIST, true);
            } else {
                uSelectedList = false;
                mBundle.putBoolean(CommonUtil.SELECTED_LIST, false);
            }
            mBundle.putInt(CommonUtil.SEND_TOTAL_PAGE, mTotalPage);
            mBundle.putInt(CommonUtil.SEND_CHANGE_PAGE, mCurrentPage);
            mBundle.putParcelableArrayList(CommonUtil.SET_LIST_KEY,
                    mArrayList);
            mMessage.setData(mBundle);
            msg.replyTo.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendList(Message msg) {
        try {
            Message mMessage = Message.obtain(null,
                    CommonUtil.SET_LIST_AUDIO_RECIVER);
            Bundle mBundle = new Bundle();
            mBundle.putInt(CommonUtil.NUMBER_PLAY, mCurPlay);
            mBundle.putInt(CommonUtil.SEND_CHANGE_PAGE, mCurrentPage);
            mBundle.putInt(CommonUtil.SEND_TOTAL_PAGE, mTotalPage);
            mBundle.putBoolean(CommonUtil.IS_PLAY_KEY, mMediaPlayer.isPlaying());
            if (mPageHasSelectedList == mCurrentPage) {
                uSelectedList = true;
                mBundle.putBoolean(CommonUtil.SELECTED_LIST, true);
            } else {
                uSelectedList = false;
                mBundle.putBoolean(CommonUtil.SELECTED_LIST, false);
            }
            mBundle.putParcelableArrayList(CommonUtil.SET_LIST_KEY,
                    mArrayList);
            mMessage.setData(mBundle);
            msg.replyTo.send(mMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendNextPrew(int mKeySend, String mKeyMessage,
            int mCurPlay, Message msg) {
        try {
            Message resp = Message.obtain(null, mKeySend);
            Bundle bResp = new Bundle();
            bResp.putInt(mKeyMessage, mCurPlay);
            resp.setData(bResp);
            msg.replyTo.send(resp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage1(int mKeySend, String mKeyMessage,
            boolean mSend, Message msg) {
        try {
            Message resp = Message.obtain(null, mKeySend);
            Bundle bResp = new Bundle();
            bResp.putBoolean(mKeyMessage, mSend);
            resp.setData(bResp);
            msg.replyTo.send(resp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * recever data for client
     * @param msg
     */
    private void receverData(Message msg) {
        switch (msg.what) {
            case CommonUtil.SUF:
                mSuf = !mSuf;
                sendMessage1(CommonUtil.CLICK_SUF,
                        CommonUtil.SUF_RECIVER_KEY, mSuf, msg);
                break;
            case CommonUtil.REPEAT:
                mRepeat = !mRepeat;
                sendMessage1(CommonUtil.REPEAT_RECIVE,
                        CommonUtil.REPEAT_RECIVER_KEY, mRepeat, msg);
                break;
            case CommonUtil.CHECK_SUF:
                sendMessage(CommonUtil.CHECK_SUF, CommonUtil.CHECK_SUF_KEY,
                        msg,
                        mSuf);
                break;
            case CommonUtil.CHECK_REPEAT:
                sendMessage(CommonUtil.CHECK_REPEAT,
                        CommonUtil.CHECK_REPEAT_KEY,
                        msg, mRepeat);
                break;
            default:
                break;
        }
    }

    /**
     * send message to client with key special
     * @param mKeySend
     * @param mKeyMessage
     * @param msg
     * @param mSend
     */
    private void sendMessage(int mKeySend, String mKeyMessage, Message msg,
            boolean mSend) {
        try {
            Message resp = Message.obtain(null, mKeySend);
            Bundle bResp = new Bundle();
            bResp.putInt(CommonUtil.NUMBER_PLAY, mCurPlay);
            bResp.putBoolean(mKeyMessage, mSend);
            resp.setData(bResp);
            msg.replyTo.send(resp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get number random song
     * @return
     */
    private int getRandomSong() {
        Random mRandom = new Random();
        int mNumberSong = 0;
        if (mArrayList.size() > 1) {
            mNumberSong = mRandom.nextInt(mArrayList.size() - 1);
        }
        return mNumberSong;
    }

    /**
     * get fullpatch of song
     * @return
     */
    private String getCurFullPath() {
        AudioPlayParcelable mAudioPlayParcelable = mArrayList.get(mCurPlay);
        AudioPlay mAudioPlay = mAudioPlayParcelable.getmAudioPlay();
        return mAudioPlay.fullPath;
    }

    /**
     * play song
     */
    private void playAudio() {
        if (mCurTimePlay > 0) {
            Log.i("ConvertService", mCurrentPage +
                    "--------playAudio---------" + mPageHasSelectedList);
            mMediaPlayer.seekTo(mCurTimePlay);
            mCurTimePlay = 0;
        }
        mMediaPlayer.start();
        if (!isStartService) {
            startService(mIntent);
        }
    }

    @Override
    public ComponentName startService(Intent service) {
        isStartService = true;
        Log.i("ConvertService", "--------startService---------");
        return super.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        isStartService = false;
        Log.i("ConvertService", "--------stopService---------");
        return super.stopService(name);
    }

    /**
     * pause song
     */
    private void pauseAudio() {
        mMediaPlayer.pause();
        if (mPageHasSelectedList == mCurrentPage) {
            mCurTimePlay = mMediaPlayer.getCurrentPosition();
        } else {
            mCurTimePlay = 0;
            mPageHasSelectedList = mCurrentPage;
        }
        // stopService(mIntent);
    }

    /**
     * play list forward
     */
    private void next() {
        if (mSuf) {
            mCurPlay = getRandomSong();
        } else {
            mCurPlay++;
            if (mCurPlay > (mArrayList.size() - 1)) {
                if(mCurrentPage < mTotalPage){
                    mCurrentPage++;
                } else {
                    mCurrentPage = CommonUtil.START_PAGE;
                }
                mCurPlay = CommonUtil.START_AUDIO;
            }
        }
        setDataSource(getCurFullPath());
        playAudio();
    }

    /**
     * play list backward
     */
    private void prew() {
        if (mSuf) {
            mCurPlay = getRandomSong();
        } else {
            mCurPlay--;
            if (mCurPlay < 0) {
                mCurPlay = mArrayList.size() - 1;
            }
        }
        setDataSource(getCurFullPath());
        playAudio();
    }

    /**
     * set path into MediaPlayer
     * @param mPath
     */
    private void setDataSource(String mPath) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent mIntent;

    @Override
    public IBinder onBind(Intent intent) {
        mIntent = intent;
        return mMessenger.getBinder();
    }

    /**
     * process play next or stop Mediaplayer (loop or not loop)
     */
    private Runnable mRunnableLoop = new Runnable() {

        @Override
        public void run() {
            mHandlerLoopSong.postDelayed(this, 1000);
            if (!mMediaPlayer.isPlaying() && mRepeat) {
                next();
            }
            if (!mMediaPlayer.isPlaying() && !mRepeat) {
                MediaApplication application = (MediaApplication) getApplication();
                MediaActivity mediaActivity = application.getMediaActivity();
                mediaActivity.changeBackGroundPlayOrPause();
                mHandlerLoopSong.removeCallbacks(mRunnableLoop);
            }
        }
    };

    // public void onRebind(Intent intent) {
    // Log.i("ConvertService", "--------onRebind---------");
    // };
    //
    // @Override
    // public boolean onUnbind(Intent intent) {
    // Log.i("ConvertService", "--------onUnbind---------");
    // return super.onUnbind(intent);
    // }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("ConvertService", "--------onDestroy---------");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mHandlerLoopSong.removeCallbacks(mRunnableLoop);
        mHandler.removeCallbacks(mRunnable);
        isStartService = false;
    }

    /**
     * process progress seekbar
     */
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            MediaApplication application = (MediaApplication) getApplication();
            MediaActivity mediaActivity = application.getMediaActivity();
            if (mNumberTime >= 1) {
                mNumberTime--;
                if (mediaActivity != null) {
                    mediaActivity.setProgressSeekbar(mNumberTime);
                }

            } else {
                if (mediaActivity != null) {
                    Log.i("ConvertService",
                            "--------mediaActivity.finish()---------");
                    mediaActivity.finish();
                }
                stopService(mIntent);
            }
        }
    };

    private void addItemList(Cursor mCursor) {
        if (mCursor != null) {
            mArrayList.clear();
            String mTitle;
            String mArtist;
            long mId;
            String mImageAlbum;
            AudioPlay mAudioPlay;
            AudioPlayParcelable mAudioPlayParcelable;
            String mFullPath;
            int mDuration;
            ArrayList<AudioPlayParcelable> mArrayAudio = new ArrayList<AudioPlayParcelable>();
            String[] mPath;
            while (mCursor.moveToNext()) {
                mId = mCursor.getLong(mCursor
                        .getColumnIndex(MediaStore.Audio.Media._ID));
                mTitle = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE));
                mArtist = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                mImageAlbum = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID));
                mFullPath = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));
                mPath = mFullPath.split(CommonUtil.REGEX);
                mDuration = mCursor.getInt(mCursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION));
                mAudioPlay = new AudioPlay(mId, mTitle, mArtist, mImageAlbum,
                        mFullPath, mDuration, mPath[mPath.length - 1]);
                mAudioPlayParcelable = new AudioPlayParcelable(mAudioPlay);
                mArrayAudio.add(mAudioPlayParcelable);
            }
            for (int i = 0; i < mArrayAudio.size(); i++) {
                AudioPlayParcelable mAudioPlayParcelables = mArrayAudio.get(i);
                AudioPlay mAudioPlays = mAudioPlayParcelables.getmAudioPlay();
                mAudioPlays.imageAlbum = getPathImageAlbum(
                        mAudioPlays.imageAlbum);
            }
            mArrayList.addAll(mArrayAudio);
        }
    }

    private void getPerPage(int start) {
        Uri mUriSong1 = MUSIC_URI_EXTERNAL_SONG.buildUpon()
                .encodedQuery("limit=" + start + "," + ROWPERPAGE).build();
        Cursor mCursor = getContentResolver().query(mUriSong1, null,
                mFillterMimeType,
                mFillterArgsMp3, null);
        addItemList(mCursor);
    }

    /**
     * get song add to ArrayList<AudioPlay>
     * @param mUriSong
     * @param mUriAlbum
     */
    private void getSongList() {
        if (uInit) {
            int start = (mCurrentPage - 1) * ROWPERPAGE;
            getPerPage(start);
            return;
        }
        uSelectedList = true;
        Cursor mCursor = getContentResolver().query(MUSIC_URI_EXTERNAL_SONG, null,
                mFillterMimeType,
                mFillterArgsMp3, null);
        int mTotalRow = mCursor.getCount();
        int mTotalPage1 = mTotalRow / ROWPERPAGE;
        int mTotalPage2 = mTotalRow % ROWPERPAGE;
        if (mTotalPage2 > 0) {
            mTotalPage1++;
        }
        mTotalPage = mTotalPage1;
        Uri mUriSong1 = MUSIC_URI_EXTERNAL_SONG.buildUpon()
                .encodedQuery("limit=0," + ROWPERPAGE).build();
        mCursor = getContentResolver().query(mUriSong1, null,
                mFillterMimeType,
                mFillterArgsMp3, null);
        addItemList(mCursor);
        uInit = true;
    }

    /**
     * get pasth image of album
     * @param id
     * @param mUri
     * @return
     */
    private String getPathImageAlbum(String id) {
        Cursor cursor = getContentResolver().query(
                MUSIC_URI_EXTENAL_ALBUM,
                new String[] {
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ALBUM_ART
                },
                MediaStore.Audio.Albums._ID + "=?", new String[] {
                    id + ""
                },
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int column = cursor
                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            if (column <= 0) {
                return null;
            }
            String path = cursor.getString(column);
            return path;
        }
        return null;
    }
}
