package filtros;

public class Retangulo {
	private Ponto pontoFinal;
	private Ponto pontoInicial;

	public Retangulo() {
		this.pontoInicial = new Ponto();
		this.pontoFinal = new Ponto();
	}

	public Retangulo(final Ponto pontoInicial, final Ponto pontoFinal) {
		this.pontoInicial = pontoInicial;
		this.pontoFinal = pontoFinal;
	}

	public Ponto getPontoFinal() {
		return this.pontoFinal;
	}

	public Ponto getPontoInicial() {
		return this.pontoInicial;
	}

	public void setPontoFinal(final Ponto pontoFinal) {
		this.pontoFinal = pontoFinal;
	}

	public void setPontoInicial(final Ponto pontoInicial) {
		this.pontoInicial = pontoInicial;
	}

}
