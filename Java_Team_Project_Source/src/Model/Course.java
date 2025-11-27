package Model;

/**
 * 강의(개설 강좌) 정보 모델 클래스
 */
public class Course {
    private int courseId;          // 강의 ID
    private int subjectId;         // 과목 ID
    private String subjectName;    // 과목명 (조회용)
    private int professorId;       // 교수 ID
    private String professorName; // 교수명 (조회용)
    private String classroom;      // 강의실
    private String semester;       // 학기
    
    public Course() {
    }
    
    public Course(int courseId, int subjectId, int professorId, String classroom, String semester) {
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.professorId = professorId;
        this.classroom = classroom;
        this.semester = semester;
    }
    
    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    
    public int getProfessorId() {
        return professorId;
    }
    
    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }
    
    public String getProfessorName() {
        return professorName;
    }
    
    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
    
    public String getClassroom() {
        return classroom;
    }
    
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", subjectName='" + subjectName + '\'' +
                ", professorName='" + professorName + '\'' +
                ", classroom='" + classroom + '\'' +
                ", semester='" + semester + '\'' +
                '}';
    }
}

