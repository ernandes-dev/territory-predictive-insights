package br.com.territorypredictive.previsao;

import java.util.ArrayList;

/** Previsão pela média das últimas N leituras (N = tamanho da janela). */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média Móvel", janela);   // precisa de pelo menos 'janela' leituras
        this.janela = janela;
    }

    public PrevisaoMediaMovel() {
        this(5);
    }

    public int getJanela() {
        return janela;
    }

    @Override
    public double prever(ArrayList<Double> leituras) {
        if (!temLeiturasSuficientes(leituras)) {
            return -1;
        }
        return media(leituras, leituras.size() - janela, leituras.size());
    }

    @Override
    public String explicar(double previsto, String unidade) {
        return super.explicar(previsto, unidade)
                + " | média das últimas " + janela + " leituras (suaviza oscilações bruscas)";
    }
}

