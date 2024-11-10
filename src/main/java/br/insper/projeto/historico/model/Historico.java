package br.insper.projeto.historico.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "historico")
@Getter
@Setter
public class Historico {
    @MongoId
    private String id;

    private String email;

    private String filmeId;

    private LocalDateTime dataHistorico;

    private Integer tempoAssistido;

    private String genero;


}
