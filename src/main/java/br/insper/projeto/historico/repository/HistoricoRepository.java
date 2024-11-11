package br.insper.projeto.historico.repository;

import br.insper.projeto.historico.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByEmail(String email);
}
