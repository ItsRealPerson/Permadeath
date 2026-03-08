package dev.itsrealperson.permadeath.api.script;

import java.util.Map;

/**
 * Evaluador de fórmulas matemáticas avanzado para Permadeath.
 * Soporta operaciones básicas, paréntesis y variables dinámicas.
 * Ahora forma parte del Core para permitir lógica programable.
 */
public class FormulaEvaluator {

    /**
     * Evalúa una expresión matemática con variables.
     * Ejemplo: "(day * 0.5) + 10" con {day=100} -> 60.0
     */
    public static double evaluate(String expression, Map<String, Double> variables) {
        // 1. Reemplazar variables
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            expression = expression.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        // Limpiar espacios
        expression = expression.replaceAll("\\s+", "");
        
        try {
            return eval(expression);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? (int) str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) return 0.0; // Error de sintaxis
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) return 0.0; // Evitar división por cero
                        x /= divisor;
                    }
                    else if (eat('%')) x %= parseFactor(); // modulo
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("abs")) x = Math.abs(x);
                    else if (func.equals("floor")) x = Math.floor(x);
                    else if (func.equals("ceil")) x = Math.ceil(x);
                    else return 0.0;
                } else {
                    return 0.0;
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
