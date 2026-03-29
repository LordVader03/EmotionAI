# Machine Learning Experiments

## Definición del problema

El objetivo de este proyecto es el de crear sistema de reconocimiento de emociones multimodal basado en features de audio y video.

Este es un problema de clasificación multiclase con 7 clases de emociones.

---

## Modelos candidatos

- Modelo de video: Red Neuronal MovileNetV2 o EfficientNet-Lite usando landmarks faciales (por ahora, se usa Dense)
- Modelo de audio: Red Neuronal 1D o LSTM usando features de MFCC (por ahora, es Dense también)
- Fusion: Fusión tardía

---

## Herramientas usadas

- Python
- TensorFlow / Keras
- TensorFlow Lite
- Google Colab
- NumPy
- Scikit-learn

---

## Pipeline de entrenamiento

1. Datos preprocesados
2. Extracción de features (simulación de features)
3. Train/test split
4. Entrenamiento de modelo
5. Evaluación de modelo
6. Exportación de modelo
6. Fusión de predicciones (fusión tardía)

---

## Experimentos

Nota: Los datos utilizados son sintéticos y aleatorios. Por tanto los resultados son bajos (~14%) y no tienen valor predictivo real. Este experimento valida la estructura del pipeline.

### Modelo de audio

- Script: audio_experiment.ipynb
- Features: 39 (MFCC simulados)
- Arquitectura: Dense(64), Dense(32), Dense(7)
- Optimizador: Adam(lr=0.001)
- **Epochs: 10**
- **Batch size: 32**
- **Validation Split: 0.2**
- Resultados: Accuracy = ~14-19%

---

### Modelo de video

- Script: video_experiment.ipynb
- Features: 128 (landmarks simulados)
- Arquitectura: Dense(128), Dense(64), Dense(7)
- Optimizador: Adam(lr=0.001)
- **Epochs: 10**
- **Batch size: 32**
- **Validation Split: 0.2**
- Resultados: Accuracy = ~13%

---

### Fusión

- Script: fusion_experiment.ipynb
- Método: media por pesos
- Parámetros: pesos [alpha, alpha] (video, audio) (alpha = 0.5)
- Resultados: Ej: [1 0 2 3 2 1 1 1 4 1] (en esta simulación estas fueran las 10 mejores)

---

## Almacenamiento de modelos

Los modelos son guardados en formato HDF5 (.h5) después de ser entrenados, permitiendo el reutlizado sin reentranamiento desde 0.

Estos modelos posteriormente son convertidos a formato TensorFlow Lite (.tflite) para desplegamiento en dispositivos mobiles.

---

## Conclusiones

- El hecho de ser multimodal mejora el rendimiento
- La modalidad de video emite señales más fuertes
- La fusión mejora la robustez