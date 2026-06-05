package com.jbox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {

    private int HORIZONTAL_CELLS = 10;
    private int VERTICAL_CELLS = 10;
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    private final Image SandSprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/sand.png").getImage();
    private final Image EmptySprite = new ImageIcon("/home/yasin/Documents/ScriptStuff/Projects/Java/JavaBox/src/main/resources/emptyCell.png").getImage();

    private final int SpriteSize = EmptySprite.getWidth(this);
    private final int FieldWidth = SpriteSize * HORIZONTAL_CELLS;
    private final int FieldHeight = SpriteSize * VERTICAL_CELLS;

    private int clickedX;
    private int clickedY;

    private final int OffsetY = SpriteSize * 2;
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
        graphics.setColor(Color.GRAY); // Hintergrund färben
        graphics.fillRect(0, 0, getWidth(), getHeight());
        initField();
        initMouseListener();
    }

    private void initField() {
        HORIZONTAL_CELLS += 1;
        VERTICAL_CELLS += 1;

        //int firstCellPosY = (SCREEN_HEIGHT - FieldHeight) / 2;
        int y = 0;
        int x = 0;
        for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
            if (x >= HORIZONTAL_CELLS) {
                y++;
                x = 0;
            }
            graphics.drawImage(EmptySprite, SpriteSize * x + OffsetX, SpriteSize * y + OffsetY, this);
            x++;
        }

    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedX = e.getX();
                clickedY = e.getY();
                System.out.println(clickedX + ", " + clickedY);

                int OffsetX = (SCREEN_WIDTH - FieldWidth) / 2; //DOES: Cords for placing the grid in the middle of the screen
                //int firstCellPosY = (SCREEN_HEIGHT - FieldHeight) / 2;
                int y = 0;
                int x = 0;
                for (int i = 0; i < HORIZONTAL_CELLS * VERTICAL_CELLS; i++) {
                    if (x >= HORIZONTAL_CELLS) {
                        y++;
                        x = 0;
                    }
                    if (clickedX > x * SpriteSize + OffsetX && clickedX < (x * SpriteSize + OffsetX) + SpriteSize) {
                        System.out.println("x: " + x);
                        if (clickedY > (y * SpriteSize) + OffsetY && clickedY < ((y + 1) * SpriteSize) + OffsetY) {
                            System.out.println("y: " + y);
                        }
                    }
                    x++;
                }

            }
        });
    }
}
