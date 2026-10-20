# Plan de Trabajo v7: Auditoría Comparativa de Funcionalidades vs `old-master`

## Objetivo
Comparar exhaustivamente todas las funcionalidades, eventos, mecánicas de días, comandos y subsistemas entre la rama histórica `old-master` (código fuente original de Permadeath 1.3/1.20) y la rama actual `master` (Permadeath 1.4-PRE2 / 1.21.8 Paper/Folia), asegurando que ninguna mecánica original se haya perdido, roto o alterado incorrectamente durante las migraciones.

## Fases de la Auditoría

### 1. Desglose y Mapeo de Listeners y Eventos
* **`event/entity/SpawnListener.java`**:
  * Spawns especiales por día: Creepers cuánticos, esqueletos con equipamiento y flechas de poción, arañas, phantoms, brujas, ghasts en el Overworld, gigantes, etc.
* **`event/player/PlayerListener.java`**:
  * Ticks de tormenta por muerte, durabilidad de armaduras, consumo de manzanas doradas (Super y Hyper GAP), crafteos restringidos, efectos de sueño y camas.
* **`event/entity/TotemListener.java`**:
  * Probabilidades de fallo de tótem, consumo de 1, 2 o 3 tótems según la era.
* **`event/raid/RaidEvents.java` & `WorldEvents.java` & `BlockListener.java`**:
  * Invasiones personalizadas, agua hirviendo/ácida, rotura de bloques prohibidos.

### 2. Progresión de Días y Toggles
* **`Main.java:tickAll()` y `DateManager.java`**:
  * Verificación de que cada día aplique sus reglas correspondientes.
* **`RecipeManager.java` & `PermadeathItems.java`**:
  * Disponibilidad correcta de ítems por día (Día 25: Netherite & Fragmento Ancestral, Día 30: Medalla de Agua, Día 40: Reliquia del Fin, Día 50: Armadura Infernal, Día 60: Orbe de Vida y Reliquia del Comienzo).

### 3. Jefes y Tareas del End
* **`EndManager.java` & `EndTask.java` & `DemonPhase.java`**:
  * Lluvia de rayos, TNT, ataques 360°, fases de furia del Dragón.

### 4. Reporte de Divergencias y Plan de Corrección
* Clasificar las diferencias entre:
  * A) Mejoras arquitectónicas (Folia, atómico, JUnit, etc. - MANTENER).
  * B) Ajustes intencionales de modernización (1.21.8 Data Components / Trims / Shaders - MANTENER).
  * C) Mecánicas faltantes o rotas inadvertidamente (CORREGIR).
