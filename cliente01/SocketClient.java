import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SocketClient extends Thread {
	
	private Socket echoSocket;
	
	public SocketClient(Socket socket) {
		this.echoSocket = socket;
	}
    public static void main(String[] args) {
    
        try {
        	// Conecta ao servidor IP/Porta
        	
			Socket echoSocket = new Socket("127.0.0.1", 12345);
			// instacia os objetos que vao controlar o fluxo de comunicacao
			
			PrintStream out = new PrintStream(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.print("Nome:");
            String meuNome = in.readLine();
            // envia o nome para o servidor
            out.println(meuNome.toUpperCase());
            // instancia uma Thread para o IP/Porta conectador e inicia a thread
            Thread thread = new SocketClient3(echoSocket);
            thread.start();
            
            String msg;
            
            while(true) {
            	
            	// faz a leitura da mensagem a ser enviada 
            	//System.out.print("Mensagem: ");
            	msg = in.readLine();
            	// envia a mensagem para o servidor
            	out.println(msg);
            }
            
        } catch (IOException e) {
            System.err.println("Falha na Conexao... .. ." + " IOException: " + e);
            System.exit(1);
        }
    }
    // execucao da Thread
    public void run() {

    	try {
    		// recebe a mensagem do outro cliente atraves do servidor
    		BufferedReader in = new BufferedReader(new InputStreamReader(this.echoSocket.getInputStream()));
    		// String userInput;
    		String msg;
    		// while ((userInput = stdIn.readLine()) != null) {
				//Bozo bozo = new Bozo();
    		while(true) {
				
    			// obtem a messagem enviada pelo servidor
    			msg = in.readLine();

    			// if (msg == null) {
    			// 	System.out.println("Conexao encerrada!");
    			// 	System.exit(0);
    			// }
    			//System.out.println();
    			//exibe a mensagem recebida
    			System.out.println(msg);
    			//cria uma linha visual para resposta
    			//System.out.print("Responder > ");
    		}
    	} catch (IOException e) {
    		System.out.println("Ocorreu uma Falha... .. ." + " IOException: " + e);	
    	}
    }
}
