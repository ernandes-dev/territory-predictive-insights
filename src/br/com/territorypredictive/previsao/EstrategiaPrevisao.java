package br.com.territorypredictive.previsao;

import java.util.ArrayList;

/** Molde de toda forma de prever o próximo valor de um sensor a partir das leituras guardadas. */
public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da estratégia não pode ser vazio.");
        }
        if (minimoLeituras < 1) {
            throw new IllegalArgumentException("O mínimo de leituras deve ser pelo menos 1.");
        }
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    public String getNome() {
        return nome;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    public boolean temLeiturasSuficientes(ArrayList<Double> leituras) {
        return leituras != null && leituras.size() >= minimoLeituras;
    }

    /** Devolve o valor previsto, ou -1 se não houver leituras suficientes. */
    public abstract double prever(ArrayList<Double> leituras);

    /** Texto base do resultado. As subclasses complementam com o significado. */
    public String explicar(double previsto, String unidade) {
        return "[" + nome + "] Valor previsto: " + String.format("%.1f", previsto) + " " + unidade;
    }

    /** Média das leituras no intervalo [inicio, fim). */
    protected double media(ArrayList<Double> leituras, int inicio, int fim) {
        double soma = 0;
        for (int i = inicio; i < fim; i++) {
            soma += leituras.get(i);
        }
        return soma / (fim - inicio);
    }
}
