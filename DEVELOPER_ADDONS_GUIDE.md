# Guía Técnica Maestra: Desarrollo de Addons para Permadeath (v1.5+)

Esta guía detalla el uso avanzado de la API de Permadeath para desarrolladores que desean integrar sus sistemas con el núcleo del plugin y aprovechar la infraestructura de red, persistencia y eventos.

---

## 0. Integración al Proyecto (Maven)

Para empezar a desarrollar, debes añadir el repositorio de JitPack y las dependencias de Permadeath a tu `pom.xml`.

### Repositorio
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Dependencias
Debes añadir la API del Core y, opcionalmente, la de PDC-Entities si vas a trabajar con mobs personalizados.

```xml
<dependencies>
    <!-- Permadeath Core API -->
    <dependency>
        <groupId>com.github.ItsRealPerson.Permadeath</groupId>
        <artifactId>api</artifactId>
        <version>1.4-PRE2</version>
        <scope>provided</scope>
    </dependency>

    <!-- PDC-Entities API (Opcional) -->
    <dependency>
        <groupId>com.github.ItsRealPerson.Permadeath</groupId>
        <artifactId>pdc-entities</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## 1. El Punto de Entrada: `PermadeathAPIProvider`

Toda la comunicación con el Core se realiza a través de esta interfaz. Al inicializar tu addon, recibirás una instancia de este proveedor.

```java
public class MiAddon implements PermadeathAddon {
    private PermadeathAPIProvider api;

    @Override
    public void onInitialize(PermadeathAPIProvider apiInstance) {
        this.api = apiInstance;
    }
}
```

### Propiedades Globales
- `api.getDay()`: Obtiene el día actual de la partida.
- `api.isExtendedDifficulty()`: Comprueba si se ha activado la extensión de dificultad (Día 60+).
- `api.getPrefix()`: Obtiene el prefijo configurado para mensajes.

---

## 2. Gestión de Módulos (`ModuleManagerAPI`)

Permadeath es modular. Puedes interactuar con módulos existentes o registrar los tuyos propios.

```java
// Buscar un módulo del Core
api.getModuleManager().getModule("DeathTrain").ifPresent(m -> {
    m.setEnabled(false); // Desactivar el Death Train temporalmente
});

// Registrar tu propio módulo
api.getModuleManager().registerModule(new MiModuloPersonalizado());
```

---

## 3. Persistencia de Datos (`PlayerDataStorage`)

Permite acceder a los datos persistentes de los jugadores (SQLite/MySQL).

```java
PlayerDataStorage storage = api.getPlayerStorage();

storage.loadPlayer("ItsRealPerson").ifPresent(data -> {
    double vida = data.getMaxHealth();
    data.setLanguage(Language.ENGLISH);
    storage.savePlayer(data); // Guardar cambios
});
```

---

## 4. Registro Global de Ítems (`ItemRegistryAPI`)

Registra tus ítems personalizados para que otros addons o el propio Core puedan usarlos.

```java
ItemStack miItem = new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("§bEspada Legendaria").build();
api.getItemRegistry().registerItem("espada_legendaria", miItem);

// Obtener un ítem registrado (incluso del Core)
Optional<ItemStack> medalla = api.getItemRegistry().getItem("medalla_superviviente");
```

---

## 5. Control de Loot y Eventos (`LootManagerAPI` & `EventManagerAPI`)

### Loot Dinámico en el Abismo
Puedes añadir ítems a las cápsulas del Abismo sin modificar archivos YAML.
```java
api.getLootManager().addAbyssLoot(miItem, 5); // 5% de probabilidad
```

### Eventos Globales
```java
// Iniciar un Death Train de 10 minutos
api.getEventManager().startDeathTrain(12000); 

// Controlar el estado del Life Orb
boolean activo = api.getEventManager().isLifeOrbActive();
```

---

## 6. Ecosistema de Red Distribuido (`NetworkManagerAPI`)

Si el servidor está en modo multiseridor (Redis), puedes enviar mensajes entre instancias.

```java
if (api.getNetworkManager().isNetworkActive()) {
    // Enviar mensaje a toda la red
    api.getNetworkManager().sendCustomMessage("ALERTA_BOSS", "El Jefe ha spawneado en " + api.getNetworkId());

    // Escuchar mensajes de otros servidores
    api.getNetworkManager().registerNetworkListener("ALERTA_BOSS", (serverId, data) -> {
        Bukkit.broadcastMessage("§c[RED] Aviso desde " + serverId + ": " + data);
    });
}
```

---

## 7. Abstracción NMS (`NMSAccessor` & `NMSHandler`)

Usa estas interfaces para realizar tareas complejas que cambian entre versiones de Minecraft sin que tu código rompa.

```java
// Spawnear una entidad NMS abstraída
api.getNmsHandler().spawnCustomEntity(EntityType.ZOMBIE, location);

// Manipular una entidad existente
api.getNmsAccessor().clearEntityPathfinders(livingEntity); // Limpiar IA
api.getNmsAccessor().setMaxHealth(livingEntity, 2000.0, true); // Bypass cap HP
```

---

## 8. Registro de Entidades Propias (`EntityRegistryAPI`)

Permite registrar "Fábricas de Entidades" para usarlas desde comandos o triggers.

```java
api.getEntityRegistry().registerEntity("MiSuperBoss", (loc) -> {
    return new MiInstanciaBoss(loc); // MiInstanciaBoss implementa PermadeathEntity
});

// Spawnearlo
api.getEntityRegistry().spawnEntity("MiSuperBoss", player.getLocation());
```

---

## 9. Sistema de Providers (Prioridad de Addons)

Los Providers permiten que tu addon **reemplace** la lógica interna del Core. Se registran mediante `AddonRegistry`.

```java
// Cambiar el sistema de cálculo de vida global
AddonRegistry.setHealthProvider((player, base) -> base + 10.0);

// Cambiar qué mobs aparecen en el mundo
AddonRegistry.setSpawnProvider((type, loc, reason) -> {
    if (type == EntityType.CREEPER) return new SpawnResult(Decision.CUSTOM, "quantum_creeper", null);
    return new SpawnResult(Decision.IGNORE, null, null);
});
```

---

## 10. Comandos Dinámicos (`AddonSubCommand`)

Añade subcomandos a `/pdc` con soporte completo de Tab-Complete.

```java
AddonRegistry.registerSubCommand(new AddonSubCommand() {
    @Override public String getName() { return "test"; }
    @Override public String getDescription() { return "Prueba de addon"; }
    @Override public boolean execute(CommandSender s, String[] args) { ... }
});
```

---

## 11. ESTO ES PARA HACER ADDONS USANDO PDC_ENTITIES

Si tu addon añade contenido temático, nuevas mecánicas de combate o disparadores complejos para los mobs, puedes extender el motor de scripting de `pdc-entities` usando la clase `PdcEntitiesAPI`.

### Registrar una Mecánica (Acción)
Permite definir acciones que los mobs realizarán (ej: lanzar rayos, curarse, aplicar efectos).
```java
PdcEntitiesAPI.registerMechanic("lanzar_fuego", args -> {
    int duration = Integer.parseInt(args.getOrDefault("ticks", "100"));
    return (context, targets) -> {
        targets.forEach(t -> t.setFireTicks(duration));
    };
});
```

### Registrar un Selector (Targeter)
Permite definir nuevas formas de encontrar objetivos (ej: jugadores con armadura de cuero, entidades con poca vida).
```java
PdcEntitiesAPI.registerTargeter("jugadores_ricos", args -> {
    return (caster) -> {
        return caster.getWorld().getPlayers().stream()
            .filter(p -> p.getInventory().contains(Material.DIAMOND))
            .collect(Collectors.toList());
    };
});
```

### Registrar un Trigger (Disparador) Personalizado
Puedes crear eventos que no existen en el Core y dispararlos desde tu código Java para que el mob reaccione.
```java
// 1. Registro del nombre del trigger
PdcEntitiesAPI.registerTrigger("onMagicHit");

// 2. Disparo del evento (dentro de tu código de addon)
PdcMobInstance instance = MobManager.getInstance(entidadTarget);
if (instance != null) {
    instance.executeTrigger("onMagicHit");
}
```

### Uso en archivos YAML
Una vez registrados en Java, los diseñadores de mobs pueden usar tus funciones directamente en los archivos `.yml`:
```yaml
Skills:
  - lanzar_fuego{ticks=200} @jugadores_ricos ~onMagicHit
```

---
**Nota Final:** Esta API está diseñada para ser compatible con **Folia**. Siempre utiliza `api.getNmsHandler()` o los schedulers nativos de las entidades para operaciones que involucren hilos de región.
