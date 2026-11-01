# Adera Delivery (አደራ ዴሊቨሪ) 🍲🇪🇹

> **Authentic Ethiopian Food Delivery Android Application**  
> Built with modern **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Firebase Firestore + Google Sign-In**.

---

## 📱 Project Overview

**Adera Delivery** is a responsive, mobile-first food delivery application tailored for authentic Ethiopian cuisine in Addis Ababa and beyond. Customers can browse traditional and modern Ethiopian restaurants, explore categorized dishes (such as *Doro Wat*, *Kitfo*, *Beyaynet*, *Tibs*, and *Shiro*), customize spice levels, save favorites, manage a shopping cart with promo codes, securely check out via Google Sign-In, and track orders in real time.

---

## ✨ Key Features

### 1. 🏠 Home Screen & Discovery
* **Addis Ababa Neighborhood Selector:** Switch delivery locations across popular zones (Bole Medhanialem, Kazanchis, Piassa, Sarbet, CMC, Old Airport).
* **Promotional Banners:** Interactive discounts and promotional campaigns (e.g., Fasting Season Specials, Weekend Tibs Feast).
* **Cuisine Categories:** Quick filtering by *Traditional Wats*, *Kitfo & Grills*, *Vegetarian / Fasting (የጾም)*, *Breakfast & Chechebsa*, and *Coffee & Drinks*.
* **Top Restaurants:** High-rating authentic cultural restaurants (Yod Abyssinia, 2000 Habesha, Kategna, Dashen, Gursha Lounge).
* **Popular Dishes:** Recommended Ethiopian culinary staples with spice badges and ETB pricing.

### 2. 🔍 Smart Search & Categorized Filters
* Real-time search across restaurants, cuisines, and dish names.
* Category chip filters and dietary toggles (Vegetarian / Fasting (የጾም), Spicy Only).
* Empty state guidance with instant clear-filter actions.

### 3. 🍲 Interactive Food Details & Customization
* Full dish photography, cultural description, and allergen / preparation notes.
* Spice level customization: **Mild (አልጫ)**, **Medium (መካከለኛ)**, and **Mitmita Spicy (በጣም የሚያቃጥል 🌶️)**.
* Special instructions input (e.g., extra injera, less butter / niter kibbeh).
* Add-to-cart with dynamic quantity and price computation in Ethiopian Birr (ETB).

### 4. ❤️ Favorites System (Dishes & Restaurants)
* Toggle favorite dishes and restaurants directly on cards.
* Dedicated **My Favorites** screen with quick tab switching between dishes and dining spots.
* Dual persistence: Local storage for guest mode + automatic Firebase Firestore cloud sync upon sign-in.

### 5. 🛒 Shopping Cart & Discounts
* Itemized cart with quantity adjustment, spice level indicators, and one-tap item removal.
* Promo code support (e.g., use promo code `ADERA` or `HABESHA` for 50 ETB off).
* Live calculation of Subtotal, Delivery Fee, Discount, and Grand Total.

### 6. 🔐 Google Sign-In Protected Checkout
* **Authentication Gating:** Checkout is strictly protected—guests are prompted to sign in with their Google account to confirm delivery credentials and submit orders.
* **Jetpack Credential Manager:** Seamless, native one-tap Google Sign-In (`GetSignInWithGoogleOption`) without legacy GoogleApiClient overhead.
* **Auto Customer Pre-fill:** Authenticated customer full name and email are automatically linked to orders.

### 7. 💳 Checkout & Multi-Payment Options
* Multiple payment methods standard in Ethiopia:
  * **Cash on Delivery (COD)**
  * **Telebirr (ቴሌብር)**
  * **CBE Birr (የኢትዮጵያ ንግድ ባንክ)**
* Delivery instructions and phone number validation.

### 8. 🚚 Live Order Tracking & Reorder
* Real-time 4-stage tracking status:
  1. **Order Received (ትዕዛዝ ተቀብለናል)**
  2. **Preparing Food (ምግብ በመዘጋጀት ላይ ነው)**
  3. **Ready for Delivery (ለማድረስ ዝግጁ ሆኗል)**
  4. **Delivered (ምግቡ ደርሷል - መልካም ምግብ!)**
* Progress stepper with animated status indicators.
* One-tap **Reorder** functionality to quickly re-populate the cart.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Language** | Kotlin 2.0+ (100% Kotlin) |
| **UI Framework** | Jetpack Compose + Material Design 3 (M3) |
| **State Management** | Android Architecture Components (`ViewModel`, `StateFlow`, `collectAsStateWithLifecycle`) |
| **Authentication** | Firebase Authentication + Google Sign-In via Jetpack `CredentialManager` |
| **Cloud Storage** | Firebase Firestore (zero-trust security rules with per-user data isolation) |
| **Local Persistence** | Android `SharedPreferences` + JSON serialization (offline-first fallback) |
| **Design System** | Custom Ethiopian-themed palette (Emerald Green `#2E7D32`, Gold `#FFC107`, Injera Warm neutrals) |
| **Testing** | JUnit 4 + Robolectric local JVM testing |

---

## 📂 Project Architecture & Directory Structure

```
adera-delivery/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt               # Single Activity entry point with Edge-to-Edge
│   │   │   │   ├── auth/
│   │   │   │   │   └── AuthManager.kt            # Jetpack CredentialManager & Google Sign-In handler
│   │   │   │   ├── data/
│   │   │   │   │   ├── AderaRepository.kt        # Dual storage repository (Local + Firestore sync)
│   │   │   │   │   └── SampleData.kt             # Ethiopian restaurants, categories, and food menu items
│   │   │   │   ├── model/
│   │   │   │   │   └── Models.kt                 # Restaurant, FoodItem, CartItem, Order, Category
│   │   │   │   ├── ui/
│   │   │   │   │   ├── AderaApp.kt               # Root scaffold, navigation router, and dialog controller
│   │   │   │   │   ├── HomeScreen.kt             # Banners, categories, top spots, recommended dishes
│   │   │   │   │   ├── RestaurantScreen.kt       # Restaurant cover, info, and categorised food menu
│   │   │   │   │   ├── SearchScreen.kt           # Search & filter UI
│   │   │   │   │   ├── FoodDetailDialog.kt       # Modal sheet for spice customization and ordering
│   │   │   │   │   ├── CartScreen.kt             # Cart list, bill details, promo code, Google-gated checkout
│   │   │   │   │   ├── CheckoutScreen.kt         # Address, phone, customer info, payment methods
│   │   │   │   │   ├── OrderTrackingScreen.kt    # 4-stage live stepper, active & past order history
│   │   │   │   │   ├── FavoritesScreen.kt        # Favorite dishes & restaurants tabs
│   │   │   │   │   ├── ProfileScreen.kt          # User card, Google Sign-in/out, cloud sync, address picker
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── CommonComponents.kt   # AppHeader, FoodItemCard, RestaurantCard, Stepper
│   │   │   │   │   │   └── LoginDialog.kt        # Google Sign-In dialog modal
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt              # Emerald Green, Gold, Spicy Red, Vegetarian Green
│   │   │   │   │       ├── Theme.kt              # Material 3 light/dark color schemes
│   │   │   │   │       └── Type.kt               # Typography scale
│   │   │   │   └── viewmodel/
│   │   │   │       └── AderaViewModel.kt         # Centralized state management & UI action handlers
│   │   │   ├── res/                              # Drawables (food images, banners, icons), strings, XMLs
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/example/                # Unit & Robolectric JVM tests
│   ├── build.gradle.kts                          # App-level dependencies & build configuration
│   └── google-services.json                      # Firebase configuration
├── firestore.rules                               # Zero-trust security rules for cloud sync
├── metadata.json                                 # AI Studio platform configuration
└── README.md                                     # Project documentation
```

---

## 🍽️ Featured Ethiopian Cuisine

Adera Delivery comes preloaded with authentic dishes celebrated across Ethiopia:

* **Doro Wat (ዶሮ ወጥ):** Slow-cooked tender chicken drumstick and hard-boiled egg in rich, spicy berbere stew served with soft injera.
* **Special Kitfo (ክትፎ):** Minced prime beef warmed in spiced clarified butter (*niter kibbeh*) and *mitmita*, served with *ayib* (cottage cheese) and *gomen*.
* **Yebeg Tibs (የበግ ጥብስ):** Sautéed tender lamb cubes with onions, garlic, rosemary, and jalapeños served sizzling hot in a traditional clay *shekla*.
* **Beyaynetu (የጾም በያይነቱ):** Colorful vegan platter with Shiro, Misir Wat, Kik Alicha, Gomen, Atkilt Wot, and beetroot salad spread over fresh teff injera.
* **Shiro Tegabino (ሽሮ ተጋቢኖ):** Thick, bubbling chickpea flour stew simmered with garlic and Ethiopian spices in a hot clay pot.
* **Special Firfir with Quanta (ቁልቋል ቋንጣ ፍርፍር):** Shredded injera simmered in spicy berbere sauce with savory sun-dried spiced beef.
* **Chechebsa / Kita Firfir (ጨጨብሳ):** Torn pan-fried unleavened bread sautéed with honey and seasoned *niter kibbeh*, served with plain yogurt.
* **Traditional Ethiopian Coffee Ceremony (የኢትዮጵያ ቡና):** Freshly roasted Arabica coffee brewed in a clay *jebena*, accompanied by fresh popcorn.

---

## 🚀 Getting Started & How to Run

### Prerequisites
* **Android Studio** (Ladybug / Hedgehog or newer recommended)
* **JDK 17+**
* Android SDK with **compileSdk 36** (minSdk 24 - Android 7.0+)

### Building the Project
From the repository root directory:

```bash
# Clean build and compile debug APK
gradle assembleDebug

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

### Running on a Device or Emulator
1. Open the project in Android Studio.
2. Let Gradle sync project dependencies.
3. Select an Android device or emulator running API 24 or higher.
4. Click **Run 'app'** (`Shift + F10`).

---

## 🔒 Firebase & Security Architecture

1. **Authentication:**
   - Handled via `AuthManager` using Jetpack `CredentialManager` and `GetSignInWithGoogleOption`.
   - The user's Google ID Token is authenticated with Firebase Auth.

2. **Database & Firestore Rules:**
   - Firestore houses persistent collections:
     - `/users/{userId}/orders/{orderId}`: User's placed orders.
     - `/users/{userId}/favorites/data`: Saved dish and restaurant IDs.
   - Guarded by zero-trust rules in `firestore.rules`:
     ```text
     match /users/{userId}/{document=**} {
       allow read, write: if request.auth != null && request.auth.uid == userId;
     }
     ```
   - Only authenticated users can read or write their own documents.

---

## 🤝 Contributing & License

Contributions, suggestions, and improvements are welcome! Feel free to fork the repository, open an issue, or submit a pull request.

*Developed with ❤️ for Ethiopian food lovers.*
