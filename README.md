# GlowScan
## Brief abstract

**Glowscan** is an Android skincare companion app, internally packaged as **ClearCanvas**, built with **Kotlin and Jetpack Compose**. Its main purpose is to scan a user’s face, estimate their skin type and concerns, then provide personalized skincare guidance.

### Main user flow

1. **Authentication**
   - Users can sign up and log in with email and password.
   - Firebase Authentication handles accounts.
   - Basic user information is stored in Firebase Realtime Database.
   - Password-reset functionality is included.

2. **Home dashboard**
   - Shows a personalized greeting and skincare feature cards.
   - Provides access to face scanning, the product library, journal, profile, and dermatologist contacts.
   - Includes camera-permission handling and animated UI elements.

3. **Face scanning**
   - Uses Android CameraX to display the camera preview.
   - Uses a face-detection analyzer to ensure a face is visible before allowing capture.
   - Supports front/back camera switching and flash control.
   - Captured images are stored temporarily through `ImageViewModel`.

4. **Skin analysis**
   - The captured image is resized and converted into a TensorFlow Lite input tensor.
   - `SkinAnalysisAI` attempts to load `skin_analysis_model.tflite`.
   - The model is expected to classify:
     - Skin type: oily, dry, combination, sensitive, or normal
     - Hydration level
     - Texture score
     - Possible concerns such as acne, dryness, oiliness, redness, or wrinkles
   - If the model is unavailable or analysis fails, the app falls back to randomized/rule-based results.

5. **Results and recommendations**
   - The result screen displays the predicted skin type, confidence score, concerns, hydration, and texture information.
   - It selects skincare products from a local `SkincareData` product map based mainly on skin type.
   - Three products are shuffled and shown as recommendations.
   - Users can track the result in a skincare journal or consult listed dermatologists.

6. **Additional features**
   - A local journal backed by SQLite.
   - A product library.
   - A dermatologist directory with phone and WhatsApp contact actions.
   - Profile and navigation screens.
   - A custom Compose theme with animated, skincare-oriented visuals.

### Architecture

The app is organized into:

- `screens/` — Jetpack Compose UI screens
- `navigation/` — Navigation routes and app flow
- `ai/` — TensorFlow Lite skin-analysis logic
- `analyzer/` — Camera/face-detection processing
- `data/` — Analysis models, product data, and journal database
- `viewmodel/` — Temporary image and journal state
- `ui/` — Theme and styling

### Important implementation note

The app’s intended core feature is AI-based skin analysis, but the current code has a fallback that generates randomized results. The results screen also contains a separate random-result fallback and randomly shuffles recommended products. Therefore, unless the TensorFlow Lite model is correctly bundled and produces valid output, the displayed analysis may not represent the captured image accurately.

In short, **Glowscan is a visually polished skincare assistant that combines Firebase authentication, camera-based face capture, TensorFlow Lite analysis, local skincare data, product recommendations, journaling, and dermatologist referrals in a single Android app**.