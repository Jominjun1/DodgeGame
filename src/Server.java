import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Server {
    private List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) {
        new Server().startServer();
    }
    private void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(5050);
            System.out.println("서버가 시작되었습니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새로운 클라이언트가 연결되었습니다.");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void broadcastMessage(String message, ClientHandler sender) {
        if (message.equals("GAME_START_REQUESTED")) {
            handleGameStartRequest(sender);
        } else {
            for (ClientHandler client : clients) {
                if (client != sender) { client.sendMessage(sender.getUserName() + ": " + message); }
            }
        }
    }

    private void handleGameStartRequest(ClientHandler sender) {
        sender.setInGame(true);
        if (allClientsReady()) { sendGameStartMessage(); }
    }
    // 클라이언트 전부가 게임시작 버튼을 누르면 게임 화면 전환 로직
    private boolean allClientsReady() {
        for (ClientHandler client : clients) {
            if (!client.isInGame()) { return false; }
        }
        return true;
    }
    
    private void sendGameStartMessage() {
        for (ClientHandler client : clients) {
            client.sendMessage("GAME_START_REQUESTED_BY_BOTH");
        }
    }
    // 클라이언트 상태 변경시 전달 받는 로직
    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
        private String userName;
        private boolean inGame;
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());
                // 클라이언트가 접속하면 서버에게 유저 이름을 전송받음
                this.userName = in.nextLine();
            } catch (IOException e) { e.printStackTrace(); }
        }
        public String getUserName() {
            return userName;
        }
        public boolean isInGame() {
            return inGame;
        }
        public void setInGame(boolean inGame) {
            this.inGame = inGame;
        }
        @Override
        public void run() {
            try {
                while (true) {
                    // 메세지 전송
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        broadcastMessage(message, this);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
            finally {
                try {
                    socket.close();
                    clients.remove(this);
                    System.out.println("클라이언트가 종료되었습니다.");
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
