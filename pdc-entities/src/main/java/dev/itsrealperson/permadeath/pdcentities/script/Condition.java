package dev.itsrealperson.permadeath.pdcentities.script;

public interface Condition {
    /**
     * @param context El contexto a evaluar.
     * @return true si se cumple la condición.
     */
    boolean check(SkillContext context);
}
