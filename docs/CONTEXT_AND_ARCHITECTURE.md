# Contexto del Sistema y Arquitectura - OpenMise

## 1. Visión General
**OpenMise** es una aplicación de gestión nutricional multiplataforma desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**. 

### Objetivo Actual
Funcionamiento pleno en **Android**, manteniendo el código de lógica y UI en `commonMain` para facilitar la expansión a iOS y Desktop.

---

## 2. Stack Tecnológico (KMP Core)
- **UI**: Compose Multiplatform (Material 3).
- **Iconografía**: Material Icons Extended.
- **Inyección de Dependencias**: Koin (v4.0+).
- **Red**: Ktor Client (JSON con Kotlinx Serialization).
- **Base de Datos Local**: Room KMP (SQLite).
- **Gestión de Estado**: Jetpack ViewModel (KMP) + StateFlow.
- **Navegación**: Navigation for Compose (KMP) + `SavedStateHandle`.
- **Fechas**: Kotlinx Datetime.

---

## 3. Arquitectura del Proyecto
Clean Architecture organizada en `composeApp/src/commonMain/kotlin/com/morchon/lain/`:

### 🟢 Capa de Dominio (`domain/`)
- **`model/`**: 
    - `Plan`: Soporta tipos `DIA_UNICO` y `SEMANAL`.
    - `ItemPlan`: Contiene `indiceDia` (0 para diario, 1-7 para semanal).
- **`usecase/`**: Lógica de orquestación como `AplicarPlanUseCase` (con filtrado inteligente por día de la semana) y `GuardarPlanUseCase`.

### 🔵 Capa de Datos (`data/`)
- Implementación de repositorios y DAOs de Room.
- **Patrón Mapper**: Conversión estricta entre `Entity` y `Model`.

### 🟡 Capa de UI/Presentación (`ui/`)
- **Gestión de Navegación**: Se utiliza `SavedStateHandle` inyectado por Koin (`handle.get()`) para recuperar argumentos de ruta de forma segura en KMP.
- **Features**:
    - `ui/planes/`: Listado y Editor avanzado con soporte semanal y sistema de copia de plantillas.
    - `ui/consumo/`: Integración de planes para registro rápido con previsualización y selección granular.

---

## 4. Conceptos Clave de Negocio
1. **Polimorfismo Nutricional**: Tratamiento unificado de Alimentos y Recetas.
2. **Patrón Snapshot**: Consumos en el diario con macros "congelados" para inmutabilidad del historial.
3. **Estructura de Planes (Templates)**:
    - **Planes Únicos**: Plantilla de un solo día (ej. "Día de entrenamiento").
    - **Planes Semanales**: Estructura de 7 días independientes.
    - **Sistema de Plantillas**: Capacidad de usar un plan existente como base para rellenar días específicos de un nuevo plan, fomentando la reutilización.
    - **Aplicación Inteligente**: Al aplicar un plan semanal al diario, el sistema filtra automáticamente los alimentos según el `isoDayNumber` de la fecha destino.
    - **Granularidad en Consumo**: Permite añadir bloques completos de un plan o seleccionar alimentos individuales desde la vista de detalle del plan.

---

## 5. Estructura de Base de Datos (Room)
- `Plan`: (id, usuario_id, nombre, tipo).
- `Item_Plan`: (id, plan_id, alimento_id, cantidad_gramos, momento_comida, indice_dia).
- `Registro_Diario`: Log con macros históricos.
