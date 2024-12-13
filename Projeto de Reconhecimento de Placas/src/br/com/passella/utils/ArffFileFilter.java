package br.com.passella.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ArffFileFilter extends FileFilter {

   @Override
   public boolean accept(final File f) {
      final String nome = f.getAbsolutePath();
      final String extensao = nome.substring(nome.lastIndexOf('.'));
      return (extensao.equalsIgnoreCase(".arff"));
   }

   @Override
   public String getDescription() {
      return "Data Set Arff";
   }

}
