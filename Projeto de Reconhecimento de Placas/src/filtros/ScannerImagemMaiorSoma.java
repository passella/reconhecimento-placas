package filtros;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ScannerImagemMaiorSoma extends AbstractScannerImagem {
	public static int JANELA_INTEIRA = 1;
	public static int JANELA_OCA = 2;
	private final int alturaRetangulo;
	private final int correcaoErroX;
	private final int correcaoErroY;
	private int janela[][];
	private final int larguraRetangulo;
	private final Ponto pontoSomaMax = new Ponto();
	private int somaMax = 0;
	private int tamBorda = 1;
	private final int tipoJanela;

	public ScannerImagemMaiorSoma(final BufferedImage image, final int largura, final int altura, final Ponto ponto,
	         final float divisor) {

		super(image, largura, altura, ponto, divisor);
		this.tipoJanela = ScannerImagemMaiorSoma.JANELA_INTEIRA;
		this.larguraRetangulo = this.larguraJanela;
		this.alturaRetangulo = this.alturaJanela;
		if (this.tipoJanela == ScannerImagemMaiorSoma.JANELA_OCA) {
			this.InicializaJanelaOca();
		}
		this.correcaoErroX = 0;
		this.correcaoErroY = 0;

	}

	public ScannerImagemMaiorSoma(final BufferedImage image, final int largura, final int altura, final Ponto ponto,
	         final float divisor, final int larguraRet, final int alturaRet, final int tipoJan, final int correcErroX,
	         final int correcErroY) {
		super(image, largura, altura, ponto, divisor);
		this.tipoJanela = tipoJan;
		this.larguraRetangulo = larguraRet;
		this.alturaRetangulo = alturaRet;
		this.correcaoErroX = correcErroX;
		this.correcaoErroY = correcErroY;
		if (this.tipoJanela == ScannerImagemMaiorSoma.JANELA_OCA) {
			this.InicializaJanelaOca();
		}

	}

	public BufferedImage DesenhaRetanguloMaiorSoma(final BufferedImage image) {
		final BufferedImage imageOut = new BufferedImage(this.getImage().getWidth(), this.getImage().getHeight(),
		         BufferedImage.TYPE_INT_RGB);
		final Graphics g = imageOut.getGraphics();
		g.drawImage(image, 0, 0, null);
		final Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2.0f));
		g.setColor(Color.red);
		final int xIni;
		final int yIni;
		final int xFim;
		final int yFim;
		xIni = this.correcaoErroX + (this.pontoSomaMax.getX());
		yIni = this.correcaoErroY + (this.pontoSomaMax.getY());
		xFim = this.correcaoErroX + (this.pontoSomaMax.getX() + this.larguraRetangulo);
		yFim = this.correcaoErroY + (this.pontoSomaMax.getY() + this.alturaRetangulo);
		g.drawLine(xIni, yIni, xFim, yIni);
		g.drawLine(xIni, yFim, xFim, yFim);
		g.drawLine(xIni, yIni, xIni, yFim);
		g.drawLine(xFim, yIni, xFim, yFim);
		g.dispose();
		return imageOut;

		/*
		 * final BufferedImage im = new BufferedImage(this.getImage().getWidth(),
		 * this.getImage().getHeight(), BufferedImage.TYPE_BYTE_GRAY); for (int x
		 * = 0; x < this.getLargura(); x++) { for (int y = 0; y <
		 * this.getAltura(); y++) { if ((y >= this.getAltura() - this.tamBorda) ||
		 * (y < this.getAltura() - (this.getAltura() - this.tamBorda)) || (x >=
		 * this.getLargura() - this.tamBorda) || (x < this.getLargura() -
		 * (this.getLargura() - this.tamBorda))) { im.getRaster().setSample(x +
		 * 30, y + 50, 0, 255); } else { im.getRaster().setSample(x + 30, y + 50,
		 * 0, 0); } } } return im;
		 */
	}

	private void EncontraMaiorSoma(final int x, final int y) {
		int Soma = 0;
		for (int xAux = x; (xAux < this.getLargura() + x) && (xAux < this.getImage().getWidth()); xAux++) {
			for (int yAux = y; (yAux < this.getAltura() + y) && (yAux < this.getImage().getHeight()); yAux++) {
				Soma += this.getImage().getRaster().getSample(xAux, yAux, 0);
			}
		}
		if (Soma > this.somaMax) {
			this.somaMax = Soma;
			this.pontoSomaMax.setX(x);
			this.pontoSomaMax.setY(y);
		}

	}

	private void EncontraMaiorSomaJanelaOca(final int x, final int y) {
		int Soma = 0;
		int xJanela = 0;
		int yJanela = 0;
		for (int xAux = x; (xAux < this.getLargura() + x) && (xAux < this.getImage().getWidth()); xAux++) {
			yJanela = 0;
			for (int yAux = y; (yAux < this.getAltura() + y) && (yAux < this.getImage().getHeight()); yAux++) {
				Soma += this.getImage().getRaster().getSample(xAux, yAux, 0) * this.janela[xAux][yAux];
				yJanela++;
			}
			xJanela++;
		}
		if (Soma > this.somaMax) {
			this.somaMax = Soma;
			this.pontoSomaMax.setX(x);
			this.pontoSomaMax.setY(y);
		}
	}

	public Ponto getPontoMaiorSoma() {
		return this.pontoSomaMax;
	}

	public Ponto getPontoSomaMax() {
		return this.pontoSomaMax;
	}

	public int getTamBorda() {
		return this.tamBorda;
	}

	public void InicializaJanelaOca() {
		this.janela = new int[this.getLargura()][this.getAltura()];
		for (int x = 0; x < this.getLargura(); x++) {
			for (int y = 0; y < this.getAltura(); y++) {
				if ((y >= this.getAltura() - this.getTamBorda())
				         || (y < this.getAltura() - (this.getAltura() - this.getTamBorda()))
				         || (x >= this.getLargura() - this.getTamBorda())
				         || (x < this.getLargura() - (this.getLargura() - this.getTamBorda()))) {
					this.janela[x][y] = 1;
				} else {
					this.janela[x][y] = 0;
				}
			}
		}
		for (int i = 0; i < this.getAltura(); i++) {
			for (int j = 0; j < this.getLargura(); j++) {
				System.out.print(this.janela[j][i]);
			}
			System.out.print("\n");
		}
	}

	@Override
	protected void scanear(final int x, final int y) {
		if (this.tipoJanela == ScannerImagemMaiorSoma.JANELA_OCA) {
			this.EncontraMaiorSomaJanelaOca(x, y);
		} else {
			this.EncontraMaiorSoma(x, y);
		}

	}

	public void setTamBorda(final int tamBorda) {
		this.tamBorda = tamBorda;
	}

}
