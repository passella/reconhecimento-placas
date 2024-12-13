package br.com.passella.neural.redeneural;

import java.util.Arrays;

public class Amostra {

   private final Atributos atributos;
   private final String nome;
   private final int valorDesejado;
   private final int[] valores;

   /*
    * Construtor
    * 
    * @atributos: Serve apenas para saber quantos atributos essa amostra possui
    * 
    * @valores: Vetor contendo o valor de cada atributo
    * 
    * @valorDesejado: Serve para informar para a rede neural se essa amostra
    * deve ser reconhecida ou n?o pela mesma
    */
   public Amostra(final Atributos atributos, final int[] valores, final int valorDesejado) {
      this(atributos, "", valores, valorDesejado);
   }

   public Amostra(final Atributos atributos, final String nome, final int[] valores, final int valorDesejado) {

      this.atributos = atributos;
      this.nome = nome;
      this.valores = valores;
      this.valorDesejado = valorDesejado;
   }

   public Atributos getAtributos() {
      return this.atributos;
   }

   public String getNome() {
      return this.nome;
   }

   public int getValorDesejado() {
      return this.valorDesejado;
   }

   /*
    * Retorna o vetor com os valores dos atributos
    */
   public int[] getValores() {
      return this.valores;
   }

   @Override
   public String toString() {
      String resultado = String.format("Amostra: %s\n", this.nome);
      resultado += String.format("Valores: %s\n", Arrays.toString(this.valores));
      resultado += String.format("Valor desejado: %.4f\n", this.valorDesejado);
      return resultado;
   }

}
