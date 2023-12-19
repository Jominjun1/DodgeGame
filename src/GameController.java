import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameController {
    private Player player;
    private GameScreen gameScreen;
    private Timer timer;
    private BufferedImage offScreenImage;
    private Random random;
    private List<Item> items;

    public GameController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.player = new Player(gameScreen.getWidth(), gameScreen.getHeight());
        gameScreen.setFocusable(true);

        offScreenImage = new BufferedImage(gameScreen.getWidth(), gameScreen.getHeight(), BufferedImage.TYPE_INT_ARGB);
        random = new Random();
        items = new ArrayList<>();
    }
    public void startGame() {
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGame();
                renderGame();
                gameScreen.repaint();
            }
        });
        timer.start();
    }
    private void updateGame() {
        player.update();
    }
    private void renderGame() {
        Graphics offScreenGraphics = offScreenImage.getGraphics();
        // 오프스크린 이미지 지우기
        offScreenGraphics.setColor(Color.BLACK);
        offScreenGraphics.fillRect(0, 0, gameScreen.getWidth(), gameScreen.getHeight());
        // 플레이어 그리기
        player.draw(offScreenGraphics);
        // 아이템 그리기
        for (Item item : items) {
            item.draw(offScreenGraphics);
        }
        offScreenGraphics.dispose();
    }

    public Player getPlayer() {
        return player;
    }
}