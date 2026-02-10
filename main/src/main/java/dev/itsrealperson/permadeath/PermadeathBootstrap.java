package dev.itsrealperson.permadeath;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Logger;

public class PermadeathBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        Logger logger = Logger.getLogger("PermadeathBootstrap");
        
        // 1. Detectar nombre del mundo principal
        String levelName = "world";
        Path serverProps = Path.of("server.properties");
        if (Files.exists(serverProps)) {
            try (InputStream in = Files.newInputStream(serverProps)) {
                Properties props = new Properties();
                props.load(in);
                levelName = props.getProperty("level-name", "world");
            } catch (IOException e) {
                logger.warning("No se pudo leer server.properties, usando 'world' por defecto.");
            }
        }

        // 2. Ruta de Datapacks
        Path datapackDir = Path.of(levelName, "datapacks", "Permadeath");
        
        logger.info("--------------------------------------------------");
        logger.info(" [Permadeath] Bootstrap: Instalando/Actualizando Datapack...");
        
        try {
            installFiles(datapackDir);
            logger.info(" [Permadeath] Datapack instalado en: " + datapackDir.toAbsolutePath());
            logger.info("--------------------------------------------------");
        } catch (Exception e) {
            logger.severe(" [Permadeath] ERROR CRITICO al instalar Datapack: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void installFiles(Path targetDir) throws IOException {
        String[] files = {
            "pack.mcmeta",
            "data/permadeath/dimension/abyss.json",
            "data/permadeath/dimension/beginning.json",
            "data/permadeath/dimension_type/abyss_type.json",
            "data/permadeath/dimension_type/beginning_type.json",
            "data/permadeath/enchantment/abyssal_breathing.json",
            "data/permadeath/worldgen/structure/beginning_island.json",
            "data/permadeath/worldgen/structure/beginning_portal.json",
            "data/permadeath/worldgen/structure/beginning_ytic.json",
            "data/permadeath/worldgen/structure_set/beginning_islands.json",
            "data/permadeath/worldgen/structure_set/beginning_ytic.json",
            "data/permadeath/worldgen/template_pool/beginning_islands.json",
            "data/permadeath/worldgen/template_pool/beginning_portal.json",
            "data/permadeath/worldgen/template_pool/beginning_ytic_pool.json",
            "data/permadeath/structures/beginning_portal.nbt",
            "data/permadeath/structures/island1.nbt",
            "data/permadeath/structures/island2.nbt",
            "data/permadeath/structures/island3.nbt",
            "data/permadeath/structures/island4.nbt",
            "data/permadeath/structures/island5.nbt",
            "data/permadeath/structures/ytic.nbt"
        };

        for (String filePath : files) {
            // Cargar desde el JAR usando el ClassLoader del Bootstrap
            InputStream in = getClass().getClassLoader().getResourceAsStream("internal_datapack/" + filePath);
            if (in == null) {
                // Logger ya definido en bootstrap(), lo obtenemos de nuevo o usamos System.err
                System.err.println("[Permadeath] Recurso no encontrado en JAR: internal_datapack/" + filePath);
                continue;
            }

            Path outFile = targetDir.resolve(filePath);
            Files.createDirectories(outFile.getParent());
            Files.copy(in, outFile, StandardCopyOption.REPLACE_EXISTING);
            in.close();
        }
    }
}
