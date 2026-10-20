# Plan de Refactorización y Optimización - Permadeath v1.4

Este documento detalla las mejoras arquitectónicas y de rendimiento planificadas para el plugin, con el objetivo de aumentar la estabilidad, facilitar el mantenimiento y mejorar la compatibilidad con servidores de alto rendimiento (Folia).

## 1. Modularización de la Lógica (Core) [COMPLETO]
- [x] Crear la interfaz `PermadeathModule`.
- [x] Migrar mecánicas a módulos independientes (`Abyss`, `Beginning`, `Health`, `Environment`, `Catastrophe`, `Interface`, `Mob`, `Event`).
- [x] Implementar `ModuleManager`.

## 2. Optimización del Sistema de Ticks [COMPLETO]
- [x] Implementar sistema de Ticks Espaciados (1s para la mayoría de tareas).
- [x] Garantizar compatibilidad con Folia Schedulers.

## 3. Flexibilidad de Configuración (Milestones) [COMPLETO]
- [x] Crear sección `Progression` en `config.yml`.
- [x] Refactorizar `DateManager` para usar hitos dinámicos.

## 4. Refuerzo de la Abstracción NMS [COMPLETO]
- [x] Eliminar importaciones de `net.minecraft` en el módulo principal.
- [x] Definir interfaces para entidades personalizadas (`PermadeathEntity`, `UltraRavager`, `CustomWarden`) en el módulo API.
- [x] Implementar sistema de wrappers en el módulo NMS para desacoplar la lógica del núcleo.

## 5. Seguridad en Entornos Multihilo (Folia) [COMPLETO]
- [x] Uso de `ConcurrentHashMap` y colecciones sincronizadas.

## 6. Pruebas y Calidad [COMPLETO]
- [x] Configurar JUnit 5 y Mockito.
- [x] Crear tests exhaustivos para las fórmulas de salud.

## 7. Persistencia de Datos y Rendimiento [COMPLETO]
- [x] Implementar `PlayerDataStorage` con soporte para YAML y SQLite.
- [x] Integrar HikariCP y migración automática.

## 8. Refactorización de Comandos [COMPLETO]
- [x] Implementar sistema de sub-comandos basado en clases independientes.

## 9. Optimización de Mensajes (Caché) [COMPLETO]
- [x] Implementar un sistema de caché en `Messages.java`.
- [x] Soporte para colores Hexadecimales en todos los mensajes.

## 10. Estructuras Nativas (Jigsaw) [EN PROCESO]
- [x] Crear carpetas y archivos JSON base en el Datapack interno.
- [ ] **[TAREA PARA EL USUARIO]** Convertir archivos `.schem` de islas a `.nbt` usando Structure Blocks en el juego.
- [ ] **[TAREA PARA EL USUARIO]** Colocar los archivos `.nbt` en `main/src/main/resources/internal_datapack/data/permadeath/structure/`.
- [ ] Vincular las estructuras en `beginning.json` y eliminar el generador manual en Java.

## 11. API y Eventos Personalizados [COMPLETO]
- [x] Crear eventos propios (`PermadeathDayChangeEvent`, `AbyssPressureLevelEvent`, `DeathTrainEvent`).
- [x] Exponer API limpia (`PermadeathAPI`) para integración con otros plugins.
- [x] Abstraer `ModuleManager` y `PlayerDataStorage` mediante interfaces en el módulo API.

## 12. Limpieza de Código Muerto [COMPLETO]
- [x] Eliminar clases obsoletas (`PDCCommand`, `PDCCommandCompleter`).
- [x] Eliminar campos y métodos sin uso en `Main.java`.

---
*Estado actual: Fase de refactorización estructural completada con éxito. JAR verificado y tests pasando.*