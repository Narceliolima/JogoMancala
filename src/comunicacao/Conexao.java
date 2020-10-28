package comunicacao;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexao {

	private ServerSocket serverSocket = null;
	private DataOutputStream ostream = null;
	private DataInputStream istream = null;
	private String host = "localhost";
	private int port = 9090;
	private Socket socket = null;
	
	public boolean conectaServidor() {
		try {
			socket = new Socket(host, port);
			criaFluxos();
		} catch(IOException e)	{
			return false;
		}
		return true;
	}
	
	public boolean criaServidor() {
		try {
			serverSocket = new ServerSocket(port);;
			socket = serverSocket.accept();
			criaFluxos();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void enviaDado(String mensagemEnv) {
		try {
			ostream.writeUTF(mensagemEnv);
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String recebeDado() {
		try {
			return istream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "erro";
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	private void criaFluxos() {
		try {
			ostream = new DataOutputStream(socket.getOutputStream());
			istream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
