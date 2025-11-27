package Util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.border.Border;

/**
 * UI 스타일 헬퍼 클래스
 */
public class UIHelper {
    
    // 색상 팔레트
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);      // 스틸 블루
    public static final Color PRIMARY_HOVER = new Color(65, 105, 225);     // 로얄 블루
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);   // 회색
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // 연한 회색 배경
    public static final Color BORDER_COLOR = new Color(222, 226, 230);      // 테두리 회색
    public static final Color TEXT_COLOR = new Color(33, 37, 41);           // 진한 텍스트
    
    // 폰트
    public static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 28);
    public static final Font HEADING_FONT = new Font("맑은 고딕", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("맑은 고딕", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("맑은 고딕", Font.PLAIN, 14);
    
    // 테두리
    public static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    
    public static final Border FOCUS_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
        BorderFactory.createEmptyBorder(7, 11, 7, 11)
    );
    
    /**
     * 버튼 스타일 적용 (Primary)
     */
    public static void stylePrimaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // 호버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_HOVER);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }
    
    /**
     * 버튼 스타일 적용 (Secondary)
     */
    public static void styleSecondaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(SECONDARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // 호버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_COLOR);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
    }
    
    /**
     * 텍스트 필드 스타일 적용
     */
    public static void styleTextField(JTextField textField) {
        textField.setFont(BODY_FONT);
        textField.setBorder(INPUT_BORDER);
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_COLOR);
        
        // 포커스 효과
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(FOCUS_BORDER);
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(INPUT_BORDER);
            }
        });
    }
    
    /**
     * 패스워드 필드 스타일 적용
     */
    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(BODY_FONT);
        passwordField.setBorder(INPUT_BORDER);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(TEXT_COLOR);
        
        // 포커스 효과
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(FOCUS_BORDER);
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(INPUT_BORDER);
            }
        });
    }
    
    /**
     * 콤보박스 스타일 적용
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setBorder(INPUT_BORDER);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
    }
}

