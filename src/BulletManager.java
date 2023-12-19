import java.util.ArrayList;
import java.util.List;

public class BulletManager {
    private List<Bullet> bullets;

    public BulletManager() {
        this.bullets = new ArrayList<>();
        generateInitialBullets();
    }

    public void resetBullets() {
        bullets.clear(); // Clear existing bullets
        generateInitialBullets();
    }

    // Generate initial bullets in a circular pattern
    public void generateInitialBullets() {
        int centerX = 1080 / 2;
        int centerY = 800 / 2;
        for (int i = 0; i < 15; i++) {
            double angle = i * (2 * Math.PI / 15);  // Divide the circle into 15 parts
            generateBullet(centerX, centerY, angle);
        }
    }

    // Decrease bullet speed for a specified duration
    public void decreaseBulletSpeed(double factor, int duration) {
        for (Bullet bullet : bullets) {
            bullet.decreaseSpeed(factor, duration);
        }
    }

    // Generate a bullet with a specified angle
    public void generateBullet(int startX, int startY, double angle) {
        bullets.add(new Bullet(startX, startY, angle));
    }

    // Update all bullets
    public void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }

    // Get the list of bullets
    public List<Bullet> getBullets() {
        return bullets;
    }
}