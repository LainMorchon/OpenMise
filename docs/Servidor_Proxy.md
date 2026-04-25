# Documentación Técnica: Proxy de Seguridad y Red para OpenMise

## 1. Introducción y Propósito
El Proxy de OpenMise es un microservicio desarrollado en **Kotlin (Ktor)** diseñado para actuar como intermediario entre la aplicación móvil (Android/KMP) y la API de **FatSecret**.

### ¿Por qué es necesario este Proxy?
1.  **Seguridad (Ofuscación de Secretos):** Las APIs de OAuth2 requieren un `clientId` y un `clientSecret`. Si estos se incluyen en el código de la App móvil, cualquier usuario podría extraerlos mediante ingeniería inversa. El Proxy los mantiene seguros en el lado del servidor.
2.  **Control de Acceso por IP:** FatSecret exige una lista blanca de IPs para autorizar las peticiones. Los dispositivos móviles cambian de IP constantemente (Wi-Fi, 4G, 5G), lo que hace imposible registrarlos. El Proxy ofrece una **IP única y estática** para todas las peticiones de todos los usuarios.

---

## 2. Arquitectura del Servidor (Ktor)

El servidor se ha construido utilizando el framework **Ktor** con motor **Netty**.

### Componentes Clave:
* **Cliente HTTP (OkHttp):** Se utiliza el motor `OkHttp` dentro de Ktor para realizar las llamadas salientes a FatSecret. Se eligió este motor por su robustez en la gestión de conexiones y resolución de nombres (DNS).
* **Negociación de Contenido (Content Negotiation):** Configurado con `Kotlinx.serialization` para procesar JSON. Se ha activado la opción `ignoreUnknownKeys = true` para evitar que el servidor falle si FatSecret añade nuevos campos a su API en el futuro.

### Endpoints Implementados:

#### 1. `GET /` (Health Check)
* **Descripción:** Un endpoint de prueba para verificar que el servidor está levantado y responde.
* **Respuesta:** Texto plano: `"🚀 Proxy de OpenMise funcionando a la perfección"`.

#### 2. `GET /search` (Proxy de Búsqueda)
* **Descripción:** El corazón del proyecto. Recibe una búsqueda, gestiona el token y devuelve los alimentos.
* **Parámetros:** `q` (String) - El nombre del alimento a buscar.
* **Lógica Interna:**
    1.  **Gestión de Token OAuth2:** El servidor comprueba si tiene un `access_token` válido en memoria.
    2.  **Solicitud de Token:** Si no hay token, realiza una petición `POST` a `https://oauth.fatsecret.com/connect/token` usando las credenciales ocultas.
    3.  **Llamada a la API:** Con el token obtenido, realiza una petición a la API de FatSecret (`https://platform.fatsecret.com/rest/server.api`) añadiendo el parámetro `method=foods.search`.
    4.  **Forwarding:** El JSON recibido de FatSecret se devuelve íntegro a la aplicación móvil.

---
* **Url para trabajar:**
👉 https://openmise-proxy-867661743699.us-central1.run.app

## 3. Contenerización (Dockerfile)

Para garantizar que el servidor funcione igual en local que en la nube de Google, hemos utilizado **Docker**.

### Estrategia de Construcción:
* **Multi-stage Build:** El Dockerfile se divide en dos etapas para reducir el tamaño final de la imagen.
    1.  **Etapa de Compilación:** Usa una imagen de Gradle con Java 21 para generar el `.jar` ejecutable.
    2.  **Etapa de Ejecución:** Usa una imagen ligera de **Ubuntu Jammy** (`eclipse-temurin:21-jre-jammy`).
* **Corrección Crítica (DNS):** Inicialmente se usó *Alpine Linux*, pero se cambió a *Jammy* debido a problemas de resolución de nombres (error `Name or service not known`) que presenta la implementación de red de Alpine con Java en ciertos entornos de Google Cloud.

---

## 4. Infraestructura en Google Cloud (Cloud Run)

El servidor está desplegado en **Google Cloud Run**, un entorno *Serverless* que escala automáticamente según la demanda.

### Pasos de Despliegue:
1.  **Habilitación de APIs:** Se activaron `artifactregistry.googleapis.com`, `cloudbuild.googleapis.com` y `run.googleapis.com`.
2.  **Compilación en la Nube:** Mediante `gcloud run deploy --source .`, Google Cloud Build lee el Dockerfile, construye la imagen y la almacena en Artifact Registry.
3.  **Invocación:** Se configuró como `--allow-unauthenticated` para permitir que la App móvil pueda conectar sin barreras de autenticación de Google.

---

## 5. Solución de Red: IP Estática y Salida Controlada

Este es el apartado más avanzado del proyecto. Por defecto, Cloud Run cambia de IP constantemente. Para cumplir con los requisitos de FatSecret, hemos creado una **"Tubería de Red"** estática.

### Componentes de la Infraestructura de Red:
1.  **Dirección IP Estática Regional:** Se reservó una IP fija (`IPv4`, `Regional`, `us-central1`) que sirve como nuestra "matrícula" oficial ante FatSecret.
2.  **Conector de Acceso a VPC sin Servidor:** Un "puente" virtual que permite a Cloud Run entrar en una red privada virtual (VPC) de Google. Se configuró con el rango de subred `10.8.0.0/28`.
3.  **Cloud Router:** Un router virtual encargado de gestionar las tablas de rutas de nuestra red privada.
4.  **Cloud NAT (Network Address Translation):** El componente clave. Se configuró para que **todo el tráfico de salida** que pase por el router hacia Internet sea sellado obligatoriamente con nuestra **IP Estática Reservada**.

### Configuración Final en Cloud Run:
Se actualizó el servicio para forzar que **el 100% del tráfico saliente** pase a través del Conector VPC. Esto garantiza que FatSecret siempre reciba las peticiones desde la misma IP, permitiendo una conexión estable y permanente.

---

## 6. Conclusión
Este Proxy no solo resuelve un problema de conectividad, sino que eleva la seguridad de la arquitectura de OpenMise a estándares profesionales, separando la lógica de negocio y las credenciales del cliente móvil.