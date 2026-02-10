package dev.itsrealperson.permadeath.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.util.TextUtils;

import java.io.File;
import java.util.Random;

/**
 * Clase encargada de la generación de estructuras manuales (Portal Overworld).
 * Ahora utiliza la API nativa de Bukkit (NBT) en lugar de WorldEdit.
 */
public class WorldEditPortal {

    public static void generatePortal(boolean overworld, Location to) {

        if (!Main.getInstance().getBeData().generatedOverWorldBeginningPortal() && overworld) {

            int x = Main.getInstance().getConfig().getInt("TheBeginning.X-Limit", 3000);
            int z = Main.getInstance().getConfig().getInt("TheBeginning.Z-Limit", 3000);

            int ranX = new Random().nextInt(x);
            int ranZ = new Random().nextInt(z);

            if (new Random().nextBoolean()) ranX *= -1;
            if (new Random().nextBoolean()) ranZ *= -1;
            
            Location loc = new Location(Main.getInstance().world != null ? Main.getInstance().world : Bukkit.getWorlds().get(0), ranX, 0, ranZ);

            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().run(Main.instance, loc, task -> {
                    int highestY = loc.getWorld().getHighestBlockYAt(loc);
                    if (highestY <= 0) highestY = 50;
                    loc.setY(highestY + 15);
                    
                    if (pasteStructure(loc, "beginning_portal")) {
                        Main.getInstance().getBeData().setOverWorldPortal(loc);
                        String msg = Main.instance.getMessages().getMsgForConsole("PortalGenerated").replace("%coords%", TextUtils.formatPosition(loc));
                        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + msg));
                    } else {
                        Main.instance.getLogger().severe("No se pudo generar el Portal del Overworld: Error al pegar la estructura NBT 'beginning_portal'.");
                    }
                });
            } else {
                int highestY = loc.getWorld().getHighestBlockYAt(loc);
                if (highestY <= 0) highestY = 50;
                loc.setY(highestY + 15);
                
                if (pasteStructure(loc, "beginning_portal")) {
                    Main.getInstance().getBeData().setOverWorldPortal(loc);
                    // Informamos de la posición exacta del bloque del portal (3, 2, 8)
                    Location center = loc.clone().add(3, 2, 8);
                    String msg = Main.instance.getMessages().getMsgForConsole("PortalGenerated").replace("%coords%", TextUtils.formatPosition(center));
                    Bukkit.broadcastMessage(TextUtils.format(Main.prefix + msg));
                } else {
                    Main.instance.getLogger().severe("No se pudo generar el Portal del Overworld: Error al pegar la estructura NBT 'beginning_portal'.");
                }
            }
        }

        // El portal interno de The Beginning ahora lo maneja el Datapack, 
        // pero mantenemos esto como fallback por si acaso se activa manualmente.
        if (!Main.getInstance().getBeData().generatedBeginningPortal() && !overworld) {
            if (Main.isRunningFolia()) {
                Bukkit.getRegionScheduler().run(Main.instance, to, task -> {
                    if (pasteStructure(to, "beginning_portal")) {
                        Main.getInstance().getBeData().setBeginningPortal(to);
                        Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&e¡Se ha generado un portal de salida en &b&lThe Beginning&e!"));
                    } else {
                        Main.instance.getLogger().severe("Error al generar portal de salida en The Beginning.");
                    }
                });
            } else {
                to.getWorld().loadChunk(to.getChunk());
                if (pasteStructure(to, "beginning_portal")) {
                    Main.getInstance().getBeData().setBeginningPortal(to);
                    Bukkit.broadcastMessage(TextUtils.format(Main.prefix + "&e¡Se ha generado un portal de salida en &b&lThe Beginning&e!"));
                } else {
                    Main.instance.getLogger().severe("Error al generar portal de salida en The Beginning.");
                }
            }
        }
    }

    /**
     * Pega una estructura NBT usando la API de Bukkit.
     * @param loc Ubicación donde pegar (será la esquina inferior)
     * @param structureName Nombre de la estructura (ej: "island1")
     * @return true si se pegó con éxito
     */
    public static boolean pasteStructure(Location loc, String structureName) {
        StructureManager sm = Bukkit.getStructureManager();
        
        // Intentar cargar desde el archivo en data/structures
        File structuresDir = new File(Main.instance.getDataFolder(), "data/structures");
        File structureFile = new File(structuresDir, structureName + ".nbt");
        
        Structure structure = null;
        if (structureFile.exists()) {
            try {
                structure = sm.loadStructure(structureFile);
            } catch (Exception e) {
                Main.instance.getLogger().severe("Error al cargar estructura NBT desde archivo: " + structureName);
            }
        }

        // Si no se pudo cargar por archivo, intentar por NamespacedKey (Datapack)
        if (structure == null) {
            NamespacedKey key = new NamespacedKey("permadeath", structureName);
            structure = sm.getStructure(key);
        }

        if (structure != null) {
            // El orden correcto es: Location, entities (bool), Rotation, Mirror, paletteIndex (int), integrity (float), Random
            structure.place(loc, true, org.bukkit.block.structure.StructureRotation.NONE, org.bukkit.block.structure.Mirror.NONE, 0, 1.0f, new Random());
            return true;
        }

        return false;
    }
}