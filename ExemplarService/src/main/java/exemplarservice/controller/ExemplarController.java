package exemplarservice.controller;

import exemplarservice.domain.dto.ExemplarDTO;
import exemplarservice.facade.ExemplarFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exemplar")
@CrossOrigin(origins="http://localhost:5173")
public class ExemplarController {

    private final ExemplarFacade facade;

    public ExemplarController(ExemplarFacade facade) {
        this.facade = facade;
    }

    /**
     * 1. GET /api/exemplars — list all exemplars
     * Accessible by VISITOR (unauthenticated) and above
     */
    @GetMapping
    public ResponseEntity<List<ExemplarDTO>> getAllExemplars() {
        List<ExemplarDTO> list = facade.getAllExemplars();
        return ResponseEntity.ok(list);
    }

    /**
     * 2. GET /api/exemplars/{id} — get one by ID
     * Accessible by VISITOR (unauthenticated) and above
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExemplarDTO> getExemplarById(@PathVariable Long id) {
        ExemplarDTO dto = facade.getExemplarById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 3. POST /api/exemplars — create new exemplar
     * Accessible by EMPLOYEE, MANAGER, ADMIN
     */
    //@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ExemplarDTO> createExemplar(@RequestBody ExemplarDTO dto) {
        ExemplarDTO created = facade.createExemplar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 4. PUT /api/exemplars/{id} — update existing exemplar
     * Accessible by EMPLOYEE, MANAGER, ADMIN
     */
    //@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ExemplarDTO> updateExemplar(
            @PathVariable Long id,
            @RequestBody ExemplarDTO dto) {
        ExemplarDTO updated = facade.updateExemplar(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 5. DELETE /api/exemplars/{id} — delete exemplar
     * Accessible by EMPLOYEE, MANAGER, ADMIN
     */
    //@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExemplar(@PathVariable Long id) {
        facade.deleteExemplar(id);
        return ResponseEntity.noContent().build();
    }

}
