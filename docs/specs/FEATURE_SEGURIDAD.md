# Spec de Implementación: Hashing de Contraseñas KMP (OpenMise)

**Objetivo:** Implementar un algoritmo de coste computacional para el hashing de contraseñas utilizando una librería KMP pura (ej. `korlibs-crypto` o un port multiplataforma de Bcrypt). Todo el desarrollo debe mantenerse estrictamente en `commonMain`.

## 1. Reglas Arquitectónicas (Clean Architecture Estricta)
- **Prohibido** usar librerías nativas dependientes de la plataforma (como `java.security` en JVM/Android o `CryptoKit` en iOS).
- **Prohibido** exponer entidades de Room a las capas superiores.
- Toda la inyección de dependencias se gestionará con Koin.

## 2. Capa Domain (`commonMain/domain`)
- **Interfaz:** Crea el contrato `PasswordCipher` con los métodos `hash(plainText: String): String` y `verify(plainText: String, hash: String): Boolean`.
- **Caso de Uso:** Actualiza `RegistrarUsuarioUseCase` (y análogos) para inyectar `PasswordCipher`. El caso de uso debe aplicar el hash a la contraseña en texto plano antes de generar el modelo de dominio `Usuario`.

## 3. Capa Data (`commonMain/data`)
- **Implementación:** Crea `BcryptPasswordCipherImpl` que implemente la interfaz `PasswordCipher`, apoyándose en la librería KMP seleccionada.
- **Persistencia (Room KMP):** Verifica que `UsuarioEntity` tenga un campo específico para el hash (ej. `hash_contrasena`).
- **Mappers:** Asegura que la transformación de `Usuario` (Domain) a `UsuarioEntity` (Data) asigne correctamente el string cifrado, garantizando que la base de datos nunca reciba texto plano.

## 4. Inyección de Dependencias (`commonMain/di`)
- Configura el módulo correspondiente de Koin (ej. `DataModule.kt`) para proveer la implementación `BcryptPasswordCipherImpl` cuando se requiera `PasswordCipher`.

Genera únicamente el código necesario para estos archivos respetando la sintaxis de Kotlin Multiplatform.