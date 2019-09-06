package whatsappserver;

import java.net.Socket;

/**
 *
 * @author Maroun
 */
    public class Client {
        protected Socket socket;
        protected String nickname;

        public Client (Socket socket ,String nickname) {
            this.socket = socket;
            this.nickname = nickname;
        }

        public Socket getSocket() {
            return this.socket;
        }

        public String getNickName() {
            return this.nickname;
        }
    }