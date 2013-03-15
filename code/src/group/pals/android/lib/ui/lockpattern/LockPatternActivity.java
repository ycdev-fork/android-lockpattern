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

import group.pals.android.lib.ui.lockpattern.util.IEncrypter;
import group.pals.android.lib.ui.lockpattern.util.InvalidEncrypterException;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main activity for this library.
 * <p>
 * You must use either {@link #_ActionCreatePattern} or
 * {@link #_ActionComparePattern}. Otherwise an
 * {@link UnsupportedOperationException} will be thrown.
 * </p>
 * 
 * @author Hai Bison
 * @since v1.0
 */
public class LockPatternActivity extends Activity {

    private static final String _ClassName = LockPatternActivity.class
            .getName();

    /**
     * Use this action to create new pattern. You can provide an
     * {@link IEncrypter} with {@link #_EncrypterClass} to improve security.
     * <p>
     * If the use created a pattern, {@link Activity#RESULT_OK} returns with the
     * pattern ({@link #_Pattern}). Otherwise {@link Activity#RESULT_CANCELED}
     * returns.
     * </p>
     * 
     * @since v2.4 beta
     * @see #_EncrypterClass
     */
    public static final String _ActionCreatePattern = _ClassName
            + ".create_pattern";

    /**
     * Use this action to compare pattern. You provide the pattern to be
     * compared with {@link #_Pattern}.
     * <p>
     * If the user passed, {@link Activity#RESULT_OK} returns. If not,
     * {@link Activity#RESULT_CANCELED} returns.
     * </p>
     * 
     * @since v2.4 beta
     * @see #_Pattern
     * @see #_EncrypterClass
     */
    public static final String _ActionComparePattern = _ClassName
            + ".compare_pattern";

    /**
     * Sets value of this key to a theme in {@code android.R.style.Theme_*}.<br>
     * Default is:<br>
     * 
     * <li>{@link android.R.style#Theme_DeviceDefault} for {@code SDK >= }
     * {@link Build.VERSION_CODES#ICE_CREAM_SANDWICH}</li>
     * 
     * <li>{@link android.R.style#Theme_Holo} for {@code SDK >= }
     * {@link Build.VERSION_CODES#HONEYCOMB}</li>
     * 
     * <li>{@link android.R.style#Theme} for older systems</li>
     * 
     * @since v1.5.3 beta
     */
    public static final String _Theme = _ClassName + ".theme";

    /**
     * Specify if the pattern will be saved automatically or not. Default =
     * {@code false}
     */
    public static final String _AutoSave = _ClassName + ".auto_save";

    /**
     * Maximum retry times, in mode {@link #ComparePattern}, default is
     * {@code 5}.
     */
    public static final String _MaxRetry = _ClassName + ".max_retry";

    /**
     * Key to hold pattern. Can be a SHA-1 string <i><b>or</b></i> an encrypted
     * string of its (if {@link #_EncrypterClass} is used).
     * 
     * @since v2 beta
     */
    public static final String _Pattern = _ClassName + ".pattern";

    /**
     * Key to hold implemented class of {@link IEncrypter}.<br>
     * If {@code null}, nothing will be used.
     * 
     * @since v2 beta
     */
    public static final String _EncrypterClass = IEncrypter.class.getName();

    /**
     * You can provide an {@link ResultReceiver} with this key. The activity
     * will notify your receiver the same result code and intent data as you
     * will receive them in {@code onActivityResult()}.
     * 
     * @since v2.4 beta
     */
    public static final String _ResultReceiver = _ClassName
            + ".result_receiver";

    /**
     * Set to {@code true} if you want to use invisible pattern (there will be
     * no visible feedback as the user enters the pattern).
     * <p>
     * Default: {@code false}
     * </p>
     */
    public static final String _StealthMode = _ClassName + ".stealth_mode";

    /*
     * FIELDS
     */
    private SharedPreferences mPrefs;
    private int mMaxRetry;
    private boolean mAutoSave;
    private IEncrypter mEncrypter;
    private ResultReceiver mResultReceiver;

    /*
     * CONTROLS
     */
    private TextView mTxtInfo;
    private LockPatternView mLockPatternView;
    private View mFooter;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG)
            Log.d(_ClassName, "ClassName = " + _ClassName);

        /*
         * THEME
         */

        if (getIntent().hasExtra(_Theme))
            setTheme(getIntent().getIntExtra(_Theme, R.style.Alp_Theme_Dark));

        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(LockPatternActivity.class.getName(), 0);

        if (!_ActionCreatePattern.equals(getIntent().getAction())
                && !_ActionComparePattern.equals(getIntent().getAction()))
            throw new UnsupportedOperationException("Unknown Action >> "
                    + getIntent().getAction());

        mMaxRetry = getIntent().getIntExtra(_MaxRetry, 5);

        /*
         * Set this to false by default, for security enhancement.
         */
        mAutoSave = getIntent().getBooleanExtra(_AutoSave, false);
        /*
         * If false, clear previous values (currently it is the pattern only).
         */
        if (!mAutoSave)
            mPrefs.edit().clear().commit();

        /*
         * Encrypter.
         */
        Class<?> encrypterClass = (Class<?>) getIntent().getSerializableExtra(
                _EncrypterClass);
        if (encrypterClass != null) {
            try {
                mEncrypter = (IEncrypter) encrypterClass.newInstance();
            } catch (Throwable t) {
                throw new InvalidEncrypterException();
            }
        }

        /*
         * ResultReceiver.
         */
        mResultReceiver = getIntent().getParcelableExtra(_ResultReceiver);

        init();
    }// onCreate()

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(_ClassName, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        init();
    }// onConfigurationChanged()

    /**
     * Initializes UI...
     */
    private void init() {
        /*
         * In case screen orientation changed, save all controls' state to
         * restore later.
         */
        CharSequence info = mTxtInfo != null ? mTxtInfo.getText() : null;
        CharSequence btnConfirmText = mBtnConfirm != null ? mBtnConfirm
                .getText() : null;
        Boolean btnConfirmEnabled = mBtnConfirm != null ? mBtnConfirm
                .isEnabled() : null;
        LockPatternView.DisplayMode lastDisplayMode = mLockPatternView != null ? mLockPatternView
                .getDisplayMode() : null;
        List<Cell> lastPattern = mLockPatternView != null ? mLockPatternView
                .getPattern() : null;

        setContentView(R.layout.alp_lock_pattern_activity);

        mTxtInfo = (TextView) findViewById(R.id.alp_info);
        mLockPatternView = (LockPatternView) findViewById(R.id.alp_lock_pattern);

        mFooter = findViewById(R.id.alp_footer);
        mBtnCancel = (Button) findViewById(R.id.alp_cancel);
        mBtnConfirm = (Button) findViewById(R.id.alp_confirm);

        /*
         * LOCK PATTERN VIEW
         */

        /*
         * Haptic feedback.
         */
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = Settings.System.getInt(
                    getContentResolver(),
                    Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
        } catch (Throwable t) {
            /*
             * Ignore it.
             */
        }
        mLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);

        mLockPatternView.setInStealthMode(getIntent().getBooleanExtra(
                _StealthMode, false));
        mLockPatternView.setOnPatternListener(mPatternViewListener);
        if (lastPattern != null && lastDisplayMode != null)
            mLockPatternView.setPattern(lastDisplayMode, lastPattern);

        /*
         * COMMAND BUTTONS
         */

        if (_ActionCreatePattern.equals(getIntent().getAction())) {
            mBtnCancel.setOnClickListener(mBtnCancelOnClickListener);
            mBtnConfirm.setOnClickListener(mBtnConfirmOnClickListener);

            mFooter.setVisibility(View.VISIBLE);

            if (info != null)
                mTxtInfo.setText(info);
            else
                mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);

            if (btnConfirmText != null) {
                mBtnConfirm.setText(btnConfirmText);
                mBtnConfirm.setEnabled(btnConfirmEnabled);
            }
        }// _ActionCreatePattern
        else if (_ActionComparePattern.equals(getIntent().getAction())) {
            mFooter.setVisibility(View.GONE);

            if (info != null)
                mTxtInfo.setText(info);
            else
                mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
        }// _ActionComparePattern

        setResult(RESULT_CANCELED);
    }// init()

    /**
     * Encodes {@code pattern} to a string.
     * <p>
     * <li>If {@link #_EncrypterClass} is not set, returns SHA-1 of
     * {@code pattern}.</li>
     * 
     * <li>If {@link #_EncrypterClass} is set, calculates SHA-1 of
     * {@code pattern}, then encrypts the SHA-1 value and returns the result.</li>
     * </p>
     * 
     * @param pattern
     *            the pattern.
     * @return SHA-1 of {@code pattern}, or encrypted string of its.
     * @since v2 beta
     */
    private String encodePattern(List<Cell> pattern) {
        if (mEncrypter == null) {
            return LockPatternUtils.patternToSha1(pattern);
        } else {
            try {
                return mEncrypter.encrypt(this,
                        LockPatternUtils.patternToSha1(pattern));
            } catch (Throwable t) {
                throw new InvalidEncrypterException();
            }
        }
    }// encodePattern()

    private int mRetryCount = 0;

    private void doComparePattern(List<Cell> pattern) {
        if (pattern == null)
            return;

        mLastPattern = new ArrayList<LockPatternView.Cell>();
        mLastPattern.addAll(pattern);

        String currentPattern = getIntent().getStringExtra(_Pattern);
        if (currentPattern == null)
            currentPattern = mPrefs.getString(_Pattern, null);

        if (encodePattern(pattern).equals(currentPattern))
            finishWithResultOk(null);
        else {
            mRetryCount++;

            if (mRetryCount >= mMaxRetry)
                finishWithResultCancelled();
            else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                mTxtInfo.setText(R.string.alp_msg_try_again);
            }
        }
    }// doComparePattern()

    private List<Cell> mLastPattern;

    private void doCreatePattern(List<Cell> pattern) {
        if (pattern.size() < 4) {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            mTxtInfo.setText(R.string.alp_msg_connect_4dots);
            return;
        }

        if (mLastPattern == null) {
            mLastPattern = new ArrayList<LockPatternView.Cell>();
            mLastPattern.addAll(pattern);
            mTxtInfo.setText(R.string.alp_msg_pattern_recorded);
            mBtnConfirm.setEnabled(true);
        } else {
            if (encodePattern(mLastPattern).equals(encodePattern(pattern))) {
                mTxtInfo.setText(R.string.alp_msg_your_new_unlock_pattern);
                mBtnConfirm.setEnabled(true);
            } else {
                mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                mBtnConfirm.setEnabled(false);
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            }
        }
    }// doCreatePattern()

    /**
     * Finishes activity with {@link Activity#RESULT_OK}.
     * 
     * @param pattern
     *            the pattern, if this is in mode creating pattern. Can be
     *            {@code null}.
     */
    private void finishWithResultOk(String pattern) {
        if (pattern != null) {
            Intent i = new Intent();
            i.putExtra(_Pattern, pattern);
            setResult(RESULT_OK, i);
        } else
            setResult(RESULT_OK);

        finish();

        if (mResultReceiver != null) {
            Bundle bundle = null;
            if (pattern != null) {
                bundle = new Bundle();
                bundle.putString(_Pattern, pattern);
            }
            mResultReceiver.send(RESULT_OK, bundle);
        }
    }// finishWithResultOk()

    /**
     * Finishes the activity with {@link Activity#RESULT_CANCELED}.
     */
    private void finishWithResultCancelled() {
        setResult(RESULT_CANCELED);
        finish();

        if (mResultReceiver != null)
            mResultReceiver.send(RESULT_CANCELED, null);
    }// finishWithResultCancelled()

    /*
     * LISTENERS
     */

    private final LockPatternView.OnPatternListener mPatternViewListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (_ActionCreatePattern.equals(getIntent().getAction())) {
                mTxtInfo.setText(R.string.alp_msg_release_finger_when_done);
                mBtnConfirm.setEnabled(false);
                if (getString(R.string.alp_cmd_continue).equals(
                        mBtnConfirm.getText()))
                    mLastPattern = null;
            }
        }// onPatternStart()

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            if (_ActionCreatePattern.equals(getIntent().getAction()))
                doCreatePattern(pattern);
            else if (_ActionComparePattern.equals(getIntent().getAction()))
                doComparePattern(pattern);
        }// onPatternDetected()

        @Override
        public void onPatternCleared() {
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (_ActionCreatePattern.equals(getIntent().getAction())) {
                mBtnConfirm.setEnabled(false);
                if (getString(R.string.alp_cmd_continue).equals(
                        mBtnConfirm.getText())) {
                    mLastPattern = null;
                    mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);
                } else
                    mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
            } else if (_ActionComparePattern.equals(getIntent().getAction())) {
                mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
            }
        }// onPatternCleared()

        @Override
        public void onPatternCellAdded(List<Cell> pattern) {
            // TODO Auto-generated method stub
        }// onPatternCellAdded()
    };// mPatternViewListener

    private final View.OnClickListener mBtnCancelOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finishWithResultCancelled();
        }// onClick()
    };// mBtnCancelOnClickListener

    private final View.OnClickListener mBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (getString(R.string.alp_cmd_continue).equals(
                    mBtnConfirm.getText())) {
                mLockPatternView.clearPattern();
                mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                mBtnConfirm.setText(R.string.alp_cmd_confirm);
                mBtnConfirm.setEnabled(false);
            } else {
                if (mAutoSave)
                    mPrefs.edit()
                            .putString(_Pattern, encodePattern(mLastPattern))
                            .commit();

                finishWithResultOk(encodePattern(mLastPattern));
            }
        }// onClick()
    };// mBtnConfirmOnClickListener
}
