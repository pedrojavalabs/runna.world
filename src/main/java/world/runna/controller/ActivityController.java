package world.runna.controller;
import world.runna.dto.ActivityDTO;
import world.runna.model.Activity;
import world.runna.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController @RequestMapping("/api/activities") @CrossOrigin(origins="*")
public class ActivityController {
    private final ActivityService service;
    public ActivityController(ActivityService service){this.service=service;}
    @GetMapping public List<ActivityDTO> feed(){ return service.feed(); }
    @PostMapping("/upload")
    public ResponseEntity<Activity> upload(@RequestParam("file") MultipartFile file,
                                           @RequestParam(defaultValue="Morning Run") String title,
                                           @RequestParam(defaultValue="RUN") String sport) throws Exception {
        return ResponseEntity.ok(service.createFromGpx(file, title, sport));
    }
    @GetMapping("/{id}") public ResponseEntity<ActivityDTO> get(@PathVariable Long id){
        return service.feed().stream().filter(a->a.getId().equals(id)).findFirst()
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
