package filtros;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Placa {
	private final int altura;
	private final int erroX;
	private final int erroY;
	private BufferedImage imagem;
	private final int largura;
	private final Ponto pontoInicial;

	public Placa(final BufferedImage image) {
		this.imagem = image;
		this.pontoInicial = new Ponto();
		this.altura = 0;
		this.largura = 0;
		this.erroX = 0;
		this.erroY = 0;
		this.imagem = image;
	}

	public Placa(final BufferedImage image, final Ponto p, final int largura, final int altura) {
		this.pontoInicial = p;
		this.altura = altura;
		this.largura = largura;
		this.erroX = 0;
		this.erroY = 0;
		int xIni;
		int yIni;
		int xFim;
		int yFim;
		xIni = this.erroX + (this.pontoInicial.getX());
		if (xIni < 0) {
			xIni = 0;
		}
		yIni = this.erroY + (this.pontoInicial.getY());
		if (yIni < 0) {
			yIni = 0;
		}
		xFim = this.erroX + (this.pontoInicial.getX() + this.largura);
		if (xIni > this.imagem.getWidth()) {
			xFim = this.imagem.getWidth();
		}
		yFim = this.erroY + (this.pontoInicial.getY() + this.altura);
		if (yFim > this.imagem.getHeight()) {
			yFim = this.imagem.getHeight();
		}

		this.imagem = new BufferedImage(largura, altura, image.getType());
		final Graphics2D area = (Graphics2D) this.imagem.getGraphics().create();
		area.drawImage(image, 0, 0, this.imagem.getHeight(), this.imagem.getWidth(), xIni, yIni, xFim, yFim, null);
		area.dispose();
	}

	public Placa(final BufferedImage image, final Ponto p, final int largura, final int altura, final int erroX,
	         final int erroY) {
		this.imagem = image;
		this.pontoInicial = p;
		this.altura = altura;
		this.largura = largura;
		this.erroX = erroX;
		this.erroY = erroY;
		int xIni;
		int yIni;
		int xFim;
		int yFim;
		xIni = this.erroX + (this.pontoInicial.getX());
		if (xIni < 0) {
			xIni = 0;
		}
		yIni = this.erroY + (this.pontoInicial.getY());
		if (yIni < 0) {
			yIni = 0;
		}
		xFim = this.erroX + (this.pontoInicial.getX() + this.largura);
		if (xIni > this.imagem.getWidth()) {
			xFim = this.imagem.getWidth();
		}
		yFim = this.erroY + (this.pontoInicial.getY() + this.altura);
		if (yFim > this.imagem.getHeight()) {
			yFim = this.imagem.getHeight();
		}
		this.imagem = new BufferedImage(xFim - xIni, yFim - yIni, image.getType());
		final Graphics2D area = (Graphics2D) this.imagem.getGraphics().create();
		area.drawImage(image, 0, 0, xFim - xIni, yFim - yIni, xIni, yIni, xFim, yFim, null);
		area.dispose();
	}

	public BufferedImage getImagem() {
		return this.imagem;
	}

}
