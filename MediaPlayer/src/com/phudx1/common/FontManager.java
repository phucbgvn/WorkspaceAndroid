package com.phudx1.common;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontManager {

	private static FontManager sFontManager;

	private Map<String, Typeface> mMapFont = new HashMap<>();

	private FontManager() {
	}

	public static FontManager getInstance() {
		if (sFontManager == null) {
			sFontManager = new FontManager();
		}
		return sFontManager;
	}

	public void updateFont(Context context, TextViewCustom... views) {
		for (TextViewCustom textView : views) {
			String fontPath = textView.getFontPath();
			if (fontPath != null) {
				Typeface typeface = mMapFont.get(fontPath);
				if (typeface == null) {
					typeface = Typeface.createFromAsset(context.getAssets(),
							fontPath);
					mMapFont.put(fontPath, typeface);
				}
				textView.setTypeface(typeface);
			}
		}
	}
}
