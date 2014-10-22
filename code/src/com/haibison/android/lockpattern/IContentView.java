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

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

/**
 * Common interface to use in both {@link Activity} and {@link Fragment}.
 * 
 * @author Hai Bison
 * 
 */
public interface IContentView {

    /**
     * Wrapper for {@link Activity#findViewById(int)} and
     * {@link View#findViewById(int)}.
     * 
     * @param id
     *            the resource ID of the view to find.
     * @return the view, or {@code null} if not found.
     */
    View findViewById(int id);

}
