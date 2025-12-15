package DAO;

import DB.DB_MAN;
import Model.Student;
import java.sql.*;

/**
 * 학생 데이터 접근 객체
 */
public class StudentDAO {
    private DB_MAN dbManager;
    
    public StudentDAO() {
        this.dbManager = DB_MAN.getInstance();
    }
    
    /**
     * 로그인 - 학번과 비밀번호로 학생 조회
     * @param studentId 학번
     * @param password 비밀번호
     * @return Student 객체 (로그인 성공 시), null (실패 시)
     */
    public Student login(String studentId, String password) throws SQLException {
        String sql = "SELECT s.student_id, s.class_id, s.password, s.name, s.department, c.class_name " +
                     "FROM student s " +
                     "LEFT JOIN class_info c ON s.class_id = c.class_id " +
                     "WHERE s.student_id = ? AND s.password = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setClassId(rs.getInt("class_id"));
                student.setPassword(rs.getString("password"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setClassName(rs.getString("class_name"));
                return student;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 학번으로 학생 조회 (중복 체크용)
     * @param studentId 학번
     * @return Student 객체 (존재 시), null (없을 시)
     */
    public Student findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT s.student_id, s.class_id, s.password, s.name, s.department, c.class_name " +
                     "FROM student s " +
                     "LEFT JOIN class_info c ON s.class_id = c.class_id " +
                     "WHERE s.student_id = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setClassId(rs.getInt("class_id"));
                student.setPassword(rs.getString("password"));
                student.setName(rs.getString("name"));
                student.setDepartment(rs.getString("department"));
                student.setClassName(rs.getString("class_name"));
                return student;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 회원가입 - 새 학생 등록
     * @param student 학생 정보
     * @return 성공 시 true, 실패 시 false
     */
    public boolean register(Student student) throws SQLException {
        // 먼저 중복 체크
        if (findByStudentId(student.getStudentId()) != null) {
            return false; // 이미 존재하는 학번
        }
        
        String sql = "INSERT INTO student (student_id, class_id, password, name, department) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, student.getStudentId());
            pstmt.setInt(2, student.getClassId());
            pstmt.setString(3, student.getPassword());
            pstmt.setString(4, student.getName());
            pstmt.setString(5, student.getDepartment());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 반 ID 조회 (반 이름으로)
     * @param className 반 이름 (예: "C반")
     * @return 반 ID, 없으면 -1
     */
    public int getClassIdByName(String className) throws SQLException {
        String sql = "SELECT class_id FROM class_info WHERE class_name = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, className);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("class_id");
            }
            return -1;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 회원탈퇴 - 학생 삭제 (비밀번호 확인)
     * @param studentId 학번
     * @param password 비밀번호 (확인용)
     * @return 성공 시 true, 실패 시 false
     */
    public boolean deleteStudent(String studentId, String password) throws SQLException {
        // 먼저 비밀번호 확인
        Student student = login(studentId, password);
        if (student == null) {
            return false; // 비밀번호가 일치하지 않음
        }
        
        // 학생 삭제 (CASCADE로 enrollment, todo_list도 자동 삭제됨)
        String sql = "DELETE FROM student WHERE student_id = ?";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
}

