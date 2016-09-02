/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package exceptionhandlers;

/**
 * Exception for shapes (too big, not found...).
 */
public class GameShapeException extends Exception {
  /**
   * Constructs a ShapeException.
   */
  public GameShapeException() {
    super();
  }
  /**
   * Constructs a ShapeException with a description.
   */
  public GameShapeException( String s ) {
    super( s );
  }
}
