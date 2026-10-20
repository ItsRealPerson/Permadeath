# 📜 Guía Maestra de Scripting - Permadeath Core v1.5

Esta guía contiene la documentación completa del motor de scripting de Permadeath. El sistema es ahora un **Framework de Desarrollo** ilimitado que te permite crear mecánicas complejas, habilidades y cambios globales.

---

## ⚡ Disparadores (Triggers)
Eventos del juego que disparan tus scripts.

| Trigger | Descripción | Variables de Contexto |
| :--- | :--- | :--- |
| `ENTITY_SPAWN` | Cuando cualquier entidad aparece. | `%caster_type%` |
| `ENTITY_DAMAGE` | Cuando una entidad recibe daño. | `%damage_amount%`, `%damage_cause%` |
| `ENTITY_DEATH` | Cuando una entidad muere. | `%target_type%` |
| `PLAYER_JOIN` | Al entrar al servidor. | `%caster_name%` |
| `PLAYER_TICK` | Cada 1 segundo por cada jugador. | `%caster_hp%`, `%caster_x%`, `%pdc_day%` |
| `PLAYER_INTERACT` | Al hacer click derecho/izquierdo. | `%action%`, `%item_type%`, `%block_location%` |
| `TICK_1S` | Globalmente cada 1 segundo. | `%pdc_day%` |
| `DAY_CHANGE` | Al cambiar el día de Permadeath. | `%day%` |
| `ITEM_CONSUME` | Al comer o beber algo. | `%item_type%`, `%item_name%` |
| `BLOCK_BREAK` | Al romper un bloque. | `%block_type%`, `%block_location%` |
| `BLOCK_PLACE` | Al colocar un bloque. | `%block_type%` |
| `BUCKET_FILL` | Al llenar un cubo. | - |
| `FURNACE_SMELT` | Al fundir algo en el horno. | `%item_type%` |
| `LIGHTNING_STRIKE`| Al caer un rayo. | `%loc_x%`, `%loc_y%`, `%loc_z%` |
| `PROJECTILE_HIT` | Al impactar un proyectil. | `%projectile_type%`, `%hit_block%` |
| `CUSTOM` | Eventos internos (ej: `TOTEM_USE`). | - |

---

## 🔍 Condiciones de Élite
*   `is_sneaking{}`, `is_sprinting{}`, `is_gliding{}`, `is_burning{}`, `is_wet{}`.
*   `has_potion_effect{type=EFFECT}`: Si tiene un efecto de poción activo.
*   `is_holding{type=MATERIAL/ID}`: Comprobar ítem en mano (Vainilla o Script Item).
*   `is_custom_item{id=ID}`: Comprobar si el ítem es uno creado por script.
*   `is_wearing{type=MATERIAL/ABYSSAL_MASK;slot=HEAD/CHEST/LEGS/FEET}`: Armadura. **Nota:** `ABYSSAL_MASK` solo detecta el slot de accesorios.
*   `has_permission{node='pdc.use'}`: Comprobar permisos.

### Entorno y Ubicación
*   `is_world{name='world'}`, `is_environment{type=NORMAL/NETHER/THE_END}`.
*   `is_exposed_to_sky{}`: Si no hay bloques encima.
*   `is_in_water{}`, `is_near_block{type=BEDROCK;radius=3}`.
*   `light_level{value=5;op='<'}`: Nivel de luz.
*   `y_level{value=64;op='<'}`: Altura actual.
*   `is_daytime{}`, `is_raining{}`: Condiciones climáticas.

### Lógica y Progresión
*   `day_at_least{value=X}`, `day_at_most{value=X}`: Rango de días.
*   `chance{value=0-100}`, `chance_by_day{base=10;increment=0.5}`: Probabilidades dinámicas.
*   `is_passive{}`: Si es un mob pacífico según el Core.
*   `check_metadata{key='var';value=10;op='>'}`: Lee variables dinámicas.

---

## 🎬 Acciones de Élite
*   `velocity{x=0;y=1.0;z=0}`, `shockwave{radius=5;power=1.5}`, `pull_closer{radius=10;speed=0.5}`.
*   `set_bossbar{id='id';title='Text';progress='1.0';color='BLUE'}`: Crea/Actualiza barra de Boss.
*   `remove_bossbar{id='id'}`: Elimina la barra de Boss.
*   `set_sky_darkness{value=0.5}`, `set_warning_border{value=true}`: Efectos de PacketEvents.
*   `damage{amount='formula'}`, `heal{amount=2.0}`, `apply_potion{type=EFFECT;duration=ticks;amplifier=X}`.
*   `play_particle{type=NAME;amount=10;spread=0.5}`, `camera_shake{power=1.0}`, `guardian_beam{}`.
*   `modify_metadata{key='n';value=1;op=ADD/SUB/SET}`: **Variables matemáticas**.
*   `give_item{id=ID;amount=1}`, `drop_item{id=ID;amount=1}`: Gestión de ítems.
*   `cancel_event{}`, `command{cmd='texto';as=CONSOLE/PLAYER}`, `ban_player{reason='Text'}`.

---

## 🎒 Sistema de Ítems Personalizados
Los ítems se definen en `scripts/items/`.

```yaml
Items:
  - Id: "MI_ITEM"
    Material: "VANILLA_MAT"
    Name: "&bNombre"
    Lore: ["Línea 1"]
    CustomModelData: 101
    Unbreakable: true
    Color: "#HEX" # Para cuero o pociones
    Enchantments: {sharpness: 5}
    Attributes: {generic.attack_damage: 10.0}
    Recipes:
      - Shape: ["AAA", " B ", " B "]
        Ingredients: "A:GOLD_INGOT,B:STICK"
      - Template: "NETHERITE_UPGRADE_SMITHING_TEMPLATE"
        Base: "DIAMOND_SWORD"
        Addition: "custom:MATERIAL_CUSTOM"
```

---

## 🧮 Placeholders Dinámicos
| Tag | Descripción |
| :--- | :--- |
| `%caster_name%`, `%target_name%` | Nombres de los involucrados. |
| `%caster_hp%`, `%caster_max_hp%` | Vida actual/máxima. |
| `%caster_x/y/z%`, `%caster_biome%` | Ubicación y bioma. |
| `%damage_amount%`, `%pdc_day%` | Daño y Día actual. |

---

## 💡 10 Ejemplos de Contenido de Élite

### 1. Espada de Zeus (Habilidad de Ítem)
Al hacer click derecho con una espada de oro, cae un rayo donde miras (coste: 2 corazones).
```yaml
  - Id: "Espada_Zeus"
    Trigger: "PLAYER_INTERACT"
    Conditions:
      - "is_holding{type=GOLDEN_SWORD}"
      - "check_metadata{key='zeus_cooldown';value=0;op='<='}"
    Actions:
      - "strike_lightning{}"
      - "damage{amount=4}"
      - "modify_metadata{key='zeus_cooldown';value=10;op=SET}"
```

### 2. Presión del Abismo (1:1 Core)
Mecánica de oxígeno y presión cuando no se lleva la máscara en el Abismo.
```yaml
  - Id: "Abyss_Oxygen_Drain"
    Trigger: "PLAYER_TICK"
    Conditions:
      - "is_world{name='world_permadeath_abyss'}"
      - "is_wearing{type=ABYSSAL_MASK;invert=true}"
      - "has_potion_effect{type=WATER_BREATHING;invert=true}"
    Actions:
      - "modify_metadata{key='abyss_pressure';value=0.005;op=SUB}"
      - "set_bossbar{id='abyss';title='&3&lPresión del Abismo';progress='%abyss_pressure%';color='BLUE'}"
```

### 3. Oxígeno Agotado
Aplicar daño y Wither cuando la presión llega a 0.
```yaml
  - Id: "Abyss_Death"
    Trigger: "PLAYER_TICK"
    Conditions:
      - "is_world{name='world_permadeath_abyss'}"
      - "check_metadata{key='abyss_pressure';value=0;op='<='}"
    Actions:
      - "damage{amount=2.0}"
      - "apply_potion{type=WITHER;duration=40;amplifier=1}"
      - "set_bossbar{id='abyss';title='&c&l¡OXÍGENO AGOTADO!';progress='0';color='RED'}"
```

### 4. Blood Moon (Evento Escalable)
Cada cambio de día, hay una probabilidad (que sube con los días) de activar una tormenta eterna.
```yaml
  - Id: "Blood_Moon_Start"
    Trigger: "DAY_CHANGE"
    Conditions:
      - "chance_by_day{base=1;increment=0.1}"
    Actions:
      - "set_weather{type=THUNDER}"
      - "send_title{title='&4&lBLOOD MOON';subtitle='&cLos antiguos han despertado...'}"
```

### 5. Vampirismo (Pasiva de Combate)
Si matas a una entidad de noche, recuperas 1 corazón.
```yaml
  - Id: "Vampire_Leech"
    Trigger: "ENTITY_DEATH"
    Conditions:
      - "is_daytime{invert=true}"
    Actions:
      - "heal{amount=2}"
      - "play_particle{type=HEART;amount=5}"
```

### 6. Oro Maldito (Trampa Ambiental)
Picar oro en el Nether tiene un 20% de probabilidad de prenderte fuego.
```yaml
  - Id: "Cursed_Gold"
    Trigger: "BLOCK_BREAK"
    Conditions:
      - "is_environment{type=NETHER}"
      - "is_item_type{type=NETHER_GOLD_ORE}"
      - "chance{value=20}"
    Actions:
      - "burn{duration=100}"
      - "play_sound{sound=ENTITY_GHAST_SCREAM}"
```

### 7. Peso Pesado (Penalización de Armadura)
Llevar armadura completa de Netherite te da Lentitud 1 pero Resistencia 1.
```yaml
  - Id: "Heavy_Armor"
    Trigger: "PLAYER_TICK"
    Conditions:
      - "is_wearing{type=NETHERITE_CHESTPLATE;slot=CHEST}"
    Actions:
      - "apply_potion{type=SLOW;duration=40;amplifier=0}"
      - "apply_potion{type=DAMAGE_RESISTANCE;duration=40;amplifier=0}"
```

### 8. Grito de Guerra (Habilidad de Área)
Al agacharte (Sneak), empujas a todos los mobs cercanos 5 bloques.
```yaml
  - Id: "War_Cry"
    Trigger: "PLAYER_TICK"
    Conditions:
      - "is_sneaking{}"
      - "check_metadata{key='cry_cd';value=0;op='<='}"
    Actions:
      - "shockwave{radius=5;power=2.0}"
      - "play_sound{sound=ENTITY_ENDER_DRAGON_GROWL;pitch=0.5}"
      - "modify_metadata{key='cry_cd';value=20;op=SET}"
```

### 9. Trampa de Luz (IA de Mobs)
Los mobs que spawnean en oscuridad total (luz < 2) son 100% agresivos y más rápidos.
```yaml
  - Id: "Dark_Spawn"
    Trigger: "ENTITY_SPAWN"
    Conditions:
      - "light_level{value=2;op='<'}"
    Actions:
      - "core_make_hostile{}"
      - "apply_potion{type=SPEED;duration=99999;amplifier=1}"
```

### 10. Flechas Explosivas (Proyectiles)
Las flechas disparadas por el jugador explotan al impactar si es Día 60+.
```yaml
  - Id: "Explosive_Arrows"
    Trigger: "PROJECTILE_HIT"
    Conditions:
      - "day_at_least{value=60}"
      - "%projectile_type% == 'ARROW'"
    Actions:
      - "command{cmd='execute at %target_uuid% run summon tnt ~ ~ ~ {Fuse:0}';as=CONSOLE}"
```
