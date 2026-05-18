package engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Petit parseur JSON autonome utilise pour eviter une dependance externe.
 *
 * <p>Il prend en charge les objets, tableaux, chaines, nombres, booleens et
 * {@code null}, ce qui suffit au schema des pieces personnalisees du projet.</p>
 */
public class SimpleJsonParser {
    private final String json;
    private int index;

    /**
     * Cree un parseur pour une chaine JSON.
     *
     * @param json contenu JSON brut
     */
    public SimpleJsonParser(String json) {
        this.json = json;
    }

    /**
     * Parse le document complet.
     *
     * @return objet Java correspondant au JSON : {@link Map}, {@link List},
     *         {@link String}, {@link Number}, {@link Boolean} ou {@code null}
     * @throws IllegalArgumentException si le JSON est invalide
     */
    public Object parse() {
        Object value = parseValue();
        skipWhitespace();
        if (index != json.length()) {
            throw new IllegalArgumentException("Caracteres inattendus apres la fin du JSON.");
        }
        return value;
    }

    private Object parseValue() {
        skipWhitespace();
        if (index >= json.length()) {
            throw new IllegalArgumentException("Fin de JSON inattendue.");
        }

        char c = json.charAt(index);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBoolean();
        if (c == 'n') return parseNull();
        if (c == '-' || Character.isDigit(c)) return parseNumber();

        throw new IllegalArgumentException("Valeur JSON invalide a la position " + index + ".");
    }

    private Map<String, Object> parseObject() {
        expect('{');
        Map<String, Object> object = new LinkedHashMap<>();
        skipWhitespace();
        if (peek('}')) {
            expect('}');
            return object;
        }

        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            expect(':');
            object.put(key, parseValue());
            skipWhitespace();
            if (peek('}')) {
                expect('}');
                return object;
            }
            expect(',');
        }
    }

    private List<Object> parseArray() {
        expect('[');
        List<Object> array = new ArrayList<>();
        skipWhitespace();
        if (peek(']')) {
            expect(']');
            return array;
        }

        while (true) {
            array.add(parseValue());
            skipWhitespace();
            if (peek(']')) {
                expect(']');
                return array;
            }
            expect(',');
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder builder = new StringBuilder();

        while (index < json.length()) {
            char c = json.charAt(index++);
            if (c == '"') {
                return builder.toString();
            }

            if (c == '\\') {
                if (index >= json.length()) {
                    throw new IllegalArgumentException("Sequence d'echappement incomplete.");
                }
                builder.append(parseEscapedChar());
            } else {
                builder.append(c);
            }
        }

        throw new IllegalArgumentException("Chaine JSON non terminee.");
    }

    private char parseEscapedChar() {
        char escaped = json.charAt(index++);
        switch (escaped) {
            case '"': return '"';
            case '\\': return '\\';
            case '/': return '/';
            case 'b': return '\b';
            case 'f': return '\f';
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            case 'u':
                if (index + 4 > json.length()) {
                    throw new IllegalArgumentException("Code unicode JSON incomplet.");
                }
                String hex = json.substring(index, index + 4);
                index += 4;
                return (char) Integer.parseInt(hex, 16);
            default:
                throw new IllegalArgumentException("Sequence d'echappement invalide : \\" + escaped);
        }
    }

    private Boolean parseBoolean() {
        if (json.startsWith("true", index)) {
            index += 4;
            return Boolean.TRUE;
        }
        if (json.startsWith("false", index)) {
            index += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Booleen JSON invalide a la position " + index + ".");
    }

    private Object parseNull() {
        if (!json.startsWith("null", index)) {
            throw new IllegalArgumentException("Null JSON invalide a la position " + index + ".");
        }
        index += 4;
        return null;
    }

    private Number parseNumber() {
        int start = index;
        if (peek('-')) index++;
        while (index < json.length() && Character.isDigit(json.charAt(index))) index++;

        boolean decimal = false;
        if (peek('.')) {
            decimal = true;
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) index++;
        }

        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            decimal = true;
            index++;
            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) index++;
        }

        String value = json.substring(start, index);
        return decimal ? Double.parseDouble(value) : Integer.parseInt(value);
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }

    private boolean peek(char expected) {
        return index < json.length() && json.charAt(index) == expected;
    }

    private void expect(char expected) {
        if (!peek(expected)) {
            throw new IllegalArgumentException("Caractere attendu '" + expected + "' a la position " + index + ".");
        }
        index++;
    }
}
