package filtros;

import java.awt.image.BufferedImage;

public abstract class AbstractScannerImagem {

	protected final int alturaJanela;
	private float divisorJanela;
	private final BufferedImage image;
	protected final int larguraJanela;
	private Ponto pontoCentral = null;

	public AbstractScannerImagem(final BufferedImage image, final int largura, final int altura) {
		this.image = image;
		this.larguraJanela = largura;
		this.alturaJanela = altura;
	}

	public AbstractScannerImagem(final BufferedImage image, final int largura, final int altura, final Ponto ponto) {
		this.image = image;
		this.larguraJanela = largura;
		this.alturaJanela = altura;
		this.pontoCentral = ponto;
		this.divisorJanela = 1;
	}

	public AbstractScannerImagem(final BufferedImage image, final int largura, final int altura, final Ponto ponto,
	         final float divisor) {
		this.image = image;
		this.larguraJanela = largura;
		this.alturaJanela = altura;
		this.pontoCentral = ponto;
		if (divisor == 0) {
			this.divisorJanela = 1;
		} else {
			this.divisorJanela = divisor;
		}

	}

	public int getAltura() {
		return this.alturaJanela;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public int getLargura() {
		return this.larguraJanela;
	}

	public void scanear() {
		int larguraPercorreImagem;
		int alturaPercorridaImagem;
		int posXini;
		int posYini;
		int posXfim;
		int posYfim;
		final Ponto pontoInicial = new Ponto();
		if (this.pontoCentral != null) {
			posXini = (int) (this.pontoCentral.getX() - (this.larguraJanela / this.divisorJanela));
			posYini = (int) (this.pontoCentral.getY() - (this.alturaJanela / this.divisorJanela));
			if (posXini < 0) {
				posXini = 0;
			}
			if (posYini < 0) {
				posYini = 0;
			}
			pontoInicial.setX(posXini);
			pontoInicial.setY(posYini);
			posXfim = (int) (this.pontoCentral.getX() + (this.larguraJanela / this.divisorJanela));
			posYfim = (int) (this.pontoCentral.getY() + (this.alturaJanela / this.divisorJanela));
			if (posXfim > this.image.getWidth()) {
				posXfim = this.image.getWidth();
			}
			if (posYfim > this.image.getHeight()) {
				posXfim = this.image.getHeight();
			}
			larguraPercorreImagem = posXfim;
			alturaPercorridaImagem = posYfim;
		} else {
			larguraPercorreImagem = this.image.getWidth() - this.getLargura();
			alturaPercorridaImagem = this.image.getHeight() - this.getAltura();

		}
		for (int x = pontoInicial.getX(); x < larguraPercorreImagem; x++) {
			for (int y = pontoInicial.getY(); y < alturaPercorridaImagem; y++) {
				this.scanear(x, y);
			}
		}
	}

	protected abstract void scanear(int x, int y);

}
