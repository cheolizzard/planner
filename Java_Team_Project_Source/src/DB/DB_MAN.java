package DB;

import java.sql.*;
import java.io.*;
import java.util.Properties;

/**
 * 데이터베이스 연결 및 관리 클래스
 * @author kkjjk
 */
public class DB_MAN {
    private String strDriver;
    private String strURL;
    private String strUser;
    private String strPWD;
    
    private Connection DB_con;  // DB Connection
    private Statement DB_stmt;  // To store a statement for DB Connection
    private ResultSet DB_rs;    // To store result of SQL Execution
    
    private static DB_MAN instance;  // 싱글톤 인스턴스
    
    /**
     * 싱글톤 패턴 - 인스턴스 가져오기
     * @return DB_MAN 인스턴스
     */
    public static DB_MAN getInstance() {
        if (instance == null) {
            instance = new DB_MAN();
        }
        return instance;
    }
    
    /**
     * 생성자 - 설정 파일에서 DB 정보 로드
     */
    private DB_MAN() {
        loadConfig();
        setUTF8Encoding();
    }
    
    /**
     * 설정 파일에서 데이터베이스 연결 정보 로드
     */
    private void loadConfig() {
        Properties props = new Properties();
        InputStream input = null;
        
        // 여러 경로에서 설정 파일 찾기 시도
        String[] possiblePaths = {
            "DB/dbconfig.properties",
            "/DB/dbconfig.properties",
            "dbconfig.properties"
        };
        
        for (String path : possiblePaths) {
            input = getClass().getClassLoader().getResourceAsStream(path);
            if (input != null) {
                break;
            }
        }
        
        // 파일 시스템에서도 시도 (개발 환경용)
        if (input == null) {
            try {
                File configFile = new File("src/DB/dbconfig.properties");
                if (configFile.exists()) {
                    input = new FileInputStream(configFile);
                }
            } catch (FileNotFoundException e) {
                // 무시하고 계속 진행
            }
        }
        
        if (input != null) {
            try {
                props.load(input);
                strDriver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                strURL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/scalable_todo_db?characterEncoding=UTF-8&serverTimezone=UTC");
                strUser = props.getProperty("db.user", "root");
                strPWD = props.getProperty("db.password", "");
                input.close();
                System.out.println("데이터베이스 설정 파일 로드 완료");
            } catch (IOException e) {
                System.err.println("설정 파일 로드 중 오류 발생: " + e.getMessage());
                setDefaultConfig();
            }
        } else {
            // 설정 파일이 없을 경우 기본값 사용
            System.out.println("설정 파일을 찾을 수 없습니다. 기본값을 사용합니다.");
            setDefaultConfig();
        }
    }
    
    /**
     * 기본 설정값 설정
     */
    private void setDefaultConfig() {
        strDriver = "com.mysql.cj.jdbc.Driver";
        strURL = "jdbc:mysql://localhost:3306/scalable_todo_db?characterEncoding=UTF-8&serverTimezone=UTC";
        strUser = "root";
        strPWD = "worldcup7!";
    }
    
    /**
     * 데이터베이스 연결 열기
     * @throws SQLException 데이터베이스 연결 실패 시
     * @throws ClassNotFoundException 드라이버 클래스를 찾을 수 없을 때
     */
    public void dbOpen() throws SQLException, ClassNotFoundException {
        try {
            Class.forName(strDriver);
            DB_con = DriverManager.getConnection(strURL, strUser, strPWD);
            DB_stmt = DB_con.createStatement();
            System.out.println("데이터베이스 연결 성공");
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            throw e;
        } catch (ClassNotFoundException e) {
            System.err.println("드라이버를 찾을 수 없습니다: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 데이터베이스 연결 닫기
     * 모든 리소스를 안전하게 닫습니다.
     */
    public void dbClose() {
        try {
            if (DB_rs != null) {
                DB_rs.close();
                DB_rs = null;
            }
            if (DB_stmt != null) {
                DB_stmt.close();
                DB_stmt = null;
            }
            if (DB_con != null && !DB_con.isClosed()) {
                DB_con.close();
                DB_con = null;
                System.out.println("데이터베이스 연결 종료");
            }
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 종료 중 오류 발생: " + e.getMessage());
        }
    }
    
    /**
     * UTF-8 인코딩 설정
     */
    private void setUTF8Encoding() {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("UTF-8 인코딩 설정 실패: " + e.getMessage());
        }
    }
    
    /**
     * Statement 실행 (SELECT 쿼리)
     * @param sql 실행할 SQL 쿼리
     * @return ResultSet 결과 집합
     * @throws SQLException SQL 실행 실패 시
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        if (DB_stmt == null || DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다. dbOpen()을 먼저 호출하세요.");
        }
        DB_rs = DB_stmt.executeQuery(sql);
        return DB_rs;
    }
    
    /**
     * Statement 실행 (INSERT, UPDATE, DELETE 쿼리)
     * @param sql 실행할 SQL 쿼리
     * @return 영향받은 행의 개수
     * @throws SQLException SQL 실행 실패 시
     */
    public int executeUpdate(String sql) throws SQLException {
        if (DB_stmt == null || DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다. dbOpen()을 먼저 호출하세요.");
        }
        return DB_stmt.executeUpdate(sql);
    }
    
    /**
     * PreparedStatement 생성 (SQL Injection 방지)
     * @param sql 실행할 SQL 쿼리 (파라미터는 ?로 표시)
     * @return PreparedStatement 객체
     * @throws SQLException PreparedStatement 생성 실패 시
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다. dbOpen()을 먼저 호출하세요.");
        }
        return DB_con.prepareStatement(sql);
    }
    
    /**
     * PreparedStatement 생성 (자동 생성 키 반환)
     * @param sql 실행할 SQL 쿼리
     * @return PreparedStatement 객체
     * @throws SQLException PreparedStatement 생성 실패 시
     */
    public PreparedStatement prepareStatementWithKeys(String sql) throws SQLException {
        if (DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다. dbOpen()을 먼저 호출하세요.");
        }
        return DB_con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }
    
    /**
     * 현재 ResultSet 가져오기
     * @return 현재 ResultSet
     */
    public ResultSet getResultSet() {
        return DB_rs;
    }
    
    /**
     * 현재 Connection 가져오기
     * @return 현재 Connection
     */
    public Connection getConnection() {
        return DB_con;
    }
    
    /**
     * 트랜잭션 시작 (자동 커밋 비활성화)
     * @throws SQLException 트랜잭션 시작 실패 시
     */
    public void beginTransaction() throws SQLException {
        if (DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다.");
        }
        DB_con.setAutoCommit(false);
    }
    
    /**
     * 트랜잭션 커밋
     * @throws SQLException 커밋 실패 시
     */
    public void commit() throws SQLException {
        if (DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다.");
        }
        DB_con.commit();
        DB_con.setAutoCommit(true);
    }
    
    /**
     * 트랜잭션 롤백
     * @throws SQLException 롤백 실패 시
     */
    public void rollback() throws SQLException {
        if (DB_con == null || DB_con.isClosed()) {
            throw new SQLException("데이터베이스 연결이 열려있지 않습니다.");
        }
        DB_con.rollback();
        DB_con.setAutoCommit(true);
    }
    
    /**
     * 연결 상태 확인
     * @return 연결되어 있으면 true, 아니면 false
     */
    public boolean isConnected() {
        try {
            return DB_con != null && !DB_con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
