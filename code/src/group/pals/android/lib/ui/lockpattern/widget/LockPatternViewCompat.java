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

package group.pals.android.lib.ui.lockpattern.widget;

import android.content.Context;
import android.view.View;

/**
 * Helper class for {@link LockPatternView} in API 16+.
 * <p>
 * <b>Minimum API: 16</b>
 * </p>
 * 
 * @since v2.4 beta
 * @author Hai Bison
 * 
 */
public class LockPatternViewCompat {

    /**
     * Wrapper for {@link View#announceForAccessibility(CharSequence)}.
     * 
     * @param view
     *            a view.
     * @param context
     *            the context.
     * @param resId
     *            String resource ID.
     * @see View#announceForAccessibility(CharSequence)
     */
    public static void announceForAccessibility(View view, Context context,
            int resId) {
        view.announceForAccessibility(context.getString(resId));
    }
}
