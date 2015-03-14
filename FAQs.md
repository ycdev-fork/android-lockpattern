### I'm new, where should I start? ###

Please, have a look at [Quick Use](QuickUse.md).

### Why resources are named with prefix `alp_42447968_`? ###

To avoid of collisions of resources with other libraries, or with the host project. At first we've tried `alp_`, but to make it safer, we've added a CRC-32 string `42447968_`. A CRC-32 string is shorter than an MD-5 string, and we think that would be safe enough.

### Where are preferences/settings saved? ###

On app's internal storage. Preferences files are named with suffix `a6eedbe5-1cf9-4684-8134-ad4ec9f6a131` (constant [Sys.UID](http://docs.android-lockpattern.googlecode.com/hg/com/haibison/android/lockpattern/util/Sys.html#UID)). And for your information, `42447968` is CRC-32 of above suffix.

And of course, the library's preferences don't mess with your app's own preferences, or other libraries' preferences.