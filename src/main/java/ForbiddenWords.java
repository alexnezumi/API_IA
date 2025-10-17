// java
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.Locale;

public final class ForbiddenWords {
    private static final Set<String> DEFAULT;
    static {
        DEFAULT = new HashSet<>(Arrays.asList(
                "porra", "caralho", "fdp", "pqp", "filha da puta", "rola",
                "vagina", "buceta", "cu", "merda", "scat", "criolo", "prostituta", "puta que pariu", "sexo",
                "preto macaco", "bicha", "boila", "viado", "negro macaco", "rapariga", "pretinho raça ruim",
                "cabelo duro", "mother fuck", "beijo grego","bixa"
        ));
    }

    public static Set<String> getDefault() {
        return new HashSet<>(DEFAULT); // retorna cópia mutável
    }

    public static boolean containsProibida(String texto) {
        if (texto == null) return false;
        String temaNorm = texto.trim().toLowerCase(Locale.ROOT);
        if (temaNorm.isEmpty()) return false;
        for (String p : DEFAULT) {
            if (p == null) continue;
            String proibidaNorm = p.trim().toLowerCase(Locale.ROOT);
            if (proibidaNorm.isEmpty()) continue;
            if (temaNorm.equals(proibidaNorm)) {
                return true;
            }
        }
        return false;
    }
}
