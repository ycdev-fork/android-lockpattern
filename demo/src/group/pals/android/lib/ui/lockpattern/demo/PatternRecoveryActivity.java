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
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This activity is used to help the user recover his/ her pattern.
 * 
 * @author Hai Bison
 * @since v1.9 beta
 */
public class PatternRecoveryActivity extends Activity {

    private static final String CLASSNAME = PatternRecoveryActivity.class
            .getName();

    private static final int REQ_VERIFY_CAPTCHA = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * In this demo, we simply ask the user to verify a CAPTCHA pattern, and
         * clear the pattern if he/ she passes.
         */
        Intent intentActivity = new Intent(
                LockPatternActivity.ACTION_VERIFY_CAPTCHA, null, this,
                LockPatternActivity.class);
        intentActivity.putExtra(LockPatternActivity.EXTRA_THEME,
                CommandsPrefsHelper.getThemeForLockPatternActivity(this));

        startActivityForResult(intentActivity, REQ_VERIFY_CAPTCHA);
    }// onCreate()

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, String.format("onActivityResult(%,d, %,d, %s)",
                    requestCode, resultCode, data));

        try {
            switch (requestCode) {
            case REQ_VERIFY_CAPTCHA: {
                int msgId = 0;

                /*
                 * In any case, there's always a key EXTRA_RETRY_COUNT, which
                 * holds the number of tries that the user did.
                 */
                final int retryCount = data.getIntExtra(
                        LockPatternActivity.EXTRA_RETRY_COUNT, 0);

                /*
                 * NOTE that there are *3* possible result codes!!!
                 */
                switch (resultCode) {
                case RESULT_OK:
                    SecurityPrefs.setPattern(this, null);
                    Toast.makeText(this, getString(R.string.msg_pattern_reset),
                            Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    // The user cancelled the task
                    msgId = android.R.string.cancel;
                    break;
                case LockPatternActivity.RESULT_FAILED:
                    // The user failed to enter the pattern
                    msgId = R.string.failed;
                    break;
                default:
                    return;
                }

                if (msgId > 0) {
                    String msg = String.format(
                            "%s (%s)",
                            getString(msgId),
                            getString(retryCount <= 1 ? R.string.pmsg_x_try
                                    : R.string.pmsg_x_tries, retryCount));
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }

                break;
            }// REQ_VERIFY_CAPTCHA
            }
        } finally {
            finish();
        }
    }// onActivityResult()

}
