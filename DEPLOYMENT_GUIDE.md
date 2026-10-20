# Guía de Despliegue y Configuración: Permadeath v1.5 Network

Esta guía detalla cómo configurar la infraestructura necesaria para correr Permadeath v1.5 en modo red distribuida (Multi-servidor).

---

## 1. Servidor Redis (Sincronización Global)
Redis es el "cerebro" que conecta todos tus servidores. Permite que el chat, los días y el modo pánico se sincronicen al instante.

### Instalación (Linux Ubuntu/Debian)
1. Instalar: `sudo apt install redis-server`
2. Configurar acceso remoto: `sudo nano /etc/redis/redis.conf`
   - Cambiar `bind 127.0.0.1` a `bind 0.0.0.0`
   - Buscar `# requirepass` y poner: `requirepass MiPasswordSeguro`
3. Reiniciar: `sudo systemctl restart redis-server`

### Configuración en `config.yml` (En cada servidor)
```yaml
Network:
  Mode: NETWORK
  ServerID: "overworld-1" # Único para cada server
  Redis:
    Host: "IP_DE_TU_REDIS"
    Port: 6379
    Password: "MiPasswordSeguro"
    Channel: "permadeath_sync"
```

---

## 2. Integración de Discord (Modo Híbrido)
En v1.5, solo necesitas configurar el bot en **un solo servidor** (el Maestro). Los demás le enviarán los datos por Redis.

### Servidor MAESTRO (El que tiene el bot)
En su `discord.yml`:
1. `Enable: true`
2. `Token: 'TU_TOKEN_DE_DISCORD'`
3. Configura los IDs de los canales:
   - `DeathChannel: 'ID_CANAL_MUERTES'`
   - `Anuncios: 'ID_CANAL_ANUNCIOS'`

### Servidores ESCLAVOS (Los demás)
En sus `discord.yml`:
1. `Enable: true`
2. `Token: ''` (Dejar vacío)
3. *Asegúrate de que el modo `NETWORK` esté activo en el config.yml.*

---

## 3. Sharding de Coordenadas (Mundo Distribuido)
El Sharding permite que un jugador cambie de servidor físico al cruzar una frontera de coordenadas.

### Configuración en `sharding.yml`
Define qué servidor se encarga de cada zona. El `ProxyName` debe ser el nombre exacto que tiene el servidor en tu **BungeeCord** o **Velocity**.

```yaml
CurrentShardID: "shard-1"

Shards:
  shard-1:
    ProxyName: "survival_norte"
    Bounds:
      MinX: -5000
      MaxX: 0
      MinZ: -5000
      MaxZ: 5000
  shard-2:
    ProxyName: "survival_sur"
    Bounds:
      MinX: 1
      MaxX: 5000
      MinZ: -5000
      MaxZ: 5000
```

---

## 4. Base de Datos (MySQL / MariaDB)
Para redes grandes, se recomienda usar MySQL en lugar de SQLite para que todos los servidores compartan la misma tabla de jugadores.

### Configuración en `config.yml`
*Nota: Asegúrate de tener el driver de MySQL en tu carpeta de librerías si el plugin no lo descarga solo.*

```yaml
Storage:
  Type: SQLITE # Cambiar a MYSQL en futuras actualizaciones de persistencia
```
*(Actualmente el plugin v1.5 usa SQLite local por defecto, pero el sistema de Sharding sincroniza el inventario vía Redis durante los saltos).*

---

## 5. Requisitos del Proxy (Velocity / BungeeCord)
Para que el Sharding funcione, el Proxy debe permitir mensajes de plugins:

1. **Velocity:** Asegúrate de que `bungee-plugin-message-channel = true` esté en `velocity.toml`.
2. **BungeeCord:** Funciona por defecto.
3. **Firewall:** Todos los servidores de juego deben poder comunicarse con la IP de Redis en el puerto **6379**.

---

## 6. Comandos de Verificación
- `/pdc config`: Abre el editor y verifica que el modo Red aparece como activo.
- `/pdc reload`: Recarga todas las configuraciones (incluyendo Redis).
- Mira la consola: Busca la línea `[RED] Sistema de red inicializado`.
