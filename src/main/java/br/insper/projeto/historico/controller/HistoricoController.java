package br.insper.projeto.historico.controller;

import br.insper.projeto.historico.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/historico")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<?> adicionarAoHistorico(@RequestHeader("Authorization") String jwtToken, @RequestParam String idFilme) {
        var h = historicoService.adicionarAoHistorico(jwtToken, idFilme);
        return ResponseEntity.status(HttpStatus.CREATED).body(h);
    }

    @GetMapping
    public ResponseEntity<?> listarHistorico(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) LocalDateTime data,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String genero) {

        var historico = historicoService.listarHistorico(jwtToken, data, titulo, genero);
        return ResponseEntity.ok(historico);
    }


    @GetMapping("/resumo")
    public ResponseEntity<?> gerarResumoUsuario(@RequestHeader("Authorization") String jwtToken) {
        var resumo = historicoService.gerarResumoUsuario(jwtToken);
        return ResponseEntity.ok(resumo);
    }

}
