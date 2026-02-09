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
        
        // 3. Verificar e Instalar
        if (Files.exists(datapackDir.resolve("pack.mcmeta"))) {
            return; // Ya instalado
        }

        logger.info("--------------------------------------------------");
        logger.info(" [Permadeath] Bootstrap: Instalando Datapack...");
        
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
            "data/permadeath/enchantment/abyssal_breathing.json"
        };

        for (String filePath : files) {
            // Cargar desde el JAR usando el ClassLoader del Bootstrap
            InputStream in = getClass().getClassLoader().getResourceAsStream("internal_datapack/" + filePath);
            if (in == null) {
                throw new IOException("Recurso no encontrado en JAR: " + filePath);
            }

            Path outFile = targetDir.resolve(filePath);
            Files.createDirectories(outFile.getParent());
            Files.copy(in, outFile, StandardCopyOption.REPLACE_EXISTING);
            in.close();
        }
    }
}
