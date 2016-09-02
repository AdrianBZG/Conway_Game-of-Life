/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package models;

import exceptionhandlers.GameShapeException;

/**
 * Contains some default Game Of Life shapes (read https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
 */
public class GameShapeCollection {
  private static final GameShape CLEAR;
  private static final GameShape GLIDER;
  private static final GameShape BLINKER;
  private static final GameShape DIEHARD;
  private static final GameShape[] COLLECTION;

  static {
    CLEAR = new GameShape("Clear", new int[][] {} );
    BLINKER = new GameShape("Blinker", new int[][] {{0,1}, {0,3}, {1,0}, {2,0}, {3,0}, {3,3}, {4,0}, {4,1}, {4,2}});
    GLIDER = new GameShape("Glider", new int[][] {{1,0}, {2,1}, {2,2}, {1,2}, {0,2}});
    DIEHARD = new GameShape("Diehard", new int[][] {{0,2}, {0,3}, {1,2}, {1,3}, {8,3}, {8,4}, {9,2}, {9,4}, {10,2}, {10,3}, {16,4}, {16,5}, {16,6}, {17,4}, {18,5}, {22,1}, {22,2}, {23,0}, {23,2}, {24,0}, {24,1}, {24,12}, {24,13}, {25,12}, {25,14}, {26,12}, {34,0}, {34,1}, {35,0}, {35,1}, {35,7}, {35,8}, {35,9}, {36,7}, {37,8}});
    COLLECTION = new GameShape[] {CLEAR, BLINKER, GLIDER, DIEHARD};
  }

  /**
   * Get array of shapes.
   * 
   * It's not tamper-proof, but that's okay.
   * @return collection of shapes
   */
  public static GameShape[] getShapes() {
    return COLLECTION;
  }

  /**
   * Get shape by its name.
   * @param name name of shape
   * @return shape object
   * @throws GameShapeException if no shape with this name exist
   */
  public static GameShape getShapeByName( String name ) throws GameShapeException {
    GameShape[] shapes = getShapes();
    for(int i = 0; i < shapes.length; i++ ) {
      if(shapes[i].getName().equals( name )  )
        return shapes[i];
    }
    throw(new GameShapeException("Unknown shape: " + name) );
  }
}