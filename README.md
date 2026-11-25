# Gobooks

Aplikasi pencarian buku dengan Google Books API, dibangun dengan Jetpack Compose,
Retrofit, dan Coil. Praktikum PPPBD P13 — modul *Retrofit: Akses Data dari Internet
dengan Google Books API (UI Jetpack Compose)*.

## Prasyarat

- Android Studio (JDK 21) dengan Android SDK API 37
- Emulator atau perangkat dengan Android 7.0 (API 24) ke atas

## 1. Memperoleh API key Google Books

1. Buka <https://console.cloud.google.com>, buat project baru (mis. `praktikum-googlebooks`).
2. **APIs & Services > Library**, cari `Books API`, lalu klik **Enable**.
3. **APIs & Services > Credentials > Create credentials > API key**.
4. Edit key tersebut, lalu atur:
   - **Application restrictions: None.** Jangan pilih *Android apps* — Retrofit tidak
     mengirim identitas aplikasi Android sehingga request akan ditolak (403).
   - **API restrictions: Restrict key**, centang **Books API**, lalu **Save**.
5. Uji key di browser sebelum menulis kode:

   ```text
   https://www.googleapis.com/books/v1/volumes?q=kotlin&key=API_KEY_ANDA
   ```

   Respons yang benar berisi `totalItems` dan `items`.

## 2. Menyimpan API key

API key dibaca dari `local.properties` di root project (file ini **tidak** masuk git):

```properties
sdk.dir=/path/ke/Android/Sdk
BOOKS_API_KEY=AIza...
BOOKS_BASE_URL=https://www.googleapis.com/books/v1/
```

`app/build.gradle.kts` meneruskan kedua nilai itu ke `BuildConfig.API_KEY` dan
`BuildConfig.BASE_URL`. Setelah mengubah `local.properties`, jalankan Gradle Sync
(atau `./gradlew assembleDebug`) sebelum Run.

Kalau `BOOKS_API_KEY` kosong, project tetap bisa di-build: Gradle hanya memberi
peringatan dan aplikasi menampilkan pesan "API key belum diisi" tanpa memanggil
jaringan.

## 3. Menjalankan

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Atau tekan **Run** di Android Studio, ketik kata kunci (mis. `kotlin`), tekan
**Cari**, lalu klik salah satu buku untuk membuka halaman detail.

## Struktur

| File | Isi |
| --- | --- |
| `data/BookModel.kt` | Model JSON Google Books (`BooksResponse`, `BookItem`, `VolumeInfo`, `ImageLinks`) |
| `network/GoogleBooksApi.kt` | Interface Retrofit endpoint `volumes` |
| `network/ApiClient.kt` | Konfigurasi Retrofit + OkHttp |
| `BookViewModel.kt` | State pencarian (`results`, `isLoading`, `error`) |
| `MainActivity.kt` | Layar pencarian dan daftar buku |
| `DetailActivity.kt` | Halaman detail buku |

## Catatan

- Jangan commit `local.properties` atau API key.
- Google Books mengirim URL cover dengan `http://`; aplikasi mengubahnya menjadi
  `https://` karena cleartext diblokir sejak Android 9.
- Logging OkHttp hanya aktif pada build debug.
