// java
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Set<String> perguntasFeitas = new HashSet<>();
        Set<String> palavrasProibidas = ForbiddenWords.getDefault();
        int pontos = 0;

        System.out.print("Digite seu nome: ");
        String nome = scanner.nextLine().trim();

        String tema;
        while (true) {
            System.out.print("""
                    Escreva qual tema você quer para o quiz 
                    (Tema de escolha livre): """);
            tema = scanner.nextLine().trim();
            if (tema.isEmpty()) {
                System.out.println("Tema vazio. Tente novamente.");
                continue;
            }
            if (ForbiddenWords.containsProibida(tema)) {
                System.out.println("Tema inválido — contém palavras proibidas. Digite outro tema.");
                continue;
            }
            break;
        }

        while (true) {
            Pergunta pergunta = null;
            int tentativas = 0;
            int maxTentativas = 10;
            boolean perguntaUnica = false;

            while (tentativas < maxTentativas) {
                try {
                    pergunta = GeminiAPI.gerarPergunta(perguntasFeitas, tema, palavrasProibidas);
                    if (!perguntasFeitas.contains(pergunta.getPergunta())) {
                        perguntasFeitas.add(pergunta.getPergunta());
                        perguntaUnica = true;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao gerar pergunta: " + e.getMessage());
                    return;
                }
                tentativas++;
            }

            if (!perguntaUnica) {
                System.out.println("Não foi possível gerar uma pergunta única. Encerrando o quiz.");
                break;
            }

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
                System.out.println("Correto!\n");
                pontos = pontos + 100;
            } else {
                System.out.println("Errado! A resposta correta era: " + letraCorreta);
                break;
            }
        }
        System.out.println("Quiz encerrado! " + nome + ", sua pontuação final foi: " + pontos);
        scanner.close();
    }
}
