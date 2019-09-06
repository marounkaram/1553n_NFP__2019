package whatsappserver;

/**
 *
 * @author Maroun
 */

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WhatsappChannel extends Thread {
    
    protected Client client;
    private  PrintWriter out;
    private  BufferedReader in;
    
    private String  clientsInformation = "" ;
     
      
    WhatsappChannel(Socket clientSocket) throws IOException  {
        
        this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.client = new Client(clientSocket,in.readLine());
        WhatsappServer.notifyAllClients("[SERVER NOTIFICATION] New client has been connected. <Name: " + this.client.getNickName() + ">");
        System.out.println("[CHANNEL NOTIFICATION] New connection accepted from [ "+this.client.getNickName() + " ]");
    }
 
 
    @Override
    public void run() {
        String message;
        
        OUTER:
        while (true) {
            
            try {
                message = in.readLine();
                System.out.println(message);
                if (null == message) {
                    this.stopClientConnection();
                    WhatsappServer.removeClient(this);
                    break;
                } else {
                    switch (message) {
                        case "_quit":
                            this.quitClient();
                        case "_who":
                            this.sendToConnectedClients();
                            break;
                        default:
                            WhatsappServer.sendMessageToAllConnectedClients(message, this.client);
                            break;
                    }
                }
            }catch (IOException e) {
                try {
                    this.stopClientConnection();
                } catch (IOException ex) {
                    Logger.getLogger(WhatsappChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Logger.getLogger(WhatsappChannel.class.getName()).log(Level.SEVERE, "Connection lost with {0}", this.client.getNickName());
                
                WhatsappServer.removeClient(this);
                WhatsappServer.notifyAllClients("[SERVER NOTIFICATION] Connection lost with " + this.client.getNickName());
                 
                break;
            }
        }
    }

       
    private void sendToConnectedClients() {
        
        clientsInformation = "Connected clients: \n";
                
        WhatsappServer.whatsappChannels.forEach(whatsappChannel -> {
            clientsInformation   += "\t- ";
            clientsInformation  +=  whatsappChannel.client.getNickName();
            clientsInformation  += " ( Address: " + whatsappChannel.client.getSocket().getInetAddress() + ":" + whatsappChannel.client.getSocket().getPort() + " )\n";
        });
        System.out.println(this.client.nickname);
        this.out.println(clientsInformation);
    }

    private void quitClient() {
        this.out.println("Bye");
        try {
            this.stopClientConnection();
            WhatsappServer.removeClient(this);
        } catch (IOException e) {
        }
        
        WhatsappServer.notifyAllClients("[SERVER NOTIFICATION] " + this.client.getNickName() + " has quit the channel.");
    }

    private void stopClientConnection() throws IOException {
        this.in.close();
        this.out.close();
        this.client.getSocket().close();
    }

    void sendMessage(String message, Client client) {
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        
        this.out.printf("%s :: < %s > %s", currentDate , client.getNickName(), message);
        this.out.println();
    }

    void notifyClient(String message) {
        this.out.println(message);
    }

    public void deleteClient(String reason) throws IOException {
        this.out.println("_kill");
    
        this.stopClientConnection();
        System.out.println("[SERVER NOTIFICATION] "+reason+"."); 
    }
}
