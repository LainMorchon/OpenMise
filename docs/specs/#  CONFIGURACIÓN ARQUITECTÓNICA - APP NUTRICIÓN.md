# 📂 CONFIGURACIÓN ARQUITECTÓNICA - APP NUTRICIÓN (KMP + ARISTIDEVS STYLE)
**Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin, Ktor, Room KMP, Paging3.
**Estructura:** Clean Architecture adaptada a la nomenclatura de AristiDevs.

---

## 1. ESTRUCTURA DE DIRECTORIOS (commonMain/kotlin/...)

### 🟢 domain/ (Reglas de Negocio)
* **`model/`**: Entidades puras de la App de Nutrición.
    * `Usuario.kt`, `Alimento.kt`, `Receta.kt`, `Plan.kt`, `Ingrediente.kt`, `ItemPlan.kt`, `RegistroDiario.kt`.
* **`Repository.kt`**: Interfaces de los repositorios (contratos).
* **`usecase/`**: Lógica orquestada (ej: `GetDailyMacrosUseCase.kt`, `ApplyPlanUseCase.kt`).

### 🔵 data/ (Infraestructura)
* **`database/`**: Implementación de Room KMP.
    * `dao/`: Interfaces `AlimentoDAO`, `PlanDAO`, etc.
    * `entity/`: Tablas de BD (ej: `AlimentoEntity.kt`).
    * `Database.kt`: Clase abstracta de la base de datos Room.
* **`remote/`**: Consumo de API con Ktor.
    * `paging/`: `FoodPagingSource.kt` para búsquedas masivas.
    * `response/`: DTOs de red (ej: `FoodResponse.kt`).
    * `ApiService.kt`: Definición de los endpoints.
* **`RepositoryImpl.kt`**: Implementación real de los repositorios que coordina `database` y `remote`.

### 🟡 ui/ (Presentación - Compose Multiplatform)
* **`core/`**: Componentes reutilizables, extensiones, temas y navegación.
    * `components/`: `FoodCard.kt`, `MacroChip.kt`, `PagingLoadingState.kt`.
    * `navigation/`: `NavigationWrapper.kt`, `Routes.kt`.
    * `util/`: `CameraManager.kt` (Gestión de cámara/galería KMP), `ImageManager.kt`.
* **`home/`**, **`detail/`**, **`plan/`** (Features):
    * `XXXScreen.kt`: El código UI de la pantalla.
    * `XXXViewModel.kt`: Gestión del estado de la interfaz. **Prohibido incluir lógica de negocio aquí.**
    * `XXXState.kt`: Clase que define el estado (ej: `data class HomeState(val foods: List<Food>, val isLoading: Boolean)`).

### 🔴 di/ (Inyección de Dependencias)
* `DataModule.kt`, `DomainModule.kt`, `UIModule.kt`, `DIConfigurator.kt`.

---

## 2. LÓGICA DE DATOS Y ESTADOS

1. **Separación de Responsabilidades (ESTRICTO):** 
    * Los **ViewModels** solo gestionan el estado de la UI y llaman a UseCases. No deben instanciar ni llamar directamente a utilidades de infraestructura (como `ImageManager` o `CameraManager`) si el resultado de estas operaciones debe ser procesado por la lógica de negocio.
    * Los **Use Cases** orquestan tanto la lógica pura como el uso de utilidades necesarias para completar una acción de negocio (ej: procesar una imagen antes de guardarla).
    * Los **Repositorios** solo orquestan el origen de los datos (Local vs Remote).
2. **Herencia & Polimorfismo:** `Receta` hereda de `Alimento`. Esto permite que cualquier pantalla que acepte un alimento pueda mostrar una receta sin cambiar el código.
2. **Patrón Snapshot:** El `RegistroDiario` guarda los valores nutricionales actuales en el momento del consumo (`historico_kcal`), evitando que el historial cambie si el alimento original se edita en la API o base de datos.
3. **Manejo de Estados:** Cada ViewModel expone un `StateFlow<UIState>` que la pantalla observa para reaccionar a cambios.