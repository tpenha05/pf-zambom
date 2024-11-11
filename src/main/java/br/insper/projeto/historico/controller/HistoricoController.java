package br.insper.projeto.historico.controller;

import br.insper.projeto.historico.dto.CompraDTO;
import br.insper.projeto.historico.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/historico")
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<?> adicionarAoHistorico(@RequestHeader("Authorization") String jwtToken, @RequestBody CompraDTO compraDTO) {
        var h = historicoService.adicionarCompra(jwtToken, compraDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(h);
    }

    @GetMapping
    public ResponseEntity<?> listarHistorico(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo) {

        var historico = historicoService.listarHistorico(jwtToken, ano, marca, modelo);
        return ResponseEntity.ok(historico);
    }



}
