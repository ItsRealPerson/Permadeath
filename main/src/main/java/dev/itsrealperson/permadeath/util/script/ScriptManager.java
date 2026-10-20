package dev.itsrealperson.permadeath.util.script;

import dev.itsrealperson.permadeath.Main;
import dev.itsrealperson.permadeath.api.script.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación central del motor de scripts para Permadeath.
 */
public class ScriptManager implements ScriptManagerAPI {

    private final Main plugin;
    private final Map<String, ScriptAction> registeredActions = new ConcurrentHashMap<>();
    private final Map<String, ScriptCondition> registeredConditions = new ConcurrentHashMap<>();
    private final Map<ScriptTrigger, List<Script>> activeScripts = new ConcurrentHashMap<>();
    private final Set<String> overriddenLogic = ConcurrentHashMap.newKeySet();
    private boolean enabled = true;

    public ScriptManager(Main plugin) {
        this.plugin = plugin;
        // Inicializar listas de triggers
        for (ScriptTrigger trigger : ScriptTrigger.values()) {
            activeScripts.put(trigger, new ArrayList<>());
        }
    }

    @Override
    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("Scripting.Enabled", true);
    }

    @Override
    public void registerAction(String id, ScriptAction action) {
        registeredActions.put(id.toLowerCase(), action);
    }

    @Override
    public void registerCondition(String id, ScriptCondition condition) {
        registeredConditions.put(id.toLowerCase(), condition);
    }

    @Override
    public boolean isLogicOverridden(String logicId) {
        if (!isEnabled() || !plugin.getConfig().getBoolean("Scripting.EnableOverrides", true)) {
            return false;
        }
        return overriddenLogic.contains(logicId.toUpperCase());
    }

    @Override
    public void fireTrigger(ScriptTrigger trigger, Entity caster, Entity target, Map<String, Object> contextData) {
        if (!isEnabled()) return;
        
        List<Script> scripts = activeScripts.get(trigger);
        if (scripts == null || scripts.isEmpty()) return;

        ScriptContext context = new ScriptContext(caster, target, contextData);

        for (Script script : scripts) {
            script.execute(context);
        }
    }

    @Override
    public void reloadScripts() {
        plugin.reloadConfig();
        overriddenLogic.clear();
        for (ScriptTrigger trigger : ScriptTrigger.values()) {
            activeScripts.get(trigger).clear();
        }
        loadScripts();
        plugin.getLogger().info("[Scripts] Sistema de scripts recargado.");
    }

    public void loadScripts() {
        if (!isEnabled()) return;
        
        File scriptsFolder = new File(plugin.getDataFolder(), "scripts");
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
            extractDefaultScripts(scriptsFolder);
        }

        scanFolder(scriptsFolder);
    }

    private void extractDefaultScripts(File folder) {
        // Estructura Ramificada del Core
        Map<String, String[]> categories = new HashMap<>();
        categories.put("progresion", new String[]{"dia_1_base.yml", "dia_25_death_train.yml", "dia_60_scaling.yml"});
        categories.put("entidades", new String[]{"dia_20_hostiles.yml", "dia_30_replacements.yml", "dia_40_replacements.yml", "dia_50_replacements.yml", "nether_spawns.yml"});
        categories.put("mundo", new String[]{"dia_40_clima.yml", "dia_50_uhc.yml", "dia_60_ambiente.yml", "catastrofes.yml", "agua_acida.yml", "the_end.yml", "abismo_mecanicas.yml"});
        categories.put("mecanicas", new String[]{"totems.yml", "consumibles.yml", "picar_bloques.yml", "hornos.yml", "cubetas.yml"});
        categories.put("items", new String[]{"core_items.yml"});

        for (Map.Entry<String, String[]> entry : categories.entrySet()) {
            File catFolder = new File(folder, entry.getKey());
            if (!catFolder.exists()) catFolder.mkdirs();

            for (String s : entry.getValue()) {
                File out = new File(catFolder, s);
                if (!out.exists()) {
                    plugin.saveResource("scripts/" + entry.getKey() + "/" + s, false);
                }
            }
        }
    }

    private void scanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file);
            } else if (file.getName().endsWith(".yml")) {
                loadYamlFile(file);
            }
        }
    }

    private void loadYamlFile(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // El archivo puede ser una lista de scripts o un mapa con 'Scripts'
        List<Map<?, ?>> scriptList = (List<Map<?, ?>>) config.get("Scripts");
        if (scriptList == null) return;

        for (Map<?, ?> map : scriptList) {
            try {
                // Comprobar si el script está habilitado individualmente
                if (map.containsKey("Enabled") && !((Boolean) map.get("Enabled"))) {
                    continue;
                }

                String id = (String) map.get("Id");
                String triggerStr = (String) map.get("Trigger");
                if (id == null || triggerStr == null) continue;

                ScriptTrigger trigger = ScriptTrigger.valueOf(triggerStr.toUpperCase());
                Script script = new Script(id, trigger);

                // Cargar condiciones
                List<String> condLines = (List<String>) map.get("Conditions");
                if (condLines != null) {
                    for (String line : condLines) {
                        var parsed = ScriptUtil.parseLine(line);
                        ScriptCondition cond = registeredConditions.get(parsed.id());
                        if (cond != null) script.addCondition(cond, parsed.args());
                    }
                }

                // Cargar acciones
                List<String> actLines = (List<String>) map.get("Actions");
                if (actLines != null) {
                    for (String line : actLines) {
                        var parsed = ScriptUtil.parseLine(line);
                        ScriptAction act = registeredActions.get(parsed.id());
                        if (act != null) script.addAction(act, parsed.args());
                    }
                }

                // Cargar sobrescrituras (Overrides)
                if (map.containsKey("Override")) {
                    String override = (String) map.get("Override");
                    overriddenLogic.add(override.toUpperCase());
                }

                activeScripts.get(trigger).add(script);
                
            } catch (Exception e) {
                plugin.getLogger().severe("[Scripts] Error cargando script en " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    private void createDefaultScripts(File folder) {
        // Crear un ejemplo por defecto para que el usuario vea cómo funciona
        File example = new File(folder, "ejemplo_dia_20.yml");
        try {
            FileConfiguration config = new YamlConfiguration();
            List<Map<String, Object>> scripts = new ArrayList<>();
            
            Map<String, Object> day20 = new HashMap<>();
            day20.put("Id", "Hostiles_Dia_20");
            day20.put("Trigger", "ENTITY_SPAWN");
            day20.put("Override", "CORE_DAY_20_MOBS");
            day20.put("Conditions", Arrays.asList("day_at_least{value=20}", "is_passive{}"));
            day20.put("Actions", Arrays.asList("core_make_hostile{}", "fake_lightning{}", "broadcast{message='&c&l¡Un mob pasivo ha despertado!'}"));
            
            scripts.add(day20);
            config.set("Scripts", scripts);
            config.save(example);
        } catch (Exception ignored) {}
    }

    @Override
    public Collection<String> getRegisteredActions() {
        return Collections.unmodifiableCollection(registeredActions.keySet());
    }

    @Override
    public Collection<String> getRegisteredConditions() {
        return Collections.unmodifiableCollection(registeredConditions.keySet());
    }
}
