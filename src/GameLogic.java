import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private Player player;
    private BulletManager bulletManager;
    private GameScreen gameScreen;
    private boolean gameOver;

    public GameLogic(Player player, BulletManager bulletManager, GameScreen gameScreen) {
        this.player = player;
        this.bulletManager = bulletManager;
        this.gameScreen = gameScreen;
        this.gameOver = false;
    }

    // 충돌 검사
    public void checkCollisions() {
        Rectangle2D playerBounds = player.getBounds();
        for (Bullet bullet : bulletManager.getBullets()) {
            Rectangle2D bulletBounds = bullet.getBounds();
            if (playerBounds.intersects(bulletBounds) && !player.isImmuneToBullets()) {
                gameOver = true;
                showGameOverMessage();
                // 타이머를 멈추어 게임을 정지
                gameScreen.getTimer().stop();
                break;
            }
        }
    }

    // 아이템 업데이트 로직
    public void updateItems(List<Item> items) {
        List<Item> itemsToRemove = new ArrayList<>();

        for (Item item : items) {
            if (player.intersects(item)) {
                handleItemEffects(item);
                itemsToRemove.add(item);
            }
        }

        // 아이템을 제거로 표시
        for (Item item : itemsToRemove) {
            item.markAsRemoved();
        }

        items.removeAll(itemsToRemove);
    }

    // 아이템 효과 처리
    private void handleItemEffects(Item item) {
        Item.ItemType itemType = item.getItemType();
        switch (itemType) {
            case GIFT:
                ItemLoader.applyRandomEffect(player, bulletManager);
                break;
        }
    }

    // 게임 오버 여부 확인
    public boolean isGameOver() {
        return gameOver;
    }

    // 게임 오버 메시지 표시
    public void showGameOverMessage() {
        player.stopPlayer();
        JOptionPane.showMessageDialog(gameScreen, "게임 끝! 다른 플레이어를 기다리세요.\n당신의 시간: " +
                gameScreen.getElapsedTime() / 60 + " 초", "게임 오버", JOptionPane.INFORMATION_MESSAGE);
        // 게임 오버 시의 추가 로직 처리, 예를 들어 메인 메뉴로 돌아가기 또는 게임 재시작 등
    }
}
