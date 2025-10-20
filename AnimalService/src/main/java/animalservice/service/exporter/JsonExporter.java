package animalservice.service.exporter;

import animalservice.domain.dto.AnimalDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonExporter {
    private final ObjectMapper mapper = new ObjectMapper();

    public byte[] export(List<AnimalDTO> list) {
        try {
            return mapper.writeValueAsBytes(list);
        } catch (Exception e) {
            throw new RuntimeException("JSON export failed", e);
        }
    }
}
