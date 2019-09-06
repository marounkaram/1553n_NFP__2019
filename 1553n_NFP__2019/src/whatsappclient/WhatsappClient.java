package whatsappclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Maroun
 */

public class WhatsappClient extends Thread {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
     String message;
     
	public static void main(String args[]) {
	    
            String host = "127.0.0.1";
	    int port = 4242;
            
            WhatsappClient whatsappClient = new WhatsappClient();
            whatsappClient.start();

	}
        
        private WhatsappClient() {
	}

        @Override
	public void run() {
            
        try {
            Scanner clientScanner = new Scanner(System.in);
            String input = clientScanner.nextLine();
            String[] params = input.split(" ");
            clientSocket = new Socket("127.0.0.1", 4242);
            
            
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()), true);
            
            while(this.clientSocket.isConnected()) {
                try {
                    if (input.startsWith("_connect ")) {
                         
                            out.println(params[1]);
                            System.out.println("[INFO] Connected successfully");
                         
                        
                    } else if (input.startsWith("_who")){
                        out.println("_who");
                    }
                    
                    String resp = in.readLine();
                    
                    if (resp!=null && resp.startsWith("_kill ")){
                        if(params[1]!=null)
                        System.exit(0);
                    }
                    
                    //System.out.println(resp+"rfger");
                    
                } catch (IOException ex) {
                    Logger.getLogger(WhatsappClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(WhatsappClient.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
}