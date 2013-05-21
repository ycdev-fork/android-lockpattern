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

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Helper interface for {@link PreferenceActivity} and
 * {@link PreferenceFragment}.
 * 
 * @author Hai Bison
 * 
 */
public interface PreferenceHolder {

    /**
     * Finds a preference by its key.
     * 
     * @param key
     *            the preference's key.
     * @return the {@link Preference} object, or {@code null} if not found.
     * @see PreferenceActivity#findPreference(CharSequence)
     * @see PreferenceFragment#findPreference(CharSequence)
     */
    Preference findPreference(CharSequence key);

}
