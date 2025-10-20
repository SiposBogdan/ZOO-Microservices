package animalservice.service.exporter;


import animalservice.domain.dto.StatsDTO;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class WordExporter {
    public static byte[] export(StatsDTO stats) {
        try (var doc = new XWPFDocument();
             var baos = new ByteArrayOutputStream()) {

            // Title
            var para = doc.createParagraph();
            para.setAlignment(ParagraphAlignment.CENTER);
            var run = para.createRun();
            run.setText("Animal Statistics");
            run.setBold(true);
            run.setFontSize(16);

            // Table for countPerCategory
            createSection(doc, "Count per Category", stats.getCountPerCategory());
            createSection(doc, "Diet Distribution", stats.getDietDistribution());

            // Averages as paragraphs
            var avgPara = doc.createParagraph();
            avgPara.createRun()
                    .setText("Average Weight: " + stats.getAvgWeight());
            avgPara = doc.createParagraph();
            avgPara.createRun()
                    .setText("Average Age: " + stats.getAvgAge());

            doc.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Stats DOCX export failed", e);
        }
    }

    private static void createSection(XWPFDocument doc, String title, Map<String,Long> data) {
        var p = doc.createParagraph();
        p.setSpacingBefore(200);
        p.createRun().setText(title);
        XWPFTable table = doc.createTable();
        var hdr = table.getRow(0);
        hdr.getCell(0).setText("Key");
        hdr.addNewTableCell().setText("Value");
        for (var entry : data.entrySet()) {
            var row = table.createRow();
            row.getCell(0).setText(entry.getKey());
            row.getCell(1).setText(String.valueOf(entry.getValue()));
        }
    }
}
