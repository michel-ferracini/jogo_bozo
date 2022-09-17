package servidor;

import java.net.*;
import java.io.*;
import java.util.*;

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

    public boolean verificaEntrada(String[] entradasValidas, String aSeremRolados) {
        List<String> list = Arrays.asList(entradasValidas);

        boolean continua = true;
        List<String> aVerificar = List.of(aSeremRolados.split(" "));
        for (String i : aVerificar) {
            if (!list.contains(i)) {
                continua = false;
            }
        }
        return continua;
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

            while (armazena(this.nomeCliente)) {
                out.println("Nome já existe. Entre com outro nome.");
                this.nomeCliente = in.readLine().toUpperCase();
//				this.clientSocket.close();
            }
            System.out.println(this.nomeCliente + " entrou no jogo.");
            mensagem_servidor = this.nomeCliente + ", bem vindo ao BOZÓ!";
            out.println(mensagem_servidor);
            Thread.sleep(5000);

            if (LISTA_DE_NOMES.size() < 4) {
                mensagem_servidor = "\nAguardando mais "
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
            if (LISTA_DE_NOMES.size() > 4) {
                mensagem_servidor = "O número de jogadores está completo.\n"
                        + " Você será desconectado!";
                out.println(mensagem_servidor);
                this.clientSocket.close();
            }

            if (this.nomeCliente == null) {
                return;
            }
            CLIENTES.add(out);
            String inputLine = in.readLine();

            String paraValidar = inputLine;
            String[] entradasValidas = new String[]{"1"};
            while (!verificaEntrada(entradasValidas, paraValidar)) {
                out.println("Jogando: Entrada inválida, tecle 1 para jogar.");
                inputLine = in.readLine();
                paraValidar = inputLine.toString();
            }

            // O jogo começa aqui...

            RolaDados dados = new RolaDados(5); // Cria os cinco dados.
            Placar placar1 = new Placar(); // Cria placar para a dupla de índices 0 e 2.
            Placar placar2 = new Placar(); // Cria placar para a dupla de índices 1 e 3.
            LISTA_DE_PLACAR.add(placar1);
            LISTA_DE_PLACAR.add(placar2);

            int rodada = 5;

            // Guarda os resultados sorteados para a dupla de índices 0 e 2:
            int resultados1[] = {};
            // Guarda os resultados sorteados para a dupla de índices 1 e 3:
            int resultados2[] = {};
            while (inputLine != null
                    && !(inputLine.trim().equals(""))
                    && LISTA_DE_NOMES.size() == 4) {
                while (rodada > 0) {
                    out.println("\nEsta é a rodada " + (6 - rodada) + ".\n");

                    int indice = LISTA_DE_NOMES.indexOf(this.nomeCliente);
                    if (indice == 0 || indice == 2) {
                        resultados1 = dados.rolar();
                    } else {
                        resultados2 = dados.rolar();
                    }

                    sendMsg(out, dados.toString());
                    out.println(dados);
                    out.println("Escolha os dados a serem rolados novamente "
                            + "ou tecle ENTER para finalizar a rodada.\n");
                    inputLine = in.readLine();

                    //Validação do input
                    String aSeremRolados = inputLine.toString();
                    entradasValidas = new String[]{"1", "2", "3", "4", "5", ""};
                    while (!verificaEntrada(entradasValidas, aSeremRolados)) {
                        out.println("Jogando: \nEntrada inválida, insira um número de 1 a 5 "
                                + "ou tecle ENTER para finalizar a rodada.\n");
                        inputLine = in.readLine();
                        aSeremRolados = inputLine.toString();
                    }
                    //a partir do momento que o input se torna válido segue a execução.


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
                        //validação input
                        aSeremRolados = inputLine.toString();
                        while (!verificaEntrada(entradasValidas, aSeremRolados)) {
                            out.println("Jogando: \nEntrada inválida, insira um número de 1 a 5 "
                                    + "ou tecle ENTER para finalizar a rodada.\n");
                            inputLine = in.readLine();
                            aSeremRolados = inputLine.toString();
                        }
                        //a partir do momento que o input se torna válido segue a execução.


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

                    out.println("Escolha uma posicao de 1 a 10 para ser ocupada.");
                    inputLine = in.readLine();
                    paraValidar = inputLine.toString();
                    //validação do input
                    entradasValidas = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                    boolean continua = true;
                    while (continua) {
                        if(verificaEntrada(entradasValidas, paraValidar)){
                            int posicaoJogar = Integer.parseInt(inputLine);
                            try {
                                if (indice == 0 || indice == 2) {
                                    LISTA_DE_PLACAR.get(0).add(posicaoJogar, resultados1);
                                    out.println(LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
                                    sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(0).toString(this.nomeCliente));
                                    break;
                                } else {
                                    LISTA_DE_PLACAR.get(1).add(posicaoJogar, resultados2);
                                    out.println(LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
                                    sendMsg(out, "Jogando: \n" + LISTA_DE_PLACAR.get(1).toString(this.nomeCliente));
                                    break;
                                }
                            } catch (IllegalArgumentException e) {
                                //o catch só roda uma vez, tem que fazer um loop eu acho.
                                System.out.println(e.getMessage());
                                out.println("Entrada inválida. Escolha uma posição válida para ser ocupada.");
                                inputLine = in.readLine();
                                paraValidar = inputLine.toString();
                            }
                        } else {
                            out.println("Jogando: Entrada inválida, insira um número de 1 a 10.");
                            inputLine = in.readLine();
                            paraValidar = inputLine.toString();
                        }
                    }


                    out.println(this.nomeCliente
                            + ", sua rodada foi finalizada.");
                    sendMsg(out, "Aviso: \nÉ a vez do "
                            + proximoJogador(this.nomeCliente) + " jogar!");
                    sendMsg(out, "O jogador "
                            + proximoJogador(this.nomeCliente)
                            + " deve digitar 1 para continuar.");

                    inputLine = in.readLine();
                    paraValidar = inputLine;
                    entradasValidas = new String[]{"1"};

                    while (!verificaEntrada(entradasValidas, paraValidar)) {
                        out.println("Jogando: Entrada inválida, tecle 1 para jogar.");
                        inputLine = in.readLine();
                        paraValidar = inputLine.toString();
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
        } catch (IOException | InterruptedException e) {
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
                + placar1.getScore()
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
        } else if (pontosDupla2 < pontosDupla1) {
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