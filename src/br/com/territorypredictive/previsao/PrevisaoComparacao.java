package br.com.territorypredictive.previsao;

import java.util.ArrayList;

/** Previsão comparando o período atual com o período anterior e projetando a mesma variação. */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private final int tamanhoPeriodo;
    private double ultimaVariacaoPercentual = 0;

    public PrevisaoComparacao(int tamanhoPeriodo) {
        super("Comparação de Períodos", tamanhoPeriodo * 2);  // precisa de dois períodos completos
        this.tamanhoPeriodo = tamanhoPeriodo;
    }

    public PrevisaoComparacao() {
        this(3);
    }

    public int getTamanhoPeriodo() {
        return tamanhoPeriodo;
    }

    public double getUltimaVariacaoPercentual() {
        return ultimaVariacaoPercentual;
    }

    @Override
    public double prever(ArrayList<Double> leituras) {
        if (!temLeiturasSuficientes(leituras)) {
            return -1;
        }
        int fim = leituras.size();
        double atual = media(leituras, fim - tamanhoPeriodo, fim);
        double anterior = media(leituras, fim - 2 * tamanhoPeriodo, fim - tamanhoPeriodo);
        double variacao = atual - anterior;

        ultimaVariacaoPercentual = (anterior == 0) ? 0 : (variacao / anterior) * 100;

        return Math.max(0, atual + variacao); // projeta a mesma variação para o próximo período
    }

    @Override
    public String explicar(double previsto, String unidade) {
        String tendencia = "estável";
        if (ultimaVariacaoPercentual > 5) {
            tendencia = "em alta";
        } else if (ultimaVariacaoPercentual < -5) {
            tendencia = "em queda";
        }
        return super.explicar(previsto, unidade)
                + " | período atual vs anterior (" + tamanhoPeriodo + " leituras cada): "
                + String.format("%.1f", ultimaVariacaoPercentual) + "% -> tendência " + tendencia;
    }
}


