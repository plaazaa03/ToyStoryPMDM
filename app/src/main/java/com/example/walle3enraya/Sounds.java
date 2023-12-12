package com.example.walle3enraya;

import android.content.Context;
import android.media.MediaPlayer;

public class Sounds {

    private static MediaPlayer mediaPlayer;

    public static void playBackgroundMusic(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.hayunamigoenmi);
            mediaPlayer.setLooping(true); // Para que la música se reproduzca en bucle
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void stopBackgroundMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
