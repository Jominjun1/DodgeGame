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
    // 서버 시작 메소드
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
    // 클라이언트에게 메시지 브로드캐스팅
    private void broadcastMessage(String message, ClientHandler sender) {
        if (message.startsWith("GAME_START_REQUEST")) { handleGameStartRequest(sender); }
        else if (message.startsWith("GAME_OVER_RESULT")) { handleGameOverResult(message, sender); }
        else { for (ClientHandler client : clients) { if (client != sender) { client.sendMessage(sender.getUserName() + ": " + message); } } }
    }
    // 게임 시작 요청 처리
    private void handleGameStartRequest(ClientHandler sender) {
        sender.setInGame(true);
        if (allClientsReady()) { sendGameStartMessage(); }
    }
    // 모든 클라이언트가 게임 준비 완료인지 확인
    private boolean allClientsReady() {
        for (ClientHandler client : clients) {
            if (!client.isInGame()) { return false; }
        }
        return true;
    }
    // 게임 시작 메시지 전송
    private void sendGameStartMessage() { for (ClientHandler client : clients) { client.sendMessage("게임 준비 완료"); } }
    // 클라이언트의 상태 변경 메시지 처리
    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private Scanner in;
        private String userName;
        private boolean inGame;
        public float getSurvivingTime() { return survivingTime; }
        public void setSurvivingTime(float survivingTime) { this.survivingTime = survivingTime; }
        private float survivingTime = -1;
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new Scanner(socket.getInputStream());
                this.userName = in.nextLine();
            } catch (IOException e) { e.printStackTrace(); }
        }
        public String getUserName() { return userName; }
        public boolean isInGame() { return inGame; }
        public void setInGame(boolean inGame) { this.inGame = inGame; }
        @Override
        public void run() {
            try {
                while (true) {
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
        public void sendMessage(String message) { out.println(message); }
    }
    // 게임 오버 결과 메시지 처리
    private void handleGameOverResult(String message, ClientHandler sender) {
        float survivingTime = Float.parseFloat(message.substring("GAME_OVER_RESULT:".length()));
        sender.setSurvivingTime(survivingTime);
        if (allClientsSentGameOverResult()) { determineWinner(); }
    }
    // 모든 클라이언트가 게임 오버 결과를 전송했는지 확인
    private boolean allClientsSentGameOverResult() {
        for (ClientHandler client : clients) {
            if (client.getSurvivingTime() == -1) { return false; }
        }
        return true;
    }
    // 승자 결정 및 결과 메시지 전송
    private void determineWinner() {
        float maxSurvivingTime = -1;
        float minSurvivingTime = -1;
        String winner = "";
        // 낮은 시간과 높은 시간을 찾아 주는 알고리즘
        for (ClientHandler client : clients) {
            float survivingTime = client.getSurvivingTime();
            if (survivingTime > maxSurvivingTime) {
                minSurvivingTime = maxSurvivingTime;
                maxSurvivingTime = survivingTime;
                winner = client.getUserName();
            } else if (survivingTime > minSurvivingTime || minSurvivingTime == -1) { minSurvivingTime = survivingTime; }
        }
        for (ClientHandler client : clients) {
            String resultMessage;
            if (client.getSurvivingTime() == maxSurvivingTime) {
                resultMessage = "당신이 이겼습니다 !! " + maxSurvivingTime / 50 + "초 생존하셨습니다.";
            } else { resultMessage = "당신이 졌습니다.. " + minSurvivingTime / 50 + "초 생존하셨습니다."; }
            client.sendMessage("GAME_RESULT:" + resultMessage);
        }
        for (ClientHandler client : clients) {
            client.setSurvivingTime(-1);
            client.setInGame(false);
        }
    }
}
