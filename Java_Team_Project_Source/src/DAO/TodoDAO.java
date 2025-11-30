package DAO;

import DB.DB_MAN;
import Model.Todo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 할일 데이터 접근 객체
 */
public class TodoDAO {
    private DB_MAN dbManager;
    
    public TodoDAO() {
        this.dbManager = DB_MAN.getInstance();
    }
    
    /**
     * 할일 추가
     * @param todo 할일 정보
     * @return 생성된 할일 ID, 실패 시 -1
     */
    public int addTodo(Todo todo) throws SQLException {
        String sql = "INSERT INTO todo_list (student_id, enroll_id, custom_category, title, content, start_datetime, end_datetime, is_completed, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatementWithKeys(sql);
            pstmt.setString(1, todo.getStudentId());
            
            if (todo.getEnrollId() != null) {
                pstmt.setInt(2, todo.getEnrollId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            pstmt.setString(3, todo.getCustomCategory());
            pstmt.setString(4, todo.getTitle());
            pstmt.setString(5, todo.getContent());
            pstmt.setString(6, todo.getStartDatetime());
            pstmt.setString(7, todo.getEndDatetime());
            pstmt.setBoolean(8, todo.isCompleted());
            pstmt.setString(9, todo.getStatus() != null ? todo.getStatus() : "미완료");
            
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
     * 할일 수정
     * @param todo 할일 정보
     * @return 성공 시 true
     */
    public boolean updateTodo(Todo todo) throws SQLException {
        String sql = "UPDATE todo_list SET enroll_id = ?, custom_category = ?, title = ?, content = ?, " +
                     "start_datetime = ?, end_datetime = ?, is_completed = ?, status = ? " +
                     "WHERE todo_id = ? AND student_id = ?";
        
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            
            if (todo.getEnrollId() != null) {
                pstmt.setInt(1, todo.getEnrollId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            
            pstmt.setString(2, todo.getCustomCategory());
            pstmt.setString(3, todo.getTitle());
            pstmt.setString(4, todo.getContent());
            pstmt.setString(5, todo.getStartDatetime());
            pstmt.setString(6, todo.getEndDatetime());
            pstmt.setBoolean(7, todo.isCompleted());
            pstmt.setString(8, todo.getStatus() != null ? todo.getStatus() : "미완료");
            pstmt.setInt(9, todo.getTodoId());
            pstmt.setString(10, todo.getStudentId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 할일 삭제
     * @param todoId 할일 ID
     * @param studentId 학번 (권한 확인용)
     * @return 성공 시 true
     */
    public boolean deleteTodo(int todoId, String studentId) throws SQLException {
        String sql = "DELETE FROM todo_list WHERE todo_id = ? AND student_id = ?";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, todoId);
            pstmt.setString(2, studentId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * enroll_id로 연결된 모든 할일 삭제 (과목 삭제 시 사용)
     * @param enrollId 수강신청 ID
     * @return 삭제된 할일 개수
     */
    public int deleteTodosByEnrollId(int enrollId) throws SQLException {
        String sql = "DELETE FROM todo_list WHERE enroll_id = ?";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, enrollId);
            int result = pstmt.executeUpdate();
            return result;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 학생의 모든 할일 조회
     * @param studentId 학번
     * @return 할일 목록
     */
    public List<Todo> getTodosByStudentId(String studentId) throws SQLException {
        String sql = "SELECT t.todo_id, t.student_id, t.enroll_id, t.custom_category, t.title, t.content, " +
                     "t.start_datetime, t.end_datetime, t.is_completed, t.status, " +
                     "COALESCE(s.subject_name, t.custom_category, '기타') as category_name " +
                     "FROM todo_list t " +
                     "LEFT JOIN enrollment e ON t.enroll_id = e.enroll_id " +
                     "LEFT JOIN course c ON e.course_id = c.course_id " +
                     "LEFT JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE t.student_id = ? " +
                     "ORDER BY t.start_datetime, t.title";
        
        List<Todo> todos = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Todo todo = mapResultSetToTodo(rs);
                todos.add(todo);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return todos;
    }
    
    /**
     * 날짜별 할일 조회
     * @param studentId 학번
     * @param date 날짜 (YYYY-MM-DD 형식)
     * @return 해당 날짜의 할일 목록
     */
    public List<Todo> getTodosByDate(String studentId, String date) throws SQLException {
        String sql = "SELECT t.todo_id, t.student_id, t.enroll_id, t.custom_category, t.title, t.content, " +
                     "t.start_datetime, t.end_datetime, t.is_completed, t.status, " +
                     "COALESCE(s.subject_name, t.custom_category, '기타') as category_name " +
                     "FROM todo_list t " +
                     "LEFT JOIN enrollment e ON t.enroll_id = e.enroll_id " +
                     "LEFT JOIN course c ON e.course_id = c.course_id " +
                     "LEFT JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE t.student_id = ? " +
                     "AND (t.start_datetime LIKE ? OR t.end_datetime LIKE ? OR " +
                     "     (t.start_datetime IS NULL AND t.end_datetime IS NULL)) " +
                     "ORDER BY t.start_datetime, t.title";
        
        List<Todo> todos = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            String datePattern = date + "%";
            pstmt.setString(2, datePattern);
            pstmt.setString(3, datePattern);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Todo todo = mapResultSetToTodo(rs);
                todos.add(todo);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return todos;
    }
    
    /**
     * 카테고리별 할일 조회
     * @param studentId 학번
     * @param enrollId 수강신청 ID (과목 관련) 또는 null (사용자 정의 카테고리)
     * @param customCategory 사용자 정의 카테고리명
     * @return 할일 목록
     */
    public List<Todo> getTodosByCategory(String studentId, Integer enrollId, String customCategory) throws SQLException {
        String sql = "SELECT t.todo_id, t.student_id, t.enroll_id, t.custom_category, t.title, t.content, " +
                     "t.start_datetime, t.end_datetime, t.is_completed, t.status, " +
                     "COALESCE(s.subject_name, t.custom_category, '기타') as category_name " +
                     "FROM todo_list t " +
                     "LEFT JOIN enrollment e ON t.enroll_id = e.enroll_id " +
                     "LEFT JOIN course c ON e.course_id = c.course_id " +
                     "LEFT JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE t.student_id = ? ";
        
        if (enrollId != null) {
            sql += "AND t.enroll_id = ? ";
        } else if (customCategory != null && !customCategory.isEmpty()) {
            sql += "AND t.custom_category = ? ";
        } else {
            sql += "AND t.enroll_id IS NULL AND (t.custom_category IS NULL OR t.custom_category = '') ";
        }
        
        sql += "ORDER BY t.start_datetime, t.title";
        
        List<Todo> todos = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            if (enrollId != null) {
                pstmt.setInt(2, enrollId);
            } else if (customCategory != null && !customCategory.isEmpty()) {
                pstmt.setString(2, customCategory);
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Todo todo = mapResultSetToTodo(rs);
                todos.add(todo);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return todos;
    }
    
    /**
     * 할일 ID로 조회
     * @param todoId 할일 ID
     * @param studentId 학번 (권한 확인용)
     * @return Todo 객체
     */
    public Todo getTodoById(int todoId, String studentId) throws SQLException {
        String sql = "SELECT t.todo_id, t.student_id, t.enroll_id, t.custom_category, t.title, t.content, " +
                     "t.start_datetime, t.end_datetime, t.is_completed, t.status, " +
                     "COALESCE(s.subject_name, t.custom_category, '기타') as category_name " +
                     "FROM todo_list t " +
                     "LEFT JOIN enrollment e ON t.enroll_id = e.enroll_id " +
                     "LEFT JOIN course c ON e.course_id = c.course_id " +
                     "LEFT JOIN subject s ON c.subject_id = s.subject_id " +
                     "WHERE t.todo_id = ? AND t.student_id = ?";
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setInt(1, todoId);
            pstmt.setString(2, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTodo(rs);
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * 완료 여부 업데이트
     * @param todoId 할일 ID
     * @param studentId 학번
     * @param isCompleted 완료 여부
     * @return 성공 시 true
     */
    public boolean updateCompletionStatus(int todoId, String studentId, boolean isCompleted) throws SQLException {
        String status = isCompleted ? "완료" : "미완료";
        return updateStatus(todoId, studentId, status);
    }
    
    /**
     * 상태 업데이트
     * @param todoId 할일 ID
     * @param studentId 학번
     * @param status 상태 (미완료, 진행중, 완료)
     * @return 성공 시 true
     */
    public boolean updateStatus(int todoId, String studentId, String status) throws SQLException {
        String sql = "UPDATE todo_list SET status = ?, is_completed = ? WHERE todo_id = ? AND student_id = ?";
        PreparedStatement pstmt = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setBoolean(2, "완료".equals(status));
            pstmt.setInt(3, todoId);
            pstmt.setString(4, studentId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }
    
    /**
     * ResultSet을 Todo 객체로 변환
     */
    private Todo mapResultSetToTodo(ResultSet rs) throws SQLException {
        Todo todo = new Todo();
        todo.setTodoId(rs.getInt("todo_id"));
        todo.setStudentId(rs.getString("student_id"));
        
        int enrollId = rs.getInt("enroll_id");
        if (!rs.wasNull()) {
            todo.setEnrollId(enrollId);
        }
        
        todo.setCustomCategory(rs.getString("custom_category"));
        todo.setTitle(rs.getString("title"));
        todo.setContent(rs.getString("content"));
        todo.setStartDatetime(rs.getString("start_datetime"));
        todo.setEndDatetime(rs.getString("end_datetime"));
        todo.setCompleted(rs.getBoolean("is_completed"));
        todo.setStatus(rs.getString("status"));
        todo.setCategoryName(rs.getString("category_name"));
        
        return todo;
    }
    
    /**
     * 학생의 사용자 정의 카테고리 목록 조회
     * @param studentId 학번
     * @return 사용자 정의 카테고리 목록 (중복 제거)
     */
    public List<String> getCustomCategories(String studentId) throws SQLException {
        String sql = "SELECT DISTINCT custom_category " +
                     "FROM todo_list " +
                     "WHERE student_id = ? " +
                     "AND enroll_id IS NULL " +
                     "AND custom_category IS NOT NULL " +
                     "AND custom_category != '' " +
                     "AND custom_category != '기타' " +
                     "ORDER BY custom_category";
        
        List<String> categories = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = dbManager.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("custom_category");
                if (category != null && !category.isEmpty()) {
                    categories.add(category);
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        }
        
        return categories;
    }
}

