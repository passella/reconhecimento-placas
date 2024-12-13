package filtros;

public class Ponto {
	private int x;
	private int y;

	public Ponto() {
		this.x = 0;
		this.y = 0;
	}

	public Ponto(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

}
