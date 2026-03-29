from tensorflow import keras
from tensorflow.keras import layers
from preprocess import load_data

_, X_video, y = load_data()

model = keras.Sequential([
    layers.Dense(128, activation='relu'),
    layers.Dense(7, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

model.fit(X_video, y, epochs=5)

model.save("../models/video_model.h5")