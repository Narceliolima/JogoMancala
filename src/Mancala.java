import javax.swing.JOptionPane;

public class Mancala {
	
	private static final int jogador1 = 0;
	private static final int jogador2 = 1;
	
	public static void imprimeMatriz(int[][] matriz) {
		int i,j;
		for(i=0;i<matriz.length;i++) {
			for(j=0;j<matriz[i].length;j++) {
				System.out.print(matriz[i][j]);
			}
			System.out.println();
		}
	}
	
	public static String toIstring(int[][] tabuleiro) {
		
		String string = "";
		
		for(int i=0;i<tabuleiro.length;i++) {
			for(int j=0;j<tabuleiro[i].length;j++) {
				string += tabuleiro[i][j]+"-";
			}
		}
		
		return string;
	}
	
	public static int[][] toTabuleiro(String string) {
		
		int[][] tabuleiro = new int[2][7];
		int n = 0;
		
		for(int i=0;i<tabuleiro.length;i++) {
			for(int j=0;j<tabuleiro[i].length;j++) {
				if(string.charAt(n+1)=='-') {
					tabuleiro[i][j] = Integer.parseInt(""+string.charAt(n));
					n+=2;
				}
				else if(string.charAt(n+1)!='-') {
					tabuleiro[i][j] = Integer.parseInt(""+string.charAt(n)+string.charAt(n+1));
					n+=3;
				}
			}
		}
		
		return tabuleiro;
	}
	
	public static String configuraHost() {
		
		String host;
		
		host = JOptionPane.showInputDialog("Qual o ip do servidor? (Padrão localhost)");
		if(host==null||host=="") {
			return "localhost";
		}
		else {
			return host;
		}
	}
	
	public static int configuraPorta() {
		
		String porta;
		
		porta = JOptionPane.showInputDialog("Qual a porta do servidor? (Padrão 9090)");
		if(porta==null||porta=="") {
			return 9090;
		}
		else {
			return Integer.parseInt(porta);
		}
	}
	
	public static void desistir(int jogador) {
		JOptionPane.showMessageDialog(null, "Jogador "+(jogador+1)+" desistiu");
	}
	
	public static int confirmaDesistencia() {
		return JOptionPane.showConfirmDialog(null, "Tem certeza?","Desistencia",JOptionPane.YES_NO_OPTION);
	}
	
	public static int confirmaJogarNovamente() {
		return JOptionPane.showConfirmDialog(null, "Deseja jogar novamente?","Revanche",JOptionPane.YES_NO_OPTION);
	}
	
	public static void vitoria() {
		JOptionPane.showMessageDialog(null, "Você venceu");
	}
	
	public static void derrota() {
		JOptionPane.showMessageDialog(null, "Você perdeu");
	}
	
	public static void empate() {
		JOptionPane.showMessageDialog(null, "Empate!");
	}
	
	public static int jogar(int jogador, int posicao, int[][] tabuleiro) {
		
		int j;
		int sementes;
		int ultimaPosicao = 0;
		boolean inverter;
		boolean ultimoJogador = false;
		
		sementes = tabuleiro[jogador][posicao];
		tabuleiro[jogador][posicao] = 0;
		if(jogador == 0) {
			j = posicao - 1;
			inverter = true; ///////
		}
		else {
			j = posicao + 1;
			inverter = false; ///////
		}
		
		while(sementes>0) {
			while(j>=0&&sementes>0&&inverter) {
				if(j>6) {
					j--;
				}
				if(jogador==jogador2&&j==0) {
					j--;
				}
				else {
					tabuleiro[jogador1][j]++;
					sementes--;
					j--;
				}
			}
			while(j<=6&&sementes>0) {
				inverter = false;
				if(j<0) {
					j++;
				}
				if(jogador==jogador1&&j==6) {
					j++;
				}
				else {
					tabuleiro[jogador2][j]++;
					sementes--;
					j++;
				}
			}
			////////////////////
			if(inverter) {
				ultimaPosicao = j+1;
			}
			else {// ----**---- ////
				ultimaPosicao = j-1;
			}
			ultimoJogador = inverter;
			inverter = true;
			/////////////////////////
		}		
		
		return aplicaRegra(ultimaPosicao,ultimoJogador,tabuleiro,jogador);
		
	}
	
	public static int aplicaRegra(int ultimaPosicao, boolean ultimoJogador, int[][] tabuleiro,int jogador) {
		
		if(ultimaPosicao==0&&ultimoJogador) { //ultimoJogador == true == jogador1
			//Jogador 1 joga novamente
			return 1;
		}
		else if(ultimaPosicao==6&&!ultimoJogador) { //ultimoJogador == false == jogador2
			//Jogador 2 joga novamente
			return 2;
		}
		else if(tabuleiro[jogador1][ultimaPosicao]-1==0&&ultimoJogador&&ultimaPosicao!=0&&tabuleiro[jogador2][ultimaPosicao-1]>0&&jogador==jogador1) {
			tabuleiro[jogador1][0] += tabuleiro[jogador2][ultimaPosicao-1] + 1;
			tabuleiro[jogador1][ultimaPosicao] = 0;
			tabuleiro[jogador2][ultimaPosicao-1] = 0;
			
		}
		else if(tabuleiro[jogador2][ultimaPosicao]-1==0&&!ultimoJogador&&ultimaPosicao!=6&&tabuleiro[jogador1][ultimaPosicao+1]>0&&jogador==jogador2) {
			tabuleiro[jogador2][6] += tabuleiro[jogador1][ultimaPosicao+1] + 1;
			tabuleiro[jogador2][ultimaPosicao] = 0;
			tabuleiro[jogador1][ultimaPosicao+1] = 0;
		}
		
		return 0;
	}
	
	public static int verificaVazios(int[][] tabuleiro) {
		
		int contagemSementes;
		int resultado;
		int linha, coluna;		
		
		for(linha=0;linha<tabuleiro.length;linha++) {
			contagemSementes = 0;
			for(coluna=0;coluna<tabuleiro[linha].length;coluna++) {
				if(linha==jogador1&&coluna!=0||linha==jogador2&&coluna!=6) {
					contagemSementes += tabuleiro[linha][coluna];
				}
			}
			if(contagemSementes==0) {
				if(linha==jogador1) {
					resultado = tabuleiro[jogador2][6];
					tabuleiro[jogador2][6] = 0;
					tabuleiro [jogador2][6] = esvaziaResto(tabuleiro[jogador2], resultado);
				}
				else {
					resultado = tabuleiro[jogador1][0];
					tabuleiro[jogador1][0] = 0;
					tabuleiro [jogador1][0] = esvaziaResto(tabuleiro[jogador1], resultado);
				}
				if(tabuleiro[jogador1][0]>tabuleiro[jogador2][6]) {
					JOptionPane.showMessageDialog(null, "Jogador 1 venceu");
					return 0;
				}
				else if(tabuleiro[jogador1][0]<tabuleiro[jogador2][6]){
					JOptionPane.showMessageDialog(null, "Jogador 2 venceu");
					return 1;
				}
				else if(tabuleiro[jogador1][0]==tabuleiro[jogador2][6]) {
					return 2;
				}
			}
		}
		return -1;
	}
	
	public static int esvaziaResto(int[] vetor, int resultado) {
		for(int i=0;i<vetor.length;i++) {
			resultado += vetor[i];
			vetor[i] = 0;
		}
		return resultado;
	}
}
