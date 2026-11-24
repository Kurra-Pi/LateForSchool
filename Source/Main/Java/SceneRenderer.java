import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

final class SceneRenderer {
    private final GameAssets assets;

    SceneRenderer(GameAssets assets) {
        this.assets = assets;
    }

    void drawMenuBackground(Graphics2D graphics) {
        drawBackground(graphics, 0);
        drawRoad(graphics, new Rectangle(0, GameConfig.GROUND_Y,
                GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT - GameConfig.GROUND_Y));
    }

    void draw(Graphics2D screen, LevelMap map, Player player, double cameraX) {
        draw(screen, map, player, cameraX, false);
    }

    void draw(Graphics2D screen, LevelMap map, Player player, double cameraX,
            boolean showCollisionBoxes) {
        screen.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        screen.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        drawBackground(screen, (int) cameraX);

        Graphics2D world = (Graphics2D) screen.create();
        world.translate(-(int) cameraX, 0);
        for (Rectangle section : map.ground) {
            drawRoad(world, section);
        }
        for (int layer = 0; layer <= 2; layer++) {
            for (PlatformObject platform : map.objectPlatforms) {
                if (platform.getDrawLayer() == layer) {
                    platform.draw(world);
                }
            }
        }
        for (Candy candy : map.candies) {
            if (!candy.collected) {
                candy.draw(world);
            }
        }
        for (Dog dog : map.dogs) {
            dog.draw(world);
        }
        drawGoalMarker(world, map.goalPole);
        player.draw(world);
        if (showCollisionBoxes) {
            world.setColor(new Color(255, 70, 70, 200));
            for (Rectangle solid : map.solids) {
                world.drawRect(solid.x, solid.y, solid.width - 1, solid.height - 1);
            }
            world.setColor(Color.CYAN);
            for (PlatformObject object : map.objectPlatforms) {
                if (object instanceof PngPlatform png) {
                    Rectangle sprite = png.getSpriteBounds();
                    world.drawRect(sprite.x, sprite.y, sprite.width, sprite.height);
                } else if (object instanceof SevenEleven store) {
                    Rectangle sprite = store.getSpriteBounds();
                    world.drawRect(sprite.x, sprite.y, sprite.width, sprite.height);
                } else if (object instanceof Building building) {
                    Rectangle facade = building.getBodyBounds();
                    world.drawRect(facade.x, facade.y, facade.width, facade.height);
                } else if (object instanceof RoofPlatform roof) {
                    Rectangle sprite = roof.getSpriteBounds();
                    world.drawRect(sprite.x, sprite.y, sprite.width, sprite.height);
                }
            }
        }
        world.dispose();
        if (showCollisionBoxes) {
            screen.setColor(new Color(0, 0, 0, 190));
            screen.fillRect(240, 473, 480, 25);
            screen.setColor(Color.WHITE);
            screen.drawString("F3 guides: RED = collisions   CYAN = sprite extent", 255, 490);
        }
    }

    private void drawBackground(Graphics2D graphics, int camera) {
        if (assets.backgroundImage != null) {
            int width = Math.max(1, (int) Math.round(
                    assets.backgroundImage.getWidth() * GameConfig.BACKGROUND_SCALE));
            int height = Math.max(1, (int) Math.round(
                    assets.backgroundImage.getHeight() * GameConfig.BACKGROUND_SCALE));
            int y = GameConfig.GROUND_Y - (int) Math.round(
                    GameConfig.BACKGROUND_BASELINE_Y * GameConfig.BACKGROUND_SCALE);
            int offset = Math.floorMod((int) (camera * GameConfig.BACKGROUND_PARALLAX), width);
            Graphics2D backdrop = (Graphics2D) graphics.create();
            // Extend the image's top sky color above the panorama.
            backdrop.setColor(new Color(assets.backgroundImage.getRGB(0, 0)));
            backdrop.fillRect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
            backdrop.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            for (int x = -offset; x < GameConfig.SCREEN_WIDTH; x += width) {
                backdrop.drawImage(assets.backgroundImage, x, y, width, height, null);
            }
            backdrop.dispose();
        } else {
            graphics.setColor(new Color(135, 190, 205));
            graphics.fillRect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        }

        drawCloud(graphics, 90, 88);
        drawCloud(graphics, 420, 118);
        drawCloud(graphics, 720, 155);
    }

    private void drawCloud(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(255, 255, 245, 225));
        graphics.fillOval(x, y + 12, 48, 26);
        graphics.fillOval(x + 17, y, 50, 38);
        graphics.fillOval(x + 43, y + 12, 50, 26);
    }

    private void drawRoad(Graphics2D graphics, Rectangle road) {
        graphics.setColor(new Color(105, 105, 101));
        graphics.fillRect(road.x, road.y, road.width, road.height);

        graphics.setColor(new Color(196, 176, 135));
        graphics.fillRect(road.x, road.y, road.width, 8);
        graphics.setColor(new Color(147, 124, 91));
        for (int x = road.x + 18; x < road.x + road.width; x += 34) {
            graphics.drawLine(x, road.y + 1, x, road.y + 7);
        }

        graphics.setColor(new Color(235, 221, 164));
        for (int x = road.x + 22; x < road.x + road.width - 24; x += 85) {
            graphics.fillRoundRect(x, road.y + 55, 40, 5, 4, 4);
        }

        graphics.setColor(new Color(65, 67, 66));
        graphics.drawLine(road.x, road.y + road.height - 1,
                road.x + road.width, road.y + road.height - 1);
    }

    private void drawGoalMarker(Graphics2D graphics, Rectangle goalPole) {
        graphics.setColor(new Color(194, 123, 160));
        graphics.fillRect(goalPole.x, goalPole.y, goalPole.width, goalPole.height);
        graphics.setColor(new Color(255, 207, 74));
        Polygon flag = new Polygon(
                new int[]{goalPole.x + goalPole.width, goalPole.x + 42,
                        goalPole.x + goalPole.width},
                new int[]{goalPole.y, goalPole.y + 15, goalPole.y + 30}, 3);
        graphics.fillPolygon(flag);
    }
}
