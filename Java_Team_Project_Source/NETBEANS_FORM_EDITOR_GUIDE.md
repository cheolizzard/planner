# NetBeans Form Editor 사용 가이드

## ⚠️ 주의사항

이 프로젝트는 **NetBeans Form Editor**와 **수동 스타일 코드**를 함께 사용합니다.

### Form Editor에서 수정 가능한 것:
- ✅ 컴포넌트 배치 (위치, 크기)
- ✅ 기본 속성 (텍스트, 크기 등)
- ✅ 레이아웃 구조

### Form Editor에서 수정하면 안 되는 것:
- ❌ 폰트 설정 (코드에서 `applyStyles()`로 관리)
- ❌ 색상 설정 (코드에서 `applyStyles()`로 관리)
- ❌ 테두리 설정 (코드에서 `applyStyles()`로 관리)

## 🔧 작업 방법

### 1. 레이아웃 변경 시
1. Form Editor에서 레이아웃 수정
2. 저장 후 생성자 확인
3. `applyStyles()` 호출이 `initComponents()` 이후에 있는지 확인
4. 없으면 다시 추가:
   ```java
   public LoginFrame() {
       initComponents();
       applyStyles();  // ← 이 줄이 있어야 함!
       // ... 기타 초기화 코드
   }
   ```

### 2. 스타일 변경 시
- `src/Util/UIHelper.java` 파일 수정
- 또는 각 클래스의 `applyStyles()` 메서드 수정
- **Form Editor는 사용하지 않음**

### 3. 컴포넌트 추가 시
1. Form Editor에서 컴포넌트 추가
2. 생성자에 `applyStyles()` 호출 확인
3. `applyStyles()` 메서드에 새 컴포넌트 스타일 추가

## 📝 현재 구조

```
생성자 {
    initComponents();        // ← Form Editor가 생성
    applyStyles();           // ← 수동으로 스타일 적용
    // ... 기타 초기화
}
```

## ⚡ 빠른 체크리스트

Form Editor 사용 후 확인:
- [ ] 생성자에 `applyStyles()` 호출이 있는가?
- [ ] `initComponents()` 이후에 호출되는가?
- [ ] 프로그램 실행 시 스타일이 적용되는가?

