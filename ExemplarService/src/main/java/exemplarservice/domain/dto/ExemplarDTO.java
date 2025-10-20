package exemplarservice.domain.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExemplarDTO {
    private Long id;
    private Long animalId;
    private String name;
    private String specie;
    private List<String> images;
    private String location;
    private Double age;
    private Double weight;
    private String notes;
}
