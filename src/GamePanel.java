import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
// Clase que representa el área de juego para la serpiente
class GamePanel extends JPanel implements ActionListener{
    // Constantes para dimensiones, tamaño de unidad, etc.
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 800;
    public static final int UNIT_SIZE = 25;
    //Must be a square window.
    public static final int GAME_UNITS = (int) (SCREEN_WIDTH/UNIT_SIZE) * (SCREEN_HEIGHT/UNIT_SIZE);
    public static final int HORIZONTAL_UNITS = SCREEN_WIDTH/UNIT_SIZE;
    public static final int VERTICAL_UNITS = SCREEN_HEIGHT/UNIT_SIZE;
    public static final int DELAY = 100;
    public static final int INITIAL_SNAKE_SIZE = 6;

    // Variables para el estado del juego, posición de la serpiente, manzana, etc.
    private boolean running = false;
    private int appleX;
    private int appleY;
    private Timer timer = new Timer(DELAY, this);
    private char direction;
    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeSize;
    private int applesEaten;
	SnakeFrame parentFrame;
    boolean keyInput = false;
	private int lowestScore;
	private ArrayList<Score> scoreList = new ArrayList<Score>();
	private boolean showJTextField = false;
	private String playerName = "";
	String[] gameOverMessages = {"suerte la proxima!", "Lo sentimos Snakey!", "Dale que se puede!", "GG WP", "Hora de mudar", "Buen teclado", "Ow :(", "Uhh eso dolio!", "Que tristeza"};
	String randomGameOverMessage = "";
	private Score actualScore;

    // Constructor que configura el panel de juego
    GamePanel(JFrame frame){
		parentFrame = (SnakeFrame) frame;

        //Si el panel no es focuseable, no va a escuchar las teclas
        this.setFocusable(true);
		this.requestFocus();
        this.addKeyListener(new MyKeyAdapter());
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);

    }
    // Método que inicializa el juego
    public void startGame(){
        snakeSize = INITIAL_SNAKE_SIZE;
        applesEaten = 0;
        for(int i = 0; i < snakeSize; i++){
            snakeX[i] = 0;
            snakeY[i] = 0;
        }
        direction = 'R';
        timer.start();
        newApple();
        System.out.println("Initialized game panel startGame()");
		loadScoreList();
		loadLowestScore();
		randomGameOverMessage = gameOverMessages[random(gameOverMessages.length)];
    }
    // Otros métodos para movimiento, colisiones, dibujo, etc
	public void loadScoreList(){
		try{
			scoreList.clear();
			BufferedReader buffer = new BufferedReader(new FileReader(new File("scores.data")));
			String line;
			String[] nameScore;
			Score aux;
			while((line = buffer.readLine()) != null){
				nameScore = line.split(",");
				aux = new Score(nameScore[0], Integer.parseInt(nameScore[1]));
				scoreList.add(aux);
			}
			System.out.println("ArrayList loaded successfully");
			System.out.println(scoreList);
		}catch(Exception ex){
			System.out.println("Error trying to read file");
		}
	}

	public void loadLowestScore(){
		//vamos a sortear una vez por las dudas. sort() SORTEA DE MENOR A MAYOR
		scoreList.sort(Comparator.reverseOrder());
		lowestScore = scoreList.get(9).getScore();
		System.out.println("lowestScore: " + lowestScore);
	}



    public void actionPerformed(ActionEvent ev){
        move();
        checkCollision();
        eatApple();
        repaint();
    }


    // Método para obtener un color dinámico
    private Color getDynamicColor() {
        float hue = (System.currentTimeMillis() % 10000) / 10000f; // Cambia con el tiempo
        return Color.getHSBColor(hue, 0.8f, 0.8f); // Saturación y luminosidad constantes
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        //dibuja la manzana x  la manzana y
        g.setColor(Color.red);
        g.fillOval(appleX , appleY, UNIT_SIZE, UNIT_SIZE);
        //dibuja la cabeza de la serpiente
        g.setColor(getDynamicColor());
        g.fillRect(snakeX[0], snakeY[0], UNIT_SIZE, UNIT_SIZE);
        //cuerpo de la serpiente
        for(int i = 1; i < snakeSize; i++){
            g.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
        }

        //cadena de puntuacion
        g.setColor(Color.white);
        g.setFont(new Font("Mendeh", Font.PLAIN, 25));
        FontMetrics fontSize = g.getFontMetrics();
        int fontX = SCREEN_WIDTH - fontSize.stringWidth("Puntaje: " + applesEaten) - 10;
        int fontY = fontSize.getHeight();
        g.drawString("Puntaje: " + applesEaten, fontX, fontY);

        if(!timer.isRunning()){
            //print game over
            g.setColor(Color.white);
            g.setFont(new Font("Mendeh", Font.PLAIN, 58));

            String message = randomGameOverMessage;
            fontSize = g.getFontMetrics();
            fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2 ;
            fontY = (SCREEN_HEIGHT - fontSize.getHeight()) /2;
            g.drawString(message, fontX, fontY);

            g.setFont(new Font("Mendeh", Font.PLAIN, 24));
            message = "Presiona F2 para reiniciar";
            fontSize = g.getFontMetrics();
            fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2 ;
            fontY = fontY + fontSize.getHeight() + 20;
            g.drawString(message, fontX, fontY);

			if(showJTextField){
				drawJTextField(g);
				drawPlayerName(g);

			}

        }
    }

	public void drawJTextField(Graphics g){
		g.setFont(new Font("Mendeh", Font.PLAIN, 24));
		String message = "Ingresa tu nombre:";
		FontMetrics fontSize = g.getFontMetrics();
		//Horizontal center
		int fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2 ;
		g.drawString(message, fontX, 350);
	}

	public void drawPlayerName(Graphics g){
		g.setFont(new Font("Mendeh", Font.PLAIN, 24));
		FontMetrics fontSize = g.getFontMetrics();
		//Horizontal center
		int fontX = (SCREEN_WIDTH - fontSize.stringWidth(playerName)) / 2 ;
		g.drawString(playerName, fontX, 400);
	}



    public void newApple(){
        //numero random entre 0 y 23 * unit size
        int x = random(HORIZONTAL_UNITS) * UNIT_SIZE;
        int y = random(VERTICAL_UNITS) * UNIT_SIZE;
        Point provisional = new Point(x,y);
        Point snakePos = new Point();
        boolean newApplePermission = true;
        for(int i = 0; i < snakeSize; i++){
            snakePos.setLocation(snakeX[i], snakeY[i]);
            if(provisional.equals(snakePos)){
                newApplePermission = false;
            }
        }

        if(newApplePermission){
            appleX = x;
            appleY = y;
        }else{
            newApple();
        }
    }

    public void checkCollision(){
        if(snakeX[0] >= (SCREEN_WIDTH) || snakeX[0] < 0 || snakeY[0] >= (SCREEN_HEIGHT) || snakeY[0] < 0){
            gameOver();
        }
        for(int i = 1; i < snakeSize; i++){
            if((snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])){
                gameOver();
            }
        }
    }

	public void eatApple(){
        if(snakeX[0] == appleX && snakeY[0] == appleY){
            snakeSize++;
            applesEaten++;
            newApple();
        }
    }

	public void move(){
        //Este metodo se ejecuta cada vez que timer nos lo permite
        //Hay que recorrer la serpiente de atras para adelante
        for(int i = snakeSize; i > 0; i--){
            snakeX[i] = snakeX[i-1];
            snakeY[i] = snakeY[i-1];
        }

        switch(direction){
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
        }

        keyInput = false;
    }

	public void gameOver(){
        timer.stop();
		if(applesEaten > lowestScore){
			showJTextField = true;
		}

    }


    public int random(int range){
        //devuelve un int de rango 0
        return (int)(Math.random() * range);
    }

    // Clase interna para manejar eventos de teclado
    class MyKeyAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent k){

            switch(k.getKeyCode()){
                case (KeyEvent.VK_DOWN):
                    if(direction != 'U' && keyInput == false){
                        direction = 'D';
                        keyInput = true;
                    }
                    break;
                case (KeyEvent.VK_UP):
                    if(direction != 'D' && !keyInput){
                        direction = 'U';
                        keyInput = true;
                    }
                    break;
                case (KeyEvent.VK_LEFT):
                    if(direction != 'R' && keyInput == false){
                        direction = 'L';
                        keyInput = true;
                    }
                    break;
                case (KeyEvent.VK_RIGHT):
                    if(direction != 'L' && keyInput == false){
                        direction = 'R';
                        keyInput = true;
                    }
                    break;
                case (KeyEvent.VK_F2):
                    if(!timer.isRunning()){
                        startGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
					parentFrame.switchToLobbyPanel();
					break;
            }

			if(showJTextField){
				if(k.getKeyCode() == KeyEvent.VK_ENTER){
					actualScore = new Score(playerName, applesEaten);
					scoreList.add(actualScore);
                    playerName = "";
					sortAndSave();
					showJTextField = false;
                    parentFrame.switchToLobbyPanel();
				}else if(k.getKeyCode() == KeyEvent.VK_BACK_SPACE && playerName.length() > 0){
					StringBuilder sb = new StringBuilder(playerName);
					sb.deleteCharAt(sb.length() - 1);
					playerName = sb.toString();
				}
				else{
					if(!k.isActionKey() && k.getKeyCode() != KeyEvent.VK_SHIFT && k.getKeyCode() != KeyEvent.VK_BACK_SPACE){
						playerName = playerName + k.getKeyChar();
					}
				}

				repaint();
			}

        }
    }

    // Otros métodos privados para carga de puntajes, manejo de colisiones, etc.
	public void sortAndSave(){
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("scores.data")));
			scoreList.sort(Comparator.reverseOrder());
			for(int i = 0; i < 10; i++){
				Score element = scoreList.get(i);
				bw.write(element.name + "," + String.valueOf(element.score) + "\n");

			}
			bw.flush();
		}catch(IOException ex){
			System.out.println("Error writing file");
		}

	}

    public void sleep(int millis){
        try{
            Thread.sleep(millis);
            System.out.println("Slept");
        }catch(Exception ex){
            System.out.println("Fatal Error in sleep() method");
        }
    }

}