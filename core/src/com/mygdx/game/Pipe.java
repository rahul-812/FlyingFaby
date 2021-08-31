package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import
import static com.mygdx.game.FlappyBird.SCREEN_HEIGHT;
import static com.mygdx.game.FlappyBird.SCREEN_WIDTH;

public class Pipe {
    public int pipeX;
    public int topPipeY, bottomPipeY;
    public static final int VERTICAL_GAP = 450;
    public static int PIPE_WIDTH, PIPE_HEIGHT, HORIZONTAL_GAP;
    public static Texture pipeTop, pipeBottom;
    public static Random random;
    public Rectangle pipeTopRect, pipeBottomRect;

    public Pipe(int pipeX) {
        this.pipeX = pipeX;

        pipeTop = new Texture("top_pipe.png");
        pipeBottom = new Texture("bottom_pipe.png");

        PIPE_WIDTH = pipeTop.getWidth();
        PIPE_HEIGHT = pipeBottom.getHeight();

        HORIZONTAL_GAP = SCREEN_WIDTH - (2 * PIPE_WIDTH);
        pipeTopRect = new Rectangle();
        pipeBottomRect = new Rectangle();
        random = new Random();
        randomGenerator();
    }

    public void randomGenerator() {
        topPipeY = (20 * random.nextInt((SCREEN_HEIGHT-200-500-VERTICAL_GAP)/20))+500+VERTICAL_GAP;
        bottomPipeY = topPipeY - VERTICAL_GAP - PIPE_HEIGHT;

    }
}
