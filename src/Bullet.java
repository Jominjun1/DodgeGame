import java.awt.*;
public class Bullet {
    private int x, y;
    private double speed; // Change to double to allow for fractional speed changes
    private double originalSpeed; // Store the original speed
    public static final int BULLET_SIZE = 5;
    private double angle;

    // Constructor
    public Bullet(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.speed = 5.0; // Initial speed
        this.originalSpeed = 5.0; // Store the original speed
        this.angle = angle;
    }
    public void update() {
        // 지정된 방향으로 총알을 이동
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
        // 만약 총알이 화면 가장자리에 닿으면 반대 방향으로 튕겨나가도록 수정
        // 수직. 수평으로 튕기기 + 튕길 때 각도에 랜덤성 추가
        if (x < 0 + 20 || x > 1080 - 20) { angle = Math.PI - angle; angle += Math.random() * Math.PI / 2 - Math.PI / 4; }
        if (y < 0 + 20) { angle = -angle; angle += Math.random() * Math.PI / 2 - Math.PI / 4; }
        if (y > 800 - 20) { angle = -angle; angle += Math.random() * Math.PI / 2 - Math.PI / 4; }
    }
    public void draw(Graphics g) {
        // 색상을 주황색으로 설정
        g.setColor(Color.ORANGE);
        // 총알을 나타내는 채워진 타원을 그림
        g.fillOval(x, y, BULLET_SIZE, BULLET_SIZE);
    }
    public void decreaseSpeed(double factor, int duration) {
        // Decrease the speed by the specified factor
        speed *= factor;

        // Schedule the speed to be reset after the specified duration
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // Reset the speed to its original value after the duration
                        speed = originalSpeed;
                    }
                },
                duration
        );
    }
    public Rectangle getBounds() { return new Rectangle(x, y, BULLET_SIZE, BULLET_SIZE); }
    public int getX() { return x; }
    public int getY() { return y; }
}