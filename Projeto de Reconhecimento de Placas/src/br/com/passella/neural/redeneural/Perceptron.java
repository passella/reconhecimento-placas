package br.com.passella.neural.redeneural;

public class Perceptron {

   private final IConfiguracaoPerceptron configuracaoPerceptron;
   private double pesoBias = 0;
   private final double[] pesos;

   /*
    * Construtor
    * 
    * @configuracaoPerceptron armazena uma referencia para a configuração do
    * perceptron
    */
   public Perceptron(final IConfiguracaoPerceptron configuracaoPerceptron) {
      this.configuracaoPerceptron = configuracaoPerceptron;
      this.pesos = configuracaoPerceptron.getPesos();
   }

   /*
    * Função de ativação, onde caso o valor seja maior que zero, retorna 1 e
    * caso contrario retorna 0
    */
   public int getNet(final double valor) {
      return valor > 0 ? 1 : 0;
   }

   public double[] getPesos() {
      return this.pesos;
   }

   public int getQtdAtributos() {
      return this.configuracaoPerceptron.getAtributos().getQtdAtributos();
   }

   /*
    * getSaidaCalculada: é o metodo que retorna o que foi reconhecido por este
    * perceptron de acordo com os atributos passados por parametro
    */
   public double getSaidaCalculada(final int[] valores) {
      return this.pesoBias + this.configuracaoPerceptron.getFormatedDouble(this.somatorioPesosValores(valores));
   }

   /*
    * Reorna a taxa de aprendizado do perceptron
    */
   public double getTaxaAprendizado() {
      return this.configuracaoPerceptron.getTaxaAprendizado();
   }

   private void recalcularPesoBias(final int saidaCalculada, final int saidaDesejada) {
      this.pesoBias += this.configuracaoPerceptron.getTaxaAprendizado() * (saidaCalculada - saidaDesejada);
      this.pesoBias = this.configuracaoPerceptron.getFormatedDouble(this.pesoBias);
   }

   private void recalcularPesos(final int[] valores, final int saidaCalculada, final int saidaDesejada) {
      for (int i = 0; i < valores.length; i++) {
         this.pesos[i] += this.configuracaoPerceptron.getTaxaAprendizado() * (saidaDesejada - saidaCalculada) * valores[i];
         this.pesos[i] = this.configuracaoPerceptron.getFormatedDouble(this.pesos[i]);
      }
      this.recalcularPesoBias(saidaCalculada, saidaDesejada);
   }

   /*
    * retorna a soma dos valores contra os pesos do perceptron
    */
   private double somatorioPesosValores(final int valores[]) {
      double resultado = 0;
      for (int i = 0; i < valores.length; i++) {
         resultado += this.pesos[i] * valores[i];
      }
      return resultado;
   }

   /*
    * Metodo principal o qual atravez de uma amostra é decido se precisa fazer
    * um ajuste nos pesos do perceptron
    */
   public boolean treinarAmostra(final Amostra amostra) {
      final int[] valores = amostra.getValores();
      boolean resultado = true;
      final int saidaCalculada = this.getNet(this.getSaidaCalculada(valores));
      final int saidaDesejada = amostra.getValorDesejado();

      if (saidaCalculada != saidaDesejada) {
         resultado = false;
         this.recalcularPesos(valores, saidaCalculada, saidaDesejada);
      }
      return resultado;
   }

}
