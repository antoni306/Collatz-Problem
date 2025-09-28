/**
 *
 *  @author Kostuj Antoni S30687
 *
 */

package zad1;


import javax.swing.*;

public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new CHART();
      }
    });
  }
}
