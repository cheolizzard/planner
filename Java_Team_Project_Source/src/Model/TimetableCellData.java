package Model;

/**
 * 시간표 셀 데이터 클래스
 */
public class TimetableCellData {
    private String subjectName;    // 과목명
    private String classroom;      // 강의실
    private String professorName;  // 교수명
    private int courseId;          // 강의 ID (편집 시 사용)
    
    public TimetableCellData() {
    }
    
    public TimetableCellData(String subjectName, String classroom, String professorName, int courseId) {
        this.subjectName = subjectName;
        this.classroom = classroom;
        this.professorName = professorName;
        this.courseId = courseId;
    }
    
    public boolean isEmpty() {
        return subjectName == null || subjectName.isEmpty();
    }
    
    public String getDisplayText() {
        if (isEmpty()) {
            return "";
        }
        return subjectName + "\n" + classroom + "\n" + professorName;
    }
    
    // Getters and Setters
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    
    public String getClassroom() {
        return classroom;
    }
    
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
    
    public String getProfessorName() {
        return professorName;
    }
    
    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}

