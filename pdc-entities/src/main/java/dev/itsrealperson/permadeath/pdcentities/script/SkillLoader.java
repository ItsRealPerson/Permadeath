package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SkillLoader {

    private final File skillsFolder;
    private final Logger logger;

    public SkillLoader(File folder, Logger logger) {
        this.skillsFolder = new File(folder, "skills");
        this.logger = logger;
        if (!skillsFolder.exists()) skillsFolder.mkdirs();
    }

    public void loadAll() {
        int count = scanAndLoad(skillsFolder);
        logger.info(" [PDC-Entities] Cargadas " + count + " MetaSkills personalizadas.");
    }

    private int scanAndLoad(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return 0;

        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                count += scanAndLoad(file);
            } else if (file.getName().endsWith(".yml")) {
                logger.info(" [PDC-Entities] Cargando habilidades desde: " + file.getName());
                loadSkills(file);
                count++;
            }
        }
        return count;
    }

    private void loadSkills(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String skillId : config.getKeys(false)) {
            List<String> lineList = config.getStringList(skillId);
            List<Skill> skills = new ArrayList<>();

            for (String line : lineList) {
                Skill skillObj = SkillParser.parse(line);
                if (skillObj != null) {
                    skills.add(skillObj); 
                }
            }
            SkillRegistry.registerSkill(skillId, skills);
        }
    }
}
