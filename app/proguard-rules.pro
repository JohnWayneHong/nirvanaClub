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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#noinspection ShrinkerUnresolvedReference
-keep public class * implements java.io.Serializable {*;}
-keep class com.ggb.nirvanahappyclub.bean.**{*;}
-keep class com.ggb.nirvanahappyclub.sql.entity.**{*;}
-keep class com.ggb.nirvanahappyclub.sql.converter_bean.**{*;}
-keep class com.ggb.nirvanahappyclub.network.**{*;}
-keep class com.ggb.nirvanahappyclub.module.select_list.common.SelectItemSupport.**{*;}
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keep public class * extends androidx.lifecycle.AndroidViewModel

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep public class * extends com.ggb.nirvanahappyclub.sql.operator.BaseOperator

#忽略R8警告
-dontwarn android.content.**
-dontwarn com.efs.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
-dontwarn com.alipay.sdk.app.H5PayCallback
-dontwarn com.alipay.sdk.app.PayTask
-dontwarn com.download.library.DownloadTask$DownloadTaskStatus

-keep public class com.ggb.nirvanahappyclub.R$*{
public static final int *;
}
#
#
#-if interface * { @retrofit2.http.* *** *(...); }
#-keep,allowobfuscation interface <3>
#
# # Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
# -keep,allowobfuscation,allowshrinking interface retrofit2.Call
# -keep,allowobfuscation,allowshrinking class retrofit2.Response
#
# # With R8 full mode generic signatures are stripped for classes that are not
# # kept. Suspend functions are wrapped in continuations where the type argument
# # is used.
# -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation