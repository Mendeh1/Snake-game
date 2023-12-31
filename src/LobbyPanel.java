import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
// Clase que representa el panel del lobby o menú principal
class LobbyPanel extends JPanel{
    public static final String TITLE_MESSAGE = "Snake RGB";
    public static final Font TITLE_FONT = new Font("Playball", 0 , 62);
    public static final Font MENU_FONT = new Font("Arial", 0 , 28);
    public static final Font CREATOR_FONT = new Font("Arial", 0 , 14);
    public static final int SCREEN_WIDTH = GamePanel.SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT = GamePanel.SCREEN_HEIGHT;
    public static final String[] MENU_ITEMS = {"Jugar", "Mejores Snakeys", "Salir"};
    private int selectedMenuItem = 0;
	SnakeFrame parentFrame;

    // Constructor que configura el panel del lobby
    public LobbyPanel(JFrame frame){
		parentFrame = (SnakeFrame) frame;
        this.addKeyListener(new MyKeyAdapter());
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        //Lo hacemos focusable para que tome teclas
        this.setFocusable(true);
		this.requestFocus();
    }
    //métodos para dibujar elementos del lobby y manejar eventos de teclado
    public void paintComponent(Graphics g){
        //Tenemos que llamar al paintComponent() de super para no perder el background color seteado en JPanel.
        super.paintComponent(g);
        drawTitle(g);
        drawMenu(g);
        drawCreator(g);
    }

    private void drawTitle(Graphics g){
        g.setColor(Color.white);
        g.setFont(TITLE_FONT);

        FontMetrics metrics = g.getFontMetrics();
        int x = (SCREEN_WIDTH - metrics.stringWidth(TITLE_MESSAGE)) / 2;
        int y = metrics.getHeight() + 100;

        g.drawString(TITLE_MESSAGE, x, y);
    }

    private void drawMenu(Graphics g){
        g.setColor(Color.white);
        g.setFont(MENU_FONT);

        FontMetrics metrics = g.getFontMetrics();
        for(int i = 0; i < MENU_ITEMS.length; i++){
            int x = (SCREEN_WIDTH - metrics.stringWidth(MENU_ITEMS[i])) / 2; //Horizontal center
            int y = metrics.getHeight() + 300 + (i * (metrics.getHeight() + 20));
            g.drawString(MENU_ITEMS[i], x, y);
            if (selectedMenuItem == i){
                drawTriangle(x - 30, y - 20, g);
            }
        }
    }

    private void drawTriangle(int x, int y, Graphics g){
        g.setColor(Color.white);
        int[] xPoints = {x, x + 20, x};
        int[] yPoints = {y, y+10, y+20};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawCreator(Graphics g){
        g.setColor(Color.white);
        g.setFont(CREATOR_FONT);

        FontMetrics metrics = g.getFontMetrics();

        int y = SCREEN_WIDTH - metrics.getHeight();


    }
    // Clase interna para manejar eventos de teclado
    private class MyKeyAdapter extends KeyAdapter{
        // Método llamado cuando se presiona una tecla
        public void keyPressed(KeyEvent e){
            // Lógica para manejar las teclas presionadas en el lobby
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    decrementMenu();
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    incrementMenu();
                    repaint();
                    break;
                case KeyEvent.VK_ENTER:
                    switchPanels();
					break;
				case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
				
            }
            
        }
    }
    // métodos privados para dibujar y gestionar el menú del lobby
    private void switchPanels(){
        switch(selectedMenuItem){
            case 0:
                parentFrame.switchToGamePanel();
                break;
            case 1:
				parentFrame.switchToLeaderboardPanel();
                break;
            case 2:
                System.exit(0);
                break;
        }
    }

    private void print(String m){
        System.out.println(m);
    }

    private void incrementMenu(){

        int lastItemIndex = MENU_ITEMS.length - 1;
        if(selectedMenuItem < lastItemIndex){ //3
            selectedMenuItem++;
        }else{
            selectedMenuItem = 0;
        }
    }

    private void decrementMenu(){
        int lastItemIndex = MENU_ITEMS.length - 1;
        if(selectedMenuItem > 0){
            selectedMenuItem--;
        }else{
            selectedMenuItem = lastItemIndex;
        }
    }

    public int getSelectedMenuItem(){
        return selectedMenuItem;
    }

}