package com.example.walle3enraya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        //crear una tarea asyncrona
        //sobrescribe el metodo run() para indicar que hara cuando se ejecute

        TimerTask tarea = new TimerTask()
        {

            @Override
            public void run() {
                // Es un Intent especial, porque ejecuta otra actividad del proyecto (no externa)
                // Esto se sabe porque la extensión es .class ¿Ok?
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                // La ejecuta como si fuera un proceso (aunque no exactamente igual)
                // Lo veremos en clase porque hay muchas diferencias.
                startActivity(intent);

                //Termina
                finish();
            }
        };

        // Usa un temporizador para lanzar la tarea anterior
        // He puesto 3seg, poned lo que queráis.
        Timer timeOut = new Timer();
        timeOut.schedule(tarea, 3000);

    }
}
