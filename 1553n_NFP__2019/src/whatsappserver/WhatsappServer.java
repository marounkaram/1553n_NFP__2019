package whatsappserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maroun
 */
public class WhatsappServer extends Thread{

        public static final int PORT_NUMBER = 4242;
        public static List<WhatsappChannel> whatsappChannels =  new ArrayList<>();;
        
	protected Socket socket;

	private WhatsappServer(Socket socket) {
		this.socket = socket;
                System.out.println("A new client has been connected from " + socket.getInetAddress().getHostAddress());
	}

        private WhatsappServer() {}
        
        @Override
	public void run() {
            
            Scanner serverScanner = new Scanner(System.in);
            
            while(true) {
                String input = serverScanner.nextLine();
                if (input.startsWith("_kill ")) {
                    try {
                        killClient(input.split(" ")[1]);
                    } catch (IOException ex) {
                        Logger.getLogger(WhatsappServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (input.equals("_shutdown")) {
                    try {
                        shutdownWhatsappServer();
                    } catch (IOException ex) {
                        Logger.getLogger(WhatsappServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (input.equals("_check")) {
                    System.out.println(whatsappChannels);
                }
                
            }
	}

     
    public static void notifyAllClients (String message) {
        whatsappChannels.forEach(clientChannel -> clientChannel.notifyClient(message));
    }    
    
    static void removeClient(WhatsappChannel clientChannel) {
        whatsappChannels.remove(clientChannel);
    }
    
    static void sendMessageToAllConnectedClients (String message, Client client) {
        whatsappChannels.forEach(clientChannel -> clientChannel.sendMessage(message, client));
    }
    
    void shutdownWhatsappServer() throws IOException {
        while (whatsappChannels.size() > 0) {
            whatsappChannels.get(0).deleteClient("Your connection has closed due to server shutdown");
        }
        out.println("Shutdown server ...");
        System.exit(0);
    }
     
    void killClient(String nickname) throws IOException {
        WhatsappChannel deletedClientChannel = null;
        
        for (WhatsappChannel whatsappChannel : whatsappChannels) {
            if (whatsappChannel.client.getNickName().equals(nickname)) {
                deletedClientChannel = whatsappChannel;
                whatsappChannel.deleteClient("You have been deleted");
            }
        }
        
        if(deletedClientChannel!=null)
        notifyAllClients("[SERVER NOTIFICATION] " + deletedClientChannel.client.getNickName() + " has been Deleted.");
    }
     
    public static void main(String[] args) {
            
            System.out.println("Starting server ...");
            ServerSocket server = null;
            
            WhatsappServer selfWhatsappSocket = new WhatsappServer();
            selfWhatsappSocket.start();
            
            try {
                server = new ServerSocket(PORT_NUMBER);
                while (true) {
                    Socket serverSocket = server.accept();
                    WhatsappChannel clientChannel = new WhatsappChannel(serverSocket);
                    whatsappChannels.add(clientChannel);
                    clientChannel.start();
                }
	    } catch (IOException ex) {
		System.out.println("We can't start the server.");
	    } finally {
		try {
		    if (server != null)
			server.close();
		    } catch (IOException ex) {
		        System.out.println("We can't close the server.");
                    }
	    }
        
    }
    
   
    
    
}
