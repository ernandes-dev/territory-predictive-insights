package br.com.territorypredictive.sensor;

/** Sensor que mede o % do estoque semanal de medicamentos já consumido. */
public class SensorEstoque implements Sensor {

    private static final double MIN = 0;
    private static final double MAX = 100;
    private double ultimoValor = 40;
    private boolean funcionando = true;

    @Override
    public double lerValor() {
        if (!funcionando) {
            return -1; // leitura inválida: o sensor está com falha
        }
        double variacao = (Math.random() - 0.30) * 6;
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
        return "Estoque";
    }

    @Override
    public String getUnidade() {
        return "% do estoque consumido";
    }
}
