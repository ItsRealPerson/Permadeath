# Plan de Implementación v15: Corrección de Teletransporte a The Beginning y Reinicio Limpio

## 1. Diagnóstico del Problema
- **Problema de Coordenadas (-17, 92, 2656):**
  Al entrar al portal en Overworld (ubicado en `X=-17, Z=2656`), el jugador aparecía en `world_permadeath_beginning` en las mismas coordenadas horizontales (`-17, 92, 2656`) en vez del portal de salida en `(0.5, 100.0, 0.5)`.
  - **Causa raíz:** `TheEndGatewayBlockEntity` de Paper/Minecraft ignora `tpEvent.setTo()` cuando el teletransporte cruza dimensiones y envía al jugador a las coordenadas $(X, Z)$ del bloque origen. Adicionalmente, el servidor de prueba estaba ejecutando el JAR previo generado a las `03:25:22` sin la cancelación explícita del evento.
  - **Solución implementada:** Cancelar inmediatamente los eventos de teletransporte nativos del portal (`e.setCancelled(true)`) tanto en `PlayerTeleportEndGatewayEvent` como en `PlayerTeleportEvent` (con causa `END_GATEWAY`), y programar un teletransporte de plugin al siguiente tick (`runNextTickTeleport`) garantizando la carga asíncrona del chunk de destino (`getChunkAtAsync(to)`).

- **Corte Cuadrado de Terreno en (0,0):**
  - **Causa raíz:** En ejecuciones previas, Paper generó los chunks nativos de The End alrededor de (0,0) antes de aplicar `EmptyGenerator`. Al estar esos chunks cacheados en disco (`region/r.0.0.mca`), conservaban el corte de 95 bloques.
  - **Solución implementada:** Eliminar `test-server/world_permadeath_beginning` y resetear `GeneratedBeginningPortal` en `theBeginning.yml` para regenerar la dimensión limpiamente con `EmptyGenerator`.

## 2. Acciones de Ejecución
1. Compilar el código con Maven (`mvn clean package -DskipTests`).
2. Copiar `main/target/Permadeath.jar` a `test-server/plugins/Permadeath.jar`.
3. Borrar `test-server/world_permadeath_beginning`.
4. Restablecer `GeneratedBeginningPortal: false` en `theBeginning.yml`.
5. Iniciar el servidor Paper en segundo plano.
6. Realizar commit y push a GitHub con subagente.
