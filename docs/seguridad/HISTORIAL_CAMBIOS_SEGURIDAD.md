# Historial de Cambios: Implementación de Seguridad (Password Hashing)

## Fecha: 23/05/2026

### 1. Dependencias
- Se añadió la librería `korlibs-crypto:krypto` en `libs.versions.toml`.
- Se configuró la dependencia en `composeApp/build.gradle.kts` para el set de fuentes `commonMain`.

### 2. Capa de Dominio
- **Interfaz `PasswordCipher`**: Creada en `domain/security/` para desacoplar el mecanismo de hashing.
- **`RegistrarUsuarioUseCase`**: Refactorizado para inyectar `PasswordCipher` y aplicar `hash()` antes de guardar el usuario.
- **`LoginUseCase`**: Refactorizado para usar `PasswordCipher.verify()` comparando el texto plano con el hash de la DB.
- **`EliminarUsuarioUseCase`**: Refactorizado para usar `PasswordCipher.verify()` para la confirmación de identidad.

### 3. Capa de Datos
- **Implementación `KryptoPasswordCipherImpl`**: Utiliza SHA-256 con Salt estático para asegurar las contraseñas en KMP.
- **Entidad `UsuarioEntity`**: Renombrado el campo `contrasena` a `hash_contrasena` para reflejar mejor su contenido y forzar la migración.
- **Base de Datos**: Incrementada la versión de Room a **9** debido al cambio de esquema en `UsuarioEntity`.

### 4. Inyección de Dependencias (Koin)
- **`DataModule.kt`**: Registro de `PasswordCipher` como `single` usando la implementación de Krypto.
- **`DomainModule.kt`**: Actualización de los Casos de Uso (`Login`, `Registrar`, `Eliminar`) para recibir la nueva dependencia.

---
**Estado Final**: El sistema ahora no almacena contraseñas en texto plano, cumpliendo con la especificación FEATURE_SEGURIDAD.md.
