## Minimum Wired Dots ##

By default, minimum wired dots required are `4`. You can configure that limit with helper class [Settings.Display](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/Settings.Display.html), the valid value is in range `1`-`9`. For example, to change it to `3`:

```
import com.haibison.android.lockpattern.util.Settings;

...

Settings.Display.setMinWiredDots(your-context, 3);
```


## Maximum Retries ##

By default, in comparing pattern mode, maximum retries allowed are `5`. For example, to change it to `3`:

```
Settings.Display.setMaxRetries(your-context, 3);
```

## Delivering result to a [PendingIntent](http://developer.android.com/reference/android/app/PendingIntent.html) or [ResultReceiver](http://developer.android.com/reference/android/os/ResultReceiver.html) ##

You can put a `PendingIntent` to [EXTRA\_PENDING\_INTENT\_OK](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#EXTRA_PENDING_INTENT_OK) or [EXTRA\_PENDING\_INTENT\_CANCELLED](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#EXTRA_PENDING_INTENT_CANCELLED). The library will call that intent according to the result. Also, to deliver the result to a `ResultReceiver`, you can use key [EXTRA\_RESULT\_RECEIVER](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/LockPatternActivity.html#EXTRA_RESULT_RECEIVER).