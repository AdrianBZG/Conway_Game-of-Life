/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import exceptionhandlers.GameShapeException;
import framework.GameCellGrid;
import models.GameCell;
import models.GameShape;

/**
 * Subclass of Canvas, which makes the cellgrid visible.
 * Communicates via CellGrid interface.
 */
public class GameCellGridCanvas extends Canvas {
  private boolean cellUnderMouse;
  /**
   * Image for double buffering, to prevent flickering.
   */
  private Image offScreenImage;
  private Graphics offScreenGraphics;
  private Image offScreenImageDrawed;
  /**
   * Image, containing the drawed grid.
   */
  private Graphics offScreenGraphicsDrawed;
  private int cellSize;
  private GameCellGrid gameCellGrid;
  private int newCellSize;
  private GameShape newShape;

  /**
   * Constructs a CellGridCanvas.
   * @param gameCellGrid the GoL cellgrid
   * @param cellSize size of cell in pixels
   */
  public GameCellGridCanvas(GameCellGrid gameCellGrid, int cellSize) {
    this.gameCellGrid = gameCellGrid;
    this.cellSize = cellSize;

    setBackground(Color.GRAY);

    addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            draw(e.getX(), e.getY());
          }
          public void mousePressed(MouseEvent e) {
            System.out.println("x: " + e.getX() + ", y: " + e.getY());
            saveCellUnderMouse(e.getX(), e.getY());
          }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        draw(e.getX(), e.getY());
      }
    });
    
    addComponentListener( 
        new ComponentListener() {
          public void componentResized(ComponentEvent e) {
            resized();
            repaint();
          }
          public void componentMoved(ComponentEvent e) {}
          public void componentHidden(ComponentEvent e) {}
          public void componentShown(ComponentEvent e) {}
        }
        );

  }

  /**
   * Set cell size (zoom factor)
   * @param cellSize Size of cell in pixels
   */
  public void setCellSize( int cellSize ) {
    this.cellSize = cellSize;
    resized();
    repaint();
  }

  /**
   * The grid is resized (by window resize or zooming).
   * Also apply post-resize properties when necessary
   */
  public void resized() {
    if ( newCellSize != 0 ) {
      cellSize = newCellSize;
      newCellSize = 0;
    }
    Dimension canvasDim = getSize();
    offScreenImage = null;
    offScreenImageDrawed = null;
    gameCellGrid.resize(canvasDim.width/cellSize, canvasDim.height/cellSize);
    if ( newShape != null ) {
      try {
        setShape( newShape );
      } catch (GameShapeException e) {
        // ignore
      }
    }

  }

  /**
   * Remember state of cell for drawing.
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void saveCellUnderMouse(int x, int y) {
    try {
      cellUnderMouse = gameCellGrid.getCell(x / cellSize, y / cellSize);
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
  }

  /**
   * Draw in this cell.
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void draw(int x, int y) {
    try {
      gameCellGrid.setCell(x / cellSize, y / cellSize, !cellUnderMouse );
      repaint();
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
  }
  
  /**
   * Draw in this cell.
   * 
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public void drawCell(int x, int y) {
    try {
      gameCellGrid.setCell(x, y, true);
      repaint();
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
  }

  /** 
   * Use double buffering.
   * @see java.awt.Component#update(java.awt.Graphics)
   */
  public void update(Graphics g) {
    Dimension d = getSize();
    if ((offScreenImage == null)) {
      offScreenImage = createImage(d.width, d.height);
      offScreenGraphics = offScreenImage.getGraphics();
    }
    paint(offScreenGraphics);
    g.drawImage(offScreenImage, 0, 0, null);
  }

  /**
   * Draw this generation.
   * @see java.awt.Component#paint(java.awt.Graphics)
   */
  public void paint(Graphics g) {
    // Draw grid on background image, which is faster
    if (offScreenImageDrawed == null) {
      Dimension dim = gameCellGrid.getDimension();
      Dimension d = getSize();
      offScreenImageDrawed = createImage(d.width, d.height);
      offScreenGraphicsDrawed = offScreenImageDrawed.getGraphics();
      // draw background (MSIE doesn't do that)
      offScreenGraphicsDrawed.setColor(getBackground());
      offScreenGraphicsDrawed.fillRect(0, 0, d.width, d.height);
      offScreenGraphicsDrawed.setColor(Color.WHITE);
      offScreenGraphicsDrawed.fillRect(0, 0, cellSize * dim.width - 1, cellSize * dim.height - 1);
      offScreenGraphicsDrawed.setColor(getBackground());
      for (int x = 1; x < dim.width; x++) {
        offScreenGraphicsDrawed.drawLine(x * cellSize - 1, 0, x * cellSize - 1, cellSize * dim.height - 1);
      }
      for (int y = 1; y < dim.height; y++) {
        offScreenGraphicsDrawed.drawLine( 0, y * cellSize - 1, cellSize * dim.width - 1, y * cellSize - 1);
      }
    }
    g.drawImage(offScreenImageDrawed, 0, 0, null);
    // draw populated cells
    g.setColor(Color.BLACK);
    @SuppressWarnings("rawtypes")
    Enumeration testEnum = gameCellGrid.getEnum();
    GameCell c;
    while ( testEnum.hasMoreElements() ) {
      c = (GameCell)testEnum.nextElement();
      g.fillRect(c.col * cellSize, c.row * cellSize, cellSize - 1, cellSize - 1);
    }
  }

  /**
   * This is the preferred size.
   * @see java.awt.Component#getPreferredSize()
   */
  public Dimension getPreferredSize() {
    Dimension dim = gameCellGrid.getDimension();
    return new Dimension( cellSize * dim.width,  cellSize * dim.height );
  }

  /**
   * This is the minimum size (size of one cell).
   * @see java.awt.Component#getMinimumSize()
   */
  public Dimension getMinimumSize() {
    return new Dimension( cellSize,  cellSize );
  }

  /**
   * Settings to appy after a window-resize.
   * @param newShape new shape
   * @param newCellSize new cellSize
   */
  public void setAfterWindowResize( GameShape newShape, int newCellSize ) {
    this.newShape = newShape;
    this.newCellSize = newCellSize;
  }

  /**
   * Draws shape in grid.
   * 
   * @param gameShape name of shape
   * @return true when shape fits, false otherwise
   * @throws GameShapeException if the shape does not fit on the canvas
   */
  public synchronized void setShape(GameShape gameShape) throws GameShapeException {
    int xOffset;
    int yOffset;
    Dimension dimShape;
    Dimension dimGrid;
    // get shape properties
    //shapeGrid = shape.getShape();
    dimShape =  gameShape.getDimension();
    dimGrid =  gameCellGrid.getDimension();

    if (dimShape.width > dimGrid.width || dimShape.height > dimGrid.height)
      throw new GameShapeException( "Shape doesn't fit on canvas (grid: "+dimGrid.width+"x"+dimGrid.height+", shape: "+dimShape.width+"x"+dimShape.height+")"); // shape doesn't fit on canvas

    // center the shape
    xOffset = (dimGrid.width - dimShape.width) / 2;
    yOffset = (dimGrid.height - dimShape.height) / 2;
    gameCellGrid.clear();

    // draw shape
    @SuppressWarnings("rawtypes")
    Enumeration cells = gameShape.getCells();
    while (cells.hasMoreElements()) {
      int[] cell = (int[]) cells.nextElement();
      gameCellGrid.setCell(xOffset + cell[0], yOffset + cell[1], true);
    }

  }
}