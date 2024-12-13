package filtros;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class ScannerImagemAssinatura {

   private final LinkedList<BufferedImage> caracteres = new LinkedList<BufferedImage>();
   private BufferedImage imagem;
   private final LinkedList<Pixel> listaLocalizados = new LinkedList<Pixel>();
   private final LinkedList<Pixel> listaPicosCaracteres = new LinkedList<Pixel>();
   private final LinkedList<Pixel> listaPixel = new LinkedList<Pixel>();
   private final LinkedList<Ponto> listaPontosCaracteres = new LinkedList<Ponto>();
   private final Retangulo RetCaracteresPlaca = new Retangulo();

   public ScannerImagemAssinatura(final BufferedImage im) {
      this.imagem = im;
   }

   public ScannerImagemAssinatura(final Placa placa) {
      this.imagem = placa.getImagem();
   }

   public BufferedImage DesenhaRetanguloCaracteres() {
      final BufferedImage im = new BufferedImage(this.imagem.getWidth(), this.imagem.getHeight(), BufferedImage.TYPE_INT_RGB);
      final Graphics g = im.getGraphics();
      g.drawImage(this.imagem, 0, 0, null);
      final Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(2.0f));
      g.setColor(Color.red);
      final int xIni = this.RetCaracteresPlaca.getPontoInicial().getX();
      final int yIni = this.RetCaracteresPlaca.getPontoInicial().getY();
      final int xFim = this.RetCaracteresPlaca.getPontoFinal().getX();
      final int yFim = this.RetCaracteresPlaca.getPontoFinal().getY();
      g.drawLine(xIni, yIni, xFim, yIni);
      g.drawLine(xIni, yFim, xFim, yFim);
      g.drawLine(xIni, yIni, xIni, yFim);
      g.drawLine(xFim, yFim, xFim, yIni);
      g.dispose();
      return im;
   }

   public BufferedImage DesenhaRetanguloCaracteres(final Placa placa) {
      final BufferedImage im = new BufferedImage(placa.getImagem().getWidth(), placa.getImagem().getHeight(), BufferedImage.TYPE_INT_RGB);
      final Graphics g = im.getGraphics();
      g.drawImage(placa.getImagem(), 0, 0, null);
      final Graphics2D g2 = (Graphics2D) g;
      g2.setStroke(new BasicStroke(2.0f));
      g.setColor(Color.red);
      final int xIni = this.RetCaracteresPlaca.getPontoInicial().getX();
      final int yIni = this.RetCaracteresPlaca.getPontoInicial().getY();
      final int xFim = this.RetCaracteresPlaca.getPontoFinal().getX();
      final int yFim = this.RetCaracteresPlaca.getPontoFinal().getY();
      g.drawLine(xIni, yIni, xFim, yIni);
      g.drawLine(xIni, yFim, xFim, yFim);
      g.dispose();
      return im;
   }

   public BufferedImage Fatia() {
      final int yIni = this.RetCaracteresPlaca.getPontoInicial().getY();
      final int yFim = this.RetCaracteresPlaca.getPontoFinal().getY();
      final BufferedImage im = new BufferedImage(this.imagem.getWidth(), this.imagem.getHeight(), BufferedImage.TYPE_INT_RGB);
      final Graphics g = im.getGraphics();
      g.drawImage(this.imagem, 0, 0, null);
      g.setColor(Color.red);
      for (final Ponto p : this.listaPontosCaracteres) {
         g.drawLine(p.getX(), yIni, p.getX(), yFim);
      }
      g.dispose();
      return im;
   }

   public LinkedList<Pixel> GetListaPixelsMeio() {

      for (int i = 0; i < this.imagem.getWidth(); i++) {
         final Ponto pontoAtual = new Ponto(i, this.imagem.getHeight() / 2);
         final int tonAtual = this.imagem.getRaster().getSample(i, this.imagem.getHeight() / 2, 0);
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPixel.add(pixelAtual);
      }

      return this.listaPixel;
   }

   public LinkedList<Pixel> iniciaListaPicos(final int distanciaPixels, final int diferencaIntensidade) {
      int tonAnterior = 0;
      int tonAtual = 0;
      tonAnterior = this.imagem.getRaster().getSample(0, 0, 0);
      for (int j = 0; j < this.imagem.getHeight(); j++) {
         for (int i = 0; i < this.imagem.getWidth(); i = i + distanciaPixels) {
            tonAtual = this.imagem.getRaster().getSample(i, j, 0);
            if ((tonAnterior - tonAtual) >= diferencaIntensidade) {
               final Ponto pontoAtual = new Ponto(i, j);
               final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
               this.listaLocalizados.add(pixelAtual);
            }
            tonAnterior = this.imagem.getRaster().getSample(i, j, 0);
         }
      }
      return this.listaLocalizados;
   }

   public LinkedList<Pixel> iniciaListaPicosCaracteres(final int distanciaPixels, final int diferencaIntensidade,
            final int distanciaCaracMax, final int distanciaCaracMin, final int quantidadePixMax, final int quantidadePixMin) {
      int tonAnterior = 0;
      int tonAtual = 0;
      int quantidadePixels = 0;
      boolean PrimeiroPontoIncializado = false;
      final LinkedList<Pixel> listaTemp = new LinkedList<Pixel>();
      tonAnterior = this.imagem.getRaster().getSample(0, 0, 0);
      Ponto pontoAnterior = new Ponto();
      for (int j = 0; j < this.imagem.getHeight(); j++) {
         for (int i = 0; i < this.imagem.getWidth(); i = i + distanciaPixels) {
            tonAtual = this.imagem.getRaster().getSample(i, j, 0);
            if (Math.abs(tonAnterior - tonAtual) >= diferencaIntensidade) {
               final Ponto pontoAtual = new Ponto(i, j);
               final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
               if (PrimeiroPontoIncializado == false) {
                  PrimeiroPontoIncializado = true;
                  listaTemp.add(pixelAtual);
                  quantidadePixels++;
               } else {
                  if (pontoAnterior.getY() != pontoAtual.getY()) {
                     if ((quantidadePixels >= quantidadePixMin) && (quantidadePixels <= quantidadePixMax)) {
                        this.listaLocalizados.addAll(listaTemp);
                     }
                     listaTemp.clear();
                     quantidadePixels = 0;
                  } else {
                     if ((pontoAnterior.getX() != pontoAtual.getX()) && (pontoAtual.getX() - pontoAnterior.getX() >= distanciaCaracMin)
                              && (pontoAtual.getX() - pontoAnterior.getX() <= distanciaCaracMax)) {
                        listaTemp.add(pixelAtual);
                        quantidadePixels++;
                     }
                  }

               }
               pontoAnterior = new Ponto(i, j);
            }
            tonAnterior = this.imagem.getRaster().getSample(i, j, 0);
         }
      }
      return this.listaLocalizados;
   }

   public Retangulo inicializaRetanguloCaracteres(final int erroYini, final int erroYfim) {
      if (!this.listaLocalizados.isEmpty()) {
         final int xInicial = 0;
         final int xFinal = this.imagem.getWidth();
         int yInicial;
         int yFinal;
         if (this.listaLocalizados.getFirst().getPonto().getY() - erroYini > 0) {
            yInicial = this.listaLocalizados.getFirst().getPonto().getY() - erroYini;
         } else {
            yInicial = 0;
         }
         if (this.listaLocalizados.getLast().getPonto().getY() + erroYfim < this.imagem.getHeight()) {
            yFinal = this.listaLocalizados.getLast().getPonto().getY() + erroYfim;
         } else {
            yFinal = this.imagem.getHeight();
         }
         final Ponto pontoInicial = new Ponto(xInicial, yInicial);
         final Ponto pontoFinal = new Ponto(xFinal, yFinal);
         this.RetCaracteresPlaca.setPontoInicial(pontoInicial);
         this.RetCaracteresPlaca.setPontoFinal(pontoFinal);
      }
      return this.RetCaracteresPlaca;
   }

   public Retangulo inicializaRetanguloCaracteresTotal(final int erroYini, final int erroYfim) {
      if (!this.listaLocalizados.isEmpty()) {
         int xInicial = this.imagem.getWidth();
         int xFinal = 0;
         int yInicial;
         int yFinal;
         if (this.listaLocalizados.getFirst().getPonto().getY() - erroYini > 0) {
            yInicial = this.listaLocalizados.getFirst().getPonto().getY() - erroYini;
         } else {
            yInicial = 0;
         }
         if (this.listaLocalizados.getLast().getPonto().getY() + erroYfim < this.imagem.getHeight()) {
            yFinal = this.listaLocalizados.getLast().getPonto().getY() + erroYfim;
         } else {
            yFinal = this.imagem.getHeight();
         }
         for (final Pixel pixelAtual : this.listaLocalizados) {
            if (xInicial > pixelAtual.getPonto().getX()) {
               xInicial = pixelAtual.getPonto().getX();
            }
            if (xFinal < pixelAtual.getPonto().getX()) {
               xFinal = pixelAtual.getPonto().getX();
            }
         }
         final Ponto pontoInicial = new Ponto(xInicial, yInicial);
         final Ponto pontoFinal = new Ponto(xFinal, yFinal);
         this.RetCaracteresPlaca.setPontoInicial(pontoInicial);
         this.RetCaracteresPlaca.setPontoFinal(pontoFinal);
      }
      return this.RetCaracteresPlaca;
   }

   public LinkedList<Ponto> InsereListaCaracteresPicos(final int variacaoPixels, final int distanciaMin, final int distanciaMax,
            final int difIntensidadeMin, final int diIntensidadeMax) {
      Pixel pixelAnterior = this.listaPicosCaracteres.getFirst();
      boolean primeiraiteracao = true;
      for (int x = 0; x < this.listaPicosCaracteres.size(); x = x + variacaoPixels) {
         final Pixel pixelAtual = this.listaPicosCaracteres.get(x);
         if (primeiraiteracao == true) {
            if ((Math.abs(pixelAtual.getTonCinza() - pixelAnterior.getTonCinza()) >= difIntensidadeMin)
                     && (Math.abs(pixelAtual.getTonCinza() - pixelAnterior.getTonCinza()) <= diIntensidadeMax)) {
               this.listaPontosCaracteres.add(pixelAtual.getPonto());
               primeiraiteracao = false;
            }
         } else {
            if ((Math.abs(pixelAtual.getTonCinza() - pixelAnterior.getTonCinza()) >= difIntensidadeMin)
                     && (Math.abs(pixelAtual.getTonCinza() - pixelAnterior.getTonCinza()) <= diIntensidadeMax)) {
               this.listaPontosCaracteres.add(pixelAtual.getPonto());
            }
         }
         pixelAnterior = pixelAtual;
      }

      return this.listaPontosCaracteres;

   }

   public LinkedList<BufferedImage> localizaCaracteresVariacaoVertical(final Placa pla, final int variacaoMin, final int variacao) {
      int tomAnterior;
      int tomAtual;
      int totalVariacao = 0;
      for (int i = 0; i < this.RetCaracteresPlaca.getPontoFinal().getX(); i++) {
         tomAnterior = pla.getImagem().getRaster().getSample(i, this.RetCaracteresPlaca.getPontoInicial().getY(), 0);
         for (int j = this.RetCaracteresPlaca.getPontoInicial().getY() + 1; j < this.RetCaracteresPlaca.getPontoFinal().getY(); j++) {
            tomAtual = pla.getImagem().getRaster().getSample(i, j, 0);
            if (Math.abs(tomAnterior - tomAtual) > variacaoMin) {
               totalVariacao++;
            }
         }
         if (totalVariacao >= variacao) {
            this.listaPontosCaracteres.add(new Ponto(i, this.RetCaracteresPlaca.getPontoFinal().getY()));
         }
         totalVariacao = 0;
      }
      if (!this.listaPontosCaracteres.isEmpty()) {
         int xAnterior = this.listaPontosCaracteres.getFirst().getX();
         int xAtual;
         final LinkedList<Ponto> listaTemp = new LinkedList<Ponto>();
         for (int i = 1; i < this.listaPontosCaracteres.size(); i++) {
            final Ponto p = this.listaPontosCaracteres.get(i);
            xAtual = p.getX();
            if ((xAtual == (xAnterior + 1)) && (!(i == (this.listaPontosCaracteres.size() - 1)))) {
               listaTemp.add(p);
            } else if (!listaTemp.isEmpty()) {
               final int largura = (listaTemp.getLast().getX() - listaTemp.getFirst().getX()) + 1;
               final int altura = this.RetCaracteresPlaca.getPontoFinal().getY() - this.RetCaracteresPlaca.getPontoInicial().getY();
               final BufferedImage im = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_GRAY);
               int x = 0;
               for (final Ponto pTemp : listaTemp) {
                  int y = 0;
                  for (int j = this.RetCaracteresPlaca.getPontoInicial().getY(); j < this.RetCaracteresPlaca.getPontoFinal().getY(); j++) {
                     final int tomTemp = this.imagem.getRaster().getSample(pTemp.getX(), j, 0);
                     im.getRaster().setSample(x, y, 0, tomTemp);
                     y++;
                  }
                  x++;
               }
               this.caracteres.add(im);
               listaTemp.clear();
            }
            xAnterior = p.getX();
         }
      }
      return this.caracteres;
   }

   public LinkedList<Pixel> InsereListaPixelsRetBufferd() {
      for (int i = 0; i < this.imagem.getWidth(); i++) {
         final Ponto pontoAtual = new Ponto(i, 0);
         final int tonAtual;
         tonAtual = this.imagem.getRaster().getSample(i, 0, 0);
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPixel.add(pixelAtual);
      }
      int index;
      for (int j = 0; j < this.imagem.getHeight(); j++) {
         index = 0;
         for (int i = 0; i < this.imagem.getWidth(); i++) {
            final int tontemp = this.listaPixel.get(index).getTonCinza() + this.imagem.getRaster().getSample(i, j, 0);
            this.listaPixel.get(index).setTonCinza(tontemp);
            index++;

         }
      }
      return this.listaPixel;
   }

   public LinkedList<Pixel> LocalizaPicosCaracteres(final int larguraCaractere) {
      int soma;
      int maiorPico = 0;
      final int Max = 255;
      int menorPico = Max;
      for (int i = 0; i < this.imagem.getWidth(); i++) {
         soma = 0;
         for (int x = i; (x < i + larguraCaractere) && (x < this.imagem.getWidth()); x++) {
            for (int y = this.RetCaracteresPlaca.getPontoInicial().getY(); y < this.RetCaracteresPlaca.getPontoFinal().getY(); y++) {
               soma = soma + this.imagem.getRaster().getSample(x, y, 0);
            }
         }
         if (soma > maiorPico) {
            maiorPico = soma;
         }
         if (soma < menorPico) {
            menorPico = soma;
         }
         final Ponto pontoAtual = new Ponto(i, this.RetCaracteresPlaca.getPontoFinal().getY());
         final int tonAtual = soma;
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPicosCaracteres.add(pixelAtual);
      }
      for (int i = 0; i < this.listaPicosCaracteres.size(); i++) {
         final Pixel pixelMedia = this.listaPicosCaracteres.get(i);
         final int novoTom;
         if ((maiorPico - menorPico) != 0) {
            novoTom = (Max * (pixelMedia.getTonCinza() - menorPico)) / (maiorPico - menorPico);
         } else {
            novoTom = 0;
         }
         this.listaPicosCaracteres.get(i).setTonCinza(novoTom);
      }

      return this.listaPicosCaracteres;
   }

   public LinkedList<Pixel> LocalizaPicosCaracteres(final int larguraCaractere, final Placa placa) {
      int soma;
      int maiorPico = 0;
      final int Max = 255;
      int menorPico = Max;
      for (int i = 0; i < placa.getImagem().getWidth(); i++) {
         soma = 0;
         for (int x = i; (x < i + larguraCaractere) && (x < placa.getImagem().getWidth()); x++) {
            for (int y = this.RetCaracteresPlaca.getPontoInicial().getY(); y < this.RetCaracteresPlaca.getPontoFinal().getY(); y++) {
               soma = soma + placa.getImagem().getRaster().getSample(x, y, 0);
               if (soma > maiorPico) {
                  maiorPico = soma;
               }
               if (soma < menorPico) {
                  menorPico = soma;
               }
            }
         }
         final Ponto pontoAtual = new Ponto(i, this.RetCaracteresPlaca.getPontoFinal().getY());
         final int tonAtual = soma;
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPicosCaracteres.add(pixelAtual);
      }
      for (int i = 0; i < this.listaPicosCaracteres.size(); i++) {
         final Pixel pixelMedia = this.listaPicosCaracteres.get(i);
         int novoTom;
         if ((maiorPico - menorPico) != 0) {
            novoTom = (Max * (pixelMedia.getTonCinza() - menorPico)) / (maiorPico - menorPico);
         } else {
            novoTom = 0;
         }
         this.listaPicosCaracteres.get(i).setTonCinza(novoTom);
      }
      return this.listaPicosCaracteres;
   }

   public LinkedList<Pixel> LocalizaPicosCaracteresTest(final int larguraCaractere) {
      int soma;
      int maiorPico = 0;
      final int Max = 255;
      int menorPico = Max;
      for (int i = 0; i < this.imagem.getWidth(); i++) {
         soma = 0;
         for (int x = i; (x < i + larguraCaractere) && (x < this.imagem.getWidth()); x++) {
            for (int y = 0; y < this.imagem.getHeight(); y++) {
               soma = soma + this.imagem.getRaster().getSample(x, y, 0);
            }
         }
         if (soma > maiorPico) {
            maiorPico = soma;
         }
         if (soma < menorPico) {
            menorPico = soma;
         }
         final Ponto pontoAtual = new Ponto(i, this.RetCaracteresPlaca.getPontoFinal().getY());
         final int tonAtual = soma;
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPicosCaracteres.add(pixelAtual);
      }
      for (int i = 0; i < this.listaPicosCaracteres.size(); i++) {
         final Pixel pixelMedia = this.listaPicosCaracteres.get(i);
         final int novoTom = (Max * (pixelMedia.getTonCinza() - menorPico)) / (maiorPico - menorPico);
         this.listaPicosCaracteres.get(i).setTonCinza(novoTom);
      }
      return this.listaPicosCaracteres;
   }

   public LinkedList<Pixel> PrintTons() {
      for (int i = 0; i < this.imagem.getWidth(); i++) {
         final Ponto pontoAtual = new Ponto(i, 0);
         final int tonAtual;
         tonAtual = this.imagem.getRaster().getSample(i, 0, 0);
         final Pixel pixelAtual = new Pixel(pontoAtual, tonAtual);
         this.listaPixel.add(pixelAtual);
         System.out.print(tonAtual + " ");
      }
      System.out.println();
      int index;
      for (int j = 0; j < this.imagem.getHeight(); j++) {
         index = 0;
         for (int i = 0; i < this.imagem.getWidth(); i++) {
            final int tontemp = this.listaPixel.get(index).getTonCinza() + this.imagem.getRaster().getSample(i, j, 0);
            this.listaPixel.get(index).setTonCinza(tontemp);
            System.out.print(this.imagem.getRaster().getSample(i, j, 0) + " ");
            index++;
         }
         System.out.println();
      }
      return this.listaPixel;
   }

   public void setPlaca(final Placa placa) {
      this.imagem = placa.getImagem();
   }

   public BufferedImage Threshold() {
      final BufferedImage im = this.imagem;
      int somatoria = 0;
      for (final Pixel pixelNew : this.listaPixel) {
         somatoria = pixelNew.getTonCinza() + somatoria;
      }
      somatoria = somatoria / 255;
      for (int i = 0; i < this.imagem.getWidth(); i++) {
         for (int j = 0; j < this.imagem.getHeight(); j++) {
            try {
               if (this.imagem.getRaster().getSample(i, j, 0) > 220) {
                  im.getRaster().setSample(i, j, 0, 255);
               } else {
                  im.getRaster().setSample(i, j, 0, 0);

               }
            }
            catch (final Exception e) {
               System.out.println(i + " " + j);
            }
         }
      }
      return im;
   }
}
