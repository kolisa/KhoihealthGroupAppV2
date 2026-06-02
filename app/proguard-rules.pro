-keep class com.zeroner.blemidautumn.** { *; }
-keep class com.zeroner.blemidautumn.bean.WristBand { *; }
-keep class com.zeroner.blemidautumn.bluetooth.** { *; }
-keep class com.zeroner.blemidautumn.task.** { *; }
-keep class com.khoihealth.app.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

# Retrofit
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
