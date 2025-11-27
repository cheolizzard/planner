package DAO;

import DB.DB_MAN;
import Model.Course;
import Model.LectureTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 강의 데이터 접근 객체
 */
public class CourseDAO {
    private DB_MAN dbManager;
    
    public CourseDAO() {
        this.dbManager = DB_MAN.getInstance();
    }
    
    /**
     * 학생의 수강 과목 목록 조회
     * @param studentId 학번
     * @return 수강 과목 목록
     */
    public List<Course> getCoursesByStudentId(String studentId) throws SQLException {
        String sql = "SELECT c.course_id, c.subject_id, s.subject_name, c.professor_id, p.name as professor_name, " +
                     "c.classroom, c.semester " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.course_id " +
                     "JOIN subject s ON c.subject_id = s.subject_id " +
                     "LEFT JOIN professor p ON c.professor_id = p.professor_id " +
                     "WHERE e.student_id = ?";
        
        List<Course> courses = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setSubjectId(rs.getInt("subject_id"));
                course.setSubjectName(rs.getString("subject_name"));
                course.setProfessorId(rs.getInt("professor_id"));
                course.setProfessorName(rs.getString("professor_name"));
                course.setClassroom(rs.getString("classroom"));
                course.setSemester(rs.getString("semester"));
                courses.add(course);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return courses;
    }
    
    /**
     * 강의의 시간표 정보 조회
     * @param courseId 강의 ID
     * @return 강의 시간 목록
     */
    public List<LectureTime> getLectureTimesByCourseId(int courseId) throws SQLException {
        String sql = "SELECT time_id, course_id, day_of_week, start_time, end_time " +
                     "FROM lecture_time " +
                     "WHERE course_id = ?";
        
        List<LectureTime> times = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LectureTime time = new LectureTime();
                time.setTimeId(rs.getInt("time_id"));
                time.setCourseId(rs.getInt("course_id"));
                time.setDayOfWeek(rs.getString("day_of_week"));
                time.setStartTime(rs.getString("start_time"));
                time.setEndTime(rs.getString("end_time"));
                times.add(time);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return times;
    }
    
    /**
     * 시간 겹침 검사
     * @param studentId 학번
     * @param dayOfWeek 요일
     * @param startTime 시작 시간
     * @param endTime 끝 시간
     * @param excludeCourseId 제외할 강의 ID (수정 시 사용)
     * @return 겹치는 강의가 있으면 true
     */
    public boolean hasTimeConflict(String studentId, String dayOfWeek, String startTime, String endTime, Integer excludeCourseId) throws SQLException {
        String sql = "SELECT COUNT(*) as cnt " +
                     "FROM enrollment e " +
                     "JOIN lecture_time lt ON e.course_id = lt.course_id " +
                     "WHERE e.student_id = ? " +
                     "AND lt.day_of_week = ? " +
                     "AND ((lt.start_time <= ? AND lt.end_time > ?) " +
                     "     OR (lt.start_time < ? AND lt.end_time >= ?) " +
                     "     OR (lt.start_time >= ? AND lt.end_time <= ?))";
        
        if (excludeCourseId != null) {
            sql += " AND e.course_id != ?";
        }
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            int paramIndex = 1;
            pstmt.setString(paramIndex++, studentId);
            pstmt.setString(paramIndex++, dayOfWeek);
            pstmt.setString(paramIndex++, startTime);
            pstmt.setString(paramIndex++, startTime);
            pstmt.setString(paramIndex++, endTime);
            pstmt.setString(paramIndex++, endTime);
            pstmt.setString(paramIndex++, startTime);
            pstmt.setString(paramIndex++, endTime);
            
            if (excludeCourseId != null) {
                pstmt.setInt(paramIndex, excludeCourseId);
            }
            
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
     * 학생의 총 학점 계산
     * @param studentId 학번
     * @return 총 학점
     */
    public int getTotalCredits(String studentId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(s.credits), 0) as total_credits " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.course_id " +
                     "JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE e.student_id = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_credits");
            }
            return 0;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 과목 ID 찾기 또는 생성
     * @param subjectName 과목명
     * @param credits 학점
     * @return 과목 ID
     */
    private int findOrCreateSubject(String subjectName, int credits) throws SQLException {
        // 기존 과목 찾기
        String findSql = "SELECT subject_id FROM subject WHERE subject_name = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(findSql);
            pstmt.setString(1, subjectName);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("subject_id");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        // 없으면 생성
        String insertSql = "INSERT INTO subject (subject_name, credits) VALUES (?, ?)";
        try {
            pstmt = dbManager.prepareStatementWithKeys(insertSql);
            pstmt.setString(1, subjectName);
            pstmt.setInt(2, credits);
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } finally {
            if (pstmt != null) pstmt.close();
        }
        
        return -1;
    }
    
    /**
     * 교수 ID 찾기 또는 생성
     * @param professorName 교수명
     * @return 교수 ID
     */
    private int findOrCreateProfessor(String professorName) throws SQLException {
        // 기존 교수 찾기
        String findSql = "SELECT professor_id FROM professor WHERE name = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(findSql);
            pstmt.setString(1, professorName);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("professor_id");
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        // 없으면 생성
        String insertSql = "INSERT INTO professor (name) VALUES (?)";
        try {
            pstmt = dbManager.prepareStatementWithKeys(insertSql);
            pstmt.setString(1, professorName);
            pstmt.executeUpdate();
            
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } finally {
            if (pstmt != null) pstmt.close();
        }
        
        return -1;
    }
    
    /**
     * 과목 추가 및 수강신청
     * @param studentId 학번
     * @param subjectName 과목명
     * @param professorName 교수명
     * @param classroom 강의실
     * @param dayOfWeek 요일
     * @param startTime 시작 시간
     * @param endTime 끝 시간
     * @param credits 학점 (기본 3)
     * @return 생성된 수강신청 ID, 실패 시 -1
     */
    public int addCourseWithEnrollment(String studentId, String subjectName, String professorName, 
                                       String classroom, String dayOfWeek, String startTime, 
                                       String endTime, int credits) throws SQLException {
        dbManager.beginTransaction();
        
        try {
            // 1. 과목 찾기 또는 생성
            int subjectId = findOrCreateSubject(subjectName, credits);
            if (subjectId == -1) {
                dbManager.rollback();
                return -1;
            }
            
            // 2. 교수 찾기 또는 생성
            int professorId = findOrCreateProfessor(professorName);
            if (professorId == -1) {
                dbManager.rollback();
                return -1;
            }
            
            // 3. 강의 생성
            String courseSql = "INSERT INTO course (subject_id, professor_id, classroom, semester) VALUES (?, ?, ?, '2025-1')";
            PreparedStatement pstmt = null;
            int courseId = -1;
            
            try {
                pstmt = dbManager.prepareStatementWithKeys(courseSql);
                pstmt.setInt(1, subjectId);
                pstmt.setInt(2, professorId);
                pstmt.setString(3, classroom);
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    courseId = rs.getInt(1);
                }
            } finally {
                if (pstmt != null) pstmt.close();
            }
            
            if (courseId == -1) {
                dbManager.rollback();
                return -1;
            }
            
            // 4. 강의 시간 추가
            String timeSql = "INSERT INTO lecture_time (course_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
            try {
                pstmt = dbManager.prepareStatement(timeSql);
                pstmt.setInt(1, courseId);
                pstmt.setString(2, dayOfWeek);
                pstmt.setString(3, startTime);
                pstmt.setString(4, endTime);
                pstmt.executeUpdate();
            } finally {
                if (pstmt != null) pstmt.close();
            }
            
            // 5. 수강신청 추가
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
            int enrollId = enrollmentDAO.addEnrollment(studentId, courseId);
            
            if (enrollId == -1) {
                dbManager.rollback();
                return -1;
            }
            
            dbManager.commit();
            return enrollId;
        } catch (SQLException e) {
            dbManager.rollback();
            throw e;
        }
    }
    
    /**
     * 강의 ID로 강의 정보 조회
     * @param courseId 강의 ID
     * @return Course 객체
     */
    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT c.course_id, c.subject_id, s.subject_name, c.professor_id, p.name as professor_name, " +
                     "c.classroom, c.semester " +
                     "FROM course c " +
                     "JOIN subject s ON c.subject_id = s.subject_id " +
                     "LEFT JOIN professor p ON c.professor_id = p.professor_id " +
                     "WHERE c.course_id = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setSubjectId(rs.getInt("subject_id"));
                course.setSubjectName(rs.getString("subject_name"));
                course.setProfessorId(rs.getInt("professor_id"));
                course.setProfessorName(rs.getString("professor_name"));
                course.setClassroom(rs.getString("classroom"));
                course.setSemester(rs.getString("semester"));
                return course;
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
}

