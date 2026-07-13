package competencia1_grafo;

/*
 * Competência 1 do PDF: Implementação do Grafo.
 * Aqui ficam as classes de modelo: Grafo, Vértice e Aresta.
 */

/**
 * Representa uma ligação entre dois vértices do grafo.
 * O peso pode ser usado como distância, custo ou valor da conexão.
 */
public class Aresta {

    private Vertice origem;
    private Vertice destino;
    private int peso;

    /**
     * Cria uma aresta entre dois vértices com o peso informado.
     */
    public Aresta(Vertice origem, Vertice destino, int peso) {
        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Origem e destino devem existir.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso da aresta deve ser maior que zero.");
        }
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    /**
     * Retorna o vértice de origem da aresta.
     */
    public Vertice getOrigem() {
        return origem;
    }

    /**
     * Retorna o vértice de destino da aresta.
     */
    public Vertice getDestino() {
        return destino;
    }

    /**
     * Retorna o peso associado a esta aresta.
     */
    public int getPeso() {
        return peso;
    }

    /**
     * Atualiza o peso da aresta.
     */
    public void setPeso(int peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("O peso da aresta deve ser maior que zero.");
        }
        this.peso = peso;
    }

    @Override
    public String toString() {
        return origem + " -> " + destino + " (" + peso + ")";
    }
}
