/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package logic;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gui.GameView;

public class Main {
  public static final String WINDOW_TITLE = "Game Of Life - v0.0.1 - Made by Adrian Rodriguez Bazaga";
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() 
    {
      @Override
      public void run()  
      {        
        // Create a frame
        JFrame frame = new JFrame(WINDOW_TITLE);

        // Create an instance of the applet
        GameView applet = new GameView();
        
        // Add the applet instance to the frame
        frame.add(applet, BorderLayout.CENTER);
        
        // Invoke applet's init method
        applet.init();
        applet.start();

        // Settings for the frame
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);                          // Center the frame
        
        // Display the frame
        frame.setVisible(true);
      }
    });
  }
}