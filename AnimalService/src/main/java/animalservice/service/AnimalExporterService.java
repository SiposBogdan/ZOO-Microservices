package animalservice.service;

import animalservice.domain.Animal;
import animalservice.repository.AnimalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVWriter;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnimalExporterService {

    private final AnimalRepository repo;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public AnimalExporterService(AnimalRepository repo) {
        this.repo = repo;
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    public byte[] exportCsv() throws IOException {
        List<Animal> list = repo.findAll();
        try (StringWriter sw = new StringWriter();
             CSVWriter writer = new CSVWriter(sw)) {
            // antet
            writer.writeNext(new String[]{"id","category","species","diet","habitat"});
            for (Animal a : list) {
                writer.writeNext(new String[]{
                        a.getId().toString(),
                        a.getCategory(),
                        a.getDietType(),
                        a.getHabitat()
                });
            }
            writer.flush();
            return sw.toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    public byte[] exportJson() throws JsonProcessingException {
        List<Animal> list = repo.findAll();
        return jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(list);
    }

    public byte[] exportXml() throws JsonProcessingException {
        List<Animal> list = repo.findAll();
        return xmlMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(Collections.singletonMap("animals", list));
    }

    public byte[] exportDocxWithCharts() throws Exception {
        // 1️⃣ generează statistici
        Map<String, Long> byCategory = repo.findAll().stream()
                .collect(Collectors.groupingBy(Animal::getCategory, Collectors.counting()));
        // 2️⃣ creează un chart JFreeChart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        byCategory.forEach((cat, cnt) -> dataset.addValue(cnt, "Count", cat));
        JFreeChart chart = ChartFactory.createBarChart(
                "Animale pe categorii", "Categorie", "Număr", dataset);

        // 3️⃣ exportă chart-ul în imagine
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(chartOut, chart, 600, 400);

        // 4️⃣ creează un document .docx şi inserează imaginea
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText("Statistici animale (pe categorii):");
        r.addBreak();
        try (InputStream pic = new ByteArrayInputStream(chartOut.toByteArray())) {
            r.addPicture(pic,
                    Document.PICTURE_TYPE_PNG,
                    "chart.png",
                    Units.toEMU(600),
                    Units.toEMU(400));
        }

        ByteArrayOutputStream docOut = new ByteArrayOutputStream();
        doc.write(docOut);
        return docOut.toByteArray();
    }
}
