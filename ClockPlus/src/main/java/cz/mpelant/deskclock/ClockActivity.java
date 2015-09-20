
package cz.mpelant.deskclock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ClockActivity extends BaseScreenOnActivity {
    private static final int SCREENSAVER_DELAY = 1000 * 30;
    public static final String TAG = "ClockActivity";
    private View mDigitalClock;
    private View mAnalogClock;
    private TextView mDate;
    private TextView mNextAlarm;
    private final Handler mHandler = new Handler();
    private final Runnable startScreenSaverRunnable = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(ClockActivity.this, ScreensaverActivity.class));
            overridePendingTransition(R.anim.undim, R.anim.dim);
        }
    };


    

    private void setClockStyle() {
        Utils.setClockStyle(this, mDigitalClock, mAnalogClock, ScreensaverSettingsActivity.KEY_CLOCK_STYLE);
    }

    @Override
    public void onResume() {
        setContentView(R.layout.desk_clock_saver);

        String size = PreferenceManager.getDefaultSharedPreferences(this).getString(
                ScreensaverSettingsActivity.KEY_CLOCK_SIZE,
                ScreensaverSettingsActivity.SIZE_DEFAULT);
        Utils.resizeContent((ViewGroup) findViewById(R.id.main_clock), size);


        mDigitalClock = findViewById(R.id.digital_clock);
        mAnalogClock = findViewById(R.id.analog_clock);
        ((View)mAnalogClock.getParent().getParent()).setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                startScreenSaverRunnable.run();
                return true;
            }
        });
        mDate = (TextView) findViewById(R.id.date);
        mNextAlarm = (TextView) findViewById(R.id.nextAlarm);

        setClockStyle();
        updateViews();


        mHandler.postDelayed(startScreenSaverRunnable, SCREENSAVER_DELAY);
        super.onResume();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(startScreenSaverRunnable);
        super.onPause();
    }
    
    @Override
    public void onUserInteraction() {
        mHandler.removeCallbacks(startScreenSaverRunnable);
        mHandler.postDelayed(startScreenSaverRunnable, SCREENSAVER_DELAY);
        super.onUserInteraction();
    }

    @Override
    protected void updateViews() {
        Utils.setAlarmTextView(this, mNextAlarm);
        Utils.setDateTextView(this, mDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        final Intent alarmIntent = Utils.getAlarmPackage(this);
        if (alarmIntent != null) {
            item = menu.add("Alarms").setIcon(R.drawable.ic_action_alarm);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startActivity(alarmIntent);
                    return true;
                }
            });
        }

        item = menu.add("Settings").setIcon(R.drawable.ic_action_settings);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(ClockActivity.this, ScreensaverSettingsActivity.class));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
