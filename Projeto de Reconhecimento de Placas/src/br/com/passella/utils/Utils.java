package br.com.passella.utils;

import java.io.File;
import javax.swing.ImageIcon;

public class Utils {

   public final static String gif = "gif";
   public final static String jpeg = "jpeg";
   public final static String jpg = "jpg";
   public final static String png = "png";
   public final static String tif = "tif";
   public final static String tiff = "tiff";

   /** Returns an ImageIcon, or null if the path was invalid. */
   protected static ImageIcon createImageIcon(final String path) {
      final java.net.URL imgURL = Utils.class.getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL);
      } else {
         System.err.println("Não foi possivel encontrar o arquivo: " + path);
         return null;
      }
   }

   public static String getExtension(final File f) {
      String ext = null;
      final String s = f.getName();
      final int i = s.lastIndexOf('.');

      if ((i > 0) && (i < s.length() - 1)) {
         ext = s.substring(i + 1).toLowerCase();
      }
      return ext;
   }
}
