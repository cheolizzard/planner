package DB;

import java.sql.*;

/**
 * DB_MAN 사용 예제 클래스
 * 이 파일은 참고용이며, 실제 프로젝트에서는 삭제해도 됩니다.
 */
public class DB_MAN_Example {
    
    public static void main(String[] args) {
        // 싱글톤 인스턴스 가져오기
        DB_MAN dbManager = DB_MAN.getInstance();
        
        try {
            // 1. 데이터베이스 연결
            dbManager.dbOpen();
            
            // 2. 일반 Statement 사용 예제
            String sql = "SELECT * FROM users WHERE id = 'test'";
            ResultSet rs = dbManager.executeQuery(sql);
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("id"));
                System.out.println("Name: " + rs.getString("name"));
            }
            rs.close();
            
            // 3. PreparedStatement 사용 예제 (권장 - SQL Injection 방지)
            String preparedSql = "SELECT * FROM users WHERE id = ? AND password = ?";
            PreparedStatement pstmt = dbManager.prepareStatement(preparedSql);
            pstmt.setString(1, "test");
            pstmt.setString(2, "password123");
            ResultSet rs2 = pstmt.executeQuery();
            while (rs2.next()) {
                System.out.println("User found: " + rs2.getString("name"));
            }
            rs2.close();
            pstmt.close();
            
            // 4. INSERT 예제 (PreparedStatement 사용)
            String insertSql = "INSERT INTO users (id, name, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = dbManager.prepareStatementWithKeys(insertSql);
            insertStmt.setString(1, "newuser");
            insertStmt.setString(2, "홍길동");
            insertStmt.setString(3, "password123");
            int affectedRows = insertStmt.executeUpdate();
            System.out.println("영향받은 행 수: " + affectedRows);
            
            // 자동 생성된 키 가져오기
            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newId = generatedKeys.getInt(1);
                System.out.println("생성된 ID: " + newId);
            }
            generatedKeys.close();
            insertStmt.close();
            
            // 5. 트랜잭션 예제
            dbManager.beginTransaction();
            try {
                // 여러 쿼리 실행
                dbManager.executeUpdate("UPDATE users SET name = '김철수' WHERE id = 'test1'");
                dbManager.executeUpdate("UPDATE users SET name = '이영희' WHERE id = 'test2'");
                
                // 모든 작업이 성공하면 커밋
                dbManager.commit();
                System.out.println("트랜잭션 커밋 완료");
            } catch (SQLException e) {
                // 오류 발생 시 롤백
                dbManager.rollback();
                System.err.println("트랜잭션 롤백: " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.err.println("데이터베이스 오류: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("드라이버를 찾을 수 없습니다: " + e.getMessage());
        } finally {
            // 6. 항상 연결 닫기
            dbManager.dbClose();
        }
    }
}

