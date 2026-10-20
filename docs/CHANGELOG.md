# Changelog de Permadeath

Todos los cambios notables en este proyecto serán documentados en este archivo cronológicamente.

## [fix/core-stability-and-fixes] - 2026-10-19
### Inicialización
- Establecimiento de la rama de estabilización del core separada del motor de scripting.
- Incorporación de `.agyignore`, `AGENTS.md` y estructura de documentación en `docs/`.

### Corregido
- **Alineación Progresiva de Recetas (Día 25):** El Fragmento Ancestral y las recetas de forja de armaduras de Netherite ahora se desbloquean estrictamente en el **Día 25** (coincidiendo con el inicio del Death Train y los drops de moldes de netherite en días 25-29). La Medalla de Agua se desbloquea en el **Día 30** (al volverse el agua ácida). Se vinculó la comprobación dinámica de días en el bucle central de `Main.java` (`tickAll()`) para que los cambios de día registren las recetas inmediatamente sin requerir reinicio del servidor.
- **I/O Masivo en Messages:** Implementado sistema de caché en memoria para `mensajes_ES.yml`, `mensajes_EN.yml` y configuración de idiomas por jugador en `Messages.java` y `PlayerDataManager.java`, eliminando decenas de lecturas de disco síncronas por segundo durante el tick del servidor.
- **BossBar en ShellEvent:** Corregida lógica invertida en `ShellEvent.removePlayer(Player)` para remover correctamente a los jugadores en lugar de re-añadirlos.
- **Compatibilidad Folia en BeginningPopulator:** Bifurcada la colocación diferida de estructuras entre `GlobalRegionScheduler` (Folia) y `runTaskLater` (Paper/Spigot), evitando excepciones `NoSuchMethodError`.
- **Compatibilidad Folia en GatoGalacticoTask:** Añadido método de arranque compatible con `RegionScheduler` en Folia, evitando `UnsupportedOperationException` al invocar `runTaskTimer`.
- **Iteración de Chunks en HostileEntityListener:** Corregido corte prematuro de chunks cambiando `return;` por `continue;` al convertir aldeanos en vindicadores.
- **Discord Handshake Asíncrono:** La conexión de JDA y `awaitReady()` en `DiscordManager.java` ahora se ejecuta en un hilo asíncrono con `CompletableFuture.runAsync`, garantizando un arranque instantáneo del servidor y eliminando congelamientos o muertes por Watchdog.
- **Eliminación de Spawns Fantasma en MobFactory:** Corregido `spawnQuantumCreeper` y `spawnEnderQuantumCreeper` en `MobFactory.java` para spawnear directamente la entidad NMS sin generar previamente entidades vanilla temporales que luego eran destruidas dejando referencias muertas.
- **Concurrencia y Guardado Atómico en PlayerDataManager:** Implementado un cerrojo estático `FILE_LOCK`, caché compartida en memoria y guardado atómico con `ATOMIC_MOVE` en `PlayerDataManager.java` y sincronizado con `ResetManager.java`, erradicando pérdida de datos o corrupción por escrituras concurrentes en Folia.
- **Eliminación de Memory Leaks de Jugadores:** Migradas las colecciones de retención de `Player` a `Set<UUID>` en `Main.java` (`doneEffectPlayers`) y `PlayerListener.java` (`globalSleeping`), asegurando limpieza reactiva al desconectarse el jugador.
- **Limpieza de BossBars en AbyssManager:** Implementado listener de `PlayerQuitEvent` en `AbyssManager.java` para destruir la BossBar de presión y remover la referencia al desconectarse del Abismo.
- **Resiliencia de Gatos Supernova:** Asegurada la remoción incondicional de entidades en `SpawnListener.java` (`explodeCat`) mediante bloque `finally`, previniendo la acumulación de gatos muertos en la lista.
- **Corrección de Bucle en DateManager y Main:** Sustituido `return;` por `continue;` al iterar jugadores offline en `DateManager.setDay()` y `Main.generateOfflinePlayerData()`.
- **Corrección de Prioridades Bukkit:** Cambiada la prioridad de `EventPriority.MONITOR` a `EventPriority.HIGHEST` en `TotemListener.java` y `SlotBlockListener.java` para eventos cancelables.
- **Purga de Código Muerto:** Eliminados los archivos huérfanos `PlayerListener.java-header` y la clase duplicada `event.ShulkerEvent`.

### Añadido (Fase 2 - Calculadores Puros y Suite JUnit 5)
- **Desacoplamiento de Lógica de Dominio Puro (Domain Calculators):**
  - Creada clase pura `HealthCalculator` en `tech.sebazcrc.permadeath.util.calc` para cómputo de vida máxima considerando hiper manzanas, sets de armadura de netherite e infernal, orbe de vida y penalizaciones de salud de épocas avanzadas (Día 40: 6 corazones base, Día 60: 2 corazones base). Desacoplada de Bukkit e integrada en `NetheriteArmor.java`.
  - Creada clase pura `TotemFailCalculator` en `tech.sebazcrc.permadeath.util.calc` para cálculo de tótems requeridos (Días 1-39: 1, Días 40-59: 2, Días 60+: 3), normalización y acotamiento de probabilidades [1, 100] y evaluación probabilística. Integrada en `TotemListener.java`.
  - Creada clase pura `StormDurationCalculator` en `tech.sebazcrc.permadeath.util.calc` para cómputo de segundos, horas y game ticks de tormenta generados por muerte de jugadores. Integrada en `PlayerListener.java`.
- **Suite de Testing Automatizado (JUnit 5):**
  - Añadida dependencia `org.junit.jupiter:junit-jupiter:5.10.2` en ámbito `test` al `pom.xml` del módulo `main`.
  - Creadas suites de pruebas exhaustivas:
    - `HealthCalculatorTest`: validación de salud base, bonus de hiper manzanas, bono de netherite/infernal, penalización de días 40 y 60 con/sin orbe y clamp a `MIN_HEALTH`.
    - `TotemFailCalculatorTest`: verificación de requerimientos por época (1, 2 y 3 tótems), acotamiento de probabilidad, tiradas de fallo y ajustes visuales para broadcasts.
    - `StormDurationCalculatorTest`: verificación de duraciones de tormenta (días 1-24, 25-49, 50 y 51-74) y conversión a ticks de Minecraft.

### Corregido (Fase 3 - Seguridad de Recursos, Manejo de Excepciones y Robustez)
- **Corrección Crítica en Spawn de CustomGiant (SpawnListener):** Eliminada la invocación por reflexión de `World.getBiome` que intentaba castearse a `boolean` (arrojando `ClassCastException` silenciosa e impidiendo para siempre el spawn de Gigantes en llanuras). Sustituido por verificación directa de Paper/Bukkit API `event.getLocation().getBlock().getBiome() == Biome.PLAINS`.
- **Prevención de Fallo Fatal en Permaban (PlayerListener):** Protegida la llamada a `p.getLastDamageCause()` en `PlayerDeathEvent` contra valores nulos (muertes por `/kill`, daño custom o vaciado), evitando que una excepción NPE impidiese la ejecución del ban del jugador y el registro de la tormenta.
- **Prevención de NullPointerException en Eventos Masivos:**
  - `PlayerListener`: agregada verificación de `hasDisplayName()` en limpieza de accesorios al morir (`e.getDrops().removeIf`), clicks de bloques con bloques infernales (`PlayerInteractEvent`), detección de ítem de menú en inventario, precedencia de operadores booleanos en crafteo de hiper/súper manzanas doradas (`CraftItemEvent`), y validación preventiva en la cuadrícula de crafteo (`PrepareItemCraftEvent:runCheckForGaps`).
  - `AbyssManager`: protegida la detección de pociones de respiración abisal y cálculo de presión en máscaras ante encantamientos no inicializados.
  - `NetheriteArmor`, `SlotBlockListener`, `GUIListener` y `AccessoryListener`: añadidas guardas de `hasDisplayName()` previas a `getDisplayName().contains(...)` o `startsWith(...)`.
- **Fuga de Recursos y Cierre Seguro (Try-With-Resources):**
  - `PDCLog.java`: encapsulada la escritura en `logs.txt` con `try-with-resources` y método `synchronized` para evitar fugas de file descriptors y colisiones entre hilos en Folia.
  - `ResetManager.java`: stream de `Files.walk` protegido con `try-with-resources`, impidiendo bloqueos de directorios del sistema operativo en Windows al resetear dimensiones.
  - `FileAPI.java`: flujos `InputStream` y `OutputStream` migrados a `try-with-resources` con transferencia directa de streams.
  - `PermadeathBootstrap.java`: extracción de datapacks internos asegurada con `try-with-resources`.
  - `WorldEditPortal.java`: lectura de esquemáticas con validación de formato no nulo y `FileInputStream` cerrado de forma determinista.
  - `BeginningLootTable.java`: verificación de límites de índice previo a insertar loot aleatorio secundario.
- **Aislamiento en Registro de Recetas (RecipeManager):** Modularizado el registro de recetas con método protector `safeRegister(...)` para que fallos o duplicidades en una receta no interrumpan el registro del resto.
- **Transición a Logging Estructurado:** Sustituido `e.printStackTrace()` y `System.out.println` por `Logger.log(Level.SEVERE, ...)` en gestores de persistencia (`BeginningDataManager`, `DateManager`, `EndDataManager`, `PlayerDataManager`, `CauldronDataManager`, `LootManager`).

### Simplificación y Limpieza (/ponytail)
- **Eliminación de Reflexión Obsoleta (`Main.java`):** Purgado bloque de reflexión heredado de Paper 1.15 (`paperConfig.disableCreeperLingeringEffect`) que fallaba silenciosamente en cada carga de mundo en 1.21.8.
- **Modernización con Stdlib (`UpdateChecker.java`):** Reemplazado `org.bukkit.util.Consumer` obsoleto por el estándar `java.util.function.Consumer` de Java, erradicando advertencias de compilación y llamadas deprecadas.

### Modificado (Bloqueo Temporal de la Dimensión del Abismo)
- **Configuración y Control Global:**
  - Añadidos toggles `Toggles.Enable-Abyss: false` y `Toggles.ExtendToDay90: false` en `config.yml` para desactivar el Abismo por defecto en Permadeath 1.4.
  - Implementado `Main.isAbyssEnabled()`, condicionando `isExtendedDifficulty()` para evitar la activación involuntaria de mecánicas abisales.
  - Desactivada la tarea en segundo plano `abyssManager.startSpawnerTask()` durante el arranque del plugin si el Abismo no está habilitado, ahorrando ciclos de CPU y comprobaciones innecesarias de jugadores.
- **Bloqueo de Acceso y Comandos:**
  - `/pdc abyss`: Bloqueado con mensaje informativo indicando que la dimensión se encuentra deshabilitada temporalmente.
  - Autocompletado: Ocultado `"abyss"` en `PDCCommandCompleter` cuando el Abismo está desactivado.
  - `AbyssManager.teleportToAbyss` y `AbyssManager.loadWorld`: Bloqueada la generación y carga de chunks de la dimensión.
- **Interacción y Recetas:**
  - Bloqueado el consumo y activación del Corazón del Abismo en `PlayerListener`.
  - Omitido el registro de recetas abisales (`abyssal_heart`, `abyssal_filter`, `abyssal_mask`) en `RecipeManager` si `isAbyssEnabled()` es falso.

### Restauración y Paridad 1:1 vs `old-master`
- **Receta de Infernal Elytra (`RecipeManager.java`):**
  - Restaurada la llamada huérfana `safeRegister(this::registerIE)` dentro de `registerD60Recipes()`, permitiendo el crafteo legítimo de las Élitros Infernales a partir del Día 60.
- **Protección Antiexploits en The Beginning (`BeginningManager.java`):**
  - Restaurado `onPlace(BlockPlaceEvent)` para marcar cofres colocados por jugadores en `chests.yml`, evitando la generación infinita de loot especial al reabrirlos.
  - Restaurado `onBreak(BlockBreakEvent)` para poblar automáticamente los cofres no abiertos antes de ser destruidos, previniendo la pérdida de ítems o exploits de rotura.
  - Restaurado `onBucket(PlayerBucketEmptyEvent)` y `onWater(BlockDispenseEvent)` para impedir colocar o dispensar cubos de agua en la dimensión The Beginning.
- **Detección de Invisibilidad en The Beginning (`Main.java`):**
  - Corregido el chequeo de mundo en la tarea de remoción de invisibilidad (`worldName.contains("beginning")`), asegurando que ningún jugador pueda permanecer invisible en The Beginning independientemente del prefijo de carpeta del mundo.
- **Reintento de WorldEdit en Día 40 (`Main.java`):**
  - Asegurado que un fallo temporal al verificar WorldEdit en Día 40 no impida reintentar el registro en recargas posteriores vía `/pdc reload`.
