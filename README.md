# EmotionAI - Neural Analysis & Emotional Advisor

EmotionAI is a professional-grade Android application that combines **On-Device Machine Learning** with a **RAG (Retrieval-Augmented Generation) AI Consultant** to provide real-time emotional analysis and personalized mental well-being insights.

## 🚀 Features

- **Real-Time Detection**: Uses MediaPipe and LiteRT (TFLite) to analyze facial expressions and Action Units (AUs) locally.
- **Mission History**: Securely stores detection sessions in a PostgreSQL backend.
- **Evolution Reports**: Generates professional PDF reports with happiness evolution graphs.
- **AI Emotional Advisor**: A RAG-powered chatbot that uses your local emotion history to provide personalized advice (powered by Ollama + Phi-3).
- **Secure Access**: JWT-based authentication system.
- **Military-Grade UI**: A high-contrast, dark-themed interface built with Jetpack Compose.

---

## 🛠️ Technology Stack

- **Mobile**: Kotlin, Jetpack Compose, CameraX, LiteRT, MediaPipe, Retrofit, DataStore.
- **Backend**: Ktor (Kotlin), PostgreSQL + pgvector.
- **AI/ML**: Ollama (Phi-3-mini), LangChain4j for RAG.
- **Network**: Tailscale VPN for secure backend communication.

---

## 📋 Prerequisites

1.  **Android Studio** (Ladybug or newer).
2.  **Ollama** installed on your server/PC.
3.  **PostgreSQL** with the `pgvector` extension enabled.
4.  **Tailscale** (if running the backend on a private network).
5.  **JDK 17+**.

---

## ⚙️ Setup & Execution

### 1. Backend Setup
1.  Ensure your Ktor backend is running and connected to PostgreSQL.
2.  Install and run Ollama with the Phi-3 model:
    ```bash
    ollama run phi3
    ```
3.  Update the `BASE_URL` in `RetrofitClient.kt` if your server IP is different:
    ```kotlin
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://100.85.9.120:8080/") // Your Tailscale or Local IP
            .build()
    }
    ```

### 2. Network Security
The app is configured to allow HTTP traffic for private IPs. If you change the server IP, update the `network_security_config.xml`:
```xml
<domain includeSubdomains="true">YOUR_NEW_IP</domain>
```

### 3. Running the App
1.  Connect an Android device (Physical device recommended for Camera/Tailscale).
2.  Build the project: `./gradlew assembleDebug`.
3.  Install and run: `Run 'app'` in Android Studio.

---

## 📂 Project Structure

- `app/src/main/java/.../ml`: ML inference and preprocessing logic.
- `app/src/main/java/.../ui`: Jetpack Compose screens (Auth, Home, Detection, Sessions, Consultant).
- `app/src/main/java/.../data`: Repository pattern and Retrofit API services.
- `app/src/main/java/.../viewmodel`: State management via LiveData/Flow.

---

## 🧪 Experimental Results

- **Inference Latency**: ~30ms per frame on mid-range devices.
- **RAG Accuracy**: High contextual relevance by retrieving up to 20 past emotional logs.
- **Memory Footprint**: Optimized TFLite model (~5MB) for low resource consumption.

---

## 🛡️ License
EXPERIMENTAL BUILD - DO NOT DISTRIBUTE. Developed for EmotionAI Neural Systems.
