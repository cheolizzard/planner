package GUI;

/**
 * 자식(TodoCardPanel)이 부모(MainFrame)에게
 * "버튼이 눌렸다"는 사실을 알리기 위해 사용하는 인터페이스(신호 체계)
 */
public interface TodoActionCallback {
    
    // 수정 버튼이 눌렸을 때 호출할 메서드 (어떤 ID인지만 알려줌)
    void onEdit(int todoId);
    
    // 삭제 버튼이 눌렸을 때 호출할 메서드
    void onDelete(int todoId);
    
}
