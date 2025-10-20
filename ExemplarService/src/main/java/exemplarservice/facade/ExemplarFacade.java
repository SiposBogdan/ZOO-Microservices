package exemplarservice.facade;

import exemplarservice.domain.dto.AnimalDTO;
import exemplarservice.domain.dto.ExemplarDTO;
import exemplarservice.service.AnimalApiService;
import exemplarservice.service.ExemplarService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExemplarFacade {

    private final ExemplarService exemplarService;
    private final AnimalApiService animalApiService;

    public ExemplarFacade(ExemplarService exemplarService,
                          AnimalApiService animalApiService) {
        this.exemplarService   = exemplarService;
        this.animalApiService  = animalApiService;
    }

    public List<ExemplarDTO> getAllExemplars() {
        List<ExemplarDTO> dtos = exemplarService.getAllExemplars();
        dtos.forEach(this::enrichWithAnimalData);
        return dtos;
    }

    public ExemplarDTO getExemplarById(Long id) {
        ExemplarDTO dto = exemplarService.getExemplarById(id);
        enrichWithAnimalData(dto);
        return dto;
    }

    public ExemplarDTO createExemplar(ExemplarDTO exemplarDTO) {
        ExemplarDTO saved = exemplarService.saveExemplar(exemplarDTO);
        enrichWithAnimalData(saved);
        return saved;
    }

    public ExemplarDTO updateExemplar(Long id, ExemplarDTO exemplarDTO) {
        ExemplarDTO updated = exemplarService.updateExemplar(id, exemplarDTO);
        enrichWithAnimalData(updated);
        return updated;
    }

    public void deleteExemplar(Long id) {
        exemplarService.deleteExemplar(id);
    }

    private void enrichWithAnimalData(ExemplarDTO exemplar) {
        AnimalDTO animal = animalApiService.fetchById(exemplar.getAnimalId());
        exemplar.setSpecie(animal.getName());
    }


}
