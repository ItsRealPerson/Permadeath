package dev.itsrealperson.permadeath.pdcentities.script;

import dev.itsrealperson.permadeath.pdcentities.script.mechanic.Mechanic;
import dev.itsrealperson.permadeath.pdcentities.script.targeter.Targeter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillParser {

    private static final Pattern BASE_PATTERN = Pattern.compile("^([^\\s@~?{]+)(?:\\{([^}]*)})?\\s*@?([^\\s~?{]+)?(?:\\{([^}]*)})?\\s*~?([^\\s?]+)?");
    private static final Pattern COND_PATTERN = Pattern.compile("\\?([^\\s{]+)(?:\\{([^}]*)})?");

    public static Skill parse(String line) {
        if (line == null || line.isEmpty() || line.trim().startsWith("#")) return null;
        
        Matcher baseMatcher = BASE_PATTERN.matcher(line.trim());
        if (!baseMatcher.find()) {
            return null;
        }

        String mechanicId = baseMatcher.group(1);
        String mechArgsStr = baseMatcher.group(2);
        String targeterId = baseMatcher.group(3);
        String targetArgsStr = baseMatcher.group(4);
        String fullTrigger = baseMatcher.group(5);

        Mechanic mechanic = MechanicRegistry.create(mechanicId, parseArgs(mechArgsStr));
        if (mechanic == null) {
            org.bukkit.Bukkit.getLogger().warning("[PDC-Entities] [Parser] Mecánica no encontrada: " + mechanicId + " en línea: " + line);
            return null;
        }

        Map<String, String> targetArgs = parseArgs(targetArgsStr);
        Targeter targeter = targeterId != null ? TargeterRegistry.create(targeterId, targetArgs) : TargeterRegistry.create("self", new HashMap<>());
        
        if (targeter == null) {
            targeter = TargeterRegistry.create("self", new HashMap<>());
        }

        List<Condition> conditions = new ArrayList<>();
        Matcher condMatcher = COND_PATTERN.matcher(line);
        while (condMatcher.find()) {
            String condId = condMatcher.group(1);
            String condArgsStr = condMatcher.group(2);
            Condition cond = ConditionRegistry.create(condId, parseArgs(condArgsStr));
            if (cond != null) conditions.add(cond);
        }

        // Parsear trigger y su valor (ej: onTimer:20)
        String triggerId = "onTimer";
        int triggerValue = 20;
        if (fullTrigger != null) {
            if (fullTrigger.contains(":")) {
                String[] tParts = fullTrigger.split(":");
                triggerId = tParts[0];
                try {
                    triggerValue = Integer.parseInt(tParts[1]);
                } catch (NumberFormatException ignored) {}
            } else {
                triggerId = fullTrigger;
            }
        }

        Skill skill = new Skill(mechanic, targeter, triggerId, conditions);
        skill.setTriggerValue(triggerValue);
        return skill;
    }

    public static Map<String, String> parseArgs(String argsStr) {
        Map<String, String> args = new HashMap<>();
        if (argsStr == null || argsStr.isEmpty()) return args;

        // Soporte para ; y , como separadores
        String[] parts = argsStr.split("[;,]");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                String value = kv[1].trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                args.put(kv[0].trim().toLowerCase(), value);
            }
        }
        return args;
    }
}
