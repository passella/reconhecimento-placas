package br.com.passella.neural.redeneural;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import br.com.passella.utils.BufferedImageUtils;

public class BtnCaracterReconhecido implements ActionListener {

   private class AmostraListaItem {

      private final Amostra amostraFalsa;
      private final Amostra amostraVerdadeira;
      private final char caracter;

      public AmostraListaItem(final char caracter, final Amostra amostraVerdadeira, final Amostra amostraFalsa) {
         this.caracter = caracter;
         this.amostraVerdadeira = amostraVerdadeira;
         this.amostraFalsa = amostraFalsa;
      }

   }

   private static LinkedList<AmostraListaItem> amostraListaItems = new LinkedList<BtnCaracterReconhecido.AmostraListaItem>();
   private final BufferedImage bufferedImage;
   private final LinkedList<Character> caracterReconhecido;

   private final GerenciadorRedeNeural gerenciadorRedeNeural;

   public BtnCaracterReconhecido(final GerenciadorRedeNeural gerenciadorRedeNeural, final LinkedList<Character> caracterReconhecido,
            final BufferedImage bufferedImage) {
      this.caracterReconhecido = caracterReconhecido;
      this.gerenciadorRedeNeural = gerenciadorRedeNeural;
      this.bufferedImage = bufferedImage;
   }

   @Override
   public void actionPerformed(final ActionEvent e) {
      try {

         if (JOptionPane.showConfirmDialog(null, "O caracter reconhecido foi : " + Arrays.toString(this.caracterReconhecido.toArray())
                  + " est? correto?", "Reconhecimento de Caracteres", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
            final char resposta = JOptionPane.showInputDialog("Entre com o caracter correto").toUpperCase().charAt(0);
            final Amostra amostraVerdadeira = BufferedImageUtils.toAmostra(this.bufferedImage, 1);
            final Amostra amostraFalsa = BufferedImageUtils.toAmostra(this.bufferedImage, 0);

            BtnCaracterReconhecido.amostraListaItems.add(new AmostraListaItem(resposta, amostraVerdadeira, amostraFalsa));
            boolean treinado = false;
            int qtdTreinamentos = 0;
            while (/* qtdTreinamentosTotais++ < 10 */true) {

               for (final AmostraListaItem item : BtnCaracterReconhecido.amostraListaItems) {
                  treinado = false;
                  while ((!treinado) && (qtdTreinamentos++ < 100)) {
                     treinado = true;
                     for (final RedeNeural neural : BtnCaracterReconhecido.this.gerenciadorRedeNeural.getRedes()) {
                        if (neural.getCaracterReconhecido() == item.caracter) {
                           treinado = ((neural.getPerceptron().treinarAmostra(item.amostraVerdadeira)) && treinado);
                        } else {
                           treinado = ((neural.getPerceptron().treinarAmostra(item.amostraFalsa)) && treinado);
                        }
                     }
                  }
               }

               if (treinado) {
                  break;
               }

            }

            for (final RedeNeural neural : this.gerenciadorRedeNeural.getRedes()) {
               this.gerenciadorRedeNeural.atualizarRedeNeural(neural);
            }

            this.caracterReconhecido.clear();
            this.caracterReconhecido.add(resposta);
            JOptionPane.showMessageDialog(null, "Redes Treinadas!");
         }
      }
      catch (final Exception exception) {
         JOptionPane.showMessageDialog(null, exception.getMessage(), "Aten??o", JOptionPane.WARNING_MESSAGE);
      }

   }
}
