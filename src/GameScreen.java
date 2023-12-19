import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends JFrame {
    private Player player;
    private Timer timer;
    private int elapsedTime;
    private BulletManager bulletManager;
    private GameLogic gameLogic;
    private List<Item> items;
    private boolean initialBulletsGenerated = false;

    public GameScreen() {
        super("총알 피하기 게임");

        timer = new Timer(16, new ActionListener() {
            private long lastBulletIncreaseTime = System.currentTimeMillis();
            private int bulletIncreaseInterval = 3000; // 3000 milliseconds (3 seconds)

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameLogic.isGameOver()) {
                    if (!initialBulletsGenerated) {
                        generateBulletInitial();  // 초기 생성 시 한 번 호출
                        initialBulletsGenerated = true;
                    }
                    updateBullets();   // 총알 위치 업데이트
                    gameLogic.updateItems(items); // 아이템 위치 업데이트
                    player.update();   // 플레이어 위치 업데이트
                    gameLogic.checkCollisions(); // 충돌 확인
                    elapsedTime++;     // 경과 시간 증가
                    repaint();         // 화면 다시 그리기

                    generateItem();

                    // 2초마다 20개의 총알 생성
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastBulletIncreaseTime >= bulletIncreaseInterval) {
                        for (int i = 0; i < 20; i++) {
                            double angle = i * (2 * Math.PI / 10);
                            bulletManager.generateBullet(1080 / 2, 800 / 2, angle);
                        }
                        lastBulletIncreaseTime = currentTime;
                    }
                }
            }
        });

        timer.start();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 800);
        player = new Player(getWidth() / 2, getHeight() / 2 - 200);
        bulletManager = new BulletManager();
        gameLogic = new GameLogic(player, bulletManager, this);
        items = new ArrayList<>(); // 아이템 리스트 초기화
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateBulletInitial() {
        bulletManager.generateInitialBullets();
    }

    private void updateBullets() {
        bulletManager.updateBullets();
    }

    // 아이템 생성 로직
    private void generateItem() {
        if (getElapsedTime() % 300 == 0) {
            int itemSize = 20;
            int x = (int) (Math.random() * (getWidth() - itemSize));
            int y = (int) (Math.random() * (getHeight() - itemSize));
            Image itemImage = ItemLoader.getRandomItemImage();
            Item.ItemType itemType = getRandomItemType();

            Item item = new Item(itemImage, x, y, itemSize, itemType, true, true);
            items.add(item);
        }
    }

    private Item.ItemType getRandomItemType() {
        Item.ItemType[] itemTypes = Item.ItemType.values();
        int randomIndex = (int) (Math.random() * itemTypes.length);
        return itemTypes[randomIndex];
    }

    @Override
    public void paint(Graphics g) {
        Image offScreenImage = createImage(getWidth(), getHeight());
        Graphics offScreenGraphics = offScreenImage.getGraphics();

        ImageIcon backgroundImageIcon = new ImageIcon("src/image/BackGround.png");
        Image backgroundImage = backgroundImageIcon.getImage();
        offScreenGraphics.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        player.draw(offScreenGraphics);
        for (Bullet bullet : bulletManager.getBullets()) {
            bullet.draw(offScreenGraphics);
        }

        // Draw items only if they are not removed
        for (Item item : items) {
            if (!item.isRemoved()) {
                item.draw(offScreenGraphics);
            }
        }
        g.drawImage(offScreenImage, 0, 0, this);
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public Timer getTimer() {
        return timer;
    }
}
