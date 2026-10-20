# Plan de Mitigación de Lag y Optimización de Rendimiento v13

## 1. Contexto y Objetivos
Durante la auditoría técnica exhaustiva de Permadeath Core en Paper / Folia 1.21.8, se identificaron varios cuellos de botella severos que provocan caídas drásticas de TPS (lag spikes de 100-500ms y Garbage Collector churn).
El objetivo de este plan es aplicar mitigaciones quirúrgicas bajo la filosofía **/ponytail** (soluciones mínimas, nativas, estándar y de máximo impacto):

1. **Eliminar deserialización NBT/Base64 en ticks continuos:** Añadir caché en memoria `Map<UUID, ItemStack[]>` en `AccessoryInventory`.
2. **Reemplazar escaneos de 25 chunks por iteraciones de jugadores:** Reescribir `MobUtils.getNearestPlayer` para iterar `world.getPlayers()`.
3. **Eliminar escaneo de 256 bloques en spawn de monstruos:** Limpiar `SpawnListener` para no escanear 128 bloques cúbicos en cada intento de spawn.
4. **Eliminar schedulers masivos por entidad:** Solo adjuntar schedulers periódicos a mobs que realmente lo requieran (ej. Ravagers Día 40+), eliminando tareas sobre animales y mobs pasivos.
5. **Caché en memoria de esquemáticas WorldEdit:** Cachear `Clipboard` en `WorldEditPortal` para evitar lectura de disco bloqueante durante la generación de The Beginning.
6. **Guard de paquetes de atributos en `NetheriteArmor`:** Evitar llamar `attribute.setBaseValue` si el valor ya es idéntico, deteniendo el envío masivo de paquetes por segundo.
7. **Filtrar `PlayerMoveEvent` en `BeginningManager`:** Descartar eventos donde el jugador no haya cambiado de bloque.
8. **Optimizar `ChunkLoadEvent`:** Reemplazar 3 llamadas consecutivas a Streams por un único bucle tradicional `for`.
9. **Ajuste de rayos en `SupernovaExplosionSimulator`:** Reducir resolución de 36 a 18 (1.944 rayos vs 7.776), manteniendo la forma cúbica vanilla exacta con un 75% menos de carga.

---

## 2. Archivos Afectados
* `api/src/main/java/tech/sebazcrc/permadeath/util/inventory/AccessoryInventory.java`
* `v1_21_R3/src/main/java/tech/sebazcrc/permadeath/nms/v1_21_R3/utils/MobUtils.java`
* `main/src/main/java/tech/sebazcrc/permadeath/event/entity/SpawnListener.java`
* `main/src/main/java/tech/sebazcrc/permadeath/event/world/WorldEvents.java`
* `main/src/main/java/tech/sebazcrc/permadeath/util/item/NetheriteArmor.java`
* `main/src/main/java/tech/sebazcrc/permadeath/world/beginning/BeginningManager.java`
* `main/src/main/java/tech/sebazcrc/permadeath/world/WorldEditPortal.java`
* `main/src/main/java/tech/sebazcrc/permadeath/util/SupernovaExplosionSimulator.java`

---

## 3. Plan de Verificación
1. `mvn clean compile` para asegurar cero errores de sintaxis en todos los módulos (`api`, `main`, `v1_21_R3`).
2. `mvn test` para validar fórmulas y lógica interna.
3. Empaquetar con `mvn package -DskipTests` y reemplazar en `test-server/plugins/`.
4. Registrar cambios en `docs/CHANGELOG.md` y hacer commit & push obligatorio a GitHub.
