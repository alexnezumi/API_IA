import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeminiAPI {
    public static Pergunta gerarPergunta() throws Exception {
        String prompt = "Gere uma pergunta de conhecimentos gerais com 4 alternativas (3 erradas e 1 correta). Responda apenas no formato JSON: {\\\"pergunta\\\": \\\"...\\\", \\\"opcoes\\\": [\"...\", \"...\", \"...\", \"...\"], \\\"resposta\\\": \\\"...\\\"}";

        String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyB6pRlIaHiwh5OCLuYu8CDlinBQSwT-usU"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new Exception("Resposta inesperada da API Gemini.");
        }
        JsonNode contentNode = candidates.get(0).path("content").path("parts");
        if (!contentNode.isArray() || contentNode.isEmpty()) {
            throw new Exception("Conteúdo inesperado na resposta da API Gemini.");
        }
        String content = contentNode.get(0).path("text").asText();

        // Extrai apenas o JSON entre ```json e ```
        String jsonLimpo = "";
        int ini = content.indexOf("```json");
        int fim = content.indexOf("```", ini + 1);
        if (ini != -1 && fim != -1) {
            jsonLimpo = content.substring(ini + 7, fim).trim();
        } else {
            throw new Exception("JSON não encontrado na resposta da API.");
        }

        JsonNode perguntaJson = mapper.readTree(jsonLimpo);
        String pergunta = perguntaJson.get("pergunta").asText();
        String resposta = perguntaJson.get("resposta").asText();

        // Extrai as opções
        JsonNode opcoesNode = perguntaJson.get("opcoes");
        String[] opcoes = new String[opcoesNode.size()];
        for (int i = 0; i < opcoesNode.size(); i++) {
            opcoes[i] = opcoesNode.get(i).asText();
        }

        return new Pergunta(pergunta, resposta, opcoes);
    }
}
