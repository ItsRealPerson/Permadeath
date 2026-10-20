# Plan de Implementación v16: Erradicación del Corte Cuadrado de 96 Bloques en The Beginning

## 1. Diagnóstico del Problema (El Corte Cuadrado de 96 Bloques)
- **Síntoma:** Al entrar a `The Beginning`, alrededor del portal en `(0, 100, 0)` existe un vacío cúbico/cuadrado exacto de 96 bloques de radio ($cx \in [-6, 6], cz \in [-6, 6]$). A partir de la coordenada 96, las islas de Purpur comienzan abruptamente con bordes cortados en seco.
- **Causa Raíz Identificada:**
  1. Paper carga las dimensiones de datapack (`permadeath:beginning`) durante el arranque del servidor (`Preparing start region for dimension permadeath:beginning`) **antes** de que los plugins de Bukkit sean habilitados (`onEnable`).
  2. En ese momento, Paper genera y almacena en caché los chunks de spawn ($[-6, 6]$ en X y Z). Dado que el datapack define un generador plano vacío (`minecraft:the_void`), esos chunks nacen como vacío puro.
  3. Permadeath solo registraba `BeginningPopulator` dentro de `loadWorld()` en `onEnable()`. Los `BlockPopulator` de Bukkit **solo** se ejecutan en chunks nuevos que se generan por primera vez; nunca en chunks ya generados y cacheados.
  4. Por ende, los spawn chunks permanecían vacíos de por vida, mientras que los chunks exteriores a 96 bloques se generaban con el populator activo cuando el jugador se desplazaba, creando el corte cuadrado.
  5. Adicionalmente, `EmptyGenerator` no sobreescribía `getDefaultPopulators()`, y `Main.getDefaultWorldGenerator()` no mapeaba correctamente `world_permadeath_beginning`.

## 2. Solución Arquitectónica
1. **Poblado Retroactivo de Chunks de Spawn ([`BeginningPopulator.java`](file:///C:/Users/Pers0n/Documents/GitHub/Permadeath/main/src/main/java/tech/sebazcrc/permadeath/world/beginning/BeginningPopulator.java)):**
   - Implementado `populateExistingChunk(Chunk chunk)` que evalúa el ruido determinista `lowGenerator.noise(...)` y deposita las capas de `PURPUR_BLOCK` respetando bloques no-aire existentes (evitando tocar el portal).
   - Generación de árboles corruptos e islas secundarias respetando un radio de seguridad respecto al portal.
2. **Escaneo Automático de Área de Spawn ([`BeginningManager.java`](file:///C:/Users/Pers0n/Documents/GitHub/Permadeath/main/src/main/java/tech/sebazcrc/permadeath/world/beginning/BeginningManager.java)):**
   - En `loadWorld()` y al ingresar por el portal (`handlePortalTeleport`), se invoca `populateSpawnArea()`, escaneando los chunks de radio $[-7, 7]$ para poblar cualquier bloque de aire pendiente según la función de ruido.
3. **Mapeo de Generador en `Main.java` y `EmptyGenerator.java`:**
   - `getDefaultPopulators()` en `EmptyGenerator` ahora retorna `BeginningPopulator`.
   - `getDefaultWorldGenerator()` en `Main.java` detecta cualquier mundo con `beginning` o `abyss`.
   - Registrado en `bukkit.yml` el generador `Permadeath` para `world_permadeath_beginning`.
