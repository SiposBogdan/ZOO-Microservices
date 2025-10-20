package animalservice.service.exporter;

import animalservice.domain.dto.AnimalDTO;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XmlExporter {
    private final XmlMapper mapper = new XmlMapper();

    public byte[] export(List<AnimalDTO> list) {
        try {
            // wrap in root element
            return mapper
                    .writer().withRootName("animals")
                    .writeValueAsBytes(list);
        } catch (Exception e) {
            throw new RuntimeException("XML export failed", e);
        }
    }
}
