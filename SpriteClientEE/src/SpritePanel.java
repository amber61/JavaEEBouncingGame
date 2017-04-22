/* File: SpritePanel.java
 * Course Name: CST8277_300
 * Student Name: Zhe Huang, Shunbiao Lin, Yingmei Zhu, Fasheng Zhang
 * Professor: Stanley Pieda
 * Reference: revised based on the starter code provided by Stanley Pieda
 * Description: The panel to display the moving sprites
 * Date: April 15, 2017
 */
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JPanel;

import business.Sprite;
import business.SpriteSessionRemote;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 * Modified by Shunbiao Lin, Yingmei Zhu, Fasheng Zhang 
 */
public class SpritePanel extends JPanel implements Runnable {

    List<Sprite> sprites;
    SpriteSessionRemote session;

    public SpritePanel(SpriteSessionRemote session) {
        this.session = session;
        this.addMouseListener(new Mouse());
    }
    /**
     * The thread run method to keep drawing the panel and the moving sprites
     * Modified by Fasheng Zhang using the instruction from professor - Stanley Pieda
     * 
     */
    @Override
    public void run() {
        System.out.println("now animating..."); //try{
        while (true) {
            try {
                sprites = session.getSpriteList();
            } catch (Exception e) { //catch(javax.ejb.NoSuchEJBException e)
                System.out.println("Lost contact with server, exiting...");
                System.exit(1);
            }
            repaint();
            //sleep while waiting to display the next frame of the animation
            try {
                Thread.sleep(200); // wake up roughly 25 frames per second
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        } // }catch(RemoteException e){ // System.out.println("game over? exiting..."); // }
    }

    private class Mouse extends MouseAdapter {

        /**
        * Modified by Yingmei Zhu,Shunbaio Lin, Fasheng Zhang
        * 
        * Monitor the event of mouse clicking event.        
        * Pause the sprite if the position clicked is within a rectangle of a sprite, open the GUI for editing sprite,
        * else create a new sprite
        *  
        * @param MouseEvent  
        */
        @Override
        public void mousePressed(final MouseEvent event) {
            Sprite clicked;
            try {
                clicked = spriteClicked(event.getX(), event.getY());
                if (clicked != null) {
                    System.out.println("Sprite clicked");
                    int preDx = clicked.getDx();
                    int preDy = clicked.getDy();
                    //when a sprite was clicked, pause the sprite by set its dx and dy to 0
                    clicked.setDx(0);
                    clicked.setDy(0);
                    session.editSprite(clicked);
                    openPanel(clicked, preDx, preDy);
                } else {
                    System.out.println("Creating a new sprite");
                    session.newSprite(event);
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sprites != null) {
            for (Sprite s : sprites) {
                s.draw(g);
            }
        }
    }
    /**
     * Created by Yingmei Zhu
     * When the mouse pressed, return the sprite if position clicked is within the rectangle of a sprite,
     * else return null
     *  
     * @param X-Y of mouse clicked 
     * @return Sprite
     * 
     */    
    private Sprite spriteClicked(int x, int y) {
        int size = 20;
        Shape circle;
        // for (int i = 0; i < sprites.size(); i++){
        for (Sprite sprite : sprites) {
            // Define a Circle object to check if the mouse click is inside the Circle
            circle = new Ellipse2D.Double(sprite.getX(), sprite.getY(), size, size);
            //Tests if the specified coordinates are inside the boundary of the circle
            if (circle.contains(x, y)) {
                return sprite;
            }
        }
        return null;
    }

    /**
     * Created by Shunbiao Lin, Fasheng Zhang
     * 
     * When a sprite was clicked, this method will be invoked to display the GUI for sprite editing
     *   
     * @param sprite paused
     * @param X of sprite paused      
     * @param Y of sprite paused
     * @throws RemoteException     * 
     */
    private void openPanel(Sprite sprite, int dx, int dy) throws RemoteException {

        JPanel editPanel = new JPanel(new GridLayout(8, 1));
        editPanel.setSize(400, 400);
        JPanel xPanel = new JPanel();
        JPanel yPanel = new JPanel();
        JPanel dXPanel = new JPanel();
        JPanel dYPanel = new JPanel();
        JPanel cLabelPanel = new JPanel();
        JPanel rPanel = new JPanel();
        JPanel gPanel = new JPanel();
        JPanel bPanel = new JPanel();

        JTextField xField = new JTextField(Integer.toString(sprite.getX()));
        xField.setPreferredSize(new Dimension(80, 30));
        JTextField yField = new JTextField(Integer.toString(sprite.getY()));
        yField.setPreferredSize(new Dimension(80, 30));
        JTextField dxField = new JTextField(Integer.toString(sprite.getDx()));
        dxField.setPreferredSize(new Dimension(80, 30));
        JTextField dyField = new JTextField(Integer.toString(sprite.getDy()));
        dyField.setPreferredSize(new Dimension(80, 30));

        JTextField redColorField = new JTextField(sprite.getColor().getRed() + "");
        redColorField.setPreferredSize(new Dimension(80, 30));
        JTextField greenColorField = new JTextField(sprite.getColor().getGreen() + "");
        greenColorField.setPreferredSize(new Dimension(80, 30));
        JTextField blueColorField = new JTextField(sprite.getColor().getBlue() + "");
        blueColorField.setPreferredSize(new Dimension(80, 30));

        xPanel.add(new JLabel("x:"));
        xPanel.add(xField);
        yPanel.add(new JLabel("y:"));
        yPanel.add(yField);
        dXPanel.add(new JLabel("Dx:"));
        dXPanel.add(dxField);
        dYPanel.add(new JLabel("Dy:"));
        dYPanel.add(dyField);
        cLabelPanel.add(new JLabel("Color:"));
        rPanel.add(new JLabel("R"));
        rPanel.add(redColorField);
        gPanel.add(new JLabel("G"));
        gPanel.add(greenColorField);
        bPanel.add(new JLabel("B"));
        bPanel.add(blueColorField);

        editPanel.add(xPanel);
        editPanel.add(yPanel);
        editPanel.add(dXPanel);
        editPanel.add(dYPanel);
        editPanel.add(cLabelPanel);
        editPanel.add(rPanel);
        editPanel.add(gPanel);
        editPanel.add(bPanel);

        int option = JOptionPane.showConfirmDialog(null, editPanel,
                "Are you sure?", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                sprite.setDx(Integer.valueOf(dxField.getText().trim()));
                sprite.setDy(Integer.valueOf(dyField.getText().trim()));
                sprite.setX(Integer.valueOf(xField.getText().trim()));
                sprite.setY(Integer.valueOf(yField.getText().trim()));
                sprite.setColor(new Color(Integer.valueOf(redColorField.getText().trim()),
                        Integer.valueOf(greenColorField.getText().trim()),
                        Integer.valueOf(blueColorField.getText().trim())));
                //when user clicked OK button, edit hte sprite
                session.editSprite(sprite);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //when user clcked NO button, reset the sprite to previous state
            sprite.setDx(dx);
            sprite.setDy(dy);
            session.editSprite(sprite);
        }

    }

}
