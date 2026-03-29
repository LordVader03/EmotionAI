from preprocess import load_data
from tensorflow import keras

X_audio, X_video, y = load_data()

audio_model = keras.models.load_model("../models/audio_model.h5")
video_model = keras.models.load_model("../models/video_model.h5")

audio_pred = audio_model.predict(X_audio)
video_pred = video_model.predict(X_video)

final_pred = 0.5 * audio_pred + 0.5 * video_pred

print("Evaluation complete")