# Plan de Trabajo v5: Fase 3 - Seguridad de Recursos, Manejo de Excepciones y Robustez

## Objetivo
Auditar y corregir el manejo de excepciones, recursos I/O sin cerrar (try-with-resources) y validaciones preventivas de null en eventos de alta frecuencia en el módulo `main` (`tech.sebazcrc.permadeath`).

## Tareas Clave
1. **Auditoría de Bloques Catch Silenciosos:**
   - Localizar capturas genéricas `catch (Exception ignored)` o `catch (Exception e) {}` que enmascaran fallos en base de datos, parseo NBT o lógica de mundo.
   - Reemplazar por logging explícito mediante `Main.getInstance().getLogger().log(...)` o fallbacks controlados.

2. **Garantía de Cierre de Recursos (Try-with-resources):**
   - Asegurar que todo `InputStream`, `OutputStream`, `Reader`, `Writer` y conexiones a archivos/SQLite utilicen bloques `try-with-resources`.

3. **Defensas contra NPE en Listeners de Alta Frecuencia:**
   - Auditar eventos críticos (`PlayerInteractEvent`, `EntityDamageByEntityEvent`, `InventoryClickEvent`) para asegurar verificación estricta de `item != null`, `item.hasItemMeta()` y `entity != null` antes de dereferenciar.

4. **Compilación Limpia y Suite de Pruebas:**
   - Ejecutar `mvn clean test` asegurando 100% de éxito.

5. **Registro y Versionado:**
   - Registrar mejoras en `docs/CHANGELOG.md`.
   - Commit y `git push` a `origin/fix/core-stability-and-fixes`.
