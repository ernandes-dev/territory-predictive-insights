package br.com.territorypredictive.sensor;

/** Sensor que mede quantas pessoas estão na fila de espera da UBS. */
public class SensorFila implements Sensor {

    private static final double MIN = 0;
    private static final double MAX = 200;
    private double ultimoValor = 15;
    private boolean funcionando = true;

    @Override
    public double lerValor() {
        if (!funcionando) {
            return -1; // leitura inválida: o sensor está com falha
        }
        double variacao = (Math.random() - 0.35) * 10;
        ultimoValor = Math.max(MIN, Math.min(MAX, ultimoValor + variacao));
        return Math.round(ultimoValor); // número inteiro de pessoas
    }

    public void simularFalha() {
        funcionando = false;
    }

    public void reparar() {
        funcionando = true;
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    @Override
    public String getTipo() {
        return "Fila";
    }

    @Override
    public String getUnidade() {
        return "pessoas na fila";
    }
}
