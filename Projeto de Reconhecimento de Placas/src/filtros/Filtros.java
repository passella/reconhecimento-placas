package filtros;

import ia.reconhecedor.Reconhecedor;
import ij.process.ByteProcessor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import exceptions.DifferentSizes;

public class Filtros {

   private int count = 1;
   private BufferedImage image = null;

   private BufferedImage processed = null;

   public Filtros() {}

   public Filtros(final BufferedImage bi) {
      this.setImage(bi);
   }

   public BufferedImage aditiveBrightRGB(final int c) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {
            final int argb = this.getImage().getRGB(i, j);

            int newRed = Pixel.getRed(argb) + c;

            if (newRed < 0) {
               newRed = 0;
            } else if (newRed > 255) {
               newRed = 255;
            }

            int newGreen = Pixel.getGreen(argb) + c;

            if (newGreen < 0) {
               newGreen = 0;
            } else if (newGreen > 255) {
               newGreen = 255;
            }

            int newBlue = Pixel.getBlue(argb) + c;

            if (newBlue < 0) {
               newBlue = 0;
            } else if (newBlue > 255) {
               newBlue = 255;
            }

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, argb);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage aditiveBrightYIQ(final int c) {
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int argb = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(argb);

            final double y = yiq.get(0) + c;

            // if(y<0) {
            // y = 0;
            // }
            // else if(y>255) {
            // y = 255;
            // }

            yiq.set(0, y);

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            final int newRed = Pixel.getRed(rgb);
            final int newGreen = Pixel.getGreen(rgb);
            final int newBlue = Pixel.getBlue(rgb);

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, rgb);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage autoThreshold() {
      final ByteProcessor byteProc = new ByteProcessor(this.image);
      byteProc.autoThreshold();
      this.processed = byteProc.getBufferedImage();
      return byteProc.getBufferedImage();
   }

   public BufferedImage autoThreshold(final BufferedImage im) {
      final BufferedImage imThreshold = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics g = imThreshold.getGraphics();
      g.drawImage(im, 0, 0, null);
      g.dispose();
      final ByteProcessor byteProc = new ByteProcessor(imThreshold);
      byteProc.autoThreshold();
      // Image image = byteProc.createImage();
      // byteProc = new ByteProcessor(image);
      // byteProc.dilate(10, 0);
      // image = byteProc.createImage();
      // byteProc = new ByteProcessor(image);
      // byteProc.erode(10, 0);
      return byteProc.getBufferedImage();
   }

   public BufferedImage Binariza() {
      // Esse método apenas cria uma imagem do tipo "BINARY" e insere a imagem
      // original dentro dela
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_BINARY);
      final Graphics g = im.getGraphics();
      g.drawImage(this.getImage(), 0, 0, null);
      g.dispose();
      this.processed = im;
      return im;
   }

   public BufferedImage Binariza(final BufferedImage imageIn) {
      final BufferedImage imageOut = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
      final Graphics g = imageOut.getGraphics();
      g.drawImage(imageIn, 0, 0, null);
      g.dispose();
      this.processed = imageOut;
      return imageOut;
   }

   private void caracteresFrame(final LinkedList<BufferedImage> listaCaracteres) {
      final JFrame caracteresFrame = new JFrame("Caracteres");
      caracteresFrame.setLayout(new FlowLayout());
      for (final BufferedImage im : listaCaracteres) {
         final JLabel label = new JLabel(new ImageIcon(im));
         caracteresFrame.add(label);
      }
      caracteresFrame.setVisible(true);
      caracteresFrame.setBackground(Color.BLUE);
      caracteresFrame.setSize(200, 100);
   }

   public BufferedImage desvioPadraoRGB(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2, doubleMask = mask * mask;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int RGB = this.getImage().getRGB(i, j);
            int r = Pixel.getRed(RGB), g = Pixel.getGreen(RGB), b = Pixel.getBlue(RGB);

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), Math.abs(l)));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), l1));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l1));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l1));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, Math.abs(l)));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, l1));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, l1));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, l1));

                  } else if (k < 0) {

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), l));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l));

                  } else if (l < 0) {

                     r += Pixel.getRed(this.getImage().getRGB(k, Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(k, Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(k, Math.abs(l)));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, l));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, l));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, l));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k, l1));
                     g += Pixel.getGreen(this.getImage().getRGB(k, l1));
                     b += Pixel.getBlue(this.getImage().getRGB(k, l1));

                  } else {

                     r += Pixel.getRed(this.getImage().getRGB(k, l));
                     g += Pixel.getGreen(this.getImage().getRGB(k, l));
                     b += Pixel.getBlue(this.getImage().getRGB(k, l));

                  }
               }
            }

            final int mediumR = r / doubleMask;
            final int mediumG = g / doubleMask;
            final int mediumB = b / doubleMask;

            int sumR = 0;
            int sumG = 0;
            int sumB = 0;

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(Math.abs(k), Math.abs(l))) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), Math.abs(l))) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), Math.abs(l))) - mediumB, 2);

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l1)) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l1)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l1)) - mediumB, 2);

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k1, Math.abs(l))) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k1, Math.abs(l))) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k1, Math.abs(l))) - mediumB, 2);

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k1, l1)) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k1, l1)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k1, l1)) - mediumB, 2);

                  } else if (k < 0) {

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l)) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l)) - mediumB, 2);

                  } else if (l < 0) {

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k, Math.abs(l))) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k, Math.abs(l))) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k, Math.abs(l))) - mediumB, 2);

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k1, l)) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k1, l)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k1, l)) - mediumB, 2);

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k, l1)) - mediumR, 2);
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k, l1)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k, l1)) - mediumB, 2);

                  } else {

                     sumR += Math.pow(Pixel.getRed(this.getImage().getRGB(k, l)) - mediumR, 2);;
                     sumG += Math.pow(Pixel.getGreen(this.getImage().getRGB(k, l)) - mediumG, 2);
                     sumB += Math.pow(Pixel.getBlue(this.getImage().getRGB(k, l)) - mediumB, 2);

                  }
               }
            }

            final int raizR = (int) Math.round(Math.sqrt(sumR / doubleMask));
            final int raizG = (int) Math.round(Math.sqrt(sumG / doubleMask));
            final int raizB = (int) Math.round(Math.sqrt(sumB / doubleMask));

            im.setRGB(i, j, Pixel.setRGB(raizR, raizG, raizB, RGB));

         }

      }
      this.processed = im;
      return im;
   }

   public BufferedImage desvioPadraoYIQ(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2, doubleMask = mask * mask;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int RGB = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(RGB);

            double y = yiq.get(0);

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), Math.abs(l))).get(0);

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l1)).get(0);

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, Math.abs(l))).get(0);

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l1)).get(0);

                  } else if (k < 0) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l)).get(0);

                  } else if (l < 0) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, Math.abs(l))).get(0);

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l)).get(0);

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l1)).get(0);

                  } else {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l)).get(0);

                  }
               }
            }

            final double mediumY = y / doubleMask;

            int sumY = 0;

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), Math.abs(l))).get(0) - mediumY, 2);

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l1)).get(0) - mediumY, 2);

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, Math.abs(l))).get(0) - mediumY, 2);

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l1)).get(0) - mediumY, 2);

                  } else if (k < 0) {

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l)).get(0) - mediumY, 2);

                  } else if (l < 0) {

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, Math.abs(l))).get(0) - mediumY, 2);

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l)).get(0) - mediumY, 2);

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l1)).get(0) - mediumY, 2);

                  } else {

                     sumY += Math.pow(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l)).get(0) - mediumY, 2);

                  }
               }
            }

            final double raizY = Math.round(Math.sqrt(sumY / doubleMask));

            yiq.set(0, raizY);

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            im.setRGB(i, j, rgb);

         }

      }
      this.processed = im;
      return im;
   }

   public BufferedImage Dilatacao() {
      return this.getImage();
   }

   public BufferedImage Equaliza() {
      final BufferedImage equalizado = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

      final Histograma histograma = new Histograma(this.image);
      final Histograma histEqualizado = histograma.getEqualizado();

      for (int x = 0; x < this.image.getWidth(); x++) {
         for (int y = 0; y < this.image.getHeight(); y++) {
            final int novoValor = histEqualizado.getBase()[this.image.getRaster().getSample(x, y, 0)];
            equalizado.getRaster().setSample(x, y, 0, novoValor);
         }
      }
      this.processed = equalizado;
      return equalizado;
   }

   public BufferedImage Equaliza(final BufferedImage bufferedImage) {
      final BufferedImage equalizado = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

      final Histograma histograma = new Histograma(bufferedImage);
      final Histograma histEqualizado = histograma.getEqualizado();

      for (int x = 0; x < bufferedImage.getWidth(); x++) {
         for (int y = 0; y < bufferedImage.getHeight(); y++) {
            final int novoValor = histEqualizado.getBase()[bufferedImage.getRaster().getSample(x, y, 0)];
            equalizado.getRaster().setSample(x, y, 0, novoValor);
         }
      }

      return equalizado;
   }

   public BufferedImage Erosao() {
      return this.getImage();
   }

   public BufferedImage Erosao(final BufferedImage im) {
      final PlanarImage imagem = PlanarImage.wrapRenderedImage(im);
      final float[] estrutura = { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1,
               1, 0, 0, 0, 0, 0, 0, };
      final KernelJAI kernel = new KernelJAI(7, 7, estrutura);
      final ParameterBlock p = new ParameterBlock();
      p.addSource(imagem);
      p.add(kernel);
      final PlanarImage erodida = JAI.create("erode", p);
      return erodida.getAsBufferedImage();
   }

   public BufferedImage Esqueleto() {
      // é criada uma imagem com a filtragem Sobel vertical
      final BufferedImage im = this.PassaAltaVertical();
      //
      final ByteProcessor processador = new ByteProcessor(im);
      processador.autoThreshold();
      processador.invertLut();
      processador.skeletonize();
      processador.invertLut();
      return processador.getBufferedImage();
   }

   public BufferedImage EsqueletoPassaAltaInteiro() {
      BufferedImage im = this.PassaAltaInteiro();
      // new ImagePlus();
      final ByteProcessor processador = new ByteProcessor(im);
      // final ImageProcessor Imp = processador.convertToByte(true);
      processador.autoThreshold();
      processador.invertLut();
      processador.skeletonize();
      im = this.negativeRGB(processador.getBufferedImage());
      // final Binary bin = new Binary();
      // bin.setup("skel", implus);
      // bin.run(Imp);
      return im;
      // ByteProcessor processador = new ByteProcessor(JAI.create("awtimage",
      // image));

   }

   public BufferedImage EsqueletoPassaAltaInteiro(final BufferedImage image) {
      BufferedImage im = this.PassaAltaInteiro(image);
      // new ImagePlus();
      final ByteProcessor processador = new ByteProcessor(im);
      // final ImageProcessor Imp = processador.convertToByte(true);
      processador.autoThreshold();
      processador.invertLut();
      processador.skeletonize();
      im = this.negativeRGB(processador.getBufferedImage());
      // final Binary bin = new Binary();
      // bin.setup("skel", implus);
      // bin.run(Imp);
      return im;
      // ByteProcessor processador = new ByteProcessor(JAI.create("awtimage",
      // image));

   }

   public BufferedImage getImage() {
      return this.image;
   }

   public BufferedImage getImageProcessed() {
      return this.processed;
   }

   public BufferedImage Localiza() {
      final int largura = 240;
      final int altura = 40;
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final ScannerImagemMedia imagemMedia = new ScannerImagemMedia(this.getImage(), largura, altura, im);
      imagemMedia.scanear();
      this.processed = imagemMedia.getImage();
      return imagemMedia.getImage();
   }

   public BufferedImage LocalizaComEsqueleto() {
      final int largura = 240;
      final int altura = 40;
      final BufferedImage imEsqueleto = this.Esqueleto();
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final ScannerImagemMedia imagemMedia = new ScannerImagemMedia(imEsqueleto, largura, altura, im);
      imagemMedia.scanear();
      final Ponto p = imagemMedia.PontoMaiorIntensidade();

      final BufferedImage imPassa = this.PassaAltaVertical();
      final ScannerImagemMaiorSoma maiorSoma = new ScannerImagemMaiorSoma(imPassa, largura, altura, p, 2, 255, 55,
               ScannerImagemMaiorSoma.JANELA_INTEIRA, -7, -5);
      maiorSoma.scanear();
      final Placa placa = new Placa(this.image, maiorSoma.getPontoSomaMax(), 255, 55, -7, -5);
      final ScannerImagemAssinatura assinatura = new ScannerImagemAssinatura(placa.getImagem());
      final Grafico graficoMeio = new Grafico(assinatura.GetListaPixelsMeio(), 255, 300, "Gráfico do Meio");
      graficoMeio.iniciaSequencia();
      graficoMeio.setVisible(true);
      graficoMeio.setSize(350, 350);
      graficoMeio.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      final Grafico grafico = new Grafico(assinatura.iniciaListaPicosCaracteres(5, 60, 38, 10, 28, 7), 255, 55, "Plotação das Transições");
      grafico.iniciaPlot();
      grafico.setVisible(true);
      grafico.setSize(300, 100);
      grafico.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      assinatura.inicializaRetanguloCaracteres(3, 5);
      final Grafico graficoMedia = new Grafico(assinatura.LocalizaPicosCaracteres(5), 255, 300, "Gráfico da Média dos Caracteres");
      graficoMedia.iniciaSequencia();
      graficoMedia.setVisible(true);
      graficoMedia.setSize(350, 350);
      this.processed = assinatura.DesenhaRetanguloCaracteres();
      /*
       * final Grafico plotPontos = new Grafico(255, 55,
       * "Pontos da Somatória das Transições");
       * plotPontos.iniciaPlot(assinatura.InsereListaCaracteres(5, 18, 38, 5,
       * 60)); plotPontos.setVisible(true); plotPontos.setSize(300, 100);
       * plotPontos.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
       */
      // assinatura.InsereListaCaracteresPicos(5, 18, 38, 10, 60);
      final Placa placaPassa = new Placa(this.autoThreshold(placa.getImagem()));
      final LinkedList<BufferedImage> listaImagens = assinatura.localizaCaracteresVariacaoVertical(placaPassa, 1, 1);
      this.caracteresFrame(listaImagens);

      this.reconhecerCaracteres(listaImagens);

      final JFrame telaPlaca = new JFrame("Placa");
      final JLabel label = new JLabel(new ImageIcon(placaPassa.getImagem()));
      telaPlaca.setLayout(new FlowLayout());
      telaPlaca.add(label);
      telaPlaca.setVisible(true);
      telaPlaca.setSize(placaPassa.getImagem().getWidth() + 50, placaPassa.getImagem().getHeight() + 50);
      final JFrame telaPlacaFatiada = new JFrame("Placa Fatiada");
      final JLabel labelfatiado = new JLabel(new ImageIcon(assinatura.Fatia()));
      telaPlacaFatiada.setLayout(new FlowLayout());
      telaPlacaFatiada.add(labelfatiado);
      telaPlacaFatiada.setVisible(true);
      telaPlacaFatiada.setSize(placaPassa.getImagem().getWidth() + 50, placaPassa.getImagem().getHeight() + 50);
      return assinatura.DesenhaRetanguloCaracteres();
      // return maiorSoma.DesenhaRetanguloMaiorSoma(this.image);

   }

   public BufferedImage LocalizaComEsqueletoTudo() {
      final int largura = 240;
      final int altura = 40;
      final BufferedImage imEsqueleto = this.Esqueleto();
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      this.PassaAltaInteiro();
      final ScannerImagemMedia imagemMedia = new ScannerImagemMedia(imEsqueleto, largura, altura, im);
      imagemMedia.scanear();
      final Ponto p = imagemMedia.PontoMaiorIntensidade();
      final BufferedImage imPassa = this.PassaAltaVertical();
      final ScannerImagemMaiorSoma maiorSoma = new ScannerImagemMaiorSoma(imPassa, largura, altura, p, 2, 255, 55,
               ScannerImagemMaiorSoma.JANELA_INTEIRA, -7, -5);
      maiorSoma.scanear();
      this.processed = maiorSoma.DesenhaRetanguloMaiorSoma(this.PassaAltaInteiro());
      final Placa placa = new Placa(this.image, maiorSoma.getPontoSomaMax(), 255, 55, -7, -5);
      final ScannerImagemAssinatura assinatura = new ScannerImagemAssinatura(placa);
      final Grafico graficoMeio = new Grafico(assinatura.GetListaPixelsMeio(), 255, 300, "Gráfico do Meio");
      graficoMeio.iniciaSequencia();
      /*
       * try { ImageIO.write(graficoMeio.image, "JPG", new
       * File("C:\\Users\\Guilherme\\Desktop\\TCC Temp\\Grafico\\gra (" +
       * this.count + ").jpg")); } catch (final IOException e) { // TODO
       * Auto-generated catch block e.printStackTrace(); }
       */
      // graficoMeio.setVisible(true);
      // graficoMeio.setSize(255, 300);
      // graficoMeio.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      final Grafico grafico = new Grafico(assinatura.iniciaListaPicosCaracteres(5, 60, 38, 10, 28, 7), 255, 55, "Plotação das Transições");
      grafico.iniciaPlot();
      /*
       * try { ImageIO.write(grafico.image, "JPG", new
       * File("C:\\Users\\Guilherme\\Desktop\\TCC Temp\\Plot\\plo (" +
       * this.count + ").jpg")); } catch (final IOException e) { // TODO
       * Auto-generated catch block e.printStackTrace(); }
       */
      assinatura.inicializaRetanguloCaracteres(3, 4);
      final Grafico graficoMedia = new Grafico(assinatura.LocalizaPicosCaracteres(5), 255, 300, "Gráfico da Média dos Caracteres");
      graficoMedia.iniciaSequencia();
      /*
       * try { ImageIO.write(graficoMedia.image, "JPG", new
       * File("C:\\Users\\Guilherme\\Desktop\\TCC Temp\\Media\\Media (" +
       * this.count + ").jpg")); } catch (final IOException e) { // TODO
       * Auto-generated catch block e.printStackTrace(); }
       */
      // final Grafico plotPontos = new Grafico(255, 55,
      // "Pontos da Somatória das Transições");
      // plotPontos.iniciaPlot(assinatura.InsereListaCaracteres(5, 18, 38, 5,
      // 80));
      // try {
      // ImageIO.write(plotPontos.image, "JPG", new
      // File("C:\\Users\\Guilherme\\Desktop\\TCC Temp\\Slice\\Slice ("
      // + this.count + ").jpg"));
      // } catch (final IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      // grafico.setVisible(true);
      // grafico.setSize(300, 100);
      // grafico.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      // return maiorSoma.DesenhaRetanguloMaiorSoma(this.PassaAltaInteiro());

      // assinatura.InsereListaCaracteresPicos(5, 18, 38, 5, 60);
      final Placa placaPassa = new Placa(this.autoThreshold(placa.getImagem()));
      try {
         ImageIO.write(this.autoThreshold(placa.getImagem()), "JPG", new File("Media (" + this.count + ").jpg"));
      }
      catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      this.count++;
      assinatura.localizaCaracteresVariacaoVertical(placaPassa, 1, 1);
      return assinatura.Fatia();

   }

   public BufferedImage LocalizaTest() {
      final Placa placa = new Placa(this.image);
      final ScannerImagemAssinatura assinatura = new ScannerImagemAssinatura(placa.getImagem());
      final Grafico graficoMedia = new Grafico(assinatura.LocalizaPicosCaracteresTest(10), 255, 300, "Gráfico da Média dos Caracteres");
      graficoMedia.iniciaSequencia();
      graficoMedia.setVisible(true);
      graficoMedia.setSize(350, 350);
      this.processed = assinatura.DesenhaRetanguloCaracteres();
      assinatura.InsereListaCaracteresPicos(7, 30, 38, 4, 20);
      return assinatura.DesenhaRetanguloCaracteres();
      // return maiorSoma.DesenhaRetanguloMaiorSoma(this.image);
   }

   public BufferedImage LocalizaTest2() {
      final int largura = 240;
      final int altura = 40;
      final BufferedImage imEsqueleto = this.Esqueleto();
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage im2 = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);
      final ScannerImagemMedia imagemMedia = new ScannerImagemMedia(imEsqueleto, largura, altura, im);
      imagemMedia.scanear();
      final Ponto p = imagemMedia.PontoMaiorIntensidade();
      final Graphics g = im2.getGraphics();
      g.drawImage(imEsqueleto, 0, 0, null);
      final Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(2.0f));
      g.setColor(Color.red);
      g.drawOval(p.getX(), p.getY(), 20, 20);
      return im2;
      // return maiorSoma.DesenhaRetanguloMaiorSoma(this.image);
   }

   public BufferedImage medianaRGB(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final ArrayList<Integer> listR = new ArrayList<Integer>();
            final ArrayList<Integer> listG = new ArrayList<Integer>();
            final ArrayList<Integer> listB = new ArrayList<Integer>();

            final int RGB = this.getImage().getRGB(i, j);

            listR.add(new Integer(Pixel.getRed(RGB)));
            listG.add(new Integer(Pixel.getGreen(RGB)));
            listB.add(new Integer(Pixel.getBlue(RGB)));

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l1))));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, Math.abs(l)))));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, l1))));

                  } else if (k < 0) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l))));

                  } else if (l < 0) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, Math.abs(l)))));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, l))));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, l1))));

                  } else {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, l))));

                  }
               }
            }

            /*
             * ArrayList<Integer> pixels = new ArrayList<Integer>(); for(int a =
             * 0; a < listR.size(); a++) { pixels.add(Pixel.setRGB(listR.get(a),
             * listG.get(a), listB.get(a), RGB)); } Collections.sort(pixels);
             * im.setRGB(i, j, pixels.get(pixels.size()/2+1));
             * /*Collections.sort(listR, new Comparator<Integer>() {
             * 
             * public int compare(Integer o1, Integer o2) { if(o1 <= o2) return
             * -1; else return 1; } });
             * 
             * 
             * 
             * Collections.sort(listG, new Comparator<Integer>() {
             * 
             * public int compare(Integer o1, Integer o2) { if(o1 <= o2) return
             * -1; else return 1; } });
             * 
             * 
             * 
             * Collections.sort(listB, new Comparator<Integer>() {
             * 
             * public int compare(Integer o1, Integer o2) { if(o1 <= o2) return
             * -1; else return 1; } });
             */
            Collections.sort(listR);
            Collections.sort(listG);
            Collections.sort(listB);
            im.setRGB(i, j,
                     Pixel.setRGB(listR.get(listR.size() / 2 + 1), listG.get(listG.size() / 2 + 1), listB.get(listB.size() / 2 + 1), RGB));

         }
      }
      this.processed = im;
      return im;
   }

   /*
    * public BufferedImage mediumRGB(int mask){
    * 
    * 
    * BufferedImage im = new BufferedImage(image.getWidth(),image.getHeight(),
    * BufferedImage.TYPE_INT_RGB);
    * 
    * int used = mask/2, doubleMask = mask*mask;
    * 
    * for(int i =0; i < image.getWidth(); i++ ){ for(int j = 0; j <
    * image.getHeight(); j++) {
    * 
    * int RGB = image.getRGB(i, j); int r = Pixel.getRed(RGB), g =
    * Pixel.getGreen(RGB), b = Pixel.getBlue(RGB);
    * 
    * if(i == 0 && j == 0) {
    * 
    * for(int k = 0; k <= used; k++) { for(int l = 0; l <= used; l++) {
    * 
    * if((l==0&&k>=1)||(l>=1&&k==0) ) { r += 2*Pixel.getRed(image.getRGB(k, l));
    * g += 2*Pixel.getGreen(image.getRGB(k, l)); b +=
    * 2*Pixel.getBlue(image.getRGB(k, l)); } else { if(k == 0 && l == 0)
    * continue; r += 4*Pixel.getRed(image.getRGB(k, l)); g +=
    * 4*Pixel.getGreen(image.getRGB(k, l)); b += 4*Pixel.getBlue(image.getRGB(k,
    * l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(i==0 && j==(image.getHeight()-1)) {
    * 
    * for(int k = 0; k <= used; k++) { for(int l = (j-used); l <= j; l++) {
    * 
    * if((l==j&&k>=1)||(l< j&&k==0) ) { r += 2*Pixel.getRed(image.getRGB(k, l));
    * g += 2*Pixel.getGreen(image.getRGB(k, l)); b +=
    * 2*Pixel.getBlue(image.getRGB(k, l)); } else { if(k == 0 && l == j)
    * continue; r += 4*Pixel.getRed(image.getRGB(k, l)); g +=
    * 4*Pixel.getGreen(image.getRGB(k, l)); b += 4*Pixel.getBlue(image.getRGB(k,
    * l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(i==(image.getWidth()-1) && j==0) {
    * 
    * for(int k = (i - used); k <= i; k++) { for(int l = 0; l <= j; l++) {
    * 
    * if((l==0&&k<i)||(l>=1&&k==i) ) { r += 2*Pixel.getRed(image.getRGB(k, l));
    * g += 2*Pixel.getGreen(image.getRGB(k, l)); b +=
    * 2*Pixel.getBlue(image.getRGB(k, l)); } else { if(k == i && l == 0)
    * continue; r += 4*Pixel.getRed(image.getRGB(k, l)); g +=
    * 4*Pixel.getGreen(image.getRGB(k, l)); b += 4*Pixel.getBlue(image.getRGB(k,
    * l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(i==(image.getWidth()-1) && j==(image.getHeight()-1)) {
    * 
    * for(int k = (i - used); k <= i; k++) { for(int l = (j-used); l <= j; l++)
    * {
    * 
    * if((l==j && k<i)||(l<j && k==i) ) { r += 2*Pixel.getRed(image.getRGB(k,
    * l)); g += 2*Pixel.getGreen(image.getRGB(k, l)); b +=
    * 2*Pixel.getBlue(image.getRGB(k, l)); } else { if(k == i && l == j)
    * continue; r += 4*Pixel.getRed(image.getRGB(k, l)); g +=
    * 4*Pixel.getGreen(image.getRGB(k, l)); b += 4*Pixel.getBlue(image.getRGB(k,
    * l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(i==0) {
    * 
    * for(int k = 0; k <= used; k++) { for(int l = (j-used); l <= j+used; l++) {
    * 
    * if(l!=j && k==0 ) { r += Pixel.getRed(image.getRGB(k, l)); g +=
    * Pixel.getGreen(image.getRGB(k, l)); b += Pixel.getBlue(image.getRGB(k,
    * l)); } else { if(k == i && l == j) continue; r +=
    * 2*Pixel.getRed(image.getRGB(k, l)); g += 2*Pixel.getGreen(image.getRGB(k,
    * l)); b += 2*Pixel.getBlue(image.getRGB(k, l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(i==(image.getWidth()-1)) {
    * 
    * for(int k = (i-used); k <= i; k++) { for(int l = (j-used); l <= j+used;
    * l++) {
    * 
    * if(l!=j && k==i ) { r += Pixel.getRed(image.getRGB(k, l)); g +=
    * Pixel.getGreen(image.getRGB(k, l)); b += Pixel.getBlue(image.getRGB(k,
    * l)); } else { if(k == i && l == j) continue; r +=
    * 2*Pixel.getRed(image.getRGB(k, l)); g += 2*Pixel.getGreen(image.getRGB(k,
    * l)); b += 2*Pixel.getBlue(image.getRGB(k, l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(j==0) {
    * 
    * for(int k = (i-used); k <= i+used; k++) { for(int l = 0; l <= used; l++) {
    * 
    * if(l==0 && k!=i ) { r += Pixel.getRed(image.getRGB(k, l)); g +=
    * Pixel.getGreen(image.getRGB(k, l)); b += Pixel.getBlue(image.getRGB(k,
    * l)); } else { if(k == i && l == j) continue; r +=
    * 2*Pixel.getRed(image.getRGB(k, l)); g += 2*Pixel.getGreen(image.getRGB(k,
    * l)); b += 2*Pixel.getBlue(image.getRGB(k, l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else if(j==(image.getHeight()-1)) {
    * 
    * for(int k = (i-used); k <= i+used; k++) { for(int l = (j-used); l <= j;
    * l++) {
    * 
    * if(l==j && k!=i ) { r += Pixel.getRed(image.getRGB(k, l)); g +=
    * Pixel.getGreen(image.getRGB(k, l)); b += Pixel.getBlue(image.getRGB(k,
    * l)); } else { if(k == i && l == j) continue; r +=
    * 2*Pixel.getRed(image.getRGB(k, l)); g += 2*Pixel.getGreen(image.getRGB(k,
    * l)); b += 2*Pixel.getBlue(image.getRGB(k, l)); } } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * } else {
    * 
    * for(int k = (i-used); k <= i+used; k++) { for(int l = (j-used); l <=
    * j+used; l++) {
    * 
    * if(k == i && l == j) continue; r += Pixel.getRed(image.getRGB(k, l)); g +=
    * Pixel.getGreen(image.getRGB(k, l)); b += Pixel.getBlue(image.getRGB(k,
    * l));
    * 
    * } }
    * 
    * im.setRGB(i, j, Pixel.setRGB(r/(doubleMask), g/doubleMask, b/doubleMask,
    * RGB));
    * 
    * }
    * 
    * } }
    * 
    * return im; }
    */

   public BufferedImage medianaYIQ(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final ArrayList<Double> listY = new ArrayList<Double>();

            final int RGB = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(RGB);

            listY.add(new Double(yiq.get(0)));

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), Math.abs(l))).get(0)));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l1)).get(0)));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, Math.abs(l))).get(0)));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l1)).get(0)));

                  } else if (k < 0) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l)).get(0)));

                  } else if (l < 0) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, Math.abs(l))).get(0)));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l)).get(0)));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l1)).get(0)));

                  } else {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l)).get(0)));

                  }
               }
            }

            Collections.sort(listY);
            yiq.set(0, listY.get(listY.size() / 2 + 1));

            im.setRGB(i, j, Pixel.yiqToRgbConversion(yiq));

         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage mediumRGB(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2, doubleMask = mask * mask;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int RGB = this.getImage().getRGB(i, j);
            int r = Pixel.getRed(RGB), g = Pixel.getGreen(RGB), b = Pixel.getBlue(RGB);

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), Math.abs(l)));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), l1));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l1));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l1));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, Math.abs(l)));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, l1));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, l1));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, l1));

                  } else if (k < 0) {

                     r += Pixel.getRed(this.getImage().getRGB(Math.abs(k), l));
                     g += Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l));
                     b += Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l));

                  } else if (l < 0) {

                     r += Pixel.getRed(this.getImage().getRGB(k, Math.abs(l)));
                     g += Pixel.getGreen(this.getImage().getRGB(k, Math.abs(l)));
                     b += Pixel.getBlue(this.getImage().getRGB(k, Math.abs(l)));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k1, l));
                     g += Pixel.getGreen(this.getImage().getRGB(k1, l));
                     b += Pixel.getBlue(this.getImage().getRGB(k1, l));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     r += Pixel.getRed(this.getImage().getRGB(k, l1));
                     g += Pixel.getGreen(this.getImage().getRGB(k, l1));
                     b += Pixel.getBlue(this.getImage().getRGB(k, l1));

                  } else {

                     r += Pixel.getRed(this.getImage().getRGB(k, l));
                     g += Pixel.getGreen(this.getImage().getRGB(k, l));
                     b += Pixel.getBlue(this.getImage().getRGB(k, l));

                  }
               }
            }

            im.setRGB(i, j, Pixel.setRGB(r / (doubleMask), g / doubleMask, b / doubleMask, RGB));

         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage mediumYIQ(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2, doubleMask = mask * mask;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int RGB = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(RGB);

            double y = yiq.get(0);

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), Math.abs(l))).get(0);

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l1)).get(0);

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, Math.abs(l))).get(0);

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l1)).get(0);

                  } else if (k < 0) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l)).get(0);

                  } else if (l < 0) {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, Math.abs(l))).get(0);

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l)).get(0);

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l1)).get(0);

                  } else {

                     y += Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l)).get(0);

                  }
               }
            }

            final double mediumY = y / doubleMask;

            yiq.set(0, mediumY);

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            im.setRGB(i, j, rgb);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage minus() {
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int argb = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(argb);

            yiq.set(0, 255.00 - yiq.get(0));

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            final int newRed = Pixel.getRed(rgb);
            final int newGreen = Pixel.getGreen(rgb);
            final int newBlue = Pixel.getBlue(rgb);

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, rgb);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage modaRGB(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final ArrayList<Integer> listR = new ArrayList<Integer>();
            final ArrayList<Integer> listG = new ArrayList<Integer>();
            final ArrayList<Integer> listB = new ArrayList<Integer>();

            final int RGB = this.getImage().getRGB(i, j);

            listR.add(new Integer(Pixel.getRed(RGB)));
            listG.add(new Integer(Pixel.getGreen(RGB)));
            listB.add(new Integer(Pixel.getBlue(RGB)));

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), Math.abs(l)))));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l1))));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, Math.abs(l)))));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, l1))));

                  } else if (k < 0) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(Math.abs(k), l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(Math.abs(k), l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(Math.abs(k), l))));

                  } else if (l < 0) {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, Math.abs(l)))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, Math.abs(l)))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, Math.abs(l)))));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k1, l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k1, l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k1, l))));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, l1))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, l1))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, l1))));

                  } else {

                     listR.add(new Integer(Pixel.getRed(this.getImage().getRGB(k, l))));
                     listG.add(new Integer(Pixel.getGreen(this.getImage().getRGB(k, l))));
                     listB.add(new Integer(Pixel.getBlue(this.getImage().getRGB(k, l))));

                  }
               }
            }

            final HashMap<Integer, Integer> arrayR = new HashMap<Integer, Integer>();

            int lastR = -1;
            for (int a = 0; a < (listR.size() - 1); a++) {
               final Integer pixel = listR.get(a);
               // System.out.println(pixel);
               if (arrayR.containsKey(pixel)) {
                  Integer quant = arrayR.get(pixel);
                  quant++;
                  arrayR.put(pixel, quant);
               } else {
                  arrayR.put(pixel, 1);
               }

               if ((lastR == -1) || (arrayR.get(pixel) > arrayR.get(lastR))) {
                  lastR = pixel;
               }
            }
            // System.out.println();
            final int modaR = lastR;
            // System.out.println("moda R: "+modaR);
            final HashMap<Integer, Integer> arrayG = new HashMap<Integer, Integer>();

            int lastG = -1;
            for (int a = 0; a < (listG.size() - 1); a++) {
               final Integer pixel = listG.get(a);
               if (arrayG.containsKey(pixel)) {
                  Integer quant = arrayG.get(pixel);
                  quant++;
                  arrayG.put(pixel, quant);
               } else {
                  arrayG.put(pixel, 1);
               }

               if ((lastG == -1) || (arrayG.get(pixel) > arrayG.get(lastG))) {
                  lastG = pixel;
               }
            }

            final int modaG = lastG;

            final HashMap<Integer, Integer> arrayB = new HashMap<Integer, Integer>();

            int lastB = -1;
            for (int a = 0; a < (listB.size() - 1); a++) {
               final Integer pixel = listB.get(a);
               if (arrayB.containsKey(pixel)) {
                  Integer quant = arrayB.get(pixel);
                  quant++;
                  arrayB.put(pixel, quant);
               } else {
                  arrayB.put(pixel, 1);
               }

               if ((lastB == -1) || (arrayB.get(pixel) > arrayB.get(lastB))) {
                  lastB = pixel;
               }
            }

            final int modaB = lastB;

            im.setRGB(i, j, Pixel.setRGB(modaR, modaG, modaB, RGB));

         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage modaYIQ(final int mask) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      final int used = mask / 2;

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final ArrayList<Double> listY = new ArrayList<Double>();

            final int RGB = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(RGB);

            listY.add(new Double(yiq.get(0)));

            for (int k = (i - used); k <= i + used; k++) {
               for (int l = (j - used); l <= j + used; l++) {

                  if ((k == i) && (l == j)) {
                     continue;
                  } else if ((k < 0) && (l < 0)) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), Math.abs(l))).get(0)));

                  } else if ((k < 0) && (l >= this.getImage().getHeight())) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l1)).get(0)));

                  } else if ((k >= this.getImage().getWidth()) && (l < 0)) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, Math.abs(l))).get(0)));

                  } else if ((k >= this.getImage().getWidth()) && (l >= this.getImage().getHeight())) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));
                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l1)).get(0)));

                  } else if (k < 0) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(Math.abs(k), l)).get(0)));

                  } else if (l < 0) {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, Math.abs(l))).get(0)));

                  } else if (k >= this.getImage().getWidth()) {

                     final int k1 = this.getImage().getWidth() - (k - (this.getImage().getWidth() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k1, l)).get(0)));

                  } else if (l >= this.getImage().getHeight()) {

                     final int l1 = this.getImage().getHeight() - (l - (this.getImage().getHeight() - 1));

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l1)).get(0)));

                  } else {

                     listY.add(new Double(Pixel.rgbToYiqConversion(this.getImage().getRGB(k, l)).get(0)));

                  }
               }
            }

            final HashMap<Double, Integer> arrayY = new HashMap<Double, Integer>();

            Double lastY = -1.0;
            for (int a = 0; a < (listY.size() - 1); a++) {
               final Double intensidade = listY.get(a);
               // System.out.println(pixel);
               if (arrayY.containsKey(intensidade)) {
                  Integer quant = arrayY.get(intensidade);
                  quant++;
                  arrayY.put(intensidade, quant);
               } else {
                  arrayY.put(intensidade, 1);
               }

               if ((lastY == -1.0) || (arrayY.get(intensidade) > arrayY.get(lastY))) {
                  lastY = intensidade;
               }
            }

            yiq.set(0, lastY);

            im.setRGB(i, j, Pixel.yiqToRgbConversion(yiq));

         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage multiplicativeBrightRGB(final double c) {

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {
            final int argb = this.getImage().getRGB(i, j);

            int newRed = (int) (Pixel.getRed(argb) * c);

            if (newRed < 0) {
               newRed = 0;
            } else if (newRed > 255) {
               newRed = 255;
            }

            int newGreen = (int) (Pixel.getGreen(argb) * c);

            if (newGreen < 0) {
               newGreen = 0;
            } else if (newGreen > 255) {
               newGreen = 255;
            }

            int newBlue = (int) (Pixel.getBlue(argb) * c);

            if (newBlue < 0) {
               newBlue = 0;
            } else if (newBlue > 255) {
               newBlue = 255;
            }

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, argb);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage multiplicativeBrightYIQ(final double c) {
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int argb = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(argb);

            double y = yiq.get(0) * c;

            if (y < 0) {
               y = 0;
            } else if (y > 255) {
               y = 255;
            }

            yiq.set(0, y);

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            final int newRed = Pixel.getRed(rgb);
            final int newGreen = Pixel.getGreen(rgb);
            final int newBlue = Pixel.getBlue(rgb);

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, rgb);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage negativeRGB(final BufferedImage ImageIn) {

      final BufferedImage ImageOut = new BufferedImage(ImageIn.getWidth(), ImageIn.getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < ImageIn.getWidth(); i++) {
         for (int j = 0; j < ImageIn.getHeight(); j++) {
            final int argb = ImageIn.getRGB(i, j);
            final int newRed = 255 - Pixel.getRed(argb);
            final int newGreen = 255 - Pixel.getGreen(argb);
            final int newBlue = 255 - Pixel.getBlue(argb);

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, argb);

            ImageOut.setRGB(i, j, ARGB);
         }
      }
      this.processed = ImageOut;
      return ImageOut;
   }

   public BufferedImage negativeYIQ() {
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int argb = this.getImage().getRGB(i, j);

            final ArrayList<Double> yiq = Pixel.rgbToYiqConversion(argb);

            final double neg = 255.0 - yiq.get(0);

            if ((neg < 0) || (neg > 255)) {
               System.out.println("!!");
            }

            yiq.set(0, neg);

            final int rgb = Pixel.yiqToRgbConversion(yiq);

            final int newRed = Pixel.getRed(rgb);
            final int newGreen = Pixel.getGreen(rgb);
            final int newBlue = Pixel.getBlue(rgb);

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, rgb);

            im.setRGB(i, j, ARGB);
         }
      }

      this.processed = im;
      return im;
   }

   public BufferedImage PassaAltaHorizontal(final BufferedImage imageIn) {
      final int[][] SobelHorizontal = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
      // final int[][] SobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1
      // } };
      long pixelAtual;
      final BufferedImage im = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics g = im.getGraphics();
      g.drawImage(imageIn, 0, 0, null);
      g.dispose();
      final Raster wRaster = im.getRaster();
      final WritableRaster rasteiro = imNova.getRaster();
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelHorizontal[0][0] + wRaster.getSample(i, j - 1, 0)
                     * SobelHorizontal[0][1] + wRaster.getSample(i + 1, j - 1, 0) * SobelHorizontal[0][2] + wRaster.getSample(i - 1, j, 0)
                     * SobelHorizontal[1][0] + wRaster.getSample(i, j, 0) * SobelHorizontal[1][1] + wRaster.getSample(i + 1, j, 0)
                     * SobelHorizontal[1][2] + wRaster.getSample(i - 1, j + 1, 0) * SobelHorizontal[2][0] + wRaster.getSample(i, j + 1, 0)
                     * SobelHorizontal[2][1] + wRaster.getSample(i + 1, j + 1, 0) * SobelHorizontal[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            rasteiro.setSample(i, j, 0, pixelAtual);
         }
      }

      this.processed = imNova;
      return imNova;

   }

   public BufferedImage PassaAltaInteiro() {
      final int[][] SobelHorizontal = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
      final int[][] SobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
      long pixelAtual;
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova2 = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics g = im.getGraphics();
      g.drawImage(this.getImage(), 0, 0, null);
      g.dispose();
      final Raster wRaster = im.getRaster();
      final WritableRaster raster = imNova.getRaster();
      final WritableRaster raster2 = imNova2.getRaster();
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelHorizontal[0][0] + wRaster.getSample(i, j - 1, 0)
                     * SobelHorizontal[0][1] + wRaster.getSample(i + 1, j - 1, 0) * SobelHorizontal[0][2] + wRaster.getSample(i - 1, j, 0)
                     * SobelHorizontal[1][0] + wRaster.getSample(i, j, 0) * SobelHorizontal[1][1] + wRaster.getSample(i + 1, j, 0)
                     * SobelHorizontal[1][2] + wRaster.getSample(i - 1, j + 1, 0) * SobelHorizontal[2][0] + wRaster.getSample(i, j + 1, 0)
                     * SobelHorizontal[2][1] + wRaster.getSample(i + 1, j + 1, 0) * SobelHorizontal[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            raster.setSample(i, j, 0, pixelAtual);
         }
      }

      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelVertical[0][0] + wRaster.getSample(i, j - 1, 0) * SobelVertical[0][1]
                     + wRaster.getSample(i + 1, j - 1, 0) * SobelVertical[0][2] + wRaster.getSample(i - 1, j, 0) * SobelVertical[1][0]
                     + wRaster.getSample(i, j, 0) * SobelVertical[1][1] + wRaster.getSample(i + 1, j, 0) * SobelVertical[1][2]
                     + wRaster.getSample(i - 1, j + 1, 0) * SobelVertical[2][0] + wRaster.getSample(i, j + 1, 0) * SobelVertical[2][1]
                     + wRaster.getSample(i + 1, j + 1, 0) * SobelVertical[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            raster2.setSample(i, j, 0, pixelAtual);
         }
      }
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = raster.getSample(i, j, 0) + raster2.getSample(i, j, 0);
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            raster2.setSample(i, j, 0, pixelAtual);
         }
      }
      this.processed = imNova2;
      return imNova2;

   }

   public BufferedImage PassaAltaInteiro(final BufferedImage imageIn) {
      // Matriz de convolução Sobel
      final int[][] SobelHorizontal = { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
      final int[][] SobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
      long pixelAtual;
      /*
       * Imagens para a convolução sendo "im" a imagem obtida a partir da imagem
       * original da classe , "imNova" a imagem que recebe o filtro horizontal e
       * imNova2 que recebe o filtro vertical
       */
      final BufferedImage im = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova2 = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      // im recebe a imagem original
      final Graphics g = im.getGraphics();
      g.drawImage(imageIn, 0, 0, null);
      g.dispose();
      /*
       * São criados os objetos "Raster" a partir das imagens criadas sendo o
       * objeto "Raster" um objeto que só permite leitura e "WritableRaster" um
       * objeto que permite leitura e escrita
       */
      final Raster wRaster = im.getRaster();
      final WritableRaster rasteiro = imNova.getRaster();
      final WritableRaster rasteiro2 = imNova2.getRaster();

      // Primeira convolução com a máscara Sobel Horizontal
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelHorizontal[0][0] + wRaster.getSample(i, j - 1, 0)
                     * SobelHorizontal[0][1] + wRaster.getSample(i + 1, j - 1, 0) * SobelHorizontal[0][2] + wRaster.getSample(i - 1, j, 0)
                     * SobelHorizontal[1][0] + wRaster.getSample(i, j, 0) * SobelHorizontal[1][1] + wRaster.getSample(i + 1, j, 0)
                     * SobelHorizontal[1][2] + wRaster.getSample(i - 1, j + 1, 0) * SobelHorizontal[2][0] + wRaster.getSample(i, j + 1, 0)
                     * SobelHorizontal[2][1] + wRaster.getSample(i + 1, j + 1, 0) * SobelHorizontal[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            rasteiro.setSample(i, j, 0, pixelAtual);
         }
      }
      // Segunda Convolução com a máscara sobel Vertical
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelVertical[0][0] + wRaster.getSample(i, j - 1, 0) * SobelVertical[0][1]
                     + wRaster.getSample(i + 1, j - 1, 0) * SobelVertical[0][2] + wRaster.getSample(i - 1, j, 0) * SobelVertical[1][0]
                     + wRaster.getSample(i, j, 0) * SobelVertical[1][1] + wRaster.getSample(i + 1, j, 0) * SobelVertical[1][2]
                     + wRaster.getSample(i - 1, j + 1, 0) * SobelVertical[2][0] + wRaster.getSample(i, j + 1, 0) * SobelVertical[2][1]
                     + wRaster.getSample(i + 1, j + 1, 0) * SobelVertical[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            rasteiro2.setSample(i, j, 0, pixelAtual);
         }
      }
      // Junção dos resultados obtidos nas duas imagens
      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = rasteiro.getSample(i, j, 0) + rasteiro2.getSample(i, j, 0);
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            rasteiro2.setSample(i, j, 0, pixelAtual);
         }
      }
      this.processed = imNova2;
      return imNova2;

   }

   public BufferedImage PassaAltaVertical() {
      final int[][] SobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
      long pixelAtual;
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final BufferedImage imNova2 = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics g = im.getGraphics();
      g.drawImage(this.getImage(), 0, 0, null);
      g.dispose();
      final Raster wRaster = im.getRaster();
      final WritableRaster raster = imNova2.getRaster();

      for (int i = 1; i < im.getWidth() - 1; i++) {
         for (int j = 1; j < im.getHeight() - 1; j++) {
            pixelAtual = wRaster.getSample(i - 1, j - 1, 0) * SobelVertical[0][0] + wRaster.getSample(i, j - 1, 0) * SobelVertical[0][1]
                     + wRaster.getSample(i + 1, j - 1, 0) * SobelVertical[0][2] + wRaster.getSample(i - 1, j, 0) * SobelVertical[1][0]
                     + wRaster.getSample(i, j, 0) * SobelVertical[1][1] + wRaster.getSample(i + 1, j, 0) * SobelVertical[1][2]
                     + wRaster.getSample(i - 1, j + 1, 0) * SobelVertical[2][0] + wRaster.getSample(i, j + 1, 0) * SobelVertical[2][1]
                     + wRaster.getSample(i + 1, j + 1, 0) * SobelVertical[2][2];
            if (pixelAtual > 255) {
               pixelAtual = 255;
            } else if (pixelAtual < 0) {
               pixelAtual = 0;
            }
            raster.setSample(i, j, 0, pixelAtual);
         }
      }
      /*
       * for (int i = 1; i < im.getWidth() - 1; i++) { for (int j = 1; j <
       * im.getHeight() - 1; j++) { pixelAtual = rasteiro.getSample(i, j, 0) +
       * rasteiro2.getSample(i, j, 0); if (pixelAtual > 255) { pixelAtual = 255;
       * } else if (pixelAtual < 0) { pixelAtual = 0; } rasteiro2.setSample(i,
       * j, 0, pixelAtual); } }
       */
      this.processed = imNova2;
      return imNova2;

   }

   private void reconhecerCaracteres(final LinkedList<BufferedImage> bufferedImages) {
      try {
         new Thread(new Runnable() {

            @Override
            public void run() {
               try {
                  final Reconhecedor reconhecedor = new Reconhecedor(bufferedImages);
                  reconhecedor.setVisible(true);
               }
               catch (final ParserConfigurationException e) {
                  e.printStackTrace();
               }
               catch (final SAXException e) {
                  e.printStackTrace();
               }
               catch (final IOException e) {
                  e.printStackTrace();
               }
               catch (final TransformerException e) {
                  e.printStackTrace();
               }
            }
         }).start();

      }
      catch (final Exception e) {
         JOptionPane.showMessageDialog(null, "Não foi possivel reconhecer os caracteres:\n" + e.getMessage(), "Atenção",
                  JOptionPane.WARNING_MESSAGE);
      }
   }

   public void setImage(final BufferedImage image) {
      this.image = image;
   }

   public BufferedImage sumOfTwoRGB(final BufferedImage bi) throws DifferentSizes {

      if ((this.getImage().getWidth() != bi.getWidth()) || (this.getImage().getHeight() != bi.getHeight())) {
         throw new DifferentSizes();
      }

      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < this.getImage().getWidth(); i++) {
         for (int j = 0; j < this.getImage().getHeight(); j++) {

            final int argb = this.getImage().getRGB(i, j);
            final int argb2 = bi.getRGB(i, j);

            final int newRed = (Pixel.getRed(argb) + Pixel.getRed(argb2)) / 2;
            final int newGreen = (Pixel.getGreen(argb) + Pixel.getGreen(argb2)) / 2;
            final int newBlue = (Pixel.getBlue(argb) + Pixel.getBlue(argb2)) / 2;

            final int ARGB = Pixel.setRGB(newRed, newGreen, newBlue, (argb + argb2) / 2);

            im.setRGB(i, j, ARGB);
         }
      }
      this.processed = im;
      return im;
   }

   public BufferedImage Threshold(final BufferedImage imagem) {
      final BufferedImage im = new BufferedImage(imagem.getWidth(), imagem.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      for (int i = 0; i < imagem.getWidth(); i++) {
         for (int j = 0; j < imagem.getHeight(); j++) {
            if (imagem.getRaster().getSample(i, j, 0) > 125) {
               im.getRaster().setSample(i, j, 0, 255);
            } else {
               im.getRaster().setSample(i, j, 0, 0);

            }
         }
      }
      return im;
   }

   public BufferedImage ToGray() {
      final BufferedImage im = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      final Graphics g = im.getGraphics();
      g.drawImage(this.getImage(), 0, 0, null);
      g.dispose();
      this.processed = im;
      return im;
   }

}
