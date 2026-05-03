# 📋 Informe de Estado Actual - OpenMise
**Fecha:** Mayo 2026
**Tecnología:** Kotlin Multiplatform (KMP), Compose Multiplatform
**Estado Arquitectónico:** Clean Architecture (Refactorizado con Use Cases)

---

## 1. Estructura del Proyecto
El proyecto sigue una arquitectura **Clean Architecture** estricta, asegurando que la lógica de negocio sea independiente de la plataforma y de los detalles de implementación.

### Organización de Carpetas (`composeApp/src/commonMain/kotlin/...`):
*   **`domain/`**: El núcleo de la aplicación.
    *   `model/`: Entidades puras de negocio.
    *   `repository/`: Interfaces que definen el contrato de datos.
    *   `usecase/`: **Lógica de negocio pura (Interactors)**. Cada acción del sistema tiene su propio Caso de Uso. **Prohibido colocar lógica de negocio fuera de aquí.**
*   **`data/`**: Implementación de infraestructura (Room, Ktor, Repositorios). Los Repositorios solo orquestan fuentes de datos.
*   **`ui/`**: Capa de presentación (Compose). Los ViewModels solo gestionan estados de UI y delegan en UseCases.
*   **`di/`**: Inyección de dependencias centralizada con Koin.

---

## 2. Clases e Interfaces Principales

### 🟢 Capa de Dominio
- **Modelos:** `Alimento`, `Receta`, `Ingrediente`, `Usuario`.
- **Casos de Uso (Implementados):**
    - **Recetas:** `ObtenerRecetasUseCase`, `ObtenerDetalleRecetaUseCase`, `EliminarRecetaUseCase`, `GuardarRecetaUseCase`.
    - **Usuario:** `LoginUseCase`, `RegistrarUsuarioUseCase`, `ObtenerUsuarioActivoUseCase`, `CerrarSesionUseCase`.
    - **Alimentos:** `BuscarAlimentosUseCase`, `GuardarAlimentoLocalUseCase`.
- **Orquestación Estricta:** Se ha movido la lógica de persistencia de imágenes al dominio (`GuardarRecetaUseCase`), eliminando dependencias de infraestructura en los ViewModels.

### 🔵 Capa de Datos
- **Room KMP:** `AppDatabase`, `AlimentoEntity`, `UsuarioEntity`, `DetalleRecetaEntity`, `IngredienteRecetaEntity`.
- **API Remote:** `FatSecretApiService` con Proxy Ktor.

---

## 3. Capacidades de la Aplicación (Funcionalidades)
1.  **Arquitectura Profesional:** Desacoplamiento total entre UI y Lógica de Negocio.
2.  **Gestión de Usuario:** Registro, Login (persistencia de sesión) y cierre de sesión.
3.  **Búsqueda e Inteligencia Nutricional:** Búsqueda en API externa y guardado automático en catálogo local.
4.  **Recetario Avanzado:**
    *   Cálculo automático de macros por 100g al guardar (Lógica en UseCase).
    *   Soporte multimedia para fotos de platos.

---

## 4. Implementaciones Faltantes y Próximos Pasos

### 🔴 Prioridad Alta
- **Objetivos Nutricionales:** Completar el modelo `Usuario` para incluir metas diarias.
- **Registro Diario:** Implementar `Registro_Diario` y el `RegistrarConsumoUseCase` con el patrón Snapshot.

### 📅 Funcionalidades Pendientes
- **Módulo de Planes:** Implementar `Plan` e `ItemPlan`.
- **Paginación:** Integrar `Paging3`.
