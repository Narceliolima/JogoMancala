import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import comunicacao.Conexao;


/*
 * Classe utilizada somente para testes, favor ignorar.
*/

public class PrincipalTeste extends Thread{
	
	private static ServerSocket serverSocket = null;
	private static Socket socket = null;
	private static DataOutputStream ostream = null;
	private static DataInputStream istream = null;
	private static int port = 9090;
	private static String host = "localhost";
	private static String mensagemEnv = "";
	private static String mensagemRec = "";
	private static Conexao conexao;
	//--------------------------------------------//--------------------------------------------//
	
	
	
	private static final int jogador1 = 0; //Trocar pra boolean
	private static final int jogador2 = 1; //Trocar pra boolean
	
	public PrincipalTeste() {
		this.start();
	}
	
	public static void main(String[] args) {
		
		
		int[][] tabuleiro ={{4,4,4,4,4,4,0},
							{4,4,4,4,4,4,0}}; //0 a 6
		
		conexao = new Conexao();
		conexao.setHost("localhost");
		conexao.setPort(9090);
		if(!conexao.conectaServidor()) {
			conexao.criaServidor();
		}
		new PrincipalTeste();
		Scanner s = new Scanner(System.in);
		String mensagem = s.nextLine();
		
		while(mensagem!="177") {
			conexao.enviaDado(mensagem+"a");
			conexao.enviaDado(mensagem+"b");
			conexao.enviaDado(mensagem+"c");
			mensagem = s.nextLine();
		}
		s.close();
		
		
		//System.out.println(Teste.jogar(1, 5, tabuleiro));
		//Conversor.imprimeMatriz(tabuleiro);
		
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
		
		/*System.out.println("Começou!");
		System.out.println("Jogador 1 joga");
		//String mensagem = win.getMensagem();
		int posicao = 999;

		//int posicao = sc.nextInt();
		int jogador = jogador1;
		int regra;*/
		
		/*
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
		System.out.println("Desistiram");*/
	}
	
	public void run() {
		while(true) {
			System.out.println(conexao.recebeDado());
		}
	}
}
