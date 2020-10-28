import core.Jogador;

public class Principal {

	public static void main(String[] args) {
		
		int[][] tabuleiro ={{0,4,4,4,4,4,4},
				{4,4,4,4,4,4,0}}; //0 a 6
		
		new Jogador(tabuleiro);

	}
}