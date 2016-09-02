/**
 * @author Adrian Rodriguez Bazaga 
 * @version 1.0.0
 * @date 18 May 2016
 * @email alu0100826456@ull.edu.es / arodriba@ull.edu.es
 * @subject Programacion de Aplicaciones Interactivas
 * @title Assignment 13 - Game of Life
 */

package helpers;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.ImageObserver;


/**
 * A component to add a image to (e.g.) a panel.
 * Supports animated GIF's.
 */
public class ImageHelper extends Canvas implements ImageObserver {
  private Image image;

  /**
   * Constucts a ImageComponent.
   * 
   * This constructor uses the MediaTracker to get the width and height of the image,
   * so the constructor has to wait for the image to load. This is not a good idea when
   * getting images over a slow network. There is a timeout of 3 sec.
   * 
   * Maybe one time the MediaTracker can be replaced, so it returns when the width and height
   * are known, not when the whole image is loaded. Then the constructor will be much faster and
   * better suited for loading images over a slow connection.
   * 
   * @param image the image to show
   */
  public ImageHelper( Image image ) {
    this.image = image;
    MediaTracker tracker = new MediaTracker( this );
    tracker.addImage( image, 0 );
    try {
      // Wait max 3 sec
      tracker.waitForID(0, 3000);
    } catch (InterruptedException e) {
      // oops. no image.
    }
  }

  /**
   * Draw the image.
   * 
   * @see java.awt.Component#paint(java.awt.Graphics)
   */
  public void paint(Graphics g) {
    g.drawImage( image, 0, 0, this );
  }

  /**
   * Returns preferred size. At the first pack()ing of the Window, the image might nog be completely
   * read and getPreferredSize() might return the wrong size. imageUpdate() corrects this.
   * @see java.awt.Component#getPreferredSize()
   */
  public Dimension getPreferredSize() {
    return new Dimension( image.getWidth( this ), image.getHeight( this ) );
  }

  /**
   * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
   */

  public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    boolean isImageRead = ( infoflags & ImageObserver.ALLBITS ) != 0;
    repaint();
    return ! isImageRead; // return true while image not completely read
  }

}