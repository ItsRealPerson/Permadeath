# Manual de Certificación de Pruebas: Permadeath v1.4 & v1.5

Este documento detalla el protocolo de pruebas de QA para validar la estabilidad, el rendimiento y la consistencia de Permadeath en entornos Standalone y Distribuidos (Folia/Network).

---

## 1. Núcleo Técnico y Arquitectura (v1.4)
*Objetivo: Validar que la refactorización modular no ha roto el ciclo de vida del plugin.*

### 1.1 Sistema de Ticks Espaciados (1s Optimization)
- [ ] **Action Bar:** Verifica que el temporizador de tormenta y tiempo de juego se actualiza exactamente cada segundo. No debe haber parpadeos ni "saltos" de tiempo.
- [ ] **Consumo de CPU:** Monitoriza con `/timings` o `/spark`. El impacto del `HealthModule` y `EnvironmentModule` debe ser un 40% menor que en la v1.3 al procesar jugadores.
- [ ] **Reload en Caliente:** Ejecuta `/pdc reload`. Verifica que los mensajes de "Disabling Module" y "Enabling Module" aparecen en orden y que las tareas de los módulos anteriores se cancelan (evitar duplicidad de tareas).

### 1.2 Compatibilidad Multi-Hilo (Folia)
- [ ] **Entity Schedulers:** Spawnea mobs en diferentes regiones del mapa. Verifica en la consola que no hay excepciones de tipo `IllegalStateException` (Acceso a entidad desde hilo incorrecto).
- [ ] **Teleport Asíncrono:** Usa el comando `/pdc abyss`. Verifica que el teletransporte usa `teleportAsync` y que el jugador no experimenta "glitches" visuales al cargar la nueva región.

### 1.3 Sistema de Mensajería y Lenguaje
- [ ] **Caché de Configuración:** Cambia un mensaje en `mensajes_ES.yml`. Ejecuta `/pdc reload`. El cambio debe reflejarse sin reiniciar el servidor.
- [ ] **Colores Hexadecimales:** Verifica que los prefijos y mensajes soportan el formato `#RRGGBB` y que se degradan correctamente en versiones de cliente antiguas.

---

## 2. Persistencia Híbrida de Datos (v1.4)
*Objetivo: Garantizar que ningún dato de jugador se pierda bajo ninguna circunstancia.*

### 2.1 Almacenamiento SQLite (HikariCP)
- [ ] **Creación de Tablas:** Borra `permadeath.db`. Inicia el plugin. Verifica que se crean las tablas `player_data` y `world_data`.
- [ ] **Migración Automática:** Coloca un `jugadores.yml` con 10 jugadores baneados. Inicia el plugin. Verifica con `/pdc info <jugador>` que la causa de muerte y el idioma se han importado a la DB.
- [ ] **Concurrencia de Guardado:** Simula que 3 jugadores mueren al mismo tiempo (usando comandos o mobs). Verifica que los 3 baneos se registran en la base de datos sin errores de "Database Locked".

### 2.2 Fallback y Seguridad
- [ ] **Modo YAML:** Cambia `Storage.Type: YAML` en `config.yml`. Verifica que se crean archivos individuales por jugador en `/playerdata/`.
- [ ] **Data Locking (v1.5):** Activa el modo red. Inicia una transferencia de servidor. Verifica que el campo `locked` en la DB se pone en `1` durante el proceso y vuelve a `0` al finalizar con éxito.

---

## 3. Dimensión: El Abismo (The Abyss - v1.4 & v1.5)
*Objetivo: Validar la dimensión más compleja jamás creada para el mod.*

### 3.1 Mecánica de Presión Abisal
- [ ] **Efecto Base:** Entra al Abismo sin máscara. La BossBar de "Presión" debe aparecer azul y bajar gradualmente.
- [ ] **Estado Crítico:** Al llegar a 0% de presión, verifica:
    - [ ] Recibes daño real (1 corazón por tick de daño).
    - [ ] Se aplica el efecto `Wither II` permanente.
- [ ] **Pociones de Gracia:** Bebe una "Poción de Respiración Abisal". La BossBar debe volverse Púrpura y darte 120 segundos de inmunidad total.

### 3.2 Equipamiento Abisal
- [ ] **Máscara del Abismo:** Equípala en el slot de **Accesorios**. La presión debe detenerse y la BossBar cambiar a "Oxígeno de la Máscara".
- [ ] **Durabilidad Dinámica:** Verifica que la durabilidad de la máscara baja aleatoriamente mientras estás en el Abismo.
- [ ] **Filtros Abisales:** Usa un filtro con la máscara dañada. Verifica que repara un % de durabilidad (configurable en `abyss.yml`).

### 3.3 Visuales Atmosféricos (PacketEvents v1.5)
- [ ] **Cielo Dinámico:** A medida que la presión baja del 50% al 0%, el cielo debe volverse más oscuro visualmente (niebla de lluvia/trueno enviada por paquetes).
- [ ] **Alerta de Borde Rojo:** Al bajar del 20% de presión, el borde de la pantalla del jugador debe parpadear en rojo (World Border Warning).

---

## 4. Evolución de Mobs y NMS Abstraction (v1.4 & v1.5)
*Objetivo: Verificar que los mobs personalizados son funcionales en la 1.21.x.*

### 4.1 Mobs del Abismo (IA Avanzada)
- [ ] **Silent Seeker:** Verifica que es invisible, que emite partículas de alma y que su explosión es sónica (no rompe bloques pero aplica Wither y Blindness).
- [ ] **Echo Archer:** Verifica que dispara proyectiles sónicos que atraviesan paredes y que el sonido de "Sonic Boom" se escucha correctamente.
- [ ] **Hollow Guard:** Verifica que tiene 1000 HP, armadura de Netherite y que entra en frenesí (Speed) si te detecta olfateando.

### 4.2 Mobs Clásicos Refactorizados
- [ ] **Quantum Reactor:** Verifica que usa el nuevo **Guardian Beam** (Láser sólido) para fijar al jugador y que el láser sigue el movimiento de flotación.
- [ ] **Ultra Ravager:** Verifica que rompe bloques de Netherrack y piedra a su paso (Wrapper NMS funcional).
- [ ] **Giga Slimes:** Verifica que en Día 25+ los slimes aparecen con tamaño 15 y salud multiplicada.

---

## 5. Editor de Configuración GUI (v1.5)
*Objetivo: Gestión total sin tocar archivos de texto.*

### 5.1 Categorías y Navegación
- [ ] **Toggles:** Cambia "Baneos", "OptifineItems" y "Spider-Effect". Verifica que el archivo `config.yml` cambia sus valores en tiempo real.
- [ ] **Progresión:** Usa los botones de `+10` y `-10` días. Verifica que el plugin dispara los cambios de salud y ambiente correspondientes al nuevo día.
- [ ] **Loot Editor (Abismo):**
    - [ ] Abre la lista de ítems. Cambia una probabilidad del 50% al 10%.
    - [ ] Sostén un ítem nuevo en la mano y dale al botón "Añadir". Debe aparecer en la menú y en el archivo `loot.yml` al instante.
    - [ ] Elimina un ítem con Shift+Click Derecho. Verifica que desaparece de la tabla de generación.

### 5.2 Administración de Jugadores
- [ ] **Perfil Individual:** Haz clic en la cabeza de un jugador online.
    - [ ] Cambia su idioma. Verifica que sus mensajes de sistema (ej: Action Bar) cambian de idioma.
    - [ ] Modifica su **HP Extra**. Verifica que sus corazones en la HUD suben o bajan al instante.

---

## 6. Infraestructura de Red y Redis (v1.5)
*Objetivo: Sincronización multi-servidor fluida.*

### 6.1 Comunicación Redis
- [ ] **Chat Global:** Escribe en el Server A. Verifica que el Server B recibe el mensaje con el formato `[ServerID] Jugador: Mensaje`.
- [ ] **Sincronización de Pánico:** Activa el **Panic Mode** en el menú de seguridad. Verifica que el tiempo se congela en TODOS los servidores de la red simultáneamente.
- [ ] **Day Change Sync:** Cambia el día en el Server A. Verifica que el Server B recibe la señal y actualiza su fecha interna sin entrar en un bucle infinito de mensajes.

### 6.2 TAB Global y Sharding
- [ ] **Inyección de TAB:** Conecta dos servidores a la misma red Redis. Verifica que en el TAB ves a los jugadores de ambos servidores (Inyectados vía PacketEvents).
- [ ] **Fronteras de Sharding:** Configura un límite en `sharding.yml` (ej: X=1000). Camina hacia esa coordenada. Verifica:
    - [ ] El plugin serializa tu inventario a Base64 y lo manda a Redis (`INV_SYNC`).
    - [ ] Eres transferido automáticamente al servidor destino (Bungee/Velocity).

---

## 7. Integraciones y Seguridad (v1.5)
- [ ] **Discord Híbrido:** 
    - [ ] Configura el Bot en un "Servidor Maestro".
    - [ ] Mata a un jugador en un "Servidor Esclavo" (sin token).
    - [ ] El Maestro debe recibir el evento por Redis y publicar el Embed de muerte en Discord.
- [ ] **Votaciones Discord:** Desde Discord (usando el comando del bot), activa un cambio de día. Verifica que todos los servidores de la red lo ejecutan.
- [ ] **MythicMobs Hook:** Spawnea un mob de MythicMobs. Verifica con `/attribute` que su vida y daño base han escalado según el día actual de Permadeath.

---
*Manual generado el 8 de febrero de 2026. Revisión: v1.5-RELEASE-CANDIDATE.*
