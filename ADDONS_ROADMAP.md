# Roadmap: Permadeath Addons & Entities Engine (v1.6+)

Este documento detalla el plan de desarrollo para el sistema modular de Addons y el Motor de Entidades personalizado integrado con BetterModel.

## Fase 1: Infraestructura de la API (Core)
El objetivo es permitir que Permadeath reconozca y gestione plugins externos como "Addons".

- [x] **Registro de Addons**: Crear `AddonRegistry` en la API para detectar y gestionar piezas externas.
- [x] **Sistema de Providers**: Implementar interfaces para que los addons puedan sobrescribir el Core:
    - [x] `HealthProvider`: Control total sobre el cálculo de vida (Hooked en NetheriteArmor).
    - [x] `SpawnProvider`: Control sobre qué mobs aparecen y dónde (Hooked en SpawnListener).
    - [x] `VisualProvider`: Puente para sistemas de modelos 3D (BetterModel).
- [x] **Eventos de API**: Añadir eventos específicos (Cancellable) para cambios de día, clima y muerte.

## Fase 2: Motor de Comportamiento (PDC-Entities)
Creación del motor tipo "Mythic Mobs" especializado para Permadeath.

- [x] **Scripting System**: Motor de carga de archivos YAML para definir mobs con:
    - [x] **FormulaEvaluator**: Soporte para expresiones matemáticas avanzadas.
    - [x] **Triggers/Skills**: Implementación inicial de habilidades y disparadores.
- [x] **IA Avanzada (NMS)**: Inyección dinámica de Pathfinders para comportamientos complejos (BloodScent, ShadowStep, TowerBuilder).
- [ ] **Pulido de Componentes**: Refinar y estabilizar el comportamiento de:
    - Mecánicas (Mecánicas de daño, efectos, señales).
    - Disparadores (Triggers de eventos y cronometrados).
    - Condiciones (Requisitos para habilidades).
    - Selectores (Búsqueda de objetivos/Targeters).
    - IA (Mejorar precisión en Goals y Targets).
- [x] **Folia Native**: Cada entidad personalizada gestionada por su propio `EntityScheduler`.

## Fase 3: Integración Visual (BetterModel)
Capa estética para entidades personalizadas.

- [x] **BetterModel Hook**: Clase puente en el addon para detectar y enviar comandos a BetterModel.
- [x] **Sincronización de Animaciones**: Vincular estados de la IA (Idle, Walk, Attack, Death) con animaciones del modelo 3D.
- [x] **Dynamic Models**: Capacidad de cambiar el modelo del mob en tiempo real según la fase de combate.

## Fase 4: Expansión y Ecosistema
Habilitar que otros desarrolladores extiendan el motor.

- [ ] **External Skill Registration**: Permitir que un Addon B añada una habilidad nueva al motor PDC-Entities (Addon A).
- [ ] **PlaceholderAPI Support**: Integrar variables de los addons en el HUD y mensajes.
- [ ] **GUI de Gestión**: Añadir sección en `/pdc config` para ver addons activos y sus estadísticas.

---
**Estado Actual**: Fase 2 avanzada (Motor PDC-Entities funcional, iniciando etapa de pulido).
**Próximo Paso**: Pulir mecánicas, triggers y selectores mientras se asegura compatibilidad total con Folia.
