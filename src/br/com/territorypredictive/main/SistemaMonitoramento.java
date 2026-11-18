package br.com.territorypredictive.main;

import br.com.territorypredictive.model.DadosTerritorio;

public class SistemaMonitoramento {
    public static void main(String[] args) {
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 45000, "ubs2026");

        dados.setPopulacao(-10);        // rejeitada
        dados.setRegiao("   ");         // rejeitada
        dados.alterarLimiares("errado", "Fila", 20, 40, 80);   // código errado
        dados.alterarLimiares("ubs2026", "Fila", 50, 40, 80);  // fora de ordem
        dados.alterarLimiares("ubs2026", "Estoque", 50, 80, 150); // fora da faixa
        dados.alterarLimiares("ubs2026", "Fila", 20, 40, 80);  // válida

        System.out.println(dados.resumo());

        // A cópia não deixa alterar por fora:
        double[] copia = dados.getLimiares("Fila");
        copia[0] = 999;
        System.out.println("Fila depois de mexer na cópia: " + dados.getLimiares("Fila")[0]); // continua 20.0
    }
}