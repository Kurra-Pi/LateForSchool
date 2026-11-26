import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

final class GamePanel extends JPanel implements ActionListener {
    private final GameAssets assets;
    private final LevelMap map;
    private final SceneRenderer renderer;
    private final String playerName;
    private final PlayerRecord savedRecord;
    private final Consumer<RunResult> onFinished;
    private final Timer timer = new Timer(16, this);

    private Player player;
    private boolean left;
    private boolean right;
    private boolean jumpHeld;
    private boolean jumpRequested;
    private boolean gameOver;
    private boolean won;
    private boolean resultReported;
    private boolean active = true;
    private long startedNanos;
    private long finishedMillis;
    private boolean showCollisionBoxes;

    private double cameraX;
    private double jumpStartY;
    private double highestY;
    private int jumpHeight;
    private int score;
    private int candiesCollected;

    GamePanel() {
        this(new GameAssets(), "Student", null, result -> { });
    }

    GamePanel(GameAssets assets, String playerName, PlayerRecord savedRecord,
            Consumer<RunResult> onFinished) {
        this.assets = assets;
        this.playerName = playerName;
        this.savedRecord = savedRecord;
        this.onFinished = onFinished;
        map = new LevelMap(assets);
        renderer = new SceneRenderer(assets);
        setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        setBackground(new Color(245, 245, 245));
        setFocusable(true);

        map.reset();
        resetPlayerPosition();
        setupControls();
        startedNanos = System.nanoTime();
        timer.start();
    }

    private void bindKey(String keyStroke, String name, Runnable action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyStroke), name);
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (active) action.run();
            }
        });
    }

    private void setupControls() {
        bindKey("pressed A", "leftOn", () -> left = true);
        bindKey("released A", "leftOff", () -> left = false);
        bindKey("pressed LEFT", "arrowLeftOn", () -> left = true);
        bindKey("released LEFT", "arrowLeftOff", () -> left = false);

        bindKey("pressed D", "rightOn", () -> right = true);
        bindKey("released D", "rightOff", () -> right = false);
        bindKey("pressed RIGHT", "arrowRightOn", () -> right = true);
        bindKey("released RIGHT", "arrowRightOff", () -> right = false);

        bindKey("pressed SPACE", "jumpSpace", this::pressJump);
        bindKey("released SPACE", "jumpSpaceOff", this::releaseJump);
        bindKey("pressed W", "jumpW", this::pressJump);
        bindKey("released W", "jumpWOff", this::releaseJump);
        bindKey("pressed UP", "jumpUp", this::pressJump);
        bindKey("released UP", "jumpUpOff", this::releaseJump);

        bindKey("pressed R", "restart", this::restartGame);
        bindKey("pressed F3", "collisionGuides", () -> {
            showCollisionBoxes = !showCollisionBoxes;
            repaint();
        });
    }

    private void pressJump() {
        if (!jumpHeld) {
            jumpRequested = true;
        }
        jumpHeld = true;
    }

    private void releaseJump() {
        jumpHeld = false;
    }

    private void restartGame() {
        map.reset();
        resetPlayerPosition();
        score = 0;
        candiesCollected = 0;
        gameOver = false;
        won = false;
        resultReported = false;
        active = true;
        finishedMillis = 0;
        startedNanos = System.nanoTime();
        left = false;
        right = false;
        jumpHeld = false;
        jumpRequested = false;
        timer.start();
        repaint();
    }

    private void resetPlayerPosition() {
        player = new Player(50, GameConfig.GROUND_Y - GameConfig.PLAYER_HEIGHT);
        player.onGround = true;
        cameraX = 0;
        jumpHeight = 0;
        jumpStartY = player.y;
        highestY = player.y;
        jumpHeld = false;
        jumpRequested = false;
        left = false;
        right = false;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        updateGame();
        repaint();
    }

    private boolean overlapsX(double x, Rectangle rectangle) {
        return x + GameConfig.PLAYER_WIDTH > rectangle.x && x < rectangle.x + rectangle.width;
    }

    private boolean overlapsY(double y, Rectangle rectangle) {
        return y + GameConfig.PLAYER_HEIGHT > rectangle.y && y < rectangle.y + rectangle.height;
    }

    private Rectangle playerBounds() {
        return new Rectangle((int) player.x, (int) player.y,
                GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
    }

    private void updateGame() {
        if (gameOver || won || !active) {
            return;
        }

        moveHorizontally();

        if (jumpRequested && player.onGround) {
            player.velocityY = GameConfig.JUMP_STRENGTH;
            player.onGround = false;
            jumpStartY = player.y;
            highestY = player.y;
            jumpHeight = 0;
        }
        jumpRequested = false;

        double oldY = player.y;
        double oldBottom = oldY + GameConfig.PLAYER_HEIGHT;
        boolean wasFalling = player.velocityY > 0;

        player.velocityY = Math.min(player.velocityY + GameConfig.GRAVITY, GameConfig.MAX_FALL_SPEED);
        double newY = oldY + player.velocityY;
        player.onGround = false;

        if (player.velocityY > 0) {
            double landingY = Double.POSITIVE_INFINITY;

            for (Rectangle solid : map.solids) {
                if (overlapsX(player.x, solid)
                        && oldBottom <= solid.y
                        && newY + GameConfig.PLAYER_HEIGHT >= solid.y
                        && solid.y < landingY) {
                    landingY = solid.y;
                }
            }

            if (landingY != Double.POSITIVE_INFINITY) {
                newY = landingY - GameConfig.PLAYER_HEIGHT;
                player.velocityY = 0;
                player.onGround = true;
            }
        } else if (player.velocityY < 0) {
            double ceilingY = Double.NEGATIVE_INFINITY;

            for (Rectangle solid : map.solids) {
                if (map.oneWayPlatforms.contains(solid)) {
                    continue;
                }
                double bottom = solid.y + solid.height;
                if (overlapsX(player.x, solid)
                        && oldY >= bottom
                        && newY <= bottom
                        && bottom > ceilingY) {
                    ceilingY = bottom;
                }
            }

            if (ceilingY != Double.NEGATIVE_INFINITY) {
                newY = ceilingY;
                player.velocityY = 0;
            }
        }

        player.y = newY;

        if (!player.onGround && player.y < highestY) {
            highestY = player.y;
        }
        jumpHeight = (int) Math.max(0, jumpStartY - highestY);

        collectCandies();
        updateDogsAndCheckCollision(wasFalling, oldBottom);

        if (gameOver) {
            finishRun();
            return;
        }

        if (player.y > GameConfig.SCREEN_HEIGHT + 100) {
            resetPlayerPosition();
            return;
        }

        updateCamera();

        if (playerBounds().intersects(map.goalArea)) {
            won = true;
            left = false;
            right = false;
            finishRun();
        }
    }

    private void moveHorizontally() {
        double dx = (right ? GameConfig.MOVE_SPEED : 0) - (left ? GameConfig.MOVE_SPEED : 0);
        if (dx == 0) {
            return;
        }

        if (dx > 0) {
            player.facingRight = true;
        } else {
            player.facingRight = false;
        }

        double oldX = player.x;
        double newX = Math.max(0, Math.min(GameConfig.WORLD_WIDTH - GameConfig.PLAYER_WIDTH, oldX + dx));

        for (Rectangle solid : map.solids) {
            if (map.oneWayPlatforms.contains(solid) || !overlapsY(player.y, solid)) {
                continue;
            }

            if (dx > 0 && oldX + GameConfig.PLAYER_WIDTH <= solid.x
                    && newX + GameConfig.PLAYER_WIDTH > solid.x) {
                newX = solid.x - GameConfig.PLAYER_WIDTH;
            } else if (dx < 0 && oldX >= solid.x + solid.width
                    && newX < solid.x + solid.width) {
                newX = solid.x + solid.width;
            }
        }

        if (player.onGround && Math.abs(newX - (oldX + dx)) > 0.01) {
            double targetX = Math.max(0,
                    Math.min(GameConfig.WORLD_WIDTH - GameConfig.PLAYER_WIDTH, oldX + dx));
            for (int step = 1; step <= 6; step++) {
                double raisedY = player.y - step;
                if (canStandAt(targetX, raisedY) && canStandAt(oldX, raisedY)
                        && hasSupportAt(targetX, raisedY)) {
                    player.y = raisedY;
                    newX = targetX;
                    break;
                }
            }
        }

        player.x = Math.max(0, Math.min(GameConfig.WORLD_WIDTH - GameConfig.PLAYER_WIDTH, newX));
    }

    private boolean canStandAt(double x, double y) {
        for (Rectangle solid : map.solids) {
            if (!map.oneWayPlatforms.contains(solid)
                    && overlapsX(x, solid) && overlapsY(y, solid)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSupportAt(double x, double y) {
        double feet = y + GameConfig.PLAYER_HEIGHT;
        for (Rectangle solid : map.solids) {
            if (overlapsX(x, solid) && Math.abs(solid.y - feet) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private void collectCandies() {
        Rectangle bounds = playerBounds();
        for (Candy candy : map.candies) {
            if (!candy.collected && bounds.intersects(candy.getBounds())) {
                candy.collected = true;
                candiesCollected++;
                score += 10;
            }
        }
    }

    private void updateDogsAndCheckCollision(boolean wasFalling, double oldBottom) {
        for (Dog dog : map.dogs) {
            dog.update();

            if (dog.isStunned()) {
                continue;
            }

            if (playerBounds().intersects(dog.getBounds())) {
                if (wasFalling && oldBottom <= dog.y + 12) {
                    dog.stun();
                    player.y = dog.y - GameConfig.PLAYER_HEIGHT;
                    player.velocityY = -8.5;
                    player.onGround = false;
                } else {
                    gameOver = true;
                }
                return;
            }
        }
    }

    private void updateCamera() {
        double target = player.x + GameConfig.PLAYER_WIDTH / 2.0 - GameConfig.SCREEN_WIDTH / 2.0;
        cameraX = Math.max(0, Math.min(GameConfig.WORLD_WIDTH - GameConfig.SCREEN_WIDTH, target));
    }

    long getElapsedMillis() {
        return resultReported ? finishedMillis
                : Math.max(0, (System.nanoTime() - startedNanos) / 1_000_000L);
    }
    
    private void drawDebugInfo(Graphics2D graphics) {
    int worldX = (int) player.x;
    int worldY = (int) player.y;
    int camera = (int) cameraX;

    int feetX = worldX + GameConfig.PLAYER_WIDTH / 2;
    int feetY = worldY + GameConfig.PLAYER_HEIGHT;
    int screenX = worldX - camera;

    int boxX = GameConfig.SCREEN_WIDTH - 382;
    int textX = boxX + 14;

    graphics.setColor(new Color(21, 34, 41, 230));
    graphics.fillRoundRect(boxX, 12, 370, 132, 14, 14);

    graphics.setColor(Color.YELLOW);
    graphics.setFont(new Font("SansSerif", Font.BOLD, 14));
    graphics.drawString(
            "F3 DEBUG - coordinates in pixels",
            textX, 35
    );

    graphics.setColor(Color.WHITE);
    graphics.setFont(new Font("Monospaced", Font.PLAIN, 13));

    graphics.drawString(
            "World top-left X: " + worldX + "  Y: " + worldY,
            textX, 59
    );

    graphics.drawString(
            "Feet center    X: " + feetX + "  Y: " + feetY,
            textX, 81
    );

    graphics.drawString(
            "Camera X: " + camera,
            textX, 103
    );

    graphics.drawString(
            "Screen top-left X: " + screenX + "  Y: " + worldY,
            textX, 125
    );
}

    private void drawHud(Graphics2D graphics) {
        graphics.setColor(new Color(21, 34, 41, 220));
        graphics.fillRoundRect(12, 12, 420, 91, 14, 14);
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 16));
        String displayName = playerName.length() > 24 ? playerName.substring(0, 24) + "..." : playerName;
        graphics.drawString("Student: " + displayName, 25, 36);
        graphics.drawString("Score: " + score + "    Candies: " + candiesCollected
                + "    Time: " + GameTime.format(getElapsedMillis()), 25, 60);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String best = savedRecord != null && savedRecord.hasRecord()
                ? "Saved best: " + savedRecord.bestScore() + " in " + GameTime.format(savedRecord.bestTimeMillis())
                : "Reach school to set your first record.";
        graphics.drawString(best, 25, 84);
        graphics.setColor(new Color(21, 34, 41, 200));
        graphics.fillRoundRect(180, GameConfig.SCREEN_HEIGHT - 32, 600, 25, 10, 10);
        graphics.setColor(Color.WHITE);
        graphics.drawString("A/D or arrows: Move   Space/W/Up: Jump   R: Restart   F3: Collision guides",
                192, GameConfig.SCREEN_HEIGHT - 14);
        
        if (showCollisionBoxes) {
            drawDebugInfo(graphics);
            }
    }

    private void finishRun() {
        if (resultReported) return;
        finishedMillis = Math.max(0, (System.nanoTime() - startedNanos) / 1_000_000L);
        resultReported = true;
        active = false;
        timer.stop();
        left = false;
        right = false;
        jumpHeld = false;
        jumpRequested = false;
        onFinished.accept(new RunResult(playerName, score, candiesCollected, finishedMillis, won));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D screen = (Graphics2D) graphics.create();
        renderer.draw(screen, map, player, cameraX, showCollisionBoxes);
        drawHud(screen);
        screen.dispose();
    }

    void stop() {
        timer.stop();
        active = false;
    }
}
