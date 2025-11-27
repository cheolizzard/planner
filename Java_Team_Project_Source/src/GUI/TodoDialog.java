/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package GUI;

import DAO.EnrollmentDAO;
import DAO.TodoDAO;
import Model.Enrollment;
import Model.Todo;
import Util.UIHelper;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author kkjjk
 */
public class TodoDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TodoDialog.class.getName());
    private TodoDAO todoDAO;
    private EnrollmentDAO enrollmentDAO;
    private String studentId;
    private Todo editTodo; // 수정 모드일 때 사용
    private boolean isEditMode;

    /**
     * Creates new form TodoDialog (테스트용 기본 생성자)
     */
    public TodoDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, "202444085", null);
    }
    
    /**
     * Creates new form TodoDialog (추가 모드)
     */
    public TodoDialog(java.awt.Frame parent, boolean modal, String studentId, Todo editTodo) {
        super(parent, modal);
        this.studentId = studentId;
        this.editTodo = editTodo;
        this.isEditMode = (editTodo != null);
        this.todoDAO = new TodoDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        initComponents();
        // applyStyles(); // 필요시 주석 해제하여 사용
        setupEventHandlers();
        setupComboBoxes();
        loadCategories();
        
        if (isEditMode) {
            loadTodoData();
            setTitle("할일 수정");
        } else {
            setTitle("할일 추가");
        }
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        btnSaveTodo.addActionListener(e -> handleSave());
        btnCancelTodo.addActionListener(e -> dispose());
        btnAddCategory.addActionListener(e -> handleAddCategory());
    }
    
    /**
     * 콤보박스 설정
     */
    private void setupComboBoxes() {
        // 시간 콤보박스 설정 (날짜+시간 형식은 나중에 개선)
        // 일단 간단하게 시간만
        String[] times = new String[25];
        for (int i = 0; i < 25; i++) {
            int hour = i;
            times[i] = String.format("%02d:00", hour);
        }
        cboTodoStartTime.setModel(new javax.swing.DefaultComboBoxModel<>(times));
        cboTodoEndTime.setModel(new javax.swing.DefaultComboBoxModel<>(times));
    }
    
    /**
     * 카테고리 목록 로드
     */
    private void loadCategories() {
        try {
            // 수강신청된 과목 목록 가져오기
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudentId(studentId);
            
            cboCategory.removeAllItems();
            cboCategory.addItem("기타");
            
            for (Enrollment enrollment : enrollments) {
                cboCategory.addItem(enrollment.getSubjectName());
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "카테고리 로드 실패", e);
        }
    }
    
    /**
     * 수정 모드일 때 기존 데이터 로드
     */
    private void loadTodoData() {
        txtTodoTitle.setText(editTodo.getTitle());
        taTodoContent.setText(editTodo.getContent());
        
        // 시간 설정 (간단화 - 실제로는 날짜+시간 파싱 필요)
        if (editTodo.getStartDatetime() != null && !editTodo.getStartDatetime().isEmpty()) {
            // 시간 부분만 추출 (HH:mm 형식)
            String startTime = editTodo.getStartDatetime().substring(11, 16);
            cboTodoStartTime.setSelectedItem(startTime);
        }
        
        if (editTodo.getEndDatetime() != null && !editTodo.getEndDatetime().isEmpty()) {
            String endTime = editTodo.getEndDatetime().substring(11, 16);
            cboTodoEndTime.setSelectedItem(endTime);
        }
        
        // 카테고리 설정
        String categoryName = editTodo.getDisplayCategory();
        for (int i = 0; i < cboCategory.getItemCount(); i++) {
            if (cboCategory.getItemAt(i).equals(categoryName)) {
                cboCategory.setSelectedIndex(i);
                break;
            }
        }
    }
    
    /**
     * 저장 처리
     */
    private void handleSave() {
        String title = txtTodoTitle.getText().trim();
        String content = taTodoContent.getText().trim();
        String startTime = (String) cboTodoStartTime.getSelectedItem();
        String endTime = (String) cboTodoEndTime.getSelectedItem();
        String category = (String) cboCategory.getSelectedItem();
        
        // 유효성 검사
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "제목을 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtTodoTitle.requestFocus();
            return;
        }
        
        try {
            Todo todo = new Todo();
            todo.setStudentId(studentId);
            todo.setTitle(title);
            todo.setContent(content);
            
            // 카테고리 설정
            if (category != null && !category.equals("기타")) {
                // 과목 카테고리인 경우 enroll_id 찾기
                List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudentId(studentId);
                for (Enrollment enrollment : enrollments) {
                    if (enrollment.getSubjectName().equals(category)) {
                        todo.setEnrollId(enrollment.getEnrollId());
                        break;
                    }
                }
            } else {
                // 사용자 정의 카테고리
                todo.setCustomCategory("기타");
            }
            
            // 시간 설정 (간단화 - 오늘 날짜로 설정)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new Date());
            
            if (startTime != null) {
                todo.setStartDatetime(today + " " + startTime + ":00");
            }
            if (endTime != null) {
                todo.setEndDatetime(today + " " + endTime + ":00");
            }
            
            if (isEditMode) {
                // 수정 모드
                todo.setTodoId(editTodo.getTodoId());
                todo.setCompleted(editTodo.isCompleted());
                boolean success = todoDAO.updateTodo(todo);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "할일이 수정되었습니다.", 
                        "수정 완료", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "할일 수정에 실패했습니다.", 
                        "오류", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 추가 모드
                int todoId = todoDAO.addTodo(todo);
                if (todoId > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "할일이 추가되었습니다.", 
                        "추가 완료", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "할일 추가에 실패했습니다.", 
                        "오류", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "할일 저장 실패", e);
            JOptionPane.showMessageDialog(this, 
                "할일 저장 중 오류가 발생했습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 카테고리 추가 처리
     */
    private void handleAddCategory() {
        String newCategory = JOptionPane.showInputDialog(this, 
            "새 카테고리 이름을 입력하세요:", 
            "카테고리 추가", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            cboCategory.addItem(newCategory.trim());
            cboCategory.setSelectedItem(newCategory.trim());
        }
    }
    
    /**
     * UI 스타일 적용
     */
    private void applyStyles() {
        // 배경색 설정
        getContentPane().setBackground(Color.WHITE);
        
        // 레이블 스타일
        lblTodoTitle.setFont(UIHelper.BODY_FONT);
        lblTodoTitle.setForeground(UIHelper.TEXT_COLOR);
        lblTodoContent.setFont(UIHelper.BODY_FONT);
        lblTodoContent.setForeground(UIHelper.TEXT_COLOR);
        lblTodoStartTime.setFont(UIHelper.BODY_FONT);
        lblTodoStartTime.setForeground(UIHelper.TEXT_COLOR);
        lblTodoEndTime.setFont(UIHelper.BODY_FONT);
        lblTodoEndTime.setForeground(UIHelper.TEXT_COLOR);
        lblCategory.setFont(UIHelper.BODY_FONT);
        lblCategory.setForeground(UIHelper.TEXT_COLOR);
        
        // 입력 필드 스타일
        UIHelper.styleTextField(txtTodoTitle);
        UIHelper.styleComboBox(cboTodoStartTime);
        UIHelper.styleComboBox(cboTodoEndTime);
        UIHelper.styleComboBox(cboCategory);
        
        // 텍스트 영역 스타일
        taTodoContent.setFont(UIHelper.BODY_FONT);
        taTodoContent.setBorder(UIHelper.INPUT_BORDER);
        taTodoContent.setBackground(Color.WHITE);
        taTodoContent.setForeground(UIHelper.TEXT_COLOR);
        
        // 버튼 스타일
        UIHelper.stylePrimaryButton(btnSaveTodo);
        UIHelper.styleSecondaryButton(btnCancelTodo);
        UIHelper.styleSecondaryButton(btnAddCategory);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTodoTitle = new javax.swing.JLabel();
        lblTodoContent = new javax.swing.JLabel();
        lblTodoStartTime = new javax.swing.JLabel();
        lblTodoEndTime = new javax.swing.JLabel();
        lblCategory = new javax.swing.JLabel();
        txtTodoTitle = new javax.swing.JTextField();
        cboTodoEndTime = new javax.swing.JComboBox<>();
        cboTodoStartTime = new javax.swing.JComboBox<>();
        cboCategory = new javax.swing.JComboBox<>();
        btnAddCategory = new javax.swing.JButton();
        btnSaveTodo = new javax.swing.JButton();
        btnCancelTodo = new javax.swing.JButton();
        taTodoContent = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblTodoTitle.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblTodoTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTodoTitle.setText("제목:");
        lblTodoTitle.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblTodoContent.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblTodoContent.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTodoContent.setText("내용:");
        lblTodoContent.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblTodoStartTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblTodoStartTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTodoStartTime.setText("시작 시간:");
        lblTodoStartTime.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblTodoEndTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblTodoEndTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTodoEndTime.setText("끝 시간:");
        lblTodoEndTime.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblCategory.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblCategory.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCategory.setText("카테고리:");
        lblCategory.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        txtTodoTitle.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboTodoEndTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboTodoStartTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboCategory.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        btnAddCategory.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnAddCategory.setText("+추가");

        btnSaveTodo.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnSaveTodo.setText("저장");

        btnCancelTodo.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnCancelTodo.setText("취소");

        taTodoContent.setColumns(20);
        taTodoContent.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        taTodoContent.setRows(5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTodoTitle)
                    .addComponent(lblTodoContent)
                    .addComponent(lblTodoStartTime)
                    .addComponent(lblTodoEndTime)
                    .addComponent(lblCategory))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTodoTitle)
                    .addComponent(taTodoContent, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(cboTodoStartTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTodoEndTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddCategory)))
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(btnSaveTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTodoTitle)
                    .addComponent(txtTodoTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taTodoContent, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTodoContent))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTodoStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTodoStartTime))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTodoEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTodoEndTime))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCategory)
                    .addComponent(lblCategory))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TodoDialog dialog = new TodoDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCategory;
    private javax.swing.JButton btnCancelTodo;
    private javax.swing.JButton btnSaveTodo;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboTodoEndTime;
    private javax.swing.JComboBox<String> cboTodoStartTime;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblTodoContent;
    private javax.swing.JLabel lblTodoEndTime;
    private javax.swing.JLabel lblTodoStartTime;
    private javax.swing.JLabel lblTodoTitle;
    private javax.swing.JTextArea taTodoContent;
    private javax.swing.JTextField txtTodoTitle;
    // End of variables declaration//GEN-END:variables
}
