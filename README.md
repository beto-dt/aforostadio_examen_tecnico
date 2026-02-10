# AforoEstadio - Sistema de Control de Aforo Inteligente en Tiempo Real

Aplicacion Android desarrollada en **Kotlin** con **Jetpack Compose** que gestiona el aforo de un estadio en tiempo real. Recibe eventos de entrada via **WebSocket**, asigna automaticamente a cada asistente el sector y bloque optimos, y muestra metricas en vivo.

---

## Arquitectura

El proyecto sigue una arquitectura por capas con separacion clara de responsabilidades:

```
com.example.aforoestadio
├── data/
│   ├── model/          -> Modelos de datos (Sector, Block, StadiumState, EntryEvent, etc.)
│   ├── repository/     -> StadiumRepository (fuente unica de verdad con StateFlow)
│   └── websocket/      -> WebSocketClient + ConnectionState
├── domain/
│   └── engine/         -> AssignmentEngine, EventProcessor, DistanceCalculator
├── presentation/
│   ├── navigation/     -> AppNavigation (Scaffold + BottomNavigation + NavHost)
│   ├── ui/             -> MapScreen, MetricsScreen, LogScreen
│   ├── viewmodel/      -> StadiumViewModel
│   └── components/     -> SectorCard, EventCard, KpiCard, ConnectionIndicator, etc.
└── ui/theme/           -> Color, Theme, Type (Material Design 3)
```

### Flujo de datos

```
WebSocket Server (Python)
    │
    ▼
WebSocketClient (OkHttp) ──> Channel<EntryEvent>
    │
    ▼
EventProcessor (Mutex) ──> AssignmentEngine (reglas de negocio)
    │
    ▼
StadiumRepository (StateFlow)
    │
    ▼
StadiumViewModel ──> UI (Compose)
```

### Dependencia de Python

El servidor WebSocket requiere la libreria `websockets`:

```bash
pip install websockets
```

---

## Servidor WebSocket (Python)

La app necesita un servidor WebSocket que emita eventos de entrada simulados. El script `websocket_server.py` esta incluido en la raiz del proyecto.

### Levantar el servidor

```bash
cd /ruta/al/proyecto/AforoEstadio
python3 websocket_server.py
```

Deberias ver:

```
🚀 WebSocket server iniciado en ws://0.0.0.0:8765
```

### Detener el servidor

Presiona `Ctrl + C` en la terminal donde corre el script.

---

## Configuracion de red (WebSocket URL)

La URL del WebSocket se configura en:

```
app/src/main/java/com/example/aforoestadio/data/websocket/WebSocketClient.kt
```

Linea relevante:

```kotlin
private val wsUrl: String = "ws://<IP>:8765"
```

### Segun el dispositivo de prueba:

| Dispositivo | URL | Explicacion |
|-------------|-----|-------------|
| **Emulador Android** | `ws://10.0.2.2:8765` | `10.0.2.2` es la IP especial que el emulador usa para referirse al `localhost` de la maquina host |
| **Dispositivo fisico** | `ws://<IP_LOCAL>:8765` | Usa la IP local de tu computador en la red WiFi. Ejemplo: `ws://192.168.100.90:8765` |

### Requisitos de red para dispositivo fisico

1. El celular y la computadora deben estar en la **misma red WiFi**
2. El servidor Python debe estar corriendo **antes** de abrir la app
3. El `AndroidManifest.xml` debe tener:
   - `<uses-permission android:name="android.permission.INTERNET" />`
   - `android:usesCleartextTraffic="true"` (necesario para `ws://` sin TLS)

---

## Compilar y ejecutar

1. **Levantar el servidor WebSocket:**

```bash
python3 websocket_server.py
```

2. **Configurar la URL** en `WebSocketClient.kt` segun tu dispositivo (emulador o fisico)

3. **Abrir el proyecto** en Android Studio

4. **Sincronizar Gradle** (File > Sync Project with Gradle Files)

5. **Ejecutar la app** en tu dispositivo o emulador

La app se conectara automaticamente al WebSocket y comenzara a procesar eventos sin necesidad de login ni configuracion adicional.


## Historias de usuario implementadas

### H0 - Acceso directo (sin login)
La app abre directamente el panel principal sin autenticacion.

### H1 - Recepcion de eventos en tiempo real
WebSocket con OkHttp, parseo JSON con Kotlinx Serialization, Channel con backpressure.

### H2 - Bloqueo por camiseta multicolor
Camiseta `MULTICOLOR` = rechazo inmediato, siempre.

### H3 - Asignacion estandar por camino mas corto
Colores estandar (`RED`, `GREEN`, `BLACK`, `WHITE`): se asigna al sector mas cercano a la puerta de entrada, bloque disponible en orden C -> B -> A. Si el sector esta lleno, se busca en sectores adyacentes por proximidad.

### H4 - Bloqueo automatico al 70%
Cuando un bloque alcanza el 70% de ocupacion, su estado cambia a `BLOCKED` y no recibe mas asistentes.

### H5 - Regla especial camiseta azul
Camiseta `BLUE`: se intenta asignar al Sector Norte primero, sin importar la puerta de entrada.

### H6 - Fallback camiseta azul a Bloque C
Si Norte esta lleno, se busca un Bloque C disponible en cualquier sector ordenado por proximidad. Si no hay ningun Bloque C disponible, se rechaza.

### H7 - Calculo de distancias
Distancia total = distancia entre sectores + distancia al bloque dentro del sector.

### H8 - Metricas de distancia por bloque
Cada bloque mantiene la lista de distancias y calcula su promedio.

### H9 - Metricas por sector y globales
Distancia media por sector y distancia media global del estadio.

### H10 - Visualizacion en tiempo real (Mapa)
Pantalla con mapa del estadio en layout 2x2, barras de capacidad animadas, estadisticas rapidas.

### H11 - Log de eventos
Feed de eventos procesados con LazyColumn, KPIs de aceptados/bloqueados, maximo 500 eventos en memoria.

### H12 - Indicador de estado WebSocket
4 estados visuales: `Disconnected`, `Reconnecting`, `ConnectedIdle`, `ConnectedWorking`. Reconexion automatica con backoff exponencial (1s -> 2s -> 4s -> ... -> 30s max).

## Concurrencia y thread-safety

- **Channel** (buffer 64): backpressure entre WebSocket y procesador
- **Mutex**: garantiza atomicidad al leer estado, decidir y actualizar
- **StateFlow**: emision reactiva del estado hacia la UI
- **Dispatchers.Default**: procesamiento CPU-bound fuera del hilo principal
