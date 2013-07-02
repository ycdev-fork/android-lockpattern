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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class PrefsActivity extends PreferenceActivity implements
        PreferenceHolder {

    private static final String CLASSNAME = PrefsActivity.class.getName();

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            /*
             * Since this demo is a small app, we borrow ALP's preferences file.
             * If you're building a medium or large app, you should use your own
             * preferences file. You can easily write some wrappers to forward
             * your preferences to ALP's preferences.
             */
            Prefs.setupPreferenceManager(this, getPreferenceManager());

            addPreferencesFromResource(R.xml.main_preferences);
            new CommandsPrefsHelper(this, this).init();
            new InfosPrefsHelper(this, this).init();

            getListView().setCacheColorHint(
                    getResources().getColor(android.R.color.transparent));
        }
    }// onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, String.format("onActivityResult(%,d, %,d, %s)",
                    requestCode, resultCode, data));

        switch (requestCode) {
        case CommandsPrefsHelper.REQ_CREATE_PATTERN: {
            if (resultCode == RESULT_OK)
                Toast.makeText(
                        this,
                        getString(
                                R.string.pmsg_your_new_pattern,
                                new String(
                                        data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN))),
                        Toast.LENGTH_LONG).show();

            break;
        }// REQ_CREATE_PATTERN

        case CommandsPrefsHelper.REQ_ENTER_PATTERN:
        case CommandsPrefsHelper.REQ_VERIFY_CAPTCHA: {
            int msgId = 0;

            /*
             * NOTE that there are *4* possible result codes!!!
             */
            switch (resultCode) {
            case RESULT_OK:
                // The user passed
                msgId = android.R.string.ok;
                break;
            case RESULT_CANCELED:
                // The user cancelled the task
                msgId = android.R.string.cancel;
                break;
            case LockPatternActivity.RESULT_FAILED:
                // The user failed to enter the pattern
                msgId = R.string.failed;
                break;
            case LockPatternActivity.RESULT_FORGOT_PATTERN:
                /*
                 * Since we started PatternRecoveryActivity, there's nothing to
                 * do here.
                 */
                return;
            default:
                return;
            }

            /*
             * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
             * the number of tries that the user did.
             */
            final int retryCount = data.getIntExtra(
                    LockPatternActivity.EXTRA_RETRY_COUNT, 0);
            String msg = String.format(
                    "%s (%s)",
                    getString(msgId),
                    getString(retryCount <= 1 ? R.string.pmsg_x_try
                            : R.string.pmsg_x_tries, retryCount));

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

            break;
        }// REQ_ENTER_PATTERN && REQ_VERIFY_CAPTCHA
        }
    }// onActivityResult()
}
