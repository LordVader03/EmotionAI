# EmotionAI

## Descripción del proyecto

EmotionAI es una aplicación Android orientada a la detección y visualización inicial de emociones a partir de entradas del usuario. En esta primera versión, la app implementa un prototipo funcional con navegación entre pantallas, gestión de estado mediante arquitectura MVVM e integración inicial de sensores y backend.

## Arquitectura general de la aplicación

La aplicación sigue una arquitectura **MVVM** (Model-View-ViewModel), con separación de responsabilidades entre interfaz, lógica de presentación y acceso a datos.

### Estructura general

- **MainActivity.kt**
    - actividad principal de la aplicación Android. Inicializa la app y carga el composable raíz `EmotionAI.kt`.

- **EmotionAI.kt**
    - composable raíz de la aplicación. Inicializa Jetpack Compose y configura la navegación principal.

- **app/ui**
    - Contiene las pantallas de la aplicación desarrolladas con Jetpack Compose.
    - Se encarga de mostrar el estado y capturar las interacciones del usuario.
    - También gestiona la interacción directa con componentes del sistema Android, como la solicitud de permisos.

Los archivos están comprendidos en 4 subdirectorios:

- `detection/DetectionScreen.kt`: pantalla central del prototipo. Gestiona la interacción de usuario relacionada con detección, permisos, estado de captura, simulación de emoción y prueba inicial de backend
- `home/HomeScreen.kt`: pantalla principal de entrada. Sirve como menú inicial o punto de acceso al resto del flujo de la aplicación.
- `settings/SettingsScreen.kt`: pantalla de ajustes o configuración general de la app.
- `state/DetectionUiState.kt`: representa el estado observable de la pantalla de detección. Incluye permisos, carga, captura, emoción actual, estado de backend y errores.

- **app/viewModel**
    - Gestiona el estado de cada pantalla.
    - Coordina la lógica de presentación.
    - Recibe eventos desde la UI y actualiza el estado observable.

Se dividen en los siguientes archivos:

- `DetectionViewModel.kt`: contiene la lógica de presentación de la pantalla de detección. Recibe eventos desde la UI, actualiza el UiState, coordina permisos, simulación y conexión con el repositorio.
- `HomeViewModel.kt`: estructura preparada para futura lógica de presentación.
- `SettingsViewModel.kt`: estructura preparada para futura lógica de presentación.

- **app/data**
    - Incluye las clases de datos del dominio, como los resultados de detección.
    - Contiene el repositorio encargado de centralizar el acceso a datos o llamadas al backend.

Se subdividen en **model** y **repository**:

- `model/EmotionResult.kt`: modelo de dominio que representa el resultado de una detección emocional, por ejemplo etiqueta y confianza.
- `repository/EmotionRepository.kt`: encapsula el acceso a datos o servicios externos. En la versión actual sirve como base de la comunicación con backend mediante una respuesta inicial simulada o baseline.

- **app/core**
    - Guarda las rutas de navegación de la app

Está constituido por un único directorio:

- `navigation/NavRoutes.kt`: define las rutas de navegación de la app.

### Flujo básico

1. El usuario interactúa con la interfaz.
2. La pantalla envía el evento al ViewModel.
3. El ViewModel actualiza el estado o consulta al repositorio.
4. La UI se recompone automáticamente con el nuevo estado.

## Módulos principales del sistema

### 1. Navegación

Gestiona el desplazamiento entre las pantallas principales de la aplicación, como inicio, detección y ajustes. Esta parte permite estructurar la app y validar el flujo básico de uso.

### 2. Módulo de detección

Es el núcleo del prototipo actual. Desde esta pantalla se controlan:
- el estado de permisos;
- el inicio o parada de captura;
- la simulación de detección;
- la prueba de conexión con backend.

### 3. Gestión de permisos

Se ha implementado la solicitud inicial de permisos necesarios para cámara y micrófono. Esto permite preparar la integración real de sensores en futuras iteraciones.

### 4. Capa de datos / backend

La aplicación incluye una capa de repositorio para encapsular la comunicación con servicios externos. En la versión actual, esta capa permite simular o preparar una llamada inicial al backend, devolviendo una respuesta estructurada que actualiza la interfaz.

### 5. Gestión de estado

El estado de pantalla se centraliza en el ViewModel mediante un `UiState`, que controla elementos como:
- permisos concedidos;
- estado de captura;
- carga;
- resultado actual;
- errores;
- estado de conexión con backend.

## Instrucciones para ejecutar la app

1. Abrir el proyecto en Android Studio.
2. Esperar a que finalice la sincronización de Gradle.
3. Ejecutar la aplicación desde Android Studio en un emulador o dispositivo.
4. Acceder a la pantalla de detección para comprobar:
    - solicitud de permisos;
    - navegación funcional;
    - simulación de detección;
    - prueba de backend.

### Estado actual del prototipo

En la versión actual ya se encuentran implementados:
- arquitectura MVVM;
- navegación básica entre pantallas;
- primer prototipo funcional;
- integración inicial de sensores mediante permisos;
- integración inicial de API/backend mediante repositorio y flujo de prueba.
