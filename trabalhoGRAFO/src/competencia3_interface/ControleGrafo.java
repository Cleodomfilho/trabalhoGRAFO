package competencia3_interface;

/*
 * Competência 3 do PDF: Interface gráfica.
 * Aqui ficam as telas, comandos e visualização animada do grafo.
 */

import competencia1_grafo.Grafo;
import competencia1_grafo.Vertice;
import competencia2_buscas.Busca;
import competencia2_buscas.ResultadoBusca;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Interface gráfica principal do trabalho.
 * A tela foi organizada para deixar os comandos compactos e mais espaço livre para o grafo.
 */
public class ControleGrafo extends JFrame {

    private final Color corFundo = new Color(241, 245, 249);
    private final Color corPainel = Color.WHITE;
    private final Color corAzul = new Color(37, 99, 235);
    private final Color corAzulEscuro = new Color(30, 64, 175);
    private final Color corVermelho = new Color(185, 28, 28);
    private final Color corTexto = new Color(15, 23, 42);
    private final Color corTextoSuave = new Color(71, 85, 105);
    private final Color corBorda = new Color(148, 163, 184);
    private final Color corBordaSuave = new Color(203, 213, 225);
    private final Color corCinzaBotao = new Color(226, 232, 240);
    private final Color corFundoComandos = new Color(248, 250, 252);
    private final Color corVerdeBotao = new Color(21, 128, 61);
    private final Color corVerdeBotaoHover = new Color(22, 101, 52);
    private final Color corAzulBotao = new Color(29, 78, 216);
    private final Color corAzulBotaoHover = new Color(30, 64, 175);
    private final Color corIndigoBotao = new Color(67, 56, 202);
    private final Color corIndigoBotaoHover = new Color(55, 48, 163);
    private final Color corLaranjaBotao = new Color(194, 65, 12);
    private final Color corLaranjaBotaoHover = new Color(154, 52, 18);
    private final Color corCinzaEscuroBotao = new Color(51, 65, 85);
    private final Color corCinzaEscuroBotaoHover = new Color(30, 41, 59);
    private final Color corVermelhoBotao = new Color(185, 28, 28);
    private final Color corVermelhoBotaoHover = new Color(153, 27, 27);

    private final Font fontePadrao = new Font("Arial", Font.PLAIN, 15);
    private final Font fonteLabel = new Font("Arial", Font.BOLD, 13);
    private final Font fonteBotao = new Font("Arial", Font.BOLD, 13);
    private final Font fonteAba = new Font("Arial", Font.BOLD, 14);

    private Grafo grafo;
    private ResultadoBusca ultimoResultadoBfs;
    private ResultadoBusca ultimoResultadoDfs;

    private JSplitPane divisaoPrincipal;
    private JPanel painelControlesCompleto;
    private JPanel painelControlesMinimizado;
    private boolean controlesVisiveis = true;

    private PainelVisualizacaoGrafo painelVisualizacao;
    private JTextField campoVertice;
    private JTextField campoPesoAresta;
    private JComboBox<String> comboOrigemAresta;
    private JComboBox<String> comboDestinoAresta;
    private JComboBox<String> comboOrigemBusca;
    private JComboBox<String> comboDestinoBusca;
    private JTextArea areaResultado;
    private JLabel rotuloResumo;
    private JLabel etiquetaBfs;
    private JLabel etiquetaDfs;
    private JLabel rotuloStatus;
    private JButton botaoAlternarControles;

    public ControleGrafo() {
        grafo = new Grafo(50, false);
        configurarJanela();
        montarTela();
        atualizarTudo("");
    }

    private void configurarJanela() {
        setTitle("Trabalho de Grafos - BFS e DFS");
        setSize(1380, 820);
        setMinimumSize(new Dimension(1080, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void montarTela() {
        JPanel raiz = new JPanel(new BorderLayout(8, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        raiz.setBackground(corFundo);

        painelControlesCompleto = criarPainelControles();
        painelControlesMinimizado = criarPainelControlesMinimizado();

        JPanel painelGrafo = criarPainelGrafo();
        divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelControlesCompleto, painelGrafo);
        divisaoPrincipal.setResizeWeight(0.0);
        divisaoPrincipal.setDividerSize(6);
        divisaoPrincipal.setBorder(BorderFactory.createEmptyBorder());
        SwingUtilities.invokeLater(() -> divisaoPrincipal.setDividerLocation(315));

        raiz.add(divisaoPrincipal, BorderLayout.CENTER);
        rotuloStatus = new JLabel("");
        add(raiz);
    }

    private JPanel criarPainelControles() {
        JPanel painel = new JPanel(new BorderLayout(7, 7));
        painel.setPreferredSize(new Dimension(315, 600));
        painel.setMinimumSize(new Dimension(295, 420));
        painel.setBackground(corFundoComandos);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(7, 7, 7, 7)
        ));

        painel.add(criarTopoComandos(), BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(fonteAba);
        abas.setBackground(Color.WHITE);
        abas.setForeground(corTexto);
        abas.setBorder(BorderFactory.createLineBorder(corBordaSuave));
        abas.addTab("Entrada", criarAbaEntrada());
        abas.addTab("Busca", criarAbaBusca());
        abas.addTab("Dados", criarAbaDados());
        abas.addTab("Resultado", criarAbaResultado());
        painel.add(abas, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarTopoComandos() {
        JPanel topo = new JPanel(new BorderLayout(6, 0));
        topo.setBackground(corAzulEscuro);
        topo.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel titulo = new JLabel("COMANDOS");
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);

        JButton botaoOcultar = criarBotaoTopo("Ocultar");
        botaoOcultar.addActionListener(e -> alternarPainelControles());

        topo.add(titulo, BorderLayout.CENTER);
        topo.add(botaoOcultar, BorderLayout.EAST);
        return topo;
    }

    private JPanel criarPainelControlesMinimizado() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setPreferredSize(new Dimension(46, 600));
        painel.setMinimumSize(new Dimension(46, 420));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(6, 5, 6, 5)
        ));

        JButton botaoMostrar = new JButton("☰");
        botaoMostrar.setFont(new Font("Arial", Font.BOLD, 20));
        botaoMostrar.setFocusPainted(false);
        botaoMostrar.setBackground(Color.WHITE);
        botaoMostrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoMostrar.addActionListener(e -> alternarPainelControles());

        painel.add(botaoMostrar, BorderLayout.NORTH);
        return painel;
    }

    private JPanel criarAbaEntrada() {
        JPanel painel = criarAbaBase();
        painel.add(criarCartaoVertice());
        painel.add(Box.createVerticalStrut(8));
        painel.add(criarCartaoAresta());
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private JPanel criarCartaoVertice() {
        JPanel painel = criarCartao("Vértice");
        painel.setLayout(new GridBagLayout());

        campoVertice = criarCampoTexto();
        campoVertice.addActionListener(e -> adicionarVertice());
        JButton botao = criarBotaoAdicionar("+ ADICIONAR");
        botao.addActionListener(e -> adicionarVertice());

        GridBagConstraints gbc = criarGbc();
        painel.add(criarLabel("Nome"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoVertice, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 2, 2, 2);
        painel.add(botao, gbc);
        return painel;
    }

    private JPanel criarCartaoAresta() {
        JPanel painel = criarCartao("Aresta");
        painel.setLayout(new GridBagLayout());

        comboOrigemAresta = criarCombo();
        comboDestinoAresta = criarCombo();
        campoPesoAresta = criarCampoTexto();
        campoPesoAresta.addActionListener(e -> adicionarAresta());

        JButton botao = criarBotaoAdicionar("+ CRIAR ARESTA");
        botao.addActionListener(e -> adicionarAresta());

        GridBagConstraints gbc = criarGbc();
        painel.add(criarLabel("Origem"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(comboOrigemAresta, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painel.add(criarLabel("Destino"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(comboDestinoAresta, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painel.add(criarLabel("Peso"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoPesoAresta, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 2, 2, 2);
        painel.add(botao, gbc);
        return painel;
    }

    private JPanel criarAbaBusca() {
        JPanel painel = criarAbaBase();

        JPanel cartao = criarCartao("Busca");
        cartao.setLayout(new GridBagLayout());

        comboOrigemBusca = criarCombo();
        comboDestinoBusca = criarCombo();

        JButton botaoExecutar = criarBotaoPrimario("EXECUTAR BFS E DFS");
        JButton botaoBfs = criarBotaoAnimacaoBfs("ANIMAR BFS");
        JButton botaoDfs = criarBotaoAnimacaoDfs("ANIMAR DFS");

        botaoExecutar.addActionListener(e -> executarBuscas());
        botaoBfs.addActionListener(e -> animarBfs());
        botaoDfs.addActionListener(e -> animarDfs());

        JSlider slider = new JSlider(220, 1300, 620);
        slider.setOpaque(false);
        slider.addChangeListener(e -> painelVisualizacao.setTempoPorPasso(slider.getValue()));

        GridBagConstraints gbc = criarGbc();
        cartao.add(criarLabel("Origem"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        cartao.add(comboOrigemBusca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        cartao.add(criarLabel("Destino"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        cartao.add(comboDestinoBusca, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 2, 3, 2);
        cartao.add(botaoExecutar, gbc);

        JPanel linhaAnimacao = new JPanel(new GridLayout(1, 2, 6, 0));
        linhaAnimacao.setOpaque(false);
        linhaAnimacao.add(botaoBfs);
        linhaAnimacao.add(botaoDfs);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 2, 3, 2);
        cartao.add(linhaAnimacao, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(6, 2, 0, 2);
        cartao.add(criarLabel("Velocidade"), gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 2, 2, 2);
        cartao.add(slider, gbc);

        etiquetaBfs = criarEtiquetaResumo("BFS: -", corAzulEscuro);
        etiquetaDfs = criarEtiquetaResumo("DFS: -", new Color(234, 88, 12));
        JPanel painelEtiquetas = new JPanel(new GridLayout(2, 1, 0, 5));
        painelEtiquetas.setOpaque(false);
        painelEtiquetas.add(etiquetaBfs);
        painelEtiquetas.add(etiquetaDfs);

        painel.add(cartao);
        painel.add(Box.createVerticalStrut(8));
        painel.add(painelEtiquetas);
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private JPanel criarAbaDados() {
        JPanel painel = criarAbaBase();
        JPanel cartao = criarCartao("Dados");
        cartao.setLayout(new GridLayout(4, 1, 0, 7));

        JButton botaoLista = criarBotaoSecundario("LISTA DE VIZINHOS");
        JButton botaoMatriz = criarBotaoSecundario("MATRIZ");
        JButton botaoLimpar = criarBotaoSecundario("LIMPAR DESTAQUES");
        JButton botaoNovo = criarBotaoPerigo("NOVO GRAFO");

        botaoLista.addActionListener(e -> mostrarLista());
        botaoMatriz.addActionListener(e -> mostrarMatriz());
        botaoLimpar.addActionListener(e -> limparDestaques());
        botaoNovo.addActionListener(e -> confirmarNovoGrafo());

        cartao.add(botaoLista);
        cartao.add(botaoMatriz);
        cartao.add(botaoLimpar);
        cartao.add(botaoNovo);

        painel.add(cartao);
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    private JPanel criarAbaResultado() {
        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBackground(corPainel);
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaResultado.setForeground(corTexto);
        areaResultado.setBackground(new Color(248, 250, 252));
        areaResultado.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        areaResultado.setText("");

        JScrollPane rolagem = new JScrollPane(areaResultado);
        rolagem.setBorder(BorderFactory.createLineBorder(corBorda));
        painel.add(rolagem, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelGrafo() {
        JPanel painel = new JPanel(new BorderLayout(0, 5));
        painel.setBackground(corPainel);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        painelVisualizacao = new PainelVisualizacaoGrafo();
        painelVisualizacao.setGrafo(grafo);

        painel.add(criarBarraFerramentasGrafo(), BorderLayout.NORTH);
        painel.add(painelVisualizacao, BorderLayout.CENTER);
        return painel;
    }

    private JToolBar criarBarraFerramentasGrafo() {
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);
        barra.setRollover(true);
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

        botaoAlternarControles = criarBotaoPequeno("☰ Comandos");
        botaoAlternarControles.addActionListener(e -> alternarPainelControles());

        JButton botaoReorganizar = criarBotaoPequeno("Organizar");
        JButton botaoCentralizar = criarBotaoPequeno("Centralizar");
        JButton botaoZoomMais = criarBotaoPequeno("+");
        JButton botaoZoomMenos = criarBotaoPequeno("−");
        JButton botaoLimpar = criarBotaoPequeno("Limpar");

        botaoReorganizar.addActionListener(e -> painelVisualizacao.reorganizarGrafo());
        botaoCentralizar.addActionListener(e -> painelVisualizacao.ajustarVisualizacao());
        botaoZoomMais.addActionListener(e -> painelVisualizacao.aumentarZoom());
        botaoZoomMenos.addActionListener(e -> painelVisualizacao.diminuirZoom());
        botaoLimpar.addActionListener(e -> limparDestaques());

        barra.add(botaoAlternarControles);
        barra.addSeparator();
        barra.add(botaoReorganizar);
        barra.add(botaoCentralizar);
        barra.add(botaoZoomMais);
        barra.add(botaoZoomMenos);
        barra.add(botaoLimpar);
        barra.add(Box.createHorizontalGlue());

        rotuloResumo = criarEtiquetaResumo("0 vértices • 0 arestas", corAzulEscuro);
        barra.add(rotuloResumo);
        return barra;
    }

    private JPanel criarAbaBase() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corPainel);
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return painel;
    }

    private JPanel criarCartao(String titulo) {
        JPanel painel = new JPanel();
        painel.setBackground(corPainel);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        TitledBorder bordaTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                titulo
        );
        bordaTitulo.setTitleFont(new Font("Arial", Font.BOLD, 14));
        bordaTitulo.setTitleColor(corAzulEscuro);
        painel.setBorder(BorderFactory.createCompoundBorder(
                bordaTitulo,
                BorderFactory.createEmptyBorder(7, 7, 8, 7)
        ));
        return painel;
    }

    private GridBagConstraints criarGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 2, 4, 2);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fonteLabel);
        label.setForeground(corTexto);
        return label;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(fontePadrao);
        campo.setPreferredSize(new Dimension(175, 34));
        campo.setMinimumSize(new Dimension(145, 34));
        campo.setBackground(Color.WHITE);
        campo.setForeground(corTexto);
        campo.setCaretColor(corAzulEscuro);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return campo;
    }

    private JComboBox<String> criarCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(fontePadrao);
        combo.setBackground(Color.WHITE);
        combo.setForeground(corTexto);
        combo.setPreferredSize(new Dimension(175, 34));
        combo.setMinimumSize(new Dimension(145, 34));
        return combo;
    }

    private JButton criarBotaoAdicionar(String texto) {
        return criarBotaoColorido(texto, corVerdeBotao, corVerdeBotaoHover, Color.WHITE, corVerdeBotaoHover, 38);
    }

    private JButton criarBotaoPrimario(String texto) {
        return criarBotaoColorido(texto, corAzulBotao, corAzulBotaoHover, Color.WHITE, corAzulEscuro, 38);
    }

    private JButton criarBotaoAnimacaoBfs(String texto) {
        return criarBotaoColorido(texto, corIndigoBotao, corIndigoBotaoHover, Color.WHITE, corIndigoBotaoHover, 36);
    }

    private JButton criarBotaoAnimacaoDfs(String texto) {
        return criarBotaoColorido(texto, corLaranjaBotao, corLaranjaBotaoHover, Color.WHITE, corLaranjaBotaoHover, 36);
    }

    private JButton criarBotaoSecundario(String texto) {
        return criarBotaoColorido(texto, corCinzaEscuroBotao, corCinzaEscuroBotaoHover, Color.WHITE, corCinzaEscuroBotaoHover, 36);
    }

    private JButton criarBotaoPerigo(String texto) {
        return criarBotaoColorido(texto, corVermelhoBotao, corVermelhoBotaoHover, Color.WHITE, corVermelhoBotaoHover, 36);
    }

    private JButton criarBotaoPequeno(String texto) {
        JButton botao = criarBotaoColorido(texto, Color.WHITE, new Color(226, 232, 240), corTexto, corBorda, 31);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setMargin(new Insets(3, 7, 3, 7));
        return botao;
    }

    private JButton criarBotaoTopo(String texto) {
        JButton botao = criarBotaoColorido(texto, Color.WHITE, new Color(219, 234, 254), corAzulEscuro, new Color(191, 219, 254), 31);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        return botao;
    }

    private JButton criarBotaoColorido(String texto, Color fundo, Color fundoHover, Color textoCor, Color borda, int altura) {
        JButton botao = criarBotaoBase(texto);
        botao.setBackground(fundo);
        botao.setForeground(textoCor);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borda, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        botao.setPreferredSize(new Dimension(0, altura));
        botao.setMinimumSize(new Dimension(90, altura));
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(fundoHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (botao.isEnabled()) {
                    botao.setBackground(fundo);
                }
            }
        });
        return botao;
    }

    private JButton criarBotaoBase(String texto) {
        JButton botao = new JButton(texto);
        botao.setUI(new BasicButtonUI());
        botao.setFont(fonteBotao);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(true);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setHorizontalAlignment(SwingConstants.CENTER);
        botao.setMargin(new Insets(5, 8, 5, 8));
        return botao;
    }

    private JLabel criarEtiquetaResumo(String texto, Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(cor);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBordaSuave),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return label;
    }

    private void adicionarVertice() {
        try {
            String nome = campoVertice.getText().trim();
            grafo.adicionarVertice(nome);
            campoVertice.setText("");
            limparResultadosSalvos();
            atualizarTudo("");
            painelVisualizacao.reorganizarGrafo();
            campoVertice.requestFocusInWindow();
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void adicionarAresta() {
        try {
            String origem = obterSelecionado(comboOrigemAresta, "Escolha a origem da aresta.");
            String destino = obterSelecionado(comboDestinoAresta, "Escolha o destino da aresta.");
            String pesoTexto = campoPesoAresta.getText().trim();
            if (pesoTexto.isEmpty()) {
                throw new IllegalArgumentException("Informe o peso da aresta.");
            }
            int peso = Integer.parseInt(pesoTexto);
            grafo.adicionarAresta(origem, destino, peso);
            campoPesoAresta.setText("");
            limparResultadosSalvos();
            atualizarTudo("");
        } catch (NumberFormatException ex) {
            mostrarErro("O peso da aresta deve ser um número inteiro.");
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void executarBuscas() {
        try {
            String origem = obterSelecionado(comboOrigemBusca, "Escolha o vértice de origem.");
            String destino = obterSelecionado(comboDestinoBusca, "Escolha o vértice de destino.");

            ultimoResultadoBfs = Busca.buscarEmLargura(grafo, origem, destino);
            ultimoResultadoDfs = Busca.buscarEmProfundidade(grafo, origem, destino);

            areaResultado.setText(montarTextoResultado(origem, destino));
            areaResultado.setCaretPosition(0);
            atualizarEtiquetasDeBusca();
            painelVisualizacao.destacarResultado(ultimoResultadoBfs);
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void animarBfs() {
        try {
            garantirBuscaCalculada();
            painelVisualizacao.animarResultado(ultimoResultadoBfs, "BFS", () -> atualizarStatus(""));
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void animarDfs() {
        try {
            garantirBuscaCalculada();
            painelVisualizacao.animarResultado(ultimoResultadoDfs, "DFS", () -> atualizarStatus(""));
        } catch (Exception ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private void garantirBuscaCalculada() {
        if (ultimoResultadoBfs == null || ultimoResultadoDfs == null) {
            executarBuscas();
        }
        if (ultimoResultadoBfs == null || ultimoResultadoDfs == null) {
            throw new IllegalStateException("Execute as buscas antes de animar.");
        }
    }

    private String montarTextoResultado(String origem, String destino) {
        StringBuilder texto = new StringBuilder();
        texto.append("Origem: ").append(origem).append("\n");
        texto.append("Destino: ").append(destino).append("\n\n");
        texto.append("BFS\n");
        texto.append("Visitados: ").append(Busca.formatarOrdemVisitada(ultimoResultadoBfs.getOrdemVisitada())).append("\n");
        texto.append("Caminho: ").append(Busca.formatarCaminho(ultimoResultadoBfs.getCaminho())).append("\n\n");
        texto.append("DFS\n");
        texto.append("Visitados: ").append(Busca.formatarOrdemVisitada(ultimoResultadoDfs.getOrdemVisitada())).append("\n");
        texto.append("Caminho: ").append(Busca.formatarCaminho(ultimoResultadoDfs.getCaminho())).append("\n");
        return texto.toString();
    }

    private void atualizarEtiquetasDeBusca() {
        if (etiquetaBfs == null || etiquetaDfs == null) {
            return;
        }
        if (ultimoResultadoBfs == null || ultimoResultadoDfs == null) {
            etiquetaBfs.setText("BFS: -");
            etiquetaDfs.setText("DFS: -");
            return;
        }
        etiquetaBfs.setText("BFS: " + resumirCaminho(ultimoResultadoBfs));
        etiquetaDfs.setText("DFS: " + resumirCaminho(ultimoResultadoDfs));
    }

    private String resumirCaminho(ResultadoBusca resultado) {
        if (resultado == null || !resultado.encontrouCaminho()) {
            return "sem caminho";
        }
        return resultado.getCaminho().size() + " nós";
    }

    private void mostrarLista() {
        areaResultado.setText(grafo.imprimirGrafo());
    }

    private void mostrarMatriz() {
        areaResultado.setText(grafo.imprimirMatriz());
    }

    private void limparDestaques() {
        painelVisualizacao.limparDestaques();
    }

    private void confirmarNovoGrafo() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja apagar o grafo atual?",
                "Novo grafo",
                JOptionPane.YES_NO_OPTION
        );
        if (resposta == JOptionPane.YES_OPTION) {
            grafo = new Grafo(50, false);
            limparResultadosSalvos();
            atualizarTudo("");
        }
    }

    private void alternarPainelControles() {
        controlesVisiveis = !controlesVisiveis;
        if (controlesVisiveis) {
            divisaoPrincipal.setLeftComponent(painelControlesCompleto);
            botaoAlternarControles.setText("☰ Comandos");
            SwingUtilities.invokeLater(() -> divisaoPrincipal.setDividerLocation(315));
        } else {
            divisaoPrincipal.setLeftComponent(painelControlesMinimizado);
            botaoAlternarControles.setText("☰ Comandos");
            SwingUtilities.invokeLater(() -> divisaoPrincipal.setDividerLocation(46));
        }
    }

    private void atualizarTudo(String mensagemStatus) {
        atualizarCombos();
        atualizarResumo();
        painelVisualizacao.setGrafo(grafo);
        atualizarEtiquetasDeBusca();
        atualizarStatus(mensagemStatus);
    }

    private void atualizarCombos() {
        DefaultComboBoxModel<String> modeloOrigemAresta = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloDestinoAresta = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloOrigemBusca = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloDestinoBusca = new DefaultComboBoxModel<>();

        for (Vertice vertice : grafo.getVertices()) {
            String nome = vertice.getNome();
            modeloOrigemAresta.addElement(nome);
            modeloDestinoAresta.addElement(nome);
            modeloOrigemBusca.addElement(nome);
            modeloDestinoBusca.addElement(nome);
        }

        comboOrigemAresta.setModel(modeloOrigemAresta);
        comboDestinoAresta.setModel(modeloDestinoAresta);
        comboOrigemBusca.setModel(modeloOrigemBusca);
        comboDestinoBusca.setModel(modeloDestinoBusca);
    }

    private void atualizarResumo() {
        if (rotuloResumo != null) {
            rotuloResumo.setText(grafo.quantidadeVertices() + " vértices • " + grafo.getArestas().size() + " arestas");
        }
    }

    private void limparResultadosSalvos() {
        ultimoResultadoBfs = null;
        ultimoResultadoDfs = null;
        if (areaResultado != null) {
            areaResultado.setText("");
        }
        if (painelVisualizacao != null) {
            painelVisualizacao.limparDestaques();
        }
    }

    private String obterSelecionado(JComboBox<String> combo, String mensagemErro) {
        Object item = combo.getSelectedItem();
        if (item == null || item.toString().trim().isEmpty()) {
            throw new IllegalArgumentException(mensagemErro);
        }
        return item.toString();
    }

    private void atualizarStatus(String mensagem) {
        if (rotuloStatus != null) {
            rotuloStatus.setText(mensagem);
        }
    }

    private void mostrarErro(String mensagem) {
        atualizarStatus(mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Entrada inválida", JOptionPane.WARNING_MESSAGE);
    }

    public static void abrir() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Mantém o visual padrão se o sistema não permitir alterar.
        }
        SwingUtilities.invokeLater(() -> new ControleGrafo().setVisible(true));
    }
}
