/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import eventhandlers.GameControlsEvent;
import exceptionhandlers.GameShapeException;
import framework.GameControlsListener;
import logic.GameController;
import models.GameShape;
import models.GameShapeCollection;

/**
 * The Game Of Life view.
 * This is the heart of the program. It initializes everything and put it together.
 */
public class GameView extends Applet implements Runnable, GameControlsListener {
  protected GameCellGridCanvas gameOfLifeCanvas;
  protected GameController gameController;
  protected int cellSize;
  protected int cellCols;
  protected int cellRows;
  protected int genTime;
  protected GameControls controls;
  protected static Thread gameThread = null;
  private ArrayList<Point> centroids = new ArrayList<Point>();

  /**
   * Initialize the applet UI
   */
  public void init() {
    getParams();

    // set background colour
    setBackground(Color.GRAY);

    // create gameOfLifeGrid
    gameController = new GameController(cellCols, cellRows);
    gameController.clear();

    // create GameOfLifeCanvas
    gameOfLifeCanvas = new GameCellGridCanvas(gameController, cellSize);

    // create GameOfLifeControls
    controls = new GameControls();
    controls.addGameOfLifeControlsListener( this );

    // put it all together
    setLayout(new BorderLayout());

    add(gameOfLifeCanvas, BorderLayout.CENTER);
    add(controls, BorderLayout.NORTH);

    setVisible(true);
    validate();
  }

  /**
   * Get params (cellSize, cellCols, cellRows, genTime) from applet-tag
   */
  protected void getParams() {
    final int DEFAULT_CELL_SIZE = 11;
    final int DEFAULT_CELL_COLUMNS = 50;
    final int DEFAULT_CELL_ROWS = 30;
    final int DEFAULT_GENERATION_TIME = 1000;

    cellSize = DEFAULT_CELL_SIZE;
    cellCols = DEFAULT_CELL_COLUMNS;
    cellRows = DEFAULT_CELL_ROWS;
    genTime  = DEFAULT_GENERATION_TIME;
  }

  /**
   * Read applet parameter (int) or, when unavailable, get default value.
   * @param name name of parameter
   * @param defaultParam default when parameter is unavailable
   * @return value of parameter
   */
  protected int getParamInteger( String name, int defaultParam ) {
    String param;
    int paramInt;

    param = getParameter( name );
    if ( param == null )
      paramInt = defaultParam;
    else
      paramInt = Integer.valueOf(param).intValue();
    return paramInt;
  }

  /**
   * Starts the game immediately creating new generations
   */
  public synchronized void startImmediately() {
    controls.start();
    if (gameThread == null) {
      gameThread = new Thread(this);
      gameThread.start();
    }
  }

  public void stop() {
    controls.stop();
    gameThread = null;
  }

  public synchronized void run() {
    while (gameThread != null) {
      nextGeneration();
      try {
        Thread.sleep(genTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Is the applet running?
   * @return true: applet is running
   */
  public boolean isRunning() {
    return gameThread != null;
  }

  /**
   * Go to the next generation.
   */
  public void nextGeneration() {
    gameController.next();
    gameOfLifeCanvas.repaint();
    showGenerations();
  }

  /**
   * Set the new shape
   * @param gameShape name of shape
   */
  public void setShape( GameShape gameShape ) {
    if ( gameShape == null )
      return;

    try {
      gameOfLifeCanvas.setShape( gameShape );
      reset();
    } catch (GameShapeException e) {
      alert( e.getMessage() );
    }
  }

  /**
   * Resets applet (after loading new shape)
   */
  public void reset() {
    stop(); // might otherwise confuse user
    gameOfLifeCanvas.repaint();
    showGenerations();
  }

  /**
   * Show number of generations.
   */
  private void showGenerations() {
    controls.setGeneration( gameController.getGenerations() );
  }

  /**
   * Set speed of new generations.
   * @param fps generations per second
   */
  public void setSpeed( int fps ) {
    genTime = fps;
  }

  /**
   * Sets cell size.
   * @param p size of cell in pixels
   */
  public void setCellSize( int p ) {
    cellSize = p;
    gameOfLifeCanvas.setCellSize( cellSize );
  }

  /**
   * Gets cell size.
   * @return size of cell
   */
  public int getCellSize() {
    return cellSize;
  }

  /**
   * Shows an alert
   * @param s text to show
   */
  public void alert( String s ) {
    showStatus( s );
  }

  /** Callback from GameOfLifeControlsListener
   * @see framework.GameControlsListener.gameoflife.GameOfLifeControlsListener#startStopButtonClicked(GameControlsEvent.bitstorm.gameoflife.GameOfLifeControlsEvent)
   */
  public void startStopButtonClicked( GameControlsEvent e ) {
    if ( isRunning() ) {
      stop();
    } else {
      startImmediately();
    }
  }

  /** Callback from GameOfLifeControlsListener
   * @see framework.GameControlsListener.gameoflife.GameOfLifeControlsListener#nextButtonClicked(GameControlsEvent.bitstorm.gameoflife.GameOfLifeControlsEvent)
   */
  public void nextButtonClicked(GameControlsEvent e) {
    nextGeneration();
  }

  /** Callback from GameOfLifeControlsListener
   * @see framework.GameControlsListener.gameoflife.GameOfLifeControlsListener#speedChanged(GameControlsEvent.bitstorm.gameoflife.GameOfLifeControlsEvent)
   */
  public void speedChanged(GameControlsEvent e) {
    setSpeed( e.getSpeed() );
  }

  /** Callback from GameOfLifeControlsListener
   * @see framework.GameControlsListener.gameoflife.GameOfLifeControlsListener#speedChanged(GameControlsEvent.bitstorm.gameoflife.GameOfLifeControlsEvent)
   */
  public void zoomChanged(GameControlsEvent e) {
    setCellSize( e.getZoom() );
  }

  /** Callback from GameOfLifeControlsListener
   * @see framework.GameControlsListener.gameoflife.GameOfLifeControlsListener#shapeSelected(GameControlsEvent.bitstorm.gameoflife.GameOfLifeControlsEvent)
   */
  public void shapeSelected(GameControlsEvent e) {
    String shapeName = (String) e.getShapeName();
    GameShape gameShape;
    try {
      gameShape = GameShapeCollection.getShapeByName( shapeName );
      if(shapeName != "Clear") {
        for(int i = 0; i < Integer.parseInt(controls.getRepeatTextField().getText()); i ++) {
        ArrayList<Point> randomPoints = getRandomPointsInCanvas(shapeName);
        for(Point point : randomPoints) {
          gameOfLifeCanvas.drawCell(point.x, point.y);
        }
        }
      } else {
        setShape( gameShape );
      }
    } catch (GameShapeException e1) {
      // Ignore. Not going to happen.
    }
  }

  private ArrayList<Point> getRandomPointsInCanvas(String shapeName) {
    Random randomGenerator = new Random();
    final String BLINKER_SHAPE_NAME = "Blinker";
    final String GLIDER_SHAPE_NAME = "Glider";
    final String DIEHARD_SHAPE_NAME = "Diehard";
    final int MAX_SIZE_X = 100;
    final int MAX_SIZE_Y = 49;
    
    int shapeXPosition = randomGenerator.nextInt(MAX_SIZE_X);
    int shapeYPosition = randomGenerator.nextInt(MAX_SIZE_Y);
    /*while(shapeColliding(new Point(shapeXPosition, shapeYPosition))) {
      shapeXPosition = randomGenerator.nextInt(MAX_SIZE_X);
      shapeYPosition = randomGenerator.nextInt(MAX_SIZE_Y);
    }*/
    centroids.add(new Point(shapeXPosition, shapeYPosition));

    ArrayList<Point> pointsToReturn = new ArrayList<Point>();

    if(shapeName == BLINKER_SHAPE_NAME) {
      pointsToReturn.add(new Point(shapeXPosition, shapeYPosition));
      pointsToReturn.add(new Point(shapeXPosition + 1, shapeYPosition)); 
      pointsToReturn.add(new Point(shapeXPosition + 2, shapeYPosition)); 
    } else if(shapeName == GLIDER_SHAPE_NAME) {
      pointsToReturn.add(new Point(shapeXPosition, shapeYPosition));
      pointsToReturn.add(new Point(shapeXPosition + 1, shapeYPosition - 1)); 
      pointsToReturn.add(new Point(shapeXPosition + 2, shapeYPosition - 1)); 
      pointsToReturn.add(new Point(shapeXPosition, shapeYPosition - 2)); 
      pointsToReturn.add(new Point(shapeXPosition + 1, shapeYPosition - 2)); 
    } else if(shapeName == DIEHARD_SHAPE_NAME) {            
      pointsToReturn.add(new Point(shapeXPosition, shapeYPosition)); 
      pointsToReturn.add(new Point(shapeXPosition - 1, shapeYPosition)); 
      pointsToReturn.add(new Point(shapeXPosition, shapeYPosition + 1));      
      pointsToReturn.add(new Point(shapeXPosition + 4, shapeYPosition + 1)); 
      pointsToReturn.add(new Point(shapeXPosition + 5, shapeYPosition + 1)); 
      pointsToReturn.add(new Point(shapeXPosition + 6, shapeYPosition + 1));      
      pointsToReturn.add(new Point(shapeXPosition + 5, shapeYPosition - 1)); 
    }

    return pointsToReturn;

  }
  
  private boolean shapeColliding(Point shapePoint) {
    for(Point point : centroids) {
      if(point.x + 5 < shapePoint.x || point.y + 3 < shapePoint.y) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the gameOfLifeCanvas
   */
  public GameCellGridCanvas getGameOfLifeCanvas() {
    return gameOfLifeCanvas;
  }

  /**
   * @param gameOfLifeCanvas the gameOfLifeCanvas to set
   */
  public void setGameOfLifeCanvas(GameCellGridCanvas gameOfLifeCanvas) {
    this.gameOfLifeCanvas = gameOfLifeCanvas;
  }

  /**
   * @return the gameController
   */
  public GameController getGameController() {
    return gameController;
  }

  /**
   * @param gameController the gameController to set
   */
  public void setGameController(GameController gameController) {
    this.gameController = gameController;
  }

  /**
   * @return the cellCols
   */
  public int getCellCols() {
    return cellCols;
  }

  /**
   * @param cellCols the cellCols to set
   */
  public void setCellCols(int cellCols) {
    this.cellCols = cellCols;
  }

  /**
   * @return the cellRows
   */
  public int getCellRows() {
    return cellRows;
  }

  /**
   * @param cellRows the cellRows to set
   */
  public void setCellRows(int cellRows) {
    this.cellRows = cellRows;
  }

  /**
   * @return the genTime
   */
  public int getGenTime() {
    return genTime;
  }

  /**
   * @param genTime the genTime to set
   */
  public void setGenTime(int genTime) {
    this.genTime = genTime;
  }

  /**
   * @return the controls
   */
  public GameControls getControls() {
    return controls;
  }

  /**
   * @param controls the controls to set
   */
  public void setControls(GameControls controls) {
    this.controls = controls;
  }

  /**
   * @return the gameThread
   */
  public static Thread getGameThread() {
    return gameThread;
  }

  /**
   * @param gameThread the gameThread to set
   */
  public static void setGameThread(Thread gameThread) {
    GameView.gameThread = gameThread;
  }
}