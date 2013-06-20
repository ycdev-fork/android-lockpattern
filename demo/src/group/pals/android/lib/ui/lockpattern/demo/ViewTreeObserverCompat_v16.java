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

import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * Helper class for {@link ViewTreeObserver} in API 16+.
 * 
 * @author Hai Bison
 * 
 */
public class ViewTreeObserverCompat_v16 {

    /**
     * Wrapper for
     * {@link ViewTreeObserver#removeOnGlobalLayoutListener(OnGlobalLayoutListener)}
     * .
     * 
     * @param viewTreeObserver
     *            {@link ViewTreeObserver}.
     * @param victim
     *            {@link OnGlobalLayoutListener}.
     */
    public static void removeOnGlobalLayoutListener(
            ViewTreeObserver viewTreeObserver, OnGlobalLayoutListener victim) {
        viewTreeObserver.removeOnGlobalLayoutListener(victim);
    }// removeOnGlobalLayoutListener()
}
