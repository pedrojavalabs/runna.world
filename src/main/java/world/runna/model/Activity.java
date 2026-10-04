package world.runna.model;
import jakarta.persistence.*;
import java.time.*;
import java.util.List;
@Entity
public class Activity {
    @Id @GeneratedValue private Long id;
    private String title; private String sportType;
    @Column(length=2000) private String description;
    private LocalDateTime startTime;
    private Double distanceMeters; private Integer durationSeconds; private Integer elevationGain;
    private Double avgSpeed; private Integer calories;
    @Lob @Column(columnDefinition="TEXT") private String gpxData;
    @Lob @Column(columnDefinition="TEXT") private String polyline;
    @ManyToOne @JoinColumn(name="user_id") private User user;
    @OneToMany(mappedBy="activity", cascade=CascadeType.ALL) private List<GpsPoint> points;
    public Activity(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getTitle(){return title;} public void setTitle(String v){this.title=v;}
    public String getSportType(){return sportType;} public void setSportType(String v){this.sportType=v;}
    public String getDescription(){return description;} public void setDescription(String v){this.description=v;}
    public LocalDateTime getStartTime(){return startTime;} public void setStartTime(LocalDateTime v){this.startTime=v;}
    public Double getDistanceMeters(){return distanceMeters;} public void setDistanceMeters(Double v){this.distanceMeters=v;}
    public Integer getDurationSeconds(){return durationSeconds;} public void setDurationSeconds(Integer v){this.durationSeconds=v;}
    public Integer getElevationGain(){return elevationGain;} public void setElevationGain(Integer v){this.elevationGain=v;}
    public Double getAvgSpeed(){return avgSpeed;} public void setAvgSpeed(Double v){this.avgSpeed=v;}
    public Integer getCalories(){return calories;} public void setCalories(Integer v){this.calories=v;}
    public String getGpxData(){return gpxData;} public void setGpxData(String v){this.gpxData=v;}
    public String getPolyline(){return polyline;} public void setPolyline(String v){this.polyline=v;}
    public User getUser(){return user;} public void setUser(User v){this.user=v;}
    public List<GpsPoint> getPoints(){return points;} public void setPoints(List<GpsPoint> v){this.points=v;}
    @Transient public String getPace() {
        if(distanceMeters==null||durationSeconds==null||distanceMeters==0) return "-";
        double paceSecPerKm = durationSeconds / (distanceMeters/1000.0);
        int m = (int)paceSecPerKm/60; int s = (int)paceSecPerKm%60;
        return String.format("%d:%02d /km", m,s);
    }
}
