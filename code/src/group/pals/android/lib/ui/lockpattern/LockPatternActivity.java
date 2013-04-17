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
import group.pals.android.lib.ui.lockpattern.util.UI;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main activity for this library.
 * <p>
 * You must use either {@link #_ActionCreatePattern} or
 * {@link #_ActionComparePattern}. Otherwise an
 * {@link UnsupportedOperationException} will be thrown.
 * </p>
 * <p>
 * You can deliver result to {@link PendingIntent}s and/ or
 * {@link ResultReceiver} too. See {@link #_OkPendingIntent},
 * {@link #_CancelledPendingIntent} and {@link #_ResultReceiver} for more
 * details.
 * </p>
 * <p>
 * <strong>NOTES</strong>
 * <li>You must use one of the themes that this library provides. They start
 * with {@code R.style.Alp_Theme_*}. The reason is the themes contain resources
 * that the library needs.</li>
 * <li>In mode comparing pattern, there are <strong><i>3 possible result
 * codes</i></strong>: {@link Activity#RESULT_OK},
 * {@link Activity#RESULT_CANCELED} and {@link #_ResultFailed}.</li>
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
     * @see #_OkPendingIntent
     * @see #_CancelledPendingIntent
     */
    public static final String _ActionCreatePattern = _ClassName
            + ".create_pattern";

    /**
     * Use this action to compare pattern. You provide the pattern to be
     * compared with {@link #_Pattern}.
     * <p>
     * If the user passes, {@link Activity#RESULT_OK} returns. If not,
     * {@link #_ResultFailed} returns.
     * </p>
     * <p>
     * If the user cancels the task, {@link Activity#RESULT_CANCELED} returns.
     * </p>
     * <p>
     * In any case, there will be key {@link #_ExtraRetryCount} available in the
     * intent result.
     * </p>
     * 
     * @since v2.4 beta
     * @see #_Pattern
     * @see #_EncrypterClass
     * @see #_OkPendingIntent
     * @see #_CancelledPendingIntent
     * @see #_ResultFailed
     * @see #_ExtraRetryCount
     */
    public static final String _ActionComparePattern = _ClassName
            + ".compare_pattern";

    /**
     * If you use {@link #_ActionComparePattern} and the user fails to "login"
     * after a number of tries, this activity will finish with this result code.
     * 
     * @see #_ActionComparePattern
     * @see #_MaxRetry
     * @see #_ExtraRetryCount
     */
    public static final int _ResultFailed = RESULT_FIRST_USER + 1;

    /**
     * If you use {@link #_ActionComparePattern}, and the user fails to "login"
     * after a number of tries, this key holds that number.
     * 
     * @see #_ActionComparePattern
     * @see #_MaxRetry
     */
    public static final String _ExtraRetryCount = _ClassName + ".retry_count";

    /**
     * Sets value of this key to a theme in {@code R.style.Alp_Theme_*}. Default
     * is the one you set in AndroidManifest.xml.
     * 
     * @since v1.5.3 beta
     */
    public static final String _Theme = _ClassName + ".theme";

    /**
     * Specify if the pattern will be saved automatically or not.
     * <p>
     * Default: {@code false}
     * </p>
     */
    public static final String _AutoSave = _ClassName + ".auto_save";

    /**
     * Use this key to set the minimum wired-dots that are allowed.
     * <p>
     * Default: {@code 4} -- min: {@code 1} -- max: {@code 9}
     * </p>
     */
    public static final String _MinWiredDots = _ClassName + ".min_wired_dots";

    /**
     * Maximum retry times, used with {@link #_ActionComparePattern}.
     * <p>
     * Default: {@code 5}
     * </p>
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
     * will receive them in {@link #onActivityResult(int, int, Intent)}.
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

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link Activity#RESULT_OK} will be returning. If you were calling this
     * activity with {@link #_ActionCreatePattern}, key {@link #_Pattern} will
     * be attached to the original intent which the pending intent holds.
     */
    public static final String _OkPendingIntent = _ClassName
            + ".ok_pending_intent";

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link Activity#RESULT_CANCELED} will be returning.
     */
    public static final String _CancelledPendingIntent = _ClassName
            + ".cancelled_pending_intent";

    /**
     * Helper enum for button OK commands. (Because we use only one "OK" button
     * for different commands).
     * 
     * @author Hai Bison
     * 
     */
    private static enum ButtonOkCommand {
        Continue, Done
    }// ButtonOkCommand

    /*
     * FIELDS
     */
    private SharedPreferences mPrefs;
    private int mMaxRetry;
    private boolean mAutoSave;
    private IEncrypter mEncrypter;
    private int mMinWiredDots;
    private ButtonOkCommand mBtnOkCmd;
    private Intent mIntentResult;

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

        mMinWiredDots = getIntent().getIntExtra(_MinWiredDots, 4);
        if (mMinWiredDots <= 0 || mMinWiredDots > 9)
            mMinWiredDots = 4;

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

        mIntentResult = new Intent();
        setResult(RESULT_CANCELED, mIntentResult);

        initContentView();
    }// onCreate()

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(_ClassName, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        initContentView();
    }// onConfigurationChanged()

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && _ActionComparePattern.equals(getIntent().getAction())) {
            /*
             * Use this hook instead of onBackPressed(), because onBackPressed()
             * is not available in API 4.
             */
            finishWithNegativeResult(RESULT_CANCELED);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }// onKeyDown()

    /**
     * Initializes UI...
     */
    private void initContentView() {
        /*
         * In case screen orientation changed, save all controls' state to
         * restore later.
         */
        CharSequence infoText = mTxtInfo != null ? mTxtInfo.getText() : null;
        Boolean btnOkEnabled = mBtnConfirm != null ? mBtnConfirm.isEnabled()
                : null;
        LockPatternView.DisplayMode lastDisplayMode = mLockPatternView != null ? mLockPatternView
                .getDisplayMode() : null;
        List<Cell> lastPattern = mLockPatternView != null ? mLockPatternView
                .getPattern() : null;

        setContentView(R.layout.alp_lock_pattern_activity);
        UI.adjustDialogSizeForLargeScreen(getWindow());

        mTxtInfo = (TextView) findViewById(R.id.alp_info);
        mLockPatternView = (LockPatternView) findViewById(R.id.alp_lock_pattern);

        mFooter = findViewById(R.id.alp_footer);
        mBtnCancel = (Button) findViewById(R.id.alp_cancel);
        mBtnConfirm = (Button) findViewById(R.id.alp_confirm);

        /*
         * LOCK PATTERN VIEW
         */

        if (getResources().getBoolean(R.bool.alp_is_large_screen)
                && !getWindow().isFloating()) {
            int size = getResources().getDimensionPixelSize(
                    R.dimen.alp_lockpatternview_size);
            LayoutParams lp = mLockPatternView.getLayoutParams();
            lp.width = size;
            lp.height = size;
            mLockPatternView.setLayoutParams(lp);
        }

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

            if (infoText != null)
                mTxtInfo.setText(infoText);
            else
                mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);

            /*
             * BUTTON OK
             */
            if (mBtnOkCmd == null)
                mBtnOkCmd = ButtonOkCommand.Continue;
            switch (mBtnOkCmd) {
            case Continue:
                mBtnConfirm.setText(R.string.alp_cmd_continue);
                break;
            case Done:
                mBtnConfirm.setText(R.string.alp_cmd_confirm);
                break;
            }
            if (btnOkEnabled != null)
                mBtnConfirm.setEnabled(btnOkEnabled);
        }// _ActionCreatePattern
        else if (_ActionComparePattern.equals(getIntent().getAction())) {
            mFooter.setVisibility(View.GONE);

            if (infoText != null)
                mTxtInfo.setText(infoText);
            else
                mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
        }// _ActionComparePattern
    }// initContentView()

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
            mIntentResult.putExtra(_ExtraRetryCount, mRetryCount);

            if (mRetryCount >= mMaxRetry)
                finishWithNegativeResult(_ResultFailed);
            else {
                mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                mTxtInfo.setText(R.string.alp_msg_try_again);
            }
        }
    }// doComparePattern()

    private List<Cell> mLastPattern;

    private void doCreatePattern(List<Cell> pattern) {
        if (pattern.size() < mMinWiredDots) {
            mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            mTxtInfo.setText(getString(R.string.alp_pmsg_connect_x_dots,
                    mMinWiredDots));
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
        if (_ActionCreatePattern.equals(getIntent().getAction()))
            mIntentResult.putExtra(_Pattern, pattern);
        else {
            /*
             * If the user was "logging in", minimum try count can not be zero.
             */
            mIntentResult.putExtra(_ExtraRetryCount, mRetryCount + 1);
        }

        setResult(RESULT_OK, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                _ResultReceiver);
        if (receiver != null) {
            Bundle bundle = new Bundle();
            if (_ActionCreatePattern.equals(getIntent().getAction()))
                bundle.putString(_Pattern, pattern);
            else {
                /*
                 * If the user was "logging in", minimum try count can not be
                 * zero.
                 */
                bundle.putInt(_ExtraRetryCount, mRetryCount + 1);
            }
            receiver.send(RESULT_OK, bundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(_OkPendingIntent);
        if (pi != null) {
            try {
                pi.send(this, RESULT_OK, mIntentResult);
            } catch (Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.e(_ClassName, "Error sending PendingIntent: " + pi);
                    Log.e(_ClassName, ">>> " + t);
                    t.printStackTrace();
                }
            }
        }

        finish();
    }// finishWithResultOk()

    /**
     * Finishes the activity with negative result (
     * {@link Activity#RESULT_CANCELED} or {@link #_ResultFailed}).
     */
    private void finishWithNegativeResult(int resultCode) {
        if (_ActionComparePattern.equals(getIntent().getAction()))
            mIntentResult.putExtra(_ExtraRetryCount, mRetryCount);

        setResult(resultCode, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                _ResultReceiver);
        if (receiver != null) {
            Bundle resultBundle = null;
            if (_ActionComparePattern.equals(getIntent().getAction())) {
                resultBundle = new Bundle();
                resultBundle.putInt(_ExtraRetryCount, mRetryCount);
            }
            receiver.send(resultCode, resultBundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(
                _CancelledPendingIntent);
        if (pi != null) {
            try {
                pi.send(this, resultCode, mIntentResult);
            } catch (Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.e(_ClassName, "Error sending PendingIntent: " + pi);
                    Log.e(_ClassName, ">>> " + t);
                    t.printStackTrace();
                }
            }
        }

        finish();
    }// finishWithNegativeResult()

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
                if (mBtnOkCmd == ButtonOkCommand.Continue)
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
                if (mBtnOkCmd == ButtonOkCommand.Continue) {
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
            finishWithNegativeResult(RESULT_CANCELED);
        }// onClick()
    };// mBtnCancelOnClickListener

    private final View.OnClickListener mBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mBtnOkCmd == ButtonOkCommand.Continue) {
                mBtnOkCmd = ButtonOkCommand.Done;
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
