# هيكل المشروع - Digital Order App

## 📁 نظرة عامة على المجلدات

```
DigitalOrderApp/
├── 📄 README.md                          # الدليل الشامل للمشروع
├── 📄 QUICK_START.md                     # دليل البدء السريع
├── 📄 BUILD_INSTRUCTIONS.md              # تعليمات البناء والنشر
├── 📄 VERSION.md                         # سجل الإصدارات
├── 📄 PROJECT_STRUCTURE.md               # هذا الملف
├── 📄 .gitignore                         # ملفات يتم تجاهلها في Git
├── 📄 build.gradle                       # إعدادات Gradle الرئيسية
├── 📄 settings.gradle                    # إعدادات المشروع
├── 📄 gradle.properties                  # خصائص Gradle
├── 📄 gradlew                            # سكريبت Gradle لـ Unix/Mac
├── 📄 gradlew.bat                        # سكريبت Gradle لـ Windows
│
├── 📁 gradle/                            # ملفات Gradle Wrapper
│   └── 📁 wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
└── 📁 app/                               # مجلد التطبيق الرئيسي
    ├── 📄 build.gradle                   # إعدادات بناء التطبيق
    ├── 📄 proguard-rules.pro             # قواعد ProGuard
    │
    └── 📁 src/
        └── 📁 main/
            ├── 📄 AndroidManifest.xml    # ملف بيان التطبيق
            │
            ├── 📁 java/com/digitalorder/store/
            │   ├── 📄 MainActivity.java          # الشاشة الرئيسية
            │   └── 📄 SplashActivity.java        # شاشة البداية
            │
            └── 📁 res/                   # الموارد (Resources)
                ├── 📁 drawable/          # الصور والرسومات
                │   ├── logo.png
                │   ├── splash_background.xml
                │   └── ic_error.xml
                │
                ├── 📁 layout/            # ملفات التصميم
                │   ├── activity_main.xml
                │   └── activity_splash.xml
                │
                ├── 📁 mipmap-mdpi/       # أيقونات 48x48
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                │
                ├── 📁 mipmap-hdpi/       # أيقونات 72x72
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                │
                ├── 📁 mipmap-xhdpi/      # أيقونات 96x96
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                │
                ├── 📁 mipmap-xxhdpi/     # أيقونات 144x144
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                │
                ├── 📁 mipmap-xxxhdpi/    # أيقونات 192x192
                │   ├── ic_launcher.png
                │   └── ic_launcher_round.png
                │
                ├── 📁 values/            # القيم والموارد
                │   ├── strings.xml       # النصوص
                │   ├── colors.xml        # الألوان
                │   └── themes.xml        # الثيمات
                │
                └── 📁 xml/               # ملفات XML الإضافية
                    ├── backup_rules.xml
                    └── data_extraction_rules.xml
```

---

## 📄 شرح الملفات الرئيسية

### ملفات الجذر

#### `build.gradle` (الرئيسي)
إعدادات Gradle على مستوى المشروع. يحدد:
- المستودعات (repositories)
- إصدار Android Gradle Plugin
- التبعيات المشتركة

#### `settings.gradle`
يحدد اسم المشروع والوحدات (modules) المضمنة.

#### `gradle.properties`
خصائص Gradle مثل:
- حجم الذاكرة المخصصة
- إعدادات AndroidX
- خيارات التحسين

#### `gradlew` / `gradlew.bat`
سكريبتات لتشغيل Gradle بدون تثبيته عالمياً.

---

### ملفات التطبيق

#### `app/build.gradle`
أهم ملف في المشروع! يحتوي على:

```gradle
android {
    namespace 'com.digitalorder.store'      // اسم الحزمة
    compileSdk 34                           // SDK للتجميع
    
    defaultConfig {
        applicationId "com.digitalorder.store"  // معرف التطبيق
        minSdk 21                           // أقل إصدار مدعوم
        targetSdk 34                        // الإصدار المستهدف
        versionCode 1                       // رقم الإصدار الداخلي
        versionName "1.0.0"                 // رقم الإصدار الظاهر
    }
}

dependencies {
    // التبعيات المطلوبة
}
```

**للتعديل**:
- غيّر `versionCode` و `versionName` عند كل إصدار جديد
- أضف تبعيات جديدة في قسم `dependencies`

#### `app/proguard-rules.pro`
قواعد ProGuard لتصغير وحماية الكود عند البناء للإنتاج.

---

### ملف البيان

#### `app/src/main/AndroidManifest.xml`
يحدد:
- **الصلاحيات** (Permissions): مثل الإنترنت
- **الأنشطة** (Activities): الشاشات في التطبيق
- **الأيقونة والاسم**: ما يظهر للمستخدم
- **إعدادات التطبيق**: مثل دعم RTL

```xml
<uses-permission android:name="android.permission.INTERNET" />

<application
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.DigitalOrder">
    
    <activity android:name=".SplashActivity" android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
    
    <activity android:name=".MainActivity" />
</application>
```

---

## 💻 ملفات Java

### `SplashActivity.java`
شاشة البداية التي تظهر عند فتح التطبيق.

**الوظائف**:
- عرض الشعار لمدة 2 ثانية
- الانتقال تلقائياً للشاشة الرئيسية

**للتعديل**:
- غيّر `SPLASH_DURATION` لتغيير مدة العرض
- عدّل `activity_splash.xml` لتغيير التصميم

### `MainActivity.java`
الشاشة الرئيسية التي تحتوي على WebView.

**الوظائف الرئيسية**:
1. **إعداد WebView**:
   - تفعيل JavaScript
   - تفعيل DOM Storage
   - إعداد الكاش

2. **معالجة التحميل**:
   - عرض شريط التقدم
   - معالجة الأخطاء
   - التحقق من الإنترنت

3. **التنقل**:
   - زر الرجوع للتنقل في الموقع
   - الضغط مرتين للخروج

4. **الروابط الخاصة**:
   - فتح روابط الهاتف والبريد
   - فتح الروابط الخارجية في المتصفح

**للتعديل**:
- غيّر `websiteUrl` لتغيير الموقع المعروض
- عدّل `shouldOverrideUrlLoading()` لتخصيص معالجة الروابط

---

## 🎨 ملفات الموارد (Resources)

### `res/drawable/`
الصور والرسومات المتجهة (Vector Drawables).

**الملفات**:
- `logo.png`: شعار التطبيق (512x512)
- `splash_background.xml`: خلفية شاشة البداية
- `ic_error.xml`: أيقونة الخطأ

### `res/layout/`
ملفات تصميم الشاشات بصيغة XML.

#### `activity_splash.xml`
تصميم شاشة البداية:
- ImageView للشعار
- ProgressBar للتحميل
- TextView لرقم الإصدار

#### `activity_main.xml`
تصميم الشاشة الرئيسية:
- Toolbar في الأعلى
- ProgressBar للتحميل
- SwipeRefreshLayout للتحديث
- WebView لعرض الموقع
- LinearLayout لعرض الأخطاء

### `res/mipmap-*/`
أيقونات التطبيق بأحجام مختلفة لدعم جميع الأجهزة.

**الأحجام**:
- **mdpi**: 48x48 (كثافة متوسطة)
- **hdpi**: 72x72 (كثافة عالية)
- **xhdpi**: 96x96 (كثافة عالية جداً)
- **xxhdpi**: 144x144 (كثافة عالية جداً جداً)
- **xxxhdpi**: 192x192 (كثافة فائقة)

### `res/values/`

#### `strings.xml`
جميع النصوص في التطبيق:
```xml
<string name="app_name">الطلب الرقمي</string>
<string name="website_url">https://digitalorder.store</string>
<string name="version">الإصدار 1.0.0</string>
```

**للترجمة**: أنشئ `values-en/strings.xml` للإنجليزية

#### `colors.xml`
الألوان المستخدمة:
```xml
<color name="primary">#1A5F7A</color>
<color name="accent">#57C5B6</color>
```

#### `themes.xml`
ثيمات التطبيق:
- `Theme.DigitalOrder`: الثيم الرئيسي
- `Theme.DigitalOrder.Splash`: ثيم شاشة البداية

---

## 🔨 ملفات البناء (Build Output)

بعد البناء، ستجد:

```
app/build/
├── outputs/
│   ├── apk/
│   │   ├── debug/
│   │   │   └── app-debug.apk
│   │   └── release/
│   │       └── app-release.apk
│   └── bundle/
│       └── release/
│           └── app-release.aab
└── intermediates/
    └── (ملفات مؤقتة)
```

---

## 📝 نصائح للتعديل

### إضافة شاشة جديدة:
1. أنشئ `NewActivity.java` في مجلد Java
2. أنشئ `activity_new.xml` في مجلد layout
3. أضف Activity في `AndroidManifest.xml`

### إضافة صلاحية جديدة:
أضف في `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.PERMISSION_NAME" />
```

### إضافة مكتبة خارجية:
أضف في `app/build.gradle`:
```gradle
dependencies {
    implementation 'com.example:library:1.0.0'
}
```

### تغيير اسم الحزمة:
1. غيّر `namespace` في `app/build.gradle`
2. غيّر `applicationId` في `defaultConfig`
3. أعد تسمية مجلدات Java
4. حدّث `package` في ملفات Java

---

## 🎯 الملفات الأكثر أهمية للتعديل

### للتخصيص الأساسي:
1. ✅ `res/values/strings.xml` - النصوص
2. ✅ `res/values/colors.xml` - الألوان
3. ✅ `res/drawable/logo.png` - الشعار

### للتطوير المتقدم:
1. ✅ `MainActivity.java` - منطق التطبيق
2. ✅ `app/build.gradle` - الإعدادات والتبعيات
3. ✅ `AndroidManifest.xml` - الصلاحيات والإعدادات

### للنشر:
1. ✅ `app/build.gradle` - رقم الإصدار
2. ✅ `proguard-rules.pro` - قواعد الحماية
3. ✅ إنشاء keystore للتوقيع

---

**نهاية الدليل**

الآن لديك فهم كامل لهيكل المشروع وكيفية التعديل عليه!
