package filtros;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Grafico extends JFrame {
	private final int altura;
	public BufferedImage image;
	private final int largura;
	private final LinkedList<Pixel> listaPixels = new LinkedList<Pixel>();

	public Grafico(final int largura, final int altura, final String nome) {
		super(nome);
		this.largura = largura;
		this.altura = altura;
	}

	public Grafico(final LinkedList<Pixel> listaPixels, final int largura, final int altura, final String nome) {
		super(nome);
		this.largura = largura;
		this.altura = altura;
		this.listaPixels.addAll(listaPixels);
	}

	public void iniciaPlot() {
		final BufferedImage grafico = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_RGB);
		for (final Pixel pixelNew : this.listaPixels) {
			grafico.getRaster().setSample(pixelNew.getPonto().getX(), pixelNew.getPonto().getY(), 0, 255);
		}

		final JLabel label = new JLabel(new ImageIcon(grafico));
		this.setLayout(new FlowLayout());
		this.add(label);
		this.image = grafico;
	}

	public void iniciaPlot(final LinkedList<Ponto> listaPontos) {
		final BufferedImage grafico = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_RGB);
		for (final Ponto pixelNew : listaPontos) {
			grafico.getRaster().setSample(pixelNew.getX(), pixelNew.getY(), 0, 255);
		}
		final JLabel label = new JLabel(new ImageIcon(grafico));
		this.setLayout(new FlowLayout());
		this.add(label);
		this.image = grafico;
	}

	public void iniciaSequencia() {
		final BufferedImage grafico = new BufferedImage(this.largura, this.altura, BufferedImage.TYPE_INT_RGB);
		final Graphics g = grafico.getGraphics();
		g.setColor(Color.BLUE);
		int i = 0;
		for (final Pixel pixelNew : this.listaPixels) {
			g.drawLine(i, grafico.getHeight(), i, pixelNew.getTonCinza() + this.altura - 255);
			i++;
		}
		g.dispose();
		final JLabel label = new JLabel(new ImageIcon(grafico));
		this.setLayout(new FlowLayout());
		this.add(label);
		this.image = grafico;
	}

}
