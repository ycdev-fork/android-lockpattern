/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package group.pals.android.lib.ui.lockpattern.demo;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQ_CREATE_PATTERN = 0;
    private static final int REQ_ENTER_PATTERN = 1;

    private CheckBox mChkLightTheme;
    private CheckBox mChkDialogTheme;
    private CheckBox mChkStealthMode;
    private TextView mTextMinWiredDots;
    private SeekBar mBarMinWiredDots;
    private TextView mTextMaxTries;
    private SeekBar mBarMaxTries;
    private Button mBtnCreateLockPattern;
    private Button mBtnEnterLockPattern;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        /*
         * MAP CONTROLS
         */

        mChkLightTheme = (CheckBox) findViewById(R.id.checkbox_light_theme);
        mChkDialogTheme = (CheckBox) findViewById(R.id.checkbox_dialog_theme);
        mChkStealthMode = (CheckBox) findViewById(R.id.checkbox_stealth_mode);
        mTextMinWiredDots = (TextView) findViewById(R.id.text_min_wired_dots);
        mBarMinWiredDots = (SeekBar) findViewById(R.id.seek_bar_min_wired_dots);
        mTextMaxTries = (TextView) findViewById(R.id.text_max_tries);
        mBarMaxTries = (SeekBar) findViewById(R.id.seek_bar_max_tries);
        mBtnCreateLockPattern = (Button) findViewById(R.id.button_create_lockpattern);
        mBtnEnterLockPattern = (Button) findViewById(R.id.button_enter_lockpattern);

        /*
         * SET LISTENERS
         */

        mChkStealthMode.setChecked(DisplayPrefs.isStealthMode(this));
        mChkStealthMode
                .setOnCheckedChangeListener(mChkStealthModeOnCheckedChangeListener);

        mBarMaxTries.setProgress(DisplayPrefs.getMaxRetry(this));
        mBarMinWiredDots.setProgress(DisplayPrefs.getMinWiredDots(this));
        for (SeekBar sb : new SeekBar[] { mBarMinWiredDots, mBarMaxTries }) {
            sb.setOnSeekBarChangeListener(mSeekBarsOnChangeListener);
            mSeekBarsOnChangeListener.onProgressChanged(sb, sb.getProgress(),
                    false);
        }

        mBtnCreateLockPattern
                .setOnClickListener(mBtnCreateLockPatternOnClickListener);
        mBtnEnterLockPattern
                .setOnClickListener(mBtnEnterLockPatternOnClickListener);

        /*
         * LOCKPATTERN PREFERENCES
         */

        SecurityPrefs.setAutoSavePattern(this, true);
        SecurityPrefs.setEncrypterClass(this, LPEncrypter.class);
    }// onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQ_CREATE_PATTERN: {
            if (resultCode == RESULT_OK)
                setTitle(new String(
                        data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN)));
            else
                setTitle(R.string.app_name);

            break;
        }// REQ_CREATE_PATTERN

        case REQ_ENTER_PATTERN: {
            int msgId = 0;

            switch (resultCode) {
            case RESULT_OK:
                msgId = android.R.string.ok;
                break;
            case RESULT_CANCELED:
                msgId = android.R.string.cancel;
                break;
            case LockPatternActivity.RESULT_FAILED:
                msgId = R.string.failed;
                break;
            default:
                return;
            }

            String msg = String.format("%s (%,d tries)", getString(msgId),
                    data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0));

            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            break;
        }// REQ_ENTER_PATTERN
        }
    }// onActivityResult()

    /**
     * Gets the theme that the user chose to apply to
     * {@link LockPatternActivity}.
     * 
     * @return the theme for {@link LockPatternActivity}.
     */
    private int getThemeForLockPatternActivity() {
        if (mChkLightTheme.isChecked())
            return mChkDialogTheme.isChecked() ? R.style.Alp_Theme_Dialog_Light
                    : R.style.Alp_Theme_Light;
        return mChkDialogTheme.isChecked() ? R.style.Alp_Theme_Dialog_Dark
                : R.style.Alp_Theme_Dark;
    }// getThemeForLockPatternActivity()

    /*
     * LISTENERS
     */

    private final CompoundButton.OnCheckedChangeListener mChkStealthModeOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            DisplayPrefs.setStealthMode(MainActivity.this, isChecked);
        }// onCheckedChanged()
    };// mChkStealthModeOnCheckedChangeListener

    private final SeekBar.OnSeekBarChangeListener mSeekBarsOnChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            /*
             * Do nothing.
             */
        }// onStopTrackingTouch()

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            /*
             * Do nothing.
             */
        }// onStartTrackingTouch()

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            if (progress == 0) {
                seekBar.setProgress(1);
                return;
            }

            if (seekBar.getId() == R.id.seek_bar_max_tries) {
                mTextMaxTries.setText(getString(
                        R.string.pmsg_max_tries_allowed, progress));
                if (fromUser)
                    DisplayPrefs.setMaxRetry(MainActivity.this, progress);
            } else if (seekBar.getId() == R.id.seek_bar_min_wired_dots) {
                mTextMinWiredDots.setText(getString(
                        R.string.pmsg_min_wired_dots, progress));
                if (fromUser)
                    DisplayPrefs.setMinWiredDots(MainActivity.this, progress);
            }
        }// onProgressChanged()
    };// mSeekBarsOnChangeListener

    private final View.OnClickListener mBtnCreateLockPatternOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN,
                    null, MainActivity.this, LockPatternActivity.class);
            i.putExtra(LockPatternActivity.EXTRA_THEME,
                    getThemeForLockPatternActivity());
            startActivityForResult(i, REQ_CREATE_PATTERN);
        }// onClick()
    };// mBtnCreateLockPatternOnClickListener

    private final View.OnClickListener mBtnEnterLockPatternOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN,
                    null, MainActivity.this, LockPatternActivity.class);
            i.putExtra(LockPatternActivity.EXTRA_THEME,
                    getThemeForLockPatternActivity());
            startActivityForResult(i, REQ_ENTER_PATTERN);
        }// onClick()
    };// mBtnEnterLockPatternOnClickListener
}
