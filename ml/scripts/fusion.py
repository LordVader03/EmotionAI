def fuse(audio_pred, video_pred, alpha=0.5):
    return alpha * audio_pred + (1 - alpha) * video_pred