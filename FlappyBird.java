import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;

    int boardHeight = 640;

    Image bottomPipeImg;
    Image birdImg;
    Image topPipeImg;
    Image backgroundImg;
   
    

    int birdWidth = 34;
    int birdHeight = 24;
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
   

    class Bird 
    {
        Image img;
        int width = birdWidth;
        int height = birdHeight;
        int x = birdX;
        int y = birdY;
       

        Bird(Image img) 
        {
            this.img = img;
        }
    }

   
    int pipeHeight = 512;
    int pipeWidth = 64;  
    int pipeX = boardWidth;
    int pipeY = 0;
   
    
    class Pipe 
    {
        int height = pipeHeight;
        int x = pipeX;
        int width = pipeWidth;
        int y = pipeY;
       
       
        Image img;
        boolean passed = false;

        Pipe(Image img) 
        {
            this.img = img;
        }
    }

    int velocityX = -4; 
    int velocityY = 0; 
    int gravity = 1;
    Bird bird;
    

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipeTimer;
    Random random = new Random();

   
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipeTimer = new Timer(1500, new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        }
        );
        placePipeTimer.start();
        
		gameLoop = new Timer(1000/60, this); 
        gameLoop.start();
	}
    
    void placePipes() 
    {

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
    
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
    
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    
    public void paintComponent(Graphics g)
    {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g)
    {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);


        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) 
        {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }


        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) 
        {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else 
        {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}

    public void move() {


        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); 


        for (int i = 0; i < pipes.size(); i++)
        {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) 
            {
                score += 0.5; 
                pipe.passed = true;
            }

            if (collision(bird, pipe))
             {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight)
        {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) 
    {
        return  a.x < b.x + b.width &&   a.x + a.width > b.x &&    a.y < b.y + b.height &&   a.y + a.height > b.y;    
               
    }


    @Override
    public void actionPerformed(ActionEvent e) 
    {
        move();
        repaint();
        if (gameOver) 
        {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) 
        {
            velocityY = -9;

            if (gameOver) 
            {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
