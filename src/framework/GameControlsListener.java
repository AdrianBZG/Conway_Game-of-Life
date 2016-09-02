/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package framework;

import java.util.EventListener;

import eventhandlers.GameControlsEvent;

/**
 * Listener interface for GameOfLifeControls.
 */
public interface GameControlsListener extends EventListener {
  /**
   * The Start/Stop button is clicked.
   * @param e event object
   */
  public void startStopButtonClicked( GameControlsEvent e );

  /**
   * The Next button is clicked.
   * @param e event object
   */
  public void nextButtonClicked( GameControlsEvent e );

  /**
   * A new speed is selected.
   * @param e event object
   */
  public void speedChanged( GameControlsEvent e );

  /**
   * A new cell size is selected.
   * @param e event object
   */
  public void zoomChanged( GameControlsEvent e );

  /**
   * A new shape is selected.
   * @param e event object
   */
  public void shapeSelected( GameControlsEvent e );
}