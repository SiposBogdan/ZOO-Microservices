package animalservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // enable CORS, disable CSRF
                .cors(Customizer.withDefaults())
                .csrf().disable()

                // make it stateless
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // rules
                // in your animal-service SecurityConfig.filterChain(...)
                .authorizeHttpRequests(auth -> auth
                        // still allow GET without auth

                        .requestMatchers(HttpMethod.GET, "/api/animal/**").permitAll()

                        // temporarily allow DELETE
                        .requestMatchers(HttpMethod.DELETE, "/api/animal/**").permitAll()

                        // lock down POST/PUT if you like
                        .requestMatchers(HttpMethod.POST,   "/api/animal/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,    "/api/animal/**").permitAll()

                        .anyRequest().authenticated()
                )

                // JWT filter comes after the username/password filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // your front-end origin
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}



//package animalservice.controller;
//
//import animalservice.domain.dto.AnimalDTO;
//import animalservice.domain.dto.StatsDTO;
//import animalservice.service.AnimalService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.ByteArrayInputStream;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/animal")
//@CrossOrigin(origins = "http://localhost:3000")
//@RequiredArgsConstructor
//public class AnimalController {
//
//    private final AnimalService animalService;
//
//    // VISITOR
//
//    /** 1. List all species, sorted by species & dietType */
//    @GetMapping
//    public ResponseEntity<List<AnimalDTO>> getAllAnimals(
//            @RequestParam(defaultValue = "species,asc;dietType,asc") String sort) {
//        return ResponseEntity.ok(animalService.listAll(sort));
//    }
//
//    /** 2. Filter by category, species, dietType, habitat */
//    @GetMapping("/filter")
//    public ResponseEntity<List<AnimalDTO>> filter(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String species,
//            @RequestParam(required = false) String dietType,
//            @RequestParam(required = false) String habitat) {
//        return ResponseEntity.ok(animalService.filter(category, species, dietType, habitat));
//    }
//
//    /** 3. Search by category or species */
//    @GetMapping("/search")
//    public ResponseEntity<List<AnimalDTO>> search(@RequestParam String q) {
//        return ResponseEntity.ok(animalService.search(q));
//    }
//
//
//    // EMPLOYEE (ANGAJAT)
//
//    /** 4. Create a new species */
//    @PostMapping
//    //@PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<AnimalDTO> createAnimal(
//            @Valid @RequestBody AnimalDTO animalDTO) {
//        AnimalDTO created = animalService.saveAnimal(animalDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }
//
//    /** 5. Update an existing species */
//    @PutMapping("/{id}")
//    //@PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<AnimalDTO> updateAnimal(
//            @PathVariable Long id,
//            @Valid @RequestBody AnimalDTO animalDTO) {
//        return ResponseEntity.ok(animalService.updateAnimal(id, animalDTO));
//    }
//
//    /** 6. Delete a species */
//    @DeleteMapping("/{id}")
//    //@PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
//        animalService.deleteAnimal(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    /** 7. Export species list in csv|json|xml|doc */
//    @GetMapping("/export")
//    //@PreAuthorize("hasRole('EMPLOYEE')")
//    public ResponseEntity<InputStreamResource> exportAll(
//            @RequestParam(defaultValue = "csv") String format) {
//
//        byte[] data = animalService.exportAll(format);
//        MediaType contentType;
//        String ext;
//        switch (format.toLowerCase()) {
//            case "json": contentType = MediaType.APPLICATION_JSON; ext = "json"; break;
//            case "xml":  contentType = MediaType.APPLICATION_XML;  ext = "xml";  break;
//            case "doc":
//                contentType = MediaType.parseMediaType(
//                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//                ext = "docx"; break;
//            default:
//                contentType = MediaType.parseMediaType("text/csv"); ext = "csv";
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDisposition(
//                ContentDisposition.attachment().filename("animals." + ext).build()
//        );
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(contentType)
//                .body(new InputStreamResource(new ByteArrayInputStream(data)));
//    }
//
//
//    // MANAGER
//
//    /** 8. Get aggregated statistics */
//    @GetMapping("/stats")
//    //@PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<StatsDTO> getStats() {
//        return ResponseEntity.ok(animalService.computeStats());
//    }
//
//    /** 9. Export statistics to Word */
//    @GetMapping("/stats/export")
//    //@PreAuthorize("hasRole('MANAGER')")
//    public ResponseEntity<InputStreamResource> exportStats() {
//        byte[] doc = animalService.exportStatsWord();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentDisposition(
//                ContentDisposition.attachment().filename("animal-stats.docx").build()
//        );
//        headers.setContentType(MediaType.parseMediaType(
//                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//        ));
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(new InputStreamResource(new ByteArrayInputStream(doc)));
//    }
//}
