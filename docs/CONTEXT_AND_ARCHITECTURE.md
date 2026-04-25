# Contexto del Sistema y Arquitectura - OpenMise

## 1. Visión General
**OpenMise** es una aplicación de gestión nutricional multiplataforma desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**. 

### Objetivo Actual
Aunque el proyecto está configurado para ser multiplataforma (Android, iOS, Desktop), el objetivo prioritario a corto plazo es el funcionamiento pleno en **Android**, manteniendo el código de lógica y UI en `commonMain` para facilitar la futura expansión.

---

## 2. Stack Tecnológico (KMP Core)
- **UI**: Compose Multiplatform (Material 3).
- **Inyección de Dependencias**: Koin.
- **Red**: Ktor Client (JSON con Kotlinx Serialization).
- **Base de Datos Local**: Room KMP (SQLite).
- **Gestión de Estado**: Jetpack ViewModel (KMP compatible) + StateFlow.
- **Navegación**: Navigation for Compose (KMP).
- **Paginación**: Paging3 (Multiplatform).
- **Fechas**: Kotlinx Datetime.

---

## 3. Arquitectura del Proyecto
Se sigue una **Clean Architecture** adaptada para KMP, organizada dentro de `composeApp/src/commonMain/kotlin/com/morchon/lain/`:

### 🟢 Capa de Dominio (`domain/`)
Contiene las reglas de negocio puras, sin dependencias de frameworks externos.
- **`model/`**: Entidades (POJOs/Data Classes). Ej: `Usuario`, `Alimento`, `Receta`, `RegistroDiario`.
- **`repository/`**: Interfaces de repositorios (contratos).
- **`usecase/`**: Lógica de orquestación de datos.

### 🔵 Capa de Datos (`data/`)
Implementación de la persistencia y red.
- **`database/`**: Configuración de Room, DAOs (`UsuarioDao`, `AlimentoDao`, etc.) y Entidades de BD.
- **`remote/`**: Servicios de red con Ktor y DTOs.
- **`repository/`**: Implementaciones reales de los repositorios que deciden si usar caché local o red.

### 🟡 Capa de UI/Presentación (`ui/`)
UI declarativa con Compose.
- **`core/`**: Componentes reutilizables, temas, navegación y extensiones.
- **Features (`home/`, `login/`, `recetas/`, etc.)**:
    - `XXXScreen`: Composable principal de la pantalla.
    - `XXXViewModel`: Lógica de presentación y gestión de estado.
    - `XXXState`: Clase que define el estado único de la UI.

### 🔴 Inyección de Dependencias (`di/`)
Configuración de módulos de Koin para unir todas las capas.

---

## 4. Conceptos Clave de Negocio
1. **Polimorfismo Nutricional**: Una `Receta` es tratada como un `Alimento`. Esto permite que el sistema de registros y planes acepte ambos de forma indistinta.
2. **Patrón Snapshot**: El `RegistroDiario` guarda una "foto" de los macros en el momento del consumo. Si el alimento original cambia sus valores en el futuro, el historial pasado permanece inalterado.
3. **Flujo de Datos**: Unidireccional (UDF). La UI dispara eventos al ViewModel, este interactúa con Use Cases/Repositorios, y actualiza un `StateFlow` que la UI observa.

---

## 5. Estructura de Base de Datos (Room)
Tablas principales definidas:
- `Usuario`: Perfil y objetivos nutricionales.
- `Alimento`: Catálogo base de alimentos.
- `Detalle_Receta`: Detalles extendidos (descripción, pasos, url, imagen).
- `Ingrediente_Receta`: Estructura de recetas compuestas.
- `Plan` / `Item_Plan`: Plantillas de alimentación.
- `Registro_Diario`: Log de consumo con macros congelados.
