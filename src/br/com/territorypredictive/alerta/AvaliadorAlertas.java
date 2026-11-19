package br.com.territorypredictive.alerta;

/** Compara um valor com os limiares {atenção, alerta, crítico} e devolve o nível correspondente. */
public class AvaliadorAlertas {

    private AvaliadorAlertas() {
        // classe utilitária: não se cria objeto
    }

    public static Alerta avaliar(String sensorTipo, String unidade, String origem,
                                 double valor, double[] limiares) {
        if (limiares == null || limiares.length != 3) {
            throw new IllegalArgumentException("Não há limiares cadastrados para o sensor: " + sensorTipo);
        }
        if (valor >= limiares[2]) {
            return new Alerta(sensorTipo, unidade, origem, valor, limiares[2], NivelAlerta.CRITICO);
        }
        if (valor >= limiares[1]) {
            return new Alerta(sensorTipo, unidade, origem, valor, limiares[1], NivelAlerta.ALERTA);
        }
        if (valor >= limiares[0]) {
            return new Alerta(sensorTipo, unidade, origem, valor, limiares[0], NivelAlerta.ATENCAO);
        }
        return new Alerta(sensorTipo, unidade, origem, valor, limiares[0], NivelAlerta.NORMAL);
    }
}