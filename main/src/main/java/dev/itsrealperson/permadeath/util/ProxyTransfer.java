package dev.itsrealperson.permadeath.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.itsrealperson.permadeath.Main;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ProxyTransfer {

    public static void transferPlayer(Player player, String serverName) {
        if (Main.instance.getNetworkManager().isNetworkActive()) {
            // Guardar inventario en Redis temporalmente
            saveInventoryToRedis(player);
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
    }

    private static void saveInventoryToRedis(Player player) {
        try {
            // Serialización simplificada para el ejemplo
            // En producción usaríamos un serializador de inventario robusto (como el de NBT)
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream dataOutput = new ObjectOutputStream(outputStream);
            
            // Guardamos solo metadatos básicos por ahora como prueba de concepto
            dataOutput.writeUTF(player.getName());
            dataOutput.close();
            
            String encoded = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            
            // TTL de 30 segundos para la transferencia
            Main.instance.getNetworkManager().sendCustomMessage("INV_SYNC", player.getUniqueId() + ":" + encoded);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
