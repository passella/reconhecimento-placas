package br.com.passella.neural.redeneural;

public class Atributos {

   private final int qtdAtributos;

   /*
    * Construtor
    * 
    * @qtdAtributos: Serve para setar a quantidade de atributos de uma amostra
    */
   public Atributos(final int qtdAtributos) {
      this.qtdAtributos = qtdAtributos;
   }

   public int getQtdAtributos() {
      return this.qtdAtributos;
   }

}
