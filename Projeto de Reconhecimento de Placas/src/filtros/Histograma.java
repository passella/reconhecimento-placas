package filtros;

import java.awt.image.BufferedImage;

public class Histograma {

	private int[] base;
	private BufferedImage bufferedImage;

	private Histograma() {
		super();
	}

	public Histograma(final BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		this.iniciarHistograma();
	}

	public int[] getBase() {
		return this.base;
	}

	public Histograma getEqualizado() {
		final Histograma resultado = new Histograma();
		resultado.base = new int[this.base.length];
		resultado.base[0] = this.base[0];
		int somatoria = 0;
		for (int i = 0; i < resultado.base.length; i++) {
			somatoria = somatoria + this.base[i];
			resultado.base[i] = somatoria;
		}
		final int I = this.bufferedImage.getWidth() * this.bufferedImage.getHeight() / 255;

		for (int i = 0; i < resultado.base.length; i++) {
			int novoValor = resultado.base[i] / I;
			novoValor = Math.round(novoValor) - 1;
			resultado.base[i] = (novoValor > 0) ? novoValor : 0;
		}
		return resultado;

	}

	private void iniciarHistograma() {
		this.base = new int[256];
		for (int x = 0; x < this.bufferedImage.getWidth(); x++) {
			for (int y = 0; y < this.bufferedImage.getHeight(); y++) {
				this.base[this.bufferedImage.getRaster().getSample(x, y, 0)]++;

			}
		}
	}

}