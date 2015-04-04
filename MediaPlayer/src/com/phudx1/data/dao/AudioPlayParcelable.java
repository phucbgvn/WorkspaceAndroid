package com.phudx1.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioPlayParcelable implements Parcelable {
	private AudioPlay mAudioPlay;

	public AudioPlayParcelable(AudioPlay mAudioPlays) {
		mAudioPlay = mAudioPlays;
	}

	public AudioPlay getmAudioPlay() {
		return mAudioPlay;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private AudioPlayParcelable(Parcel in) {
		mAudioPlay = (AudioPlay) in.readValue(AudioPlay.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(mAudioPlay);
	}

	public static final Parcelable.Creator<AudioPlayParcelable> CREATOR = new Parcelable.Creator<AudioPlayParcelable>() {
		@Override
		public AudioPlayParcelable createFromParcel(Parcel source) {
			return new AudioPlayParcelable(source);
		}

		public AudioPlayParcelable[] newArray(int size) {
			return new AudioPlayParcelable[size];
		}

	};
}
