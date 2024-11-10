package br.insper.projeto.historico.repository;

import br.insper.projeto.historico.model.Historico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRepository extends MongoRepository<Historico, Integer> {
    List<Historico> findByEmail(String email);

    List<Historico> findAllByEmail(String emailFromToken);
}
