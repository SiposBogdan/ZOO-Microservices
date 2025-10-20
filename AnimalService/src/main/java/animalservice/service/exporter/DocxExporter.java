package animalservice.service.exporter;

import animalservice.domain.dto.AnimalDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class DocxExporter {
    public byte[] export(List<AnimalDTO> list) {
        try (var doc = new XWPFDocument();
             var baos = new ByteArrayOutputStream()) {

            XWPFTable table = doc.createTable();
            // header row
            var hdr = table.getRow(0);
            hdr.getCell(0).setText("ID");
            hdr.addNewTableCell().setText("Name");
            hdr.addNewTableCell().setText("Category");
            hdr.addNewTableCell().setText("DietType");
            hdr.addNewTableCell().setText("Habitat");
            hdr.addNewTableCell().setText("AvgWeight");
            hdr.addNewTableCell().setText("AvgAge");

            // data rows
            for (var a : list) {
                var row = table.createRow();
                row.getCell(0).setText(String.valueOf(a.getId()));
                row.getCell(1).setText(a.getName());
                row.getCell(2).setText(a.getCategory());
                row.getCell(3).setText(a.getDietType());
                row.getCell(4).setText(a.getHabitat());
                row.getCell(5).setText(String.valueOf(a.getAverageWeight()));
                row.getCell(6).setText(String.valueOf(a.getAverageAge()));
            }

            doc.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("DOCX export failed", e);
        }
    }
}
