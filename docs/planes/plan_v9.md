# Plan v9: Diagnóstico y Propuesta de Optimización de Alto Rendimiento para la Explosión de Gatos Supernova

## 1. Contexto y Diagnóstico del Problema

Actualmente, cuando un **Gato Supernova** detona (programado 30 segundos tras el broadcast de alerta en `SpawnListener.java`), se ejecuta la siguiente llamada estándar de Bukkit:

```java
int configPower = plugin.getConfig().getInt("Toggles.Gatos-Supernova.Explosion-Power"); // 200 por defecto
float power = (float) configPower;
boolean breakBlocks = plugin.getConfig().getBoolean("Toggles.Gatos-Supernova.Destruir-Bloques"); // true
boolean placeFire = plugin.getConfig().getBoolean("Toggles.Gatos-Supernova.Fuego"); // false

w.createExplosion(loc, power, placeFire, breakBlocks, cat);
```

### ¿Por qué `createExplosion` con Poder 200 congela o tumba el servidor?

1. **Raycasting Cúbico Masivo de Mojang:**
   - La implementación interna de Minecraft (`Explosion.explode()`) lanza un mallado 3D de **16 × 16 × 16 = 4.096 rayos vectoriales** en todas direcciones.
   - Cada rayo se propaga por pasos infinitesimales evaluando la dureza (`block.getBlastResistance()`) de cada bloque hasta agotar el poder (200).
   - En una explosión con poder 200, esto genera entre **800.000 y 1.500.000 comprobaciones de estado de bloque** en un solo tick síncrono.

2. **Destrucción y Carga de Cientos de Chunks:**
   - Un radio esférico de 200 bloques cubre un volumen de hasta 400×400 bloques, lo que abarca **más de 625 chunks** (25 × 25 chunks).
   - Si los chunks vecinos no están en memoria, Paper intenta cargarlos síncronamente, bloqueando el hilo principal (`Server thread`).
   - En **Folia**, tocar bloques o entidades en chunks más allá del radio local de la región viola la afinidad de hilos (`IllegalStateException: Asynchronous block access` o `Region contention`).

3. **Inundación de Entidades (Tile Drops) y Motor de Iluminación:**
   - La explosión pulveriza entre **40.000 y 120.000 bloques**.
   - Cada bloque destruido notifica a sus 6 vecinos, activa físicas de gravedad (arena, grava, agua), y somete al motor de iluminación (Starlight) a recalcular la propagación de luz para decenas de miles de bloques aéreos expuestos.
   - Los bloques destruidos generan potencialmente miles de entidades de ítems (`Item drop`), llevando el recuento de entidades al límite y colapsando los TPS a 0.

---

## 2. Opciones de Optimización Analizadas

| Enfoque | Descripción | Impacto Visual / Jugable | Rendimiento (TPS) | Compatibilidad Folia/Paper |
| :--- | :--- | :--- | :--- | :--- |
| **Opción 1: Cráter Físico Realista + Onda de Choque Sin Lag (Recomendada)** | Se limita el cráter destructivo directo a un radio masivo pero seguro (ej. poder 20-30, que ya crea un cráter apocalíptico de 40-60 bloques de diámetro). El resto del poder (hasta 200 bloques) se propaga como **onda de choque sónica/térmica**: daño letal escalonado por distancia a jugadores/mobs, empuje explosivo (knockback), partículas expansivas (`EXPLOSION_EMITTER`) y sonido atronador (`ENTITY_GENERIC_EXPLODE` + `ENTITY_DRAGON_FIREBALL_EXPLODE`). Además, se desactivan los drops de bloques rotos (`setYield(0)` o limpieza de ítems). | **100% fiel al terror de Permadeath** (mata a todo lo que esté cerca y deja un cráter enorme), pero sin destruir el mapa a 400 bloques ni generar millones de ítems. | **Cero lag (20 TPS estables)**. Tiempo de ejecución < 5ms. | **Perfecta**. No toca chunks fuera de la región local. |
| **Opción 2: Destrucción Fraccionada en Ticks (Time-Sliced Asíncrono)** | Se calcula el volumen de bloques a destruir y se divide en lotes de 1.000 bloques por tick a lo largo de varios segundos mediante un `BukkitRunnable` o `RegionScheduler`. | El cráter de 200 bloques se crea lentamente como un desmoronamiento gradual. | Evita el congelamiento instantáneo, pero mantiene al servidor con carga pesada y FPS bajos en los clientes durante 10-20 segundos. | Compleja en Folia por la sincronización entre ticks en regiones limítrofes. |
| **Opción 3: Limitar Potencia en Configuración (Vía Rápida)** | Establecer `Explosion-Power` en un valor razonable (ej. 25-40) directamente en `config.yml`. | Reduce el tamaño del cráter y del rango letal si no se añade la onda de choque. | Bueno si el valor es <= 25. | Alta. |

---

## 3. Propuesta de Arquitectura e Implementación (Opción 1)

1. **Separación de Destrucción Física vs Daño de Onda de Choque:**
   - **Cráter Físico:** Ejecutar una explosión controlada con potencia máxima acotada (por ejemplo, `Math.min(configPower, 25.0f)` para el terreno), asegurando que `doTileDrops` no inunde el suelo de basura.
   - **Onda de Choque Radial (Radio configurable hasta 200 bloques):**
     - Obtener las entidades vivas en el área local cargada mediante `world.getNearbyEntities(loc, radius, radius, radius)`.
     - Para cada entidad, calcular el daño inversamente proporcional a la distancia:
       $$\text{Daño} = \text{configPower} \times \left(1.0 - \frac{\text{distancia}}{\text{radio}}\right) \times 2.0$$
     - Si la entidad está a menos de 20 bloques: muerte instantánea / daño masivo (500+ HP).
     - Aplicar vector de empuje cinético radial alejándola del epicentro.
   - **Efectos Inmersivos:**
     - `Particle.EXPLOSION_EMITTER` en el centro y a lo largo de 8 puntos cardinales.
     - Sonido global `Sound.ENTITY_GENERIC_EXPLODE` con volumen alto y tono bajo para que retumbe en todo el mapa.
