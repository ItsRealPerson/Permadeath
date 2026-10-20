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

