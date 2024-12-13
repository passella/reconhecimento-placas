package ia.reconhecedor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import br.com.passella.neural.redeneural.BtnCaracterReconhecido;
import br.com.passella.neural.redeneural.GerenciadorRedeNeural;
import br.com.passella.utils.BufferedImageUtils;
import br.com.passella.utils.Configuracao;

public class Reconhecedor extends JFrame {

   private static final long serialVersionUID = -2956148852965060397L;
   private final LinkedList<Character> caracteresReocnhecidos;
   private final GerenciadorRedeNeural gerenciadorRedeNeural;
   private JLabel lblResultado;
   private final LinkedList<BufferedImage> lstImagens;
   private JPanel pnlComandos;
   private JPanel pnlImagens;

   private JScrollPane scpImagem;

   public Reconhecedor(final LinkedList<BufferedImage> listaCaracteres) throws ParserConfigurationException, SAXException, IOException,
            TransformerException {
      this.setBounds(new Rectangle(100, 100, 400, 200));
      this.setTitle("Caracteres Reconhecidos");
      this.lstImagens = listaCaracteres;
      this.gerenciadorRedeNeural = new GerenciadorRedeNeural();
      this.caracteresReocnhecidos = new LinkedList<Character>();
      this.iniciarComponentes();
      this.exibirImagens();
      this.gerenciadorRedeNeural.salvar();
   }

   private void exibirImagens() {
      for (final BufferedImage bufferedImage : this.lstImagens) {
         this.pnlImagens.add(this.getNovoButton(bufferedImage));
      }
      this.pack();
      this.lblResultado.setText("Os caracteres reconhecidos foram: " + Arrays.toString(this.caracteresReocnhecidos.toArray()));

   }

   private BufferedImage getImagemProcessada(final BufferedImage bufferedImage) {
      BufferedImage resultado = BufferedImageUtils.getImagemCinza(bufferedImage);
      resultado = BufferedImageUtils.getScaledInstance(resultado, Configuracao.getLarguraIdeal(), Configuracao.getAlturaIdeal(),
               BufferedImage.TYPE_BYTE_GRAY);
      resultado = BufferedImageUtils.getImagemEsqueleto(resultado);
      return resultado;
   }

   private JLabel getLblResultado() {
      if (this.lblResultado == null) {
         this.lblResultado = new JLabel("Caracteres Reconhecidos: ");

      }
      return this.lblResultado;
   }

   private JButton getNovoButton(final BufferedImage bufferedImage) {
      final BufferedImage imagemProcessada = this.getImagemProcessada(bufferedImage);
      final LinkedList<Character> caracterReconhecido = this.reconhecerCaracter(imagemProcessada);

      final JButton resultado = new JButton(new ImageIcon(bufferedImage));
      resultado.addActionListener(new BtnCaracterReconhecido(this.gerenciadorRedeNeural, caracterReconhecido, imagemProcessada));

      if (caracterReconhecido.size() <= 3) {
         this.caracteresReocnhecidos.addAll(caracterReconhecido);
      }
      resultado.setToolTipText("O caracter reconhecido foi: " + Arrays.toString(caracterReconhecido.toArray()));
      resultado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      return resultado;

   }

   private JPanel getPnlComandos() {
      if (this.pnlComandos == null) {
         this.pnlComandos = new JPanel();
         this.pnlComandos.setLayout(new BoxLayout(this.pnlComandos, BoxLayout.Y_AXIS));
         this.pnlComandos.add(this.getLblResultado());
      }
      return this.pnlComandos;
   }

   private JPanel getPnlImagens() {
      if (this.pnlImagens == null) {
         this.pnlImagens = new JPanel();
         this.pnlImagens.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
      }
      return this.pnlImagens;
   }

   private JScrollPane getScpImagem() {
      if (this.scpImagem == null) {
         this.scpImagem = new JScrollPane();
         this.scpImagem.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
         this.scpImagem.setViewportView(this.getPnlImagens());
      }
      return this.scpImagem;
   }

   private void iniciarComponentes() {
      this.getContentPane().setLayout(new BorderLayout(0, 0));
      this.getContentPane().add(this.getPnlComandos(), BorderLayout.SOUTH);
      this.getContentPane().add(this.getScpImagem(), BorderLayout.CENTER);
   }

   private LinkedList<Character> reconhecerCaracter(final BufferedImage bufferedImage) {
      return this.gerenciadorRedeNeural.reconhecerCaracter(bufferedImage);
   }
}
