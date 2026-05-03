# SYSTEM CONTEXT & ARCHITECTURE - APP DE NUTRICIÓN (KMP EDITION)
**Stack Tecnológico Objetivo:** Kotlin Multiplatform (KMP), Compose Multiplatform, Clean Architecture.
**Librerías Core:** Koin (DI), Ktor (Network), Room KMP (Base de datos local), Jetpack ViewModel, Navigation for Compose, app.cash.paging (Paginación).
**Propósito del Documento:** Proveer el contexto arquitectónico, estructura de paquetes y lógica de negocio para la generación de código multiplataforma.

---

## 1. VISIÓN GENERAL Y STACK
Aplicación de gestión nutricional que permite a los usuarios registrar consumos diarios, crear recetas personalizadas y diseñar planes nutricionales (plantillas). El núcleo del sistema radica en la estandarización de consumibles: cualquier elemento (alimento simple o receta) es tratado bajo una interfaz común mediante polimorfismo.
Aplicación multiplataforma de gestión nutricional. El núcleo radica en la estandarización mediante polimorfismo: cualquier elemento (alimento simple o receta) es tratado bajo una interfaz común. Todo el código de negocio, red, base de datos y UI (Compose) reside en `commonMain`.

---

## 2. ARQUITECTURA DE CAPAS Y RESPONSABILIDADES
El proyecto sigue **Clean Architecture** para separar la infraestructura de la lógica de negocio.

1.  **Capa de Dominio (`domain/`)**: Contiene los modelos, interfaces de repositorios y **Use Cases**. Es el corazón del sistema. **Toda la lógica de negocio debe residir aquí.**
2.  **Capa de Datos (`data/`)**: Implementa los repositorios, gestiona Room KMP y Ktor. No contiene lógica de negocio, solo orquestación de fuentes de datos.
3.  **Capa de Presentación (`ui/`)**: Contiene las pantallas y ViewModels. Los ViewModels solo gestionan el estado visual y delegan las acciones a los Use Cases.

---

## 3. ARQUITECTURA DE PERSISTENCIA (Room KMP)
El esquema consta de 7 tablas normalizadas.

* **Patrón Snapshot:** La tabla `Registro_Diario` no usa JOINs en tiempo real. Congela los macros (`historico_kcal`, etc.) en el momento del consumo para hacer el historial inmutable.
* **Composición de Recetas:** Relación 1:1 entre `Alimento` y `Detalle_Receta`.

**Tablas Principales (Entities):**
1. `Usuario`: id, email, hash_contrasena, objetivos_nutricionales.
2. `Alimento`: id, nombre, origen, kcal_100g, proteinas_100g, carbohidratos_100g, grasas_100g.
3. `Detalle_Receta`: alimento_id (PK/FK), descripcion, pasos_preparacion.
4. `Ingrediente_Receta`: receta_id, alimento_base_id, cantidad_gramos.
5. `Plan`: id, usuario_id, nombre, tipo.
6. `Item_Plan`: plan_id, alimento_id, momento_comida, indice_dia.
7. `Registro_Diario`: id, usuario_id, alimento_id (Ref), fecha, cantidad_gramos, historicos_macros...

---

## 3. MODELO DE DOMINIO (Lógica POO)
Modelo conceptual en `commonMain/domain/model/`.

* **Herencia (`<|--`):** `Receta` hereda de `Alimento`. Permite que `Item_Plan` y `Registro_Diario` acepten ambos indistintamente.
* **Composición (`*--`):** `Usuario` con `Receta` (Privadas) // `Receta` con `Ingrediente` // `Plan` con `Item_Plan`. Si el padre se borra, los hijos también.
* **Asociación (`-->`):** Referencias a `Alimento` desde registros y planes. No se borra el alimento del catálogo si se borra el registro.

**Clases y Métodos Clave:**
* **`Usuario`**: `establecerObjetivos(kcal, prot, carb, gras)`.
* **`Alimento`**: `calcularMacrosPorGramos(cantidad)`.
* **`Receta`**: `calcularMacrosTotales()` (Itera ingredientes).
* **`Ingrediente`**: Contiene instancia de `Alimento`. `obtenerMacrosAportados()`.
* **`Plan`**: `añadirItem()`, `aplicarAlRegistroDiario(fecha)`.
* **`Item_Plan`**: Define cantidad, momento (ej. Desayuno) e `indice_dia` (Int).
* **`Registro_Diario`**: `congelarMacrosHistoricos(alimento)` (Snapshot).

---

## 4. MÁQUINAS DE ESTADO (Ciclos de Vida)

Controlado mediante Use Cases interactuando con las entidades y repositorios.

### 4.1. Entidad: `Plan` Nutricional
* `[Borrador]`: Creación y mutación de ítems. Sin impacto en el diario.
* `[Activo]`: Asignado a una ventana temporal. Vuelca consumos al diario. Regresión a Borrador si se edita.
* `[Archivado]`: Transición por fin de tiempo. Inmutable ("solo lectura").

### 4.2. Entidad: `Receta`
* `[Borrador]`: Fase de composición (edición de ingredientes). Inaccesible para asociaciones.
* `[Disponible]`: Guardada en catálogo privado. Lista para usarse en diarios y planes.
* `[Eliminada]`: Purga total (destruye ingredientes por composición).