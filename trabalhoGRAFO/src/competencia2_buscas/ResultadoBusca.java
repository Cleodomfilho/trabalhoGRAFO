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

    public ResultadoBusca(List<String> ordemVisitada, List<String> caminho) {
        this(ordemVisitada, caminho, new ArrayList<>());
    }

    public ResultadoBusca(List<String> ordemVisitada, List<String> caminho, List<String> arestasAnimacao) {
        this.ordemVisitada = new ArrayList<>(ordemVisitada);
        this.caminho = new ArrayList<>(caminho);
        this.arestasAnimacao = new ArrayList<>(arestasAnimacao);
    }

    public List<String> getOrdemVisitada() {
        return Collections.unmodifiableList(ordemVisitada);
    }

    public List<String> getCaminho() {
        return Collections.unmodifiableList(caminho);
    }

    public List<String> getArestasAnimacao() {
        return Collections.unmodifiableList(arestasAnimacao);
    }

    public String getArestaAnimacaoDoPasso(int indice) {
        if (indice < 0 || indice >= arestasAnimacao.size()) {
            return "";
        }
        return arestasAnimacao.get(indice);
    }

    public boolean encontrouCaminho() {
        return !caminho.isEmpty();
    }
}
