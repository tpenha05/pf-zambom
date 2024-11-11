package br.insper.projeto.historico.service;

import br.insper.projeto.historico.dto.CompraDTO;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

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
                PlanoUsuarioDTO planoUsuario = response.getBody();
                return planoUsuario;
            }
            else {
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



    public List<Historico> listarHistorico(String jwtToken, Integer ano, String marca, String modelo) {

        // Obtendo o email a partir do token JWT
        String email = achaUsuario(jwtToken) != null ? achaUsuario(jwtToken).getEmail() : null;

        if (email == null) {
            throw new IllegalArgumentException("Token JWT inválido ou usuário não encontrado.");
        }

        // Buscando histórico pelo email
        List<Historico> historico = historicoRepository.findByEmail(email);

        // Verifica se o histórico não é nulo para evitar NullPointerException
        if (historico == null) {
            return Collections.emptyList(); // Retorna uma lista vazia se nenhum histórico for encontrado
        }

        // Filtrando pelo ano, se fornecido
        if (ano != null) {
            historico.removeIf(h -> !h.getAno().equals(ano));
        }

        // Filtrando pelo modelo, se fornecido (corrigido para usar getModelo)
        if (modelo != null) {
            historico.removeIf(h -> !h.getModelo().equals(modelo));
        }

        // Filtrando pela marca, se fornecido
        if (marca != null) {
            historico.removeIf(h -> !h.getMarca().equals(marca));
        }

        return historico;
    }


    public Historico adicionarCompra(String jwtToken, CompraDTO compraDTO) {


        Historico compra = new Historico();

        compra.setEmail(achaUsuario(jwtToken).getEmail());
        compra.setAno(compraDTO.getAno());
        compra.setMarca(compraDTO.getMarca());
        compra.setComprador(achaUsuario(jwtToken).getNome());
        compra.setModelo(compraDTO.getModelo());

        historicoRepository.save(compra);
        return compra;
    }





}
