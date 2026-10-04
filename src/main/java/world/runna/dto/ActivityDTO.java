package world.runna.dto;
import java.time.LocalDateTime;
import java.util.List;
public class ActivityDTO {
    private Long id; private String title; private String sportType;
    private LocalDateTime startTime; private Double distanceMeters;
    private Integer durationSeconds; private Integer elevationGain;
    private String polyline; private List<double[]> route;
    private String username;
    public ActivityDTO(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getTitle(){return title;} public void setTitle(String v){this.title=v;}
    public String getSportType(){return sportType;} public void setSportType(String v){this.sportType=v;}
    public LocalDateTime getStartTime(){return startTime;} public void setStartTime(LocalDateTime v){this.startTime=v;}
    public Double getDistanceMeters(){return distanceMeters;} public void setDistanceMeters(Double v){this.distanceMeters=v;}
    public Integer getDurationSeconds(){return durationSeconds;} public void setDurationSeconds(Integer v){this.durationSeconds=v;}
    public Integer getElevationGain(){return elevationGain;} public void setElevationGain(Integer v){this.elevationGain=v;}
    public String getPolyline(){return polyline;} public void setPolyline(String v){this.polyline=v;}
    public List<double[]> getRoute(){return route;} public void setRoute(List<double[]> v){this.route=v;}
    public String getUsername(){return username;} public void setUsername(String v){this.username=v;}
}
