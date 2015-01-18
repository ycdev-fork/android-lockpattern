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

package com.haibison.android.lockpattern.widget;

import java.util.List;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.haibison.android.lockpattern.widget.LockPatternView.Cell;
import com.haibison.android.lockpattern.widget.LockPatternView.DisplayMode;
import com.haibison.android.lockpattern.widget.LockPatternView.OnPatternListener;

/**
 * Common interface for use of {@link LockPatternView} and
 * {@link LockPatternView_v11}.
 * 
 * @author Hai Bison
 *
 */
public interface ILockPatternView {

    /**
     * Gets display mode.
     * 
     * @return display mode.
     */
    DisplayMode getDisplayMode();

    /**
     * Retrieves current pattern.
     * 
     * @return current displaying pattern. <b>Note:</b> This is an independent
     *         list with the view's pattern itself.
     */
    List<Cell> getPattern();

    /**
     * <em>Wrapper for {@link View#getLayoutParams()}.</em>
     */
    LayoutParams getLayoutParams();

    /**
     * <em>Wrapper for {@link View#setLayoutParams(LayoutParams)}.</em>
     */
    void setLayoutParams(LayoutParams lp);

    /**
     * Set whether the view will use tactile feedback. If {@code true}, there
     * will be tactile feedback as the user enters the pattern.
     * 
     * @param tactileFeedbackEnabled
     *            Whether tactile feedback is enabled
     */
    void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled);

    /**
     * Set whether the view is in stealth mode. If {@code true}, there will be
     * no visible feedback as the user enters the pattern.
     *
     * @param inStealthMode
     *            Whether in stealth mode.
     */
    void setInStealthMode(boolean inStealthMode);

    /**
     * Set the call back for pattern detection.
     * 
     * @param onPatternListener
     *            The call back.
     */
    void setOnPatternListener(OnPatternListener onPatternListener);

    /**
     * Set the pattern explicitely (rather than waiting for the user to input a
     * pattern).
     * 
     * @param displayMode
     *            How to display the pattern.
     * @param pattern
     *            The pattern.
     */
    void setPattern(DisplayMode displayMode, List<Cell> pattern);

    /**
     * Set the display mode of the current pattern. This can be useful, for
     * instance, after detecting a pattern to tell this view whether change the
     * in progress result to correct or wrong.
     * 
     * @param displayMode
     *            The display mode.
     */
    void setDisplayMode(DisplayMode displayMode);

    /**
     * <em>Wrapper for {@link View#postDelayed(Runnable, long)}.</em>
     */
    boolean postDelayed(Runnable action, long delayMillis);

    /**
     * <em>Wrapper for {@link View#removeCallbacks(Runnable)}.</em>
     */
    boolean removeCallbacks(Runnable action);

    /**
     * Clear the pattern.
     */
    void clearPattern();

}
