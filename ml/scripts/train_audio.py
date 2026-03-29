from tensorflow import keras
from tensorflow.keras import layers
from preprocess import load_data

X_audio, _, y = load_data()

model = keras.Sequential([
    layers.Dense(64, activation='relu'),
    layers.Dense(7, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(X_audio, y, epochs=5)

model.save("../models/audio_model.h5")