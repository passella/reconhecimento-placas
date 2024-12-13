package br.com.passella.neural.redeneural;

public interface IConfiguracaoPerceptron {

   public Atributos getAtributos();

   public double getFormatedDouble(double valor);

   public double[] getPesos();

   public double getTaxaAprendizado();

}
