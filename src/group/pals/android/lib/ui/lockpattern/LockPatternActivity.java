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

package group.pals.android.lib.ui.lockpattern;

import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main activity for this library.
 * 
 * @author Hai Bison
 * @since v1.0
 */
public class LockPatternActivity extends Activity {

    /**
     * Mode for {@link LockPatternActivity}. Default is
     * {@link LPMode#CreatePattern}<br>
     * Acceptable values:<br>
     * - {@link LPMode#CreatePattern}<br>
     * - {@link LPMode#ComparePattern}
     */
    public static final String _Mode = LPMode.class.getName();

    /**
     * Lock pattern mode for this activity.
     * 
     * @author Hai Bison
     * @since v1.3 alpha
     */
    public static enum LPMode {
        /**
         * Creates new pattern.
         */
        CreatePattern,
        /**
         * Compares to existing pattern.
         */
        ComparePattern
    }

    /**
     * Specify if the pattern will be saved automatically or not. Default =
     * {@code true}
     */
    public static final String _AutoSave = "auto_save";

    /**
     * Maximum retry times, in mode {@link #ComparePattern}
     */
    public static final String _MaxRetry = "max_retry";

    /**
     * Key to hold pattern result (in SHA-1 string).
     */
    public static final String _PaternSha1 = "pattern_sha1";

    private SharedPreferences mPrefs;
    private LPMode mMode;
    private int mMaxRetry;
    private boolean mAutoSave;

    private TextView mTxtInfo;
    private LockPatternView mLockPatternView;
    private View mFooter;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alp_lock_pattern_activity);

        mPrefs = getSharedPreferences(LockPatternActivity.class.getSimpleName(), 0);

        mMode = (LPMode) getIntent().getSerializableExtra(_Mode);
        if (mMode == null)
            mMode = LPMode.CreatePattern;

        mMaxRetry = getIntent().getIntExtra(_MaxRetry, 5);
        mAutoSave = getIntent().getBooleanExtra(_AutoSave, true);

        mTxtInfo = (TextView) findViewById(R.id.alp_lpa_text_info);
        mLockPatternView = (LockPatternView) findViewById(R.id.alp_lpa_lockPattern);

        mFooter = findViewById(R.id.alp_lpa_layout_footer);
        mBtnCancel = (Button) findViewById(R.id.alp_lpa_button_cancel);
        mBtnConfirm = (Button) findViewById(R.id.alp_lpa_button_confirm);

        init();
    }// onCreate()

    private void init() {
        // haptic feedback
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
        } catch (Throwable t) {
            // ignore it
        }
        mLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);

        mLockPatternView.setOnPatternListener(fPatternViewListener);

        switch (mMode) {
        case CreatePattern:
            mBtnCancel.setOnClickListener(fBtnCancelOnClickListener);
            mBtnConfirm.setOnClickListener(fBtnConfirmOnClickListener);

            mFooter.setVisibility(View.VISIBLE);
            mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);
            break;
        case ComparePattern:
            mFooter.setVisibility(View.GONE);
            mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
            break;
        }

        setResult(RESULT_CANCELED);
    }// init()

    private int retryCount = 0;

    private void doComparePattern(String pattern) {
        if (pattern == null)
            return;

        String currentPattern = getIntent().getStringExtra(_PaternSha1);
        if (currentPattern == null)
            currentPattern = mPrefs.getString(_PaternSha1, null);

        if (pattern.equals(currentPattern)) {
            setResult(RESULT_OK);
            finish();
        } else {
            retryCount++;

            if (retryCount >= mMaxRetry) {
                setResult(RESULT_CANCELED);
                finish();
            } else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                mTxtInfo.setText(R.string.alp_msg_try_again);
            }
        }
    }// doComparePattern()

    private String lastPattern;

    private void doCreatePattern(List<Cell> pattern) {
        if (pattern.size() < 4) {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            mTxtInfo.setText(R.string.alp_msg_connect_4dots);
            return;
        }

        if (lastPattern == null) {
            lastPattern = LockPatternUtils.patternToSha1(pattern);
            mTxtInfo.setText(R.string.alp_msg_pattern_recorded);
            mBtnConfirm.setEnabled(true);
        } else {
            if (lastPattern.equals(LockPatternUtils.patternToSha1(pattern))) {
                mTxtInfo.setText(R.string.alp_msg_your_new_unlock_pattern);
                mBtnConfirm.setEnabled(true);
            } else {
                mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                mBtnConfirm.setEnabled(false);
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            }
        }
    }// doCreatePattern()

    private final LockPatternView.OnPatternListener fPatternViewListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (mMode == LPMode.CreatePattern) {
                mTxtInfo.setText(R.string.alp_msg_release_finger_when_done);
                mBtnConfirm.setEnabled(false);
                if (getString(R.string.alp_cmd_continue).equals(mBtnConfirm.getText()))
                    lastPattern = null;
            }
        }

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            switch (mMode) {
            case CreatePattern:
                doCreatePattern(pattern);
                break;
            case ComparePattern:
                doComparePattern(LockPatternUtils.patternToSha1(pattern));
                break;
            }
        }// onPatternDetected()

        @Override
        public void onPatternCleared() {
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            switch (mMode) {
            case CreatePattern:
                mBtnConfirm.setEnabled(false);
                if (getString(R.string.alp_cmd_continue).equals(mBtnConfirm.getText())) {
                    lastPattern = null;
                    mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);
                } else
                    mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                break;
            case ComparePattern:
                mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
                break;
            }
        }// onPatternCleared()

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {
            // TODO Auto-generated method stub
        }
    };// fPatternViewListener

    private final View.OnClickListener fBtnCancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };// fBtnCancelOnClickListener

    private final View.OnClickListener fBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (getString(R.string.alp_cmd_continue).equals(mBtnConfirm.getText())) {
                mLockPatternView.clearPattern();
                mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                mBtnConfirm.setText(R.string.alp_cmd_confirm);
                mBtnConfirm.setEnabled(false);
            } else {
                if (mAutoSave)
                    mPrefs.edit().putString(_PaternSha1, lastPattern).commit();

                Intent i = new Intent();
                i.putExtra(_PaternSha1, lastPattern);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    };// fBtnConfirmOnClickListener
}
