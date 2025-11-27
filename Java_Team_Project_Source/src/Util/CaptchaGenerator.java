package Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 * CAPTCHA 이미지 생성 클래스
 */
public class CaptchaGenerator {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 0, O, I, 1 제외
    private String captchaText;
    private BufferedImage captchaImage;
    
    /**
     * CAPTCHA 생성
     */
    public CaptchaGenerator() {
        generateCaptcha();
    }
    
    /**
     * 새로운 CAPTCHA 생성
     */
    public void generateCaptcha() {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        
        // 5자리 랜덤 문자열 생성
        for (int i = 0; i < 5; i++) {
            text.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        this.captchaText = text.toString();
        this.captchaImage = createImage(this.captchaText);
    }
    
    /**
     * CAPTCHA 이미지 생성
     */
    private BufferedImage createImage(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 안티앨리어싱 설정
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // 배경색 설정
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 랜덤 노이즈 라인 추가
        Random random = new Random();
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // 텍스트 그리기
        Font font = new Font("Arial", Font.BOLD, 30);
        g2d.setFont(font);
        
        for (int i = 0; i < text.length(); i++) {
            // 각 문자마다 랜덤 색상과 위치
            g2d.setColor(new Color(
                random.nextInt(100),
                random.nextInt(100),
                random.nextInt(100)
            ));
            
            // 문자 위치에 약간의 랜덤 오프셋 추가
            int x = 20 + i * 25 + random.nextInt(5);
            int y = 30 + random.nextInt(10);
            
            // 약간의 회전
            double angle = (random.nextDouble() - 0.5) * 0.3;
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
            g2d.rotate(-angle, x, y);
        }
        
        g2d.dispose();
        return image;
    }
    
    /**
     * CAPTCHA 텍스트 반환
     */
    public String getCaptchaText() {
        return captchaText;
    }
    
    /**
     * CAPTCHA 이미지 반환
     */
    public ImageIcon getCaptchaImage() {
        return new ImageIcon(captchaImage);
    }
    
    /**
     * 입력값 검증
     */
    public boolean validate(String input) {
        if (input == null) {
            return false;
        }
        return captchaText.equalsIgnoreCase(input.trim());
    }
}

