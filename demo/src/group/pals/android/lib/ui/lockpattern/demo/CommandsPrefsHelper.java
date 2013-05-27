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
import group.pals.android.lib.ui.lockpattern.util.UI;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.Preference;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Helper class for {@link PrefsActivity} and {@link PrefsActivity_v11}.
 * 
 * @author Hai Bison
 * 
 */
public class CommandsPrefsHelper {

    public static final int REQ_CREATE_PATTERN = 0;
    public static final int REQ_ENTER_PATTERN = 1;

    /**
     * Initializes handler for commands.
     * 
     * @param activity
     *            the activity.
     * @param holder
     *            instance of {@link PreferenceHolder}.
     */
    public static void init(final Activity activity, PreferenceHolder holder) {
        /*
         * COMMAND "CREATE LOCK PATTERN"
         */
        holder.findPreference(
                activity.getString(R.string.pkey_create_lock_pattern))
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {

                            @Override
                            public boolean onPreferenceClick(
                                    Preference preference) {
                                Intent intentActivity = new Intent(
                                        LockPatternActivity.ACTION_CREATE_PATTERN,
                                        null, activity,
                                        LockPatternActivity.class);
                                intentActivity
                                        .putExtra(
                                                LockPatternActivity.EXTRA_THEME,
                                                getThemeForLockPatternActivity(activity));
                                activity.startActivityForResult(intentActivity,
                                        REQ_CREATE_PATTERN);

                                return true;
                            }// onPreferenceClick()
                        });

        /*
         * COMMAND "ENTER LOCK PATTERN"
         */
        holder.findPreference(
                activity.getString(R.string.pkey_enter_lock_pattern))
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {

                            @Override
                            public boolean onPreferenceClick(
                                    Preference preference) {
                                Intent intentActivity = new Intent(
                                        LockPatternActivity.ACTION_COMPARE_PATTERN,
                                        null, activity,
                                        LockPatternActivity.class);
                                intentActivity
                                        .putExtra(
                                                LockPatternActivity.EXTRA_THEME,
                                                getThemeForLockPatternActivity(activity));
                                activity.startActivityForResult(intentActivity,
                                        REQ_ENTER_PATTERN);

                                return true;
                            }// onPreferenceClick()
                        });

        /*
         * COMMAND "ABOUT..."
         */
        holder.findPreference(activity.getString(R.string.pkey_about))
                .setOnPreferenceClickListener(
                        new Preference.OnPreferenceClickListener() {

                            @Override
                            public boolean onPreferenceClick(
                                    Preference preference) {
                                /*
                                 * Build the dialog.
                                 */
                                Dialog dialog = new Dialog(activity, UI
                                        .resolveAttribute(activity,
                                                R.attr.alp_theme_dialog));
                                dialog.setTitle(R.string.about);
                                dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                                dialog.setCanceledOnTouchOutside(true);

                                dialog.setContentView(R.layout.dialog_about);

                                dialog.setFeatureDrawableResource(
                                        Window.FEATURE_LEFT_ICON,
                                        android.R.drawable.ic_dialog_info);

                                /*
                                 * Set text...
                                 */
                                TextView textInfo = (TextView) dialog
                                        .findViewById(R.id.textview_info);
                                textInfo.setText(activity.getString(
                                        R.string.pmsg_about_info,
                                        activity.getString(R.string.alp_lib_name),
                                        activity.getString(R.string.alp_lib_version_name)));
                                textInfo.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            activity.startActivity(new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("http://www.haibison.com")));
                                        } catch (Throwable t) {
                                            /*
                                             * Ignore it.
                                             */
                                        }
                                    }// onClick()
                                });

                                /*
                                 * Show the dialog.
                                 */
                                dialog.show();

                                return true;
                            }// onPreferenceClick()
                        });
    }// init()

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
