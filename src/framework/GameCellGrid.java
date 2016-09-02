/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package framework;

import java.awt.Dimension;
import java.util.Enumeration;

import models.GameCell;

/**
 * Interface between GameOfLifeCanvas and GameOfLife.
 * This way GameOfLifeCanvas is generic, independent of GameOfLife.
 * It contains generic methods to operate on a cell grid.
 */
public interface GameCellGrid {
  /**
   * Get status of cell (alive or dead).
   * @param col x-position
   * @param row y-position
   * @return living or not
   */
  public boolean getCell( int col, int row );

  /**
   * Set status of cell (alive or dead).
   * @param col x-position
   * @param row y-position
   * @param cell living or not
   */
  public void setCell( int col, int row, boolean cell );

  /**
   * Get dimension of cellgrid.
   * @return dimension
   */
  public Dimension getDimension();

  /**
   * Resize the cell grid.
   * @param col new number of columns.
   * @param row new number of rows.
   */
  public void resize( int col, int row );

  /**
   * Get cell-enumerator. Enumerates over all living cells (type Cell).
   * @return Enumerator over Cell.
   * @see GameCell
   */
  @SuppressWarnings("rawtypes")
  public Enumeration getEnum();

  /**
   * Clears grid.
   */
  public void clear();
}