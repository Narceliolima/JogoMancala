import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/*
 * Classe utilizada somente para testes, favor ignorar.
*/

public class Principal {
	
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static DataOutputStream ostream = null;
	private static DataInputStream istream = null;
	private static int port = 9090;
	private static String host = "localhost";
	private static String mensagemEnv = "";
	private static String mensagemRec = "";
	//--------------------------------------------//--------------------------------------------//
	
	
	
	private static final int jogador1 = 0; //Trocar pra boolean
	private static final int jogador2 = 1; //Trocar pra boolean
	
	public static void main(String[] args) {
		
		
		int[][] tabuleiro ={{0,4,4,4,4,4,4},
							{4,4,4,4,4,4,0}}; //0 a 6
		
		int[][] teste = new int[1][1];
		
		//new Jogador(tabuleiro).start();
		
		/*try {
			serverSocket = new ServerSocket(port);
			System.out.println("Aguardando conexão...");
			socket = serverSocket.accept();
			System.out.println("Conexão Estabelecida.");
			ostream = new DataOutputStream(socket.getOutputStream());
			istream = new DataInputStream(socket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
				
		/*GUI win = new GUI(tabuleiro);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					win.getJFrame().setVisible(true);
					win.varreBotao();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
		
		System.out.println("Começou!");
		System.out.println("Jogador 1 joga");
		//String mensagem = win.getMensagem();
		int posicao = 999;

		//int posicao = sc.nextInt();
		int jogador = jogador1;
		int regra;
		
		int[][] t = tabuleiro.clone();
		
		System.out.println(tabuleiro);
		System.out.println(t);
		Mancala.imprimeMatriz(tabuleiro);
		tabuleiro[0][3] = 20;
		Mancala.imprimeMatriz(t);
		
		while(posicao!=999) {
			//Cuidado com os static....
			regra = Mancala.jogar(jogador, posicao, tabuleiro);
			//win.atualizaInterface();
			Mancala.imprimeMatriz(tabuleiro);
			Mancala.verificaVazios(tabuleiro);
			if(jogador==jogador1&&regra!=1) {
				jogador = jogador2;
			}
			else if(regra!=2){
				jogador = jogador1;
			}
			System.out.println("Jogador "+(jogador+1)+" joga");
			posicao = 1;
		}
		//sc.close();
		System.out.println("Desistiram");
	}
	
	public void colocan() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Aguardando conexão...");
			socket = serverSocket.accept();
			System.out.println("Conexão Estabelecida.");
			ostream = new DataOutputStream(socket.getOutputStream());
			istream = new DataInputStream(socket.getInputStream());

			//this.start();

			Scanner console = new Scanner(System.in);
			while(true){
				System.out.println("Mensagem: ");
				String MSnd = console.nextLine(); 
				ostream.writeUTF(MSnd);
				ostream.flush();
			}
		} catch(Exception e){
			System.out.println(e);
		}
	}
}
