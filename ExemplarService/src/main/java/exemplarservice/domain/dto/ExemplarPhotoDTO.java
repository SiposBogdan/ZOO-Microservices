package exemplarservice.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class ExemplarPhotoDTO {
    private Long    animalId;
    private String  name;
    private String  location;
    private Integer age;
    private Double  weight;
    private List<MultipartFile> photos;
}
