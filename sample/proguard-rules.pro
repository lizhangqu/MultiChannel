-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontoptimize
-dontwarn
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.widget.LinearLayout
-keep public class * extends android.widget.TextView
-keep public class * extends android.widget.GridView
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.widget.ListView
-keep public class * extends android.widget.RelativeLayout
-keep public class * extends android.widget.FrameLayout


-keep class **.R$* { *; }
-keepclassmembers class * {public <init>(org.json.JSONObject);}

-keepattributes SourceFile,LineNumberTable


-keepclassmembers class * extends android.content.Context {
  public void *(android.view.View);
  public void *(android.view.MenuItem);
}


-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}


-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-assumenosideeffects class android.util.Log { *; }

