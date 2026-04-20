package com.morchon.lain.ui.login

import com.morchon.lain.domain.model.Usuario

/**
 * Representa el estado exacto de la pantalla de Login en un instante de tiempo.
 */
data class LoginState(
    val email: String = "",
    val contrasena: String = "",
    val estaCargando: Boolean = false,
    val error: String? = null,
    val loginExitoso: Boolean = false,
    val usuariosRegistrados: List<Usuario> = emptyList()
)