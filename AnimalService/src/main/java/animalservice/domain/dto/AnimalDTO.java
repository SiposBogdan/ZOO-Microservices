package animalservice.domain.dto;

import animalservice.domain.Animal;
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

    /** Mapează din entitate în DTO */
    public static AnimalDTO fromEntity(Animal a) {
        if (a == null) return null;
        return AnimalDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .category(a.getCategory())
                .dietType(a.getDietType())
                .habitat(a.getHabitat())
                .averageWeight(a.getAverageWeight())
                .averageAge(a.getAverageAge())
                .build();
    }

    /** Mapează din DTO în entitate */
    public static Animal toEntity(AnimalDTO dto) {
        if (dto == null) return null;
        Animal a = new Animal();
        a.setId(dto.getId());
        a.setName(dto.getName());
        a.setCategory(dto.getCategory());
        a.setDietType(dto.getDietType());
        a.setHabitat(dto.getHabitat());
        a.setAverageWeight(dto.getAverageWeight());
        a.setAverageAge(dto.getAverageAge());
        return a;
    }
}
