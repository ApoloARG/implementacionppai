# CU 36 — Consultar seguimiento de bolsines (PPAI DSI 2026 · Bolsines · Grupo impar)

Implementación del CU 36 rediseñado con el **patrón Adapter** para comunicarse con los tres
modelos de GPS Tracker (XTR-4500L, NavTrack QX-7A, GeoPulse MTR-900), contra la API mock
provista por la cátedra (`dsi-api-bolsines`).

| Aspecto | Detalle |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2 (API REST) |
| Tecnología | Web: backend REST + frontend React 18 (Vite + Material UI) |
| Base de datos / persistencia | H2 (en memoria) + Spring Data JPA / Hibernate |
| Mapa | Leaflet + teselas de OpenStreetMap (gratuito, sin API key) |
| GPS Tracker | API REST de la cátedra (`dsi-api-bolsines`) en `http://localhost:8080` |

Misma estructura que la implementación de referencia de la cátedra
(*Administración de Campos*, versión `java-persistencia`).

---

## Arranque rápido (Windows)

Requisitos: **JDK 17** (o un JDK 17 portable descomprimido en `jdk\jdk-17.0.13+11`) y **Node.js 18+**.

1. Clonar o descargar este repo.
2. Doble click en **`INICIAR_DEMO.bat`**.
3. Se abren 3 ventanas (API del GPS en 8080, backend en 8081 y front en 3000) y, cuando está todo arriba, el navegador en `http://localhost:3000`.

La carpeta `dsi-api-bolsines` es una copia de la API mock del GPS Tracker provista por la cátedra
([LiberaeMentis/dsi-api-bolsines](https://github.com/LiberaeMentis/dsi-api-bolsines)).

---

## 1. Cómo correrlo a mano (3 terminales)

Requisitos: **Java 17+**, **Maven** (o el wrapper de la API), **Node 18+**.

```bash
# Terminal 1 — API del GPS Tracker de la cátedra (puerto 8080)
cd dsi-api-bolsines
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run

# Terminal 2 — nuestro backend (puerto 8081)
cd backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run   (o mvn spring-boot:run)

# Terminal 3 — frontend (puerto 3000)
cd front
npm install
npm run dev
```

Abrir **http://localhost:3000**.
Consola de la base: **http://localhost:8081/h2-console** (JDBC URL `jdbc:h2:mem:bolsinesdb`, usuario `sa`, sin contraseña).

### Cambiar de modelo de GPS (mostrar el patrón en la defensa)

En `backend/src/main/resources/application.properties`:

```properties
gps.modelo=XTR-4500L          # o NAVTRACK-QX-7A, o GEOPULSE-MTR-900
```

Reiniciar el backend y repetir la consulta: **el resultado es el mismo** y el Gestor no cambió
ni una línea. Solo cambia qué adaptador concreto se crea en `crearAdaptadorGPS()`.

---

## 2. Guion para la defensa (flujo descripto + alternativos)

La sesión arranca con **Lucía Pérez — EB de la CM Córdoba** (selector arriba a la derecha).

| # | Qué hacer | Qué se ve |
|---|---|---|
| Flujo descripto (pasos 1 a 11) | *Consultar ubicación de bolsines* → elegir el **bolsín 101** (lista o marcador) → **Enviar correo** | CM del usuario, bolsín 101 en el mapa, pregunta de confirmación y "Se ejecutó con éxito el CU 31…" (el correo simulado queda en el log del backend) |
| **A1** No hay bolsines enviados | Cambiar la sesión a **Julián Sosa — CM La Plata** y consultar | "No se encuentran bolsines en estado Enviado…" |
| **A2** Precinto inexistente | En *Filtrar* escribir `PR-9999` | "No se encuentra bolsín con el número de precinto ingresado." |
| **A3** El GPS no informa la ubicación | Consultar con Lucía | 111 y 112 aparecen en "El GPS Tracker no pudo informar la ubicación de…" (el mock no los conoce). Si se apaga la API del GPS, todos caen en A3 |
| **A4** El EB cancela | Botón **Cancelar** | Vuelve al inicio: "Se canceló la consulta…" |
| **A5** No envía el correo | Elegir el 101 → **No enviar** | "No se envió el correo electrónico (A5). Fin del caso de uso." |

Otros datos útiles: **Ramiro Díaz (CM Rosario)** ve el bolsín 102; **Ana Fernández (CM Tucumán)** ve el 105.
Los bolsines 113 (Creado) y 114 (Recibido) de Córdoba **no** se consultan porque no están en estado Enviado.

> Opcional: en `extras/` hay una línea para agregar el bolsín 111 al mock del GPS, así se ven
> dos marcadores en el mapa de Córdoba.

---

## 3. Dónde está cada cosa del modelo

```
backend/src/main/java/com/dsi/bolsines/
├── controller/
│   ├── PantallaConsultarUbicacionBolsin.java   ← Boundary (pantalla) del CU
│   └── SesionDemoController.java               ← solo para la demo (cambiar usuario logueado)
├── service/
│   ├── GestorConsultarUbicacionBolsin.java     ← Control del CU · CLIENTE del Adapter
│   ├── GestorNotificarUbicacionBolsin.java     ← CU 31 incluido (simulado)
│   └── FlujoAlternativoException.java
├── adaptador/gps/                              ← PATRÓN ADAPTER
│   ├── IAdaptadorGPSTracker.java               ← Target (interfaz objetivo)
│   ├── AdaptadorGPSXTR4500L.java               ← Adaptadores concretos
│   ├── AdaptadorGPSNavTrackQX7A.java
│   ├── AdaptadorGPSGeoPulseMTR900.java
│   ├── ServicioGPSXTR4500L.java                ← Adaptables (servicios externos)
│   ├── ServicioGPSNavTrackQX7A.java
│   ├── ServicioGPSGeoPulseMTR900.java
│   └── ClienteHttpGPS.java                     ← utilidad HTTP (no es parte del patrón)
├── boundary/InterfazGoogleMaps.java            ← Boundary del mapa
├── model/                                      ← Entidades (JPA)
├── repository/                                 ← Spring Data JPA
├── dto/                                        ← lo que viaja a la pantalla (nunca entidades)
└── config/CorsConfig.java
```

### Decisiones de implementación (consistentes con la referencia de la cátedra)

- **API REST**: los mensajes *Gestor → Pantalla* del diagrama (`mostrarDatosLocalizacionBolsin`,
  `solicitarConfirmacionEnvioCorreo`, `informarEjecucionExitosa`) se resuelven con el **retorno**
  de cada método del Gestor, igual que en *Administración de Campos*.
- **A la pantalla viajan DTOs**, nunca entidades.
- **Experto**: cada objeto responde por sí mismo (`bolsin.sosEnviado()`, `empleado.sosGerente()`
  → `rol.sosGCM()`, `bolsin.esTuCMOrigen(cm)`), el Gestor no consulta atributos para decidir.
- **Batch hacia el GPS**: el Gestor arma los arreglos de números y códigos de CM y crea **un**
  adaptador con todos; el loop de llamadas queda **dentro** del adaptador (`solicitarDatosLocalizacion`).
- **Formato común del Adapter**: cada fila de `Object[][]` = `[numeroBolsin: Integer, latitud: Double,
  longitud: Double, fechaHoraUltimaActualizacion: LocalDateTime]`.
- **CU 31**: el envío del mail es responsabilidad del CU incluido; acá se simula (log).
- **Mapa**: se mantiene el nombre `InterfazGoogleMaps` por consistencia con el modelo, pero se usa
  OpenStreetMap/Leaflet porque Google Maps exige API key con facturación.
