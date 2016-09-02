/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package models;

/**
 * Every cell in the grid is a Cell-object.
 * So it must be as small as possible.
 * Because every cell is pre-generated, no cells have to be generated when the Game Of Life playw.
 * Whether a cell is alive or not, is not part of the Cell-object.
 */
public class GameCell {
  public final short col;
  public final short row;
  /**
   * Number of neighbours of this cell.
   * 
   * Determines the next state.
   */
  public byte neighbour; // Neighbour is International English

  /**
   * HASHFACTOR must be larger than the maximum number of columns (that is: the max width of a monitor in pixels).
   * It should also be smaller than 65536. (sqrt(MAXINT)).
   */
  private final int HASHFACTOR = 5000; 

  /**
   * Constructor
   * @param col column of cell
   * @param row row or cell
   */
  public GameCell( int col, int row ) {
    this.col = (short)col;
    this.row = (short)row;
    neighbour = 0;
  }

  /**
   * Compare cell-objects for use in hashtables
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o) {
    if (!(o instanceof GameCell) )
      return false;
    return col==((GameCell)o).col && row==((GameCell)o).row;
  }

  /**
   * Calculate hash for use in hashtables
   * 
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return HASHFACTOR*row+col;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Cell at ("+col+", "+row+") with "+neighbour+" neighbour"+(neighbour==1?"":"s");
  }
}