package test;

/**
 * Class: Example1
 * <p>
 * 
 * @author Jean-Pierre Dube <jpdube@videotron.ca>
 * @version 1.0
 * @since 1.0
 * @see Printable
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

public class JavaWorldPrintExample1 implements Printable {
  public static void main(String[] args) {

    JavaWorldPrintExample1 example1 = new JavaWorldPrintExample1();
    System.exit(0);
  }

  //--- Private instances declarations
  private final double INCH = 72;

  /**
   * Constructor: Example1
   * <p>
   *  
   */
  public JavaWorldPrintExample1() {

    //--- Create a printerJob object
    PrinterJob printJob = PrinterJob.getPrinterJob();

    //--- Set the printable class to this one since we
    //--- are implementing the Printable interface
    printJob.setPrintable(this);

    //--- Show a print dialog to the user. If the user
    //--- click the print button, then print otherwise
    //--- cancel the print job
    if (printJob.printDialog()) {
      try {
        printJob.print();
      } catch (Exception PrintException) {
        PrintException.printStackTrace();
      }
    }

  }

  /**
   * Method: print
   * <p>
   * 
   * This class is responsible for rendering a page using the provided
   * parameters. The result will be a grid where each cell will be half an
   * inch by half an inch.
   * 
   * @param g
   *            a value of type Graphics
   * @param pageFormat
   *            a value of type PageFormat
   * @param page
   *            a value of type int
   * @return a value of type int
   */
  public int print(Graphics g, PageFormat pageFormat, int page) {

    int i;
    Graphics2D g2d;
    Line2D.Double line = new Line2D.Double();

    //--- Validate the page number, we only print the first page
    if (page == 0) {

      //--- Create a graphic2D object a set the default parameters
      g2d = (Graphics2D) g;
      g2d.setColor(Color.black);

      //--- Translate the origin to be (0,0)
      g2d.translate(pageFormat.getImageableX(), pageFormat
          .getImageableY());

      //--- Print the vertical lines
      for (i = 0; i < pageFormat.getWidth(); i += INCH / 2) {
        line.setLine(i, 0, i, pageFormat.getHeight());
        g2d.draw(line);
      }

      //--- Print the horizontal lines
      for (i = 0; i < pageFormat.getHeight(); i += INCH / 2) {
        line.setLine(0, i, pageFormat.getWidth(), i);
        g2d.draw(line);
      }

      return (PAGE_EXISTS);
    } else
      return (NO_SUCH_PAGE);
  }

} //Example1