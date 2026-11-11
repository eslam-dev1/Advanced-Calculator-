# الحاسبة الذكية (Smart Calculator)

تطبيق حاسبة علمية متطور لنظام أندرويد، مبني بأحدث تقنيات **Kotlin** و **Jetpack Compose** مع التزام كامل بإرشادات **Material Design 3**. يتميز التطبيق بتصميم أنيق (Obsidian Dark) وأداء عالي، مع دعم لميزات متقدمة مثل الرسم البياني للدوال، النافذة العائمة، ومحول الوحدات.

---

## 🚀 المميزات الرئيسية

*   **الحاسبة العلمية المتقدمة:** دعم كامل للعمليات الحسابية والعلمية (الدوال المثلثية، اللوغاريتمات، الأسس، الجذور، والمضروب).
*   **الرسم البياني التفاعلي (Grapher):** رسم الدوال الرياضية (مثل `sin(x)`، `cos(x)`، `x^2`) على مستوى إحداثيات ديكارتي ثنائي الأبعاد.
*   **الحاسبة المصغرة العائمة (Floating Mini Calculator):** نافذة عائمة تسمح بإجراء العمليات السريعة فوق التطبيقات الأخرى.
*   **تأثيرات صوتية ولمسية (Audio & Haptic Feedback):** استجابة لمسية واهتزاز خفيف مع أصوات نقر قابلة للتعطيل من الإعدادات.
*   **محول الوحدات الشامل:** تحويل فوري لوحدات الطول، الوزن، المساحة، الحجم، درجة الحرارة، السرعة، والزمن.
*   **وضع المبرمج (Programmer Mode):** تحويل فوري ودقيق بين الأنظمة العددية (HEX, DEC, OCT, BIN).
*   **محرك السمات (Theme Engine):** إمكانية التبديل بين 6 ألوان تمييز جذابة (Electric Blue, Emerald, Cyber Amber, Crimson, Solarized, Midnight Purple).
*   **سجل العمليات (History):** حفظ العمليات الحسابية محلياً مع إمكانية استرجاعها ونسخها.

---

## 🛠 التقنيات المستخدمة (Tech Stack)

*   **لغة البرمجة:** [Kotlin 2.2+](https://kotlinlang.org/)
*   **واجهة المستخدم:** [Jetpack Compose (M3)](https://developer.android.com/jetpack/compose)
*   **النمط المعماري:** MVVM (Model-View-ViewModel) + Clean Architecture
*   **قاعدة البيانات المحلية:** [Room Database](https://developer.android.com/training/data-storage/room)
*   **إدارة الحزم:** Version Catalog (`gradle/libs.versions.toml`)
*   **نظام البناء:** Gradle 9.3+ / Android Gradle Plugin 9.1+
*   **التكامل المستمر (CI/CD):** GitHub Actions Workflow

---

## 🏗 البناء التلقائي عبر GitHub (GitHub Actions CI/CD)

المشروع مُجهز بالكامل للتشغيل والبناء التلقائي على مستودع GitHub:

1. **البناء التلقائي عند الدفع (Automated Build on Push):**
   - بمجرد رفع الكود إلى فرع `main` أو `master` أو فتح `Pull Request`، يقوم سير العمل في GitHub Actions بتهيئة البيئة (JDK 21)، وتثبيت الاعتماديات، وتشغيل كافة اختبارات الوحدة (`Unit Tests`)، وبناء حزمة التطبيق `APK`.

2. **التشغيل اليدوي (Manual Workflow Dispatch):**
   - توجه إلى تبويب **Actions** في مستودع GitHub.
   - اختر سير العمل **Android CI**.
   - اضغط على **Run workflow** للبدء فوراً دون الحاجة لتعديل الكود.

3. **تحميل حزمة التطبيق الجاهزة (Download APK):**
   - بعد اكتمال سير العمل بنجاح، ستجد ملف الـ APK متاحاً للتنزيل مباشرة تحت قسم **Artifacts** باسم `smart-calculator-debug-apk`.

---

## 💻 التشغيل والبناء محلياً (Local Build)

### المتطلبات الأساسية:
* **JDK 21** أو أحدث.
* **Android Studio** (نسخة Koala أو أحدث).
* نظام Android 8.0 (API Level 26) أو أعلى لتشغيل التطبيق.

### خطوات البناء عبر سطر الأوامر:
```bash
# استنساخ المستودع
git clone https://github.com/your-username/smart-calculator.git
cd smart-calculator

# منح صلاحية التنفيذ لـ gradlew (أنظمة Linux و macOS)
chmod +x gradlew

# تشغيل الاختبارات
./gradlew testDebugUnitTest

# بناء حزمة التطبيق Debug APK
./gradlew assembleDebug
```
ستجد ملف الـ APK الناتج داخل المسار:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 الترخيص (License)
هذا المشروع مفتوح المصدر ومتاح تحت رخصة [MIT License](LICENSE).
