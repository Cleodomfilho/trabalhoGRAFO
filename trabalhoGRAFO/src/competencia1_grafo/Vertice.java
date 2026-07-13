package competencia1_grafo;

/*
 * Competência 1 do PDF: Implementação do Grafo.
 * Aqui ficam as classes de modelo: Grafo, Vértice e Aresta.
 */

import java.util.Objects;

/**
 * Representa um nó/vértice do grafo.
 * O nome é tratado em maiúsculo para evitar duplicidade como "A" e "a".
 */
public class Vertice {

    private String nome;

    /**
     * Cria um vértice com o nome informado.
     */
    public Vertice(String nome) {
        setNome(nome);
    }

    /**
     * Retorna o nome do vértice em maiúsculo.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ajusta o nome do vértice e normaliza para evitar duplicatas.
     */
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do vértice não pode ser vazio.");
        }
        this.nome = nome.trim().toUpperCase();
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vertice)) {
            return false;
        }
        Vertice outro = (Vertice) obj;
        return nome.equalsIgnoreCase(outro.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toUpperCase());
    }
}
