package competencia2_buscas;

/*
 * Competência 2 do PDF: Implementação das buscas BFS e DFS.
 * Aqui ficam os algoritmos e o objeto que guarda o resultado da busca.
 */

import competencia1_grafo.Grafo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Classe responsável pelos algoritmos de busca em grafos.
 * A BFS usa fila e a DFS usa recursão.
 */
public class Busca {

    /**
     * Faz a busca em largura (BFS) do nó de origem até o destino.
     */
    public static ResultadoBusca buscarEmLargura(Grafo grafo, String origem, String destino) {
        validarEntrada(grafo, origem, destino);

        int indiceOrigem = grafo.getIndiceVertice(origem);
        int indiceDestino = grafo.getIndiceVertice(destino);

        boolean[] visitados = new boolean[grafo.quantidadeVertices()];
        int[] anterior = new int[grafo.quantidadeVertices()];
        Arrays.fill(anterior, -1);

        List<String> ordemVisitada = new ArrayList<>();
        Queue<Integer> fila = new LinkedList<>();

        visitados[indiceOrigem] = true;
        fila.add(indiceOrigem);

        while (!fila.isEmpty()) {
            int atual = fila.poll();
            ordemVisitada.add(grafo.getVertice(atual).getNome());

            if (atual == indiceDestino) {
                break;
            }

            for (int vizinho = 0; vizinho < grafo.quantidadeVertices(); vizinho++) {
                boolean existeAresta = grafo.getPesoAresta(atual, vizinho) > 0;

                if (existeAresta && !visitados[vizinho]) {
                    visitados[vizinho] = true;
                    anterior[vizinho] = atual;
                    fila.add(vizinho);
                }
            }
        }

        List<String> caminho = new ArrayList<>();
        if (visitados[indiceDestino]) {
            caminho = reconstruirCaminho(grafo, anterior, indiceOrigem, indiceDestino);
        }

        return new ResultadoBusca(ordemVisitada, caminho, montarArestasDaAnimacao(grafo, anterior, ordemVisitada, indiceOrigem));
    }

    /**
     * Faz a busca em profundidade (DFS) do nó de origem até o destino.
     */
    public static ResultadoBusca buscarEmProfundidade(Grafo grafo, String origem, String destino) {
        validarEntrada(grafo, origem, destino);

        int indiceOrigem = grafo.getIndiceVertice(origem);
        int indiceDestino = grafo.getIndiceVertice(destino);

        boolean[] visitados = new boolean[grafo.quantidadeVertices()];
        int[] anterior = new int[grafo.quantidadeVertices()];
        Arrays.fill(anterior, -1);

        List<String> ordemVisitada = new ArrayList<>();
        boolean encontrou = dfsRecursivo(grafo, indiceOrigem, indiceDestino, visitados, anterior, ordemVisitada);

        List<String> caminho = new ArrayList<>();
        if (encontrou) {
            caminho = reconstruirCaminho(grafo, anterior, indiceOrigem, indiceDestino);
        }

        return new ResultadoBusca(ordemVisitada, caminho, montarArestasDaAnimacao(grafo, anterior, ordemVisitada, indiceOrigem));
    }

    /**
     * Função recursiva que explora o grafo em profundidade.
     */
    private static boolean dfsRecursivo(Grafo grafo, int atual, int destino, boolean[] visitados, int[] anterior, List<String> ordemVisitada) {
        visitados[atual] = true;
        ordemVisitada.add(grafo.getVertice(atual).getNome());

        if (atual == destino) {
            return true;
        }

        for (int vizinho = 0; vizinho < grafo.quantidadeVertices(); vizinho++) {
            boolean existeAresta = grafo.getPesoAresta(atual, vizinho) > 0;

            if (existeAresta && !visitados[vizinho]) {
                anterior[vizinho] = atual;

                if (dfsRecursivo(grafo, vizinho, destino, visitados, anterior, ordemVisitada)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Monta a lista de arestas usada para animar os passos da busca.
     */
    private static List<String> montarArestasDaAnimacao(Grafo grafo, int[] anterior, List<String> ordemVisitada, int indiceOrigem) {
        List<String> arestas = new ArrayList<>();

        for (String nomeVertice : ordemVisitada) {
            int indiceVertice = grafo.getIndiceVertice(nomeVertice);

            if (indiceVertice == indiceOrigem || indiceVertice < 0) {
                arestas.add("");
                continue;
            }

            int indiceAnterior = anterior[indiceVertice];
            if (indiceAnterior == -1) {
                arestas.add("");
            } else {
                String origem = grafo.getVertice(indiceAnterior).getNome();
                String destino = grafo.getVertice(indiceVertice).getNome();
                arestas.add(origem + "->" + destino);
            }
        }

        return arestas;
    }

    /**
     * Reconstrói o caminho final a partir do vetor de antecessores.
     */
    private static List<String> reconstruirCaminho(Grafo grafo, int[] anterior, int origem, int destino) {
        LinkedList<String> caminho = new LinkedList<>();
        int atual = destino;

        while (atual != -1) {
            caminho.addFirst(grafo.getVertice(atual).getNome());

            if (atual == origem) {
                break;
            }
            atual = anterior[atual];
        }

        return caminho;
    }

    /**
     * Verifica se os dados da busca são válidos antes de executar o algoritmo.
     */
    private static void validarEntrada(Grafo grafo, String origem, String destino) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo não pode ser nulo.");
        }
        if (grafo.quantidadeVertices() == 0) {
            throw new IllegalArgumentException("O grafo está vazio. Adicione vértices antes de buscar.");
        }
        if (origem == null || origem.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o vértice de origem.");
        }
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o vértice de destino.");
        }
        if (!grafo.existeVertice(origem)) {
            throw new IllegalArgumentException("O vértice de origem não existe: " + origem);
        }
        if (!grafo.existeVertice(destino)) {
            throw new IllegalArgumentException("O vértice de destino não existe: " + destino);
        }
    }

    /**
     * Converte o caminho encontrado em texto legível para exibição.
     */
    public static String formatarCaminho(List<String> caminho) {
        if (caminho == null || caminho.isEmpty()) {
            return "Não existe caminho entre os vértices escolhidos.";
        }
        return String.join(" -> ", caminho);
    }

    /**
     * Converte a ordem de visitação em texto legível para exibição.
     */
    public static String formatarOrdemVisitada(List<String> ordemVisitada) {
        if (ordemVisitada == null || ordemVisitada.isEmpty()) {
            return "Nenhum vértice foi visitado.";
        }
        return String.join(" -> ", ordemVisitada);
    }
}
