# Plan v10: Formulación Matemática y Física para la Simulación Procedural de la Supernova

## 1. Diagnóstico: ¿Por qué `createExplosion(loc, 200)` NO produce un cráter masivo en el juego?

Aunque en la configuración se defina `Explosion-Power: 200`, el motor de Minecraft vanilla (`ServerExplosion`) no genera un cráter de 200 bloques de radio por razones matemáticas y físicas inherentes al código de Mojang:

### A. Atenuación Exponencial por Resistencia de Explosión (*Blast Resistance*)
El algoritmo de Mojang traza rayos vectoriales donde la potencia $P$ se degrada en cada paso de $\Delta s = 0.3$ bloques según la fórmula:
$$P_{t+1} = P_t - \left(\frac{R_{bloque} + 0.3}{1} \times 0.3\right) - 0.225$$
Donde $R_{bloque}$ es la resistencia a explosiones del bloque:
- **Tierra / Césped:** $R = 0.5 \implies \Delta P = 0.465$ por paso.
- **Piedra / Deepslate:** $R = 6.0 \implies \Delta P = (6.3 \times 0.3) + 0.225 = \mathbf{2.115}$ de potencia consumida cada 0.3 bloques.

**Cálculo de penetración en piedra:**
$$\text{Distancia máxima en piedra} = \frac{200}{2.115} \times 0.3 \approx \mathbf{28.3 \text{ metros}}$$
En piedra pura, un poder de 200 **se extingue completamente a los 28 bloques de profundidad**.

### B. Divergencia Angular de los Rayos (Efecto "Colador" $1/r^2$)
Minecraft lanza únicamente **1.352 rayos** (generados desde el perímetro de un cubo de $16 \times 16 \times 16$).
La densidad de rayos por unidad de superficie esférica decae con el cuadrado del radio:
$$\rho(r) = \frac{N_{rayos}}{4\pi r^2} = \frac{1.352}{4\pi r^2}$$

- A $r = 5$ bloques: Área $= 314 \text{ m}^2 \implies \mathbf{4.3 \text{ rayos/bloque}}$ (destrucción continua).
- A $r = 25$ bloques: Área $= 7.854 \text{ m}^2 \implies \mathbf{0.17 \text{ rayos/bloque}}$ (solo 1 de cada 6 bloques recibe un rayo).
- A $r = 50$ bloques: Área $= 31.415 \text{ m}^2 \implies \mathbf{0.04 \text{ rayos/bloque}}$ (solo 1 de cada 25 bloques recibe un rayo).

A distancias superiores a 25 bloques, los rayos están tan separados entre sí que atraviesan los huecos sin colisionar con los bloques intermedios, creando agujeros aislados o deteniéndose de inmediato, lo que explica por qué el cráter visualmente parece mucho más pequeño de lo esperado pese a devorar CPU comprobando rayos al aire.

---

## 2. Formulación Matemática de la Simulación Procedural

En lugar de delegar en el raycasting imperfecto de Mojang, diseñamos un modelo matemático continuo basado en la física de impacto astronómico y detonación termonuclear:

### A. Modelo Geométrico del Cráter (Semi-Elipsoide de Impacto)
El volumen principal del cráter de la supernova se rige por un semi-elipsoide parametrizado:
$$\left(\frac{x - x_0}{R_x}\right)^2 + \left(\frac{y - y_0}{R_y}\right)^2 + \left(\frac{z - z_0}{R_z}\right)^2 \le 1 \quad \text{para } y \le y_0$$
- $R_x = R_z = R_0$ (Radio horizontal del cráter, ej. 30 a 50 bloques).
- $R_y = \alpha R_0$ con $\alpha \in [0.4, 0.6]$ (Profundidad vertical, simulando la amortiguación del choque contra la corteza terrestre).

### B. Perturbación Fractal / Ruido Procedural (Bordes Naturales y Escarpados)
Para evitar que el cráter parezca una esfera artificial de WorldEdit, perturbamos el radio efectivo en cada coordenada esférica $(\theta, \phi)$ mediante dos octavas de ruido continuo (Simplex / OpenSimplex Noise):
$$R(\theta, \phi) = R_0 \cdot \left[1 + A_1 \cdot \mathcal{N}(f_1 x, f_1 y, f_1 z) + A_2 \cdot \mathcal{N}(f_2 x, f_2 y, f_2 z)\right]$$
- $A_1 = 0.20, f_1 = 0.08$ (Deformaciones de gran escala: crestas, bahías y derrumbes).
- $A_2 = 0.08, f_2 = 0.25$ (Rugosidad rocosa de alta frecuencia).

### C. Cresta Periférica de Eyección (*Crater Rim Elevation*)
En explosiones masivas, el material expulsado del núcleo se deposita en la corona exterior del cráter:
Para $r \in [R_0, R_0 + W_{rim}]$:
$$\Delta y_{rim}(r) = H_{rim} \cdot \exp\left(-\frac{(r - R_0)^2}{2\sigma^2}\right)$$
Eleva el terreno en el borde de 2 a 4 bloques con eyección de roca triturada (`Cobblestone`, `Gravel`, `Coarse Dirt`).

### D. Zonificación Térmica y Vitrificación de Bloques
La supernova no solo pulveriza, sino que altera termodinámicamente los materiales que sobreviven en el lecho del cráter:
1. **Zona de Fusión (Núcleo, $r < 0.35 R_0$):**
   - Suelo calcinado: `MAGMA_BLOCK`, `OBSIDIAN`, `CRYING_OBSIDIAN`, `BLACKSTONE`.
2. **Zona de Fractura e Impacto ($0.35 R_0 \le r < 0.85 R_0$):**
   - Roca fracturada: `COBBLESTONE`, `CRACKED_STONE_BRICKS`, `GRAVEL`.
3. **Zona de Shock Térmico ($0.85 R_0 \le r \le R_0$):**
   - Dispersión de fuego (`FIRE`), `COARSE_DIRT` y ceniza.

### E. Onda de Choque Cinemática a Entidades
Para cualquier entidad viva a distancia $d \le R_{shockwave}$ (ej. 80–120 bloques):
$$\text{Daño}(d) = \text{Daño}_{max} \cdot \left(1 - \frac{d}{R_{shockwave}}\right)^{1.5}$$
$$\vec{F}_{knockback} = \frac{K_{fuerza}}{\sqrt{d} + 1} \cdot \frac{\vec{x}_{entidad} - \vec{x}_{epicentro}}{\|\vec{x}_{entidad} - \vec{x}_{epicentro}\|}$$

---

## 3. Estrategia de Ejecución de Alto Rendimiento (20 TPS)

1. **Pre-cálculo Matemático:** Las comprobaciones de volumen y ruido se evalúan de forma analítica instantánea sin raycasts.
2. **Lotes de Bloques Escalonados (Time-Sliced Worker):**
   - Se modifica el terreno en capas radiales o lotes de 1.500 bloques por tick usando el scheduler de la región en Folia o Paper.
   - Modificación sin físicas reactivas (`setType(Material.AIR, false)`), evitando la caída en cascada de arena y los miles de drops de ítems.
3. **Efectos Inmersivos:** Sonido atronador en todo el servidor, pantalla temblorosa (knockback cinético leve) y partículas masivas.
