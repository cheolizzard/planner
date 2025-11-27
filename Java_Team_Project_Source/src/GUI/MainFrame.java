package GUI;

import DAO.CourseDAO;
import DAO.EnrollmentDAO;
import DAO.TodoDAO;
import DB.DB_MAN;
import Model.Course;
import Model.LectureTime;
import Model.Student;
import Model.Todo;
import Util.UIHelper;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author kkjjk
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private Student currentStudent;
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;
    private TodoDAO todoDAO;
    private List<Course> courseList;
    private List<Todo> todoList;
    private DB_MAN dbManager;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
    }
    
    /**
     * 로그인한 학생 정보와 함께 MainFrame 생성
     */
    public MainFrame(Student student) {
        initComponents();
        this.currentStudent = student;
        // applyStyles(); // 필요시 주석 해제하여 사용
        initDatabase();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.todoDAO = new TodoDAO();
        setTitle("NexPlan - " + student.getName() + "님 환영합니다");
        setupEventHandlers();
        initCalendarPanel();
        loadCourseList();
        loadTodoList();
    }
    
    /**
     * UI 스타일 적용
     */
    private void applyStyles() {
        // 배경색 설정
        getContentPane().setBackground(UIHelper.BACKGROUND_COLOR);
        pnlTimetable.setBackground(Color.WHITE);
        pnlCalendarTodo.setBackground(Color.WHITE);
        
        // 레이블 스타일
        lblTimetable.setFont(UIHelper.HEADING_FONT);
        lblTimetable.setForeground(UIHelper.TEXT_COLOR);
        lblSubjects.setFont(UIHelper.HEADING_FONT);
        lblSubjects.setForeground(UIHelper.TEXT_COLOR);
        
        // 리스트 스타일
        lstSubjects.setFont(UIHelper.BODY_FONT);
        lstSubjects.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // 테이블 스타일
        tblTimetable.setFont(UIHelper.BODY_FONT);
        tblTimetable.setRowHeight(30);
        tblTimetable.setGridColor(UIHelper.BORDER_COLOR);
        tblTimetable.setShowGrid(true);
        tblTimetable.getTableHeader().setFont(UIHelper.BODY_FONT);
        tblTimetable.getTableHeader().setBackground(UIHelper.BACKGROUND_COLOR);
        tblTimetable.getTableHeader().setForeground(UIHelper.TEXT_COLOR);
        
        // 버튼 스타일
        UIHelper.stylePrimaryButton(btnAddSubject);
        UIHelper.styleSecondaryButton(btnEditSubject);
        UIHelper.styleSecondaryButton(btnDeleteSubject);
    }
    
    /**
     * 데이터베이스 연결 확인 및 초기화
     */
    private void initDatabase() {
        try {
            dbManager = DB_MAN.getInstance();
            // 이미 연결되어 있으면 그대로 사용, 없으면 연결
            if (!dbManager.isConnected()) {
                dbManager.dbOpen();
            }
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "데이터베이스 연결 실패", e);
            JOptionPane.showMessageDialog(this, 
                "데이터베이스 연결에 실패했습니다.", 
                "연결 오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 시간표 관리 탭
        btnAddSubject.addActionListener(e -> handleAddSubject());
        btnEditSubject.addActionListener(e -> handleEditSubject());
        btnDeleteSubject.addActionListener(e -> handleDeleteSubject());
        
        // 탭 변경 이벤트
        tabMain.addChangeListener(e -> {
            if (tabMain.getSelectedIndex() == 1) {
                // 캘린더/할일 탭 선택 시
                loadTodoList();
            }
        });
    }
    
    /**
     * 캘린더 패널 초기화
     */
    private void initCalendarPanel() {
        // 패널에 기본 레이아웃 설정
        pnlCalendarTodo.setLayout(new java.awt.BorderLayout());
        
        // 상단: 날짜 선택 및 버튼
        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        
        javax.swing.JLabel lblDate = new javax.swing.JLabel("날짜: ");
        javax.swing.JTextField txtDate = new javax.swing.JTextField(10);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        txtDate.setText(dateFormat.format(new Date()));
        txtDate.setEditable(false);
        
        javax.swing.JButton btnAddTodo = new javax.swing.JButton("할일 추가");
        btnAddTodo.addActionListener(e -> handleAddTodo());
        
        topPanel.add(lblDate);
        topPanel.add(txtDate);
        topPanel.add(btnAddTodo);
        
        // 중앙: 할일 리스트
        javax.swing.JList<String> todoListDisplay = new javax.swing.JList<>();
        todoListDisplay.setFont(new java.awt.Font("맑은 고딕", 0, 14));
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(todoListDisplay);
        
        // 하단: 버튼
        javax.swing.JPanel bottomPanel = new javax.swing.JPanel();
        bottomPanel.setLayout(new java.awt.FlowLayout());
        
        javax.swing.JButton btnEditTodo = new javax.swing.JButton("수정");
        btnEditTodo.addActionListener(e -> handleEditTodo(todoListDisplay));
        
        javax.swing.JButton btnDeleteTodo = new javax.swing.JButton("삭제");
        btnDeleteTodo.addActionListener(e -> handleDeleteTodo(todoListDisplay));
        
        javax.swing.JCheckBox chkShowCompleted = new javax.swing.JCheckBox("완료된 할일 표시");
        chkShowCompleted.addActionListener(e -> loadTodoList());
        
        bottomPanel.add(btnEditTodo);
        bottomPanel.add(btnDeleteTodo);
        bottomPanel.add(chkShowCompleted);
        
        // 패널에 추가
        pnlCalendarTodo.add(topPanel, java.awt.BorderLayout.NORTH);
        pnlCalendarTodo.add(scrollPane, java.awt.BorderLayout.CENTER);
        pnlCalendarTodo.add(bottomPanel, java.awt.BorderLayout.SOUTH);
        
        // 참조 저장 (나중에 사용)
        // TODO: 멤버 변수로 저장하거나 다른 방식으로 관리
    }
    
    /**
     * 할일 목록 로드
     */
    private void loadTodoList() {
        try {
            todoList = todoDAO.getTodosByStudentId(currentStudent.getStudentId());
            updateTodoListDisplay();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "할일 목록 로드 실패", e);
            JOptionPane.showMessageDialog(this, 
                "할일 목록을 불러오는 중 오류가 발생했습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 할일 리스트 표시 업데이트
     */
    private void updateTodoListDisplay() {
        // TODO: 실제 리스트 컴포넌트에 연결
        // 현재는 간단한 구현
    }
    
    /**
     * 할일 추가 처리
     */
    private void handleAddTodo() {
        TodoDialog dialog = new TodoDialog(this, true, currentStudent.getStudentId(), null);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        loadTodoList(); // 목록 새로고침
    }
    
    /**
     * 할일 수정 처리
     */
    private void handleEditTodo(javax.swing.JList<String> todoListDisplay) {
        int selectedIndex = todoListDisplay.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "수정할 할일을 선택하세요.", 
                "선택 오류", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedIndex < todoList.size()) {
            Todo selectedTodo = todoList.get(selectedIndex);
            TodoDialog dialog = new TodoDialog(this, true, currentStudent.getStudentId(), selectedTodo);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            loadTodoList(); // 목록 새로고침
        }
    }
    
    /**
     * 할일 삭제 처리
     */
    private void handleDeleteTodo(javax.swing.JList<String> todoListDisplay) {
        int selectedIndex = todoListDisplay.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "삭제할 할일을 선택하세요.", 
                "선택 오류", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedIndex < todoList.size()) {
            Todo selectedTodo = todoList.get(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "정말 '" + selectedTodo.getTitle() + "' 할일을 삭제하시겠습니까?", 
                "삭제 확인", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean success = todoDAO.deleteTodo(selectedTodo.getTodoId(), currentStudent.getStudentId());
                    if (success) {
                        JOptionPane.showMessageDialog(this, 
                            "할일이 삭제되었습니다.", 
                            "삭제 완료", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadTodoList();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "할일 삭제에 실패했습니다.", 
                            "오류", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    logger.log(java.util.logging.Level.SEVERE, "할일 삭제 실패", e);
                    JOptionPane.showMessageDialog(this, 
                        "할일 삭제 중 오류가 발생했습니다.", 
                        "오류", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * 과목 목록 로드
     */
    private void loadCourseList() {
        try {
            courseList = courseDAO.getCoursesByStudentId(currentStudent.getStudentId());
            updateSubjectList();
            updateTimetable();
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "과목 목록 로드 실패", e);
            JOptionPane.showMessageDialog(this, 
                "과목 목록을 불러오는 중 오류가 발생했습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 과목 리스트 업데이트
     */
    private void updateSubjectList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (Course course : courseList) {
            model.addElement(course.getSubjectName() + " - " + course.getProfessorName());
        }
        lstSubjects.setModel(model);
    }
    
    /**
     * 시간표 업데이트
     */
    private void updateTimetable() {
        // 시간표 테이블 모델 설정
        String[] columns = {"시간", "월", "화", "수", "목", "금"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // 시간대별로 데이터 구성 (간단한 예시)
        // 실제로는 lecture_time 데이터를 기반으로 구성해야 함
        model.addRow(new Object[]{"09:00-12:00", "", "", "", "", ""});
        model.addRow(new Object[]{"13:00-16:00", "", "", "", "", ""});
        
        tblTimetable.setModel(model);
    }
    
    /**
     * 과목 추가 처리
     */
    private void handleAddSubject() {
        SubjectDialog dialog = new SubjectDialog(this, true, currentStudent.getStudentId(), null);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        loadCourseList(); // 목록 새로고침
    }
    
    /**
     * 과목 수정 처리
     */
    private void handleEditSubject() {
        int selectedIndex = lstSubjects.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "수정할 과목을 선택하세요.", 
                "선택 오류", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Course selectedCourse = courseList.get(selectedIndex);
        SubjectDialog dialog = new SubjectDialog(this, true, currentStudent.getStudentId(), selectedCourse);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        loadCourseList(); // 목록 새로고침
    }
    
    /**
     * 과목 삭제 처리
     */
    private void handleDeleteSubject() {
        int selectedIndex = lstSubjects.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, 
                "삭제할 과목을 선택하세요.", 
                "선택 오류", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Course selectedCourse = courseList.get(selectedIndex);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "정말 " + selectedCourse.getSubjectName() + " 과목을 삭제하시겠습니까?\n" +
            "해당 과목의 할일도 함께 삭제됩니다.", 
            "삭제 확인", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // enrollment 삭제 (CASCADE로 course, lecture_time도 삭제됨)
                // 먼저 enroll_id 찾기
                var enrollments = enrollmentDAO.getEnrollmentsByStudentId(currentStudent.getStudentId());
                for (var enrollment : enrollments) {
                    if (enrollment.getCourseId() == selectedCourse.getCourseId()) {
                        enrollmentDAO.deleteEnrollment(enrollment.getEnrollId());
                        break;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    selectedCourse.getSubjectName() + " 과목이 삭제되었습니다.", 
                    "삭제 완료", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadCourseList();
            } catch (SQLException e) {
                logger.log(java.util.logging.Level.SEVERE, "과목 삭제 실패", e);
                JOptionPane.showMessageDialog(this, 
                    "과목 삭제 중 오류가 발생했습니다.", 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabMain = new javax.swing.JTabbedPane();
        pnlTimetable = new javax.swing.JPanel();
        tblTimetable = new javax.swing.JTable();
        lblTimetable = new javax.swing.JLabel();
        lblSubjects = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstSubjects = new javax.swing.JList<>();
        btnDeleteSubject = new javax.swing.JButton();
        btnEditSubject = new javax.swing.JButton();
        btnAddSubject = new javax.swing.JButton();
        pnlCalendarTodo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1200, 550));

        pnlTimetable.setPreferredSize(new java.awt.Dimension(800, 400));

        tblTimetable.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        tblTimetable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));

        lblTimetable.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        lblTimetable.setText("주간 시간표");

        lblSubjects.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        lblSubjects.setText("과목 리스트");

        lstSubjects.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(lstSubjects);

        btnDeleteSubject.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnDeleteSubject.setText("삭제");

        btnEditSubject.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnEditSubject.setText("수정");

        btnAddSubject.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnAddSubject.setText("과목 추가");

        javax.swing.GroupLayout pnlTimetableLayout = new javax.swing.GroupLayout(pnlTimetable);
        pnlTimetable.setLayout(pnlTimetableLayout);
        pnlTimetableLayout.setHorizontalGroup(
            pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimetableLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblTimetable)
                        .addComponent(lblSubjects)
                        .addComponent(tblTimetable, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                        .addComponent(jScrollPane1))
                    .addGroup(pnlTimetableLayout.createSequentialGroup()
                        .addComponent(btnAddSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnlTimetableLayout.setVerticalGroup(
            pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimetableLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblTimetable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblTimetable, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSubjects)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tabMain.addTab("시간표 관리", pnlTimetable);

        javax.swing.GroupLayout pnlCalendarTodoLayout = new javax.swing.GroupLayout(pnlCalendarTodo);
        pnlCalendarTodo.setLayout(pnlCalendarTodoLayout);
        pnlCalendarTodoLayout.setHorizontalGroup(
            pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        pnlCalendarTodoLayout.setVerticalGroup(
            pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
        );

        tabMain.addTab("캘린더 / 할 일", pnlCalendarTodo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSubject;
    private javax.swing.JButton btnDeleteSubject;
    private javax.swing.JButton btnEditSubject;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSubjects;
    private javax.swing.JLabel lblTimetable;
    private javax.swing.JList<String> lstSubjects;
    private javax.swing.JPanel pnlCalendarTodo;
    private javax.swing.JPanel pnlTimetable;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblTimetable;
    // End of variables declaration//GEN-END:variables
}
