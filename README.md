# Fast Official Sri Lanka — Fast Deposit & Withdrawal App

**Fast Official Sri Lanka** යනු ශ්‍රී ලංකාවේ Betting පරිශීලකයින් සඳහා වේගවත්, සුරක්ෂිත සහ විශ්වසනීය තැන්පතු (Deposits) සහ මුදල් ලබාගැනීම් (Withdrawals) පහසු කරන නිල Android යෙදුමයි (Cashier Helper App).

---

## 🌙 100% Dark Theme Support

- පූර්ණයෙන්ම **දියුණු කළ Dark Mode** සහ **Light Mode** දෙකම සහය දක්වයි.
- සෑම තිරයකටම මෙම යෙදුම **ගැඹුරු ඉදිරිපත් කිරීම්** සහ **ඉතා සුමට අක්ෂර පදනම්කරණය** ලබා දෙයි.
- `Material 3` හි කറු පදනම් තේමාවන් හා **edge-to-edge adaptive UI** එකට ගැලපේ.
- දින/රැයේ භාවිතය සඳහා සුවපහසු පරිසරයක් සහ පළපුරුදු UI පාලක රටාවන්.

---

## 🌟 ප්‍රධාන විශේෂාංග (Key Features)

### 1. ⚡ වේගවත් තැන්පතු (Fast Deposits)
- ලංකාවේ ප්‍රධාන බැංකු හරහා තැන්පතු කිරීමේ පහසුකම (**Bank of Ceylon, Commercial Bank, Sampath Bank, Hatton National Bank, People's Bank**).
- ගෙවීම් රිසිට්පත් (Deposit Receipts) කෙළින්ම ඇප් එක හරහා Upload කිරීමේ හැකියාව.
- **1xBet Player ID** එක යොදා විනාඩි 5–15ක් ඇතුළත ගිණුමට මුදල් එකතු කරගැනීමේ පහසුකම.

### 2. 💸 ආරක්ෂිත මුදල් ලබාගැනීම (Secure Withdrawals)
- **1xBet Cash** (Address: *Walasmulla, Beliaththa Road 24/7*) හරහා ඉල්ලුම් කර ලබාගන්නා **ඉලක්කම් 4ක Secret Code** එක යොදා ආරක්ෂිතව මුදල් බැංකු ගිණුමට ගෙවා ගැනීම.
- අවම සීමාව: **LKR 1,000** | උපරිම සීමාව: **LKR 500,000**.

### 3. 🔥 150% විශේෂ Promo Banner & Code (`VGSL`)
- ප්‍රධාන තිරයේ උඩින්ම පිහිටි **VGSL Promo Banner** එක මගින් 1xBet හි ලියාපදිංචි වීමේදී **150% Welcome Bonus** ලබාගැනීමට 1-Tap Copy පහසුකම.
- සෘජු ලියාපදිංචි වීමේ ලින්ක් එක (1xBet Registration Link).

### 4. 📞 24/7 පාරිභෝගික සහාය (24/7 Customer Support)
- **WhatsApp Live Chat** (+94765865387) සහ **Telegram Official Support** (@fast_xbet_cashier) වෙත සෘජුවම සම්බන්ධ වීමේ පහසුකම.

### 5. ❓ නිතර අසන ප්‍රශ්න (FAQ Accordion) & User Guide
- තැන්පතු/ලබාගැනීම්, Bank Transfer Remarks, සහ Secret Code ලබාගන්නා ආකාරය පිළිබඳ සම්පූර්ණ තොරතුරු අඩංගු **Interactive FAQ Accordion**.
- පියවරෙන් පියවර උපදෙස් ඇතුළත් **User Guide**.

### 6. 🌐 ත්‍රිභාෂා සහාය (Trilingual Support)
- **සිංහල (Sinhala)**, **English**, සහ **தமிழ் (Tamil)** භාෂා 3ම සඳහා 100% පරිපූර්ණ සහාය.

---

## 🛠️ තාක්ෂණික පිරිවිතර (Technical Stack)

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** Clean Architecture / MVVM (Model-View-ViewModel)
- **Local Database:** Room Database with KSP
- **Asynchronous Flow:** Kotlin Coroutines & Flow
- **Navigation:** Jetpack Type-Safe Navigation Compose
- **Design System:** Custom Dark/Light dynamic colors with full dark mode optimization

---

## 📂 ව්‍යාපෘති ව්‍යූහය (Project Structure)

```text
app/src/main/java/com/example/
├── data/
│   ├── CashierRepository.kt       # Repository pattern for local storage
│   └── local/
│       ├── AppDatabase.kt         # Room Database initialization
│       ├── dao/                   # Data Access Objects (TransactionDao, BankDao)
│       └── entity/                # Room Entities (TransactionEntity, BankEntity)
└── ui/
    ├── components/
    │   ├── FaqAccordionComponent.kt # Animated FAQ Accordion UI
    │   └── SupportDialog.kt         # WhatsApp & Telegram Contact Modal
    ├── screens/
    │   ├── HomeScreen.kt          # Main Dashboard with Promo Banner & Quick Actions
    │   ├── DepositScreen.kt       # Bank selection, deposit submission & support
    │   ├── WithdrawalScreen.kt    # Secret Code validation & withdrawal request
    │   ├── HistoryScreen.kt       # Transaction status tracking (Pending/Approved)
    │   ├── UserGuideScreen.kt     # Step-by-step guides & FAQ accordion
    │   └── PrivacyPolicyScreen.kt # Terms & privacy policy details
    ├── theme/                     # Material 3 Color palette & Typography
    └── viewmodel/
        └── CashierViewModel.kt    # Main ViewModel managing app state & database operations
```

---

[![Build Android APK](https://github.com/Lakmal2078/fast-xbet-official-cashier/actions/workflows/build.yml/badge.svg)](https://github.com/Lakmal2078/fast-xbet-official-cashier/actions/workflows/build.yml)

## 🚀 ධාවනය කරවන ආකාරය (How to Run)

1. **Android Studio** (Ladybug / Jellyfish හෝ ඊට ඉහළ) භාවිතයෙන් මෙම ව්‍යාපෘතිය open කරන්න.
2. Gradle dependencies ස්වයංක්‍රීයව sync වන තෙක් රැඳී සිටින්න.
3. Android Emulator එකක් හෝ භෞතික දුරකථනයක් (Physical Device) යොදාගෙන **Run (Shift + F10)** ඔබන්න.

## 🔐 SECURITY NOTES

- Backend authentication is required for production. The app currently supports a client-side `TokenProvider` abstraction that returns no token by default.
- Do not commit secrets, API keys, or admin PIN values into source control.
- `ADMIN_PIN_HASH` is injected via build config and is only a weak local safeguard. Production admin access should be verified by the backend.
- Database encryption uses SQLCipher with a runtime-generated passphrase stored via AndroidX Security. Do not replace this with a hard-coded passphrase.
- Release build minification is enabled in `app/build.gradle.kts`; keep ProGuard/R8 rules updated when adding reflection-heavy libraries.

---

© 2026 **Fast Xbet Official Sri Lanka**. සියලුම හිමිකම් ඇවිරිණි.
