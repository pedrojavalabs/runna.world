package world.runna.service;
import world.runna.dto.ActivityDTO;
import world.runna.model.*;
import world.runna.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

@Service
public class ActivityService {
    private final ActivityRepository activityRepo;
    private final UserRepository userRepo;
    private final GpxParserService gpxParser;
    public ActivityService(ActivityRepository activityRepo, UserRepository userRepo, GpxParserService gpxParser){
        this.activityRepo=activityRepo; this.userRepo=userRepo; this.gpxParser=gpxParser;
    }
    public Activity createFromGpx(MultipartFile file, String title, String sport) throws Exception {
        GpxParserService.ParsedResult parsed = gpxParser.parse(file.getInputStream());
        User user = userRepo.findById(1L).orElseGet(()->{
            User u=new User(); u.setUsername("pedro"); u.setDisplayName("Pedro"); u.setEmail("pedro@strava.local");
            return userRepo.save(u);
        });
        Activity a = new Activity();
        a.setTitle(title); a.setSportType(sport); a.setStartTime(LocalDateTime.now());
        a.setDistanceMeters(parsed.distance); a.setElevationGain(parsed.elevationGain);
        a.setDurationSeconds(parsed.points.size()*2);
        a.setGpxData(new String(file.getBytes()));
        a.setUser(user);
        String poly = parsed.route.stream().map(c->c[0]+","+c[1]).collect(Collectors.joining(";"));
        a.setPolyline(poly);
        Activity saved = activityRepo.save(a);
        for(GpsPoint p: parsed.points){ p.setActivity(saved); }
        return saved;
    }
    public List<ActivityDTO> feed() {
        return activityRepo.findAllByOrderByStartTimeDesc().stream().map(this::toDTO).collect(Collectors.toList());
    }
    private ActivityDTO toDTO(Activity a){
        ActivityDTO dto=new ActivityDTO();
        dto.setId(a.getId()); dto.setTitle(a.getTitle()); dto.setSportType(a.getSportType());
        dto.setStartTime(a.getStartTime()); dto.setDistanceMeters(a.getDistanceMeters());
        dto.setDurationSeconds(a.getDurationSeconds()); dto.setElevationGain(a.getElevationGain());
        dto.setUsername(a.getUser()!=null?a.getUser().getUsername():"anonymous");
        if(a.getPolyline()!=null && !a.getPolyline().isEmpty()){
            List<double[]> route = Arrays.stream(a.getPolyline().split(";"))
                .filter(s->s.contains(","))
                .map(s->{String[] p=s.split(","); return new double[]{Double.parseDouble(p[0]), Double.parseDouble(p[1])};})
                .collect(Collectors.toList());
            dto.setRoute(route);
        }
        return dto;
    }
}
