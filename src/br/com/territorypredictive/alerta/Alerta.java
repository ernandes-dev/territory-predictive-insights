package br.com.territorypredictive.alerta;

/** Resultado da comparação de um valor (leitura ou previsão) com os limiares de um sensor. */
public class Alerta {

    private final String sensorTipo;
    private final String unidade;
    private final String origem;      // "Leitura atual", "Média Móvel", "Comparação de Períodos"
    private final double valor;
    private final double limite;      // limiar que foi atingido (ou o de ATENÇÃO, se NORMAL)
    private final NivelAlerta nivel;

    public Alerta(String sensorTipo, String unidade, String origem,
                  double valor, double limite, NivelAlerta nivel) {
        this.sensorTipo = sensorTipo;
        this.unidade = unidade;
        this.origem = origem;
        this.valor = valor;
        this.limite = limite;
        this.nivel = nivel;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    /** Texto para o console, com o porquê: qual sensor, qual valor e qual limite foi atingido. */
    public String explicar() {
        String base = sensorTipo + " (" + origem + "): "
                + String.format("%.1f", valor) + " " + unidade;
        if (nivel == NivelAlerta.NORMAL) {
            return "[NORMAL] " + base + " - abaixo do limite de ATENÇÃO ("
                    + String.format("%.1f", limite) + ").";
        }
        String destaque = "";
        if (nivel == NivelAlerta.ATENCAO) {
            destaque = "(!) ";
        } else if (nivel == NivelAlerta.ALERTA) {
            destaque = "(!!) ";
        } else if (nivel == NivelAlerta.CRITICO) {
            destaque = "(!!!) ";
        }
        return destaque + "[" + nivel.getRotulo() + "] " + base + " - atingiu o limite de "
                + nivel.getRotulo() + " (" + String.format("%.1f", limite) + ").";
    }
}