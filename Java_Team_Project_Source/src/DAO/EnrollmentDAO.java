package DAO;

import DB.DB_MAN;
import Model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 수강신청 데이터 접근 객체
 */
public class EnrollmentDAO {
    private DB_MAN dbManager;
    
    public EnrollmentDAO() {
        this.dbManager = DB_MAN.getInstance();
    }
    
    /**
     * 수강신청 추가
     * @param studentId 학번
     * @param courseId 강의 ID
     * @return 생성된 수강신청 ID, 실패 시 -1
     */
    public int addEnrollment(String studentId, int courseId) throws SQLException {
        // 중복 체크
        if (isEnrolled(studentId, courseId)) {
            return -1; // 이미 수강신청됨
        }
        
        String sql = "INSERT INTO enrollment (student_id, course_id) VALUES (?, ?)";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatementWithKeys(sql);
            pstmt.setString(1, studentId);
            pstmt.setInt(2, courseId);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 수강신청 삭제
     * @param enrollId 수강신청 ID
     * @return 성공 시 true
     */
    public boolean deleteEnrollment(int enrollId) throws SQLException {
        String sql = "DELETE FROM enrollment WHERE enroll_id = ?";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, enrollId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 학생의 수강신청 목록 조회
     * @param studentId 학번
     * @return 수강신청 목록
     */
    public List<Enrollment> getEnrollmentsByStudentId(String studentId) throws SQLException {
        String sql = "SELECT e.enroll_id, e.student_id, e.course_id, s.subject_name " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.course_id " +
                     "JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE e.student_id = ?";
        
        List<Enrollment> enrollments = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollId(rs.getInt("enroll_id"));
                enrollment.setStudentId(rs.getString("student_id"));
                enrollment.setCourseId(rs.getInt("course_id"));
                enrollment.setSubjectName(rs.getString("subject_name"));
                enrollments.add(enrollment);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return enrollments;
    }
    
    /**
     * 수강신청 여부 확인
     * @param studentId 학번
     * @param courseId 강의 ID
     * @return 수강신청되어 있으면 true
     */
    public boolean isEnrolled(String studentId, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt FROM enrollment WHERE student_id = ? AND course_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            pstmt.setInt(2, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("cnt") > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 수강신청 ID로 조회
     * @param enrollId 수강신청 ID
     * @return Enrollment 객체
     */
    public Enrollment getEnrollmentById(int enrollId) throws SQLException {
        String sql = "SELECT e.enroll_id, e.student_id, e.course_id, s.subject_name " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.course_id " +
                     "JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE e.enroll_id = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, enrollId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setEnrollId(rs.getInt("enroll_id"));
                enrollment.setStudentId(rs.getString("student_id"));
                enrollment.setCourseId(rs.getInt("course_id"));
                enrollment.setSubjectName(rs.getString("subject_name"));
                return enrollment;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
}

