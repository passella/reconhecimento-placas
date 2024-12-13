package filtros;

import java.util.ArrayList;

public class Pixel {
	public static int getBlue(final int ARGB) {
		return ((ARGB & (0x000000FF)) >> 0);
	}

	public static int getGreen(final int ARGB) {
		return ((ARGB & (0x0000FF00)) >> 8);
	}

	public static int getRed(final int ARGB) {
		return ((ARGB & (0x00FF0000)) >> 16);
	}

	public static ArrayList<Double> rgbToYiqConversion(final int argb) {

		final int R = Pixel.getRed(argb);
		final int G = Pixel.getGreen(argb);
		final int B = Pixel.getBlue(argb);

		final double Y = (0.299 * R + 0.587 * G + 0.114 * B);
		final double I = (0.596 * R - 0.274 * G - 0.322 * B);
		final double Q = (0.211 * R - 0.523 * G + 0.312 * B);

		final ArrayList<Double> yiqDots = new ArrayList<Double>();
		yiqDots.add(Y);
		yiqDots.add(I);
		yiqDots.add(Q);

		return yiqDots;
	}

	public static int setRGB(final int R, final int G, final int B, final int ARGB) {
		int resultado = 0;
		resultado += ARGB & 0xFF000000;
		resultado += R << 16;
		resultado += G << 8;
		resultado += B << 0;

		return resultado;
	}

	public static int yiqToRgbConversion(final ArrayList<Double> yiqDots) {

		final double Y = yiqDots.get(0);
		final double I = yiqDots.get(1);
		final double Q = yiqDots.get(2);

		int R = (int) Math.round(Y + 0.956 * I + 0.621 * Q);
		int G = (int) Math.round(Y - 0.272 * I - 0.647 * Q);
		int B = (int) Math.round(Y - 1.106 * I + 1.703 * Q);

		if (R < 0) {
			R = 0;
		} else if (R > 255) {
			R = 255;
		}

		if (G < 0) {
			G = 0;
		} else if (G > 255) {
			G = 255;
		}

		if (B < 0) {
			B = 0;
		} else if (B > 255) {
			B = 255;
		}

		return Pixel.setRGB(R, G, B, (R + B + G) / 3);
	}

	private Ponto ponto;

	private int tonCinza;

	public Pixel() {

	}

	public Pixel(final Ponto p, final int ton) {
		this.setPonto(p);
		this.setTonCinza(ton);
	}

	public Ponto getPonto() {
		return this.ponto;
	}

	public int getTonCinza() {
		return this.tonCinza;
	}

	public void setPonto(final Ponto ponto) {
		this.ponto = ponto;
	}

	public void setTonCinza(final int tonCinza) {
		this.tonCinza = tonCinza;
	}
}
