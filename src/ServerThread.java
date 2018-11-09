import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread implements Runnable{
	private ServerSocket servsock;
	ServerThread(int port) throws IOException{
		servsock= new ServerSocket(port);
		servsock.setSoTimeout(30000);
	}
	@Override
	public void run() {
		while(true) {
			try{
			System.out.println("waiting for connection on " + servsock.getLocalPort());
			Socket server= servsock.accept();
			System.out.println("connect to "+server.getRemoteSocketAddress());
			DataInputStream in= new DataInputStream(server.getInputStream());
			System.out.println(in.readUTF());
			DataOutputStream out= new DataOutputStream(server.getOutputStream());
			out.writeUTF("Thank you for connecting to "	+ server.getLocalSocketAddress() + "\nGoodbye!");
			server.close();
			} catch (Exception e) {
				System.out.println(e.toString());
				try {
					servsock.close(); System.out.println("Bye bye");
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			} 
		}
	}
}
