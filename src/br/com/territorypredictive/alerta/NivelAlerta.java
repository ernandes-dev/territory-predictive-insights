package br.com.territorypredictive.alerta;
 
/** Níveis de alerta, do mais leve ao mais grave. A ordem das constantes importa (compareTo). */
public enum NivelAlerta {
    NORMAL("NORMAL"),
    ATENCAO("ATENÇÃO"),
    ALERTA("ALERTA"),
    CRITICO("CRÍTICO");
 
    private final String rotulo;
 
    NivelAlerta(String rotulo) {
        this.rotulo = rotulo;
    }
 
    public String getRotulo() {
        return rotulo;
    }
}
 