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

import group.pals.android.lib.ui.lockpattern.util.UI;

import java.io.InputStream;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

/**
 * Helper class for {@link PrefsActivity} and {@link PrefsActivity_v11}.
 * 
 * @author Hai Bison
 * 
 */
public class InfosPrefsHelper {

    /**
     * Used for debugging...
     */
    private static final String CLASSNAME = InfosPrefsHelper.class.getName();

    private final Context mContext;
    private final PreferenceHolder mPreferenceHolder;

    /**
     * Creates new instace.
     * 
     * @param context
     *            the context.
     * @param holder
     *            the preference holder.
     */
    public InfosPrefsHelper(Context context, PreferenceHolder holder) {
        mContext = context;
        mPreferenceHolder = holder;
    }// InfosPrefsHelper()

    /**
     * Initializes handler for commands.
     */
    public void init() {
        mPreferenceHolder.findPreference(
                mContext.getString(R.string.pkey_licenses_android_lockpattern))
                .setOnPreferenceClickListener(mCmdShowAlpLicense);
        mPreferenceHolder.findPreference(
                mContext.getString(R.string.pkey_about))
                .setOnPreferenceClickListener(mCmdAboutListener);
    }// init()

    /*
     * LISTENERS
     */

    private final Preference.OnPreferenceClickListener mCmdShowAlpLicense = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            /*
             * Build the dialog.
             */
            final Dialog dialog = new Dialog(mContext, UI.resolveAttribute(
                    mContext, R.attr.alp_theme_dialog));

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.dialog_about);

            UI.adjustDialogSizeForLargeScreen(dialog);

            final ViewTreeObserver.OnGlobalLayoutListener dialogOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

                int mOrientation = mContext.getResources().getConfiguration().orientation;

                @Override
                public void onGlobalLayout() {
                    if (BuildConfig.DEBUG)
                        Log.d(CLASSNAME, "ViewTreeObserver >> onGlobalLayout()");
                    if (mContext.getResources().getConfiguration().orientation != mOrientation) {
                        mOrientation = mContext.getResources()
                                .getConfiguration().orientation;
                        UI.adjustDialogSizeForLargeScreen(dialog);
                    }
                }// onGlobalLayout()
            };
            dialog.getWindow().getDecorView().getViewTreeObserver()
                    .addOnGlobalLayoutListener(dialogOnGlobalLayoutListener);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                        dialog.getWindow()
                                .getDecorView()
                                .getViewTreeObserver()
                                .removeGlobalOnLayoutListener(
                                        dialogOnGlobalLayoutListener);
                    else
                        ViewTreeObserverCompat_v16
                                .removeOnGlobalLayoutListener(dialog
                                        .getWindow().getDecorView()
                                        .getViewTreeObserver(),
                                        dialogOnGlobalLayoutListener);
                }// onDismiss()
            });

            /*
             * Load license...
             */
            StringBuilder license = new StringBuilder();
            InputStream inputStream = mContext.getResources().openRawResource(
                    R.raw.android_lockpattern_license);
            try {
                try {
                    int read;
                    byte[] buf = new byte[1024];
                    while ((read = inputStream.read(buf)) > 0)
                        license.append(new String(buf, 0, read));
                } finally {
                    inputStream.close();
                }
            } catch (Throwable t) {
                /*
                 * Ignore it.
                 */
            }

            TextView textInfo = (TextView) dialog
                    .findViewById(R.id.textview_info);
            textInfo.setText(license);

            /*
             * Show the dialog.
             */
            dialog.show();

            return true;
        }// onPreferenceClick()
    };// mCmdShowAlpLicense

    private final Preference.OnPreferenceClickListener mCmdAboutListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            /*
             * Build the dialog.
             */
            Dialog dialog = new Dialog(mContext, UI.resolveAttribute(mContext,
                    R.attr.alp_theme_dialog));
            dialog.setTitle(R.string.about);
            dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
            dialog.setCanceledOnTouchOutside(true);

            dialog.setContentView(R.layout.dialog_about);

            dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                    android.R.drawable.ic_dialog_info);

            /*
             * Set text...
             */
            TextView textInfo = (TextView) dialog
                    .findViewById(R.id.textview_info);
            textInfo.setText(mContext.getString(R.string.pmsg_about_info,
                    mContext.getString(R.string.alp_lib_name),
                    mContext.getString(R.string.alp_lib_version_name)));
            textInfo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
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
    };// mCmdAboutListener

}
