/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.mpelant.deskclock;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;

@TargetApi(17)
public class Screensaver extends DreamService {
    static final boolean DEBUG = false;
    static final String TAG = "DeskClock/Screensaver";

    private View mContentView, mSaverView;
    private View mAnalogClock, mDigitalClock;

    private final Handler mHandler = new Handler();

    private final ScreensaverRunnable mMoveSaverRunnable;

    public Screensaver() {
        if (DEBUG)
            Log.d(TAG, "Screensaver allocated");
        mMoveSaverRunnable = new ScreensaverRunnable(mHandler);
    }

    @Override
    public void onCreate() {
        if (DEBUG)
            Log.d(TAG, "Screensaver created");
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (DEBUG)
            Log.d(TAG, "Screensaver configuration changed");
        super.onConfigurationChanged(newConfig);
        mHandler.removeCallbacks(mMoveSaverRunnable);
        layoutClockSaver();
    }

    @Override
    public void onAttachedToWindow() {
        if (DEBUG)
            Log.d(TAG, "Screensaver attached to window");
        super.onAttachedToWindow();

        // We want the screen saver to exit upon user interaction.
        setInteractive(false);
        setFullscreen(true);
        layoutClockSaver();
    }

    @Override
    public void onDetachedFromWindow() {
        if (DEBUG)
            Log.d(TAG, "Screensaver detached from window");
        super.onDetachedFromWindow();

        mHandler.removeCallbacks(mMoveSaverRunnable);
    }

    private void setClockStyle() {
        Utils.setClockStyle(this, mDigitalClock, mAnalogClock, ScreensaverSettingsActivity.KEY_CLOCK_STYLE);
        mSaverView = findViewById(R.id.main_clock);
        int brightness = PreferenceManager.getDefaultSharedPreferences(this).getInt(ScreensaverSettingsActivity.KEY_BRIGHTNESS, ScreensaverSettingsActivity.BRIGHTNESS_DEFAULT);
        Utils.dimView(brightness, mSaverView);
        setScreenBright(brightness>=ScreensaverSettingsActivity.BRIGHTNESS_NIGHT);
    }

    private void layoutClockSaver() {
        if (getWindow() == null || mSaverView == null || mMoveSaverRunnable == null || mHandler == null)// fix for a weird fc
            return;
        setContentView(R.layout.desk_clock_saver);
        mDigitalClock = findViewById(R.id.digital_clock);
        mAnalogClock = findViewById(R.id.analog_clock);
        setClockStyle();
        mContentView = (View) mSaverView.getParent();
        mSaverView.setAlpha(0);

        mMoveSaverRunnable.registerViews(mContentView, mSaverView);
        mHandler.post(mMoveSaverRunnable);
    }
}
