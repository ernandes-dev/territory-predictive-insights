package br.com.territorypredictive.model;

public abstract class ComponenteTerritorial {

    private int id;
    private String nome;
    private String regiao;
    private boolean status;

    protected ComponenteTerritorial(int id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = false; // todo componente nasce desativado
    }

    public void ativar() {
        if (status) {
            System.out.println("[" + nome + "] já está ativo.");
            return;
        }
        this.status = true;
        System.out.println("[" + nome + "] ativado.");
    }

    public void desativar() {
        if (!status) {
            System.out.println("[" + nome + "] já está inativo.");
            return;
        }
        this.status = false;
        System.out.println("[" + nome + "] desativado.");
    }

    public boolean isAtivo() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public abstract String verificarStatus();
}