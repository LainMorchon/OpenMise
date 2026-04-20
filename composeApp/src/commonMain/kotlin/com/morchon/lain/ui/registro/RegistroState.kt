package com.morchon.lain.ui.registro

data class RegistroState(
    val nombre: String = "",
    val email: String = "",
    val estaCargando: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
)