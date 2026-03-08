package dev.itsrealperson.permadeath.api.script;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilería para procesar cadenas de texto de scripts YAML.
 */
public class ScriptUtil {

    private static final Pattern ARGS_PATTERN = Pattern.compile("([a-zA-Z0-9_]+)=(['\"]?)(.+?)\\2(?=;|$|})");

    /**
     * Parsea argumentos de una cadena estilo "key1=val1;key2='val con espacios'".
     * @param input La cadena de argumentos (ej: "amount=5.0;type=FIRE").
     * @return Un mapa con los argumentos.
     */
    public static Map<String, String> parseArgs(String input) {
        Map<String, String> args = new HashMap<>();
        if (input == null || input.isEmpty()) return args;

        Matcher matcher = ARGS_PATTERN.matcher(input);
        while (matcher.find()) {
            args.put(matcher.group(1).toLowerCase(), matcher.group(3));
        }
        return args;
    }

    /**
     * Extrae el ID de una acción y sus argumentos de una línea completa.
     * Ejemplo: "broadcast{message='Hola'}" -> ID: broadcast, Args: {message=Hola}
     */
    public static ParsedScriptLine parseLine(String line) {
        if (!line.contains("{")) {
            return new ParsedScriptLine(line.toLowerCase(), new HashMap<>());
        }
        
        String id = line.substring(0, line.indexOf("{")).toLowerCase().trim();
        String argsPart = line.substring(line.indexOf("{") + 1, line.lastIndexOf("}")).trim();
        
        return new ParsedScriptLine(id, parseArgs(argsPart));
    }

    public record ParsedScriptLine(String id, Map<String, String> args) {}
}
