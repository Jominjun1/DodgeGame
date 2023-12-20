import javax.swing.*;
import java.awt.*;

public class ItemLoader {
    private static final String GIFT_IMAGE_PATH = "src/image/gift.png";
    public static Image getRandomItemImage() {
        return new ImageIcon(GIFT_IMAGE_PATH).getImage();
    }
    public static void applyRandomEffect(Player player, BulletManager bulletManager) {
        int randomEffect = (int) (Math.random() * 6); // 0 to 5
        applyEffect(player, bulletManager, randomEffect);
    }
    private static void applyEffect(Player player, BulletManager bulletManager, int effectIndex) {
        switch (effectIndex) {
            case 0:
                // 플레이어의 속도를 높입니다
                player.increaseSpeed(1.5, 5000);
                break;
            case 1:
                // 플레이어의 속도를 낮춥니다
                player.increaseSpeed(0.5, 5000);
                break;
            case 2:
                // 탄막의 속도를 높입니다
                bulletManager.decreaseBulletSpeed(1.5, 5000);
                break;
            case 3:
                // 탄막의 속도를 낮춥니다
                bulletManager.decreaseBulletSpeed(0.5, 5000);
                break;
            case 4:
                // 플레이어에게 무적 효과를 부여합니다
                player.enableBarrier(5000);
                break;
            case 5:
                // 탄막을 초기화 합니다.
                bulletManager.resetBullets();
                break;
        }
    }
}