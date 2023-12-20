import java.util.ArrayList;
import java.util.List;
public class BulletManager {
    private List<Bullet> bullets;
    public BulletManager() {
        this.bullets = new ArrayList<>();
        generateInitialBullets();
    }
    public void resetBullets() {
        // 탄막 초기화
        bullets.clear(); 
        generateInitialBullets();
    }
    // 원형 패턴으로 초기 글머리 기호를 생성합니다.
    public void generateInitialBullets() {
        int centerX = 1080 / 2;
        int centerY = 800 / 2;
        for (int i = 0; i < 15; i++) {
            double angle = i * (2 * Math.PI / 15);  // 원을 15개 부분으로 나눕니다.
            generateBullet(centerX, centerY, angle);
        }
    }
    // 지정된 기간 동안 총알 속도를 줄입니다.
    public void decreaseBulletSpeed(double factor, int duration) {
        for (Bullet bullet : bullets) { bullet.decreaseSpeed(factor, duration); }
    }
    // 지정된 각도의 총알 생성
    public void generateBullet(int startX, int startY, double angle) {
        bullets.add(new Bullet(startX, startY, angle));
    }
    // 모든 글머리 기호 업데이트
    public void updateBullets() { for (Bullet bullet : bullets) { bullet.update(); } }
    // 글머리 기호 목록을 가져옵니다.
    public List<Bullet> getBullets() {
        return bullets;
    }
}