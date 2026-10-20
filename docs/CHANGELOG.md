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

