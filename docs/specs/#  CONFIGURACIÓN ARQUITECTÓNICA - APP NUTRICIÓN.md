# 📂 CONFIGURACIÓN ARQUITECTÓNICA - APP NUTRICIÓN (KMP + ARISTIDEVS STYLE)
**Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin, Ktor, Room KMP, Paging3.
**Estructura:** Clean Architecture adaptada a la nomenclatura de AristiDevs.

---

## 1. ESTRUCTURA DE DIRECTORIOS (commonMain/kotlin/...)

### 🟢 domain/ (Reglas de Negocio)
* **`model/`**: Entidades puras.
    * `Plan.kt`: Define `PlanType` (DIA_UNICO, SEMANAL).
    * `ItemPlan.kt`: Entidad atómica de un plan con `indiceDia`.

### 🔵 data/ (Infraestructura)
* **`database/`**: Implementación de Room KMP.
* **`RepositoryImpl.kt`**: Coordinación entre local y remoto.

### 🟡 ui/ (Presentación - Compose Multiplatform)
* **`navigation/`**: Gestión de rutas. En ViewModels, se utiliza `SavedStateHandle` para recuperar IDs de navegación en KMP.
* **Features**:
    * `ui/home/`: Dashboard principal con resumen de progreso diario (kcal y macros).
    * `ui/diario/`: Historial de consumo con navegación por fechas, agrupación por momentos del día y resumen de macronutrientes.
    * `ui/planes/editar/`: Implementa lógica de filtrado por día, búsqueda de alimentos y sistema de plantillas (copia profunda de items entre planes).

### 🔴 di/ (Inyección de Dependencias)
* **`UIModule.kt`**: Configuración de ViewModels. Uso obligatorio de `handle.get()` para inyectar `SavedStateHandle` en ViewModels parametrizados.

---

## 2. LÓGICA DE DATOS Y ESTADOS

1. **Separación de Responsabilidades:** 
    * ViewModels: Gestión de UI State y flujo de navegación.
    * Use Cases: Lógica de negocio (ej. copiar alimentos de una plantilla a un plan).
2. **Sistema de Planes:**
    * Un Plan Semanal tiene 7 estados internos (días 1 a 7).
    * El `indiceDia` 0 se reserva para planes de un único día.
    * Las plantillas permiten al usuario importar la configuración de un día de un plan "A" al día actual del plan "B".
3. **Patrón Snapshot:** El `RegistroDiario` guarda los valores nutricionales actuales en el momento del consumo, garantizando la inalterabilidad histórica.
4. **Manejo de Estados:** Uso de `StateFlow` y clases `State` dedicadas para cada pantalla.
