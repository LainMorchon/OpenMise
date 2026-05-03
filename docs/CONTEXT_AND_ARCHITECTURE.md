# Contexto del Sistema y Arquitectura - OpenMise

## 1. Visión General
**OpenMise** es una aplicación de gestión nutricional multiplataforma desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**. 

### Objetivo Actual
Aunque el proyecto está configurado para ser multiplataforma (Android, iOS, Desktop), el objetivo prioritario a corto plazo es el funcionamiento pleno en **Android**, manteniendo el código de lógica y UI en `commonMain` para facilitar la futura expansión.

---

## 2. Stack Tecnológico (KMP Core)
- **UI**: Compose Multiplatform (Material 3).
- **Iconografía**: Material Icons Extended (vía `compose.materialIconsExtended`).
- **Imágenes**: Coil 3 (Soporte para ByteArray, File y URL).
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
- **`model/`**: Entidades (POJOs/Data Classes). 
    - `Usuario`: Perfil y objetivos.
    - `Alimento` / `Receta`: Base nutricional.
    - `Plan` / `ItemPlan`: Plantillas de alimentación estructuradas por momentos del día.
    - `RegistroDiario`: Log de consumo real con valores "congelados" (Snapshot).
- **`repository/`**: Interfaces de repositorios (contratos para `PlanRepository`, `RegistroDiarioRepository`, etc.).
- **`usecase/`**: Lógica de orquestación.
    - `GetConsumoDiarioUseCase`: Calcula el progreso del día actual.
    - `AplicarPlanUseCase`: Vuelca una plantilla completa al registro diario.
    - `AddAlimentoAlRegistroUseCase`: Implementa la lógica de snapshot al guardar un consumo.

### 🔵 Capa de Datos (`data/`)
Implementación de la persistencia y red.
- **`database/`**: Configuración de Room, DAOs (`UsuarioDao`, `AlimentoDao`, etc.) y Entidades de BD.
- **`remote/`**: Servicios de red con Ktor y DTOs.
- **`repository/`**: Implementaciones reales de los repositorios que deciden si usar caché local o red.

### 🟡 Capa de UI/Presentación (`ui/`)
UI declarativa con Compose.
- **`core/util/`**: Contiene `CameraManager`, una abstracción `expect/actual` para manejar la Cámara y Galería de forma nativa en Android e iOS, con optimización de memoria y gestión de permisos.
- **Features**:
    - `ui/home/`: Dashboard principal con barras de progreso nutricional.
    - `ui/recetas/`: Creación y listado de recetas complejas.
    - `ui/planes/`: Gestión de plantillas de comidas semanales o diarias.
    - `ui/consumo/`: Flujo de búsqueda y adición de alimentos al log diario (Buscador polimórfico).
    - `ui/perfil/`: Configuración de objetivos y datos físicos del usuario.

### 🔴 Inyección de Dependencias (`di/`)
Configuración de módulos de Koin para unir todas las capas.

---

## 4. Conceptos Clave de Negocio
1. **Polimorfismo Nutricional**: Una `Receta` es tratada como un `Alimento`. Esto permite que tanto el `RegistroDiario` como los `ItemPlan` operen sobre una base común, permitiendo añadir una manzana (alimento simple) o una paella (receta) indistintamente.
2. **Patrón Snapshot**: El `RegistroDiario` no referencia dinámicamente los macros del alimento original. Al añadir un consumo, se realiza una copia física de los macros (Kcal, P, HC, G) en la entrada del log. Esto garantiza que el historial del usuario sea inalterable aunque la receta o el alimento original cambien sus valores en el futuro.
3. **Estructura de Planes (Templates)**: Un `Plan` es un conjunto de `ItemPlan` organizados por `MomentoComida` (Desayuno, Almuerzo, Cena, Snacks). No representa un consumo real, sino una plantilla que el usuario puede "aplicar" a cualquier día de su calendario.
4. **Cálculo del Consumo Diario**: El Dashboard realiza una agregación reactiva de todos los `RegistroDiario` de la fecha seleccionada, restando el total consumido de los objetivos definidos en el perfil del `Usuario`.

---

## 5. Estructura de Base de Datos (Room)
Tablas principales definidas:
- `Usuario`: Perfil y objetivos nutricionales.
- `Alimento`: Catálogo base de alimentos.
- `Detalle_Receta`: Detalles extendidos (descripción, pasos de preparación, enlace web, URL de imagen local/remota).
- `Ingrediente_Receta`: Estructura de recetas compuestas con cálculo de macros por 100g.
- `Plan` / `Item_Plan`: Plantillas de alimentación.
- `Registro_Diario`: Log de consumo con macros congelados.
