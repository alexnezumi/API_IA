import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean acertou = false;

        while (!acertou) {
            try {
                Pergunta pergunta = GeminiAPI.gerarPergunta();
                System.out.println("Pergunta: " + pergunta.getPergunta());
                String[] opcoes = pergunta.getOpcoes();
                char letra = 'A';
                for (String opcao : opcoes) {
                    System.out.println(letra + ") " + opcao);
                    letra++;
                }
                System.out.print("Digite a letra da alternativa: ");
                String respostaUsuario = scanner.nextLine().trim().toUpperCase();

                int indiceCorreto = -1;
                for (int i = 0; i < opcoes.length; i++) {
                    if (opcoes[i].equalsIgnoreCase(pergunta.getResposta())) {
                        indiceCorreto = i;
                        break;
                    }
                }
                String letraCorreta = String.valueOf((char) ('A' + indiceCorreto));

                if (respostaUsuario.equals(letraCorreta)) {
                    System.out.println("Correto! Fim do programa.");
                    acertou = true;
                } else {
                    System.out.println("Errado! Tente novamente.\n");
                }
            } catch (Exception e) {
                System.out.println("Erro ao gerar pergunta: " + e.getMessage());
                break;
            }
        }
        scanner.close();
    }
}
