package br.insper.projeto.historico.service;

import br.insper.projeto.historico.dto.PlanoUsuarioDTO;
import br.insper.projeto.historico.model.Feedback;
import br.insper.projeto.historico.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    private PlanoUsuarioDTO achaUsuario(String jwtToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "http://184.72.80.215/usuario/validate";

        try {
            ResponseEntity<PlanoUsuarioDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlanoUsuarioDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Usuário não encontrado. Status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erro de cliente ao verificar o papel do usuário: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro de servidor ao verificar o papel do usuário: " + e.getStatusCode(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar papel do usuário", e);
        }
    }

    public Feedback enviarFeedback(String token, Feedback feedback) {
        PlanoUsuarioDTO usuario = achaUsuario(token);

        if (!"ADMIN".equals(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        String email = usuario.getEmail();
        feedback.setEmail(email);
        return historicoRepository.save(feedback);
    }

    public List<Feedback> listarFeedbacks(String token) {
        PlanoUsuarioDTO usuario = achaUsuario(token);

        if (!"ADMIN".equals(usuario.getPapel()) && !"DEVELOPER".equals(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        return historicoRepository.findAll();
    }

    public Optional<Feedback> consultarFeedbackPorId(String token, String id) {
        PlanoUsuarioDTO usuario = achaUsuario(token);

        if (!"ADMIN".equals(usuario.getPapel()) && !"DEVELOPER".equals(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        return historicoRepository.findById(id);
    }

    public void excluirFeedback(String token, String id) {
        PlanoUsuarioDTO usuario = achaUsuario(token);

        if (!"ADMIN".equals(usuario.getPapel())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }

        historicoRepository.deleteById(id);
    }
}
