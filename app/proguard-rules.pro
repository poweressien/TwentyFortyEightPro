# Add project specific ProGuard rules here.

# Keep kotlinx.serialization model classes — reflection-free but the
# generated serializer objects still need their structure preserved.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class com.twentyfortyeightpro.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.twentyfortyeightpro.data.model.**$$serializer { *; }
-keepclassmembers class com.twentyfortyeightpro.data.model.** {
    *** Companion;
}

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Signature, Exceptions
