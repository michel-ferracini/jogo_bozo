import java.net.*;
import java.io.*;
import java.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

public class SocketServer extends Thread {
	String mensagem_servidor;
	private Socket clientSocket;
	private static Vector<PrintStream> CLIENTES;
	private String nomeCliente;
	private static List<String> LISTA_DE_NOMES = new ArrayList<String>();
	private static List<Placar> LISTA_DE_PLACAR = new ArrayList<Placar>();

	public SocketServer(Socket socket) {
		this.clientSocket = socket;
	}

	public boolean armazena(String novoNome) {
		System.out.println(LISTA_DE_NOMES);
		for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
			if (LISTA_DE_NOMES.get(i).equals(novoNome)) {
				return true;
			}
		}
		LISTA_DE_NOMES.add(novoNome);
		return false;
	}

	public void remove(String outNome) {
		for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
			if (LISTA_DE_NOMES.get(i).equals(outNome)) {
				LISTA_DE_NOMES.remove(outNome);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		CLIENTES = new Vector<PrintStream>();

		try {
			serverSocket = new ServerSocket(12345); // Bind
			System.out.println("Aguardando jogadores...");

			while (true) {
				Socket clientSocket = serverSocket.accept();
				Thread t = new SocketServer(clientSocket);
				t.start(); // Quando iniciado, chama o método run
			}
		} catch (IOException e) {
			System.err.println("Falha ao conectar.");
			System.exit(1);
		}

		System.out.println("Conexão realizada com sucesso.");
		System.out.println("Aguardando jogadores...");
	} // Fim do main

	public void run() {
		try {
			PrintStream out = new PrintStream(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			this.nomeCliente = in.readLine();

			if (armazena(this.nomeCliente)) {
				out.println("Nome já existe. Entre com outro nome.");
				this.clientSocket.close();
				return;
			} else {
				System.out.println(this.nomeCliente + " entrou no jogo.");
				mensagem_servidor = this.nomeCliente + ", bem vindo ao BOZÓ!\n";

				if (LISTA_DE_NOMES.size() < 4) {
					mensagem_servidor = mensagem_servidor + "Aguardando mais "
							+ (4 - LISTA_DE_NOMES.size()) + " jogadores...\n";
				}

				mensagem_servidor = mensagem_servidor + "==================\n";
				out.println(mensagem_servidor);

				if (LISTA_DE_NOMES.size() == 4) {
					mensagem_servidor = "O número de jogadores está completo.\n"
							+ "Aguarde " + LISTA_DE_NOMES.get(0)
							+ " digitar (1) para iniciar o Jogo!";
					sendMsg(out, mensagem_servidor);

					out.println(mensagem_servidor);
				}
			}
			if (this.nomeCliente == null) {
				return;
			}
			CLIENTES.add(out);
			String inputLine = in.readLine();

			// O jogo começa aqui...

			RolaDados dados = new RolaDados(5); // Cria os cinco dados.
			Placar placar1 = new Placar(); // Cria placar para a dupla de índices 0 e 2.
			Placar placar2 = new Placar(); // Cria placar para a dupla de índices 1 e 3.
			LISTA_DE_PLACAR.add(placar1);
			LISTA_DE_PLACAR.add(placar2);

			int rodada = 10;

			// Guarda os resultados sorteados para a dupla de índices 0 e 2:
			int resultados1[] = {};
			// Guarda os resultados sorteados para a dupla de índices 1 e 3:
			int resultados2[] = {};
			while (inputLine != null
					&& !(inputLine.trim().equals(""))
					&& LISTA_DE_NOMES.size() == 4) {
				while (rodada > 0) {
					out.println("\nEsta é a rodada " + (11 - rodada) + ".\n");

					int indice = LISTA_DE_NOMES.indexOf(this.nomeCliente);
					if (indice == 0 || indice == 2) {
						resultados1 = dados.rolar();
					} else {
						resultados2 = dados.rolar();
					}

					sendMsg(out, dados.toString());
					out.println(dados);
					out.println(
							"Escolha os dados a serem rolados novamente "
									+ "ou tecle ENTER para finalizar a rodada.\n");
					inputLine = in.readLine();

					String aSeremRolados = inputLine.toString();
					if (!aSeremRolados.equals("")) {

						if (indice == 0 || indice == 2) {
							resultados1 = dados.rolar(aSeremRolados);
						} else {
							resultados2 = dados.rolar(aSeremRolados);
						}

						out.println(dados);
						sendMsg(out, dados.toString());
						out.println("Jogando: \nEscolha os dados a serem rolados novamente "
								+ "ou tecle ENTER para finalizar a rodada.\n");
						inputLine = in.readLine();
						aSeremRolados = inputLine.toString();

						if (!aSeremRolados.equals("")) {

							if (indice == 0 || indice == 2) {
								resultados1 = dados.rolar(aSeremRolados);
							} else {
								resultados2 = dados.rolar(aSeremRolados);
							}
						}
						sendMsg(out, "Jogando: \n" + dados.toString());
						out.println(dados);
					}

					sendMsg(out, "Jogando: \n" + dados.toString());
					out.println(dados);

					if (indice == 0 || indice == 2) {
						sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
						out.println(LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
					} else {
						sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
						out.println(LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
					}

					out.println("Escolha uma posicao de 1 a 10 para ser ocupada\n");
					inputLine = in.readLine();
					int posicaoJogar = Integer.parseInt(inputLine);

					try {
						if (indice == 0 || indice == 2) {
							LISTA_DE_PLACAR.get(0).add(posicaoJogar, resultados1);
							out.println(LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
							sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
						} else {
							LISTA_DE_PLACAR.get(1).add(posicaoJogar, resultados2);
							out.println(LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
							sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
						}

						out.println(this.nomeCliente
								+ ", sua rodada foi finalizada.");
						sendMsg(out, "Aviso: \nÉ a vez do "
								+ proximoJogador(this.nomeCliente) + " jogar!");
						sendMsg(out, "O jogador "
								+ proximoJogador(this.nomeCliente)
								+ " deve digitar 1 para continuar.");

						inputLine = in.readLine();
					} catch (IllegalArgumentException e) {
						System.out.println(e.getMessage());
						out.println("Escolha uma posição válida para ser ocupada.");
						inputLine = in.readLine();
						posicaoJogar = Integer.parseInt(inputLine);

						if (indice == 0 || indice == 2) {
							LISTA_DE_PLACAR.get(0).add(posicaoJogar, resultados1);
						} else {
							LISTA_DE_PLACAR.get(1).add(posicaoJogar, resultados2);
						}

						// out.println("Sua rodada foi finalizada.");
						out.println(this.nomeCliente
								+ ", sua rodada foi finalizada.");
						sendMsg(out, "Aviso: \nÉ a vez do "
								+ proximoJogador(this.nomeCliente)
								+ " jogar!");
						sendMsg(out, "O jogador "
								+ proximoJogador(this.nomeCliente)
								+ " deve digitar 1 para continuar.");
					}

					rodada--;
				} // Fim do while que gerencia as rodadas

				resultados(out, LISTA_DE_PLACAR.get(0), LISTA_DE_PLACAR.get(1));
				inputLine = in.readLine();
			} // Fim do While que gerencia os jogadores

			// O jogo acaba aqui.

			// Se cliente enviar linha em branco, servidor encerra a conexão.
			System.out.println(this.nomeCliente + " desconectou.");
			// Mensagem de saida do chat aos CLIENTES conectados:
			sendMsg(out, " Saiu !!!");
			// Remove nome da lista
			remove(this.nomeCliente);
			// Exclui atributos relacionados ao cliente:
			CLIENTES.remove(out);
			// Fecha a conexão com este cliente:
			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println("Falha na Conexão...\nIOException: " + e);
		}
	} // Fim run

	public String proximoJogador(String nomeAtual) {
		String proximo = "";
		int indice = LISTA_DE_NOMES.indexOf(nomeAtual);
		switch (indice) {
			case 0:
				proximo = LISTA_DE_NOMES.get(1);
				break;
			case 1:
				proximo = LISTA_DE_NOMES.get(2);
				break;
			case 2:
				proximo = LISTA_DE_NOMES.get(3);
				break;
			case 3:
				proximo = LISTA_DE_NOMES.get(0);
				break;
		}
		return proximo;
	}

	public void resultados(PrintStream out, Placar placar1, Placar placar2) {
		out.println("A dupla "
				+ LISTA_DE_NOMES.get(0)
				+ " e "
				+ LISTA_DE_NOMES.get(2)
				+ " obteve "
				+ placar2.getScore()
				+ " pontos!\n");

		out.println("A dupla "
				+ LISTA_DE_NOMES.get(1)
				+ " e "
				+ LISTA_DE_NOMES.get(3)
				+ " obteve "
				+ placar2.getScore()
				+ " pontos!\n");

		// Pontuação final das duplas:
		int pontosDupla1 = placar1.getScore();
		int pontosDupla2 = placar2.getScore();

		if (pontosDupla1 > pontosDupla2) {
			out.println("A dupla "
					+ LISTA_DE_NOMES.get(0)
					+ " e "
					+ LISTA_DE_NOMES.get(2)
					+ " venceu!");
		} else if (pontosDupla2 > pontosDupla1) {
			out.println("A dupla "
					+ LISTA_DE_NOMES.get(1)
					+ " e "
					+ LISTA_DE_NOMES.get(3)
					+ " venceu!");
		} else {
			out.println("O jogo terminou empatado.");
		}
	}

	public void sendMsg(PrintStream out, String msg) {
		Enumeration<PrintStream> e = CLIENTES.elements();
		while (e.hasMoreElements()) {
			// Obtém o fluxo de saída de um dos clientes:
			PrintStream chat = (PrintStream) e.nextElement();
			if (chat != out) {
				// Envia mensagem para todos, menos para o próprio jogador:
				chat.println(msg);
			}
		}
	}
} // Fim class SocketServer