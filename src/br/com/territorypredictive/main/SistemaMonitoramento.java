package br.com.territorypredictive.main;

import br.com.territorypredictive.model.DadosTerritorio;
import br.com.territorypredictive.model.PainelInformativoUBS;
import br.com.territorypredictive.sensor.Sensor;
import br.com.territorypredictive.sensor.SensorAtendimentos;
import br.com.territorypredictive.sensor.SensorEstoque;
import br.com.territorypredictive.sensor.SensorFila;

// Teste provisório, depois vai ser trocado pelo menu
public class SistemaMonitoramento {

    public static void main(String[] args) {

        // Painel
        System.out.println("Painel");
        PainelInformativoUBS painel = new PainelInformativoUBS(1, "Painel Recepção", "Zona Leste");
        System.out.println(painel.verificarStatus());
        painel.ativar();
        painel.setMensagemAtual("Fila estimada: 20 min");
        painel.setMensagemAtual("   ");
        System.out.println(painel.verificarStatus());
        painel.desativar();
        System.out.println(painel.verificarStatus());

        // Dados do território
        System.out.println("\nDados do território");
        DadosTerritorio dados = new DadosTerritorio("Zona Leste", 45000, "ubs2026");
        dados.setPopulacao(-10);
        dados.setRegiao("   ");
        dados.alterarLimiares("errado", "Fila", 20, 40, 80);
        dados.alterarLimiares("ubs2026", "Fila", 50, 40, 80);
        dados.alterarLimiares("ubs2026", "Estoque", 50, 80, 150);
        dados.alterarLimiares("ubs2026", "Fila", 20, 40, 80);
        System.out.println(dados.resumo());

        double[] copia = dados.getLimiares("Fila");
        copia[0] = 999;
        System.out.println("Fila depois de mexer na cópia: " + dados.getLimiares("Fila")[0]);

        // Sensores
        System.out.println("\nSensores");
        Sensor[] sensores = { new SensorAtendimentos(), new SensorFila(), new SensorEstoque() };

        for (int i = 1; i <= 5; i++) {
            System.out.println("--- Leitura " + i + " ---");
            for (Sensor s : sensores) {
                System.out.println(s.getTipo() + ": " + s.lerValor() + " " + s.getUnidade()
                        + " | funcionando: " + s.verificarFuncionamento());
            }
        }
    }
}