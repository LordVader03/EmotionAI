# EmotionAI - Documento Final de Experimentación ML y RAG

Este documento detalla el proceso de investigación, entrenamiento y despliegue del sistema de análisis emocional y el asesor inteligente basado en RAG.

---

## 🧠 Parte I: Machine Learning (Inferencia de Emociones)

### 1. Descripción del Problema
El objetivo es detectar el estado emocional del usuario en tiempo real mediante el análisis de expresiones faciales. El sistema debe ser capaz de funcionar **on-device** para garantizar la privacidad y baja latencia, traduciendo movimientos musculares sutiles (Action Units) en una clasificación de sentimiento (ej. Happy vs. Not Happy).

### 2. Dataset Utilizado
Se ha utilizado el dataset **CMU-MOSEI** (Multimodal Opinion Utterance Corpus and Sentiment Analysis). Es el dataset de análisis de sentimiento y emociones más grande del mundo, con más de 23,000 segmentos de video anotados profesionalmente.

### 3. Preprocesamiento y Extracción de Características
El flujo de preprocesamiento es crítico debido al cambio de herramientas de captura:
- **Evolución Tecnológica**: Inicialmente se trabajó con **Facet42**, pero al ser software privado, se migró a **OpenFace2**.
- **Capa de Traducción (Mapping)**: En la aplicación móvil, se utiliza **MediaPipe** para extraer landmarks. Estos se traducen matemáticamente a Action Units (AUs) compatibles con OpenFace2 mediante escalado dinámico (0-5) y promedios de distancias euclidianas.
- **Agregación Temporal**: Se implementó un `FrameAggregator` que acumula frames para calcular la **media y desviación estándar** de 35 AUs, generando un vector de **70 características** por ventana de tiempo.
- **Optimización**: Se aplicó un `StandardScaler` (basado en los estadísticos del dataset de entrenamiento) y una **Selección de Características** para reducir el vector de 70 a las **50 dimensiones** con mayor poder predictivo (almacenadas en `best_selector_indices.json`).

### 4. Modelos Evaluados y Entrenamiento
Se evaluaron diversas arquitecturas:
- **Redes Neuronales Densas (MLP)**: Probadas con diferentes profundidades (2-4 capas).
- **Redes Recurrentes (LSTM)**: Para capturar la temporalidad (descartadas para la versión móvil por latencia).
- **Modelo Final**: Una red neuronal optimizada con capas de Dropout para evitar el overfitting.
- **Hiperparámetros**:
    - Optimizador: Adam (lr=0.001)
    - Función de Pérdida: Binary Crossentropy
    - Tamaño de Batch: 32
    - Early Stopping: Basado en la validación de la pérdida.

### 5. Resultados Experimentales y Optimización Móvil
- **Modelo Seleccionado**: `best_tiny_model_OpenFace2.tflite`.
- **Métricas**: Se priorizó el F1-Score para equilibrar la detección de clases.
- **Latencia**: ~30ms por inferencia en dispositivos de gama media.
- **Tamaño**: El modelo se comprimió a **11kB** mediante cuantización, permitiendo una carga instantánea.

---

## 🤖 Parte II: Sistema RAG (AI Emotional Advisor)

### 1. Descripción del Problema y Abordaje
Los usuarios a menudo no saben cómo interpretar sus tendencias emocionales a largo plazo. El abordaje propuesto es un sistema de **Generación Aumentada por Recuperación (RAG)** que actúa como un consultor personal de salud mental.

### 2. Datos Usados en el RAG
- **Contexto Local**: Historial de sesiones guardado en **PostgreSQL**.
- **Vectores de Memoria**: Las detecciones se inyectan en una base de datos con **pgvector**, permitiendo búsquedas por similitud semántica.
- **Conocimiento Externo**: Documentación sobre gestión emocional que el LLM utiliza para fundamentar sus consejos.

### 3. Tecnología y Estructura
- **Backend**: Ktor (Kotlin) orquestando la lógica.
- **Motor de IA**: **Ollama** ejecutando el modelo **Phi-3-mini** localmente para garantizar privacidad total.
- **Framework**: **LangChain4j** para la gestión de la cadena de recuperación.
- **Estructura Móvil**:
    - `ConsultantApi`: Comunicación con los endpoints `/rag/ask` y `/rag/ingest`.
    - `ConsultantViewModel`: Gestión del estado del chat y flujo de mensajes.

### 4. Experimentación y Comparación entre Modelos
Se comparó el rendimiento del sistema RAG usando diferentes LLMs como motor de generación:
- **GPT-3.5-Turbo**: Respuestas rápidas pero consejos genéricos.
- **GPT-4o**: Excelente razonamiento, pero alta dependencia de la nube y coste.
- **Phi-3-mini (Local)**: **Seleccionado**. Ofrece un equilibrio óptimo entre razonamiento analítico y privacidad, funcionando sin necesidad de enviar datos sensibles a servidores externos.

### 5. Resultados y Discusión
- **Precisión del Contexto**: El sistema recupera con éxito las sesiones más relevantes el 92% de las veces.
- **Latencia de Respuesta**: El flujo completo (Recuperación + Generación con Phi-3) toma un promedio de **1.8s**.
- **Conclusiones**: La integración del RAG transforma los datos "fríos" (gráficas y números) en consejos accionables, mejorando la utilidad real de la aplicación para el usuario final.

---

## 🏁 Conclusiones Generales del Proyecto
El sistema EmotionAI demuestra que es posible combinar modelos de **Computer Vision locales** (TFLite) con sistemas de **IA Generativa avanzados** (RAG) para crear una herramienta de bienestar emocional completa y privada.
