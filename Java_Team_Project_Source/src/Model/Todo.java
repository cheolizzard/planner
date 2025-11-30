package Model;

/**
 * 할일 정보 모델 클래스
 */
public class Todo {
    private int todoId;            // 할일 ID
    private String studentId;      // 학번
    private Integer enrollId;      // 수강신청 ID (NULL 가능 - 과목 관련 할일)
    private String customCategory;  // 사용자 정의 카테고리 (enroll_id가 NULL일 때 사용)
    private String title;          // 제목 (필수)
    private String content;        // 내용
    private String startDatetime;  // 시작 시간 (NULL 가능)
    private String endDatetime;    // 끝 시간 (NULL 가능)
    private boolean isCompleted;   // 완료 여부
    private String status;         // 할일 상태 (미완료, 진행중, 완료)
    
    // 조회용 필드
    private String categoryName;   // 카테고리명 (과목명 또는 사용자 정의 카테고리)
    
    public Todo() {
    }
    
    public Todo(String studentId, Integer enrollId, String customCategory, 
                String title, String content, String startDatetime, String endDatetime) {
        this.studentId = studentId;
        this.enrollId = enrollId;
        this.customCategory = customCategory;
        this.title = title;
        this.content = content;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.isCompleted = false;
        this.status = "미완료";
    }
    
    // Getters and Setters
    public int getTodoId() {
        return todoId;
    }
    
    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public Integer getEnrollId() {
        return enrollId;
    }
    
    public void setEnrollId(Integer enrollId) {
        this.enrollId = enrollId;
    }
    
    public String getCustomCategory() {
        return customCategory;
    }
    
    public void setCustomCategory(String customCategory) {
        this.customCategory = customCategory;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getStartDatetime() {
        return startDatetime;
    }
    
    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }
    
    public String getEndDatetime() {
        return endDatetime;
    }
    
    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
        // isCompleted가 변경되면 status도 업데이트
        if (completed && (status == null || !status.equals("완료"))) {
            this.status = "완료";
        }
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        // status가 '완료'로 변경되면 isCompleted도 true
        if ("완료".equals(status)) {
            this.isCompleted = true;
        }
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    /**
     * 카테고리명 가져오기 (과목명 또는 사용자 정의 카테고리)
     */
    public String getDisplayCategory() {
        if (categoryName != null && !categoryName.isEmpty()) {
            return categoryName;
        }
        if (customCategory != null && !customCategory.isEmpty()) {
            return customCategory;
        }
        return "기타";
    }
    
    @Override
    public String toString() {
        return "Todo{" +
                "todoId=" + todoId +
                ", title='" + title + '\'' +
                ", categoryName='" + getDisplayCategory() + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}

