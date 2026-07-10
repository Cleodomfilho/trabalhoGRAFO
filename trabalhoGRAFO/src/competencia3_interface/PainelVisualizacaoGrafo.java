package competencia3_interface;

/*
 * Competência 3 do PDF: Interface gráfica.
 * Aqui ficam as telas, comandos e visualização animada do grafo.
 */

import competencia1_grafo.Aresta;
import competencia1_grafo.Grafo;
import competencia1_grafo.Vertice;
import competencia2_buscas.ResultadoBusca;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Painel responsável por desenhar e animar o grafo.
 *
 * Nesta versão a animação das buscas foi melhorada:
 * - a aresta que leva ao vértice atual é desenhada de forma progressiva;
 * - um ponto se movimenta pela aresta durante cada passo;
 * - os vértices visitados recebem numeração de ordem;
 * - o vértice atual pulsa durante a animação;
 * - o caminho final recebe destaque mais forte ao final da busca.
 */
public class PainelVisualizacaoGrafo extends JPanel {

    private static final int RAIO_VERTICE = 30;
    private static final int MARGEM_LAYOUT = 110;
    private static final int INTERVALO_TIMER = 16;

    private int tempoPorPasso = 620;

    private final Color corFundoTopo = new Color(239, 246, 255);
    private final Color corFundoBase = new Color(248, 250, 252);
    private final Color corGrade = new Color(226, 232, 240, 105);
    private final Color corTexto = new Color(15, 23, 42);
    private final Color corTextoSuave = new Color(71, 85, 105);
    private final Color corAresta = new Color(100, 116, 139);
    private final Color corArestaVisitada = new Color(37, 99, 235);
    private final Color corArestaAtual = new Color(249, 115, 22);
    private final Color corArestaCaminho = new Color(22, 163, 74);
    private final Color corVerticeNormal = Color.WHITE;
    private final Color corVerticeVisitado = new Color(219, 234, 254);
    private final Color corVerticeAtual = new Color(254, 215, 170);
    private final Color corVerticeCaminho = new Color(187, 247, 208);
    private final Color corBordaNormal = new Color(51, 65, 85);
    private final Color corBordaVisitado = new Color(37, 99, 235);
    private final Color corBordaAtual = new Color(234, 88, 12);
    private final Color corBordaCaminho = new Color(22, 163, 74);
    private final Color corCartao = new Color(255, 255, 255, 235);

    private Grafo grafo;
    private ResultadoBusca resultadoDestacado;
    private List<String> ordemAnimacao = new ArrayList<>();
    private List<String> caminhoDestacado = new ArrayList<>();
    private List<String> arestasAnimacao = new ArrayList<>();
    private final Map<String, Point2D.Double> posicoes = new HashMap<>();

    private Timer timerAnimacao;
    private int indiceAnimacao = -1;
    private double progressoPasso = 0.0;
    private long inicioPasso = 0;
    private String nomeBuscaAnimada = "";
    private Runnable aoFinalizarAnimacao;

    private double zoom = 1.0;
    private double deslocamentoX = 0;
    private double deslocamentoY = 0;
    private String verticeArrastado;
    private Point ultimoMouse;
    private boolean arrastandoFundo;
    private boolean posicoesForamCriadas = false;

    public PainelVisualizacaoGrafo() {
        setPreferredSize(new Dimension(760, 580));
        setMinimumSize(new Dimension(540, 420));
        setBackground(corFundoBase);
        setFocusable(true);
        configurarEventosMouse();
    }

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
        limparDestaquesSemRepaint();
        garantirPosicoesDosVertices();
        repaint();
    }

    public void destacarResultado(ResultadoBusca resultado) {
        pararAnimacao();
        this.resultadoDestacado = resultado;
        this.ordemAnimacao = resultado == null ? new ArrayList<>() : new ArrayList<>(resultado.getOrdemVisitada());
        this.arestasAnimacao = resultado == null ? new ArrayList<>() : new ArrayList<>(resultado.getArestasAnimacao());
        this.caminhoDestacado = resultado == null ? new ArrayList<>() : new ArrayList<>(resultado.getCaminho());
        this.indiceAnimacao = ordemAnimacao.size() - 1;
        this.progressoPasso = 1.0;
        this.nomeBuscaAnimada = resultado == null ? "" : "Resultado";
        repaint();
    }

    public void animarResultado(ResultadoBusca resultado, Runnable aoFinalizar) {
        animarResultado(resultado, "Busca", aoFinalizar);
    }

    public void animarResultado(ResultadoBusca resultado, String nomeBusca, Runnable aoFinalizar) {
        if (resultado == null) {
            return;
        }

        pararAnimacao();
        this.resultadoDestacado = resultado;
        this.ordemAnimacao = new ArrayList<>(resultado.getOrdemVisitada());
        this.arestasAnimacao = new ArrayList<>(resultado.getArestasAnimacao());
        this.caminhoDestacado = new ArrayList<>();
        this.indiceAnimacao = ordemAnimacao.isEmpty() ? -1 : 0;
        this.progressoPasso = 0.0;
        this.nomeBuscaAnimada = nomeBusca == null ? "Busca" : nomeBusca;
        this.aoFinalizarAnimacao = aoFinalizar;
        this.inicioPasso = System.currentTimeMillis();

        if (ordemAnimacao.isEmpty()) {
            finalizarAnimacao();
            return;
        }

        timerAnimacao = new Timer(INTERVALO_TIMER, e -> atualizarAnimacao());
        timerAnimacao.start();
        repaint();
    }

    private void atualizarAnimacao() {
        long agora = System.currentTimeMillis();
        progressoPasso = Math.min(1.0, (agora - inicioPasso) / (double) tempoPorPasso);

        if (progressoPasso >= 1.0) {
            if (indiceAnimacao >= ordemAnimacao.size() - 1) {
                finalizarAnimacao();
                return;
            }

            indiceAnimacao++;
            inicioPasso = agora;
            progressoPasso = 0.0;
        }

        repaint();
    }

    private void finalizarAnimacao() {
        pararAnimacao();
        progressoPasso = 1.0;
        indiceAnimacao = ordemAnimacao.size() - 1;

        if (resultadoDestacado != null) {
            caminhoDestacado = new ArrayList<>(resultadoDestacado.getCaminho());
        }

        repaint();

        if (aoFinalizarAnimacao != null) {
            Runnable tarefa = aoFinalizarAnimacao;
            aoFinalizarAnimacao = null;
            tarefa.run();
        }
    }

    public void limparDestaques() {
        pararAnimacao();
        limparDestaquesSemRepaint();
        repaint();
    }

    private void limparDestaquesSemRepaint() {
        resultadoDestacado = null;
        ordemAnimacao = new ArrayList<>();
        caminhoDestacado = new ArrayList<>();
        arestasAnimacao = new ArrayList<>();
        indiceAnimacao = -1;
        progressoPasso = 0.0;
        nomeBuscaAnimada = "";
        aoFinalizarAnimacao = null;
    }

    public void aumentarZoom() {
        zoom = Math.min(2.2, zoom + 0.12);
        repaint();
    }

    public void diminuirZoom() {
        zoom = Math.max(0.55, zoom - 0.12);
        repaint();
    }

    public void ajustarVisualizacao() {
        zoom = 1.0;
        deslocamentoX = 0;
        deslocamentoY = 0;
        repaint();
    }

    public void reorganizarGrafo() {
        criarLayoutCircular(true);
        ajustarVisualizacao();
        repaint();
    }

    public void setTempoPorPasso(int tempoPorPasso) {
        this.tempoPorPasso = Math.max(180, Math.min(1600, tempoPorPasso));
    }

    private void pararAnimacao() {
        if (timerAnimacao != null && timerAnimacao.isRunning()) {
            timerAnimacao.stop();
        }
        timerAnimacao = null;
    }

    private void configurarEventosMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                ultimoMouse = e.getPoint();
                verticeArrastado = encontrarVerticeNaTela(e.getPoint());
                arrastandoFundo = verticeArrastado == null;
                setCursor(Cursor.getPredefinedCursor(verticeArrastado == null ? Cursor.MOVE_CURSOR : Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                verticeArrastado = null;
                arrastandoFundo = false;
                ultimoMouse = null;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (ultimoMouse == null) {
                    ultimoMouse = e.getPoint();
                    return;
                }

                int dx = e.getX() - ultimoMouse.x;
                int dy = e.getY() - ultimoMouse.y;

                if (verticeArrastado != null) {
                    Point2D.Double mundo = converterTelaParaMundo(e.getPoint());
                    posicoes.put(verticeArrastado, mundo);
                } else if (arrastandoFundo) {
                    deslocamentoX += dx;
                    deslocamentoY += dy;
                }

                ultimoMouse = e.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                String vertice = encontrarVerticeNaTela(e.getPoint());
                setCursor(Cursor.getPredefinedCursor(vertice == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double fator = e.getWheelRotation() < 0 ? 1.08 : 0.92;
                zoom = Math.max(0.55, Math.min(2.2, zoom * fator));
                repaint();
            }
        });
    }

    private void garantirPosicoesDosVertices() {
        if (grafo == null) {
            posicoes.clear();
            posicoesForamCriadas = false;
            return;
        }

        Set<String> nomesAtuais = new HashSet<>();
        for (Vertice vertice : grafo.getVertices()) {
            nomesAtuais.add(vertice.getNome());
        }
        posicoes.keySet().removeIf(nome -> !nomesAtuais.contains(nome));

        if (!posicoesForamCriadas || posicoes.size() != grafo.quantidadeVertices()) {
            criarLayoutCircular(false);
        }
    }

    private void criarLayoutCircular(boolean forcarReorganizacao) {
        if (grafo == null || grafo.quantidadeVertices() == 0) {
            posicoes.clear();
            posicoesForamCriadas = false;
            return;
        }

        List<Vertice> vertices = grafo.getVertices();
        int quantidade = vertices.size();
        int largura = Math.max(getWidth(), 680);
        int altura = Math.max(getHeight(), 480);
        int centroX = largura / 2;
        int centroY = altura / 2 + 20;
        int raioX = Math.max(150, largura / 2 - MARGEM_LAYOUT);
        int raioY = Math.max(125, altura / 2 - MARGEM_LAYOUT);

        if (quantidade == 1) {
            if (forcarReorganizacao || !posicoes.containsKey(vertices.get(0).getNome())) {
                posicoes.put(vertices.get(0).getNome(), new Point2D.Double(centroX, centroY));
            }
            posicoesForamCriadas = true;
            return;
        }

        for (int i = 0; i < quantidade; i++) {
            Vertice vertice = vertices.get(i);
            if (!forcarReorganizacao && posicoes.containsKey(vertice.getNome())) {
                continue;
            }

            double angulo = 2 * Math.PI * i / quantidade - Math.PI / 2;
            double x = centroX + raioX * Math.cos(angulo);
            double y = centroY + raioY * Math.sin(angulo);
            posicoes.put(vertice.getNome(), new Point2D.Double(x, y));
        }

        posicoesForamCriadas = true;
    }

    private String encontrarVerticeNaTela(Point pontoTela) {
        if (grafo == null) {
            return null;
        }

        for (Vertice vertice : grafo.getVertices()) {
            Point2D.Double posicaoMundo = posicoes.get(vertice.getNome());
            if (posicaoMundo == null) {
                continue;
            }

            Point2D.Double posicaoTela = converterMundoParaTela(posicaoMundo);
            if (pontoTela.distance(posicaoTela) <= RAIO_VERTICE + 8) {
                return vertice.getNome();
            }
        }
        return null;
    }

    private Point2D.Double converterMundoParaTela(Point2D.Double mundo) {
        double centroX = getWidth() / 2.0;
        double centroY = getHeight() / 2.0;
        double x = (mundo.x - centroX) * zoom + centroX + deslocamentoX;
        double y = (mundo.y - centroY) * zoom + centroY + deslocamentoY;
        return new Point2D.Double(x, y);
    }

    private Point2D.Double converterTelaParaMundo(Point tela) {
        double centroX = getWidth() / 2.0;
        double centroY = getHeight() / 2.0;
        double x = (tela.x - centroX - deslocamentoX) / zoom + centroX;
        double y = (tela.y - centroY - deslocamentoY) / zoom + centroY;
        return new Point2D.Double(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        configurarQualidade(g2);
        desenharFundo(g2);

        if (grafo == null || grafo.quantidadeVertices() == 0) {
            desenharEstadoVazio(g2);
            g2.dispose();
            return;
        }

        garantirPosicoesDosVertices();
        desenharResumoSuperior(g2);
        desenharArestasBase(g2);
        desenharArestasVisitadas(g2);
        desenharCaminhoFinal(g2);
        desenharArestaAtual(g2);
        desenharVertices(g2);

        g2.dispose();
    }

    private void configurarQualidade(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void desenharFundo(Graphics2D g2) {
        GradientPaint gradiente = new GradientPaint(0, 0, corFundoTopo, 0, getHeight(), corFundoBase);
        g2.setPaint(gradiente);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(corGrade);
        int espaco = 42;
        for (int x = 0; x < getWidth(); x += espaco) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += espaco) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    private void desenharEstadoVazio(Graphics2D g2) {
        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;
        desenharCartao(g2, centroX - 180, centroY - 95, 360, 190, 26);

        Point p1 = new Point(centroX - 78, centroY - 42);
        Point p2 = new Point(centroX + 78, centroY - 42);
        Point p3 = new Point(centroX, centroY + 44);

        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(147, 197, 253));
        g2.draw(new Line2D.Double(p1, p2));
        g2.draw(new Line2D.Double(p2, p3));
        g2.draw(new Line2D.Double(p3, p1));

        desenharNoIlustrativo(g2, p1, "A");
        desenharNoIlustrativo(g2, p2, "B");
        desenharNoIlustrativo(g2, p3, "C");

        g2.setFont(new Font("Arial", Font.BOLD, 21));
        g2.setColor(corTexto);
        desenharTextoCentralizado(g2, "Grafo vazio", centroX, centroY + 92);
    }

    private void desenharNoIlustrativo(Graphics2D g2, Point p, String texto) {
        g2.setColor(Color.WHITE);
        g2.fillOval(p.x - 24, p.y - 24, 48, 48);
        g2.setColor(new Color(37, 99, 235));
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(p.x - 24, p.y - 24, 48, 48);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(corTexto);
        desenharTextoCentralizado(g2, texto, p.x, p.y + 6);
    }

    private void desenharResumoSuperior(Graphics2D g2) {
        String tipo = grafo.isDirecionado() ? "direcionado" : "não direcionado";
        String texto = grafo.quantidadeVertices() + " vértices • " + grafo.getArestas().size() + " arestas • " + tipo;
        String status = statusAnimacaoAtual();
        String completo = status.isEmpty() ? texto : texto + " • " + status;

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(completo) + 28;
        int altura = 34;
        int x = 16;
        int y = 16;

        desenharCartao(g2, x, y, largura, altura, 18);
        g2.setColor(corTexto);
        g2.drawString(completo, x + 14, y + 22);
    }

    private String statusAnimacaoAtual() {
        if (nomeBuscaAnimada == null || nomeBuscaAnimada.isEmpty()) {
            return "";
        }
        if (timerAnimacao != null && timerAnimacao.isRunning()) {
            int passo = Math.max(1, Math.min(indiceAnimacao + 1, ordemAnimacao.size()));
            return nomeBuscaAnimada + " " + passo + "/" + ordemAnimacao.size();
        }
        return nomeBuscaAnimada;
    }

    private void desenharArestasBase(Graphics2D g2) {
        Stroke strokeOriginal = g2.getStroke();
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (Aresta aresta : grafo.getArestas()) {
            String origem = aresta.getOrigem().getNome();
            String destino = aresta.getDestino().getNome();
            Point2D.Double p1 = obterTela(origem);
            Point2D.Double p2 = obterTela(destino);

            if (p1 == null || p2 == null) {
                continue;
            }

            Shape curva = criarCurvaAresta(p1, p2);
            g2.setColor(corAresta);
            g2.draw(curva);

            if (grafo.isDirecionado()) {
                desenharSeta(g2, p1, p2, corAresta, 11);
            }

            desenharPeso(g2, p1, p2, aresta.getPeso());
        }

        g2.setStroke(strokeOriginal);
    }

    private void desenharArestasVisitadas(Graphics2D g2) {
        if (ordemAnimacao.isEmpty()) {
            return;
        }

        Stroke strokeOriginal = g2.getStroke();
        g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(corArestaVisitada);

        int limite = Math.min(indiceAnimacao, arestasAnimacao.size() - 1);
        for (int i = 1; i <= limite; i++) {
            if (i == indiceAnimacao && timerAnimacao != null && timerAnimacao.isRunning()) {
                continue;
            }

            String aresta = arestasAnimacao.get(i);
            String[] partes = separarAresta(aresta);
            if (partes == null) {
                continue;
            }

            Point2D.Double p1 = obterTela(partes[0]);
            Point2D.Double p2 = obterTela(partes[1]);
            if (p1 != null && p2 != null) {
                g2.draw(criarCurvaAresta(p1, p2));
            }
        }

        g2.setStroke(strokeOriginal);
    }

    private void desenharCaminhoFinal(Graphics2D g2) {
        if (caminhoDestacado.size() < 2) {
            return;
        }

        Stroke strokeOriginal = g2.getStroke();

        for (int i = 0; i < caminhoDestacado.size() - 1; i++) {
            Point2D.Double p1 = obterTela(caminhoDestacado.get(i));
            Point2D.Double p2 = obterTela(caminhoDestacado.get(i + 1));
            if (p1 == null || p2 == null) {
                continue;
            }

            Shape curva = criarCurvaAresta(p1, p2);
            g2.setColor(new Color(22, 163, 74, 70));
            g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(curva);

            g2.setColor(corArestaCaminho);
            g2.setStroke(new BasicStroke(6.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(curva);
        }

        g2.setStroke(strokeOriginal);
    }

    private void desenharArestaAtual(Graphics2D g2) {
        if (timerAnimacao == null || !timerAnimacao.isRunning()) {
            return;
        }
        if (indiceAnimacao < 0 || indiceAnimacao >= arestasAnimacao.size()) {
            return;
        }

        String aresta = arestasAnimacao.get(indiceAnimacao);
        String[] partes = separarAresta(aresta);
        if (partes == null) {
            return;
        }

        Point2D.Double p1 = obterTela(partes[0]);
        Point2D.Double p2 = obterTela(partes[1]);
        if (p1 == null || p2 == null) {
            return;
        }

        double progressoSuave = suavizar(progressoPasso);
        Stroke strokeOriginal = g2.getStroke();

        g2.setColor(new Color(249, 115, 22, 80));
        g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(criarCurvaParcial(p1, p2, progressoSuave));

        g2.setColor(corArestaAtual);
        g2.setStroke(new BasicStroke(5.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(criarCurvaParcial(p1, p2, progressoSuave));
        g2.setStroke(strokeOriginal);

        Point2D.Double pontoMovel = pontoNaCurva(p1, p2, progressoSuave);
        desenharPontoMovel(g2, pontoMovel);
    }

    private Shape criarCurvaAresta(Point2D.Double p1, Point2D.Double p2) {
        double[] dados = calcularDadosCurva(p1, p2);
        return new QuadCurve2D.Double(dados[0], dados[1], dados[2], dados[3], dados[4], dados[5]);
    }

    private Shape criarCurvaParcial(Point2D.Double p1, Point2D.Double p2, double progresso) {
        double[] dados = calcularDadosCurva(p1, p2);
        double inicioX = dados[0];
        double inicioY = dados[1];
        double controleX = dados[2];
        double controleY = dados[3];
        double fimX = dados[4];
        double fimY = dados[5];

        Path2D.Double caminho = new Path2D.Double();
        caminho.moveTo(inicioX, inicioY);
        int segmentos = Math.max(2, (int) (32 * progresso));
        for (int i = 1; i <= segmentos; i++) {
            double t = progresso * i / segmentos;
            double x = pontoQuadratico(inicioX, controleX, fimX, t);
            double y = pontoQuadratico(inicioY, controleY, fimY, t);
            caminho.lineTo(x, y);
        }
        return caminho;
    }

    private Point2D.Double pontoNaCurva(Point2D.Double p1, Point2D.Double p2, double progresso) {
        double[] dados = calcularDadosCurva(p1, p2);
        double x = pontoQuadratico(dados[0], dados[2], dados[4], progresso);
        double y = pontoQuadratico(dados[1], dados[3], dados[5], progresso);
        return new Point2D.Double(x, y);
    }

    private double[] calcularDadosCurva(Point2D.Double p1, Point2D.Double p2) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia == 0) {
            distancia = 1;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;
        double inicioX = p1.x + ux * RAIO_VERTICE;
        double inicioY = p1.y + uy * RAIO_VERTICE;
        double fimX = p2.x - ux * RAIO_VERTICE;
        double fimY = p2.y - uy * RAIO_VERTICE;

        double meioX = (inicioX + fimX) / 2.0;
        double meioY = (inicioY + fimY) / 2.0;
        double curvatura = Math.min(38, Math.max(8, distancia / 18));
        double controleX = meioX - uy * curvatura;
        double controleY = meioY + ux * curvatura;

        return new double[]{inicioX, inicioY, controleX, controleY, fimX, fimY};
    }

    private double pontoQuadratico(double inicio, double controle, double fim, double t) {
        double umMenosT = 1.0 - t;
        return umMenosT * umMenosT * inicio + 2 * umMenosT * t * controle + t * t * fim;
    }

    private double suavizar(double valor) {
        double t = Math.max(0, Math.min(1, valor));
        return t * t * (3 - 2 * t);
    }

    private void desenharSeta(Graphics2D g2, Point2D.Double origem, Point2D.Double destino, Color cor, int tamanho) {
        double dx = destino.x - origem.x;
        double dy = destino.y - origem.y;
        double angulo = Math.atan2(dy, dx);

        int pontaX = (int) (destino.x - Math.cos(angulo) * RAIO_VERTICE);
        int pontaY = (int) (destino.y - Math.sin(angulo) * RAIO_VERTICE);
        int x1 = (int) (pontaX - tamanho * Math.cos(angulo - Math.PI / 6));
        int y1 = (int) (pontaY - tamanho * Math.sin(angulo - Math.PI / 6));
        int x2 = (int) (pontaX - tamanho * Math.cos(angulo + Math.PI / 6));
        int y2 = (int) (pontaY - tamanho * Math.sin(angulo + Math.PI / 6));

        g2.setColor(cor);
        g2.drawLine(pontaX, pontaY, x1, y1);
        g2.drawLine(pontaX, pontaY, x2, y2);
    }

    private void desenharPeso(Graphics2D g2, Point2D.Double p1, Point2D.Double p2, int peso) {
        String texto = String.valueOf(peso);
        double x = (p1.x + p2.x) / 2.0;
        double y = (p1.y + p2.y) / 2.0;

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int largura = fm.stringWidth(texto) + 16;
        int altura = 22;
        int rx = (int) x - largura / 2;
        int ry = (int) y - altura / 2;

        g2.setColor(new Color(255, 255, 255, 238));
        g2.fillRoundRect(rx, ry, largura, altura, 12, 12);
        g2.setColor(new Color(203, 213, 225));
        g2.drawRoundRect(rx, ry, largura, altura, 12, 12);
        g2.setColor(corTexto);
        g2.drawString(texto, (int) x - fm.stringWidth(texto) / 2, (int) y + fm.getAscent() / 2 - 3);
    }

    private void desenharPontoMovel(Graphics2D g2, Point2D.Double ponto) {
        int raio = 8;
        g2.setColor(new Color(249, 115, 22, 80));
        g2.fillOval((int) ponto.x - 14, (int) ponto.y - 14, 28, 28);
        g2.setColor(Color.WHITE);
        g2.fillOval((int) ponto.x - raio, (int) ponto.y - raio, raio * 2, raio * 2);
        g2.setColor(corArestaAtual);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval((int) ponto.x - raio, (int) ponto.y - raio, raio * 2, raio * 2);
    }

    private void desenharVertices(Graphics2D g2) {
        for (Vertice vertice : grafo.getVertices()) {
            Point2D.Double p = obterTela(vertice.getNome());
            if (p != null) {
                desenharVertice(g2, vertice.getNome(), p);
            }
        }
    }

    private void desenharVertice(Graphics2D g2, String nome, Point2D.Double p) {
        boolean visitado = verticeJaFoiVisitado(nome);
        boolean caminho = caminhoDestacado.contains(nome);
        boolean atual = nome.equals(getVerticeAtualAnimacao());
        double pulso = calcularPulso();

        Color preenchimento = corVerticeNormal;
        Color borda = corBordaNormal;
        int raio = RAIO_VERTICE;
        float espessura = 2.4f;

        if (visitado) {
            preenchimento = corVerticeVisitado;
            borda = corBordaVisitado;
            espessura = 3.0f;
        }
        if (caminho) {
            preenchimento = corVerticeCaminho;
            borda = corBordaCaminho;
            espessura = 4.5f;
        }
        if (atual) {
            preenchimento = corVerticeAtual;
            borda = corBordaAtual;
            raio = (int) (RAIO_VERTICE + 4 + pulso * 5);
            espessura = 4.8f;
            desenharHalo(g2, p, pulso);
        }

        g2.setColor(new Color(15, 23, 42, 45));
        g2.fillOval((int) p.x - raio + 4, (int) p.y - raio + 6, raio * 2, raio * 2);

        GradientPaint gradiente = new GradientPaint(
                (float) p.x, (float) (p.y - raio), Color.WHITE,
                (float) p.x, (float) (p.y + raio), preenchimento
        );
        g2.setPaint(gradiente);
        g2.fill(new Ellipse2D.Double(p.x - raio, p.y - raio, raio * 2, raio * 2));

        g2.setColor(borda);
        g2.setStroke(new BasicStroke(espessura));
        g2.draw(new Ellipse2D.Double(p.x - raio, p.y - raio, raio * 2, raio * 2));

        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(corTexto);
        desenharTextoCentralizado(g2, nome, (int) p.x, (int) p.y + 6);

        int ordem = indiceDoVerticeNaAnimacao(nome);
        if (ordem >= 0 && visitado) {
            desenharMarcadorOrdem(g2, p, ordem + 1, caminho);
        }
    }

    private void desenharHalo(Graphics2D g2, Point2D.Double p, double pulso) {
        int raioHalo = (int) (RAIO_VERTICE + 15 + pulso * 14);
        g2.setColor(new Color(249, 115, 22, 80));
        g2.fillOval((int) p.x - raioHalo, (int) p.y - raioHalo, raioHalo * 2, raioHalo * 2);
    }

    private void desenharMarcadorOrdem(Graphics2D g2, Point2D.Double p, int numero, boolean caminho) {
        String texto = String.valueOf(numero);
        int raio = 11;
        int x = (int) p.x + 19;
        int y = (int) p.y - 28;

        g2.setColor(caminho ? corArestaCaminho : corArestaVisitada);
        g2.fillOval(x - raio, y - raio, raio * 2, raio * 2);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x - fm.stringWidth(texto) / 2, y + fm.getAscent() / 2 - 3);
    }

    private int indiceDoVerticeNaAnimacao(String nome) {
        int limite = indiceLimiteVisitados();
        for (int i = 0; i <= limite && i < ordemAnimacao.size(); i++) {
            if (ordemAnimacao.get(i).equals(nome)) {
                return i;
            }
        }
        return -1;
    }

    private boolean verticeJaFoiVisitado(String nome) {
        return indiceDoVerticeNaAnimacao(nome) >= 0;
    }

    private int indiceLimiteVisitados() {
        if (ordemAnimacao.isEmpty() || indiceAnimacao < 0) {
            return -1;
        }
        return Math.min(indiceAnimacao, ordemAnimacao.size() - 1);
    }

    private String getVerticeAtualAnimacao() {
        if (timerAnimacao == null || !timerAnimacao.isRunning()) {
            return null;
        }
        if (indiceAnimacao < 0 || indiceAnimacao >= ordemAnimacao.size()) {
            return null;
        }
        return ordemAnimacao.get(indiceAnimacao);
    }

    private boolean arestaEstaNoCaminho(String origem, String destino) {
        if (caminhoDestacado.size() < 2) {
            return false;
        }

        for (int i = 0; i < caminhoDestacado.size() - 1; i++) {
            String atual = caminhoDestacado.get(i);
            String proximo = caminhoDestacado.get(i + 1);

            boolean mesmaDirecao = atual.equals(origem) && proximo.equals(destino);
            boolean direcaoContraria = !grafo.isDirecionado() && atual.equals(destino) && proximo.equals(origem);

            if (mesmaDirecao || direcaoContraria) {
                return true;
            }
        }
        return false;
    }

    private String[] separarAresta(String aresta) {
        if (aresta == null || !aresta.contains("->")) {
            return null;
        }
        String[] partes = aresta.split("->", 2);
        if (partes.length != 2 || partes[0].trim().isEmpty() || partes[1].trim().isEmpty()) {
            return null;
        }
        return new String[]{partes[0].trim(), partes[1].trim()};
    }

    private Point2D.Double obterTela(String nomeVertice) {
        Point2D.Double mundo = posicoes.get(nomeVertice);
        if (mundo == null) {
            return null;
        }
        return converterMundoParaTela(mundo);
    }

    private double calcularPulso() {
        if (timerAnimacao == null || !timerAnimacao.isRunning()) {
            return 0.0;
        }
        return 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 115.0);
    }

    private void desenharCartao(Graphics2D g2, int x, int y, int largura, int altura, int arco) {
        g2.setColor(new Color(15, 23, 42, 25));
        g2.fill(new RoundRectangle2D.Double(x + 3, y + 5, largura, altura, arco, arco));
        g2.setColor(corCartao);
        g2.fill(new RoundRectangle2D.Double(x, y, largura, altura, arco, arco));
        g2.setColor(new Color(203, 213, 225));
        g2.draw(new RoundRectangle2D.Double(x, y, largura, altura, arco, arco));
    }

    private void desenharTextoCentralizado(Graphics2D g2, String texto, int centroX, int baseY) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, centroX - fm.stringWidth(texto) / 2, baseY);
    }
}
