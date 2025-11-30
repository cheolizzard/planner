package GUI;

import DAO.CourseDAO;
import DAO.EnrollmentDAO;
import DAO.TodoDAO;
import DB.DB_MAN;
import Model.Course;
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
        loadTodoList();
        setTitle("NexPlan - " + student.getName() + "님 환영합니다");
        setupEventHandlers();
        loadCourseList();
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
        
        UIHelper.stylePrimaryButton(btnAddTodo);
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
        
        btnAddTodo.addActionListener(e -> handleAddTodo());
        
        // 탭 변경 시 데이터 새로고침
        tabMain.addChangeListener(e -> {
            if (tabMain.getSelectedIndex() == 1) {
                loadTodoList();
            }
        });
        
        // 달력 날짜 클릭 시 화면 갱신
        jCalendar.addPropertyChangeListener("calendar", e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(jCalendar.getDate());
            lblSelectedDate.setText(date);
            
            updateTodoListDisplay();
        });
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
        
        // 사용자 카테고리 목록도 업데이트
        updateCategoryList();
    }
    
    /**
     * 사용자 카테고리 목록 업데이트
     */
    private void updateCategoryList() {
        try {
            List<String> categories = todoDAO.getCustomCategories(currentStudent.getStudentId());
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String category : categories) {
                model.addElement(category);
            }
            lstCategories.setModel(model);
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "카테고리 목록 로드 실패", e);
        }
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
                // 먼저 enroll_id 찾기
                var enrollments = enrollmentDAO.getEnrollmentsByStudentId(currentStudent.getStudentId());
                int enrollIdToDelete = -1;
                for (var enrollment : enrollments) {
                    if (enrollment.getCourseId() == selectedCourse.getCourseId()) {
                        enrollIdToDelete = enrollment.getEnrollId();
                        break;
                    }
                }
                
                if (enrollIdToDelete > 0) {
                    // 1. 해당 과목의 모든 할 일 삭제 (명세서 요구사항)
                    todoDAO.deleteTodosByEnrollId(enrollIdToDelete);
                    
                    // 2. enrollment 삭제 (CASCADE로 course, lecture_time도 삭제됨)
                    enrollmentDAO.deleteEnrollment(enrollIdToDelete);
                    
                    JOptionPane.showMessageDialog(this, 
                        selectedCourse.getSubjectName() + " 과목이 삭제되었습니다.", 
                        "삭제 완료", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
        loadCourseList();
        loadTodoList();
        updateCategoryList(); // 할 일 목록도 새로고침
                }
            } catch (SQLException e) {
                logger.log(java.util.logging.Level.SEVERE, "과목 삭제 실패", e);
                JOptionPane.showMessageDialog(this, 
                    "과목 삭제 중 오류가 발생했습니다.", 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //================================= 캘린더 코드 =================================
    /**
     * 할일 목록을 DB에서 새로 불러옵니다.
     */
    private void loadTodoList() {
        if (currentStudent == null) return;
        
        try {
            // DB에서 해당 학생의 모든 할일을 가져옴
            todoList = todoDAO.getTodosByStudentId(currentStudent.getStudentId());
            updateTodoListDisplay();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 선택된 날짜에 맞는 할일 카드를 생성해서 화면에 붙입니다.
     */
    private void updateTodoListDisplay() {
        pnlTodoListContainer.removeAll();
        
        //  달력에서 선택된 날짜 가져오기
        Date selectedDate = jCalendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDateStr = sdf.format(selectedDate);

        // 콜백 만들기
        TodoActionCallback callback = new TodoActionCallback() {
            @Override
            public void onEdit(int todoId) {
                // 수정 로직
                handleEditTodoById(todoId);
            }

            @Override
            public void onDelete(int todoId) {
                // 삭제 로직
                handleDeleteTodoById(todoId);
            }
            
            @Override
            public void onStatusChange(int todoId, String newStatus) {
                // 상태 변경 로직
                handleStatusChange(todoId, newStatus);
            }
        };

        if (todoList != null) {
            boolean hasItem = false;
            for (Todo todo : todoList) {
                String todoDate = todo.getStartDatetime(); 
                
                // 날짜가 일치하는 경우에만 카드 생성
                if (todoDate != null && todoDate.startsWith(targetDateStr)) {
                    
                    // 카드 패널 생성 (데이터 + 콜백 전달)
                    TodoCardPanel card = new TodoCardPanel(todo, callback);
                    
                    // 컨테이너에 붙이기
                    pnlTodoListContainer.add(card);
                    
                    // 카드 사이에 간격 벌리기(px 단위)
                    pnlTodoListContainer.add(javax.swing.Box.createVerticalStrut(5));
                    hasItem = true;
                }
            }
            
            // 할 일이 하나도 없으면 안내 문구 표시
            if (!hasItem) {
                javax.swing.JLabel lblEmpty = new javax.swing.JLabel("등록된 일정이 없습니다.");
                lblEmpty.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                lblEmpty.setFont(new java.awt.Font("맑은 고딕", 0, 14));
                lblEmpty.setForeground(java.awt.Color.GRAY);
                pnlTodoListContainer.add(javax.swing.Box.createVerticalStrut(20));
                pnlTodoListContainer.add(lblEmpty);
            }
            
            // 카테고리 목록도 업데이트
            updateCategoryList();
        }
        
        pnlTodoListContainer.revalidate();
        pnlTodoListContainer.repaint();
    }
    /**
     * 할일 상태 변경 처리
     */
    private void handleStatusChange(int todoId, String newStatus) {
        try {
            boolean success = todoDAO.updateStatus(todoId, currentStudent.getStudentId(), newStatus);
            
            if (success) {
                loadTodoList(); // 목록 새로고침
            } else {
                JOptionPane.showMessageDialog(this, 
                    "상태 변경에 실패했습니다.", 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "상태 변경 실패", e);
            JOptionPane.showMessageDialog(this, 
                "상태 변경 중 오류가 발생했습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * ID를 이용해 할일 삭제 처리
     */
    private void handleDeleteTodoById(int todoId) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "정말 이 할일을 삭제하시겠습니까?", 
            "삭제 확인", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                todoDAO.deleteTodo(todoId, currentStudent.getStudentId());
                loadTodoList(); // 삭제 후 목록 새로고침
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "삭제 실패!");
            }
        }
    }

    /**
     * ID를 이용해 할일 수정 다이얼로그 띄우기
     */
    private void handleEditTodoById(int todoId) {
        // 리스트에서 해당 ID의 객체 찾기
        Todo targetTodo = null;
        for (Todo t : todoList) {
            if (t.getTodoId() == todoId) {
                targetTodo = t;
                break;
            }
        }
        
        if (targetTodo != null) {
            // 수정 모드로 다이얼로그 열기
            TodoDialog dialog = new TodoDialog(this, true, currentStudent.getStudentId(), targetTodo);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            loadTodoList();
        }
    }

    /**
     * 할일 추가 버튼 처리
     */
    private void handleAddTodo() {
        // 캘린더에서 선택된 날짜 가져오기
        Date selectedDate = jCalendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(selectedDate);
        
        // 추가 모드로 다이얼로그 열기 (선택된 날짜 전달)
        TodoDialog dialog = new TodoDialog(this, true, currentStudent.getStudentId(), null, dateStr);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        loadTodoList();
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
        lblCalendar = new javax.swing.JLabel();
        lblSelectedDate = new javax.swing.JLabel();
        lblTodoList = new javax.swing.JLabel();
        scrTodoList = new javax.swing.JScrollPane();
        pnlTodoListContainer = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnAddTodo = new javax.swing.JButton();
        jCalendar = new com.toedter.calendar.JCalendar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 550));

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

        lblCategories = new javax.swing.JLabel();
        lblCategories.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        lblCategories.setText("사용자 카테고리");

        lstCategories = new javax.swing.JList<>();
        lstCategories.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane2.setViewportView(lstCategories);

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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addComponent(lblCategories)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                    .addGroup(pnlTimetableLayout.createSequentialGroup()
                        .addComponent(btnAddSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeleteSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCategories)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlTimetableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tabMain.addTab("시간표 관리", pnlTimetable);

        lblCalendar.setFont(new java.awt.Font("맑은 고딕", 1, 24)); // NOI18N
        lblCalendar.setText("달력");

        lblSelectedDate.setFont(new java.awt.Font("맑은 고딕", 1, 24)); // NOI18N
        lblSelectedDate.setText("날짜 미정");

        lblTodoList.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        lblTodoList.setText("할 일 목록");

        scrTodoList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlTodoListContainer.setBackground(new java.awt.Color(255, 255, 255));
        pnlTodoListContainer.setLayout(new javax.swing.BoxLayout(pnlTodoListContainer, javax.swing.BoxLayout.Y_AXIS));
        scrTodoList.setViewportView(pnlTodoListContainer);

        btnAddTodo.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        btnAddTodo.setText("+ 할일 추가");

        jCalendar.setBackground(new java.awt.Color(255, 255, 255));
        jCalendar.setWeekOfYearVisible(false);

        javax.swing.GroupLayout pnlCalendarTodoLayout = new javax.swing.GroupLayout(pnlCalendarTodo);
        pnlCalendarTodo.setLayout(pnlCalendarTodoLayout);
        pnlCalendarTodoLayout.setHorizontalGroup(
            pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                .addGroup(pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(lblCalendar))
                    .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(32, 32, 32)
                .addGroup(pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblSelectedDate)
                    .addComponent(lblTodoList)
                    .addComponent(btnAddTodo, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(scrTodoList))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlCalendarTodoLayout.setVerticalGroup(
            pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCalendar)
                    .addComponent(lblSelectedDate))
                .addGap(7, 7, 7)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlCalendarTodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(lblTodoList, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrTodoList))
                    .addGroup(pnlCalendarTodoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addComponent(btnAddTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );

        tabMain.addTab("캘린더 / 할 일", pnlCalendarTodo);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
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
        try {
            // "Nimbus" 부분을 지우고 아래처럼 시스템 기본값으로 설정하세요.
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSubject;
    private javax.swing.JButton btnAddTodo;
    private javax.swing.JButton btnDeleteSubject;
    private javax.swing.JButton btnEditSubject;
    private com.toedter.calendar.JCalendar jCalendar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCalendar;
    private javax.swing.JLabel lblCategories;
    private javax.swing.JLabel lblSelectedDate;
    private javax.swing.JLabel lblSubjects;
    private javax.swing.JLabel lblTimetable;
    private javax.swing.JLabel lblTodoList;
    private javax.swing.JList<String> lstSubjects;
    private javax.swing.JList<String> lstCategories;
    private javax.swing.JPanel pnlCalendarTodo;
    private javax.swing.JPanel pnlTimetable;
    private javax.swing.JPanel pnlTodoListContainer;
    private javax.swing.JScrollPane scrTodoList;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblTimetable;
    // End of variables declaration//GEN-END:variables
}
