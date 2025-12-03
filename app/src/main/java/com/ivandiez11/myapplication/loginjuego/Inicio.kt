package com.ivandiez11.myapplication.loginjuego

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ivandiez11.myapplication.loginjuego.model.SesionUsuario

class Inicio : AppCompatActivity() {

    // Cambia los nombres de las variables para que reflejen los IDs (opcional pero recomendado)
    private lateinit var tvBienvenida: TextView
    private lateinit var btnSesion: Button
    private lateinit var btnJugar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // *** CORRECCIÓN ***
        // Inicializar vistas con los IDs correctos del XML
        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnSesion = findViewById(R.id.btnSesion)
        btnJugar = findViewById(R.id.btnJugar)

        actualizarUI()

        // Botón Iniciar Sesión / Cerrar Sesión
        btnSesion.setOnClickListener { // <-- Cambiado aquí
            if (!SesionUsuario.isLoggedIn) {
                val intent = Intent(this, Formulario::class.java)
                startActivity(intent)
            } else {
                SesionUsuario.cerrarSesion()
                actualizarUI()
            }
        }

        // Botón Jugar Ahora
        btnJugar.setOnClickListener { // <-- Cambiado aquí
            if (SesionUsuario.isLoggedIn) {
                val intent = Intent(this, Juego::class.java)
                startActivity(intent)
            }
        }
    }

    private fun actualizarUI() {
        if (SesionUsuario.isLoggedIn) {
            tvBienvenida.text = "Bienvenido al ajedrez, ${SesionUsuario.username}!"
            btnSesion.text = "Cerrar Sesión"
            btnJugar.isEnabled = true
        } else {
            tvBienvenida.text = "¡Bienvenido al ajedrez!"
            btnSesion.text = "Iniciar Sesión"
            btnJugar.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarUI()
    }
}
