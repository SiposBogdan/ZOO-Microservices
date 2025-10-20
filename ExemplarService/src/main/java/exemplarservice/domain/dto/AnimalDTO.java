package exemplarservice.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimalDTO {
    private Long id;
    private String name;
    private String category;
    private String dietType;
    private String habitat;
    private Double averageWeight;
    private Double averageAge;

}
