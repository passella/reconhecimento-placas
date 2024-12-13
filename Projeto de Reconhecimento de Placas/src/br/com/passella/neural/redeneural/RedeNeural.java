package br.com.passella.neural.redeneural;

/*
 * Classe que possui um perceptron e define qual caracter este perceptron reconhece
 */
public class RedeNeural {

   private final char caracterReconhecido;
   private final Perceptron perceptron;

   /*
    * Construtor
    * 
    * @configuracaoPerceptron: são os parametros iniciais
    * 
    * @caracterReconhecido: o caracter que esta rede neural reconhece
    */
   public RedeNeural(final IConfiguracaoPerceptron configuracaoPerceptron, final char caracterReconhecido) {
      this.perceptron = new Perceptron(configuracaoPerceptron);
      this.caracterReconhecido = caracterReconhecido;
   }

   /*
    * Retorna o caracter que a rede reconhece
    */
   public char getCaracterReconhecido() {
      return this.caracterReconhecido;
   }

   public Perceptron getPerceptron() {
      return this.perceptron;
   }

   public boolean isCaracterReconhecido(final Amostra amostra) {
      return (this.perceptron.getNet(this.perceptron.getSaidaCalculada(amostra.getValores())) == 1);
   }

   public boolean isCaracterReconhecido(final int[] valores) {
      return (this.perceptron.getNet(this.perceptron.getSaidaCalculada(valores)) == 1);
   }

}
