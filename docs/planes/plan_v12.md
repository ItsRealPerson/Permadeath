# Plan v12: Simulación de Supernova por Ray-Marching Cúbico Vanilla Puro (Destrucción Total)

## 1. Corrección Inmediata a la Retroalimentación del Usuario

El usuario reportó de forma contundente:
> *"ehhh mira la cosa es que no deberia de generar bloques me hiciste una esfera toda rara y de por si no es a como queria esta horrible osea debe desstruir no convertir"*

### Errores del enfoque anterior (Plan v10/v11):
1. **Generación Indeseada de Bloques (Vitrificación):** Se intentó colocar magma, obsidiana llorosa y cobblestone en el perímetro. En el aire abierto, esto creó una cúpula/esfera flotante grotesca en lugar de una explosión.
2. **Debe ser 100% destructivo:** Una explosión en Minecraft jamás transmuta piedra en magma ni genera bloques flotantes; únicamente vacía y pulveriza el terreno a `Material.AIR`.
3. **Pérdida de la Esencia Voxel:** La función matemática elipsoidal se percibía artificial como una esfera deformada.

---

## 2. El Verdadero Algoritmo de Explosión de Minecraft (NMS / Mojang)

En el código fuente de Mojang (`Explosion.java` / `ServerExplosion.java`), las explosiones de Minecraft son imperfectas, cúbicas y escalonadas debido a un principio geométrico exacto:

1. **Emisión de Rayos desde las Caras de un Cubo:**
   Los rayos no se distribuyen en una esfera perfecta, sino desde los bordes y caras de un cubo de $[-1, 1]^3$:
   ```java
   double d0 = (double)((float)i / (float)(N - 1) * 2.0F - 1.0F);
   double d1 = (double)((float)j / (float)(N - 1) * 2.0F - 1.0F);
   double d2 = (double)((float)k / (float)(N - 1) * 2.0F - 1.0F);
   double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
   d0 /= d3; d1 /= d3; d2 /= d3;
   ```
   Esta proyección de un cubo sobre una esfera concentra rayos en las esquinas y aristas, produciendo la silueta cuadrada/facetada característica de Minecraft.

2. **Energía Aleatoria por Rayo:**
   Cada rayo individual posee una potencia fluctuante:
   $$P_{ray} = \text{Poder} \times (0.7 + \text{random} \times 0.6)$$

3. **Atenuación Física por Resistencia de Bloque:**
   En cada paso discretizado ($\Delta s = 0.35$ m):
   $$P \leftarrow P - (R_{bloque} + 0.3) \times 0.3 - 0.225$$
   - Si $P > 0$, el bloque se añade al conjunto de destrucción `toBlow`.
   - Si golpea tierra o madera ($R \le 2$), el rayo sigue avanzando.
   - Si golpea piedra ($R = 6$), pierde potencia rápidamente.
   - Si golpea obsidiana ($R = 1200$) o bedrock, se extingue al instante.

4. **Escalonamiento Natural y "Tosqueo":**
   Debido a que rayos contiguos tienen potencias aleatorias distintas y atraviesan composiciones de roca diferentes, al llegar al límite exterior se forman cornisas, salientes de 1 a 4 bloques, zanjas y pozos que atrapan al jugador si sobrevive.

---

## 3. Arquitectura del Nuevo `SupernovaExplosionSimulator`

1. **Cero Creación de Bloques:**
   - Prohibido modificar bloques a cualquier material distinto de `Material.AIR`.
   - Si `breakBlocks == false`, no se toca ningún bloque (solo daño a entidades).

2. **Ray-Marching Cúbico Denso de Alto Rendimiento:**
   - Para que una explosión de gran poder no deje agujeros ni líneas finas ("agujas"), aumentamos la resolución del cubo emisor a $N = 36$ (unos 7.500 rayos direccionales).
   - Trazado de rayos en memoria ultrarrápido con comprobación de resistencia (`block.getType().getBlastResistance()`).
   - Todos los bloques afectados se recopilan en un `Set<Long>` (coordenadas empaquetadas para evitar miles de asignaciones de objetos).

3. **Vaciado Eficiente sin Lag (Time-Sliced Air):**
   - Los bloques se convierten a `AIR` mediante `b.setType(Material.AIR, false)` (sin updates físicos en cascada que congelen el servidor).
   - Se distribuyen las capas en 2 o 3 ticks para asegurar 20.0 TPS estables.

4. **Onda de Choque y Daño a Entidades:**
   - Daño masivo en el centro e impulso vertical y radial a entidades vivas.
