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

import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import group.pals.android.lib.ui.lockpattern.util.IEncrypter;
import group.pals.android.lib.ui.lockpattern.util.InvalidEncrypterException;
import group.pals.android.lib.ui.lockpattern.util.UI;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.Cell;
import group.pals.android.lib.ui.lockpattern.widget.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
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
 * You must use either {@link #ACTION_CREATE_PATTERN} or
 * {@link #ACTION_COMPARE_PATTERN}. Otherwise an
 * {@link UnsupportedOperationException} will be thrown.
 * </p>
 * <p>
 * You can deliver result to {@link PendingIntent}s and/ or
 * {@link ResultReceiver} too. See {@link #EXTRA_OK_PENDING_INTENT},
 * {@link #EXTRA_CANCELLED_PENDING_INTENT} and {@link #EXTRA_RESULT_RECEIVER}
 * for more details.
 * </p>
 * <p>
 * <strong>NOTES</strong>
 * <li>You must use one of the themes that this library provides. They start
 * with {@code R.style.Alp_Theme_*}. The reason is the themes contain resources
 * that the library needs.</li>
 * <li>In mode comparing pattern, there are <strong><i>3 possible result
 * codes</i></strong>: {@link Activity#RESULT_OK},
 * {@link Activity#RESULT_CANCELED} and {@link #RESULT_FAILED}.</li>
 * </p>
 * 
 * @author Hai Bison
 * @since v1.0
 */
public class LockPatternActivity extends Activity {

    private static final String CLASS_NAME = LockPatternActivity.class
            .getName();

    /**
     * Use this action to create new pattern. You can provide an
     * {@link IEncrypter} with {@link #_EncrypterClass} to improve security.
     * <p>
     * If the use created a pattern, {@link Activity#RESULT_OK} returns with the
     * pattern ({@link #EXTRA_PATTERN}). Otherwise
     * {@link Activity#RESULT_CANCELED} returns.
     * </p>
     * 
     * @since v2.4 beta
     * @see #_EncrypterClass
     * @see #EXTRA_OK_PENDING_INTENT
     * @see #EXTRA_CANCELLED_PENDING_INTENT
     */
    public static final String ACTION_CREATE_PATTERN = CLASS_NAME
            + ".create_pattern";

    /**
     * Use this action to compare pattern. You provide the pattern to be
     * compared with {@link #EXTRA_PATTERN}.
     * <p>
     * If the user passes, {@link Activity#RESULT_OK} returns. If not,
     * {@link #RESULT_FAILED} returns.
     * </p>
     * <p>
     * If the user cancels the task, {@link Activity#RESULT_CANCELED} returns.
     * </p>
     * <p>
     * In any case, there will be key {@link #EXTRA_RETRY_COUNT} available in
     * the intent result.
     * </p>
     * 
     * @since v2.4 beta
     * @see #EXTRA_PATTERN
     * @see #_EncrypterClass
     * @see #EXTRA_OK_PENDING_INTENT
     * @see #EXTRA_CANCELLED_PENDING_INTENT
     * @see #RESULT_FAILED
     * @see #EXTRA_RETRY_COUNT
     */
    public static final String ACTION_COMPARE_PATTERN = CLASS_NAME
            + ".compare_pattern";

    /**
     * If you use {@link #ACTION_COMPARE_PATTERN} and the user fails to "login"
     * after a number of tries, this activity will finish with this result code.
     * 
     * @see #ACTION_COMPARE_PATTERN
     * @see #_MaxRetry
     * @see #EXTRA_RETRY_COUNT
     */
    public static final int RESULT_FAILED = RESULT_FIRST_USER + 1;

    /**
     * If you use {@link #ACTION_COMPARE_PATTERN}, and the user fails to "login"
     * after a number of tries, this key holds that number.
     * 
     * @see #ACTION_COMPARE_PATTERN
     * @see #_MaxRetry
     */
    public static final String EXTRA_RETRY_COUNT = CLASS_NAME + ".retry_count";

    /**
     * Sets value of this key to a theme in {@code R.style.Alp_Theme_*}. Default
     * is the one you set in AndroidManifest.xml.
     * 
     * @since v1.5.3 beta
     */
    public static final String EXTRA_THEME = CLASS_NAME + ".theme";

    /**
     * Key to hold the pattern. It must be a char array.
     * 
     * @since v2 beta
     */
    public static final String EXTRA_PATTERN = CLASS_NAME + ".pattern";

    /**
     * You can provide an {@link ResultReceiver} with this key. The activity
     * will notify your receiver the same result code and intent data as you
     * will receive them in {@link #onActivityResult(int, int, Intent)}.
     * 
     * @since v2.4 beta
     */
    public static final String EXTRA_RESULT_RECEIVER = CLASS_NAME
            + ".result_receiver";

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link Activity#RESULT_OK} will be returning. If you were calling this
     * activity with {@link #ACTION_CREATE_PATTERN}, key {@link #EXTRA_PATTERN}
     * will be attached to the original intent which the pending intent holds.
     */
    public static final String EXTRA_OK_PENDING_INTENT = CLASS_NAME
            + ".ok_pending_intent";

    /**
     * Put a {@link PendingIntent} into this key. It will be sent before
     * {@link Activity#RESULT_CANCELED} will be returning.
     */
    public static final String EXTRA_CANCELLED_PENDING_INTENT = CLASS_NAME
            + ".cancelled_pending_intent";

    /**
     * Helper enum for button OK commands. (Because we use only one "OK" button
     * for different commands).
     * 
     * @author Hai Bison
     * 
     */
    private static enum ButtonOkCommand {
        CONTINUE, DONE
    }// ButtonOkCommand

    /*
     * FIELDS
     */
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
            Log.d(CLASS_NAME, "ClassName = " + CLASS_NAME);

        /*
         * EXTRA_THEME
         */

        if (getIntent().hasExtra(EXTRA_THEME))
            setTheme(getIntent().getIntExtra(EXTRA_THEME,
                    R.style.Alp_Theme_Dark));

        super.onCreate(savedInstanceState);

        if (!ACTION_CREATE_PATTERN.equals(getIntent().getAction())
                && !ACTION_COMPARE_PATTERN.equals(getIntent().getAction()))
            throw new UnsupportedOperationException("Unknown Action >> "
                    + getIntent().getAction());

        mMinWiredDots = DisplayPrefs.getMinWiredDots(this);
        mMaxRetry = DisplayPrefs.getMaxRetry(this);
        mAutoSave = SecurityPrefs.isAutoSavePattern(this);

        /*
         * Encrypter.
         */
        char[] encrypterClass = SecurityPrefs.getEncrypterClass(this);
        if (encrypterClass != null) {
            try {
                mEncrypter = (IEncrypter) Class.forName(
                        new String(encrypterClass), false, getClassLoader())
                        .newInstance();
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
        Log.d(CLASS_NAME, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        initContentView();
    }// onConfigurationChanged()

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
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
         * LOCK EXTRA_PATTERN VIEW
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

        mLockPatternView.setInStealthMode(DisplayPrefs.isStealthMode(this));
        mLockPatternView.setOnPatternListener(mPatternViewListener);
        if (lastPattern != null && lastDisplayMode != null)
            mLockPatternView.setPattern(lastDisplayMode, lastPattern);

        /*
         * COMMAND BUTTONS
         */

        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
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
                mBtnOkCmd = ButtonOkCommand.CONTINUE;
            switch (mBtnOkCmd) {
            case CONTINUE:
                mBtnConfirm.setText(R.string.alp_cmd_continue);
                break;
            case DONE:
                mBtnConfirm.setText(R.string.alp_cmd_confirm);
                break;
            }
            if (btnOkEnabled != null)
                mBtnConfirm.setEnabled(btnOkEnabled);
        }// ACTION_CREATE_PATTERN
        else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
            mFooter.setVisibility(View.GONE);

            if (infoText != null)
                mTxtInfo.setText(infoText);
            else
                mTxtInfo.setText(R.string.alp_msg_draw_pattern_to_unlock);
        }// ACTION_COMPARE_PATTERN
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
    private char[] encodePattern(List<Cell> pattern) {
        if (mEncrypter == null) {
            return LockPatternUtils.patternToSha1(pattern).toCharArray();
        } else {
            try {
                return mEncrypter.encrypt(this,
                        LockPatternUtils.patternToSha1(pattern).toCharArray());
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

        char[] currentPattern = getIntent().getCharArrayExtra(EXTRA_PATTERN);
        if (currentPattern == null)
            currentPattern = SecurityPrefs.getPattern(this);

        if (Arrays.equals(encodePattern(pattern), currentPattern))
            finishWithResultOk(null);
        else {
            mRetryCount++;
            mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount);

            if (mRetryCount >= mMaxRetry)
                finishWithNegativeResult(RESULT_FAILED);
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
            if (Arrays.equals(encodePattern(mLastPattern),
                    encodePattern(pattern))) {
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
     *            the pattern, if this is in mode creating pattern. In any
     *            cases, it can be set to {@code null}.
     */
    private void finishWithResultOk(char[] pattern) {
        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction()))
            mIntentResult.putExtra(EXTRA_PATTERN, pattern);
        else {
            /*
             * If the user was "logging in", minimum try count can not be zero.
             */
            mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount + 1);
        }

        setResult(RESULT_OK, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            Bundle bundle = new Bundle();
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction()))
                bundle.putCharArray(EXTRA_PATTERN, pattern);
            else {
                /*
                 * If the user was "logging in", minimum try count can not be
                 * zero.
                 */
                bundle.putInt(EXTRA_RETRY_COUNT, mRetryCount + 1);
            }
            receiver.send(RESULT_OK, bundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(
                EXTRA_OK_PENDING_INTENT);
        if (pi != null) {
            try {
                pi.send(this, RESULT_OK, mIntentResult);
            } catch (Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.e(CLASS_NAME, "Error sending PendingIntent: " + pi);
                    Log.e(CLASS_NAME, ">>> " + t);
                    t.printStackTrace();
                }
            }
        }

        finish();
    }// finishWithResultOk()

    /**
     * Finishes the activity with negative result (
     * {@link Activity#RESULT_CANCELED} or {@link #RESULT_FAILED}).
     */
    private void finishWithNegativeResult(int resultCode) {
        if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction()))
            mIntentResult.putExtra(EXTRA_RETRY_COUNT, mRetryCount);

        setResult(resultCode, mIntentResult);

        /*
         * ResultReceiver
         */
        ResultReceiver receiver = getIntent().getParcelableExtra(
                EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            Bundle resultBundle = null;
            if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                resultBundle = new Bundle();
                resultBundle.putInt(EXTRA_RETRY_COUNT, mRetryCount);
            }
            receiver.send(resultCode, resultBundle);
        }

        /*
         * PendingIntent
         */
        PendingIntent pi = getIntent().getParcelableExtra(
                EXTRA_CANCELLED_PENDING_INTENT);
        if (pi != null) {
            try {
                pi.send(this, resultCode, mIntentResult);
            } catch (Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.e(CLASS_NAME, "Error sending PendingIntent: " + pi);
                    Log.e(CLASS_NAME, ">>> " + t);
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

            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                mTxtInfo.setText(R.string.alp_msg_release_finger_when_done);
                mBtnConfirm.setEnabled(false);
                if (mBtnOkCmd == ButtonOkCommand.CONTINUE)
                    mLastPattern = null;
            }
        }// onPatternStart()

        @Override
        public void onPatternDetected(List<Cell> pattern) {
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction()))
                doCreatePattern(pattern);
            else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction()))
                doComparePattern(pattern);
        }// onPatternDetected()

        @Override
        public void onPatternCleared() {
            mLockPatternView.setDisplayMode(DisplayMode.Correct);

            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                mBtnConfirm.setEnabled(false);
                if (mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    mLastPattern = null;
                    mTxtInfo.setText(R.string.alp_msg_draw_an_unlock_pattern);
                } else
                    mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
            } else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
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
            if (mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                mBtnOkCmd = ButtonOkCommand.DONE;
                mLockPatternView.clearPattern();
                mTxtInfo.setText(R.string.alp_msg_redraw_pattern_to_confirm);
                mBtnConfirm.setText(R.string.alp_cmd_confirm);
                mBtnConfirm.setEnabled(false);
            } else {
                final char[] pattern = encodePattern(mLastPattern);
                if (mAutoSave)
                    SecurityPrefs.setPattern(LockPatternActivity.this, pattern);
                finishWithResultOk(pattern);
            }
        }// onClick()
    };// mBtnConfirmOnClickListener
}
