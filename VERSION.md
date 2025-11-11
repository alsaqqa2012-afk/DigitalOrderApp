# سجل الإصدارات - Digital Order App

## الإصدار 1.0.1 (2025-11-11)

### إصلاح الأخطاء
- **حرج**: إصلاح تعارض Gradle الذي كان يمنع بناء التطبيق
- إصلاح استخدام API قديمة (NetworkInfo) واستبدالها بـ NetworkCapabilities
- إصلاح memory leak في Handler في SplashActivity
- إصلاح memory leak بسيط في Toast

### التحسينات
- إضافة معالجة لأخطاء SSL مع حوار تحذيري للمستخدم
- تحسين تنظيف WebView في onDestroy (مسح التاريخ والكاش)
- إضافة حفظ واستعادة حالة WebView عند تدوير الشاشة
- تحسين منطق SplashActivity لمنع فتح MainActivity بعد الإغلاق

### المواصفات التقنية
- **Package Name**: com.digitalorder.store
- **Version Code**: 2
- **Version Name**: 1.0.1
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

---

## الإصدار 1.0.0 (2025-11-11)

### الميزات الأساسية
- عرض موقع digitalorder.store في WebView
- شاشة بداية (Splash Screen) مع شعار التطبيق
- دعم كامل لـ JavaScript و DOM Storage
- شريط تقدم لتحميل الصفحات
- السحب للأسفل للتحديث (Pull to Refresh)
- معالجة الأخطاء وعرض رسائل مناسبة
- التحقق من الاتصال بالإنترنت
- التنقل بزر الرجوع داخل الموقع
- الضغط مرتين للخروج من التطبيق
- دعم اللغة العربية (RTL)
- أيقونة مخصصة للتطبيق
- معالجة الروابط الخارجية (tel:, mailto:, whatsapp:)
- فتح الروابط الخارجية في المتصفح

### المواصفات التقنية
- **Package Name**: com.digitalorder.store
- **Version Code**: 1
- **Version Name**: 1.0.0
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### التبعيات
- AndroidX AppCompat 1.6.1
- Material Components 1.10.0
- ConstraintLayout 2.1.4
- SwipeRefreshLayout 1.1.0

---

## كيفية تحديث الإصدار

عند إضافة ميزات جديدة أو إصلاح أخطاء، اتبع الخطوات التالية:

### 1. تحديث رقم الإصدار في `app/build.gradle`
```gradle
defaultConfig {
    versionCode 2  // زيادة الرقم بمقدار 1
    versionName "1.0.1"  // تحديث رقم الإصدار
}
```

### 2. تحديث النص في `res/values/strings.xml`
```xml
<string name="version">الإصدار 1.0.1</string>
```

### 3. توثيق التغييرات في هذا الملف
أضف قسم جديد في الأعلى يوضح التغييرات:

```markdown
## الإصدار 1.0.1 (التاريخ)

### التحسينات
- وصف التحسين

### إصلاح الأخطاء
- وصف الإصلاح

### ميزات جديدة
- وصف الميزة
```

### 4. بناء التطبيق
```bash
./gradlew assembleRelease
```

---

## نظام ترقيم الإصدارات (Semantic Versioning)

نستخدم نظام **X.Y.Z**:
- **X (Major)**: تغييرات كبيرة أو غير متوافقة
- **Y (Minor)**: ميزات جديدة متوافقة
- **Z (Patch)**: إصلاحات أخطاء صغيرة

مثال:
- `1.0.0` → `1.0.1`: إصلاح خطأ
- `1.0.1` → `1.1.0`: ميزة جديدة
- `1.1.0` → `2.0.0`: تغيير كبير
