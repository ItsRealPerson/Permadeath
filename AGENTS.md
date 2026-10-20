# Guía Técnica del Proyecto - Permadeath Core (1.21.8)

## 1. Información General
* **Plataforma:** Paper / Folia (Multi-threaded).
* **Versión de Minecraft:** 1.21.8 (paper-api 1.21.8-R0.1-SNAPSHOT).
* **Java:** 21.
* **Paquete Base:** `tech.sebazcrc.permadeath`.
* **Herramienta de Compilación:** Maven.

## 2. Reglas de Compatibilidad Folia
* Nunca usar `Bukkit.getScheduler()` directamente sin verificar `if (Main.isRunningFolia())`.
* En Folia, usar:
  * `Bukkit.getGlobalRegionScheduler()` para reglas mundiales y temporizadores independientes de chunks.
  * `Bukkit.getRegionScheduler()` para chunks específicos.
  * `player.getScheduler()` / `entity.getScheduler()` para entidades y jugadores.
* Prohibido invocar `runTaskTimer` o `runTaskLater` sobre `BukkitRunnable` en Folia.

## 3. Caché y Rendimiento
* No cargar archivos YAML (`YamlConfiguration.loadConfiguration`) en el bucle de tick (`tickAll` o `tickPlayer`).
* Todos los mensajes e I18N deben mantenerse cargados en memoria y refrescarse solo con `/pdc reload`.

## 4. Registro de Bugs Conocidos y Resueltos
* **Bug Ancestral Fragment (Día 24):** Las recetas base se registraban únicamente en el Día 40. Resuelto inicializando `RecipeManager.registerRecipes()` de forma temprana.
* **Bug ShellEvent removePlayer:** La condición y llamada invertidas añadían al jugador en lugar de removerlo de la BossBar.
* **I/O Masivo en Messages:** Resuelto agregando caché de `FileConfiguration` para ES y EN.
* **Folia NoSuchMethodError en BeginningPopulator:** Resuelto agregando bifurcación condicional entre `GlobalRegionScheduler` y `Bukkit.getScheduler()`.
