package com.ivandiez11.myapplication.loginjuego.model

/**
 * Objeto Singleton para gestionar los datos de la sesión del usuario en toda la app.
 */
object SesionUsuario {

    // 1. Propiedades para almacenar todos los datos del formulario
    var username: String? = null
    var email: String? = null
    var birthDate: String? = null
    var favoriteGameType: String? = null
    var isLoggedIn: Boolean = false
        private set // Solo se puede modificar desde dentro de este objeto

    /**
     * 2. Método actualizado para aceptar todos los parámetros del formulario.
     */
    fun iniciarSesion(
        nombreUsuario: String,
        email: String,
        birthDate: String,
        favoriteGameType: String
    ) {
        this.username = nombreUsuario
        this.email = email
        this.birthDate = birthDate
        this.favoriteGameType = favoriteGameType
        this.isLoggedIn = true
    }

    /**
     * 3. Método actualizado para limpiar todas las propiedades al cerrar sesión.
     */
    fun cerrarSesion() {
        this.username = null
        this.email = null
        this.birthDate = null
        this.favoriteGameType = null
        this.isLoggedIn = false
    }
}
