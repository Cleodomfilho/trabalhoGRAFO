# TrabalhoGrafos — Reorganizado pelas competências do trabalho

Projeto Java para o trabalho avaliativo **Buscas em Grafos: Implementação de BFS e DFS para Encontrar o Caminho entre Dois Nós**.

A organização abaixo segue diretamente os critérios de avaliação do PDF: implementação do grafo, implementação das buscas, interface e apresentação/domínio do conteúdo.

## Estrutura do projeto

```text
TrabalhoGrafos/
├── src/
│   └── br/ifma/grafos/
│       ├── Main.java
│       ├── competencia1_grafo/
│       │   ├── Grafo.java
│       │   ├── Vertice.java
│       │   └── Aresta.java
│       ├── competencia2_buscas/
│       │   ├── Busca.java
│       │   └── ResultadoBusca.java
│       └── competencia3_interface/
│           ├── ControleGrafo.java
│           └── PainelVisualizacaoGrafo.java
├── documentacao/
│   ├── 00_Mapa_Das_Competencias.md
│   ├── 01_Implementacao_Do_Grafo.md
│   ├── 02_Buscas_BFS_DFS.md
│   ├── 03_Interface.md
│   └── 04_Apresentacao.md
├── apresentacao/
│   ├── Roteiro_Apresentacao.md
│   └── Checklist_Dia_Apresentacao.md
├── prompts.txt
├── .gitignore
├── compilar.bat
├── executar.bat
└── README.md
```

## Como compilar pelo terminal

No Windows PowerShell ou CMD, entre na pasta `TrabalhoGrafos` e rode:

```bash
javac -encoding UTF-8 -d out src/br/ifma/grafos/Main.java src/br/ifma/grafos/competencia1_grafo/*.java src/br/ifma/grafos/competencia2_buscas/*.java src/br/ifma/grafos/competencia3_interface/*.java
java -cp out Main
```

Também deixei dois arquivos prontos:

```bash
compilar.bat
executar.bat
```

## Mapa rápido das competências

| Competência do PDF | Pasta principal | O que contém |
|---|---|---|
| 1. Implementação do Grafo | `competencia1_grafo` | Matriz de adjacência, vértices, arestas e validações |
| 2. Implementação das buscas | `competencia2_buscas` | BFS com fila, DFS com recursão, visitados, caminho e resultado |
| 3. Interface | `competencia3_interface` | Tela principal, entrada de dados, busca, desenho e animação do grafo |
| 4. Apresentação | `apresentacao` e `documentacao` | Roteiro, checklist e explicação para estudar |
| 5. Entrega/prompts | `prompts.txt` | Registro dos prompts usados com IA |

## Observação sobre o grafo

O projeto está configurado como **grafo não direcionado**. Isso significa que, ao adicionar uma aresta de A para B, a matriz também registra B para A. Essa escolha pode ser justificada dizendo que o exemplo usado representa caminhos entre pontos/cidades, onde normalmente é possível ir e voltar pela mesma conexão.
