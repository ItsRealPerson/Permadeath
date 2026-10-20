# Plan de Trabajo v1: Corrección de Bugs Críticos, Rendimiento y Crafteo

## Contexto
En la rama `fix/core-stability-and-fixes`, se han detectado varios bugs críticos de rendimiento y lógica en el core (`tech.sebazcrc.permadeath`), incluyendo el fallo reportado donde el **Fragmento Ancestral** (`ancestral_fragment`) no puede ser crafteado en el Día 24.

## Tareas a Realizar

### 1. Desbloqueo y Registro Temprano de Recetas Base (Ancestral Fragment & Netherite)
* **Causa raíz:** `RecipeManager.registerRecipes()` se ejecutaba únicamente dentro del bloque `getDay() >= 40` en `Main.java`.
* **Solución:** Inicializar `this.recipes = new RecipeManager(this); this.recipes.registerRecipes();` de forma temprana en `startPlugin()` o en el bloque del Día 1/20, de modo que el Fragmento Ancestral (8 bloques de diamante + 1 lingote de netherite) y la Medalla de Agua estén disponibles cuando el jugador disponga de los materiales (Día 24, etc.).

### 2. Optimización de I/O Masivo en Messages y PlayerDataManager
* **Causa raíz:** `Messages.java` carga y analiza `mensajes_ES.yml` y `mensajes_EN.yml` desde el disco mediante `YamlConfiguration.loadConfiguration(f)` en cada llamada a `getMessageByPlayer` y `getMessageForConsole`.
* **Solución:** Mantener instancias en caché (`FileConfiguration configES`, `FileConfiguration configEN`) que se cargan en el arranque y se refrescan solo en `reloadFiles()`.
* Cachear el idioma de los jugadores para evitar re-parsear `jugadores.yml` en cada tick.

### 3. Corrección del Bug en ShellEvent
* **Causa raíz:** `removePlayer(Player player)` comprueba si la BossBar contiene al jugador y lo agrega con `bossBar.addPlayer(player)`.
* **Solución:** Cambiar a `if (bossBar.getPlayers().contains(player)) bossBar.removePlayer(player);`.

### 4. Corrección de Incompatibilidades Folia vs Spigot / Paper
* **`BeginningPopulator.java`:** Condicionar la llamada a `Bukkit.getGlobalRegionScheduler()` y `Bukkit.getRegionScheduler()` detrás de `if (Main.isRunningFolia())`, usando `Bukkit.getScheduler().runTaskLater()` como alternativa estándar.
* **`SpawnListener.java` / `GatoGalacticoTask.java`:** Asegurar que la ejecución de `GatoGalacticoTask` no invoque `runTaskTimer` de BukkitRunnable si corre en Folia, adaptándola al scheduler regional.

### 5. Compilación y Validación
* Ejecutar compilación con Maven para verificar que todo compila limpiamente sin errores en Java 21 y Paper 1.21.8.
* Registrar cambios en `docs/CHANGELOG.md`.
* Commit y `git push` a `origin/fix/core-stability-and-fixes`.
