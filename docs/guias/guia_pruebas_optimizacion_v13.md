# Guía de Pruebas de Optimización y Rendimiento (Plan v13)

Esta guía detalla los comandos y procedimientos en el juego para verificar cada una de las optimizaciones aplicadas en el Plan v13 sin alterar el comportamiento de las mecánicas de Permadeath.

---

## 1. Verificación de Rendimiento Global (TPS y MSPT)
* **Comando:** `/tps` o `/paper mspt` en consola/juego.
* **Resultado Esperado:** 
  - El servidor debe marcar **20.0 TPS** constantes.
  - El tiempo medio por tick (**MSPT**) debe mantenerse bajo (< 10 ms en reposo, < 25 ms bajo carga), indicando que el servidor tiene un margen de más del 50% de CPU libre.

---

## 2. Accesorios en Memoria (Caché sin Deserialización NBT)
* **Objetivo:** Verificar que los accesorios funcionan al 100% leyendo de RAM sin congelar el hilo principal en cada segundo.
* **Pasos:**
  1. Abre el inventario de accesorios (`/pdc inv` o mediante el ítem de accesorios).
  2. Equípate la **Medalla de Agua** en su slot correspondiente (o el **Orbe de Vida**).
  3. Establece el Día 30: `/pdc setDay 30`
  4. Entra al agua en modo Supervivencia (`/gamemode survival`):
     - **Con Medalla:** No debes recibir daño de agua ácida.
     - **Sin Medalla:** Recibes 1 corazón de daño por segundo y partículas de burbujas.
  5. Desconéctate y vuelve a entrar: los accesorios se cargan al instante sin demoras.

---

## 3. Inteligencia Artificial de Mobs (`MobUtils` Optimizado)
* **Objetivo:** Validar que los mobs custom y animales hostiles detectan al jugador de forma reactiva sin escanear 25 chunks.
* **Pasos:**
  1. Establece el Día 20 o superior: `/pdc setDay 20`
  2. Spawnea animales pacíficos cerca: `/summon cow ~ ~ ~`, `/summon sheep ~ ~ ~`
  3. Cambia a modo Supervivencia:
     - Los animales se volverán hostiles de inmediato y correrán hacia ti para atacarte.
  4. Prueba la **Mecánica de Sigilo:**
     - Aléjate a unos 12-15 bloques y agáchate (`Shift`/Sneak).
     - El mob perderá el objetivo ya que el rango de detección en sigilo se reduce a 8 bloques.
  5. Spawnea mobs abisales o especiales (`/pdc spawn creeper`): te perseguirán fluidamente.

---

## 4. Spawns de Mobs y Descarte de Escaneos Masivos (256 Chunks)
* **Objetivo:** Confirmar que el conteo de mobcap no genera tirones de lag en `CreatureSpawnEvent`.
* **Pasos:**
  1. Establece el Día 25: `/pdc setDay 25`
  2. Ve a una cueva o área oscura y deja que los mobs aparezcan de forma natural, o invoca un zombi con `/summon zombie ~ ~ ~`.
  3. Observa los efectos de poción de los mobs:
     - Tienen automáticamente los efectos de *Death Train* (Velocidad, Resistencia, Fuerza) aplicados una única vez en su aparición, sin registrar timers innecesarios.

---

## 5. Scheduler Selectivo de Ultra Ravagers
* **Objetivo:** Comprobar que solo los Ravagers en Día 40+ ejecutan tareas repetitivas de destrucción de bloques.
* **Pasos:**
  1. Establece el Día 40: `/pdc setDay 40`
  2. Spawnea un Ravager: `/summon ravager ~ ~ ~`
  3. Coloca una pared de **Netherrack** delante de él en su camino.
  4. Comprueba que al avanzar rompe los bloques de Netherrack con sonido de piedra rompiéndose.

---

## 6. Generación de The Beginning (Caché de Esquemáticas WorldEdit)
* **Objetivo:** Validar que la generación de islas y estructuras Ytic no congela el servidor al leer archivos `.schem`.
* **Pasos:**
  1. Asegura que el día sea 40 o superior: `/pdc setDay 40`
  2. Entra a The Beginning a través del portal de End Gateway o con `/tp <jugador> 0 100 0` en el mundo de Beginning.
  3. Equípate élitros en Modo Creativo y vuela rápido hacia chunks inexplorados (`/tp ~1000 ~ ~1000`).
  4. Observa cómo las islas y ciudades Ytic se generan fluidamente sin micro-bloqueos en el servidor.

---

## 7. Gatos Supernova (Ray-Marching Cúbico 18x18)
* **Objetivo:** Comprobar que la explosión es 100% destructiva, conserva su silueta cúbica e imperfecta y no provoca lag.
* **Pasos:**
  1. Establece el Día 40: `/pdc setDay 40`
  2. Spawnea un gato: `/summon cat ~ ~ ~`
  3. El gato se convertirá en *Gato Supernova* y detonará:
     - Produce la onda expansiva y sonido de explosión.
     - Destruye bloques de arriba a abajo por capas sin congelar el juego.
     - El cráter resultante tiene bordes cúbicos, cornisas toscas y desniveles sin generar obsidiana ni magma flotante.

---

## 8. Sincronización de Salud Máxima (`NetheriteArmor`)
* **Objetivo:** Verificar que los atributos de salud solo envían paquetes de actualización cuando hay cambios reales.
* **Pasos:**
  1. Establece el Día 40: `/pdc setDay 40` (tu vida base bajará a 6 corazones a menos que tengas Orbe o Netherite).
  2. Ponte una armadura completa de Netherite: tu vida máxima subirá al instante.
  3. Quítate una pieza: la barra de vida se actualiza inmediatamente una sola vez sin parpadeos continuos cada segundo.
