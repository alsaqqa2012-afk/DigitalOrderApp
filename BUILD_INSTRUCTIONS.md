# تعليمات بناء التطبيق

## المتطلبات الأساسية

### 1. تثبيت Android Studio

قم بتنزيل وتثبيت **Android Studio** من الموقع الرسمي:
- الموقع: https://developer.android.com/studio
- الإصدار الموصى به: Arctic Fox أو أحدث

### 2. تثبيت JDK

تأكد من تثبيت **Java Development Kit (JDK)**:
- الإصدار المطلوب: JDK 8 أو أحدث
- يمكنك تثبيته من خلال Android Studio أو بشكل منفصل

### 3. تثبيت Android SDK

عند تثبيت Android Studio، سيتم تثبيت SDK تلقائياً. تأكد من تثبيت:
- **Android SDK Platform 34** (Android 14)
- **Android SDK Build-Tools 34.0.0** أو أحدث
- **Android SDK Platform-Tools**
- **Android SDK Tools**

## خطوات فتح المشروع

### الطريقة 1: من خلال Android Studio

1. افتح **Android Studio**
2. اختر **File** → **Open**
3. انتقل إلى مجلد `DigitalOrderApp`
4. اضغط على **OK**
5. انتظر حتى يتم مزامنة Gradle (قد يستغرق بضع دقائق في المرة الأولى)

### الطريقة 2: من سطر الأوامر

```bash
cd /path/to/DigitalOrderApp
./gradlew build
```

## بناء التطبيق

### بناء APK للتطوير (Debug)

#### من Android Studio:
1. اذهب إلى **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. انتظر حتى ينتهي البناء
3. ستظهر رسالة بمكان الملف: `app/build/outputs/apk/debug/app-debug.apk`

#### من سطر الأوامر:
```bash
cd DigitalOrderApp
./gradlew assembleDebug
```

الملف الناتج: `app/build/outputs/apk/debug/app-debug.apk`

### بناء APK للإنتاج (Release)

#### من سطر الأوامر:
```bash
cd DigitalOrderApp
./gradlew assembleRelease
```

الملف الناتج: `app/build/outputs/apk/release/app-release-unsigned.apk`

**ملاحظة مهمة**: ملف APK غير الموقع لا يمكن تثبيته مباشرة. تحتاج إلى توقيعه أولاً.

## توقيع التطبيق للنشر

### 1. إنشاء مفتاح التوقيع (Keystore)

```bash
keytool -genkey -v -keystore digitalorder-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias digitalorder
```

سيطلب منك إدخال:
- كلمة مرور للـ keystore
- معلومات عن المطور/الشركة
- كلمة مرور للـ alias

**احتفظ بهذا الملف في مكان آمن ولا تفقد كلمة المرور!**

### 2. إعداد ملف التوقيع

أنشئ ملف `keystore.properties` في جذر المشروع:

```properties
storeFile=../digitalorder-release-key.jks
storePassword=كلمة_المرور_للكيستور
keyAlias=digitalorder
keyPassword=كلمة_المرور_للالياس
```

**لا تضف هذا الملف إلى Git!** أضفه إلى `.gitignore`

### 3. تعديل app/build.gradle

أضف قبل `android {`:

```gradle
def keystorePropertiesFile = rootProject.file("keystore.properties")
def keystoreProperties = new Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(new FileInputStream(keystorePropertiesFile))
}
```

وداخل `android {` أضف:

```gradle
signingConfigs {
    release {
        if (keystorePropertiesFile.exists()) {
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
        }
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

### 4. بناء APK موقع

```bash
./gradlew assembleRelease
```

الملف الناتج: `app/build/outputs/apk/release/app-release.apk`

هذا الملف جاهز للنشر على Google Play أو التوزيع المباشر.

## تشغيل التطبيق

### على جهاز حقيقي

1. فعّل **خيارات المطور** على جهازك:
   - اذهب إلى **الإعدادات** → **حول الهاتف**
   - اضغط على **رقم الإصدار** 7 مرات
   
2. فعّل **تصحيح USB**:
   - اذهب إلى **الإعدادات** → **خيارات المطور**
   - فعّل **تصحيح USB**

3. وصل الجهاز بالكمبيوتر عبر USB

4. في Android Studio:
   - اضغط على زر **Run** (▶️)
   - اختر جهازك من القائمة

### على محاكي (Emulator)

1. افتح **AVD Manager**:
   - في Android Studio: **Tools** → **AVD Manager**

2. أنشئ محاكي جديد:
   - اضغط **Create Virtual Device**
   - اختر جهاز (مثل Pixel 5)
   - اختر صورة نظام (System Image) - يفضل API 34
   - اضغط **Finish**

3. شغّل المحاكي واضغط على زر **Run** في Android Studio

## تثبيت APK مباشرة

### على جهاز أندرويد

1. انقل ملف APK إلى جهازك
2. افتح الملف من مدير الملفات
3. اسمح بتثبيت التطبيقات من مصادر غير معروفة إذا طُلب منك
4. اضغط على **تثبيت**

### باستخدام ADB

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## حل المشاكل الشائعة

### مشكلة: Gradle sync failed

**الحل**:
1. تأكد من اتصالك بالإنترنت
2. في Android Studio: **File** → **Invalidate Caches / Restart**
3. احذف مجلد `.gradle` من المشروع وأعد المزامنة

### مشكلة: SDK not found

**الحل**:
1. افتح **File** → **Project Structure** → **SDK Location**
2. تأكد من تحديد مسار Android SDK الصحيح
3. أو أنشئ ملف `local.properties` في جذر المشروع:
```properties
sdk.dir=/path/to/Android/Sdk
```

### مشكلة: Build failed - missing dependencies

**الحل**:
1. افتح **SDK Manager**: **Tools** → **SDK Manager**
2. في تبويب **SDK Platforms**: تأكد من تثبيت API Level 34
3. في تبويب **SDK Tools**: تأكد من تثبيت Build-Tools

### مشكلة: Unable to start daemon process

**الحل**:
```bash
./gradlew --stop
./gradlew clean
./gradlew build
```

### مشكلة: Out of memory

**الحل**: عدّل ملف `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## تحسين أداء البناء

### 1. تفعيل Gradle Daemon

في `gradle.properties`:
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

### 2. استخدام Build Cache

```properties
android.enableBuildCache=true
org.gradle.caching=true
```

### 3. زيادة ذاكرة Gradle

```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

## النشر على Google Play

### 1. إنشاء حساب مطور

- اذهب إلى: https://play.google.com/console
- سجل كحساب مطور (رسوم لمرة واحدة: $25)

### 2. إنشاء تطبيق جديد

- اضغط **Create app**
- املأ المعلومات المطلوبة

### 3. رفع APK أو AAB

يفضل استخدام **Android App Bundle (AAB)** بدلاً من APK:

```bash
./gradlew bundleRelease
```

الملف الناتج: `app/build/outputs/bundle/release/app-release.aab`

### 4. إكمال معلومات المتجر

- لقطات شاشة
- وصف التطبيق
- أيقونة التطبيق
- سياسة الخصوصية
- تصنيف المحتوى

### 5. نشر التطبيق

- اختر **Production** أو **Internal testing**
- ارفع ملف AAB
- راجع وأرسل للمراجعة

## الخلاصة

الآن لديك تطبيق أندرويد كامل جاهز للتطوير والنشر. يمكنك:
- ✅ فتح المشروع في Android Studio
- ✅ تعديل الكود والتصميم
- ✅ بناء APK للاختبار
- ✅ توقيع التطبيق للنشر
- ✅ رفعه على Google Play

**نصيحة**: احتفظ بنسخة احتياطية من ملف التوقيع (keystore) في مكان آمن!
