# Comparativa: Permadeath 1.3 (Original) vs Permadeath 1.4 (Folia Update)

Este documento detalla la evolución del plugin. La diferencia fundamental es que la **v1.3** se centraba en la dificultad progresiva y la dimensión "The Beginning", mientras que la **v1.4** introduce una dimensión totalmente nueva ("The Abyss") y reescribe el núcleo del plugin para soportar Folia.

## 1. Contenido Nuevo (Exclusivo de v1.4)

Todo lo listado a continuación **NO EXISTÍA** en la versión 1.3 original:

### 🌌 Nueva Dimensión: El Abismo (The Abyss)
*   **Concepto:** Una dimensión de oscuridad perpetua accesible a partir del Día 60 (configurable).
*   **Generación:** Mundo personalizado basado en el bioma *Deep Dark*, con suelo de Sculk, techo de Bedrock y cápsulas de loot flotantes.
*   **Mecánicas Ambientales:**
    *   **Presión Abisal:** Sistema de oxígeno que requiere una máscara especial.
    *   **Oscuridad:** Efecto permanente de Darkness/Blindness que no se quita con leche.
*   **Items Nuevos:**
    *   **Máscara del Abismo:** Item esencial con durabilidad dinámica.
    *   **Filtro Abisal:** Consumible para reparar la máscara.
    *   **Corazón del Abismo:** Item de tier alto para crafteos avanzados.
    *   **Fragmento de Vacío:** Material de crafteo.

### 🧟 Nuevos Mobs (IA Nativa)
Estos enemigos fueron creados desde cero para la v1.4:
*   **Silent Seeker (Creeper):** Ciego, detecta vibraciones/olfato, explosión sónica.
*   **Hollow Guard (Husk):** Rastrea olor, entra en frenesí.
*   **Echo Archer (Stray):** Dispara proyectiles sónicos que atraviesan muros.
*   **Sculk Parasite (Silverfish):** Inyecta efectos negativos al contacto.

---

## 2. Diferencias en Contenido Existente

Cambios en mecánicas que ya existían en la v1.3 pero han sido modificadas en la v1.4:

### ⚔️ Combate y Equipamiento
| Característica | Versión 1.3 (Original) | Versión 1.4 (Actual) |
| :--- | :--- | :--- |
| **Netherite Infernal** | Existía. Crafteo posicional a veces estricto. | **Optimizado.** Mejor detección de recetas y atributos NBT persistentes (PDC) para evitar conflictos con otros plugins de items custom. |
| **Reliquias** | Reliquia del Fin y del Comienzo existían. | **Rebalanceadas.** Se han ajustado los costos y la lógica de detección en la mesa de crafteo para mayor seguridad. |
| **Hyper/Super Gaps** | Efectos potentes. | **Validación Estricta.** La receta ahora exige cantidades exactas (ej. 8 bloques de oro) para evitar exploits o pérdidas accidentales. |

### 🌍 Dimensión "The Beginning"
| Característica | Versión 1.3 (Original) | Versión 1.4 (Actual) |
| :--- | :--- | :--- |
| **Generación** | Bioma de End modificado. | **Generación Mejorada.** Se mantiene la estética pero se optimiza la carga de chunks y la generación de estructuras "Ytics" para no saturar el servidor. |
| **Entrada** | Portal frame custom. | **Lógica Asíncrona.** El teletransporte y la creación de la plataforma segura se calculan en hilos separados para no congelar el servidor al entrar. |

### 🛠️ Diferencias Técnicas (Motor)
| Característica | Versión 1.3 (Original) | Versión 1.4 (Actual) |
| :--- | :--- | :--- |
| **Plataforma** | Spigot/Paper (Single Thread). | **Folia (Multi-Threaded) & Paper.** Soporte nativo para regiones independientes. |
| **Mob AI** | Atributos vanilla modificados (Vida, Daño). IA básica. | **Inyección NMS.** Se modifica el "cerebro" (Pathfinders) de los mobs para comportamientos avanzados (romper bloques, pillar, usar items) sin lag. |
| **Dependencias** | Varias dependencias utilitarias. | **Cero Dependencias Pesadas.** Se eliminaron librerías externas (como PacketEvents si se llegó a considerar) en favor de código nativo ligero. |

## Resumen
La **v1.4** no es solo una actualización, es una **secuela técnica y de contenido**. Añade el "End Game" real (El Abismo) que faltaba en la versión original y moderniza todo el código para que funcione en servidores de 2026 (Folia/1.21.x).
