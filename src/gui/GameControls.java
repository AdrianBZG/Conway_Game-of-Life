/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eventhandlers.GameControlsEvent;
import framework.GameControlsListener;
import logic.GameController;
import models.GameShape;
import models.GameShapeCollection;


/**
 * GUI-controls of the Game of Life.
 * It contains controls like Shape, zoom and speed selector, next and start/stop-button.
 * It is a seperate class, so it can be replaced by another implementation for e.g. mobile phones or PDA's.
 * Communicates via the GameOfLifeControlsListener.
 */
public class GameControls extends JPanel implements ActionListener {
  public final static String INFO_ICON_PATH = "/home/alu4724/datos/homes.rala/Desktop/es.ull.esit.pai.p13/res/info-image-16.png";
  public final static ImageIcon infoIcon = new ImageIcon(INFO_ICON_PATH);
  private JLabel infoLabel;                               // The label for the information icon
  private JLabel genLabel;
  private final String PREBUILT_PATTERNS_TEXT = "Patterns:";
  private final String CHANGE_DIMENSIONS_TEXT = "Change dimensions:";
  private final String CHANGE_SPEED_TEXT = "Change speed:";
  private final String genLabelText = "Number of generations: ";
  private final String nextLabelText = "Step";
  private final String startLabelText = "Start";
  private final String CLEAR_BUTTON_TEXT = "Clear";
  private final String stopLabelText = "Stop";
  public static final String SMALL = "Small";
  public static final String MEDIUM = "Medium";
  public static final String BIG = "Big";
  public static final String VERYBIG = "Very big";
  public static final String INSANE = "Insane";
  public static final int SIZE_SMALL = 11;
  public static final int SIZE_MEDIUM = 8;
  public static final int SIZE_BIG = 5;
  public static final int SIZE_VERYBIG = 3;
  public static final int SIZE_INSANE = 2;
  public static final int MIN_GAME_SPEED = 1;
  public static final int DEFAULT_GAME_SPEED = 1;
  public static final int MAX_GAME_SPEED = 1000;
  public static final String REPETITIONS_TEXT = "Repetitions";
  private JButton startstopButton;
  private JButton clearButton;
  private JButton nextButton;
  private JSlider speedSlider;
  private Vector<GameControlsListener> listeners;
  private Choice patternsChoice;
  private Choice dimensionsChoice;
  private Choice lifeRules;
  private JTextField repeatTextField = new JTextField(3);
  private String selectedShape;

  /**
   * Contructs the controls.
   */
  public GameControls() {
    listeners = new Vector<GameControlsListener>();

    // pull-down menu with shapes
    patternsChoice = new Choice();

    lifeRules = new Choice();
    
    lifeRules.addItem("Original");
    lifeRules.addItem("High Life");
    lifeRules.addItem("Life without Death");
    
    // when item is selected
    lifeRules.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            String arg = (String) e.getItem();
            if ("Original".equals(arg)) {
              GameController.setLifeRules(0);
              System.out.println("Original rules");
            } else if ("High Life".equals(arg)) {
              GameController.setLifeRules(1);
              System.out.println("High life rules");
            }
          }
        }
        );

    // Put names of shapes in menu
    GameShape[] shapes = GameShapeCollection.getShapes();
    for ( int i = 1; i < shapes.length; i++ )
      patternsChoice.addItem( shapes[i].getName() );

    // when shape is selected
    patternsChoice.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            setSelectedShape((String)e.getItem());
            System.out.println("You've selected: " + (String) e.getItem());
          }
        }
        );

    getRepeatTextField().addActionListener(this);
    // Pull-down menu with dimensions
    dimensionsChoice = new Choice();

    // add speeds
    dimensionsChoice.addItem(SMALL);
    dimensionsChoice.addItem(MEDIUM);
    dimensionsChoice.addItem(BIG);
    dimensionsChoice.addItem(VERYBIG);
    dimensionsChoice.addItem(INSANE);

    // when item is selected
    dimensionsChoice.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            String arg = (String) e.getItem();
            if (BIG.equals(arg)) {
              zoomChanged(SIZE_BIG);
            } else if (MEDIUM.equals(arg)) {
              zoomChanged(SIZE_MEDIUM);
            } else if (SMALL.equals(arg)) {
              zoomChanged(SIZE_SMALL);
            } else if (VERYBIG.equals(arg)) {
              zoomChanged(SIZE_VERYBIG);
            } else if (INSANE.equals(arg)) {
              zoomChanged(SIZE_INSANE);
            }
          }
        }
        );

    // number of generations
    genLabel = new JLabel(genLabelText+"         ");

    // start and stop buttom
    startstopButton = new JButton(startLabelText);
    startstopButton.setName(startLabelText);

    // when start/stop button is clicked
    startstopButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if(startstopButton.getText() == startLabelText) {
              shapeSelected(getSelectedShape());
            }
            startStopButtonClicked();
          }
        }
        );

    // Clear button
    setClearButton(new JButton(CLEAR_BUTTON_TEXT));
    getClearButton().setName(CLEAR_BUTTON_TEXT);

    // when start/stop button is clicked
    getClearButton().addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            shapeSelected(CLEAR_BUTTON_TEXT);
          }
        }
        );

    // next generation button
    nextButton = new JButton(nextLabelText);
    nextButton.setName(nextLabelText);

    // when next button is clicked
    nextButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            nextButtonClicked();
          }
        }
        );

    // The info icon
    setInfoLabel(new JLabel(infoIcon));
    getInfoLabel().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        final String INFORMATION_TEXT = "Subject: Programacion de Aplicaciones Interactivas\n\nAssignment: Game of Life (13)\n\nDescription: Develop an app that simulates the John Conway's Game of Life\n\nVersion: 0.0.1\n\nAuthor: Adrian Rodriguez Bazaga\n\nEmail: arodriba@ull.edu.es";
        JOptionPane.showMessageDialog(null, INFORMATION_TEXT);
      }
    });
    //

    //The game speed slider
    setSpeedSlider(new JSlider(JSlider.HORIZONTAL, MIN_GAME_SPEED, MAX_GAME_SPEED, DEFAULT_GAME_SPEED));
    getSpeedSlider().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
          int newSpeed = MAX_GAME_SPEED - (int)source.getValue();
          if(newSpeed == 0) {
            newSpeed = 1;
          }
          speedChanged(newSpeed);
        }
      }
    });
    //

    // create panel with controls
    add(getInfoLabel(), BorderLayout.EAST);
    add(new JLabel(REPETITIONS_TEXT));
    add(getRepeatTextField());
    add(getLifeRules());
    add(new JLabel(PREBUILT_PATTERNS_TEXT));
    add(patternsChoice);
    add(getClearButton());
    add(startstopButton);
    add(nextButton);
    add(new JLabel(CHANGE_SPEED_TEXT));
    add(getSpeedSlider());
    add(new JLabel(CHANGE_DIMENSIONS_TEXT));
    add(dimensionsChoice);
    add(genLabel);
    validate();
  }

  public void actionPerformed(ActionEvent evt) {
    final String INFORMATION_TEXT = "Type a numeric value between 1 and 100";
    try {
      int textFieldValue = Integer.parseInt(getRepeatTextField().getText());
      if(textFieldValue > 0 && textFieldValue < 101) {
        System.out.println("Valido: " + textFieldValue);
      } else {
        JOptionPane.showMessageDialog(null, INFORMATION_TEXT);
      }
      getRepeatTextField().selectAll();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, INFORMATION_TEXT);
    }
  }


  /**
   * Add listener for this control
   * @param listener Listener object
   */
  public void addGameOfLifeControlsListener( GameControlsListener listener ) {
    listeners.addElement( listener );
  }

  /**
   * Remove listener from this control
   * @param listener Listener object
   */
  public void removeGameOfLifeControlsListener( GameControlsListener listener ) {
    listeners.removeElement( listener );
  }

  /**
   * Set the number of generations in the control bar.
   * @param generations number of generations
   */
  public void setGeneration( int generations ) {
    genLabel.setText(genLabelText + generations + "         ");
  }

  /**
   * Start-button is activated.
   */
  public void start() {
    startstopButton.setText(stopLabelText);
    nextButton.setEnabled(false);
    patternsChoice.setEnabled(false);
  }

  /**
   * Stop-button is activated.
   */
  public void stop() {
    startstopButton.setText(startLabelText);
    nextButton.setEnabled(true);
    patternsChoice.setEnabled(true);
  }

  /**
   * Called when the start/stop-button is clicked.
   * Notify event-listeners.
   */
  public void startStopButtonClicked() {
    GameControlsEvent event = new GameControlsEvent( this );
    for(Enumeration<GameControlsListener> e = listeners.elements(); e.hasMoreElements(); )
      ((GameControlsListener) e.nextElement()).startStopButtonClicked( event );
  }

  /**
   * Called when the next-button is clicked.
   * Notify event-listeners.
   */
  public void nextButtonClicked() {
    GameControlsEvent event = new GameControlsEvent( this );
    for (Enumeration<GameControlsListener> e = listeners.elements(); e.hasMoreElements(); )
      ((GameControlsListener) e.nextElement()).nextButtonClicked( event );
  }

  /**
   * Called when a new speed from the speed pull down is selected.
   * Notify event-listeners.
   */
  public void speedChanged( int speed ) {
    GameControlsEvent event = GameControlsEvent.getSpeedChangedEvent( this, speed );
    for (Enumeration<GameControlsListener> e = listeners.elements(); e.hasMoreElements(); )
      ((GameControlsListener) e.nextElement()).speedChanged( event );
  }

  /**
   * Called when a new zoom from the zoom pull down is selected.
   * Notify event-listeners.
   */
  public void zoomChanged( int zoom ) {
    GameControlsEvent event = GameControlsEvent.getZoomChangedEvent( this, zoom );
    for (Enumeration<GameControlsListener> e = listeners.elements(); e.hasMoreElements(); )
      ((GameControlsListener) e.nextElement()).zoomChanged( event );
  }

  /**
   * Called when a new shape from the shape pull down is selected.
   * Notify event-listeners.
   */
  public void shapeSelected( String shapeName ) {
    GameControlsEvent event = GameControlsEvent.getShapeSelectedEvent( this, shapeName );
    for (Enumeration<GameControlsListener> e = listeners.elements(); e.hasMoreElements(); )
      ((GameControlsListener) e.nextElement()).shapeSelected( event );
  }

  /**
   * Called when a new cell size from the zoom pull down is selected.
   * Notify event-listeners.
   */
  public void setZoom( String n ) {
    dimensionsChoice.select(n);
  }


  public JLabel getInfoLabel() {
    return infoLabel;
  }


  public void setInfoLabel(JLabel infoLabel) {
    this.infoLabel = infoLabel;
  }


  public JSlider getSpeedSlider() {
    return speedSlider;
  }


  public void setSpeedSlider(JSlider speedSlider) {
    this.speedSlider = speedSlider;
  }


  public JButton getClearButton() {
    return clearButton;
  }


  public void setClearButton(JButton clearButton) {
    this.clearButton = clearButton;
  }

  /**
   * @return the genLabel
   */
  public JLabel getGenLabel() {
    return genLabel;
  }


  /**
   * @param genLabel the genLabel to set
   */
  public void setGenLabel(JLabel genLabel) {
    this.genLabel = genLabel;
  }


  /**
   * @return the startstopButton
   */
  public JButton getStartstopButton() {
    return startstopButton;
  }


  /**
   * @param startstopButton the startstopButton to set
   */
  public void setStartstopButton(JButton startstopButton) {
    this.startstopButton = startstopButton;
  }


  /**
   * @return the nextButton
   */
  public JButton getNextButton() {
    return nextButton;
  }


  /**
   * @param nextButton the nextButton to set
   */
  public void setNextButton(JButton nextButton) {
    this.nextButton = nextButton;
  }


  /**
   * @return the listeners
   */
  public Vector<GameControlsListener> getListeners() {
    return listeners;
  }


  /**
   * @param listeners the listeners to set
   */
  public void setListeners(Vector<GameControlsListener> listeners) {
    this.listeners = listeners;
  }


  /**
   * @return the patternsChoice
   */
  public Choice getPatternsChoice() {
    return patternsChoice;
  }


  /**
   * @param patternsChoice the patternsChoice to set
   */
  public void setPatternsChoice(Choice patternsChoice) {
    this.patternsChoice = patternsChoice;
  }


  /**
   * @return the dimensionsChoice
   */
  public Choice getDimensionsChoice() {
    return dimensionsChoice;
  }


  /**
   * @param dimensionsChoice the dimensionsChoice to set
   */
  public void setDimensionsChoice(Choice dimensionsChoice) {
    this.dimensionsChoice = dimensionsChoice;
  }


  /**
   * @return the startLabelText
   */
  public String getStartLabelText() {
    return startLabelText;
  }


  public JTextField getRepeatTextField() {
    return repeatTextField;
  }


  public void setRepeatTextField(JTextField repeatTextField) {
    this.repeatTextField = repeatTextField;
  }

  public String getSelectedShape() {
    return selectedShape;
  }

  public void setSelectedShape(String selectedShape) {
    this.selectedShape = selectedShape;
  }

  public Choice getLifeRules() {
    return lifeRules;
  }

  public void setLifeRules(Choice lifeRules) {
    this.lifeRules = lifeRules;
  }
}