# Plan de Trabajo v3: Fase 1 - Eliminación de Memory Leaks, Prioridades de Eventos y Bugs de Flujo

## Contexto
Implementación de la Fase 1 de buenas prácticas en `tech.sebazcrc.permadeath`, erradicando fugas de memoria por retención de objetos `Player`, corrigiendo prioridades ilegales de eventos según las directrices de Bukkit/Paper, solucionando cortes prematuros de bucle en `DateManager` y purgando archivos huérfanos.

## Tareas Detalladas

### 1. Reemplazo de Colecciones de `Player` por `UUID` (Memory Leaks)
* **`Main.java`:** Cambiar `ArrayList<Player> doneEffectPlayers` por `Set<UUID> doneEffectPlayers = new HashSet<>()`.
* **`PlayerListener.java`:** Cambiar `ArrayList<Player> globalSleeping` por `Set<UUID> globalSleeping = new HashSet<>()` y asegurar remoción en `PlayerQuitEvent`.

### 2. Limpieza de BossBars en Desconexión en `AbyssManager.java`
* Registrar listener de `PlayerQuitEvent` en `AbyssManager` que remueva al jugador de su BossBar de presión y elimine la entrada de `pressureBars.remove(player.getUniqueId())`.

### 3. Corrección de Fuga en Gatos Supernova en `SpawnListener.java`
* Asegurar que `gatosSupernova.remove(cat)` se invoque incondicionalmente antes de verificar `cat == null || !cat.isValid()`, evitando que entidades muertas o descargadas saturen permanentemente el límite de 50 gatos.

### 4. Corrección de Bucle en `DateManager.setDay()`
* Sustituir `if (off.isBanned()) return;` por `if (off.isBanned()) continue;` en el recorrido de `Bukkit.getOfflinePlayers()`.

### 5. Corrección de Prioridades en Eventos (`MONITOR` -> `HIGHEST`)
* **`TotemListener.java`:** Cambiar prioridad de `EventPriority.MONITOR` a `EventPriority.HIGHEST` en `totemNerf`.
* **`SlotBlockListener.java`:** Cambiar prioridad de `EventPriority.MONITOR` a `EventPriority.HIGHEST` en los listeners de inventario cancelados.

### 6. Purga de Archivos y Clases Huérfanas
* Eliminar archivo corrupto `PlayerListener.java-header`.
* Eliminar clase muerta `tech.sebazcrc.permadeath.event.ShulkerEvent`.

### 7. Compilación, Registro y Commit
* Validar compilación limpia (`mvn clean compile`).
* Actualizar `docs/CHANGELOG.md`.
* Commit y `git push` a `origin/fix/core-stability-and-fixes`.
