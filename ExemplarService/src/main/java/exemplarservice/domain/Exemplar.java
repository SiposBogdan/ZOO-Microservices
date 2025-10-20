package exemplarservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "exemplar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * În loc de @ManyToOne spre Animal,
     * reținem doar ID-ul animalului.
     */
    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @ElementCollection
    @CollectionTable(
            name = "exemplar_images",
            joinColumns = @JoinColumn(name = "exemplar_id")
    )
    @Column(name = "image_url", nullable = false, length = 255)
    private List<String> images;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private Double age;

    @Column(nullable = false)
    private Double weight;

    @Column(length = 500)
    private String notes;
}
