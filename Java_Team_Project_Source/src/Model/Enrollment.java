package Model;

/**
 * 수강신청 정보 모델 클래스
 */
public class Enrollment {
    private int enrollId;      // 수강신청 ID
    private String studentId;   // 학번
    private int courseId;       // 강의 ID
    private String subjectName; // 과목명 (조회용)
    
    public Enrollment() {
    }
    
    public Enrollment(int enrollId, String studentId, int courseId) {
        this.enrollId = enrollId;
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    // Getters and Setters
    public int getEnrollId() {
        return enrollId;
    }
    
    public void setEnrollId(int enrollId) {
        this.enrollId = enrollId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    
    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollId=" + enrollId +
                ", studentId='" + studentId + '\'' +
                ", courseId=" + courseId +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}

