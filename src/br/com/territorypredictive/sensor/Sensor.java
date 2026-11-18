package br.com.territorypredictive.sensor;

public interface Sensor {
    
    double lerValor();

    boolean verificarFuncionamento();

    String getTipo();

    String getUnidade();
}