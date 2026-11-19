package br.com.territorypredictive.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import br.com.territorypredictive.alerta.Alerta;
import br.com.territorypredictive.alerta.AvaliadorAlertas;
import br.com.territorypredictive.alerta.NivelAlerta;
import br.com.territorypredictive.model.DadosTerritorio;
import br.com.territorypredictive.model.PainelInformativoUBS;
import br.com.territorypredictive.sensor.Sensor;
import br.com.territorypredictive.sensor.SensorAtendimentos;
import br.com.territorypredictive.sensor.SensorEstoque;
import br.com.territorypredictive.sensor.SensorFila;
import br.com.territorypredictive.previsao.EstrategiaPrevisao;
import br.com.territorypredictive.previsao.PrevisaoComparacao;
import br.com.territorypredictive.previsao.PrevisaoMediaMovel;


 // Menu
public class SistemaMonitoramento {

    private static final int LEITURAS_INICIAIS = 6;

    private final Scanner entrada = new Scanner(System.in);
    private final DadosTerritorio dados = new DadosTerritorio("Zona Leste", 45000, "ubs2026");
    private final PainelInformativoUBS painel = new PainelInformativoUBS(1, "Painel Recepção", "Zona Leste");
    private final Sensor[] sensores = { new SensorAtendimentos(), new SensorFila(), new SensorEstoque() };
    private final EstrategiaPrevisao[] estrategias = { new PrevisaoMediaMovel(5), new PrevisaoComparacao(3) };
    private final HashMap<String, ArrayList<Double>> historico = new HashMap<>();

    public static void main(String[] args) {
        new SistemaMonitoramento().executar();
    }

    public SistemaMonitoramento() {
        for (Sensor s : sensores) {
            historico.put(s.getTipo(), new ArrayList<>());
        }
        painel.ativar();
        painel.setMensagemAtual("Monitoramento da UBS em andamento.");
        for (int i = 0; i < LEITURAS_INICIAIS; i++) {
            for (Sensor s : sensores) {
                lerEGuardar(s);
            }
        }
    }

    // ------------------------------------------------------------------ loop principal
    public void executar() {
        System.out.println("=== Territory Predictive Insights - UBS " + dados.getRegiao() + " ===");
        System.out.println(LEITURAS_INICIAIS + " leituras iniciais carregadas por sensor.");
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            System.out.println();
            switch (opcao) {
                case 1: verificarSensores(); break;
                case 2: rodarPrevisoesEAlertas(); break;
                case 3: gerenciarDados(); break;
                case 4: simularLeituras(); break;
                case 5: exibirStatusCompleto(); break;
                case 6: alternarFalhaDeSensor(); break;
                case 7: simular24Horas(); break;
                case 0: System.out.println("Encerrando o sistema."); break;
                default: System.out.println("Opção inválida. Escolha um número do menu.");
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("-------- MENU --------");
        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Rodar previsões e alertas");
        System.out.println("3 - Gerenciar dados do território");
        System.out.println("4 - Simular próximas leituras");
        System.out.println("5 - Exibir status completo");
        System.out.println("6 - Simular falha ou reparo de um sensor");
        System.out.println("7 - Simular 24 horas (bônus)");
        System.out.println("0 - Sair");
    }

    // ------------------------------------------------------------------ 1. sensores
    private void verificarSensores() {
        for (Sensor s : sensores) {
            Double valor = lerEGuardar(s);
            if (valor == null) {
                System.out.println(s.getTipo() + ": SENSOR COM FALHA - sem leitura (não entra no histórico).");
                continue;
            }
            Alerta alerta = AvaliadorAlertas.avaliar(s.getTipo(), s.getUnidade(), "Leitura atual",
                    valor, dados.getLimiares(s.getTipo()));
            System.out.println(alerta.explicar());
        }
    }

    /** Lê o sensor e guarda no histórico. Sensor com falha não gera leitura (devolve null). */
    private Double lerEGuardar(Sensor s) {
        if (!s.verificarFuncionamento()) {
            return null;
        }
        double valor = s.lerValor();
        historico.get(s.getTipo()).add(valor);
        return valor;
    }

    // ------------------------------------------------------------------ 2. previsões e alertas
    private void rodarPrevisoesEAlertas() {
        NivelAlerta pior = NivelAlerta.NORMAL;

        for (Sensor s : sensores) {
            ArrayList<Double> leituras = historico.get(s.getTipo());
            System.out.println("--- " + s.getTipo() + " (" + leituras.size() + " leituras) ---");

            for (EstrategiaPrevisao e : estrategias) {
                if (!e.temLeiturasSuficientes(leituras)) {
                    System.out.println("  [" + e.getNome() + "] sem previsão: " + leituras.size()
                            + " leituras, mínimo " + e.getMinimoLeituras() + ".");
                    continue;
                }
                double previsto = e.prever(leituras);
                System.out.println("  " + e.explicar(previsto, s.getUnidade()));

                Alerta alerta = AvaliadorAlertas.avaliar(s.getTipo(), s.getUnidade(), e.getNome(),
                        previsto, dados.getLimiares(s.getTipo()));
                System.out.println("  " + alerta.explicar());

                if (alerta.getNivel().compareTo(pior) > 0) {
                    pior = alerta.getNivel();
                }
            }
        }
        atualizarPainel(pior);
    }

    private void atualizarPainel(NivelAlerta pior) {
        String mensagem;
        if (pior.compareTo(NivelAlerta.ALERTA) >= 0) {
            mensagem = "Alta demanda prevista. Casos não urgentes: considere voltar em outro horário.";
        } else if (pior == NivelAlerta.ATENCAO) {
            mensagem = "Demanda em elevação. Atendimento segue normal.";
        } else {
            mensagem = "Atendimento normal.";
        }
        painel.setMensagemAtual(mensagem);
        System.out.println("\nPainel da recepção: " + painel.getMensagemAtual());
    }

    // ------------------------------------------------------------------ 3. dados do território
    private void gerenciarDados() {
        int opcao;
        do {
            System.out.println("\n--- Dados do território ---");
            System.out.println("1 - Ver dados e limiares");
            System.out.println("2 - Alterar população");
            System.out.println("3 - Alterar região");
            System.out.println("4 - Alterar limiares de um sensor (exige código de acesso)");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");
            switch (opcao) {
                case 1:
                    System.out.println(dados.resumo());
                    break;
                case 2:
                    if (dados.setPopulacao(lerInteiro("Nova população: "))) {
                        System.out.println("População atualizada.");
                    }
                    break;
                case 3:
                    if (dados.setRegiao(lerTexto("Nova região: "))) {
                        System.out.println("Região atualizada.");
                    }
                    break;
                case 4:
                    alterarLimiares();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void alterarLimiares() {
        String codigo = lerTexto("Código de acesso: ");
        System.out.println("Sensores: " + dados.getTiposMonitorados());
        String tipo = lerTexto("Sensor: ");
        double atencao = lerDouble("Novo limiar de ATENÇÃO: ");
        double alerta = lerDouble("Novo limiar de ALERTA: ");
        double critico = lerDouble("Novo limiar de CRÍTICO: ");
        dados.alterarLimiares(codigo, tipo, atencao, alerta, critico);
    }

    // ------------------------------------------------------------------ 4. simular leituras
    private void simularLeituras() {
        int n = lerInteiro("Quantas leituras simular por sensor (1 a 100)? ");
        if (n < 1 || n > 100) {
            System.out.println("Valor fora da faixa (1 a 100). Nada foi simulado.");
            return;
        }
        for (int i = 0; i < n; i++) {
            for (Sensor s : sensores) {
                lerEGuardar(s);
            }
        }
        System.out.println(n + " leituras simuladas por sensor (sensores com falha foram ignorados).\n");
        rodarPrevisoesEAlertas();
    }

    // ------------------------------------------------------------------ 5. status completo
    private void exibirStatusCompleto() {
        System.out.println("Painel: " + painel.verificarStatus());
        System.out.println();
        System.out.println(dados.resumo());
        for (Sensor s : sensores) {
            ArrayList<Double> leituras = historico.get(s.getTipo());
            String ultima = leituras.isEmpty() ? "sem leituras"
                    : leituras.get(leituras.size() - 1) + " " + s.getUnidade();
            System.out.println(s.getTipo() + ": " + (s.verificarFuncionamento() ? "funcionando" : "COM FALHA")
                    + " | leituras guardadas: " + leituras.size() + " | última: " + ultima);
        }
    }

    // ------------------------------------------------------------------ 6. falha de sensor
    private void alternarFalhaDeSensor() {
        for (int i = 0; i < sensores.length; i++) {
            System.out.println((i + 1) + " - " + sensores[i].getTipo()
                    + (sensores[i].verificarFuncionamento() ? " (funcionando)" : " (com falha)"));
        }
        int escolha = lerInteiro("Qual sensor? ");
        if (escolha < 1 || escolha > sensores.length) {
            System.out.println("Sensor inexistente.");
            return;
        }
        Sensor s = sensores[escolha - 1];
        if (s.verificarFuncionamento()) {
            s.simularFalha();
            System.out.println(s.getTipo() + ": falha simulada. Não haverá novas leituras até o reparo.");
        } else {
            s.reparar();
            System.out.println(s.getTipo() + ": sensor reparado.");
        }
    }

    // ------------------------------------------------------------------ 7. simulação de 24 horas (bônus)
    private void simular24Horas() {
        System.out.println("Simulação de 24 horas: uma leitura por hora de cada sensor.");
        System.out.println("Em cada hora vale o nível mais grave entre as duas previsões.\n");

        HashMap<String, NivelAlerta> maiorNivel = new HashMap<>();
        HashMap<String, ArrayList<String>> marcos = new HashMap<>();
        for (Sensor s : sensores) {
            maiorNivel.put(s.getTipo(), NivelAlerta.NORMAL);
            marcos.put(s.getTipo(), new ArrayList<>());
        }
        NivelAlerta pior = NivelAlerta.NORMAL;

        for (int hora = 0; hora < 24; hora++) {
            String rotuloHora = String.format("%02dh", hora);
            ArrayList<String> partes = new ArrayList<>();
            ArrayList<String> detalhes = new ArrayList<>();

            for (Sensor s : sensores) {
                String tipo = s.getTipo();
                if (lerEGuardar(s) == null) {
                    partes.add(tipo + ": FALHA");
                    continue;
                }

                // junta as duas previsões e fica com o nível mais grave
                ArrayList<Double> leituras = historico.get(tipo);
                Alerta maisGrave = null;
                for (EstrategiaPrevisao e : estrategias) {
                    if (!e.temLeiturasSuficientes(leituras)) {
                        continue;
                    }
                    Alerta alerta = AvaliadorAlertas.avaliar(tipo, s.getUnidade(), e.getNome(),
                            e.prever(leituras), dados.getLimiares(tipo));
                    if (maisGrave == null || alerta.getNivel().compareTo(maisGrave.getNivel()) > 0) {
                        maisGrave = alerta;
                    }
                }
                if (maisGrave == null) {
                    partes.add(tipo + ": sem previsão");
                    continue;
                }
                partes.add(tipo + ": " + maisGrave.getNivel().getRotulo());

                // quando o sensor sobe de nível pela primeira vez, explica o porquê
                if (maisGrave.getNivel().compareTo(maiorNivel.get(tipo)) > 0) {
                    maiorNivel.put(tipo, maisGrave.getNivel());
                    marcos.get(tipo).add(maisGrave.getNivel().getRotulo() + " às " + rotuloHora);
                    detalhes.add("      -> " + maisGrave.explicar());
                }
                if (maisGrave.getNivel().compareTo(pior) > 0) {
                    pior = maisGrave.getNivel();
                }
            }

            System.out.println(rotuloHora + " | " + String.join(" | ", partes));
            for (String detalhe : detalhes) {
                System.out.println(detalhe);
            }
        }

        System.out.println("\n--- Resumo das 24 horas ---");
        for (Sensor s : sensores) {
            ArrayList<String> m = marcos.get(s.getTipo());
            String texto;
            if (!m.isEmpty()) {
                texto = String.join(", ", m);
            } else if (!s.verificarFuncionamento()) {
                texto = "sensor com falha, sem leituras no período";
            } else {
                texto = "sem alerta no período";
            }
            System.out.println(s.getTipo() + ": " + texto);
        }
        atualizarPainel(pior);
    }

    // ------------------------------------------------------------------ leitura de entrada
    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem).trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private double lerDouble(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(mensagem).trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número (use ponto ou vírgula para decimais).");
            }
        }
    }
}