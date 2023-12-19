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
                // For example, increase player's speed
                player.increaseSpeed(1.5, 5000);
                break;
            case 1:
                // For example, decrease player's speed
                player.increaseSpeed(0.5, 5000);
                break;
            case 2:
                // For example, increase bullet speed
                bulletManager.decreaseBulletSpeed(1.5, 5000);
                break;
            case 3:
                // For example, decrease bullet speed
                bulletManager.decreaseBulletSpeed(0.5, 5000);
                break;
            case 4:
                // For example, enable invincibility
                player.enableBarrier(5000);
                break;
            case 5:
                // For example, reset bullets
                bulletManager.resetBullets();
                break;
        }
    }

}