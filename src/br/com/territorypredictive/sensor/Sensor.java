package br.com.territorypredictive.sensor;

public interface Sensor {
    
    double lerValor();

    boolean verificarFuncionamento();

    String getTipo();

    String getUnidade();
    
    /** Simula uma falha: depois disso, verificarFuncionamento() devolve false. */
    void simularFalha();

    /** Conserta o sensor depois de uma falha simulada. */
    void reparar();
}
