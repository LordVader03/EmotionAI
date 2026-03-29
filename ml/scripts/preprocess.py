import numpy as np

def load_data():
    X_audio = np.random.rand(1000, 39)
    X_video = np.random.rand(1000, 128)
    y = np.random.randint(0, 7, 1000)
    return X_audio, X_video, y

if __name__ == "__main__":
    X_audio, X_video, y = load_data()
    print("Data loaded:", X_audio.shape, X_video.shape)