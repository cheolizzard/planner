package Model;

/**
 * 학생 정보 모델 클래스
 */
public class Student {
    private String studentId;      // 학번
    private int classId;           // 반 ID
    private String className;      // 반 이름 (조회용)
    private String password;       // 비밀번호
    private String name;           // 이름
    private String department;     // 학과
    
    public Student() {
    }
    
    public Student(String studentId, int classId, String password, String name, String department) {
        this.studentId = studentId;
        this.classId = classId;
        this.password = password;
        this.name = name;
        this.department = department;
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public int getClassId() {
        return classId;
    }
    
    public void setClassId(int classId) {
        this.classId = classId;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}

