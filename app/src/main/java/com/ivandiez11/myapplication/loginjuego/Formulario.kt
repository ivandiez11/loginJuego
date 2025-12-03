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

    // Referencias a los elementos visuales
    private lateinit var etNombreUsuario: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etFechaNacimiento: TextInputEditText
    private lateinit var spinnerTipoJuego: Spinner
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnIniciarSesion: Button

    // Estado de validaciones
    private var nombreValido = false
    private var emailValido = false
    private var passwordValida = false
    private var fechaValida = false
    private var tipoJuegoValido = false
    private var terminosAceptados = false

    // *** CORRECCIÓN: Se elimina el companion object. No es necesario. ***

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        // ============================
        // 1. Inicializar vistas
        // ============================
        etNombreUsuario = findViewById(R.id.etNombreUsuario)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        spinnerTipoJuego = findViewById(R.id.spinnerTipoJuego)
        cbTerminos = findViewById(R.id.cbTerminos)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)

        // ============================
        // 2. Configurar Spinner con placeholder
        // ============================
        val tiposDeJuego = listOf(
            "Selecciona...",   // placeholder
            "Rol",
            "Aventura",
            "Estrategia",
            "Acción",
            "Simulación",
            "Deportes",
            "Puzzle"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposDeJuego
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoJuego.adapter = adapter

        // ============================
        // 3. Listeners para validación
        // ============================
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etNombreUsuario.addTextChangedListener(watcher)
        etCorreo.addTextChangedListener(watcher)
        etContrasena.addTextChangedListener(watcher)
        etFechaNacimiento.addTextChangedListener(watcher)

        spinnerTipoJuego.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                validarCampos()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                validarCampos()
            }
        }

        cbTerminos.setOnCheckedChangeListener { _, isChecked ->
            terminosAceptados = isChecked
            validarCampos()
        }

        btnIniciarSesion.isEnabled = false

        // ============================
        // 4. DatePicker para fecha de nacimiento
        // ============================
        etFechaNacimiento.setOnClickListener {
            mostrarDatePicker()
        }

        // ============================
        // 5. Botón Iniciar Sesión
        // ============================
        btnIniciarSesion.setOnClickListener {
            // *** CORRECCIÓN PRINCIPAL ***
            // Se llama al método del singleton para actualizar sus datos.
            SesionUsuario.iniciarSesion(
                nombreUsuario = etNombreUsuario.text?.toString() ?: "",
                email = etCorreo.text?.toString() ?: "",
                birthDate = etFechaNacimiento.text?.toString() ?: "",
                favoriteGameType = spinnerTipoJuego.selectedItem.toString()
            )

            // Navegar a Inicio
            val intent = Intent(this, Inicio::class.java)
            // Estas flags limpian la pila de navegación para que el usuario no vuelva al formulario
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

            finish() // Cierra esta actividad (Formulario)
        }
    }

    // ============================
    // Función DatePicker
    // ============================
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
                // No es necesario llamar a validarCampos() aquí, el TextWatcher ya lo hace.
            },
            añoActual,
            mesActual,
            diaActual
        )

        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    // ============================
    // Validación centralizada
    // ============================
    private fun validarCampos() {
        val nombre = etNombreUsuario.text?.toString()?.trim() ?: ""
        nombreValido = nombre.isNotEmpty()

        val email = etCorreo.text?.toString()?.trim() ?: ""
        emailValido = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

        val pass = etContrasena.text?.toString() ?: ""
        passwordValida = pass.length >= 6

        val fecha = etFechaNacimiento.text?.toString()?.trim() ?: ""
        fechaValida = fecha.isNotEmpty()

        val pos = spinnerTipoJuego.selectedItemPosition
        tipoJuegoValido = pos > 0 // El placeholder está en la posición 0

        btnIniciarSesion.isEnabled = nombreValido && emailValido && passwordValida &&
                fechaValida && tipoJuegoValido && terminosAceptados
    }
}
