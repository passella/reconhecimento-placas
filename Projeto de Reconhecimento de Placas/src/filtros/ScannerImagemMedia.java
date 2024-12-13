package filtros;

import java.awt.image.BufferedImage;

public class ScannerImagemMedia extends AbstractScannerImagem {

	private final BufferedImage imageOut;
	private final int total = 0;

	public ScannerImagemMedia(final BufferedImage image, final int largura, final int altura,
	         final BufferedImage imageOut) {
		super(image, largura, altura);
		this.imageOut = imageOut;
		// TODO Auto-generated constructor stub
	}

	private int getMedia(final int x, final int y) {
		int media = 0;
		for (int xAux = x; xAux < this.getLargura() + x; xAux++) {
			for (int yAux = y; yAux < this.getAltura() + y; yAux++) {
				media += this.getImage().getRaster().getSample(xAux, yAux, 0);
			}
		}
		media = media / (this.getLargura() * this.getAltura());
		return media;
	}

	public int getTotal() {
		return this.total;
	}

	public Ponto PontoMaiorIntensidade() {
		int maior = 0;
		int xMaior = 0;
		int yMaior = 0;
		for (int x = 0; x < this.imageOut.getWidth(); x++) {
			for (int y = 0; y < this.imageOut.getHeight(); y++) {
				if (maior < this.imageOut.getRaster().getSample(x, y, 0)) {
					maior = this.imageOut.getRaster().getSample(x, y, 0);
					xMaior = x;
					yMaior = y;
				}
			}
		}
		return new Ponto(xMaior, yMaior);

	}

	@Override
	protected void scanear(final int x, final int y) {
		final int media = this.getMedia(x, y);
		this.imageOut.getRaster().setSample(x + (this.getLargura() / 2), y + (this.getAltura() / 2), 0, media);
	}

}
