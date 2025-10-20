package animalservice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "animal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nume comun al speciei */
    @Column(nullable = false, length = 100)
    private String name;

    /** Categoria (pasăre, reptilă, insectă, pește etc.) */
    @Column(nullable = false, length = 50)
    private String category;

    /** Tip de alimentație (erbivor, carnivor, omnivor etc.) */
    @Column(nullable = false, length = 50)
    private String dietType;

    /** Habitatul natural */
    @Column(nullable = false, length = 100)
    private String habitat;

    /** Greutatea medie în kilograme */
    @Column(nullable = false)
    private Double averageWeight;

    /** Vârsta medie în ani */
    @Column(nullable = false)
    private Double averageAge;
}
