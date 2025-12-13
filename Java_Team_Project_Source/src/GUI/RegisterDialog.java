/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package GUI;

import DAO.StudentDAO;
import Model.Student;
import Util.CaptchaGenerator;
import Util.UIHelper;
import java.awt.Color;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author kkjjk
 */
public class RegisterDialog extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegisterDialog.class.getName());
    private StudentDAO studentDAO;
    private CaptchaGenerator captchaGenerator;

    /**
     * Creates new form RegisterDialog
     */
    public RegisterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // applyStyles(); // 필요시 주석 해제하여 사용
        studentDAO = new StudentDAO();
        setupEventHandlers();
        setupInitialValues();
        generateNewCaptcha();
    }
    
    /**
     * UI 스타일 적용
     */
    private void applyStyles() {
        // 배경색 설정
        getContentPane().setBackground(Color.WHITE);
        
        // 레이블 스타일
        lblStudentId.setFont(UIHelper.BODY_FONT);
        lblStudentId.setForeground(UIHelper.TEXT_COLOR);
        lblName.setFont(UIHelper.BODY_FONT);
        lblName.setForeground(UIHelper.TEXT_COLOR);
        lblDepartment.setFont(UIHelper.BODY_FONT);
        lblDepartment.setForeground(UIHelper.TEXT_COLOR);
        lblClass.setFont(UIHelper.BODY_FONT);
        lblClass.setForeground(UIHelper.TEXT_COLOR);
        lblPassword.setFont(UIHelper.BODY_FONT);
        lblPassword.setForeground(UIHelper.TEXT_COLOR);
        lblCapcha.setFont(UIHelper.BODY_FONT);
        lblCapcha.setForeground(UIHelper.TEXT_COLOR);
        lblInput.setFont(UIHelper.BODY_FONT);
        lblInput.setForeground(UIHelper.TEXT_COLOR);
        
        // 입력 필드 스타일
        UIHelper.styleTextField(txtStudentId);
        UIHelper.styleTextField(txtName);
        UIHelper.styleTextField(txtDepartment);
        UIHelper.stylePasswordField(pwPassword);
        UIHelper.styleTextField(txtCaptchaInput);
        UIHelper.styleComboBox(cboClassSection);
        
        // 버튼 스타일
        UIHelper.stylePrimaryButton(btnRegisterComplete);
        UIHelper.styleSecondaryButton(btnRegisterCancel);
        UIHelper.styleSecondaryButton(btnRefreshCaptcha);
    }
    
    /**
     * 초기값 설정
     */
    private void setupInitialValues() {
        // 학과는 고정값으로 설정 (편집 불가)
        txtDepartment.setText("컴퓨터 정보학과");
        txtDepartment.setEditable(false);
        txtDepartment.setEnabled(false);
        
        // 반 드롭다운에서 "반을 선택하시오" 제거하고 C반을 기본값으로
        cboClassSection.removeAllItems();
        cboClassSection.addItem("C반");
        cboClassSection.addItem("A반");
        cboClassSection.addItem("B반");
        cboClassSection.setSelectedIndex(0); // C반 기본 선택
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 회원가입 버튼
        btnRegisterComplete.addActionListener(e -> handleRegister());
        
        // 취소 버튼
        btnRegisterCancel.addActionListener(e -> dispose());
        
        // CAPTCHA 새로고침 버튼
        btnRefreshCaptcha.addActionListener(e -> generateNewCaptcha());
    }
    
    /**
     * 새로운 CAPTCHA 생성
     */
    private void generateNewCaptcha() {
        captchaGenerator = new CaptchaGenerator();
        lblCaptchaImage.setIcon(captchaGenerator.getCaptchaImage());
        txtCaptchaInput.setText("");
    }
    
    /**
     * 회원가입 처리
     */
    private void handleRegister() {
        // 입력값 가져오기
        String studentId = txtStudentId.getText().trim();
        String name = txtName.getText().trim();
        String department = txtDepartment.getText().trim();
        String selectedClass = (String) cboClassSection.getSelectedItem();
        String password = new String(pwPassword.getPassword());
        String captchaInput = txtCaptchaInput.getText().trim();
        
        // 유효성 검사
        if (!validateInput(studentId, name, selectedClass, password, captchaInput)) {
            return;
        }
        
        try {
            // 반 ID 가져오기
            int classId = studentDAO.getClassIdByName(selectedClass);
            if (classId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "선택한 반을 찾을 수 없습니다.", 
                    "오류", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 학생 객체 생성
            Student student = new Student();
            student.setStudentId(studentId);
            student.setClassId(classId);
            student.setPassword(password);
            student.setName(name);
            student.setDepartment(department);
            
            // 회원가입 처리
            boolean success = studentDAO.register(student);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "회원가입이 완료되었습니다!\n로그인 화면으로 돌아갑니다.", 
                    "회원가입 성공", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "이미 존재하는 학번입니다.", 
                    "회원가입 실패", 
                    JOptionPane.ERROR_MESSAGE);
                txtStudentId.requestFocus();
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "회원가입 중 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "회원가입 중 오류가 발생했습니다.\n다시 시도해주세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 입력값 유효성 검사
     */
    private boolean validateInput(String studentId, String name, String selectedClass, 
                                   String password, String captchaInput) {
        // 학번 검증 (9자리)
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "학번을 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtStudentId.requestFocus();
            return false;
        }
        
        if (studentId.length() != 9 || !studentId.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this, 
                "학번은 9자리 숫자여야 합니다.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtStudentId.requestFocus();
            return false;
        }
        
        // 이름 검증
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "이름을 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return false;
        }
        
        // 반 검증
        if (selectedClass == null || selectedClass.equals("반을 선택하시오")) {
            JOptionPane.showMessageDialog(this, 
                "반을 선택하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            cboClassSection.requestFocus();
            return false;
        }
        
        // 비밀번호 검증 (최소 8자리)
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "비밀번호를 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            pwPassword.requestFocus();
            return false;
        }
        
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, 
                "비밀번호는 최소 8자리 이상이어야 합니다.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            pwPassword.requestFocus();
            return false;
        }
        
        // CAPTCHA 검증
        if (captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "로봇 방지 문자를 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtCaptchaInput.requestFocus();
            return false;
        }
        
        if (!captchaGenerator.validate(captchaInput)) {
            JOptionPane.showMessageDialog(this, 
                "로봇 방지 문자가 올바르지 않습니다.\n새로운 문자를 확인해주세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            generateNewCaptcha();
            txtCaptchaInput.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtStudentId = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtDepartment = new javax.swing.JTextField();
        cboClassSection = new javax.swing.JComboBox<>();
        pwPassword = new javax.swing.JPasswordField();
        lblCaptchaImage = new javax.swing.JLabel();
        txtCaptchaInput = new javax.swing.JTextField();
        btnRegisterComplete = new javax.swing.JButton();
        btnRegisterCancel = new javax.swing.JButton();
        btnRefreshCaptcha = new javax.swing.JButton();
        lblStudentId = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDepartment = new javax.swing.JLabel();
        lblClass = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblCapcha = new javax.swing.JLabel();
        lblInput = new javax.swing.JLabel();
        sep = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(450, 500));
        setPreferredSize(new java.awt.Dimension(400, 500));
        setSize(new java.awt.Dimension(400, 500));

        txtStudentId.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        txtName.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        txtDepartment.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        cboClassSection.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        cboClassSection.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "반을 선택하시오", "A반", "B반", "c반" }));

        pwPassword.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        txtCaptchaInput.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N

        btnRegisterComplete.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnRegisterComplete.setText("회원가입");

        btnRegisterCancel.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnRegisterCancel.setText("취소");

        btnRefreshCaptcha.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnRefreshCaptcha.setText("새로고침");

        lblStudentId.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblStudentId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStudentId.setText("학번:");

        lblName.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblName.setText("이름:");

        lblDepartment.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblDepartment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDepartment.setText("학과:");

        lblClass.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblClass.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblClass.setText("반:");

        lblPassword.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPassword.setText("비밀번호:");
        lblPassword.setToolTipText("");

        lblCapcha.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblCapcha.setText("로봇방지:");

        lblInput.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        lblInput.setText("입력:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(btnRegisterComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRegisterCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCapcha)
                    .addComponent(lblInput))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 123, Short.MAX_VALUE)
                        .addComponent(lblCaptchaImage)
                        .addGap(35, 35, 35)
                        .addComponent(btnRefreshCaptcha, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtCaptchaInput, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sep, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblStudentId)
                                    .addComponent(lblName))
                                .addComponent(lblDepartment))
                            .addComponent(lblClass)
                            .addComponent(lblPassword))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtName)
                            .addComponent(txtStudentId)
                            .addComponent(txtDepartment)
                            .addComponent(cboClassSection, 0, 241, Short.MAX_VALUE)
                            .addComponent(pwPassword))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStudentId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDepartment))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboClassSection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addGap(29, 29, 29)
                .addComponent(sep, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCaptchaImage)
                    .addComponent(btnRefreshCaptcha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCapcha))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCaptchaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInput))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegisterComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegisterCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
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
                RegisterDialog dialog = new RegisterDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnRefreshCaptcha;
    private javax.swing.JButton btnRegisterCancel;
    private javax.swing.JButton btnRegisterComplete;
    private javax.swing.JComboBox<String> cboClassSection;
    private javax.swing.JLabel lblCapcha;
    private javax.swing.JLabel lblCaptchaImage;
    private javax.swing.JLabel lblClass;
    private javax.swing.JLabel lblDepartment;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblStudentId;
    private javax.swing.JPasswordField pwPassword;
    private javax.swing.JSeparator sep;
    private javax.swing.JTextField txtCaptchaInput;
    private javax.swing.JTextField txtDepartment;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtStudentId;
    // End of variables declaration//GEN-END:variables
}
