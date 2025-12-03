package com.ivandiez11.myapplication.loginjuego

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Juego : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // El tablero solo es visual
        // No hay lógica ni interacción
    }
}
