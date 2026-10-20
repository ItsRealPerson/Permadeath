# Plan de Trabajo v4: Fase 2 - Desacoplamiento de Calculadores de Dominio Puro y Suite de Tests Unitarios JUnit 5

## Contexto
Implementación de la Fase 2 de buenas prácticas y arquitectura en `tech.sebazcrc.permadeath`: extracción de funciones de cálculo matemático de dominio puro (sin acoplamiento a Bukkit/NMS), integración en el código existente y creación de una suite de pruebas unitarias automatizadas con JUnit 5.

## Tareas Detalladas

### 1. Creación de Calculadores Puros en `tech.sebazcrc.permadeath.util.calc`
* **`HealthCalculator.java`:**
  * Función estática pura: `double calculateMaxHealth(long day, boolean hasOrb, boolean ateHyperOne, boolean ateHyperTwo, int netheritePieces, int infernalPieces)`.
  * Sin efectos secundarios (no aplica pociones).
  * Manejo exacto de bonificaciones (+4 HP manzanas hiper, +8 HP armadura netherite completa, +10 HP armadura infernal completa) y penalizaciones (-8 HP en día 40, -8 HP en día 60, -16 HP en día 60 si falta el orbe de vida).
* **`TotemFailCalculator.java`:**
  * `int getRequiredTotems(long day)`: Devuelve 1 (Día < 40), 2 (Día 40-59), 3 (Día 60+).
  * `int clampProbability(int failProb)`: Asegura rango válido [1, 100].
  * `boolean rollsFailure(int failProb, int roll)`: Comprueba si el tiro aleatorio resulta en fallo.
* **`StormDurationCalculator.java`:**
  * `long calculateStormTicks(long day)`: Calcula la duración en ticks de la tormenta según el día de progresión.
  * `long calculateStormHours(long day)`: Calcula las horas equivalentes.

### 2. Integración en el Código Existente
* Delegar el cálculo en `NetheriteArmor.getAvailableMaxHealth` hacia `HealthCalculator.calculateMaxHealth(...)`.
* Delegar en `TotemListener.java` hacia `TotemFailCalculator`.
* Delegar en `PlayerListener.java:loadTicks` hacia `StormDurationCalculator`.

### 3. Configuración de JUnit 5 en Maven
* Agregar dependencia `org.junit.jupiter:junit-jupiter` (versión 5.10.2, scope `test`) en el `pom.xml` raíz o de `main`.
* Configurar `maven-surefire-plugin` si es necesario.

### 4. Creación de Tests Unitarios Automatizados en `src/test/java`
* `HealthCalculatorTest.java`: Tests parametrizados y exhaustivos para días 1, 25, 40, 50, 60, comprobando orbe, armaduras y manzanas hiper.
* `TotemFailCalculatorTest.java`: Validación de tótems requeridos y porcentajes en días 1-39, 40-59 y 60+.
* `StormDurationCalculatorTest.java`: Validación de duración de tormentas en todas las eras.

### 5. Compilación y Ejecución de Tests
* Ejecutar `mvn test` para certificar que todos los tests pasan en verde (100% SUCCESS).
* Actualizar `docs/CHANGELOG.md`.
* Commit y `git push` a `origin/fix/core-stability-and-fixes`.
