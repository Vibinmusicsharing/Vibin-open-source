# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#Crashlytics
-dontwarn com.google.**
-keep class com.google.** { *; }

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class android.os.Handler.** { *; }

-keep class com.shorincity.vibin.music_sharing.model** { *; }

-keepnames class * implements android.os.Parcelable {
public static final ** CREATOR;}

-dontwarn com.google.android.gms.**

-ignorewarnings
-keep class * {
    public private *;
}

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes Exceptions, Signature, LineNumberTable
-keep public class * extends java.lang.Exception
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
