package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Almacena las MetaSkills definidas por el usuario en YAML.
 */
public class SkillRegistry {

    private static final Map<String, List<Skill>> metaSkills = new HashMap<>();

    public static void registerSkill(String id, List<Skill> skills) {
        metaSkills.put(id.toLowerCase(), skills);
    }

    public static List<Skill> getSkill(String id) {
        List<Skill> skills = metaSkills.get(id.toLowerCase());
        if (skills == null) {
            dev.itsrealperson.permadeath.pdcentities.PdcEntitiesAddon.debug("§c[Registry] MetaSkill no encontrada: " + id);
        }
        return skills;
    }

    public static void clear() {
        metaSkills.clear();
    }
}
