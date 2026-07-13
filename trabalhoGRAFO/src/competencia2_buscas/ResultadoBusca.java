package competencia2_buscas;

/*
 * Competência 2 do PDF: Implementação das buscas BFS e DFS.
 * Aqui ficam os algoritmos e o objeto que guarda o resultado da busca.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Guarda o resultado de uma busca.
 *
 * A ordem visitada é usada para mostrar a sequência de visitação.
 * O caminho é a resposta final entre origem e destino.
 * As arestas da animação indicam de qual vértice cada nó foi alcançado,
 * permitindo animar a busca com movimento entre os nós.
 */
public class ResultadoBusca {

    private List<String> ordemVisitada;
    private List<String> caminho;
    private List<String> arestasAnimacao;

    /**
     * Cria o resultado da busca com ordem visitada e caminho encontrado.
     */
    public ResultadoBusca(List<String> ordemVisitada, List<String> caminho) {
        this(ordemVisitada, caminho, new ArrayList<>());
    }

    /**
     * Cria o resultado da busca incluindo os passos da animação.
     */
    public ResultadoBusca(List<String> ordemVisitada, List<String> caminho, List<String> arestasAnimacao) {
        this.ordemVisitada = new ArrayList<>(ordemVisitada);
        this.caminho = new ArrayList<>(caminho);
        this.arestasAnimacao = new ArrayList<>(arestasAnimacao);
    }

    /**
     * Retorna os vértices visitados na ordem em que foram alcançados.
     */
    public List<String> getOrdemVisitada() {
        return Collections.unmodifiableList(ordemVisitada);
    }

    /**
     * Retorna o caminho final reconstruído entre origem e destino.
     */
    public List<String> getCaminho() {
        return Collections.unmodifiableList(caminho);
    }

    /**
     * Retorna a lista de arestas usadas para animar a busca.
     */
    public List<String> getArestasAnimacao() {
        return Collections.unmodifiableList(arestasAnimacao);
    }

    /**
     * Retorna a aresta usada em um passo específico da animação.
     */
    public String getArestaAnimacaoDoPasso(int indice) {
        if (indice < 0 || indice >= arestasAnimacao.size()) {
            return "";
        }
        return arestasAnimacao.get(indice);
    }

    /**
     * Indica se a busca encontrou um caminho entre os vértices.
     */
    public boolean encontrouCaminho() {
        return !caminho.isEmpty();
    }
}
