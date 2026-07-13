package competencia1_grafo;

/*
 * Competência 1 do PDF: Implementação do Grafo.
 * Aqui ficam as classes de modelo: Grafo, Vértice e Aresta.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe principal da estrutura de dados Grafo.
 * A representação usada é a matriz de adjacência.
 */
public class Grafo {

    private int[][] matrizAdjacencia;
    private List<Vertice> vertices;
    private List<Aresta> arestas;
    private boolean direcionado;
    private int capacidadeMaxima;

    /**
     * Cria um grafo com capacidade máxima de vértices e tipo direcionado ou não.
     */
    public Grafo(int capacidadeMaxima, boolean direcionado) {
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("A capacidade do grafo deve ser maior que zero.");
        }
        this.capacidadeMaxima = capacidadeMaxima;
        this.direcionado = direcionado;
        this.matrizAdjacencia = new int[capacidadeMaxima][capacidadeMaxima];
        this.vertices = new ArrayList<>();
        this.arestas = new ArrayList<>();
    }

    /**
     * Adiciona um novo vértice ao grafo se o nome for válido e não existir.
     */
    public void adicionarVertice(String nome) {
        if (vertices.size() >= capacidadeMaxima) {
            throw new IllegalStateException("Limite máximo de vértices atingido.");
        }

        Vertice novoVertice = new Vertice(nome);

        if (existeVertice(novoVertice.getNome())) {
            throw new IllegalArgumentException("Já existe um vértice com esse nome.");
        }

        vertices.add(novoVertice);
    }

    /**
     * Cria uma aresta entre dois vértices existentes com o peso informado.
     */
    public void adicionarAresta(String nomeOrigem, String nomeDestino, int peso) {
        int indiceOrigem = getIndiceVertice(nomeOrigem);
        int indiceDestino = getIndiceVertice(nomeDestino);

        if (indiceOrigem == -1 || indiceDestino == -1) {
            throw new IllegalArgumentException("Origem ou destino não existe no grafo.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso da aresta precisa ser maior que zero.");
        }
        if (indiceOrigem == indiceDestino) {
            throw new IllegalArgumentException("A origem e o destino precisam ser diferentes.");
        }

        matrizAdjacencia[indiceOrigem][indiceDestino] = peso;
        if (!direcionado) {
            matrizAdjacencia[indiceDestino][indiceOrigem] = peso;
        }

        atualizarListaDeArestas(indiceOrigem, indiceDestino, peso);
    }

    /**
     * Atualiza a lista de arestas no modo visual e evita duplicidade.
     */
    private void atualizarListaDeArestas(int indiceOrigem, int indiceDestino, int peso) {
        Vertice origem = vertices.get(indiceOrigem);
        Vertice destino = vertices.get(indiceDestino);

        for (Aresta aresta : arestas) {
            boolean mesmaDirecao = aresta.getOrigem().equals(origem) && aresta.getDestino().equals(destino);
            boolean direcaoContraria = !direcionado && aresta.getOrigem().equals(destino) && aresta.getDestino().equals(origem);

            if (mesmaDirecao || direcaoContraria) {
                aresta.setPeso(peso);
                return;
            }
        }

        arestas.add(new Aresta(origem, destino, peso));
    }

    /**
     * Verifica se um vértice com este nome existe no grafo.
     */
    public boolean existeVertice(String nome) {
        return getIndiceVertice(nome) != -1;
    }

    /**
     * Retorna o índice do vértice pelo nome ou -1 se não existir.
     */
    public int getIndiceVertice(String nome) {
        if (nome == null) {
            return -1;
        }

        String nomeTratado = nome.trim().toUpperCase();

        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getNome().equalsIgnoreCase(nomeTratado)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retorna o vértice no índice informado.
     */
    public Vertice getVertice(int indice) {
        if (indice < 0 || indice >= vertices.size()) {
            throw new IndexOutOfBoundsException("Índice de vértice inválido.");
        }
        return vertices.get(indice);
    }

    /**
     * Lista os vértices diretamente conectados a partir de um vértice.
     */
    public List<Vertice> getVizinhos(String nome) {
        int linha = getIndiceVertice(nome);

        if (linha == -1) {
            throw new IllegalArgumentException("Vértice não encontrado: " + nome);
        }

        List<Vertice> vizinhos = new ArrayList<>();

        for (int coluna = 0; coluna < vertices.size(); coluna++) {
            if (matrizAdjacencia[linha][coluna] > 0) {
                vizinhos.add(vertices.get(coluna));
            }
        }
        return vizinhos;
    }

    /**
     * Retorna o peso da aresta entre dois índices de vértice.
     */
    public int getPesoAresta(int indiceOrigem, int indiceDestino) {
        if (indiceOrigem < 0 || indiceDestino < 0 || indiceOrigem >= vertices.size() || indiceDestino >= vertices.size()) {
            throw new IndexOutOfBoundsException("Índice de aresta inválido.");
        }
        return matrizAdjacencia[indiceOrigem][indiceDestino];
    }

    /**
     * Retorna o peso da aresta entre dois vértices pelos seus nomes.
     */
    public int getPesoAresta(String origem, String destino) {
        int indiceOrigem = getIndiceVertice(origem);
        int indiceDestino = getIndiceVertice(destino);

        if (indiceOrigem == -1 || indiceDestino == -1) {
            return 0;
        }
        return matrizAdjacencia[indiceOrigem][indiceDestino];
    }

    /**
     * Retorna a lista de vértices do grafo.
     */
    public List<Vertice> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    /**
     * Retorna a lista de arestas cadastradas no grafo.
     */
    public List<Aresta> getArestas() {
        return Collections.unmodifiableList(arestas);
    }

    /**
     * Retorna uma cópia da matriz de adjacência atual.
     */
    public int[][] getMatrizAdjacencia() {
        int[][] copia = new int[vertices.size()][vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                copia[i][j] = matrizAdjacencia[i][j];
            }
        }
        return copia;
    }

    /**
     * Indica se o grafo é direcionado.
     */
    public boolean isDirecionado() {
        return direcionado;
    }

    /**
     * Retorna a quantidade de vértices atualmente adicionados.
     */
    public int quantidadeVertices() {
        return vertices.size();
    }

    /**
     * Forma um texto legível com os vizinhos e pesos de cada vértice.
     */
    public String imprimirGrafo() {
        StringBuilder texto = new StringBuilder();

        if (vertices.isEmpty()) {
            return "O grafo ainda está vazio. Adicione vértices e arestas para começar.";
        }

        for (Vertice vertice : vertices) {
            texto.append(vertice.getNome()).append(" -> ");
            List<Vertice> vizinhos = getVizinhos(vertice.getNome());

            if (vizinhos.isEmpty()) {
                texto.append("sem vizinhos");
            } else {
                for (int i = 0; i < vizinhos.size(); i++) {
                    Vertice vizinho = vizinhos.get(i);
                    texto.append(vizinho.getNome());
                    int peso = getPesoAresta(vertice.getNome(), vizinho.getNome());
                    texto.append("(").append(peso).append(")");
                    if (i < vizinhos.size() - 1) {
                        texto.append(", ");
                    }
                }
            }
            texto.append("\n");
        }

        return texto.toString();
    }

    /**
     * Cria uma representação em texto da matriz de adjacência.
     */
    public String imprimirMatriz() {
        StringBuilder texto = new StringBuilder();

        if (vertices.isEmpty()) {
            return "A matriz está vazia porque ainda não há vértices.";
        }

        texto.append("Matriz de adjacência:\n\n     ");
        for (Vertice vertice : vertices) {
            texto.append(String.format("%5s", vertice.getNome()));
        }
        texto.append("\n");

        for (int i = 0; i < vertices.size(); i++) {
            texto.append(String.format("%5s", vertices.get(i).getNome()));
            for (int j = 0; j < vertices.size(); j++) {
                texto.append(String.format("%5d", matrizAdjacencia[i][j]));
            }
            texto.append("\n");
        }

        return texto.toString();
    }
}
