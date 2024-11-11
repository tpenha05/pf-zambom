package br.insper.projeto.common;

import br.insper.projeto.historico.dto.PlanoUsuarioDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private String token;

    public String login(String email, String password) {
        String loginUrl = "http://184.72.80.215/usuario/login";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(loginUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            this.token = response.getBody().get("token").toString();
        } else {
            throw new RuntimeException("Login failed: " + response.getStatusCode());
        }

        return token;
    }

    public boolean validateToken() {
        String validateUrl = "http://184.72.80.215/usuario/validate";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(validateUrl, HttpMethod.GET, request, Map.class);

        return response.getStatusCode().is2xxSuccessful();
    }

    public String getToken() {
        return this.token;
    }

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
}
