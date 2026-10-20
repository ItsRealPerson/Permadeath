# PDC-Engine v1.5 (The Universal Update)

Este documento consolida la hoja de ruta estratégica para la transformación de Permadeath en **PDC-Engine (Programmable Dynamic Core)**. El objetivo es proporcionar una infraestructura de red distribuida y un motor de lógica universal para Minecraft.

---

## 1. Visión General
PDC-Engine v1.5 se centra en cuatro pilares fundamentales:
1.  **Infraestructura de Red:** Soporte para servidores distribuidos (Sharding) y dimensiones separadas.
2.  **Excelencia Visual (PacketEvents):** Migración de efectos NMS a paquetes para estabilidad y fluidez.
3.  **Administración In-Game (GUI Editor):** Gestión total del motor sin tocar archivos YAML.
4.  **Framework de Scripting:** Lógica 100% programable vía YAML para crear cualquier tipo de juego (Survival, RPG, Apocalipsis).

---

## 2. Infraestructura de Red y Sincronización Global

### Modos de Operación
*   [x] **STANDALONE:** Modo optimizado para un solo servidor. Uso de SQLite/YAML local.
*   [x] **NETWORK:** Modo red activa. Uso de MySQL/MariaDB y Redis Pub/Sub.

### Consistencia de Mundo
*   [x] **Automated Seed Protocol:** Sincronización de semilla global por red.
*   [x] **Dimensional Hosting:** Distribución de dimensiones por servidor físico.
*   [x] **Coordinate Sharding:** División del Overworld en múltiples servidores.

---

## 3. Framework de Scripting Universal (PDC-Script)

### Motor de Lógica
*   [x] **Global Scripting Motor:** Motor de lógica YAML migrado al Core.
*   [x] **PDC-Item Engine:** Creación de ítems personalizados con NBT y recetas de Smithing.
*   [x] **Transcripción Core:** Lógica original de Permadeath (Días, Dragona, Abismo) movida a scripts espejo.
*   [x] **Triggers Totales:** Daño, bloques, interacción, consumo, clima y ticks de Folia.

---

## 4. Editor de Configuración In-game (`/pdc config`)

### Seguridad Administrativa
*   [x] **Safe Mode:** Los menús son de "Solo Lectura" hasta activar el modo de edición.
*   [x] **Logging:** Registro detallado de cada cambio administrativo.

---

## 5. Seguridad y Resiliencia (Fail-Safe)
*   [x] **Data Locking:** Bloqueo de perfiles para evitar corrupciones.
*   [x] **Panic Mode:** Congelación de lógica sincronizada por red.
