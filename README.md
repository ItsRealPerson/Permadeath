# Permadeath Core (1.21.x - Folia & Paper)

![Version](https://img.shields.io/badge/Version-1.4-red.svg)
![Platform](https://img.shields.io/badge/Platform-Folia%20%7C%20Paper-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)

Una reconstrucción avanzada y optimizada del plugin de supervivencia extrema **Permadeath**, diseñada específicamente para las versiones más recientes de Minecraft (1.21.x) y con soporte nativo para el entorno multihilo de **Folia**.

---

## 🌟 Características Principales

### 🌌 Dimensiones Personalizadas
*   **The Beginning (Día 40+):** Una dimensión hostil basada en el End con generación de terreno personalizada, estructuras flotantes (Ytics) y mobs de élite.
*   **El Abismo Profundo (Día 60+):** Una dimensión de oscuridad total donde la presión atmosférica es mortal. Requiere equipamiento especializado (Máscaras Abisales y Pociones de Respiración) para sobrevivir.

### 📈 Escalado de Dificultad Dinámico (Días 1-90)
*   **Progresión Diaria:** Los atributos de los mobs (vida, daño, velocidad) aumentan cada día.
*   **Eventos de Muerte:** Cuando un jugador muere, comienza el **Death Train**, una tormenta eléctrica que enfurece a los mobs y les otorga efectos de poción masivos.
*   **Modo UHC (Día 50+):** La regeneración natural se desactiva permanentemente.
*   **Evolución Abisal (Día 70-90):** Mecánicas exclusivas como el bombardeo de Phantoms, reducción extrema de curación y buffs de velocidad.

### ⚔️ Equipamiento Legendario
*   **Netherite Infernal:** Una mejora superior a la Netherite convencional, irrompible y con propiedades defensivas únicas.
*   **Reliquias del Fin y del Comienzo:** Objetos de crafteo complejo necesarios para desbloquear el máximo potencial de los jugadores.
*   **Orbe de Vida:** Un artefacto místico que permite recuperar contenedores de vida perdidos.

---

## 🚀 Mejoras de esta Versión (v1.4)

*   **Soporte Completo para Folia:** Lógica de teletransporte asíncrona y schedulers regionales para evitar crashes y lag spikes.
*   **Inmersión Sonora en el Abismo:** Sistema de latidos y efectos visuales que reaccionan al nivel de oxígeno del jugador.
*   **Nether Overhaul:** Reinforcements automáticos y Piglins equipados con armaduras de diamante/netherite desde el día 30.
*   **Sistema de Backups:** Comando `/pdc backup` para respaldar tus mundos de forma asíncrona y segura.
*   **Traducción Completa:** Todas las entidades especiales y mensajes del sistema están en español.

---

## 🛠️ Requisitos
*   **Software:** [Folia](https://papermc.io/software/folia) o [PaperMC](https://papermc.io/software/paper) 1.21.8+.
*   **Java:** Versión 21 o superior.
*   **Dependencias:** 
    *   [WorldEdit](https://dev.bukkit.org/projects/worldedit) o [FastAsyncWorldEdit (FAWE)](https://www.spigotmc.org/resources/fastasyncworldedit.13932/).
    *   **Datapack de Permadeath:** Necesario para el registro de biomas y dimensiones (Beginning/Abyss).

---

## 💻 Comandos de Administración

| Comando | Descripción |
| :--- | :--- |
| `/pdc dias` | Muestra el día actual del servidor. |
| `/pdc cambiarDia <día>` | Cambia la fecha actual del mundo. |
| `/pdc backup` | Crea un respaldo ZIP de todos los mundos. |
| `/pdc recipes` | Abre el menú visual de recetas personalizadas. |
| `/pdc storm <add/remove> <cant> <h/m>` | Gestiona la duración del Death Train. |
| `/pdc debug optimize_spawns` | Activa/Desactiva el optimizador de mobs. |
| `/pdc give <item>` | Entrega ítems especiales de Permadeath. |

---

## 📝 Instalación

### 1. Plugin
1.  Descarga el archivo `Permadeath.jar`.
2.  Colócalo en la carpeta `plugins` de tu servidor.
3.  Asegúrate de tener instalada la última versión de **WorldEdit**.

### 2. Datapack (Obligatorio)
Para que las dimensiones personalizadas funcionen correctamente, debes instalar el datapack:
1.  Localiza la carpeta `datapacks` dentro de la carpeta de tu mundo principal (ej: `world/datapacks/`).
2.  Copia la carpeta `Permadeath` (incluida en el repositorio o la descarga) dentro de esa carpeta.
3.  Reinicia el servidor o ejecuta `/minecraft:reload`.

---

## 👥 Créditos
*   **Desarrollador:** SebazCRC
*   **Mantenimiento y Optimizaciones:** InfernalCore Team / ItsRealPerson
*   **Basado en:** La serie original de Permadeath de ElRichMC.
