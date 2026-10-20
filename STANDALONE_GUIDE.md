# Guía de Instalación Standalone: Permadeath v1.5

Esta guía detalla cómo instalar y configurar Permadeath v1.5 en un único servidor (sin red de servidores).

---

## 1. Requisitos Técnicos
*   **Versión de Minecraft:** 1.21.x (Probado en 1.21.1 y 1.21.3).
*   **Software de Servidor:** PaperMC o Folia (Recomendado para máximo rendimiento).
*   **Java:** Versión 21 o superior.
*   **Plugin Dependencia:** Ninguna (PacketEvents y Jedis ya están integrados en el JAR).

---

## 2. Instalación Básica
1.  Descarga el archivo `Permadeath.jar`.
2.  Colócalo en la carpeta `/plugins/` de tu servidor.
3.  Inicia el servidor para generar los archivos de configuración.
4.  (Opcional) Acepta el Resource Pack cuando el plugin te lo pida al entrar al juego.

---

## 3. Configuración Inicial (`config.yml`)
Para el modo Standalone, asegúrate de que la sección de red esté configurada así:

```yaml
Network:
  Mode: STANDALONE  # IMPORTANTE: Mantener en STANDALONE
  ServerID: "mi-servidor"
```

### Almacenamiento de Datos
El plugin usa **SQLite** por defecto, lo que crea un archivo `permadeath.db` en la carpeta del plugin. No necesitas instalar bases de datos externas.

Si prefieres usar archivos de texto tradicionales:
```yaml
Storage:
  Type: YAML
```

---

## 4. Configuración de Discord (Bot Local)
En modo Standalone, tu servidor será el **Maestro** y controlará el bot directamente.

1.  Ve al [Discord Developer Portal](https://discord.com/developers/applications).
2.  Crea una aplicación, ve a la sección "Bot" y obtén tu **Token**.
3.  Activa los **Privileged Gateway Intents** (Presence, Server Members, Message Content).
4.  En el archivo `discord.yml` del plugin:
    ```yaml
    Enable: true
    Token: 'TU_TOKEN_AQUÍ'
    Status: '¡Sobreviviendo a Permadeath!'
    Channels:
      DeathChannel: 'ID_DEL_CANAL_DE_MUERTES'
      Anuncios: 'ID_DEL_CANAL_DE_AVISOS'
    ```

---

## 5. El Abismo y Dimensiones
El plugin genera automáticamente las dimensiones `permadeath:abyss` y `permadeath:beginning`.

*   **Comando de acceso:** `/pdc abyss` (Solo si el Corazón del Abismo ha sido despertado).
*   **Beginning:** El portal se genera solo en el mundo principal a partir del Día 40.

---

## 6. Comandos Útiles para Administradores
*   `/pdc config`: Abre el editor visual para cambiar opciones sin reiniciar.
*   `/pdc dias <numero>`: Cambia el día actual del mundo.
*   `/pdc reload`: Recarga todas las configuraciones y módulos.
*   `/pdc info <jugador>`: Muestra el estado detallado de un superviviente.

---

## 7. Solución de Problemas Comunes
*   **¿Los ítems son invisibles?** Asegúrate de tener el Resource Pack activo. Puedes forzarlo con `/pdc reload`.
*   **¿El servidor va lento?** Activa `Toggles.Optimizar-Mob-Spawns` en el `config.yml` o desde el menú GUI.
*   **¿Crasheos en Folia?** Asegúrate de estar usando la última build del plugin y de no tener otros plugins de NMS incompatibles.
