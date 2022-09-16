package cliente03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SocketClient3 extends Thread {

	private Socket echoSocket;
	private static String msgRecebida = "";
	private static boolean enviaMsg = false;

	public SocketClient3(Socket socket) {
		this.echoSocket = socket;
	}

	public static void main(String[] args) {

		try {
			// Conecta ao servidor IP/Porta:
			Socket echoSocket = new Socket("127.0.0.1", 12345);

			// Instacia os objetos que vao controlar o fluxo de comunicação:
			PrintStream out = new PrintStream(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Nome: ");
			String meuNome = in.readLine();

			// Envia o nome para o servidor:
			out.println(meuNome.toUpperCase());

			// Instancia uma Thread para o IP/Porta conectador e inicia a thread:
			Thread thread = new SocketClient3(echoSocket);
			thread.start();

			String msg;

			while (true) {
				String instrucaoInicio = "Aguarde " + meuNome.toUpperCase()
						+ " digitar (1) para iniciar o Jogo!";
				String instrucaoProsseguimento = "O jogador " + meuNome.toUpperCase()
						+ " deve digitar 1 para continuar.";
				String instrucaoFim = "Escolha uma posicao de 1 a 10 para ser ocupada.";
				String instrucaoNomeInvalido = "Nome já existe. Entre com outro nome.";

				boolean verificaInicio = msgRecebida.equals(instrucaoInicio);
				boolean verificaProsseguimento = msgRecebida.equals(instrucaoProsseguimento);
				boolean verificaFim = msgRecebida.equals(instrucaoFim);
				boolean verificaNome = msgRecebida.equals(instrucaoNomeInvalido);

				if (verificaInicio || verificaProsseguimento || verificaNome) {
//					System.out.println("Inicio: " + verificaInicio
//							+ "\nFim: " + verificaProsseguimento
//							+ "\nNome: " + verificaNome);
					enviaMsg = true;
				}

				if (enviaMsg) {
					if (verificaFim) {
//						System.out.println("Verifica fim.");
						enviaMsg = false;
					}
					Thread.sleep(2000);
					if(msgRecebida.contains("bem vindo ao BOZÓ!")){
//						System.out.println("Verifica novoNOme.");
						meuNome = msgRecebida.split(",")[0];
						enviaMsg = false;
					}
//					System.out.println();
//					System.out.println(msgRecebida);
//					System.out.println(msgRecebida.contains("bem vindo ao BOZÓ!"));
					if (enviaMsg) {
//						System.out.println(meuNome + "envia mensagem.");
						// Faz a leitura da mensagem a ser enviada:
						msg = in.readLine();
						// Envia a mensagem para o servidor:
						out.println(msg);
					}
				}
			}

		} catch (IOException | InterruptedException e) {
			System.err.println("Falha na Conexao... .. ." + " IOException: " + e);
			System.exit(1);
		}
	}

	// execucao da Thread
	public void run() {

		try {
			// recebe a mensagem do outro cliente atraves do servidor
			BufferedReader in = new BufferedReader(new InputStreamReader(this.echoSocket.getInputStream()));
			while (true) {
				// obtem a messagem enviada pelo servidor
				msgRecebida = in.readLine();
				// exibe a mensagem recebida
				System.out.println(msgRecebida);
				// cria uma linha visual para resposta
			}
		} catch (IOException e) {
			System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);
		}
	}
}
