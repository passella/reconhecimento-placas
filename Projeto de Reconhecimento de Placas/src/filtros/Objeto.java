package filtros;

import java.util.LinkedList;

public class Objeto {
	private int id;
	public LinkedList<Pixel> Objeto = new LinkedList<Pixel>();

	public Objeto(final int id) {
		this.id = id;
	}

	public boolean ExistePixel(final Pixel pixelComp) {
		for (final Pixel pixelAtual : this.Objeto) {
			if ((pixelComp.getTonCinza() == pixelAtual.getTonCinza())
			         && (pixelComp.getPonto().getX() == pixelAtual.getPonto().getX())
			         && (pixelComp.getPonto().getY() == pixelAtual.getPonto().getY())) {
				return true;
			}
		}
		return false;
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}
