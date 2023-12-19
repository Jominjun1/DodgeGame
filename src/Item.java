import java.awt.*;

public class Item {
    public enum ItemType {
        // 아이템 타입 열거형
        GIFT,
        // 추가 아이템 타입이 있다면 이곳에 추가 가능
    }

    private Image image;
    private int x;
    private int y;
    private int size;
    private ItemType itemType;
    private boolean hasSpeedEffect;
    private boolean hasBarrierEffect;

    private boolean removed;

    public Item(Image image, int x, int y, int size, ItemType itemType, boolean hasSpeedEffect, boolean hasBarrierEffect) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.size = size;
        this.itemType = itemType;
        this.hasSpeedEffect = hasSpeedEffect;
        this.hasBarrierEffect = hasBarrierEffect;
        this.removed = false;
    }

    public void draw(Graphics g) {
        if (!removed) {
            g.drawImage(image, x, y, size, size, null);
        }
    }

    // 아이템을 제거로 표시하는 메서드 추가
    public void markAsRemoved() {
        this.removed = true;
    }

    // 아이템이 제거되었는지 확인하는 메서드 추가
    public boolean isRemoved() {
        return removed;
    }

    // 아이템이 플레이어와 충돌하는지 확인하는 메서드
    public boolean intersects(Player player) {
        Rectangle playerBounds = player.getBounds();
        Rectangle itemBounds = new Rectangle(x, y, size, size);
        return playerBounds.intersects(itemBounds);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean hasSpeedEffect() {
        return hasSpeedEffect;
    }

    public boolean hasBarrierEffect() {
        return hasBarrierEffect;
    }
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
