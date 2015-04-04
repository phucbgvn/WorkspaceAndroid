package com.phudx1.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.phudx1.mediaplayer.R;

public class TextViewCustom extends TextView {

	private String mFontPath;

	public TextViewCustom(Context context) {
		super(context);
	}

	public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.CustomFont);
		String string = array.getString(R.styleable.CustomFont_fontName);
		if (string != null) {
			mFontPath = string;
		}
		array.recycle();
	}

	public TextViewCustom(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public String getFontPath() {
		return mFontPath;
	}

}
