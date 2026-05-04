# 📋 Informe de Estado Actual - OpenMise
**Fecha:** Mayo 2026
**Tecnología:** Kotlin Multiplatform (KMP), Compose Multiplatform
**Estado Arquitectónico:** Clean Architecture (Refactorizado con Use Cases)

---

## 1. Estructura del Proyecto
El proyecto sigue una arquitectura **Clean Architecture** estricta, asegurando que la lógica de negocio sea independiente de la plataforma y de los detalles de implementación.

---

## 2. Clases e Interfaces Principales

### 🟢 Capa de Dominio
- **Modelos:** `Alimento`, `Receta`, `Ingrediente`, `Usuario`, `ObjetivosNutricionales`, `RegistroDiario`, `ConsumoProgreso`.
- **Casos de Uso (Implementados):**
    - **Recetas:** `ObtenerRecetasUseCase`, `ObtenerDetalleRecetaUseCase`, `EliminarRecetaUseCase`, `GuardarRecetaUseCase`.
    - **Usuario:** `LoginUseCase`, `RegistrarUsuarioUseCase`, `ObtenerUsuarioActivoUseCase`, `CerrarSesionUseCase`, `ActualizarObjetivosUseCase`.
    - **Alimentos:** `BuscarAlimentosUseCase`, `GuardarAlimentoLocalUseCase`.
    - **Consumo:** `BuscarConsumiblesUseCase`, `RegistrarConsumoUseCase`, `ObtenerProgresoDiarioUseCase`.

### 🔵 Capa de Datos
- **Room KMP (v6):** `AppDatabase`, `AlimentoEntity`, `UsuarioEntity` (con objetivos), `DetalleRecetaEntity`, `IngredienteRecetaEntity`, `RegistroDiarioEntity`.
- **DAOs:** `UsuarioDao`, `AlimentoDao`, `RecetaDao`, `RegistroDiarioDao`.
- **API Remote:** `FatSecretApiService` con Proxy Ktor.

---

## 3. Capacidades de la Aplicación (Funcionalidades)
1.  **Arquitectura Profesional:** Desacoplamiento total entre UI y Lógica de Negocio.
2.  **Gestión de Usuario:** Registro, Login (persistencia de sesión), cierre de sesión y **configuración de objetivos nutricionales personalizados**.
3.  **Búsqueda e Inteligencia Nutricional:** Búsqueda en API externa y guardado automático en catálogo local.
4.  **Recetario Avanzado:** Cálculo automático de macros por 100g y soporte multimedia.
5.  **Registro Diario y Diario de Consumo (Historial):**
    *   **Patrón Snapshot**: Inmutabilidad del historial; el consumo guarda una copia física de los macros en el momento de la ingesta.
    *   **Diario Detallado**: Visualización por fechas, agrupación por momentos del día (Desayuno, Almuerzo, etc.) y resumen de macronutrientes totales del día.
    *   **Gestión de Registros**: Capacidad para eliminar consumos y actualizar el progreso en tiempo real.
6.  **Gestión de Planes Nutricionales:** Sistema de plantillas (Día Único/Semanal) para planificación avanzada y registro rápido.

---

## 4. Implementaciones Faltantes y Próximos Pasos

### 🔴 Prioridad Alta
- **Paginación:** Integrar `Paging3` en la búsqueda de alimentos para optimizar el rendimiento con grandes volúmenes de datos.

### 📅 Funcionalidades Pendientes
- **Gráficas de Historial:** Visualización de la evolución del peso y consumos semanales/mensuales.
- **Recordatorios/Notificaciones:** Alertas para registrar comidas o beber agua.
