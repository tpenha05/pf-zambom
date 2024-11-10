package br.insper.projeto.historico.service;

import br.insper.projeto.common.TokenUtils;
import br.insper.projeto.historico.dto.CatalogoDTO;
import br.insper.projeto.historico.dto.PlanoUsuarioDTO;

import br.insper.projeto.historico.model.Historico;
import br.insper.projeto.historico.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    private boolean usuarioTemPlanoAtivo(String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // URL simplificada
        String url = "http://54.91.81.157:8080/api/usuarios";

        try {
            ResponseEntity<PlanoUsuarioDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlanoUsuarioDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                PlanoUsuarioDTO planoUsuario = response.getBody();
                // Verifica se o nome é "N/A" para identificar usuários inativos
                return planoUsuario != null && !"N/A".equals(planoUsuario.getNome());
            } else {
                return false;
            }
        } catch (HttpClientErrorException e) {
            // Trate erros HTTP, como 404
            return false;
        } catch (Exception e) {
            // Trate outros erros
            throw new RuntimeException("Erro ao verificar plano do usuário", e);
        }
    }

    private CatalogoDTO obterFilmeDoCatalogo(String filmeId, String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // Se o serviço do catálogo não exigir autenticação, remova esta linha
        headers.set("Authorization", jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://3.81.36.34:8080/filmes/" + filmeId;

        try {
            ResponseEntity<CatalogoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    CatalogoDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return null;
            }

        } catch (HttpClientErrorException.NotFound e) {
            // Filme não encontrado no catálogo
            return null;
        } catch (Exception e) {
            // Tratar outros erros
            throw new RuntimeException("Erro ao obter filme do catálogo", e);
        }
    }

    // função que lista todos os filmes do histórico, podendo filtrar por data, título e gênero
    public List<Historico> listarHistorico(String jwtToken, LocalDateTime data, String titulo, String genero) {
//        if (!usuarioTemPlanoAtivo(jwtToken)) {
//            throw new RuntimeException("Usuário não tem plano ativo");
//        }

        String email = TokenUtils.getEmailFromToken(jwtToken);

        List<Historico> historico = historicoRepository.findByEmail(email);
        //ArrayList<Historico> response = new ArrayList<>();

        //Stream<Historico> s = response.stream();

        if (data != null) {
            historico.removeIf(h -> !h.getDataHistorico().equals(data));
        }

        if (titulo != null) {
            historico.removeIf(h -> !h.getFilmeId().equals(titulo));
        }
        if (genero != null) {
            historico.removeIf(h -> !h.getFilmeId().equals(genero));
        }

        return historico;
    }

    public Historico adicionarAoHistorico(String jwtToken, String filmeId) {
//        if (!usuarioTemPlanoAtivo(jwtToken)) {
//            throw new RuntimeException("Usuário não tem plano ativo");
//        }

        CatalogoDTO filme = obterFilmeDoCatalogo(filmeId, jwtToken);
        if (filme == null) {
            throw new RuntimeException("Filme não encontrado no catálogo");
        }

        Historico historico = new Historico();
        historico.setFilmeId(filmeId);
        historico.setEmail(TokenUtils.getEmailFromToken(jwtToken));
        historico.setDataHistorico(LocalDateTime.now());
        historico.setTempoAssistido(0); // não sei o tempo pausado, Integer pois é em minutos
        historico.setGenero(filme.getGenero());
        historicoRepository.save(historico);

        return historico;
    }

    public String gerarResumoUsuario(String jwtToken) {
//        if (!usuarioTemPlanoAtivo(jwtToken)) {
//            throw new RuntimeException("Usuário não tem plano ativo");
//        }

        String email = TokenUtils.getEmailFromToken(jwtToken);

        List<Historico> historico = historicoRepository.findByEmail(email);
        int tempoTotal = 0;

        Map<String, Integer> generoContagemMap = new HashMap<>();

        for (Historico h : historico) {
            tempoTotal += h.getTempoAssistido();

            String genero = h.getGenero();
            generoContagemMap.put(genero, generoContagemMap.getOrDefault(genero, 0) + 1);
        }

        // Ordenar os gêneros pelo número de filmes assistidos em ordem decrescente
        List<Map.Entry<String, Integer>> generosOrdenados = new ArrayList<>(generoContagemMap.entrySet());
        generosOrdenados.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        StringBuilder resumo = new StringBuilder();
        resumo.append("Tempo total de filmes assistidos: ").append(tempoTotal).append(" minutos\n");
        resumo.append("Gêneros mais assistidos: ");

        for (Map.Entry<String, Integer> entry : generosOrdenados) {
            resumo.append(entry.getKey()).append(" (").append(entry.getValue()).append(" filmes), ");
        }

        // Remover a última vírgula e espaço, se necessário
        if (!generoContagemMap.isEmpty()) {
            resumo.setLength(resumo.length() - 2);
        }

        return resumo.toString();
    }



}
