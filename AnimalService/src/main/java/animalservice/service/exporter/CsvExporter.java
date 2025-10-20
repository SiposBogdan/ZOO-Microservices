package animalservice.service.exporter;

import animalservice.domain.dto.AnimalDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Component
public class CsvExporter {
    public byte[] export(List<AnimalDTO> list) {
        try (var baos = new ByteArrayOutputStream();
             var osw  = new OutputStreamWriter(baos);
             var writer = new CSVWriter(osw)) {

            // Header
            writer.writeNext(new String[]{
                    "ID", "Name", "Category", "DietType", "Habitat", "AvgWeight", "AvgAge"
            });

            // Rows
            for (var a : list) {
                writer.writeNext(new String[]{
                        String.valueOf(a.getId()),
                        a.getName(),
                        a.getCategory(),
                        a.getDietType(),
                        a.getHabitat(),
                        String.valueOf(a.getAverageWeight()),
                        String.valueOf(a.getAverageAge())
                });
            }

            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("CSV export failed", e);
        }
    }
}
