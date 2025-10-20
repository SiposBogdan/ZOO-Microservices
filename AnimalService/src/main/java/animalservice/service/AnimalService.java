// src/main/java/animalservice/service/AnimalService.java
package animalservice.service;

import animalservice.domain.Animal;
import animalservice.domain.dto.AnimalDTO;
import animalservice.domain.dto.StatsDTO;
import animalservice.repository.AnimalRepository;
import animalservice.service.exporter.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final CsvExporter csvExporter;
    private final JsonExporter jsonExporter;
    private final XmlExporter xmlExporter;
    private final DocxExporter docxExporter;
    // inject your exporters/stats exporters here

    public AnimalService(AnimalRepository animalRepository,
                         CsvExporter csvExporter,
                         JsonExporter jsonExporter,
                         XmlExporter xmlExporter,
                         DocxExporter docxExporter) {
        this.animalRepository = animalRepository;
        this.csvExporter      = csvExporter;
        this.jsonExporter     = jsonExporter;
        this.xmlExporter      = xmlExporter;
        this.docxExporter     = docxExporter;
    }

    public byte[] exportAll(String format) {
        List<AnimalDTO> list = listAll("name,asc");
        switch (format.toLowerCase()) {
            case "json":
                return jsonExporter.export(list);
            case "xml":
                return xmlExporter.export(list);
            case "doc":
                return docxExporter.export(list);
            default:
                return csvExporter.export(list);
        }
    }

    // VISITOR: list & sort by name & dietType
    public List<AnimalDTO> listAll(String sort) {
        List<AnimalDTO> dtos = animalRepository.findAll().stream()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());

        Comparator<AnimalDTO> cmp = Comparator.comparing(AnimalDTO::getName);
        for (String part : sort.split(";")) {
            String[] kv = part.split(",");
            String field = kv[0], dir = kv.length>1?kv[1]:"asc";
            Comparator<AnimalDTO> by = "dietType".equals(field)
                    ? Comparator.comparing(AnimalDTO::getDietType)
                    : Comparator.comparing(AnimalDTO::getName);
            cmp = "desc".equalsIgnoreCase(dir)
                    ? cmp.thenComparing(by.reversed())
                    : cmp.thenComparing(by);
        }
        dtos.sort(cmp);
        return dtos;
    }

    // VISITOR: filter by category, name, dietType, habitat
    public List<AnimalDTO> filter(String category,
                                  String name,
                                  String dietType,
                                  String habitat) {
        return animalRepository.findAll().stream()
                .map(AnimalDTO::fromEntity)
                .filter(a -> category  == null || a.getCategory().equalsIgnoreCase(category))
                .filter(a -> name      == null || a.getName().equalsIgnoreCase(name))
                .filter(a -> dietType  == null || a.getDietType().equalsIgnoreCase(dietType))
                .filter(a -> habitat   == null || a.getHabitat().equalsIgnoreCase(habitat))
                .collect(Collectors.toList());
    }

    // VISITOR: search by category or name
    public List<AnimalDTO> search(String q) {
        String term = q.toLowerCase();
        return animalRepository.findAll().stream()
                .map(AnimalDTO::fromEntity)
                .filter(a -> a.getCategory().toLowerCase().contains(term)
                        || a.getName().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    // EMPLOYEE: CRUD
    public AnimalDTO saveAnimal(AnimalDTO animalDTO) {
        Animal a = AnimalDTO.toEntity(animalDTO);
        return AnimalDTO.fromEntity(animalRepository.save(a));
    }

    public AnimalDTO getAnimalById(Long id) {
        return animalRepository.findById(id)
                .map(AnimalDTO::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("Animal not found: " + id));
    }

    public AnimalDTO updateAnimal(Long id, AnimalDTO animalDTO) {
        Animal existing = animalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Animal not found: " + id));
        existing.setName(animalDTO.getName());
        existing.setCategory(animalDTO.getCategory());
        existing.setDietType(animalDTO.getDietType());
        existing.setHabitat(animalDTO.getHabitat());
        existing.setAverageWeight(animalDTO.getAverageWeight());
        existing.setAverageAge(animalDTO.getAverageAge());
        return AnimalDTO.fromEntity(animalRepository.save(existing));
    }

    public void deleteAnimal(Long id) {
        animalRepository.deleteById(id);
    }

    // EMPLOYEE & MANAGER: export in multiple formats


    // MANAGER: stats
    public StatsDTO computeStats() {
        List<Animal> all = animalRepository.findAll();
        StatsDTO stats = new StatsDTO();
        stats.setCountPerCategory(all.stream()
                .collect(Collectors.groupingBy(Animal::getCategory, Collectors.counting())));
        stats.setDietDistribution(all.stream()
                .collect(Collectors.groupingBy(Animal::getDietType, Collectors.counting())));
        stats.setAvgWeight(all.stream().mapToDouble(Animal::getAverageWeight).average().orElse(0));
        stats.setAvgAge(all.stream().mapToDouble(Animal::getAverageAge).average().orElse(0));
        return stats;
    }

    public byte[] exportStatsWord() {
        StatsDTO stats = computeStats();
        return WordExporter.export(stats);
    }
}



//package animalservice.service;
//
//import animalservice.domain.Animal;
//import animalservice.domain.dto.AnimalDTO;
//import animalservice.repository.AnimalRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class AnimalService {
//
//    private final AnimalRepository animalRepository;
//
//    public AnimalService(AnimalRepository animalRepository) {
//        this.animalRepository = animalRepository;
//    }
//
//    public List<AnimalDTO> getAllAnimal() {
//        return animalRepository.findAll().stream()
//                .map(AnimalDTO::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    public AnimalDTO getAnimalById(Long id) {
//        return animalRepository.findById(id)
//                .map(AnimalDTO::fromEntity)
//                .orElseThrow(() -> new RuntimeException("Animal not found"));
//    }
//
//    public AnimalDTO saveAnimal(AnimalDTO animalDTO) {
//        Animal animal = AnimalDTO.toEntity(animalDTO);
//        return AnimalDTO.fromEntity(animalRepository.save(animal));
//    }
//
//    public AnimalDTO updateAnimal(Long id, AnimalDTO animalDTO) {
//
//        Animal existingAnimal = animalRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
//
//        existingAnimal.setName(animalDTO.getName());
//        existingAnimal.setCategory(animalDTO.getCategory());
//
//        existingAnimal.setDietType(animalDTO.getDietType());
//        existingAnimal.setHabitat(animalDTO.getHabitat());
//        existingAnimal.setAverageWeight(animalDTO.getAverageWeight());
//        existingAnimal.setAverageAge(animalDTO.getAverageAge());
//        Animal saved = animalRepository.save(existingAnimal);
//        return AnimalDTO.fromEntity(saved);
//
//    }
//
//    public void deleteAnimal(Long id) {
//        animalRepository.deleteById(id);
//    }
//}