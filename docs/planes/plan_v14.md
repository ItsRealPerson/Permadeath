# Plan de Corrección v14 - Generación Limpia y Spawn Preciso de The Beginning

## 1. Diagnóstico de los Problemas

### Problema A: Corte cuadrado de ~95 bloques alrededor del portal
* **Causa:** En `BeginningManager.loadWorld()`, la dimensión se inicializaba con:
  ```java
  WorldCreator creator = new WorldCreator(WORLD_NAME);
  creator.environment(World.Environment.THE_END);
  creator.generateStructures(false);
  this.beginningWorld = Bukkit.createWorld(creator);
  ```
  Al no suministrar `creator.generator(new EmptyGenerator())`, Minecraft generaba el terreno vanilla de The End. En Minecraft, la isla central de The End mide exactamente 96 bloques de radio (chunks -6 a +6, $X \in [-96, 96]$ y $Z \in [-96, 96]$), rodeada por un anillo de vacío de 1000 bloques. `BeginningPopulator` intentaba colocar bloques encima, produciendo el corte cuadrado artificial observado.

### Problema B: Teletransporte erróneo a (-96, 100, -16) en lugar de (0, 100, 0)
* **Causa:** Al tocar el bloque `END_GATEWAY`:
  1. `PlayerMoveEvent` y `PlayerTeleportEndGatewayEvent` colisionaban.
  2. En `handlePortalTeleport`, se ejecutaba `event.setCancelled(true)` y `p.teleport(to)`. Al cancelar `PlayerTeleportEvent` en Paper, el teleport asíncrono se abortaba y el tile entity nativo de `TheEndGateway` redirigía al jugador a su destino por defecto de la isla del End vanilla (coordenada exacta `[-96.2, 100.0, -16.4]`).
  3. Para que Paper redirija limpiamente un portal de End Gateway, se debe utilizar `((PlayerTeleportEvent) event).setTo(to)`.

---

## 2. Modificaciones Técnicas

1. **`EmptyGenerator.java`:**
   - Implementar los métodos de Paper 1.21.8:
     - `generateChunkData(WorldInfo, Random, int, int, ChunkData)` retornando el chunk vacío.
     - `shouldGenerateSurface() = false`
     - `shouldGenerateCaves() = false`
     - `shouldGenerateDecorations() = false`
     - `shouldGenerateStructures() = false`
     - `shouldGenerateMobs() = true`

2. **`BeginningManager.java`:**
   - Pasar `creator.generator(new EmptyGenerator())` en `loadWorld()`.
   - En `handlePortalTeleport`, si el evento es un `PlayerTeleportEvent`, redirigir usando `((PlayerTeleportEvent) event).setTo(to)` en vez de cancelar el evento y forzar `p.teleport()`.
   - Establecer el punto de aterrizaje en `Location(beginningWorld, 0.5, 100.0, 0.5, 90f, 0f)` (sobre la plataforma frente al portal de regreso).

3. **Limpieza del Mundo de Pruebas:**
   - Detener el servidor.
   - Eliminar la carpeta `test-server/world_permadeath_beginning` para que se regenere con el generador de vacío puro.
   - Reiniciar `theBeginning.yml` para que regenere las esquemáticas del portal en el nuevo mundo vacío.
   - Iniciar el servidor y verificar.
