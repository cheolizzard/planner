package Model;

/**
 * 과목 정보 모델 클래스
 */
public class Subject {
    private int subjectId;         // 과목 ID
    private String subjectName;     // 과목명
    private int credits;           // 학점
    
    public Subject() {
    }
    
    public Subject(int subjectId, String subjectName, int credits) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
    }
    
    // Getters and Setters
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
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", credits=" + credits +
                '}';
    }
}

