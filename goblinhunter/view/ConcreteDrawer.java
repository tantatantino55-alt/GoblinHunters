package goblinhunter.view;

import goblinhunter.controller.ControllerForView;
import goblinhunter.utils.Config;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ConcreteDrawer extends AbstractDrawer{
    private Rectangle2D.Double rect;

    public ConcreteDrawer() {
        this.rect = new Rectangle2D.Double(Config.GRID_OFFSET_X, Config.GRID_OFFSET_Y, Config.GAME_PANEL_WIDTH, Config.GAME_PANEL_HEIGHT  );
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.red);
        g2d.fill(rect);
        g2d.setColor(Color.black);
        g2d.draw(rect);


        g2d.setColor(Color.BLUE);
        g2d.fillRect(ControllerForView.getInstance().XCoordinatePlayer(), ControllerForView.getInstance().yCoordinatePlayer(), Config.TILE_SIZE,Config.TILE_SIZE);
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
