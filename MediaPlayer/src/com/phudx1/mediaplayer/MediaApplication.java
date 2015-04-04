package com.phudx1.mediaplayer;

import android.app.Application;

public class MediaApplication extends Application {

	private MediaActivity mMediaActivity;

	public void setMediaActivity(MediaActivity mMediaActivity) {
		this.mMediaActivity = mMediaActivity;
	}

	public MediaActivity getMediaActivity() {
		return mMediaActivity;
	}

}
