import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    private String userName;
    private JFrame frame;
    private JButton startGameButton;
    private boolean inGame = false;
    private boolean gameStarted = false;
    private Image mainMenuImg = new ImageIcon("src/image/StartMenu.png").getImage();
    private Image gameInfoImg = new ImageIcon("src/image/GameInfo.png").getImage();
    public Client() {
        // 사용자 이름 입력 받기. 만약 입력하지 않으면 메인 화면에 나가지 않음
        do { this.userName = JOptionPane.showInputDialog("사용할 닉네임을 입력하세요:");
            if (this.userName == null) { System.exit(0); }
        } while (this.userName.trim().isEmpty());

        // 메인 프레임 생성
        frame = new JFrame("Dodge Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 800);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel mainMenuPanel = createMainMenuPanel();
        frame.add(mainMenuPanel, BorderLayout.CENTER);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout());
        frame.add(subPanel, BorderLayout.EAST);

        // 채팅창 기능 구현
        chatArea = new JTextArea(20, 20);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        subPanel.add(scrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        JButton sendButton = new JButton("전송");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        subPanel.add(inputPanel, BorderLayout.SOUTH);

        // Enter 키를 처리하기 위해 messageField에 ActionListener 추가
        messageField.addActionListener(e -> sendMessage());

        frame.setVisible(true);
        connectToServer();
    }
    // 메인 화면에 패널을 만들고 버튼 설정
    private JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new MainMenuPanel();
        mainMenuPanel.setLayout(null);

        // 게임 시작 버튼 로직
        startGameButton = new JButton("게임 시작");
        startGameButton.setBounds(700, 300, 120, 50);
        startGameButton.addActionListener(e -> startGameButtonClicked(startGameButton));
        mainMenuPanel.add(startGameButton);

        // 게임 방법 버튼 로직
        JButton gameInfoButton = new JButton("게임 방법");
        gameInfoButton.setBounds(700, 400, 120, 50);
        gameInfoButton.addActionListener(e -> gameInfoButtonClicked(mainMenuPanel));
        mainMenuPanel.add(gameInfoButton);

        // 게임 종료 버튼 로직
        JButton exitButton = new JButton("게임 종료");
        exitButton.setBounds(700, 500, 120, 50);
        exitButton.addActionListener(e -> System.exit(0));
        mainMenuPanel.add(exitButton);

        return mainMenuPanel;
    }
    // 양쪽 플레이어가 준비 완료가 될 때 게임 화면 전환 로직
    private void startGameButtonClicked(JButton startGameButton) {
        if (!inGame && !gameStarted) {
            out.println(userName + "님이 준비가 완료 되었습니다 !");
            chatArea.append("게임을 시작하려면 상대방을 기다려주세요...\n");
            startGameButton.setEnabled(false);
            // 서버에게 이 클라이언트가 게임을 시작할 준비가 되었음을 알림
            out.println("GAME_START_REQUESTED");
        }
    }
    // 게임 방법 창
    public void gameInfoButtonClicked(JPanel mainMenuPanel) {
        chatArea.append("게임 방법을 표시합니다.\n");
        mainMenuPanel.setVisible(false);
        JPanel gameInfoPanel = new GameInfoPanel();
        gameInfoPanel.setLayout(null);

        JButton backButton = new JButton("뒤로 가기");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenuPanel.setVisible(true);
                gameInfoPanel.setVisible(false); // 창 안보이게 하기
            }
        });
        backButton.setBounds(700, 700, 120, 50);
        gameInfoPanel.add(backButton);
        frame.add(gameInfoPanel, BorderLayout.CENTER);
        out.println("님이 게임 방법을 보고있습니다.");
    }
    // JPanel을 상속받은 내부 클래스 선언
    private class MainMenuPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(mainMenuImg, 0, 0, 900, 800, null);
        }
    }
    // JPanel을 상속받은 내부 클래스 선언
    private class GameInfoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(mainMenuImg, 0, 0, 900, 800, null);
            g.drawImage(gameInfoImg, 100, 30, 650, 650, null);
        }
    }
    // 서버 연결 여부 확인
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5050);
            chatArea.append("서버에 연결되었습니다.\n");
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            out.println(userName);
            new Thread(() -> {
                while (true) {
                    if (in.hasNextLine()) {
                        String message = in.nextLine();
                        handleServerMessage(message);
                    }
                }
            }).start();
        } catch (IOException e) { e.printStackTrace(); }
    }
    // 서버에 메세지를 보내서 상대 클라이언트와 채팅을 사용
    private void handleServerMessage(String message) {
        System.out.println("서버에서 메시지 받음: " + message);
        if (message.startsWith("게임 준비 완료")) {
            // 두 클라이언트가 모두 시작 버튼을 눌렀으므로 게임 화면을 표시
            gameStarted = true;
            inGame = true;
            showGameScreen();
        } else if (message.startsWith("GAME_RESULT:")) {
            // 게임 결과 메시지 추출
            String resultMessage = message.substring("GAME_RESULT:".length());
            // 게임 결과를 포함한 팝업 다이얼로그 표시
            SwingUtilities.invokeLater(() -> { JOptionPane.showMessageDialog(frame, resultMessage, "게임 결과", JOptionPane.INFORMATION_MESSAGE); });
            // 클라이언트 측의 게임 관련 상태 재설정
            inGame = false;
            gameStarted = false;
            startGameButton.setEnabled(true);
                // 다른 메시지는 기존과 같이 처리
        } else { chatArea.append(message + "\n"); }
    }
    // 게임 화면 전환 로직
    private void showGameScreen() {
        SwingUtilities.invokeLater(() -> {
            inGame = false;
            gameStarted = false;
            startGameButton.setEnabled(true);
            // 게임 화면 생성
            GameScreen gameScreen = new GameScreen(frame, out);
            // 게임 시작
            GameController gameController = new GameController(gameScreen);
            gameController.startGame();
            // 메인 메뉴 프레임 숨기기
            frame.setVisible(false);
        });
    }
    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        chatArea.append("나: " + message + "\n");
        messageField.setText("");
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(Client::new); }
}
