package br.insper.projeto.historico.controller;

import br.insper.projeto.historico.model.Feedback;
import br.insper.projeto.historico.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<Feedback> enviarFeedback(
            @RequestHeader("Authorization") String token,
            @RequestBody Feedback feedback) {

        Feedback novoFeedback = historicoService.enviarFeedback(token, feedback);
        return new ResponseEntity<>(novoFeedback, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> listarFeedbacks(@RequestHeader("Authorization") String token) {
        List<Feedback> feedbacks = historicoService.listarFeedbacks(token);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> consultarFeedbackPorId(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {

        Optional<Feedback> feedback = historicoService.consultarFeedbackPorId(token, id);
        return feedback.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirFeedback(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {

        historicoService.excluirFeedback(token, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
