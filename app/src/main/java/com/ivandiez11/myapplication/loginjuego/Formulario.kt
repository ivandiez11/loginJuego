package com.ivandiez11.myapplication.loginjuego

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.ivandiez11.myapplication.loginjuego.model.SesionUsuario
import java.util.Calendar

class Formulario : AppCompatActivity() {

    private lateinit var etNombreUsuario: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etFechaNacimiento: TextInputEditText
    private lateinit var spinnerTipoJuego: Spinner
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnIniciarSesion: Button
    private lateinit var btnVolverInicio: Button
    private lateinit var tvErrores: TextView

    private var nombreValido = false
    private var emailValido = false
    private var passwordValida = false
    private var fechaValida = false
    private var edadValida = false
    private var tipoJuegoValido = false
    private var terminosAceptados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        // Inicializar vistas
        etNombreUsuario = findViewById(R.id.etNombreUsuario)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        spinnerTipoJuego = findViewById(R.id.spinnerTipoJuego)
        cbTerminos = findViewById(R.id.cbTerminos)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)
        tvErrores = findViewById(R.id.tvErrores)

        btnIniciarSesion.isEnabled = false
        btnVolverInicio.isEnabled = true

        // Spinner
        val tiposDeJuego = listOf(
            "Selecciona...", "Rol", "Aventura", "Estrategia",
            "Acción", "Simulación", "Deportes", "Puzzle"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposDeJuego)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoJuego.adapter = adapter

        // TextWatcher que actualiza validación en tiempo real
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validarCampos() }
            override fun afterTextChanged(s: Editable?) {}
        }

        etNombreUsuario.addTextChangedListener(watcher)
        etCorreo.addTextChangedListener(watcher)
        etContrasena.addTextChangedListener(watcher)
        etFechaNacimiento.addTextChangedListener(watcher)

        spinnerTipoJuego.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { validarCampos() }
            override fun onNothingSelected(parent: AdapterView<*>?) { validarCampos() }
        }

        cbTerminos.setOnCheckedChangeListener { _, isChecked ->
            terminosAceptados = isChecked
            validarCampos()
        }

        etFechaNacimiento.setOnClickListener { mostrarDatePicker() }

        // Botón Iniciar Sesión
        btnIniciarSesion.setOnClickListener {
            // Ya no se necesita validación adicional, todo se maneja en tiempo real
            SesionUsuario.iniciarSesion(
                nombreUsuario = etNombreUsuario.text?.toString() ?: "",
                email = etCorreo.text?.toString() ?: "",
                birthDate = etFechaNacimiento.text?.toString() ?: "",
                favoriteGameType = spinnerTipoJuego.selectedItem.toString()
            )
            val intent = Intent(this, Inicio::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Botón Volver al inicio
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val añoActual = calendario.get(Calendar.YEAR)
        val mesActual = calendario.get(Calendar.MONTH)
        val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                etFechaNacimiento.setText(fechaFormateada)
                validarCampos() // actualizar validación al elegir fecha
            },
            añoActual, mesActual, diaActual
        )

        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun validarCampos() {
        // Validaciones individuales
        val nombre = etNombreUsuario.text?.toString()?.trim() ?: ""
        nombreValido = nombre.isNotEmpty()

        val email = etCorreo.text?.toString()?.trim() ?: ""
        emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

        val pass = etContrasena.text?.toString() ?: ""
        passwordValida = pass.length >= 6

        val fecha = etFechaNacimiento.text?.toString()?.trim() ?: ""
        fechaValida = fecha.isNotEmpty()

        // Validación de edad >= 13 años
        edadValida = false
        if (fechaValida) {
            val partes = fecha.split("/")
            if (partes.size == 3) {
                val dia = partes[0].toIntOrNull() ?: 0
                val mes = partes[1].toIntOrNull() ?: 0
                val año = partes[2].toIntOrNull() ?: 0

                val today = Calendar.getInstance()
                var edad = today.get(Calendar.YEAR) - año
                if (today.get(Calendar.MONTH) + 1 < mes ||
                    (today.get(Calendar.MONTH) + 1 == mes && today.get(Calendar.DAY_OF_MONTH) < dia)) {
                    edad--
                }
                edadValida = edad >= 13
            }
        }

        tipoJuegoValido = spinnerTipoJuego.selectedItemPosition > 0

        // Botón habilitado solo si todo es válido
        btnIniciarSesion.isEnabled =
            nombreValido && emailValido && passwordValida &&
                    fechaValida && edadValida && tipoJuegoValido && terminosAceptados

        // Mostrar errores en tiempo real
        val errores = mutableListOf<String>()
        if (!nombreValido) errores.add("Nombre de usuario inválido")
        if (!emailValido) errores.add("Correo electrónico inválido")
        if (!passwordValida) errores.add("Contraseña inválida (mínimo 6 caracteres)")
        if (!fechaValida) errores.add("Fecha de nacimiento inválida")
        if (fechaValida && !edadValida) errores.add("Debes tener al menos 13 años")
        if (!tipoJuegoValido) errores.add("Selecciona un tipo de juego")
        if (!terminosAceptados) errores.add("Debes aceptar los términos")

        tvErrores.text = errores.joinToString("\n")
        tvErrores.visibility = if (errores.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
