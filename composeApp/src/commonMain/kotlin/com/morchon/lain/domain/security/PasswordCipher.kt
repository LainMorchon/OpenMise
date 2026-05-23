package com.morchon.lain.domain.security

/**
 * Interfaz para el cifrado y verificación de contraseñas.
 */
interface PasswordCipher {
    /**
     * Crea un hash seguro a partir de una contraseña en texto plano.
     */
    fun hash(plainText: String): String

    /**
     * Verifica si una contraseña en texto plano coincide con un hash guardado.
     */
    fun verify(plainText: String, hash: String): Boolean
}
