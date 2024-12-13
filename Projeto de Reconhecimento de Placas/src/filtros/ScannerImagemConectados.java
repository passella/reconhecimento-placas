package filtros;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class ScannerImagemConectados {
	private final LinkedList<Objeto> listaObjetos = new LinkedList<Objeto>();
	public BufferedImage placa;

	public ScannerImagemConectados(final Placa placa) {
		this.placa = placa.getImagem();
		this.InicializaListaObjetos();
	}

	public LinkedList<Objeto> getListaObjetos() {
		return this.listaObjetos;
	}

	private void InicializaListaObjetos() {
		for (int i = 0; i < this.placa.getHeight(); i++) {
			for (int j = 0; j < this.placa.getWidth(); j++) {
				new Pixel();

			}
		}
	}
}
