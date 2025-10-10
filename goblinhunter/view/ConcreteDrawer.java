package goblinhunter.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ConcreteDrawer extends AbstractDrawer{
    private Rectangle2D.Double rect;


    public ConcreteDrawer() {
        this.rect = new Rectangle2D.Double(130.45, 100.56, 624, 528);
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.red);
        g2d.fill(rect);
        g2d.setColor(Color.black);
        g2d.draw(rect);
    }

    @Override
    public int getDrawingWidth() {
        return ((int)Math.round(rect.getX() + rect.getWidth() + 0.5));
    }

    @Override
    public int getDrawingHeight() {
        return ((int)Math.round(rect.getY() + rect.getHeight() + 0.5));
    }
    /*
    public int getDrawingWidth() {

    }
    public int getDrawingHeight(){

    }

    public void updateDrawing() {

    }*/
}
