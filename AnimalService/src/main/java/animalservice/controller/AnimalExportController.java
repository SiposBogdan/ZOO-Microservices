package animalservice.controller;

import animalservice.service.AnimalExporterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/animal/export")
public class AnimalExportController {

    private final AnimalExporterService svc;
    public AnimalExportController(AnimalExporterService svc) {
        this.svc = svc;
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> csv() throws IOException {
        byte[] data = svc.exportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animals.csv")
                .body(data);
    }

    @GetMapping(value = "/json", produces = "application/json")
    public ResponseEntity<byte[]> json() throws JsonProcessingException {
        byte[] data = svc.exportJson();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animals.json")
                .body(data);
    }

    @GetMapping(value = "/xml", produces = "application/xml")
    public ResponseEntity<byte[]> xml() throws JsonProcessingException {
        byte[] data = svc.exportXml();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animals.xml")
                .body(data);
    }

    @GetMapping(value = "/docx", produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> docx() throws Exception {
        byte[] data = svc.exportDocxWithCharts();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=animals-stats.docx")
                .body(data);
    }
}
