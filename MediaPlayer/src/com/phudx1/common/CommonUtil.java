package com.phudx1.common;

import java.util.Locale;

public class CommonUtil {
    public static final int START_AUDIO = 0;
    public static final int START_PAGE = 1;
    public static final int PLAY = 101;
    public static final int PAUSE = 102;
    public static final int SET_DATA_SOURCE = 103;
    public static final int SET_LIST_AUDIO = 104;
    public static final int NEXT = 105;
    public static final int PREV = 106;
    public static final int REPEAT = 107;
    public static final int SUF = 108;
    // public static final int IS_PLAY = 109;
    public static final int IS_PLAY_RECIVER = 110;
    public static final int CLICK_SUF = 112;
    public static final int REPEAT_RECIVE = 113;
    public static final int CHECK_REPEAT = 114;
    public static final int CHECK_SUF = 115;
    public static final int START_SEEKBAR = 121;
    public static final int STOP_SEEKBAR = 122;
    public static final int SET_LIST_AUDIO_RECIVER = 124;
    public static final int GET_NUMBER_PLAY = 125;
    public static final int CHANGE_PAGE = 126;
    public static final int CHANGE_SELECTED_ITEM = 127;
    public static final int NEXT_PAGE = 128;
    public static final int BACK_PAGE = 129;
    public static final String SEND_CHANGE_PAGE = "01";
    public static final String SET_LIST_KEY = "02";
    public static final String SET_DATA_SOURCE_KEY = "03";
    public static final String CUR_PLAY = "04";
    public static final String IS_PLAY_KEY = "05";
    public static final String SUF_RECIVER_KEY = "06";
    public static final String REPEAT_RECIVER_KEY = "07";
    public static final String CHECK_REPEAT_KEY = "08";
    public static final String CHECK_SUF_KEY = "09";
    public static final String NUMBER_TIME_KEY = "10";
    public static final String NUMBER_PLAY = "11";
    public static final String SEND_TOTAL_PAGE = "12";
    public static final String SELECTED_LIST = "14";
    public static final String SEND_OLD_PAGE = "15";
    public static final String REGEX = "/";

    /**
     * return time from time secon
     * @param mTimeSecon
     * @return
     */
    public static String getTimeFromSecon(int mTimeSecon) {
        int mTimeHour = mTimeSecon / 3600;
        int mTimeMin = (mTimeSecon / 60) % 60;
        int mSecon = mTimeSecon % 60;
        String mTimeConvert;
        if (mTimeHour > 0) {
            mTimeConvert = String.format(Locale.ENGLISH, "%d:%02d:%02d",
                    mTimeHour, mTimeMin, mSecon);
        } else {
            mTimeConvert = String.format(Locale.ENGLISH, "%02d:%02d", mTimeMin,
                    mSecon);
        }
        return mTimeConvert;
    }

    /**
     * return time from time minisecon
     * @param mTimeMinisecon
     * @return
     */
    public static String getTimeFromMinisecon(int mTimeMinisecon) {
        int mTotalsecon = mTimeMinisecon / 1000;
        int mTimeHour = mTotalsecon / 3600;
        int mTimeMin = (mTotalsecon / 60) % 60;
        int mSecon = mTotalsecon % 60;
        String mTimeConvert;
        if (mTimeHour > 0) {
            mTimeConvert = String.format(Locale.ENGLISH, "%d:%02d:%02d",
                    mTimeHour, mTimeMin, mSecon);
        } else {
            mTimeConvert = String.format(Locale.ENGLISH, "%02d:%02d", mTimeMin,
                    mSecon);
        }
        return mTimeConvert;
    }
}
