package animalservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    /** Number of species per category (e.g. “Bird” → 12) */
    private Map<String, Long> countPerCategory;
    /** Distribution of diet types (e.g. “Herbivore” → 8) */
    private Map<String, Long> dietDistribution;
    /** Overall average weight across all species */
    private double avgWeight;
    /** Overall average age across all species */
    private double avgAge;
}
