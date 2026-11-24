import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

final class Building extends PlatformObject {
    static final int ROOF_THICKNESS = 8;
    private final RooftopSprite sprite;

    Building(int x, int roofY, int width, boolean finishBuilding) {
        this(x, roofY, width, finishBuilding, ImageUtils.loadPng(
                finishBuilding ? "/assets/school.png" : "/assets/building.png"));
    }

    Building(int x, int roofY, int width, boolean finishBuilding, BufferedImage image) {
        super(x, roofY, width, ROOF_THICKNESS);
        if (width <= 0 || roofY < 0 || roofY >= GameConfig.GROUND_Y) {
            throw new IllegalArgumentException("Building needs a positive width and a roof above the road");
        }
        sprite = new RooftopSprite(image,
                new Rectangle(x, roofY, width, GameConfig.GROUND_Y - roofY),
                finishBuilding ? "SCHOOL PNG" : "OFFICE PNG", new Color(91, 108, 126));
        bounds.setBounds(sprite.roofCollision(ROOF_THICKNESS));
    }

    Rectangle getBodyBounds() {
        return sprite.getDrawingBounds();
    }

    @Override
    boolean isOneWay() {
        return true;
    }

    @Override
    int getDrawLayer() {
        return 0;
    }

    @Override
    void draw(Graphics2D graphics) {
        sprite.draw(graphics);
    }
}
