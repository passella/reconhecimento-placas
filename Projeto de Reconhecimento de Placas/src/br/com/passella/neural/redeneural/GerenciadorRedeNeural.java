package br.com.passella.neural.redeneural;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import br.com.passella.utils.BufferedImageUtils;
import br.com.passella.utils.Configuracao;

public class GerenciadorRedeNeural implements IConfiguracaoPerceptron {

   private final Atributos atributos;
   private DocumentBuilder db;
   private DocumentBuilderFactory dbf;
   private final DecimalFormat decimalFormat;
   private Document doc;
   final File gerenciadorRedeNeuralXml;
   private final LinkedList<RedeNeural> redes;

   /*
    * Construtor: responsavel pela inicialização das redes neurais e arquivo XML
    */
   public GerenciadorRedeNeural() throws ParserConfigurationException, SAXException, IOException {
      this.redes = new LinkedList<RedeNeural>();
      this.atributos = new Atributos(Configuracao.getLarguraIdeal() * Configuracao.getAlturaIdeal());
      this.decimalFormat = new DecimalFormat("0.0000");
      this.gerenciadorRedeNeuralXml = new File("GerenciadorRedeNeural.xml");
      this.construirArquivoXml();
   }

   /*
    * Retorna a rede neural de acordo com o caracter passado
    */
   private RedeNeural acharRedeNeural(final char caracter) {
      for (final RedeNeural r : this.redes) {
         if (r.getCaracterReconhecido() == caracter) {
            return r;
         }
      }
      return null;

   }

   /*
    * Instancia uma rede neiral pelo nó passado do XML
    */
   private void adicionarRedeNeural(final Element redeNeural) {
      final RedeNeural neural = new RedeNeural(new IConfiguracaoPerceptron() {

         @Override
         public Atributos getAtributos() {
            return GerenciadorRedeNeural.this.getAtributos();
         }

         @Override
         public double getFormatedDouble(final double valor) {
            return GerenciadorRedeNeural.this.getFormatedDouble(valor);
         }

         @Override
         public double[] getPesos() {
            final double[] resultado = new double[GerenciadorRedeNeural.this.getAtributos().getQtdAtributos()];
            Node pesos = null;
            for (int i = 0; i < redeNeural.getChildNodes().getLength(); i++) {
               if (redeNeural.getChildNodes().item(i).getNodeName().equalsIgnoreCase("pesos")) {
                  pesos = redeNeural.getChildNodes().item(i);
                  break;
               }
            }

            int r = 0;
            for (int i = 0; i < pesos.getChildNodes().getLength(); i++) {
               try {
                  resultado[r] = Double.parseDouble(pesos.getChildNodes().item(i).getTextContent());
                  r++;
               }
               catch (final Exception e) {
                  continue;
               }
            }
            return resultado;
         }

         @Override
         public double getTaxaAprendizado() {
            return GerenciadorRedeNeural.this.getTaxaAprendizado();
         }
      }, redeNeural.getAttribute("caracterReconhecido").charAt(0));
      this.redes.add(neural);
   }

   /*
    * Adiciona uma rede neural na estrutura XML definida
    */
   private void adicionarRedeNeural(final Element redesNeurais, final RedeNeural neural) {
      final Element redeNeural = this.doc.createElement("redeNeural");
      redesNeurais.appendChild(redeNeural);
      redeNeural.setAttribute("caracterReconhecido", String.valueOf(neural.getCaracterReconhecido()));
      final Element pesos = this.doc.createElement("pesos");
      redeNeural.appendChild(pesos);

      for (int i = 0; i < neural.getPerceptron().getPesos().length; i++) {
         final Element peso = this.doc.createElement("p" + i);
         peso.setTextContent(String.valueOf(neural.getPerceptron().getPesos()[i]));
         pesos.appendChild(peso);
      }

      this.redes.add(neural);
   }

   /*
    * Atualiza os dados de uma rede neural no arquivo XML
    */
   public void atualizarRedeNeural(final RedeNeural neural) throws TransformerException, IOException {
      final Element redesNeurais = this.doc.getDocumentElement();
      for (int i = 0; i < redesNeurais.getChildNodes().getLength(); i++) {
         final Node nodeRede = redesNeurais.getChildNodes().item(i);
         if (nodeRede.getAttributes() == null) {
            continue;
         }
         if (nodeRede.getAttributes().getNamedItem("caracterReconhecido").getTextContent().toUpperCase().charAt(0) == neural
                  .getCaracterReconhecido()) {
            for (int p = 0; p < nodeRede.getChildNodes().getLength(); p++) {
               final Node nodeP = nodeRede.getChildNodes().item(p);
               if (nodeP.getNodeName().equalsIgnoreCase("pesos")) {
                  for (int pi = 0; pi < nodeP.getChildNodes().getLength(); pi++) {
                     try {
                        final int index = Integer.parseInt(nodeP.getChildNodes().item(pi).getNodeName().replace("p", ""));
                        nodeP.getChildNodes().item(pi).setTextContent(String.valueOf(neural.getPerceptron().getPesos()[index]));
                     }
                     catch (final Exception exception) {
                        continue;
                     }

                  }
               }
            }
            this.salvar();
            return;
         }
      }

   }

   /*
    * Instancia um arquivo xml e cria as redes neurais de acordo com o mesmo
    */
   private void construirArquivoXml() throws ParserConfigurationException, SAXException, IOException {
      this.dbf = DocumentBuilderFactory.newInstance();
      this.db = this.dbf.newDocumentBuilder();

      if (this.gerenciadorRedeNeuralXml.exists()) {
         try {
            this.doc = this.db.parse(this.gerenciadorRedeNeuralXml);
         }
         catch (final Exception e) {
            this.doc = this.db.newDocument();
         }
      } else {
         this.doc = this.db.newDocument();
      }

      this.construirPerceptrons();
   }

   /*
    * construirPerceptrons: constroi todas as redes neurais de acordo com o XLM,
    * caso não tenha, crie e adicione no arquivo XML
    */
   private void construirPerceptrons() {
      Element redesNeurais = this.doc.getDocumentElement();
      if (redesNeurais == null) {
         redesNeurais = this.doc.createElement("redesNeurais");
         this.doc.appendChild(redesNeurais);
      }

      final NodeList redeNeuralList = redesNeurais.getElementsByTagName("redeNeural");

      /*
       * Instancia as redes neurais de acordo com o arquivo XML
       */
      for (int i = 0; i < redeNeuralList.getLength(); i++) {
         final Element redeNeural = (Element) redeNeuralList.item(i);
         this.adicionarRedeNeural(redeNeural);
      }

      /*
       * Constroi as redes de 0 a 9
       */
      for (char c = 'A'; c <= 'Z'; c++) {
         if (this.acharRedeNeural(c) == null) {
            this.adicionarRedeNeural(redesNeurais, new RedeNeural(this, c));
         }
      }

      /*
       * Constroi as redes de 0 a 9
       */
      for (char c = '0'; c <= '9'; c++) {
         if (this.acharRedeNeural(c) == null) {
            this.adicionarRedeNeural(redesNeurais, new RedeNeural(this, c));
         }
      }

   }

   @Override
   public Atributos getAtributos() {
      return this.atributos;
   }

   /*
    * Formata o valor de acordo com a mascara definida
    */
   @Override
   public double getFormatedDouble(final double valor) {
      return Double.parseDouble(this.decimalFormat.format(valor).replace(",", "."));
   }

   private double getPesoAleatorio() {
      double resultado = Math.random();
      while ((resultado < this.getPesoMinimo()) || (resultado > this.getPesoMaximo())) {
         resultado = Math.random();
      }
      return this.getFormatedDouble(resultado);
   }

   protected double getPesoMaximo() {
      return 0.9;
   }

   protected double getPesoMinimo() {
      return 0.1;
   }

   /*
    * Gera um vetor de pesos alietarios
    */
   @Override
   public double[] getPesos() {
      final double[] resultado = new double[this.getAtributos().getQtdAtributos()];
      for (int i = 0; i < resultado.length; i++) {
         resultado[i] = this.getPesoAleatorio();
      }

      return resultado;

   }

   /*
    * Retorna a rede neural de acordo com o caracter passado como argumento.
    */
   public RedeNeural getRedeNeural(final char caracter) {
      for (final RedeNeural r : this.redes) {
         if (r.getCaracterReconhecido() == caracter) {
            return r;
         }
      }
      final RedeNeural novaRede = new RedeNeural(this, caracter);
      this.redes.add(novaRede);
      return novaRede;
   }

   /*
    * Retorna as redes neurais instanciadas
    */
   public LinkedList<RedeNeural> getRedes() {
      return this.redes;
   }

   /*
    * getTaxaAprendizado: retorna a taxa de aprendizado padrao para cada novo
    * perceptron
    */
   @Override
   public double getTaxaAprendizado() {
      return 0.1;
   }

   /*
    * reconhecerCaracter: retorna uma lista das redes neurais que reconheceram a
    * imagem passada como argumento
    */
   public LinkedList<Character> reconhecerCaracter(final BufferedImage bufferedImage) {
      final LinkedList<Character> resultado = new LinkedList<Character>();
      final Amostra amostra = BufferedImageUtils.toAmostra(bufferedImage, 1);

      for (final RedeNeural r : this.redes) {
         if (r.isCaracterReconhecido(amostra)) {
            resultado.add(r.getCaracterReconhecido());
         }
      }
      return resultado;

   }

   /*
    * Salva todas as informação das redes neurais no arquivo XML
    */
   public File salvar() throws TransformerException, IOException {
      final TransformerFactory transformerFactory = TransformerFactory.newInstance();
      final Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      final FileOutputStream fileOutputStream = new FileOutputStream(this.gerenciadorRedeNeuralXml);
      final StreamResult result = new StreamResult(fileOutputStream);
      final DOMSource domSource = new DOMSource(this.doc);
      transformer.transform(domSource, result);
      fileOutputStream.close();
      return this.gerenciadorRedeNeuralXml;

   }
}
