/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package logic;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Hashtable;

import framework.GameCellGrid;
import models.GameCell;

/**
 * Contains the cellgrid, the current shape and the Game Of Life algorithm that changes it.
 */
public class GameController implements GameCellGrid {
  private int cellRows;
  private int cellCols;
  private int generations;
  private static int lifeRules = 0;
  /**
   * Contains the current, living shape.
   * It's implemented as a hashtable. Tests showed this is 70% faster than Vector.
   */
  private Hashtable<GameCell, GameCell> currentShape;
  private Hashtable<GameCell, GameCell> nextShape;
  /**
   * Every cell on the grid is a Cell object. This object can become quite large.
   */
  private GameCell[][] grid;

  /**
   * Contructs a GameOfLifeGrid.
   * 
   * @param cellCols number of columns
   * @param cellRows number of rows
   */
  public GameController(int cellCols, int cellRows) {
    this.cellCols = cellCols;
    this.cellRows = cellRows;
    currentShape = new Hashtable<GameCell, GameCell>();
    nextShape = new Hashtable<GameCell, GameCell>();

    grid = new GameCell[cellCols][cellRows];
    for ( int c=0; c<cellCols; c++)
      for ( int r=0; r<cellRows; r++ )
        grid[c][r] = new GameCell( c, r );
  }

  /**
   * Clears grid.
   */
  public synchronized void clear() {
    generations = 0;
    currentShape.clear();
    nextShape.clear();
  }

  /**
   * Create next generation of shape.
   */
  public synchronized void next() {
    GameCell gameCell;
    int col, row;
    Enumeration<GameCell> testEnum;

    generations++;
    nextShape.clear();

    // Reset cells
    testEnum = currentShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      gameCell.neighbour = 0;
    }
    // Add neighbours
    testEnum = currentShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      col = gameCell.col;
      row = gameCell.row;
      addNeighbour( col-1, row-1 );
      addNeighbour( col, row-1 );
      addNeighbour( col+1, row-1 );
      addNeighbour( col-1, row );
      addNeighbour( col+1, row );
      addNeighbour( col-1, row+1 );
      addNeighbour( col, row+1 );
      addNeighbour( col+1, row+1 );
    }

    // Bury the dead
    // We are walking through an enum from we are also removing elements. Can be tricky.
    testEnum = currentShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      // Original
      if(getLifeRules() == 0) {
        if ( gameCell.neighbour != 3 && gameCell.neighbour != 2 ) {
          currentShape.remove( gameCell );
        }
      } /* High Life */ else if(getLifeRules() == 1) {
        if ( gameCell.neighbour != 3 && gameCell.neighbour != 2 ) {
          currentShape.remove( gameCell );
        }
      }
    }
    // Bring out the new borns
    testEnum = nextShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      // Original
      if(getLifeRules() == 0) {
        if ( gameCell.neighbour == 3 ) {
          setCell( gameCell.col, gameCell.row, true );
        }
      } /* High Life */ else if(getLifeRules() == 1) {
        if ( gameCell.neighbour == 3 || gameCell.neighbour == 6) {
          setCell( gameCell.col, gameCell.row, true );
        }
      }
    }
  }

  /**
   * Adds a new neighbour to a cell.
   * 
   * @param col Cell-column
   * @param row Cell-row
   */
  public synchronized void addNeighbour(int col, int row) {
    try {
      GameCell gameCell = (GameCell)nextShape.get( grid[col][row] );
      if ( gameCell == null ) {
        // Cell is not in hashtable, then add it
        GameCell c = grid[col][row];
        c.neighbour = 1;
        nextShape.put(c, c);
      } else {
        // Else, increments neighbour count
        gameCell.neighbour++;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
  }

  /**
   * Get enumeration of Cell's
   * @see framework.GameCellGrid.gameoflife.CellGrid#getEnum()
   */
  public Enumeration<GameCell> getEnum() {
    return currentShape.keys();
  }

  /**
   * Get value of cell.
   * @param col x-coordinate of cell
   * @param row y-coordinate of cell
   * @return value of cell
   */
  public synchronized boolean getCell( int col, int row ) {
    try {
      return currentShape.containsKey(grid[col][row]);
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
    return false;
  }

  /**
   * Set value of cell.
   * @param col x-coordinate of cell
   * @param row y-coordinate of cell
   * @param c value of cell
   */
  public synchronized void setCell( int col, int row, boolean c ) {
    try {
      GameCell gameCell = grid[col][row];
      if ( c ) {
        currentShape.put(gameCell, gameCell);
      } else {
        currentShape.remove(gameCell);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // ignore
    }
  }

  /**
   * Get number of generations.
   * @return number of generations
   */
  public int getGenerations() {
    return generations;
  }

  /**
   * Get dimension of grid.
   * @return dimension of grid
   */
  public Dimension getDimension() {
    return new Dimension( cellCols, cellRows );
  }

  /**
   * Resize grid. Reuse existing cells.
   * @see framework.GameCellGrid.gameoflife.CellGrid#resize(int, int)
   */
  public synchronized void resize(int cellColsNew, int cellRowsNew) {
    if ( cellCols==cellColsNew && cellRows==cellRowsNew )
      return; // Not really a resize

    // Create a new grid, reusing existing Cell's
    GameCell[][] gridNew = new GameCell[cellColsNew][cellRowsNew];
    for ( int c=0; c<cellColsNew; c++)
      for ( int r=0; r<cellRowsNew; r++ )
        if ( c < cellCols && r < cellRows )
          gridNew[c][r] = grid[c][r];
        else
          gridNew[c][r] = new GameCell( c, r );

    // Copy existing shape to center of new shape
    int colOffset = (cellColsNew-cellCols)/2;
    int rowOffset = (cellRowsNew-cellRows)/2;
    GameCell gameCell;
    Enumeration<GameCell> testEnum;
    nextShape.clear();
    testEnum = currentShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      int colNew = gameCell.col + colOffset;
      int rowNew = gameCell.row + rowOffset;
      try {
        nextShape.put( gridNew[colNew][rowNew], gridNew[colNew][rowNew] );
      } catch ( ArrayIndexOutOfBoundsException e ) {
        // ignore
      }
    }

    // Copy new grid and hashtable to working grid/hashtable
    grid = gridNew;
    currentShape.clear();
    testEnum = nextShape.keys();
    while ( testEnum.hasMoreElements() ) {
      gameCell = (GameCell) testEnum.nextElement();
      currentShape.put( gameCell, gameCell );
    }

    cellCols = cellColsNew;
    cellRows = cellRowsNew;
  }

  public static int getLifeRules() {
    return lifeRules;
  }

  public static void setLifeRules(int newLifeRules) {
    lifeRules = newLifeRules;
  }
}