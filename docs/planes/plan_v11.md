# Plan v11: Simulación de Cráter Voxelizado Imperfecto Estilo Minecraft (Rugoso y Cúbico)

## 1. Visión y Estética del Jugador

El usuario señala un principio fundamental de la jugabilidad y estética de Minecraft:
> *"No es una cuenca la de las explosiones normales, es un cuadrado o algo muy imperfecto y eso le daba su toque porque al ser imperfecto tosqueaba a la persona en la zona si sobrevivía."*

Un cráter suave, esférico o elíptico (estilo meteorito o pincel de WorldEdit) pierde la identidad de Minecraft. Las explosiones de Minecraft son icónicas precisamente porque:
1. **Geometría Cúbica / Facetada:** Los rayos se originan en las caras de un cubo ($16 \times 16 \times 16$), dando una tendencia facetada cúbica con bordes semi-rectangulares.
2. **Escalonamiento Voxel Discreto y Terreno Agreste:** Hay cambios abruptos de altura de 2 a 5 bloques de golpe (acantilados verticales, cornisas flotantes, agujeros estrechos y bloques sueltos).
3. **Obstáculo Táctico ("Tosqueo"):** El terreno resultante es caótico e irregular, haciendo que un jugador con armadura o tótems que sobreviva no pueda correr ni escapar fácilmente (se queda trabado en desniveles de 2 bloques, pozos ciegos y paredes empinadas).

---

## 2. Diagnóstico Confirmado de la Prueba en Vivo

En la prueba realizada en el servidor en vivo en las coordenadas $(118, 71, 42)$:
- A $Y = 60$ (11 bloques de profundidad): El terreno fue excavado (`Test passed: Air`).
- A $Y = 45$ (26 bloques de profundidad): El terreno ya era sólido (`Test failed: Stone`).
- A distancia horizontal de 22 bloques: El terreno ya era sólido.

**Causa:**
El motor de Minecraft con poder 200 solo penetra $\approx 20 - 25$ bloques en piedra porque cada bloque de roca absorbe $7.05$ de poder, y a partir de $R > 20$ los 1.352 rayos se separan tanto que se extinguen contra las paredes de piedra sin llegar a conectar entre sí.

---

## 3. Modelo Matemático del Cráter Voxelizado Imperfecto (Estilo Minecraft)

Para recrear esa misma forma tosca, cúbica e imperfecta a la escala deseada (más grande que los 20 bloques de vanilla, ej. 40–60 bloques de ancho):

### A. Métrica de Distancia Híbrida (Euclídea + Chebyshev / Manhattan)
En lugar de una distancia puramente radial $r = \sqrt{x^2 + y^2 + z^2}$, usamos una combinación con la métrica de Chebyshev ($L_\infty = \max(|x|, |z|)$):
$$\mathcal{D}_{voxel}(x, y, z) = (1 - \beta) \cdot \frac{\sqrt{x^2 + z^2}}{R_h} + \beta \cdot \frac{\max(|x|, |z|)}{R_h} + \left(\frac{y}{R_v}\right)^2$$
Con $\beta \approx 0.35 - 0.45$:
- Crea una forma **cuadrada con esquinas biseladas**, idéntica al patrón de dispersión de rayos de un cubo de Minecraft.

### B. Cuantización Escalonada y Ruido Celular Discreto
Para generar escalones pronunciados de 2 a 4 bloques que "tosqueen" el movimiento del jugador:
$$H_{escalon}(x, z) = \text{floor}\left( \text{Noise}(x \cdot f, z \cdot f) \times K_{steps} \right) \cdot \Delta h$$
- Modula el umbral de destrucción para que ciertas columnas de piedra resistan y otras se desplomen, generando **cornisas voladizas, pilares partidos y zanjas irregulares**.

### C. Resistencia Voxel Diferenciada
- **Tierra / Césped / Madera:** Umbral de evaporación bajo (desaparecen en un radio amplio).
- **Piedra / Minerales:** Umbral medio (quedan paredes escarpadas y columnas).
- **Obsidiana / Bloques Reforzados:** Resisten, quedando expuestos como salientes.

---

## 4. Beneficios Técnicos y de Rendimiento
1. **0 Lag:** Se evalúa de forma analítica por bloque sin trazar 1.352 rayos flotantes.
2. **Escala Controlada:** Podemos definir exactamente el tamaño visual (ej. 45 bloques de ancho, 25 bloques de profundidad) con la misma textura rota de Minecraft.
3. **Fidelidad al Permadeath Clásico:** Mantiene la dificultad extrema y el terreno intransitable tras la detonación.
