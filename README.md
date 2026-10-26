# Hava

Hava, şehirlerin güncel hava durumunu ve hava tahminini gösteren bir Android uygulamasıdır. Ana sayfada seçili şehirlerin hava durumu özetleri görüntülenir; şehir arayarak farklı bir konumun tahminine ulaşabilirsiniz.

## Özellikler

- İstanbul, Ankara, Tokyo, Londra, New York ve Sidney için ana sayfa hava durumu özeti
- Şehir arama ve seçilen şehir için hava tahmini
- Güncel hava durumu, saatlik tahmin ve 7 günlük tahmin
- Nem, rüzgâr, yağış ve UV indeksi gibi hava detayları
- Hava koşullarına göre kıyafet önerisi ve aya/gökyüzüne ilişkin bilgiler
- Yükleniyor, boş sonuç ve hata durumlarının ekranda gösterilmesi

Hava durumu ve şehir arama verileri, API anahtarı gerektirmeyen [Open-Meteo](https://open-meteo.com/) servislerinden alınır.

## Ekran görüntüleri

| Ana sayfa | Şehir arama | Bugün |
|---|---|---|
| <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.45.jpeg" width="180" alt="Ana sayfa"> | <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.45%20%281%29.jpeg" width="180" alt="Şehir arama"> | <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.45%20%282%29.jpeg" width="180" alt="Bugün hava durumu ve kıyafet önerisi"> |
| Hafta | Detaylar | Gökyüzü |
| <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.46%20%281%29.jpeg" width="180" alt="Haftalık tahmin"> | <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.46.jpeg" width="180" alt="Hava durumu detayları"> | <img src="docs/images/WhatsApp%20Image%202026-10-26%20at%2009.59.46%20%282%29.jpeg" width="180" alt="Ay ve bu gece gözlenebilecek yıldızlar"> |

## Teknolojiler

- Kotlin ve Jetpack Compose (Material 3)
- AndroidX ViewModel, StateFlow ve Coroutines
- Retrofit ve Gson
- Navigation Compose
- JUnit

## Gereksinimler

- Android Studio ve Android SDK
- JDK 17
- Android 8.0 (API 26) veya üzeri bir cihaz ya da emülatör

## Derleme ve çalıştırma

Android SDK yolunun `local.properties` içindeki `sdk.dir` ayarında veya `ANDROID_HOME` ortam değişkeninde tanımlı olduğundan emin olun.

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

macOS / Linux:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Test ve biçimlendirme

Windows'ta `.\gradlew.bat`, macOS/Linux'ta `./gradlew` kullanın:

```bash
./gradlew testDebugUnitTest
./gradlew spotlessCheck
./gradlew spotlessApply
```

## Proje yapısı

```text
app/src/main/java/com/kampplus/hava/
├── data/       Open-Meteo API'leri ve veri erişim katmanı
├── model/      Şehir, hava tahmini ve yardımcı modeller
└── ui/         Compose ekranları, ViewModel'ler ve tema
```

Uygulama; arayüzün durumu çizdiği, ViewModel'lerin ekran durumunu yönettiği ve repository katmanının API'lerden veri aldığı sade bir yapıyı izler. Daha fazla mimari bilgi için [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) dosyasına bakın.

## Release imzası

Release APK'sını kendi anahtarınızla imzalamak için `keystore.properties.example` dosyasını `keystore.properties` adıyla kopyalayıp değerleri doldurun. Keystore dosyasını ve parolaları depoya eklemeyin. Örnek ayarlar yapılmamışsa release derlemesi debug anahtarıyla imzalanır; mağaza yayını için kendi release anahtarınızı kullanın.
