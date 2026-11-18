package br.com.territorypredictive.model;

/** Painel digital na recepção da UBS que exibe uma mensagem para a comunidade. */
public class PainelInformativoUBS extends ComponenteTerritorial {

    private String mensagemAtual;

    public PainelInformativoUBS(int id, String nome, String regiao) {
        super(id, nome, regiao);
        this.mensagemAtual = "Sem mensagem";
    }

    public String getMensagemAtual() {
        return mensagemAtual;
    }

    public void setMensagemAtual(String mensagemAtual) {
        if (mensagemAtual == null || mensagemAtual.isBlank()) {
            System.out.println("Mensagem inválida: não pode ser vazia. Mantida: \"" + this.mensagemAtual + "\"");
            return;
        }
        this.mensagemAtual = mensagemAtual.trim();
    }

    @Override
    public String verificarStatus() {
        return getNome() + " (" + getRegiao() + ") - "
                + (isAtivo() ? "ATIVO" : "INATIVO")
                + " | Mensagem: " + mensagemAtual;
    }
}