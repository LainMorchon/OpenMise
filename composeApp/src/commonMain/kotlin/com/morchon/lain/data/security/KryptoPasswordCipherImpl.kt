package com.morchon.lain.data.security

import com.morchon.lain.domain.security.PasswordCipher
import korlibs.crypto.SHA256

/**
 * Implementación de PasswordCipher utilizando SHA-256 con Salt.
 * Utiliza korlibs-crypto (krypto) para compatibilidad multiplataforma.
 */
class KryptoPasswordCipherImpl : PasswordCipher {
    
    // En una implementación real, el salt debería ser aleatorio por usuario y guardarse en DB.
    // Para este proyecto, usaremos un salt estático para simplificar la lógica de migración.
    private val salt = "OpenMise_Secure_Salt_2024"

    override fun hash(plainText: String): String {
        return SHA256.digest((plainText + salt).encodeToByteArray()).hex
    }

    override fun verify(plainText: String, hash: String): Boolean {
        val hashedInput = hash(plainText)
        return hashedInput == hash
    }
}
