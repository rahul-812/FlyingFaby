package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

/**
 * @see ApplicationAdapter
 * @see ApplicationAdapter
 * @see Intersector
 * @author RAHUL
 * @version 1.0.0
 */
public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	public Texture gameStartImage, background, ground, gameOverImage;
	public int groundX, ground2X;

	public static int SCREEN_WIDTH, SCREEN_HEIGHT, BIRD_WIDTH, BIRD_HEIGHT;
	public Texture[] birds;
	public int birdX, birdY, birdCount, velocity;
	protected boolean touchedScreen;
	private Circle birdCircle;

	protected Pipe[] pipe;
	public int pipeCount;

	private Music backgroundMusic;
	private Sound wingSoundOnTap, collapseSound, fallingSound;
	private boolean gameStarted, gameOver;
	private BitmapFont font;
	private int score;


	public void create () {
		batch = new SpriteBatch();
		gameStartImage = new Texture(Gdx.files.internal("game_start_screen.png"));
		background = new Texture("background.png");
		ground = new Texture("ground.png");
		gameOverImage = new Texture("game_over.png");

		birds = new Texture[4];
		birds[0] = new Texture("flappy_bird.png");
		birds[1] = new Texture("flappy_bird2.png");
		birds[2] = new Texture("dead_bird.png");
		birds[3] = new Texture("dead_bird2.png");

		SCREEN_WIDTH =Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		BIRD_WIDTH = birds[0].getWidth();
		BIRD_HEIGHT = birds[0].getHeight();
		pipe = new Pipe[3];

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
		backgroundMusic.setLooping(true);

		wingSoundOnTap = Gdx.audio.newSound(Gdx.files.internal("wing.wav"));
		collapseSound = Gdx.audio.newSound(Gdx.files.internal("collapse.wav"));
		fallingSound = Gdx.audio.newSound(Gdx.files.internal("dead.wav"));
		font = new BitmapFont();
		font.getData().setScale(5, 7);
		gameStarted = false;
		startGame();
	}

	/**
	 * starts/restarts the game when players touch screen
	 */
	public void startGame() {
		birdCount = 0;
		pipeCount = 0;
		touchedScreen = true;
		velocity = -30;
		groundX = 0;
		ground2X = SCREEN_WIDTH;
		gameOver = false;
		score = 0;
		birdX = (SCREEN_WIDTH-BIRD_WIDTH)/2;
		birdY = (SCREEN_HEIGHT-BIRD_HEIGHT)/2;

		pipe[0] = new Pipe(SCREEN_WIDTH);
		pipe[1] = new Pipe(SCREEN_WIDTH + Pipe.HORIZONTAL_GAP);
		pipe[2] = new Pipe(SCREEN_WIDTH + (Pipe.HORIZONTAL_GAP * 2));
		birdCircle = new Circle();

		backgroundMusic.play();
	}

	public void render () {
		batch.begin();
		if(!gameStarted) {
			batch.draw(gameStartImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			batch.end();
			if(Gdx.input.justTouched())
				gameStarted = true;
			return;
		}
		batch.draw(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		addGravityOnBird();
		checkIfGameOverOrNot();
		createPipeRecycleView();
		displayPipes();
		font.draw(batch, String.valueOf(score), SCREEN_WIDTH/2f, SCREEN_HEIGHT - 100);
		displayGroundInTheBottom();
		displayBirds();
		batch.end();
	}

	/**
	 * if the player taps on screen the bird gets a push upwards
	 * and then starts falling down. When velocity is negative birdY increases
	 * and when velocity = -->0+, birdY decreases so it starts falling down
	 */
	public void addGravityOnBird() {
		// when player touches the screen
		if (Gdx.input.justTouched() && !gameOver) {
			/* if player touches top of screen the it should be unable to tap again
			during falling */
			velocity = -20;
			wingSoundOnTap.play();
		}
		if (birdY >= 310 || velocity == -20) {
			velocity = velocity + 1;
			birdY = birdY - velocity;
		}else if(birdCount != 3){// when the bird collapse with ground
			// here checking gameOver or not for avoiding repetition
			birdCount = 3;
			collapseSound.play();
			gameOver = true;
		}else if (Gdx.input.justTouched()) {
			startGame();
		}
	}

	private void checkIfGameOverOrNot() {
		if(birdY + 96 >= SCREEN_HEIGHT) {
			birdCount = 2;
			velocity = -velocity;
			gameOver = true;
			collapseSound.play();
			fallingSound.play();
		}
	}

	public void displayPipes() {
		for(Pipe p : pipe) {
			batch.draw(Pipe.pipeTop, p.pipeX, p.topPipeY);
			batch.draw(Pipe.pipeBottom, p.pipeX, p.bottomPipeY);

			p.pipeTopRect.set(p.pipeX, p.topPipeY, Pipe.PIPE_WIDTH, Pipe.PIPE_HEIGHT);
			p.pipeBottomRect.set(p.pipeX, p.bottomPipeY, Pipe.PIPE_WIDTH, Pipe.PIPE_HEIGHT);

			if(!gameOver)
				p.pipeX -= 4;
			if ((Intersector.overlaps(birdCircle, p.pipeTopRect) || Intersector.overlaps(birdCircle, p.pipeBottomRect)) && !gameOver) {
				birdCount = 2;
				gameOver = true;
				collapseSound.play();
				fallingSound.play();
			}
			if(birdX > p.pipeX + Pipe.PIPE_WIDTH && birdX <= p.pipeX + Pipe.PIPE_WIDTH + 4) {
				score++;
			}
		}
		if(gameOver) {
			batch.draw(gameOverImage, (SCREEN_WIDTH - gameOverImage.getWidth()) / 2f, (SCREEN_HEIGHT - gameOverImage.getHeight()) / 2f);
		}
	}

	public void displayBirds() {
		batch.draw(birds[birdCount], birdX, birdY);
		birdCircle.set(birdX + 68, birdY + 48, 40);
		if(birdCount <= 1) {
			birdCount = 1 - birdCount;// one time '0' ont time '1'
		}
	}


	public void displayGroundInTheBottom() {
		// ground
		batch.draw(ground, groundX,0, SCREEN_WIDTH, 300);
		batch.draw(ground, ground2X,0, SCREEN_WIDTH, 300);

		if (groundX + SCREEN_WIDTH <= 0)
			groundX = SCREEN_WIDTH;
		if (ground2X + SCREEN_WIDTH <= 0)
			ground2X = SCREEN_WIDTH;
		if(!gameOver){
			groundX-=4; ground2X-=4;
		}
	}

	/**
	 * this function displays 3 set of pipes in a recycle view, that means
	 * if one set of pipes(top and bottom) goes completely left side of the screen, then it will be placed
	 * at the right side after the last pipe with a random y co-ordinate and leaving a horizontal gap
	 * basically it is displaying three set of pipes again and again.
	 * @see Pipe#randomGenerator()
	 */
	private void createPipeRecycleView() {
			if(pipe[pipeCount].pipeX + Pipe.PIPE_WIDTH <= 0) {
			pipe[pipeCount].randomGenerator(); // sets random value to Pipe.topPipeY, Pipe.bottomPipeY
			// set the pipe after the last remaining pipe with a HORIZONTAL_GAP
			pipe[pipeCount].pipeX = pipe[(pipeCount==0? pipeCount + 2: pipeCount - 1)].pipeX + Pipe.HORIZONTAL_GAP;
			pipeCount = (pipeCount + 1) % 3; // repeats pipeCount value 0,1,2 again and again
		}
	}

	@Override
	public void dispose () {
		background.dispose();
		batch.dispose();
		ground.dispose();
		Pipe.pipeTop.dispose();
		Pipe.pipeBottom.dispose();
		birds[0].dispose();
		birds[1].dispose();
		background.dispose();
	}

	@Override
	public void pause() {
		backgroundMusic.pause();
		collapseSound.stop();
		wingSoundOnTap.stop();
		fallingSound.stop();
	}

	@Override
	public void resume() {
		backgroundMusic.play();
	}
}

