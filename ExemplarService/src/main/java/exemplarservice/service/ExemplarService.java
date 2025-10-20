// src/main/java/exemplarservice/service/ExemplarService.java
package exemplarservice.service;

import exemplarservice.domain.dto.ExemplarDTO;
import exemplarservice.repository.ExemplarRepository;
import exemplarservice.domain.Exemplar;
import exemplarservice.domain.dto.AnimalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExemplarService {

    private final ExemplarRepository repository;
    private final AnimalApiService animalApiService;  // << inject

    public ExemplarService(
            ExemplarRepository repository,
            AnimalApiService animalApiService
    ) {
        this.repository        = repository;
        this.animalApiService  = animalApiService;
    }

    @Transactional(readOnly = true)
    public List<ExemplarDTO> getAllExemplars() {
        return repository.findAll().stream()
                .map(this::toDto)            // map to DTO
                .peek(this::enrichWithSpecie) // enrich each
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExemplarDTO getExemplarById(Long id) {
        Exemplar ex = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar not found: " + id));
        ExemplarDTO dto = toDto(ex);
        enrichWithSpecie(dto);
        return dto;
    }

    @Transactional
    public ExemplarDTO saveExemplar(ExemplarDTO dto) {
        Exemplar ex = toEntity(dto);
        Exemplar saved = repository.save(ex);
        ExemplarDTO result = toDto(saved);
        enrichWithSpecie(result);
        return result;
    }

    @Transactional
    public ExemplarDTO updateExemplar(Long id, ExemplarDTO dto) {
        Exemplar existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exemplar not found: " + id));
        existing.setName(dto.getName());
        existing.setAnimalId(dto.getAnimalId());
        existing.setImages(dto.getImages());
        existing.setLocation(dto.getLocation());
        existing.setAge(dto.getAge());
        existing.setWeight(dto.getWeight());
        existing.setNotes(dto.getNotes());
        Exemplar updated = repository.save(existing);
        ExemplarDTO result = toDto(updated);
        enrichWithSpecie(result);
        return result;
    }

    @Transactional
    public void deleteExemplar(Long id) {
        repository.deleteById(id);
    }

    // --- Internal helpers ---

    private ExemplarDTO toDto(Exemplar ex) {
        return ExemplarDTO.builder()
                .id(ex.getId())
                .name(ex.getName())
                .animalId(ex.getAnimalId())
                .images(ex.getImages())
                .location(ex.getLocation())
                .age(ex.getAge())
                .weight(ex.getWeight())
                .notes(ex.getNotes())
                .build();
    }

    private Exemplar toEntity(ExemplarDTO dto) {
        return Exemplar.builder()
                .name(dto.getName())
                .animalId(dto.getAnimalId())
                .images(dto.getImages())
                .location(dto.getLocation())
                .age(dto.getAge())
                .weight(dto.getWeight())
                .notes(dto.getNotes())
                .build();
    }

    private void enrichWithSpecie(ExemplarDTO dto) {
        AnimalDTO animal = animalApiService.fetchById(dto.getAnimalId());
        dto.setSpecie(animal.getName());
    }
}
