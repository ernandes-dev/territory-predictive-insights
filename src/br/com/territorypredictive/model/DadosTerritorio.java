package br.com.territorypredictive.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Dados da UBS e limiares de alerta, protegidos por código de acesso.
 * Todos os atributos são privados; qualquer alteração passa por validação.
 */
public class DadosTerritorio {

    private String regiao;
    private int populacao;
    private final String codigoAcesso;                         // sem getter, de propósito
    private final HashMap<String, double[]> limiares = new HashMap<>();   // {atenção, alerta, crítico}
    private final HashMap<String, Double> maximoPorSensor = new HashMap<>();

    public DadosTerritorio(String regiao, int populacao, String codigoAcesso) {
        if (!regiaoValida(regiao)) {
            throw new IllegalArgumentException("Região inválida: não pode ser vazia.");
        }
        if (!populacaoValida(populacao)) {
            throw new IllegalArgumentException("População inválida: deve ser maior que zero.");
        }
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new IllegalArgumentException("Código de acesso inválido: não pode ser vazio.");
        }
        this.regiao = regiao.trim();
        this.populacao = populacao;
        this.codigoAcesso = codigoAcesso;

        // Limiares iniciais e valor máximo de cada sensor (as chaves são o getTipo() dos sensores)
        limiares.put("Atendimentos", new double[] {50, 80, 110});
        maximoPorSensor.put("Atendimentos", 200.0);

        limiares.put("Fila", new double[] {30, 60, 100});
        maximoPorSensor.put("Fila", 200.0);

        limiares.put("Estoque", new double[] {60, 80, 95});
        maximoPorSensor.put("Estoque", 100.0);
    }

    // ---------- Região ----------
    public String getRegiao() {
        return regiao;
    }

    public boolean setRegiao(String novaRegiao) {
        if (!regiaoValida(novaRegiao)) {
            System.out.println("Região rejeitada: não pode ser vazia. Mantida: " + regiao);
            return false;
        }
        this.regiao = novaRegiao.trim();
        return true;
    }

    // ---------- População ----------
    public int getPopulacao() {
        return populacao;
    }

    public boolean setPopulacao(int novaPopulacao) {
        if (!populacaoValida(novaPopulacao)) {
            System.out.println("População rejeitada (" + novaPopulacao
                    + "): deve ser maior que zero. Mantida: " + populacao);
            return false;
        }
        this.populacao = novaPopulacao;
        return true;
    }

    // ---------- Limiares ----------
    /** Devolve uma CÓPIA {atenção, alerta, crítico}, ou null se o sensor não existir. */
    public double[] getLimiares(String tipoSensor) {
        double[] atual = limiares.get(tipoSensor);
        if (atual == null) {
            return null;
        }
        return atual.clone();
    }

    /** Lista os tipos de sensor que têm limiares cadastrados. */
    public ArrayList<String> getTiposMonitorados() {
        return new ArrayList<>(limiares.keySet());
    }

    public boolean alterarLimiares(String codigo, String tipoSensor,
                                   double atencao, double alerta, double critico) {
        // 1) Código de acesso (equals de String conhecida: não quebra se 'codigo' for null)
        if (!codigoAcesso.equals(codigo)) {
            System.out.println("Acesso negado: código incorreto. Limiares mantidos.");
            return false;
        }
        // 2) Sensor existe?
        if (!limiares.containsKey(tipoSensor)) {
            System.out.println("Sensor desconhecido: \"" + tipoSensor + "\". Limiares mantidos.");
            return false;
        }
        // 3) Ordem dos níveis
        if (!(atencao > 0 && atencao < alerta && alerta < critico)) {
            System.out.println("Limiares inválidos: use 0 < atenção < alerta < crítico. Valores mantidos.");
            return false;
        }
        // 4) Faixa do sensor
        double maximo = maximoPorSensor.get(tipoSensor);
        if (critico > maximo) {
            System.out.println("Limiar crítico fora da faixa: máximo para " + tipoSensor
                    + " é " + maximo + ". Valores mantidos.");
            return false;
        }
        limiares.put(tipoSensor, new double[] {atencao, alerta, critico});
        System.out.println("Limiares de " + tipoSensor + " atualizados.");
        return true;
    }

    // ---------- Resumo ----------
    public String resumo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Região: ").append(regiao)
          .append(" | População: ").append(populacao).append("\n");
        for (String tipo : limiares.keySet()) {
            double[] l = limiares.get(tipo);
            sb.append("  ").append(tipo)
              .append(" -> atenção: ").append(l[0])
              .append(", alerta: ").append(l[1])
              .append(", crítico: ").append(l[2]).append("\n");
        }
        return sb.toString();
    }

    // ---------- Validações internas (privadas) ----------
    private boolean regiaoValida(String r) {
        return r != null && !r.isBlank();
    }

    private boolean populacaoValida(int p) {
        return p > 0;
    }
}