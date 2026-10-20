# Plan de Trabajo v2: Estabilización Integral del Core Main

## Contexto
Enfoque exclusivo en el módulo `main` de la versión 1.4/1.21.8, descartando módulos experimentales posteriores (`pdc-entities`).

## Tareas a Realizar

### 1. Desacoplamiento Asíncrono de Discord en `DiscordManager.java`
* **Problema:** `this.bot.awaitReady()` síncrono en el hilo principal bloquea el arranque del servidor y expone al plugin a muertes por Watchdog si la API de Discord tarda en responder.
* **Solución:** Envolver el inicio y conexión de JDA en un hilo asíncrono (`CompletableFuture.runAsync`), garantizando arranque instantáneo del servidor y envío reactivo de alertas una vez que el Gateway de Discord esté listo.

### 2. Corrección de Doble Spawn y Entidad Fantasma en `MobFactory.java`
* **Problema:** Al spawnear Creepers Cuánticos y Ender Quantum Creepers en 1.21, se spawnea un creeper vanilla temporal que se mata 1 ms después y se retorna esa entidad muerta, causando parpadeo visual y referencias rotas.
* **Solución:** Spawnear directamente la entidad NMS correspondiente (`spawnNMSCustomEntity`) sin instanciar la entidad vanilla temporal previa, retornando la instancia viva válida.

### 3. Sincronización y Protección de Concurrencia en `PlayerDataManager.java`
* **Problema:** Múltiples instancias independientes leían y escribían `jugadores.yml` de forma asíncrona sin un lock estático real, generando pérdida de datos o corrupción por escrituras concurrentes en Folia.
* **Solución:**
  * Crear un lock estático `FILE_LOCK` y una configuración compartida en memoria (`sharedConfig`).
  * Implementar guardado seguro mediante escritura en archivo temporal y movimiento atómico (`ATOMIC_MOVE`).
  * Sincronizar el reset en `ResetManager.java`.

### 4. Compilación, Registro y Commit
* Compilar con Maven (`mvn clean compile`).
* Actualizar `docs/CHANGELOG.md`.
* Commit y `git push` a `origin/fix/core-stability-and-fixes`.
