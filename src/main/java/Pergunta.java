public class Pergunta {
    private String pergunta;
    private String resposta;
    private String[] opcoes;

    public Pergunta(String pergunta, String resposta, String[] opcoes) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.opcoes = opcoes;
    }

    public String getPergunta() { return pergunta; }
    public String getResposta() { return resposta; }
    public String[] getOpcoes() { return opcoes; }
}