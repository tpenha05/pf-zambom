package br.insper.projeto.historico.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CatalogoDTO {
    private String id;
    private String titulo;
    private String descricao;
    private String genero;
    private Integer ano;
    private String classificacao;
    private ArrayList<String> diretores;
    private ArrayList<String> atores;

    private Integer tempoFilme;
}
