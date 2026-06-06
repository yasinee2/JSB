package com.jbox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel {

    private int HORIZONTAL_CELLS = 10;
    private int VERTICAL_CELLS = 10;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;
    private final int FPS = 15;

    private final boolean PrintDebugInfo = true;

    private final Image AirSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/emptyCell.png").getImage();
    private final Image SandSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/sand.png").getImage();
    private final Image StoneSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/stone.png").getImage();
    private final Image LavaSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/lava.png").getImage();
    private final Image WaterSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/water.png").getImage();
    private final Image NothingSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/nothing.png").getImage();

    private final Image SelectedBorderSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/SelectedElement.png").getImage();

    private final Image DebugSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/debug.png").getImage();

    private final int SpriteSize = AirSprite.getWidth(this);
    private final int FieldWidth = SpriteSize * HORIZONTAL_CELLS;
    private final int FieldHeight = SpriteSize * VERTICAL_CELLS;
    private Image SelectedElement = NothingSprite;
    private Image[][] FieldBuffer = new Image[HORIZONTAL_CELLS][VERTICAL_CELLS];
    private boolean isMousePressed = false;
    private int[] lastClickedCellPos = new int[2];
    private boolean isClickInField = false;

    private final int OffsetY = (SCREEN_HEIGHT - FieldHeight) / 2;
    private final int OffsetX = (SCREEN_WIDTH - FieldWidth) / 2; //DOES: Cords for placing the grid in the middle of the screen

    private Graphics graphics;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
        frame.add(new Main());
        //frame.setSize(500, 500);
        frame.setTitle("JavaSandBox");
        frame.setVisible(true);

    }

    @Override
    protected void paintComponent(Graphics graphics) {
        this.graphics = graphics;
        super.paintComponent(graphics);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        initFieldBuffer();
        initField();
        DrawElementMenu();

        graphics.setColor(Color.WHITE);
        graphics.drawString("Selected: " + getSpriteName(SelectedElement), OffsetX, OffsetY - 10);

    }

    private Main() {

        if (PrintDebugInfo) {
            System.out.println("    Debug info:      ");
            System.out.println("    Screen width:" + SCREEN_WIDTH);
            System.out.println("    Screen height: " + SCREEN_HEIGHT);
            System.out.println("    Field width: " + FieldWidth);
            System.out.println("    Field height: " + FieldHeight);
            System.out.println("    Horizontal cells: " + HORIZONTAL_CELLS);
            System.out.println("    Vertical cells: " + VERTICAL_CELLS);
            System.out.println("    Sprite size: " + SpriteSize);
            System.out.println("");

        }

        Timer timer = new Timer(1000 / FPS, e -> {
            tick();
        });
        timer.start();

        initMouseListener();
    }

    private void initFieldBuffer() {
        if (FieldBuffer[0][0] == null) {
            int x = 0;
            int y = 0;

            for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
                if (x >= HORIZONTAL_CELLS) {
                    y++;
                    x = 0;
                }
                FieldBuffer[x][y] = AirSprite;
                x++;
            }
        }

    }

    private void initField() {
        int y = 0;
        int x = 0;
        for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
            if (x >= HORIZONTAL_CELLS) {
                y++;
                x = 0;
            }
            graphics.drawImage(FieldBuffer[x][y], x * SpriteSize + OffsetX, y * SpriteSize + OffsetY, this);
            x++;
        }

    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isMousePressed = true;
                    FieldClickHandler(e.getX(), e.getY());
                    MenuClickHandler(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isMousePressed = false;
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                FieldClickHandler(e.getX(), e.getY());
            }
        });
    }

    private void FieldClickHandler(int ClickedX, int ClickedY) {
        //int firstCellPosY = (SCREEN_HEIGHT - FieldHeight) / 2;
        int y = 0;
        int x = 0;
        if (ClickedX > OffsetX && ClickedX < OffsetX + FieldWidth) { //DOES: Checks if the click is in the grid
            if (ClickedY > OffsetY && ClickedY < OffsetY + FieldHeight) {//DOES:-------------------------------
                isClickInField = true;
                for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
                    if (x >= HORIZONTAL_CELLS) {
                        y++;
                        x = 0;
                    }
                    if (ClickedX > x * SpriteSize + OffsetX && ClickedX < (x * SpriteSize + OffsetX) + SpriteSize) {

                        if (ClickedY > (y * SpriteSize) + OffsetY && ClickedY < ((y + 1) * SpriteSize) + OffsetY) {

                            lastClickedCellPos[0] = x;
                            lastClickedCellPos[1] = y;
                            if (PrintDebugInfo) {
                                System.out.println("ClickedCell: " + y + ", " + x);
                            }
                        }
                    }
                    x++;
                }
            } else {
                isClickInField = false;
            }
        } else {
            isClickInField = false;
        }

    }

    private void DrawElementMenu() {
        int MenuOffsetX = 2;
        graphics.drawImage(SandSprite, OffsetX - SpriteSize * MenuOffsetX, OffsetY + SpriteSize, this);
        graphics.drawImage(WaterSprite, OffsetX - SpriteSize * MenuOffsetX, OffsetY + SpriteSize * 3, this);
        graphics.drawImage(LavaSprite, OffsetX - SpriteSize * MenuOffsetX, OffsetY + SpriteSize * 5, this);
        graphics.drawImage(StoneSprite, OffsetX - SpriteSize * MenuOffsetX, OffsetY + SpriteSize * 7, this);
        graphics.drawImage(AirSprite, OffsetX - SpriteSize * MenuOffsetX, OffsetY + SpriteSize * 9, this);

        int SelectedBorderSpriteOffset = (SelectedBorderSprite.getWidth(this) - SpriteSize) / 2;
        int[] SelectedBorderSpritePos = {(OffsetX - SpriteSize * 2) - SelectedBorderSpriteOffset, (OffsetY + SpriteSize * 11) - SelectedBorderSpriteOffset};
        graphics.drawImage(SelectedBorderSprite, SelectedBorderSpritePos[0], SelectedBorderSpritePos[1], this);
        graphics.drawImage(SelectedElement, OffsetX - SpriteSize * 2, OffsetY + SpriteSize * 11, this);
    }

    private void MenuClickHandler(int ClickedX, int ClickedY) {
        int MenuOffsetX = 2;
        if (ClickedX > OffsetX - SpriteSize * MenuOffsetX && ClickedX < (OffsetX - SpriteSize * MenuOffsetX) + SpriteSize) {

            if (ClickedY > OffsetY + SpriteSize && ClickedY < OffsetY + SpriteSize * 2) {
                System.out.println("Sand selected");
                ShowSelectedElement(SandSprite);
            }
            if (ClickedY > OffsetY + SpriteSize * 3 && ClickedY < OffsetY + SpriteSize * 4) {
                System.out.println("Water selected");
                ShowSelectedElement(WaterSprite);
            }
            if (ClickedY > OffsetY + SpriteSize * 5 && ClickedY < OffsetY + SpriteSize * 6) {
                System.out.println("Lava selected");
                ShowSelectedElement(LavaSprite);
            }
            if (ClickedY > OffsetY + SpriteSize * 7 && ClickedY < OffsetY + SpriteSize * 8) {
                System.out.println("Stone selected");
                ShowSelectedElement(StoneSprite);
            }
            if (ClickedY > OffsetY + SpriteSize * 9 && ClickedY < OffsetY + SpriteSize * 10) {
                System.out.println("Air selected");
                ShowSelectedElement(NothingSprite);
            }
        }
    }

    private void ShowSelectedElement(Image SpriteOfElement) {

        SelectedElement = SpriteOfElement;
        repaint();
    }

    private void PlaceElement(int CellPosX, int CellPosY) {
        if (SelectedElement == NothingSprite) {
            FieldBuffer[CellPosX][CellPosY] = AirSprite;
        } else {
            FieldBuffer[CellPosX][CellPosY] = SelectedElement;
        }
        repaint();

    }

    private String getSpriteName(Image sprite) {
        if (sprite == SandSprite) {
            return "Sand";
        }
        if (sprite == StoneSprite) {
            return "Stone";
        }
        if (sprite == WaterSprite) {
            return "Water";
        }
        if (sprite == LavaSprite) {
            return "Lava";
        }

        if (sprite == NothingSprite) {
            return "Air";
        }
        return "Unknown";
    }

    private void tick() {
        if (isMousePressed == true && isClickInField == true) {
            PlaceElement(lastClickedCellPos[0], lastClickedCellPos[1]);
        }
        UpdateGame();
    }

    private void UpdateGame() {
        boolean WaterCheckingRight = false;
        boolean LavaCheckingRight = true;
        int x = 0;
        int y = 0;
        for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
            if (x >= HORIZONTAL_CELLS) {
                y++;
                x = 0;
            }

            //DOES: Logic for sand
            if (FieldBuffer[x][y] == SandSprite) {
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x][y + 1] = SandSprite;
                } else if (inBounds(x + 1, y + 1) && FieldBuffer[x + 1][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x + 1][y + 1] = SandSprite;
                } else if (inBounds(x - 1, y + 1) && FieldBuffer[x - 1][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x - 1][y + 1] = SandSprite;
                }
                //DOES: Replaces water
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == WaterSprite) {
                    FieldBuffer[x][y] = WaterSprite;
                    FieldBuffer[x][y + 1] = SandSprite;
                } else if (inBounds(x + 1, y + 1) && FieldBuffer[x + 1][y + 1] == WaterSprite) {
                    FieldBuffer[x][y] = WaterSprite;
                    FieldBuffer[x + 1][y + 1] = SandSprite;
                } else if (inBounds(x - 1, y + 1) && FieldBuffer[x - 1][y + 1] == WaterSprite) {
                    FieldBuffer[x][y] = WaterSprite;
                    FieldBuffer[x - 1][y + 1] = SandSprite;
                }
            }

            //DOES: Logic for water
            if (FieldBuffer[x][y] == WaterSprite) {
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x][y + 1] = WaterSprite;
                } else if (inBounds(x + 1, y + 1) && FieldBuffer[x + 1][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x + 1][y + 1] = WaterSprite;
                } else if (inBounds(x - 1, y + 1) && FieldBuffer[x - 1][y + 1] == AirSprite) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x - 1][y + 1] = WaterSprite;
                } else if (inBounds(x - 1, y) && FieldBuffer[x - 1][y] == AirSprite && !WaterCheckingRight) {
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x - 1][y] = WaterSprite;
                } else if (inBounds(x + 1, y) && FieldBuffer[x + 1][y] == AirSprite) {
                    WaterCheckingRight = true;
                    FieldBuffer[x][y] = AirSprite;
                    FieldBuffer[x + 1][y] = WaterSprite;
                } else {
                    WaterCheckingRight = false;
                }

                //DOES: replaces nearby lava
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == LavaSprite) {
                    FieldBuffer[x][y + 1] = StoneSprite;
                }
                if (inBounds(x, y - 1) && FieldBuffer[x][y - 1] == LavaSprite) {
                    FieldBuffer[x][y - 1] = StoneSprite;
                }
                if (inBounds(x + 1, y) && FieldBuffer[x + 1][y] == LavaSprite) {
                    FieldBuffer[x + 1][y] = StoneSprite;
                }
                if (inBounds(x - 1, y) && FieldBuffer[x - 1][y] == LavaSprite) {
                    FieldBuffer[x - 1][y] = StoneSprite;
                }
            }

            //DOES: Logic for lava
            if (FieldBuffer[x][y] == LavaSprite) {
                //DOES: Replaces lava with stone if water is its neighbor
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == WaterSprite) {
                    FieldBuffer[x][y] = StoneSprite;
                }
                if (inBounds(x, y - 1) && FieldBuffer[x][y - 1] == WaterSprite) {
                    FieldBuffer[x][y] = StoneSprite;
                }
                if (inBounds(x + 1, y) && FieldBuffer[x + 1][y] == WaterSprite) {
                    FieldBuffer[x][y] = StoneSprite;
                }
                if (inBounds(x - 1, y) && FieldBuffer[x - 1][y] == WaterSprite) {
                    FieldBuffer[x][y] = StoneSprite;
                }
                //DOES: Replaces sand with lava if water is its neighbor
                if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == SandSprite) {
                    FieldBuffer[x][y + 1] = LavaSprite;
                }
                if (inBounds(x, y - 1) && FieldBuffer[x][y - 1] == SandSprite) {
                    FieldBuffer[x][y - 1] = LavaSprite;
                }
                if (inBounds(x + 1, y) && FieldBuffer[x + 1][y] == SandSprite) {
                    FieldBuffer[x + 1][y] = LavaSprite;
                }
                if (inBounds(x - 1, y) && FieldBuffer[x - 1][y] == SandSprite) {
                    FieldBuffer[x - 1][y] = LavaSprite;
                }

                //DOES: Normal logic
                if (FieldBuffer[x][y] == LavaSprite) { // nochmal prüfen falls oben zu stone wurde
                    if (inBounds(x, y + 1) && FieldBuffer[x][y + 1] == AirSprite) {
                        FieldBuffer[x][y] = AirSprite;
                        FieldBuffer[x][y + 1] = LavaSprite;
                    } else if (inBounds(x + 1, y + 1) && FieldBuffer[x + 1][y + 1] == AirSprite) {
                        FieldBuffer[x][y] = AirSprite;
                        FieldBuffer[x + 1][y + 1] = LavaSprite;
                    } else if (inBounds(x - 1, y + 1) && FieldBuffer[x - 1][y + 1] == AirSprite) {
                        FieldBuffer[x][y] = AirSprite;
                        FieldBuffer[x - 1][y + 1] = LavaSprite;
                    } else if (inBounds(x - 1, y) && FieldBuffer[x - 1][y] == AirSprite && LavaCheckingRight) {
                        FieldBuffer[x][y] = AirSprite;
                        FieldBuffer[x - 1][y] = LavaSprite;
                    } else if (inBounds(x + 1, y) && FieldBuffer[x + 1][y] == AirSprite) {
                        LavaCheckingRight = false;
                        FieldBuffer[x][y] = AirSprite;
                        FieldBuffer[x + 1][y] = LavaSprite;
                    } else {
                        LavaCheckingRight = true;
                    }
                }
            }

            x++;
        }

        repaint();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < HORIZONTAL_CELLS && y >= 0 && y < VERTICAL_CELLS;
    }
}
