package br.com.passella.utils;

import ij.process.ByteProcessor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import br.com.passella.neural.redeneural.Amostra;
import br.com.passella.neural.redeneural.Atributos;

public class BufferedImageUtils {

   public static BufferedImage getImagemCinza(final BufferedImage bufferedImage) {
      final BufferedImage retorno = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics graphics = retorno.getGraphics();
      graphics.drawImage(bufferedImage, 0, 0, null);
      graphics.dispose();
      return retorno;
   }

   public static BufferedImage getImagemEsqueleto(final BufferedImage bufferedImage) {

      final ByteProcessor processador = new ByteProcessor(bufferedImage);
      processador.autoThreshold();
      processador.skeletonize();
      processador.invertLut();

      return processador.getBufferedImage();
   }

   public static BufferedImage getImgemPassaAltaVertical(final BufferedImage bufferedImage) {
      final int[][] SobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };

      final BufferedImage resultado = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

      final Raster raster = bufferedImage.getRaster();
      final WritableRaster writableRaster = resultado.getRaster();

      for (int i = 1; i < bufferedImage.getWidth() - 1; i++) {
         for (int j = 1; j < bufferedImage.getHeight() - 1; j++) {
            long pixelAtual = raster.getSample(i - 1, j - 1, 0) * SobelVertical[0][0] + raster.getSample(i, j - 1, 0) * SobelVertical[0][1]
                     + raster.getSample(i + 1, j - 1, 0) * SobelVertical[0][2] + raster.getSample(i - 1, j, 0) * SobelVertical[1][0]
                     + raster.getSample(i, j, 0) * SobelVertical[1][1] + raster.getSample(i + 1, j, 0) * SobelVertical[1][2]
                     + raster.getSample(i - 1, j + 1, 0) * SobelVertical[2][0] + raster.getSample(i, j + 1, 0) * SobelVertical[2][1]
                     + raster.getSample(i + 1, j + 1, 0) * SobelVertical[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            writableRaster.setSample(i, j, 0, pixelAtual);
         }
      }

      return resultado;
   }

   public static BufferedImage getScaledInstance(final BufferedImage bufferedImage, final int width, final int height, final int hints) {
      final BufferedImage retorno = new BufferedImage(width, height, bufferedImage.getType());
      final Graphics2D g = retorno.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(bufferedImage, 0, 0, width, height, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
      g.dispose();
      return retorno;
   }

   public static Amostra toAmostra(final BufferedImage bufferedImage, final int valorDesejado) {
      BufferedImage image = BufferedImageUtils.getImagemCinza(bufferedImage);
      image = BufferedImageUtils.getScaledInstance(image, Configuracao.getLarguraIdeal(), Configuracao.getAlturaIdeal(),
               BufferedImage.TYPE_BYTE_GRAY);
      image = BufferedImageUtils.getImagemEsqueleto(image);

      final int valores[] = BufferedImageUtils.toArray(image);
      final Amostra amostra = new Amostra(new Atributos(Configuracao.getLarguraIdeal() * Configuracao.getAlturaIdeal()), valores,
               valorDesejado);
      return amostra;
   }

   public static int[] toArray(final BufferedImage bufferedImage) {
      final int[] retorno = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
      int i = 0;
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
         for (int x = 0; x < bufferedImage.getWidth(); x++) {
            retorno[i++] = bufferedImage.getRaster().getSample(x, y, 0) > 128 ? 0 : 1;

         }
      }
      return retorno;
   }

}
