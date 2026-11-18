package br.com.territorypredictive.sensor;

/** Sensor que mede quantos atendimentos a UBS realiza por hora. */
public class SensorAtendimentos implements Sensor {

    private static final double MIN = 0;
    private static final double MAX = 200;
    private double ultimoValor = 25; // valor inicial
    private boolean funcionando = true;

    @Override
    public double lerValor() {
        if (!funcionando) {
            return -1; // leitura inválida: o sensor está com falha
        }
        // Passeio aleatório com leve tendência de alta (simula pico de procura)
        double variacao = (Math.random() - 0.35) * 8;
        ultimoValor = Math.max(MIN, Math.min(MAX, ultimoValor + variacao));
        return Math.round(ultimoValor * 10) / 10.0;
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
        return "Atendimentos";
    }

    @Override
    public String getUnidade() {
        return "atendimentos/hora";
    }
}