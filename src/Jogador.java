import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Jogador extends Thread{
	
	private ServerSocket serverSocket = null;
	private DataOutputStream ostream = null;
	private DataInputStream istream = null;
	private String host = "localhost";
	private int port = 9090;
	private Socket socket = null;
	private String mensagemEnv = "";
	private String mensagemRec = "";
	private GUI win;
	//--------------------------------------------//--------------------------------------------//
	private ArrayList<Integer> historico;
	private boolean fim = false;
	private int jogador = 1;
	private int jogando = 0;
	private static final int jogador1 = 0;
	private static final int jogador2 = 1;

	public Jogador(int[][] tabuleiro) {
		
		win = new GUI(tabuleiro);
		historico = new ArrayList<Integer>();
		createRunnable();
		host = Mancala.configuraHost();
		port = Mancala.configuraPorta();
		
		try {
			socket = new Socket(host, port);
		} catch(IOException e)	{
			win.setMensagemEnviada("Host não encontrado.");
			win.setMensagemEnviada("Criando servidor...");
			try {
				serverSocket = new ServerSocket(port);
				win.setMensagemEnviada("Aguardando conexão...");
				socket = serverSocket.accept();
				win.setMensagemEnviada("Conexão Estabelecida.");
				this.jogador = 0;
			} catch (Exception e2) {
				System.out.println(e2);
			}
		}
		
		try {
			ostream = new DataOutputStream(socket.getOutputStream());
			istream = new DataInputStream(socket.getInputStream());
			this.start();
			
			int novamente = 0;
			
			while(novamente == 0) {
				int posicao = 0;
				int regra;
				int ganhador = -1;
				win.setJogador(jogador);
				
				win.setMensagemEnviada("Bem vindo ao Mancala");
				win.setMensagemEnviada("Você e o Jogador "+(jogador+1));
				mensagemEnv = "msg:>Conectado";
				
				win.salvaTabuleiro();
				salvaJogador();
				
				while(mensagemEnv!="sur:>"&&!fim) {
					if(mensagemEnv.contains("msg:>")&&mensagemEnv.indexOf(">")==4) {
						ostream.writeUTF(mensagemEnv.substring(0, 5)+"Jogador "+(jogador+1)+": "+mensagemEnv.substring(5));
						ostream.flush();
					}
					else if(mensagemEnv.contains("int:>")&&mensagemEnv.indexOf(">")==4&&this.jogador==jogando) {
						posicao = Integer.parseInt(mensagemEnv.replaceFirst("int:>", ""));
						tabuleiro = win.getTabuleiro();
						regra = Mancala.jogar(jogando, posicao, tabuleiro);
						ganhador = Mancala.verificaVazios(tabuleiro);
						if(ganhador!=-1) {
							fim = true;
							jogando = ganhador;
						}
						else if(jogando==jogador1&&regra!=1) {
							jogando = jogador2;
						}
						else if(regra!=2){
							jogando = jogador1;
						}
						win.salvaTabuleiro();
						salvaJogador();
						ostream.writeUTF("tab:>"+Mancala.toIstring(tabuleiro));
						ostream.writeUTF("jog:>"+jogando);
						if(fim) {
							ostream.writeUTF("fim:>");
						}
						ostream.flush();
					}
					else if(mensagemEnv.contains("vol:>")&&mensagemEnv.indexOf(">")==4&&this.jogador==jogando) {
						win.voltaJogada();
						voltaJogada();
						ostream.writeUTF(mensagemEnv);
						ostream.flush();
					}
					win.turno(jogando);
					win.atualizaInterface();
					if(!fim) {
						mensagemEnv = win.getMensagem();
					}
					if(mensagemEnv=="sur:>"&&Mancala.confirmaDesistencia()==1) {
						mensagemEnv = "";
					}
				}
				if(mensagemEnv=="sur:>") {
					ostream.writeUTF(mensagemEnv+jogador);
					if(jogador==jogador1) {
						ostream.writeUTF("jog:>"+jogador2);
					}
					else {
						ostream.writeUTF("jog:>"+jogador1);
					}
					ostream.flush();
					Mancala.derrota();
				}
				else if(jogando==jogador) {
					Mancala.vitoria();
				}
				else if(jogando==2) {
					Mancala.empate();
				}
				else {
					Mancala.derrota();
				}
				
				novamente = Mancala.confirmaJogarNovamente();
				
				if(novamente == 0) {
					int[][] newTabuleiro ={{0,4,4,4,4,4,4},
										   {4,4,4,4,4,4,0}}; //0 a 6
					win.setTabuleiro(newTabuleiro);
					fim = false;
				}
			}
			System.exit(0);
		} catch(Exception e) {//System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		int[][] tabuleiro ={{0,4,4,4,4,4,4},
							{4,4,4,4,4,4,0}}; //0 a 6

		new Jogador(tabuleiro);

	}

	public void run(){
		while (true) {
			try {        
				mensagemRec = istream.readUTF();
				if(mensagemRec.contains("msg:>")&&mensagemRec.indexOf(">")==4) {
					win.setMensagemEnviada(mensagemRec.replaceFirst("msg:>", ""));
					//win.atualizaInterface();
				}
				else if(mensagemRec.contains("tab:>")&&mensagemRec.indexOf(">")==4) {
					int[][] tabuleiro = Mancala.toTabuleiro(mensagemRec.replaceFirst("tab:>", ""));
					win.setTabuleiro(tabuleiro);
					win.salvaTabuleiro();
					//win.atualizaInterface();
				}
				else if(mensagemRec.contains("jog:>")&&mensagemRec.indexOf(">")==4) {
					this.jogando = Integer.parseInt(mensagemRec.replaceFirst("jog:>", ""));
					win.turno(jogando);
					salvaJogador();
					//win.atualizaInterface();
				}
				else if(mensagemRec.contains("sur:>")&&mensagemRec.indexOf(">")==4) {
					Mancala.desistir(Integer.parseInt(mensagemRec.replaceFirst("sur:>", "")));
					if(jogador!=Integer.parseInt(mensagemRec.replaceFirst("sur:>", ""))){
						fim = true;
						win.finaliza();
					}
				}
				else if(mensagemRec.contains("fim:>")&&mensagemRec.indexOf(">")==4) {
					fim = true;
					win.finaliza();
				}
				else if(mensagemRec.contains("vol:>")&&mensagemRec.indexOf(">")==4) {
					win.voltaJogada();
					voltaJogada();
					win.turno(jogando);
					//win.atualizaInterface();
				}
				win.atualizaInterface();
			} catch(Exception e) {}
		}
	}
	
	private void salvaJogador() {
		if(historico.size()>=5) {
			historico.remove(0);
		}
		historico.add(jogando);
	}
	
	private void voltaJogada() {
		if(historico.isEmpty()) {
			win.setMensagemEnviada("Não é possivel mais voltar jogadas");
		}
		else {
			jogando = historico.get(historico.size()-1);
			historico.remove(historico.size()-1);
		}
	}
	
	private void createRunnable() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					win.getJFrame().setVisible(true);
					win.varreBotao();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
