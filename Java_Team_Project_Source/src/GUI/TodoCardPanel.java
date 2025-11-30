package GUI;

import Model.Todo;
import java.awt.Color;
import java.awt.Dimension;

public class TodoCardPanel extends javax.swing.JPanel {

    // 멤버 변수
    private int todoId;                  // 할일 ID 저장
    private TodoActionCallback callback; // 부모에게 신호 보낼 리모컨

    /**
     * 생성자: Todo 객체와 Callback 인터페이스를 받습니다.
     */
    public TodoCardPanel(Todo todo, TodoActionCallback callback) {
        initComponents();

        // 데이터 저장
        this.todoId = todo.getTodoId();
        this.callback = callback;

        // 화면에 데이터 뿌리기
        // 제목: [카테고리] 할일제목
        String category = todo.getDisplayCategory(); 
        String fullTitle = "[" + category + "] " + todo.getTitle();
        // 제목이 너무 길면 자르기 (버튼 공간 확보)
        if (fullTitle.length() > 30) {
            fullTitle = fullTitle.substring(0, 30) + "...";
        }
        lblTitle.setText(fullTitle);

        // 시간: 시작 ~ 끝
        String timeText = formatTime(todo.getStartDatetime(), todo.getEndDatetime());
        lblTime.setText(timeText);

        // 상태 표시
        String status = todo.getStatus();
        if (status == null || status.isEmpty()) {
            status = "미완료";
        }
        lblStatus.setText(status);
        
        // 상태별 색상 설정
        switch (status) {
            case "완료":
                lblStatus.setForeground(new Color(76, 175, 80)); // 초록색
                this.setBackground(new Color(245, 245, 245)); // 연한 회색
                lblTitle.setForeground(Color.GRAY);
                break;
            case "진행중":
                lblStatus.setForeground(new Color(255, 152, 0)); // 주황색
                this.setBackground(Color.WHITE);
                lblTitle.setForeground(Color.BLACK);
                break;
            default: // 미완료
                lblStatus.setForeground(new Color(158, 158, 158)); // 회색
                this.setBackground(Color.WHITE);
                lblTitle.setForeground(Color.BLACK);
                break;
        }

        // 패널 크기 고정 (리스트 모양 유지용)
        Dimension size = new Dimension(400, 60);
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
        
    }
    
    /**
     * 시간 문자열을 포맷팅 (날짜 제거하고 시간만 표시)
     * 예: "14:00 ~ 16:00"
     */
    private String formatTime(String start, String end) {
        String sTime = extractTimeOnly(start);
        String eTime = extractTimeOnly(end);

        if (sTime.isEmpty() && eTime.isEmpty()) return "시간 미정";
        if (sTime.isEmpty()) return "~ " + eTime;
        if (eTime.isEmpty()) return sTime + " ~";
        
        return sTime + " ~ " + eTime;
    }

    /**
     *  시간 추출 헬퍼
     */
    private String extractTimeOnly(String datetime) {
        if (datetime == null || datetime.trim().isEmpty()) return "";
        
        String[] parts = datetime.split(" ");
        
        if (parts.length > 1) {
            String timePart = parts[1]; // "14:00:00" 또는 "14:00"
            
            if (timePart.length() >= 5) {
                return timePart.substring(0, 5);
            }
            return timePart;
        }
        return datetime; 
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204), 2));
        setMaximumSize(new java.awt.Dimension(400, 60));
        setPreferredSize(new java.awt.Dimension(340, 60));

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 14)); // NOI18N
        lblTitle.setText("할일");

        lblTime.setFont(new java.awt.Font("맑은 고딕", 0, 10)); // NOI18N
        lblTime.setForeground(new java.awt.Color(51, 51, 51));
        lblTime.setText("시간");

        lblStatus.setFont(new java.awt.Font("맑은 고딕", 0, 11)); // NOI18N
        lblStatus.setText("상태");
        lblStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblStatus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblStatusMouseClicked(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnEdit.setText("수정");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("맑은 고딕", 0, 14)); // NOI18N
        btnDelete.setText("삭제");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTime)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTime)
                            .addComponent(lblStatus)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEdit)
                            .addComponent(btnDelete))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        if (callback != null) {
            callback.onEdit(todoId);
        }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (callback != null) {
            callback.onDelete(todoId);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void lblStatusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblStatusMouseClicked
        // 상태 변경: 미완료 -> 진행중 -> 완료 -> 미완료...
        String currentStatus = lblStatus.getText();
        String newStatus;
        
        switch (currentStatus) {
            case "미완료":
                newStatus = "진행중";
                break;
            case "진행중":
                newStatus = "완료";
                break;
            case "완료":
                newStatus = "미완료";
                break;
            default:
                newStatus = "미완료";
                break;
        }
        
        if (callback != null) {
            callback.onStatusChange(todoId, newStatus);
        }
    }//GEN-LAST:event_lblStatusMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTitle;
    // End of variables declaration//GEN-END:variables
}
