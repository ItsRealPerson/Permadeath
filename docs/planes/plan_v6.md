# Plan de Trabajo v6: Bloqueo Temporal y Seguro de la Dimensión del Abismo

## Objetivo
Deshabilitar y bloquear temporalmente la dimensión del Abismo (`pdc_the_abyss` / `permadeath:abyss`) y sus subsistemas asociados (spawners en segundo plano, comandos, teletransporte y recetas avanzadas), manteniendo la opción configurable mediante toggle en `config.yml`.

## Puntos de Intervención

1. **Configuración (`config.yml`):**
   - Incorporar toggle `Toggles.Enable-Abyss: false` (deshabilitado por defecto para 1.4).

2. **Núcleo del Plugin (`Main.java`):**
   - Método `public boolean isAbyssEnabled()`.
   - Modificar `isExtendedDifficulty()` para requerir `isAbyssEnabled()`.
   - Omitir el inicio de `abyssManager.startSpawnerTask()` si el Abismo está deshabilitado.

3. **Gestor de Dimensión (`AbyssManager.java`):**
   - Bloquear `loadWorld()` y `teleportToAbyss()` cuando `!isAbyssEnabled()`.
   - Mensaje informativo: *"El Abismo se encuentra deshabilitado temporalmente."*

4. **Comandos (`PDCCommand.java` y `PDCCommandCompleter.java`):**
   - `/pdc abyss`: Responder que la dimensión está deshabilitada.
   - Ocultar `"abyss"` en el autocompletado si está deshabilitado.

5. **Activación de Ítems (`PlayerListener.java`):**
   - Bloquear la activación del Corazón del Abismo con mensaje informativo.

6. **Recetas (`RecipeManager.java`):**
   - Condicionar recetas de ítems abisales (`abyssal_heart`, `abyssal_filter`, `abyssal_mask`) a `isAbyssEnabled()`.

7. **Validación:**
   - Compilación con `mvn clean test` y `mvn clean package`.
   - Registro en `docs/CHANGELOG.md`.
   - Commit y push a `master` con subagente Git Release Engineer.
