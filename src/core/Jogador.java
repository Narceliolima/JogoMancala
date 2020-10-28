package core;
import java.awt.EventQueue;
import java.util.ArrayList;
import comunicacao.Conexao;
import grafica.GUI;
import grafica.Notificacao;

public class Jogador extends Thread{
	
	private String mensagemEnv = "";
	private String mensagemRec = "";
	private Conexao conexao;
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
		createRunnable();
		historico = new ArrayList<Integer>();
		conexao = new Conexao();
		
		conexao.setHost(Notificacao.configuraHost());
		conexao.setPort(Notificacao.configuraPorta());
		if(!conexao.conectaServidor()) {
			win.setMensagemEnviada("Host não encontrado.");
			win.setMensagemEnviada("Criando servidor...");
			win.setMensagemEnviada("Aguardando conexão...");
			if(conexao.criaServidor()) {
				win.setMensagemEnviada("Conexão Estabelecida.");
				this.jogador = 0;
			}
			else{
				win.setMensagemEnviada("Ocorreu um erro, reinicie seu jogo");
			}
		}
		
		
		try {
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
						conexao.enviaDado(mensagemEnv.substring(0, 5)+"Jogador "+(jogador+1)+": "+mensagemEnv.substring(5));
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
						conexao.enviaDado("tab:>"+Conversor.toIstring(tabuleiro));
						conexao.enviaDado("jog:>"+jogando);
						if(fim) {
							conexao.enviaDado("fim:>");
						}
					}
					else if(mensagemEnv.contains("vol:>")&&mensagemEnv.indexOf(">")==4&&this.jogador==jogando) {
						win.voltaJogada();
						voltaJogada();
						conexao.enviaDado(mensagemEnv);
					}
					else if(mensagemEnv.contains("sai:>")&&mensagemEnv.indexOf(">")==4) {
						conexao.enviaDado(mensagemEnv+this.jogador);
					}
					win.turno(jogando);
					win.atualizaInterface();
					if(!fim) {
						mensagemEnv = win.getMensagem();
					}
					if(mensagemEnv=="sur:>"&&Notificacao.confirmaDesistencia()==1) {
						mensagemEnv = "";
					}
				}
				if(mensagemEnv=="sur:>") {
					conexao.enviaDado(mensagemEnv+jogador);
					if(jogador==jogador1) {
						conexao.enviaDado("jog:>"+jogador2);
					}
					else {
						conexao.enviaDado("jog:>"+jogador1);
					}
					Notificacao.derrota();
				}
				else if(jogando==jogador) {
					Notificacao.vitoria();
				}
				else if(jogando==2) {
					Notificacao.empate();
				}
				else {
					Notificacao.derrota();
				}
				
				novamente = Notificacao.confirmaJogarNovamente();
				
				if(novamente == 0) {
					int[][] newTabuleiro ={{0,4,4,4,4,4,4},
										   {4,4,4,4,4,4,0}}; //0 a 6
					win.setTabuleiro(newTabuleiro);
					this.jogando = 0;
					fim = false;
				}
			}
			conexao.enviaDado("sai:>"+this.jogador);
			System.exit(0);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
/*	
	public static void main(String[] args) {
		
		int[][] tabuleiro ={{0,4,4,4,4,4,4},
							{4,4,4,4,4,4,0}}; //0 a 6

		new Jogador(tabuleiro);

	}
*/
	public void run(){
		while (mensagemRec!="erro") {
			try {
				mensagemRec = conexao.recebeDado();
				if(mensagemRec.contains("msg:>")&&mensagemRec.indexOf(">")==4) {
					win.setMensagemEnviada(mensagemRec.replaceFirst("msg:>", ""));
				}
				else if(mensagemRec.contains("tab:>")&&mensagemRec.indexOf(">")==4) {
					int[][] tabuleiro = Conversor.toTabuleiro(mensagemRec.replaceFirst("tab:>", ""));
					win.setTabuleiro(tabuleiro);
					win.salvaTabuleiro();
				}
				else if(mensagemRec.contains("jog:>")&&mensagemRec.indexOf(">")==4) {
					this.jogando = Integer.parseInt(mensagemRec.replaceFirst("jog:>", ""));
					win.turno(jogando);
					salvaJogador();
				}
				else if(mensagemRec.contains("sur:>")&&mensagemRec.indexOf(">")==4) {
					Notificacao.desistir(Integer.parseInt(mensagemRec.replaceFirst("sur:>", "")));
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
				}
				else if(mensagemRec.contains("sai:>")&&mensagemRec.indexOf(">")==4) {
					Notificacao.saida(Integer.parseInt(mensagemRec.replaceFirst("sai:>", "")));
					if(jogador!=Integer.parseInt(mensagemRec.replaceFirst("sai:>", ""))){
						fim = true;
						this.jogando = this.jogador;
						win.finaliza();
					}
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
