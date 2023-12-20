import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    private int x, y;
    private double speed;
    private double originalSpeed;
    private boolean leftPressed, rightPressed, upPressed, downPressed;
    public static final int PLAYER_SIZE = 20; // 플레이어 크기를 조절
    private Image playerImage;
    private Image glowImage; // Glow 이미지 추가
    private long speedIncreaseStartTime;
    private long speedIncreaseDuration;
    private boolean barrierEnabled;
    private long barrierStartTime;
    private long barrierDuration;
    public Player(int xpos, int ypos) {
        this.originalSpeed = 3.0;
        this.speed = originalSpeed;
        this.speedIncreaseStartTime = 0;
        this.speedIncreaseDuration = 0;
        this.barrierEnabled = false;
        this.barrierStartTime = 0;
        this.barrierDuration = 0;

        // 플레이어 이미지 및 Glow 이미지 로드
        playerImage = new ImageIcon("src/image/player.png").getImage();
        glowImage = new ImageIcon("src/image/glow.png").getImage().getScaledInstance((int) (PLAYER_SIZE * 1.2), (int) (PLAYER_SIZE * 1.2), Image.SCALE_DEFAULT);
        // 플레이어 위치를 화면 중앙으로 설정
        this.x = xpos - PLAYER_SIZE;
        this.y = ypos - PLAYER_SIZE;

        // 더 부드러운 이동을 위해 KeyAdapter 추가
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        handleKeyEvent(e);
                        return false;
                    }
                });
        // 게임 루프 시작
        startGameLoop();
    }
    private void startGameLoop() {
        new Thread(() -> {
            while (true) {
                update();
                // 더 부드러운 이동을 위해 필요한 만큼 sleep 지연시간 조절
                try { Thread.sleep(10); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }
    public void update() {
        if (leftPressed && x > 0) { x -= speed; }
        if (rightPressed && x < 1080 - PLAYER_SIZE) { x += speed; }
        if (upPressed && y > PLAYER_SIZE) { y -= speed; }
        if (downPressed && y < 800 - PLAYER_SIZE) { y += speed; }
        // 속도 증가 시간이 지났는지 확인
        long currentTime = System.currentTimeMillis();
        if (speedIncreaseStartTime > 0 && currentTime - speedIncreaseStartTime >= speedIncreaseDuration) {
            // 속도를 원래 값으로 재설정
            speedIncreaseStartTime = 0;
            speedIncreaseDuration = 0;
            speed = originalSpeed;
        }
        // 장벽 지속시간이 지났는지 확인
        if (barrierEnabled && currentTime - barrierStartTime >= barrierDuration) {
            // 장벽 효과 비활성화
            barrierEnabled = false;
        }
    }
    public void draw(Graphics g) {
        // 플레이어 이미지 그리기
        g.drawImage(playerImage, x, y, PLAYER_SIZE, PLAYER_SIZE, null);
        // Glow 이미지 그리기 (베리어 효과 중일 때만)
        if (barrierEnabled) {
            g.drawImage(glowImage, x - (int) (PLAYER_SIZE * 0.1), y - (int) (PLAYER_SIZE * 0.1),
                    PLAYER_SIZE + (int) (PLAYER_SIZE * 0.2), PLAYER_SIZE + (int) (PLAYER_SIZE * 0.2), null);
        }
    }
    // 방향키 이동 설정하는 키보드 입력
    public void handleKeyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean pressed = e.getID() == KeyEvent.KEY_PRESSED;
        if (keyCode == KeyEvent.VK_LEFT) { leftPressed = pressed; }
        if (keyCode == KeyEvent.VK_RIGHT) { rightPressed = pressed; }
        if (keyCode == KeyEvent.VK_UP) { upPressed = pressed; }
        if (keyCode == KeyEvent.VK_DOWN) { downPressed = pressed; }
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
    }
    public void increaseSpeed(double factor, int duration) {
        // 플레이어의 속도를 높입니다.
        speed *= factor;
        // 지정된 기간 후에 속도가 재설정되도록 예약합니다.
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() { // 지속 시간이 지나면 속도를 원래 값으로 재설정합니다.
                        speed = originalSpeed;
                    }
                }, duration );
    }
    public void enableBarrier(int duration) {
        // 장벽 효과 활성화
        barrierEnabled = true;
        // 지정된 기간 후에 장벽 효과가 비활성화되도록 예약합니다.
        barrierStartTime = System.currentTimeMillis();
        barrierDuration = duration;
    }
    public boolean isImmuneToBullets() {
        return barrierEnabled;
    }
    // 플레이어와 아이템 간의 충돌 확인
    public boolean intersects(Item item) {
        Rectangle playerBounds = getBounds();
        Rectangle itemBounds = item.getBounds();
        return playerBounds.intersects(itemBounds);
    }
    public void stopPlayer() {
    	this.speed = 0;
    }
}