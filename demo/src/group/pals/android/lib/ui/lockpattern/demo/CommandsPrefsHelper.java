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
import group.pals.android.lib.ui.lockpattern.prefs.Prefs;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * Helper class for {@link PrefsActivity} and {@link PrefsActivity_v11}.
 * 
 * @author Hai Bison
 * 
 */
public class CommandsPrefsHelper {

    public static final int REQ_CREATE_PATTERN = 0;
    public static final int REQ_ENTER_PATTERN = 1;
    public static final int REQ_VERIFY_CAPTCHA = 2;

    private final Activity mActivity;
    private final PreferenceHolder mPreferenceHolder;

    /**
     * Creates new instance.
     * 
     * @param activity
     *            the activity, which will be used to start
     *            {@link LockPatternActivity}.
     * @param holder
     *            the preference holder.
     */
    public CommandsPrefsHelper(Activity activity, PreferenceHolder holder) {
        mActivity = activity;
        mPreferenceHolder = holder;
    }// CommandsPrefsHelper()

    /**
     * Initializes handler for commands.
     */
    public void init() {
        mPreferenceHolder.findPreference(
                mActivity.getString(R.string.pkey_create_pattern))
                .setOnPreferenceClickListener(mCmdCreatePatternListener);
        mPreferenceHolder.findPreference(
                mActivity.getString(R.string.pkey_enter_pattern))
                .setOnPreferenceClickListener(mCmdEnterPatternListener);
        mPreferenceHolder.findPreference(
                mActivity.getString(R.string.pkey_verify_captcha))
                .setOnPreferenceClickListener(mCmdVerifyCaptchaListener);
    }// init()

    /*
     * LISTENERS
     */

    private final Preference.OnPreferenceClickListener mCmdCreatePatternListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intentActivity = new Intent(
                    LockPatternActivity.ACTION_CREATE_PATTERN, null, mActivity,
                    LockPatternActivity.class);
            intentActivity.putExtra(LockPatternActivity.EXTRA_THEME,
                    getThemeForLockPatternActivity(mActivity));
            mActivity
                    .startActivityForResult(intentActivity, REQ_CREATE_PATTERN);

            return true;
        }// onPreferenceClick()
    };// mCmdCreatePatternListener

    private final Preference.OnPreferenceClickListener mCmdEnterPatternListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intentActivity = new Intent(
                    LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                    mActivity, LockPatternActivity.class);
            intentActivity.putExtra(LockPatternActivity.EXTRA_THEME,
                    getThemeForLockPatternActivity(mActivity));
            mActivity.startActivityForResult(intentActivity, REQ_ENTER_PATTERN);

            return true;
        }// onPreferenceClick()
    };// mCmdEnterPatternListener

    private final Preference.OnPreferenceClickListener mCmdVerifyCaptchaListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intentActivity = new Intent(
                    LockPatternActivity.ACTION_VERIFY_CAPTCHA, null, mActivity,
                    LockPatternActivity.class);
            mActivity
                    .startActivityForResult(intentActivity, REQ_VERIFY_CAPTCHA);

            return true;
        }// onPreferenceClick()
    };// mCmdVerifyCaptchaListener

    /*
     * UTILITIES
     */

    /**
     * Gets the theme that the user chose to apply to
     * {@link LockPatternActivity}.
     * 
     * @param context
     *            the context.
     * @return the theme for {@link LockPatternActivity}.
     */
    @SuppressLint("InlinedApi")
    public static int getThemeForLockPatternActivity(Context context) {
        SharedPreferences p = context.getSharedPreferences(
                Prefs.genPreferenceFilename(context),
                Context.MODE_MULTI_PROCESS);
        boolean useLightTheme = p
                .getBoolean(
                        context.getString(R.string.pkey_lockpattern_activity_light_theme),
                        context.getResources()
                                .getBoolean(
                                        R.bool.pkey_lockpattern_activity_light_theme_default));
        boolean useDialogTheme = p
                .getBoolean(
                        context.getString(R.string.pkey_lockpattern_activity_dialog_theme),
                        context.getResources()
                                .getBoolean(
                                        R.bool.pkey_lockpattern_activity_dialog_theme_default));

        if (useLightTheme)
            return useDialogTheme ? R.style.Alp_Theme_Dialog_Light
                    : R.style.Alp_Theme_Light;
        return useDialogTheme ? R.style.Alp_Theme_Dialog_Dark
                : R.style.Alp_Theme_Dark;
    }// getThemeForLockPatternActivity()

}
