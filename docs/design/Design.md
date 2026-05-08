Actúa como un desarrollador senior de Android especializado en Jetpack Compose y Material Design 3. Necesito actualizar la identidad visual de mi proyecto "OpenMise" con los siguientes requisitos técnicos:

### 1. Lógica de Marca (Bicolor)
Genera un componente de Compose reutilizable para el nombre de la app. Debe usar `AnnotatedString` para aplicar diferentes colores:
* **"Open"**: Verde (#10B981).
* **"Mise"**: Naranja (#F97316).

### 2. Implementación en Pantallas
Genera el código para las siguientes estructuras de UI:

* **Pantalla de Login**: 
    * El logo de la app y el texto "OpenMise" centrados.
    * El tamaño del texto debe ser grande (estilo `displayMedium` o `headlineLarge`).
    * Tipografía principal: Nunito Sans.

* **Pantalla Home**: 
    * El nombre "OpenMise" en la parte superior, centrado.
    * Tamaño normal (`titleLarge` o `headlineSmall`).
    * Usa un `CenterAlignedTopAppBar` de Material 3.

### 3. Lógica de Botones Seleccionables
Implementa componentes de botones seleccionables (por ejemplo, para filtrar o elegir opciones) con el siguiente comportamiento visual condicional:
* **Estado Seleccionado**: Debe tener un borde sólido (ej. `BorderStroke(1.dp, Color)`) utilizando el color de acento correspondiente.
* **Estado No Seleccionado**: Debe mostrarse sin borde (borde nulo o transparente).

### 4. Configuración de Estilos
* **Fondo**: #18181B.
* **Tipografías**: Nunito Sans (Títulos) y GoogleSans (Cuerpo).
* **Modo**: Únicamente Dark Mode.

Proporciona el código de Kotlin optimizado, incluyendo el uso de `SpanStyle` para el texto bicolor y la lógica de estado para los bordes de los botones.