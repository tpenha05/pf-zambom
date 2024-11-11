package br.insper.projeto.historico.dto;

public class PlanoUsuarioDTO {
    private String nome;
    private String email;
    private String cpf;

    private String password;
    private String papel;

    public PlanoUsuarioDTO() {
    }

    public PlanoUsuarioDTO(String nome, String email, String cpf, String password, String papel) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.papel = papel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }
}

