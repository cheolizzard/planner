/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package GUI;

import DAO.CourseDAO;
import Model.Course;
import Util.UIHelper;
import java.awt.Color;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author kkjjk
 */
public class SubjectDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SubjectDialog.class.getName());
    private CourseDAO courseDAO;
    private String studentId;
    private Course editCourse; // 수정 모드일 때 사용
    private boolean isEditMode;

    /**
     * Creates new form SubjectDialog (테스트용 기본 생성자)
     */
    public SubjectDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, "202444085", null);
    }
    
    /**
     * Creates new form SubjectDialog (추가 모드)
     */
    public SubjectDialog(java.awt.Frame parent, boolean modal, String studentId, Course editCourse) {
        super(parent, modal);
        this.studentId = studentId;
        this.editCourse = editCourse;
        this.isEditMode = (editCourse != null);
        this.courseDAO = new CourseDAO();
        initComponents();
        // applyStyles(); // 필요시 주석 해제하여 사용
        setupEventHandlers();
        setupTimeComboBoxes();
        
        if (isEditMode) {
            loadCourseData();
            setTitle("과목 수정");
        } else {
            setTitle("과목 추가");
        }
    }
    
    /**
     * UI 스타일 적용
     */
    private void applyStyles() {
        // 배경색 설정
        getContentPane().setBackground(Color.WHITE);
        
        // 레이블 스타일
        lalSubjectName.setFont(UIHelper.BODY_FONT);
        lalSubjectName.setForeground(UIHelper.TEXT_COLOR);
        lblProfessor.setFont(UIHelper.BODY_FONT);
        lblProfessor.setForeground(UIHelper.TEXT_COLOR);
        lblClassroom.setFont(UIHelper.BODY_FONT);
        lblClassroom.setForeground(UIHelper.TEXT_COLOR);
        lblStartTime.setFont(UIHelper.BODY_FONT);
        lblStartTime.setForeground(UIHelper.TEXT_COLOR);
        lblEndTime.setFont(UIHelper.BODY_FONT);
        lblEndTime.setForeground(UIHelper.TEXT_COLOR);
        
        // 입력 필드 스타일
        UIHelper.styleTextField(txtSubjectName);
        UIHelper.styleTextField(txtProfessor);
        UIHelper.styleTextField(txtClassroom);
        UIHelper.styleComboBox(cboStartTime);
        UIHelper.styleComboBox(cboEndTime);
        
        // 버튼 스타일
        UIHelper.stylePrimaryButton(btnSaveProject);
        UIHelper.styleSecondaryButton(btnCancelSubject);
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        btnSaveProject.addActionListener(e -> handleSave());
        btnCancelSubject.addActionListener(e -> dispose());
    }
    
    /**
     * 시간 콤보박스 설정
     */
    private void setupTimeComboBoxes() {
        // 시간: 09:00 ~ 18:00 (30분 단위)
        String[] times = new String[19];
        for (int i = 0; i < 19; i++) {
            int hour = 9 + i / 2;
            int minute = (i % 2) * 30;
            times[i] = String.format("%02d:%02d", hour, minute);
        }
        
        cboStartTime.setModel(new javax.swing.DefaultComboBoxModel<>(times));
        cboEndTime.setModel(new javax.swing.DefaultComboBoxModel<>(times));
        
        // 요일 콤보박스 설정
        String[] days = {"월", "화", "수", "목", "금"};
        cboDayOfWeek.setModel(new javax.swing.DefaultComboBoxModel<>(days));
        
        // 기본값 설정
        cboStartTime.setSelectedIndex(0); // 09:00
        cboEndTime.setSelectedIndex(6);  // 12:00
        cboDayOfWeek.setSelectedIndex(0); // 월요일
    }
    
    /**
     * 수정 모드일 때 기존 데이터 로드
     */
    private void loadCourseData() {
        try {
            txtSubjectName.setText(editCourse.getSubjectName());
            txtProfessor.setText(editCourse.getProfessorName());
            txtClassroom.setText(editCourse.getClassroom());
            
            // 강의 시간 로드
            var times = courseDAO.getLectureTimesByCourseId(editCourse.getCourseId());
            if (!times.isEmpty()) {
                var time = times.get(0); // 첫 번째 시간만 표시 (간단화)
                cboDayOfWeek.setSelectedItem(time.getDayOfWeek());
                cboStartTime.setSelectedItem(time.getStartTime());
                cboEndTime.setSelectedItem(time.getEndTime());
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "과목 데이터 로드 실패", e);
        }
    }
    
    /**
     * 저장 처리
     */
    private void handleSave() {
        String subjectName = txtSubjectName.getText().trim();
        String professorName = txtProfessor.getText().trim();
        String classroom = txtClassroom.getText().trim();
        String startTime = (String) cboStartTime.getSelectedItem();
        String endTime = (String) cboEndTime.getSelectedItem();
        
        // 유효성 검사
        if (subjectName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "과목 이름을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtSubjectName.requestFocus();
            return;
        }
        
        if (professorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "담당 교수를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtProfessor.requestFocus();
            return;
        }
        
        if (classroom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "강의실을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            txtClassroom.requestFocus();
            return;
        }
        
        if (startTime == null || endTime == null) {
            JOptionPane.showMessageDialog(this, "시작 시간과 끝 시간을 선택하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String dayOfWeek = (String) cboDayOfWeek.getSelectedItem();
        if (dayOfWeek == null) {
            JOptionPane.showMessageDialog(this, "요일을 선택하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (isEditMode) {
                // 수정 모드
                // 시간 겹침 검사 (현재 수정 중인 과목 제외)
                if (courseDAO.hasTimeConflict(studentId, dayOfWeek, startTime, endTime, editCourse.getCourseId())) {
                    JOptionPane.showMessageDialog(this, 
                        "해당 시간에 이미 수강 중인 과목이 있습니다.", 
                        "시간 겹침", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 과목 수정
                boolean success = courseDAO.updateCourse(
                    editCourse.getCourseId(), 
                    subjectName, 
                    professorName, 
                    classroom, 
                    dayOfWeek, 
                    startTime, 
                    endTime, 
                    3);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        subjectName + " 과목이 수정되었습니다.", 
                        "수정 완료", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "과목 수정에 실패했습니다.", 
                        "오류", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 추가 모드
                // 시간 겹침 검사
                if (courseDAO.hasTimeConflict(studentId, dayOfWeek, startTime, endTime, null)) {
                    JOptionPane.showMessageDialog(this, 
                        "해당 시간에 이미 수강 중인 과목이 있습니다.", 
                        "시간 겹침", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 학점 한도 검사 (기본 3학점)
                int currentCredits = courseDAO.getTotalCredits(studentId);
                if (currentCredits + 3 > 21) {
                    JOptionPane.showMessageDialog(this, 
                        "학점 한도(21학점)를 초과할 수 없습니다.\n현재 학점: " + currentCredits + "학점", 
                        "학점 한도 초과", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 과목 추가
                int enrollId = courseDAO.addCourseWithEnrollment(
                    studentId, subjectName, professorName, classroom, 
                    dayOfWeek, startTime, endTime, 3);
                
                if (enrollId > 0) {
                    JOptionPane.showMessageDialog(this, 
                        subjectName + " 과목이 추가되었습니다.", 
                        "추가 완료", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "과목 추가에 실패했습니다.", 
                        "오류", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "과목 저장 실패", e);
            JOptionPane.showMessageDialog(this, 
                "과목 저장 중 오류가 발생했습니다.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
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

        lalSubjectName = new javax.swing.JLabel();
        lblProfessor = new javax.swing.JLabel();
        lblClassroom = new javax.swing.JLabel();
        lblDayOfWeek = new javax.swing.JLabel();
        lblStartTime = new javax.swing.JLabel();
        lblEndTime = new javax.swing.JLabel();
        txtSubjectName = new javax.swing.JTextField();
        txtProfessor = new javax.swing.JTextField();
        txtClassroom = new javax.swing.JTextField();
        btnSaveProject = new javax.swing.JButton();
        btnCancelSubject = new javax.swing.JButton();
        cboDayOfWeek = new javax.swing.JComboBox<>();
        cboStartTime = new javax.swing.JComboBox<>();
        cboEndTime = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lalSubjectName.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lalSubjectName.setText("과목 이름:");

        lblProfessor.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblProfessor.setText("담당 교수:");

        lblClassroom.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblClassroom.setText("강의실:");

        lblDayOfWeek.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblDayOfWeek.setText("요일:");

        lblStartTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblStartTime.setText("시작 시간:");

        lblEndTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblEndTime.setText("끝 시간:");

        txtSubjectName.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        txtProfessor.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        txtClassroom.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        btnSaveProject.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnSaveProject.setText("저장");

        btnCancelSubject.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnCancelSubject.setText("취소");

        cboDayOfWeek.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboStartTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboEndTime.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblEndTime)
                    .addComponent(lblStartTime)
                    .addComponent(lblDayOfWeek)
                    .addComponent(lblClassroom)
                    .addComponent(lblProfessor)
                    .addComponent(lalSubjectName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSubjectName)
                    .addComponent(txtProfessor)
                    .addComponent(txtClassroom)
                    .addComponent(cboDayOfWeek, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboStartTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboEndTime, 0, 270, Short.MAX_VALUE))
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(btnSaveProject, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(btnCancelSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lalSubjectName)
                    .addComponent(txtSubjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProfessor)
                    .addComponent(txtProfessor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClassroom)
                    .addComponent(txtClassroom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDayOfWeek)
                    .addComponent(cboDayOfWeek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStartTime)
                    .addComponent(cboStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEndTime)
                    .addComponent(cboEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelSubject, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveProject, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
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
                SubjectDialog dialog = new SubjectDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancelSubject;
    private javax.swing.JButton btnSaveProject;
    private javax.swing.JComboBox<String> cboDayOfWeek;
    private javax.swing.JComboBox<String> cboEndTime;
    private javax.swing.JComboBox<String> cboStartTime;
    private javax.swing.JLabel lalSubjectName;
    private javax.swing.JLabel lblClassroom;
    private javax.swing.JLabel lblDayOfWeek;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblProfessor;
    private javax.swing.JLabel lblStartTime;
    private javax.swing.JTextField txtClassroom;
    private javax.swing.JTextField txtProfessor;
    private javax.swing.JTextField txtSubjectName;
    // End of variables declaration//GEN-END:variables
}
