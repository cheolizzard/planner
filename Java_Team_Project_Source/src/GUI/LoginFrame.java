/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import DAO.StudentDAO;
import DB.DB_MAN;
import Model.Student;
import Util.UIHelper;
import java.awt.Color;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author kkjjk
 */
public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());
    private StudentDAO studentDAO;
    private DB_MAN dbManager;

    /**
     * Creates new form LoginFrame
     */
    public LoginFrame() {
        initComponents();
        // applyStyles(); // 필요시 주석 해제하여 사용
        initDatabase();
        setupEventHandlers();
    }
    
    /**
     * UI 스타일 적용
     */
    private void applyStyles() {
        // 배경색 설정
        getContentPane().setBackground(Color.WHITE);
        
        // 제목 스타일
        lblTitle.setFont(UIHelper.TITLE_FONT);
        lblTitle.setForeground(UIHelper.PRIMARY_COLOR);
        
        // 레이블 스타일
        lblID.setFont(UIHelper.BODY_FONT);
        lblID.setForeground(UIHelper.TEXT_COLOR);
        lblPassword.setFont(UIHelper.BODY_FONT);
        lblPassword.setForeground(UIHelper.TEXT_COLOR);
        
        // 입력 필드 스타일
        UIHelper.styleTextField(txtUserId);
        UIHelper.stylePasswordField(pwUserPassword);
        
        // 버튼 스타일
        UIHelper.stylePrimaryButton(btnLogin);
        UIHelper.styleSecondaryButton(btnToRegister);
    }
    
    /**
     * 데이터베이스 초기화
     */
    private void initDatabase() {
        try {
            dbManager = DB_MAN.getInstance();
            dbManager.dbOpen();
            studentDAO = new StudentDAO();
        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "데이터베이스 연결 실패", e);
            JOptionPane.showMessageDialog(this, 
                "데이터베이스 연결에 실패했습니다.\n프로그램을 종료합니다.", 
                "연결 오류", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    /**
     * 이벤트 핸들러 설정
     */
    private void setupEventHandlers() {
        // 로그인 버튼
        btnLogin.addActionListener(e -> handleLogin());
        
        // 회원가입 버튼
        btnToRegister.addActionListener(e -> openRegisterDialog());
        
        // Enter 키로 로그인
        pwUserPassword.addActionListener(e -> handleLogin());
        txtUserId.addActionListener(e -> handleLogin());
    }
    
    /**
     * 로그인 처리
     */
    private void handleLogin() {
        String studentId = txtUserId.getText().trim();
        String password = new String(pwUserPassword.getPassword());
        
        // 입력 검증
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "학번을 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            txtUserId.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "비밀번호를 입력하세요.", 
                "입력 오류", 
                JOptionPane.WARNING_MESSAGE);
            pwUserPassword.requestFocus();
            return;
        }
        
        try {
            Student student = studentDAO.login(studentId, password);
            
            if (student != null) {
                // 로그인 성공
                JOptionPane.showMessageDialog(this, 
                    "로그인 성공!\n환영합니다, " + student.getName() + "님.", 
                    "로그인", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // 메인 화면 열기
                openMainFrame(student);
            } else {
                // 로그인 실패
                JOptionPane.showMessageDialog(this, 
                    "비밀번호 혹은 학번이 올바르지 않습니다.", 
                    "로그인 실패", 
                    JOptionPane.ERROR_MESSAGE);
                pwUserPassword.setText("");
                pwUserPassword.requestFocus();
            }
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "로그인 중 오류 발생", e);
            JOptionPane.showMessageDialog(this, 
                "로그인 중 오류가 발생했습니다.\n다시 시도해주세요.", 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 회원가입 다이얼로그 열기
     */
    private void openRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    /**
     * 메인 화면 열기
     */
    private void openMainFrame(Student student) {
        MainFrame mainFrame = new MainFrame(student);
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
        this.dispose(); // 로그인 화면 닫기
    }
    
    /**
     * 창 닫기 시 데이터베이스 연결 종료
     */
    @Override
    protected void processWindowEvent(java.awt.event.WindowEvent e) {
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING) {
            if (dbManager != null) {
                dbManager.dbClose();
            }
        }
        super.processWindowEvent(e);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        pwUserPassword = new javax.swing.JPasswordField();
        txtUserId = new javax.swing.JTextField();
        btnLogin = new javax.swing.JButton();
        btnToRegister = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(400, 35));

        lblTitle.setFont(new java.awt.Font("HY견고딕", 0, 24)); // NOI18N
        lblTitle.setText("NexPlan");
        lblTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblID.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        lblID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblID.setText("ID:");

        lblPassword.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N
        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPassword.setText("Password:");

        pwUserPassword.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N

        txtUserId.setFont(new java.awt.Font("맑은 고딕", 0, 16)); // NOI18N

        btnLogin.setFont(new java.awt.Font("맑은 고딕", 0, 18)); // NOI18N
        btnLogin.setText("로그인");

        btnToRegister.setFont(new java.awt.Font("맑은 고딕", 0, 18)); // NOI18N
        btnToRegister.setText("회원가입");
        btnToRegister.setAlignmentY(2.0F);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle)
                .addGap(130, 130, 130))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 19, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPassword)
                                .addGap(18, 18, 18)
                                .addComponent(pwUserPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblID)
                                .addGap(18, 18, 18)
                                .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnToRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblID))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pwUserPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnToRegister;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPasswordField pwUserPassword;
    private javax.swing.JTextField txtUserId;
    // End of variables declaration//GEN-END:variables
}
