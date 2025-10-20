package exemplarservice.service;

import exemplarservice.domain.dto.AnimalDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnimalApiService {
    private final RestTemplate rest;
    public AnimalApiService(RestTemplate rest) {
        this.rest = rest;
    }

    public AnimalDTO fetchById(Long id) {
        return rest.getForObject(
                "http://localhost:8081/api/animal/{id}",
                AnimalDTO.class,
                id
        );
    }
}
