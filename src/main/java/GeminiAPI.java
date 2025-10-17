// java
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Set;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GeminiAPI {
    public static Pergunta gerarPergunta(Set<String> perguntasFeitas, String tema, Set<String> palavrasProibidas) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        while (true) {
            try {
                StringBuilder prompt = new StringBuilder(
                        "Gere uma pergunta sobre " + tema + " com 4 alternativas (3 erradas e 1 correta). " +
                                "Responda apenas no formato JSON: {\"pergunta\": \"...\", \"opcoes\": [\"...\", \"...\", \"...\", \"...\"], \"resposta\": \"...\"}."
                );
                if (!perguntasFeitas.isEmpty()) {
                    prompt.append(" Não repita nenhuma destas perguntas já feitas: ");
                    for (String feita : perguntasFeitas) {
                        prompt.append("\"").append(feita).append("\", ");
                    }
                }
              prompt.append(".");


                ObjectNode part = mapper.createObjectNode();
                part.put("text", prompt.toString());

                ArrayNode partsArray = mapper.createArrayNode();
                partsArray.add(part);

                ObjectNode content = mapper.createObjectNode();
                content.set("parts", partsArray);

                ArrayNode contentsArray = mapper.createArrayNode();
                contentsArray.add(content);

                ObjectNode requestBodyNode = mapper.createObjectNode();
                requestBodyNode.set("contents", contentsArray);

                String requestBody = mapper.writeValueAsString(requestBodyNode);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyB6pRlIaHiwh5OCLuYu8CDlinBQSwT-usU"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode root = mapper.readTree(response.body());
                JsonNode candidates = root.path("candidates");
                if (!candidates.isArray() || candidates.isEmpty()) {
                    continue;
                }
                JsonNode contentNode = candidates.get(0).path("content").path("parts");
                if (!contentNode.isArray() || contentNode.isEmpty()) {
                    continue;
                }
                String contentText = contentNode.get(0).path("text").asText();

                String jsonLimpo = "";
                int ini = contentText.indexOf("```json");
                int fim = contentText.indexOf("```", ini + 1);
                if (ini != -1 && fim != -1) {
                    jsonLimpo = contentText.substring(ini + 7, fim).trim();
                } else {
                    jsonLimpo = contentText.trim();
                }

                JsonNode perguntaJson = mapper.readTree(jsonLimpo);
                String pergunta = perguntaJson.get("pergunta").asText();
                String resposta = perguntaJson.get("resposta").asText();

                JsonNode opcoesNode = perguntaJson.get("opcoes");
                String[] opcoes = new String[opcoesNode.size()];
                for (int i = 0; i < opcoesNode.size(); i++) {
                    opcoes[i] = opcoesNode.get(i).asText();
                }

                if (containsProibida(pergunta, palavrasProibidas) ||
                        containsProibida(resposta, palavrasProibidas) ||
                        anyOptionContainsProibida(opcoes, palavrasProibidas)) {
                    continue; // rejeita e tenta novamente
                }

                return new Pergunta(pergunta, resposta, opcoes);
            } catch (Exception e) {
                continue;
            }
        }
    }

    private static boolean containsProibida(String texto, Set<String> proibidas) {
        if (texto == null || proibidas == null || proibidas.isEmpty()) return false;
        for (String p : proibidas) {
            Pattern pat = Pattern.compile("\\b" + Pattern.quote(p) + "\\b", Pattern.CASE_INSENSITIVE);
            if (pat.matcher(texto).find()) return true;
        }
        return false;
    }

    private static boolean anyOptionContainsProibida(String[] opcoes, Set<String> proibidas) {
        if (opcoes == null || proibidas == null || proibidas.isEmpty()) return false;
        for (String op : opcoes) {
            if (containsProibida(op, proibidas)) return true;
        }
        return false;
    }
}