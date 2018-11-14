import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{

	public boolean server = true;
	public static void main(String[] argv) {
		launch(argv);
	}
	public static GridPane gridpane;
	TextArea chat;
	TextArea message;
	Button send;
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox layout = new VBox(1);
		// TODO Auto-generated method stub
		gridpane = new GridPane();
		gridpane.setHgap(10); 
		gridpane.setVgap(10); 
		gridpane.setPadding(new Insets(25, 25, 25, 25));
		final Scene scene = new Scene(layout, 720, 400);
		chat = new TextArea();
		chat.setEditable(false);
		chat.setMinSize(500, 200);
		chat.setMaxSize(700, 350);
		layout.setLayoutX(10);
		layout.setLayoutY(10);
		message = new TextArea();
		message.setEditable(true);
		message.setMinSize(200, 20);
		message.setMaxSize(600, 50);
		send = new Button("Send!");
		gridpane.add(message, 0, 0);
		gridpane.add(send, 1, 0);
		layout.getChildren().addAll(chat,gridpane);
		ClientThread client = new ClientThread("localhost", 4343,chat);
        client.start();
		send.setOnAction(e-> {
			String text = message.getText();
			System.out.println("Send");
            client.sent(text);
            message.setText("");
		});
		 
		primaryStage.setTitle("Client"); 
		primaryStage.setScene(scene); 
		primaryStage.show();
	}
}
class ClientThread extends Thread {

    final String url;
    final int port;
    private Socket client;

    private TextArea TextShowArea;

    public ClientThread(String ip, int port, TextArea tx) {
        this.url = ip;
        this.port = port;
        this.TextShowArea = tx;
        try {
            client = new Socket(url, port);
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        System.out.println("Client created");
    }
    
    public void run()
    {
        readFromServer();
    }
    
    void readFromServer() {
        Thread t2 = null;
        t2 = new Thread(){
            @Override
            public void run() {
               
                while (true) {
                    try {
                        InputStream inFromServer = client.getInputStream();
                        DataInputStream in = new DataInputStream(inFromServer);
                        String str = in.readUTF();
                        System.out.println(str);
                        if(!str.matches("Client No. :(.*)"))
                            TextShowArea.appendText(str+"\n");
                        else {
                        	String[] mes = str.split(":");
                        	TextShowArea.setPromptText("Client No : "+mes[1]);
                        }
                        
                    } catch (IOException ex) {
                    	ex.printStackTrace();
                    }
                }
            }
        };
        t2.start();
    }
    public void sent(String str)
    {
        try {
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.flush();
            out.writeUTF(str);   
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }
}