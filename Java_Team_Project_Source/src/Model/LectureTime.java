package Model;

/**
 * 강의 시간 정보 모델 클래스
 */
public class LectureTime {
    private int timeId;        // 시간 ID
    private int courseId;      // 강의 ID
    private String dayOfWeek; // 요일 (월, 화, 수, 목, 금)
    private String startTime;  // 시작 시간 (HH:mm)
    private String endTime;    // 끝 시간 (HH:mm)
    
    public LectureTime() {
    }
    
    public LectureTime(int timeId, int courseId, String dayOfWeek, String startTime, String endTime) {
        this.timeId = timeId;
        this.courseId = courseId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public int getTimeId() {
        return timeId;
    }
    
    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public String toString() {
        return "LectureTime{" +
                "dayOfWeek='" + dayOfWeek + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}

