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

package com.haibison.android.lockpattern;

import static com.haibison.android.lockpattern.LockPatternActivity.ACTION_COMPARE_PATTERN;
import static com.haibison.android.lockpattern.LockPatternActivity.ACTION_CREATE_PATTERN;
import static com.haibison.android.lockpattern.LockPatternActivity.ACTION_VERIFY_CAPTCHA;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_PATTERN;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_PENDING_INTENT_CANCELLED;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_PENDING_INTENT_OK;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_RESULT_RECEIVER;
import static com.haibison.android.lockpattern.LockPatternActivity.EXTRA_RETRY_COUNT;
import static com.haibison.android.lockpattern.util.Settings.Display.METADATA_CAPTCHA_WIRED_DOTS;
import static com.haibison.android.lockpattern.util.Settings.Display.METADATA_MAX_RETRIES;
import static com.haibison.android.lockpattern.util.Settings.Display.METADATA_MIN_WIRED_DOTS;
import static com.haibison.android.lockpattern.util.Settings.Display.METADATA_STEALTH_MODE;
import static com.haibison.android.lockpattern.util.Settings.Security.METADATA_AUTO_SAVE_PATTERN;
import static com.haibison.android.lockpattern.util.Settings.Security.METADATA_ENCRYPTER_CLASS;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.haibison.android.lockpattern.util.IEncrypter;
import com.haibison.android.lockpattern.util.InvalidEncrypterException;
import com.haibison.android.lockpattern.util.Settings;
import com.haibison.android.lockpattern.widget.LockPatternUtils;
import com.haibison.android.lockpattern.widget.LockPatternView;
import com.haibison.android.lockpattern.widget.LockPatternView.Cell;
import com.haibison.android.lockpattern.widget.LockPatternView.DisplayMode;

/**
 * This "virtual" fragment is used in bold {@link LockPatternActivity} and
 * {@link LockPatternFragment}.
 * 
 * @author Hai Bison
 * 
 */
public class VirtualFragment {

    /**
     * Helper enum for button OK commands. (Because we use only one "OK" button
     * for different commands).
     * 
     * @author Hai Bison
     */
    private static enum ButtonOkCommand {
        CONTINUE, FORGOT_PATTERN, DONE
    }// ButtonOkCommand

    private final Context mContext;
    private final String mAction;
    private final Bundle mArguments;

    /*
     * CONTROLS
     */
    private TextView mTextInfo;
    private LockPatternView mLockPatternView;
    private View mFooter;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    /*
     * FIELDS
     */
    private int mMaxRetries, mMinWiredDots, mRetryCount = 0, mCaptchaWiredDots;
    private boolean mAutoSave, mStealthMode;
    private IEncrypter mEncrypter;
    private ButtonOkCommand mBtnOkCmd;
    private Intent mIntentResult;

    public VirtualFragment(Context context, String action, Bundle args) {
        mContext = context;
        mAction = action;
        mArguments = args;

        loadSettings();
    }// VirtualFragment()

    /**
     * Loads settings, either from manifest or {@link Settings}.
     */
    public void loadSettings() {
        Bundle metaData = null;
        try {
            metaData = mContext.getPackageManager().getActivityInfo(
                    new ComponentName(mContext, LockPatternActivity.class),
                    PackageManager.GET_META_DATA).metaData;
        } catch (NameNotFoundException e) {
            /*
             * The user might just use LockPatternFragment, not
             * LockPatternActivity.
             */
            e.printStackTrace();
        }

        if (metaData != null && metaData.containsKey(METADATA_MIN_WIRED_DOTS))
            mMinWiredDots = Settings.Display.validateMinWiredDots(mContext,
                    metaData.getInt(METADATA_MIN_WIRED_DOTS));
        else
            mMinWiredDots = Settings.Display.getMinWiredDots(mContext);

        if (metaData != null && metaData.containsKey(METADATA_MAX_RETRIES))
            mMaxRetries = Settings.Display.validateMaxRetries(mContext,
                    metaData.getInt(METADATA_MAX_RETRIES));
        else
            mMaxRetries = Settings.Display.getMaxRetries(mContext);

        if (metaData != null
                && metaData.containsKey(METADATA_AUTO_SAVE_PATTERN))
            mAutoSave = metaData.getBoolean(METADATA_AUTO_SAVE_PATTERN);
        else
            mAutoSave = Settings.Security.isAutoSavePattern(mContext);

        if (metaData != null
                && metaData.containsKey(METADATA_CAPTCHA_WIRED_DOTS))
            mCaptchaWiredDots = Settings.Display.validateCaptchaWiredDots(
                    mContext, metaData.getInt(METADATA_CAPTCHA_WIRED_DOTS));
        else
            mCaptchaWiredDots = Settings.Display.getCaptchaWiredDots(mContext);

        if (metaData != null && metaData.containsKey(METADATA_STEALTH_MODE))
            mStealthMode = metaData.getBoolean(METADATA_STEALTH_MODE);
        else
            mStealthMode = Settings.Display.isStealthMode(mContext);

        /*
         * Encrypter.
         */
        char[] encrypterClass;
        if (metaData != null && metaData.containsKey(METADATA_ENCRYPTER_CLASS))
            encrypterClass = metaData.getString(METADATA_ENCRYPTER_CLASS)
                    .toCharArray();
        else
            encrypterClass = Settings.Security.getEncrypterClass(mContext);

        if (encrypterClass != null) {
            try {
                mEncrypter = (IEncrypter) Class.forName(
                        new String(encrypterClass), false,
                        mContext.getClassLoader()).newInstance();
            } catch (Throwable t) {
                throw new InvalidEncrypterException();
            }
        }
    }// loadSettings()

    public void initContentView(IContentView container) {
        /*
         * Save all controls' state to restore later.
         */
        CharSequence infoText = mTextInfo != null ? mTextInfo.getText() : null;
        Boolean btnOkEnabled = mBtnConfirm != null ? mBtnConfirm.isEnabled()
                : null;
        LockPatternView.DisplayMode lastDisplayMode = mLockPatternView != null ? mLockPatternView
                .getDisplayMode() : null;
        List<Cell> lastPattern = mLockPatternView != null ? mLockPatternView
                .getPattern() : null;

        mTextInfo = (TextView) container
                .findViewById(R.id.alp_42447968_textview_info);
        mLockPatternView = (LockPatternView) container
                .findViewById(R.id.alp_42447968_view_lock_pattern);

        mFooter = container.findViewById(R.id.alp_42447968_viewgroup_footer);
        mBtnCancel = (Button) container
                .findViewById(R.id.alp_42447968_button_cancel);
        mBtnConfirm = (Button) container
                .findViewById(R.id.alp_42447968_button_confirm);

        /*
         * LOCK PATTERN VIEW
         */

        switch (mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) {
        case Configuration.SCREENLAYOUT_SIZE_LARGE:
        case Configuration.SCREENLAYOUT_SIZE_XLARGE: {
            final int size = mContext.getResources().getDimensionPixelSize(
                    R.dimen.alp_42447968_lockpatternview_size);
            LayoutParams lp = mLockPatternView.getLayoutParams();
            lp.width = size;
            lp.height = size;
            mLockPatternView.setLayoutParams(lp);

            break;
        }// LARGE / XLARGE
        }

        /*
         * Haptic feedback.
         */
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = android.provider.Settings.System
                    .getInt(mContext.getContentResolver(),
                            android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED,
                            0) != 0;
        } catch (Throwable t) {
            /*
             * Ignore it.
             */
        }
        mLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);

        mLockPatternView.setInStealthMode(mStealthMode
                && !ACTION_VERIFY_CAPTCHA.equals(mAction));
        // mLockPatternView.setOnPatternListener(mLockPatternViewListener);
        if (lastPattern != null && lastDisplayMode != null
                && !ACTION_VERIFY_CAPTCHA.equals(mAction))
            mLockPatternView.setPattern(lastDisplayMode, lastPattern);

        /*
         * COMMAND BUTTONS
         */

        if (ACTION_CREATE_PATTERN.equals(mAction)) {
            // mBtnCancel.setOnClickListener(mBtnCancelOnClickListener);
            mBtnConfirm.setOnClickListener(mBtnConfirmOnClickListener);

            mBtnCancel.setVisibility(View.VISIBLE);
            mFooter.setVisibility(View.VISIBLE);

            if (infoText != null)
                mTextInfo.setText(infoText);
            else
                mTextInfo
                        .setText(R.string.alp_42447968_msg_draw_an_unlock_pattern);

            /*
             * BUTTON OK
             */
            if (mBtnOkCmd == null)
                mBtnOkCmd = ButtonOkCommand.CONTINUE;
            switch (mBtnOkCmd) {
            case CONTINUE:
                mBtnConfirm.setText(R.string.alp_42447968_cmd_continue);
                break;
            case DONE:
                mBtnConfirm.setText(R.string.alp_42447968_cmd_confirm);
                break;
            default:
                /*
                 * Do nothing.
                 */
                break;
            }
            if (btnOkEnabled != null)
                mBtnConfirm.setEnabled(btnOkEnabled);
        }// ACTION_CREATE_PATTERN
        else if (ACTION_COMPARE_PATTERN.equals(mAction)) {
            if (TextUtils.isEmpty(infoText))
                mTextInfo
                        .setText(R.string.alp_42447968_msg_draw_pattern_to_unlock);
            else
                mTextInfo.setText(infoText);
            if (mArguments.containsKey(EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN)) {
                mBtnConfirm.setOnClickListener(mBtnConfirmOnClickListener);
                mBtnConfirm.setText(R.string.alp_42447968_cmd_forgot_pattern);
                mBtnConfirm.setEnabled(true);
                mFooter.setVisibility(View.VISIBLE);
            }
        }// ACTION_COMPARE_PATTERN
        else if (ACTION_VERIFY_CAPTCHA.equals(mAction)) {
            mTextInfo
                    .setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);

            /*
             * NOTE: EXTRA_PATTERN should hold a char[] array. In this case we
             * use it as a temporary variable to hold a list of Cell.
             */

            final ArrayList<Cell> pattern;
            if (mArguments.containsKey(EXTRA_PATTERN))
                pattern = mArguments.getParcelableArrayList(EXTRA_PATTERN);
            else
                mArguments.putParcelableArrayList(
                        EXTRA_PATTERN,
                        pattern = LockPatternUtils
                                .genCaptchaPattern(mCaptchaWiredDots));

            mLockPatternView.setPattern(DisplayMode.Animate, pattern);
        }// ACTION_VERIFY_CAPTCHA
    }// initContentView()

    /*
     * LISTENERS
     */
    /*
     * private final LockPatternView.OnPatternListener mLockPatternViewListener
     * = new LockPatternView.OnPatternListener() {
     * 
     * @Override public void onPatternStart() {
     * mLockPatternView.removeCallbacks(mLockPatternViewReloader);
     * mLockPatternView.setDisplayMode(DisplayMode.Correct);
     * 
     * if (ACTION_CREATE_PATTERN.equals(mAction)) { mTextInfo
     * .setText(R.string.alp_42447968_msg_release_finger_when_done);
     * mBtnConfirm.setEnabled(false); if (mBtnOkCmd == ButtonOkCommand.CONTINUE)
     * getIntent().removeExtra(EXTRA_PATTERN); }// ACTION_CREATE_PATTERN else if
     * (ACTION_COMPARE_PATTERN.equals(mAction)) { mTextInfo
     * .setText(R.string.alp_42447968_msg_draw_pattern_to_unlock); }//
     * ACTION_COMPARE_PATTERN else if (ACTION_VERIFY_CAPTCHA.equals(mAction)) {
     * mTextInfo .setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
     * }// ACTION_VERIFY_CAPTCHA }// onPatternStart()
     * 
     * @Override public void onPatternDetected(List<Cell> pattern) { if
     * (ACTION_CREATE_PATTERN.equals(mAction)) {
     * doCheckAndCreatePattern(pattern); }// ACTION_CREATE_PATTERN else if
     * (ACTION_COMPARE_PATTERN.equals(mAction)) { doComparePattern(pattern); }//
     * ACTION_COMPARE_PATTERN else if (ACTION_VERIFY_CAPTCHA.equals(mAction)) {
     * if (!DisplayMode.Animate.equals(mLockPatternView .getDisplayMode()))
     * doComparePattern(pattern); }// ACTION_VERIFY_CAPTCHA }//
     * onPatternDetected()
     * 
     * @Override public void onPatternCleared() {
     * mLockPatternView.removeCallbacks(mLockPatternViewReloader);
     * 
     * if (ACTION_CREATE_PATTERN.equals(mAction)) {
     * mLockPatternView.setDisplayMode(DisplayMode.Correct);
     * mBtnConfirm.setEnabled(false); if (mBtnOkCmd == ButtonOkCommand.CONTINUE)
     * { getIntent().removeExtra(EXTRA_PATTERN); mTextInfo
     * .setText(R.string.alp_42447968_msg_draw_an_unlock_pattern); } else
     * mTextInfo .setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
     * }// ACTION_CREATE_PATTERN else if
     * (ACTION_COMPARE_PATTERN.equals(mAction)) {
     * mLockPatternView.setDisplayMode(DisplayMode.Correct); mTextInfo
     * .setText(R.string.alp_42447968_msg_draw_pattern_to_unlock); }//
     * ACTION_COMPARE_PATTERN else if (ACTION_VERIFY_CAPTCHA.equals(mAction)) {
     * mTextInfo .setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
     * List<Cell> pattern = getIntent().getParcelableArrayListExtra(
     * EXTRA_PATTERN); mLockPatternView.setPattern(DisplayMode.Animate,
     * pattern); }// ACTION_VERIFY_CAPTCHA }// onPatternCleared()
     * 
     * @Override public void onPatternCellAdded(List<Cell> pattern) { // TODO
     * Auto-generated method stub }// onPatternCellAdded() };//
     * mLockPatternViewListener
     *//*
        * private final View.OnClickListener mBtnCancelOnClickListener = new
        * View.OnClickListener() {
        * 
        * @Override public void onClick(View v) {
        * finishWithNegativeResult(RESULT_CANCELED); }// onClick() };//
        * mBtnCancelOnClickListener
        */
    private final View.OnClickListener mBtnConfirmOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (ACTION_CREATE_PATTERN.equals(mAction)) {/*
                                                         * if (mBtnOkCmd ==
                                                         * ButtonOkCommand
                                                         * .CONTINUE) {
                                                         * mBtnOkCmd =
                                                         * ButtonOkCommand.DONE;
                                                         * mLockPatternView
                                                         * .clearPattern();
                                                         * mTextInfo
                                                         * .setText(R.string.
                                                         * alp_42447968_msg_redraw_pattern_to_confirm
                                                         * );
                                                         * mBtnConfirm.setText
                                                         * (R.string.
                                                         * alp_42447968_cmd_confirm
                                                         * );
                                                         * mBtnConfirm.setEnabled
                                                         * (false); } else {
                                                         * final char[] pattern
                                                         * = getIntent().
                                                         * getCharArrayExtra(
                                                         * EXTRA_PATTERN); if
                                                         * (mAutoSave)
                                                         * Settings.Security
                                                         * .setPattern
                                                         * (LockPatternActivity
                                                         * .this, pattern);
                                                         * finishWithResultOk
                                                         * (pattern); }
                                                         */
            }// ACTION_CREATE_PATTERN
            else if (ACTION_COMPARE_PATTERN.equals(mAction)) {
                /*
                 * We don't need to verify the extra. First, this button is only
                 * visible if there is this extra in the intent. Second, it is
                 * the responsibility of the caller to make sure the extra is
                 * good.
                 *//*
                    * PendingIntent pi = null; try { pi =
                    * getIntent().getParcelableExtra(
                    * EXTRA_PENDING_INTENT_FORGOT_PATTERN); pi.send(); } catch
                    * (Throwable t) { Log.e(CLASSNAME,
                    * "Error sending pending intent: " + pi, t); }
                    * finishWithNegativeResult(RESULT_FORGOT_PATTERN);
                    */
            }// ACTION_COMPARE_PATTERN
        }// onClick()
    };// mBtnConfirmOnClickListener

    /**
     * This reloads the {@link #mLockPatternView} after a wrong pattern.
     */
    private final Runnable mLockPatternViewReloader = new Runnable() {

        @Override
        public void run() {
            mLockPatternView.clearPattern();
            // mLockPatternViewListener.onPatternCleared();
        }// run()
    };// mLockPatternViewReloader

}
