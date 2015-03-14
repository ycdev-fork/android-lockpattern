# …to v3.2 #

Please update your code (Java, XML…) due to these changes:

  * We drop support for APIs < 7.
  * Rename `EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN` to `EXTRA_PENDING_INTENT_FORGOT_PATTERN`. Note that its value type is also changed from `Intent` to `PendingIntent`.

# …to v3.1 #

Please update your code (Java, XML…) due to these changes:

  * Change package name to `com.haibison.android.lockpattern`.
  * Change prefixes for resources from:
    * `alp_` to `alp_42447968_`
    * `Alp.` to `Alp.42447968.`

You might want to go through the [Quick Use](QuickUse.md) again  :-)

# …to v3.0.7 #

There's nothing changed.

# …to v3.0.6 #

There's nothing changed. However we add new support for configuring settings directly via your app's [AndroidManifest.xml](AndroidManifest.md).

# …to v3.0.5 #

Class `Settings.Display`: rename methods `setMaxRetry()` to `setMaxRetries()`, `getMaxRetry()` to `getMaxRetries()`.

# …to v3.0.4 #

Refactor package `prefs` to a single class `util.Settings`:

  * `prefs.Prefs` -> `util.Settings`
  * `prefs.DisplayPrefs` -> `util.Settings.Display`
  * `prefs.SecurityPrefs` -> `util.Settings.Security`

# …to v3.0.3 #

There's nothing changed  :-)

# …to v3.0 #

Add one more method to interface `IEncrypter`:
```
    /**
     * Decrypts an encrypted pattern.
     * 
     * @param context
     *            the context.
     * @param encryptedPattern
     *            the encrypted pattern.
     * @return the original pattern.
     */
    List<Cell> decrypt(Context context, char[] encryptedPattern);
```

Please update your code for above change. Basically, it decrypts the encrypted pattern from `encrypt(Context, List<Cell>)`.

# …to v2.9 #

There's nothing changed  :-)

# …to v2.8 #

  * Change method `IEncrypter.encrypt()` from:
```
char[] encrypt(Context context, char[] pattern);
```
> to:
```
char[] encrypt(Context context, List<Cell> pattern);
```
> To keep your last encrypter safe, use helper class  `LockPatternUtils`:
```
import group.pals.android.lib.ui.lockpattern.widget.LockPatternUtils;

...

    @Override
    public char[] encrypt(Context context, List<Cell> pattern) {
        /*
         * This is the value that last version passed to your encrypter.
         */
        char[] patternAsChars = LockPatternUtils.patternToSha1(pattern)
                .toCharArray();

        return ...
    }// encrypt()
```
  * Rename `EXTRA_OK_PENDING_INTENT`, `EXTRA_CANCELLED_PENDING_INTENT` to `EXTRA_PENDING_INTENT_OK` and `EXTRA_PENDING_INTENT_CANCELLED`.
  * Add new extra `EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN` to help the user recover the pattern if he/ she forgot it. Check [QuickUse](QuickUse#%22Forgot_pattern%3F%22.md) for details. This leads to some changes below:
    * `ACTION_COMPARE_PATTERN`: there are _**4 possible result codes**_: `RESULT_OK`, `RESULT_CANCELED`, `LockPatternActivity.RESULT_FAILED` and `LockPatternActivity.RESULT_FORGOT_PATTERN`.
    * `ACTION_VERIFY_CAPTCHA`: there are _**3 possible result codes**_ like above _excluding_ `RESULT_FORGOT_PATTERN`.

# …to v2.7 #

There's nothing changed. We've added new action named `ACTION_VERIFY_CAPTCHA`. You simply use it with new `Intent` and the library will do the rest. To change default wired dots (which is `4`), use helper class `DisplayPrefs`:
```
import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;

...

DisplayPrefs.setCaptchaWiredDots(your-context, 9);
```

# …to v2.6 #

  * Change coding style: use `UPPER_CASE` for all `static final` fields and enums.
  * Move all dynamic settings to `SharedPreferences`. For example all these keys were deleted: ~~`_EncrypterClass`~~, ~~`_StealthMode`~~… Check QuickUse again for details.
  * Change interface `IEncrypter`. Please see [Encryption](https://code.google.com/p/android-lockpattern/wiki/Encryption) for further details.
  * Please note that the pattern is now hold in key `EXTRA_PATTERN` and it is a `char[]` array (_not_ `String`).

# …to v2.5 #

## Important notes ##

  * We've added four built-in themes. In order to let the library work properly, you have to use one of them. Because the themes contain resources that the library needs. They are: `Alp_Theme_Dark`, `Alp_Theme_Light`, `Alp_Theme_Dialog_Dark` and `Alp_Theme_Dialog_Light`.
  * In mode comparing pattern, **_if_** the user fails to pass the process, the library will return result code `LockPatternActivity._ResultFailed` (**_not_** `RESULT_CANCELED`). **_Please note_** that there are _**[3 possible result codes](QuickUse#Let_the_user_identify_himself_with_lock_pattern.md)**_. You might want to take a look at the [demo project](https://code.google.com/p/android-lockpattern/source/browse/demo) for further information.

# …to v2.4 #

Key ~~`_Mode`~~ and enum ~~`LPMode`~~ were removed. Now you configure handlers via action names:
  * Use `_ActionCreatePattern` to create new pattern
  * Use `_ActionComparePattern` to compare pattern
(Check [Usage](QuickUse#Usage.md)).

# …to v2.3 #

To reduce binary size, we removed all keys/ methods which were deprecated and were notified and kept in at least one older version (for migrating purpose):
  * Key `LockPatternActivity.`~~`_PaternSha1`~~
  * Method `IEncrypter.`~~`encrypt(String)`~~

# …to v2.1 #

  * [Encryption](https://code.google.com/p/android-lockpattern/wiki/Encryption): Method `IEncrypter.`~~`encrypt(String)`~~ is _deprecated_ and _no longer used_. Please use this new method instead:
```
String IEncrypter.encrypt(Context, String);
```

# …to v2 #

From version 2, key `LockPatternActivity`.~~`_PaternSha1`~~ is _deprecated_ and _no longer used_. Please use this new key instead:
```
LockPatternActivity._Pattern
```