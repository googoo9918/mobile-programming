# mobile-programming

## 패키지 구조
```bash
com.example.game <br>
├── model
│   ├── GameManager
│   ├── Board
│   ├── Card
│   ├── ItemCard
│   ├── Player
│   ├── GameState
│   ├── Difficulty (Enum)
│   ├── CardType (Enum)
│   ├── ItemType (Enum)
│   └── itemeffects
│       ├── ItemEffect (인터페이스)
│       ├── TurnExtensionEffect
│       ├── DoubleScoreEffect
│       ├── RemoveOpponentItemEffect
│       ├── RevealCardEffect
│       ├── StealItemEffect
│       ├── BombMinusOneEffect
│       └── BombLoseAllItemsEffect
├── view
│   ├── MainActivity
│   ├── GameActivity
│   ├── ResultActivity
│   ├── ItemDialogFragment
│   ├── adapters
│   │   ├── CardAdapter
│   │   └── ItemAdapter
├── controller
│   └── GameController
├── network
│   ├── NetworkService (인터페이스)
│   ├── NetworkServiceImpl
│   └── DataReceivedListener (인터페이스)
├── events
    ├── GameEventListener
    ├── GameErrorListener
    └── OnItemSelectedListener
```
## 클래스 설명
### 1. Model 계층
- `GameManager`
  - 게임 핵심 로직 관리
    - 게임 상태, 플레이어 턴, 보드 상태 등
- `Board`
  - 게임 보드를 나타냄
    - 카드 위치, 상태 관리
- `Card`
  - 개별 카드를 나타냄
    - 카드의 종류, 식별자, 이미지 리소스 등
- `ItemCard`
  - 아이템 카드를 나타냄(Card 상속)
    - 아이템 타입(7종)
- `Player`
  - 플레이어 정보
    - 이름, 점수, 보유 아이템 목록
- `GameState`
  - 현재 게임 상태
    - 네트워크 통신 시 게임 상태 직렬화하여 전송
- `GameResult`
  - 게임 결과
    - 승자 정보, 플레이어들의 점수

### 1.1 Model 계층 Enum 클래스
- `Difficulty`
  - 게임 난이도 정의
  - `EASY`, `NORMAL`, `HARD`
- `CardType`
  - 카드 종류 정의
  - `NORMAL`, `ITEM`, `BOMB`
- `ItemType`
  - 아이템의 종류를 정의

### 1.2 Model 계층 하위 패키지
- `ItemEffect`
  - 아이템 효과 정의 인터페이스
  - 각 효과에 따른 구현 클래스 7개 존재

### 2. View 계층(Android Component)
- `MainActivity`
  - 앱의 진입점
    - 게임 모드 선택 및 설정 화면 제공
- `GameActivity`
  - 게임 진행화면 제공
    - 게임 보드, 플레이어 점수, 아이템 사용 버튼 등을 포함
- `ResultActivity`
  - 게임 결과 화면 제공
    - 승자 정보, 점수, 재시작 or 메인 화면으로 돌아감
    - 재시작 or 메인 화면으로 돌아가는 것은 임의로 추가해 보았습니다.
- `ItemDialogFragment`
  - 아이템 사용 선택을 위한 다이얼로그

### 2.1 View 계층 하위 패키지
- `adapters`
  - UI 요소의 데이터 바인딩을 위한 클래스
  - `CardAdapter`
    - 게임 보드의 카드 표시를 위한 어댑터
  - `ItemAdapter`
    - 아이템 목록 표시를 위한 어댑터

### 3. Controller 계층
- 사용자 입력 처리, 모델 업데이트, 모델 상태변화 뷰에 반영 
- `GameController`
  - 모델과 뷰 사이 중재자
  - 게임 주요 로직 처리
  - 사용자 입력 -> 모델 업데이트
  - 모델 상태변화 -> 뷰에 전달

### 4. 네트워크 통신 관련 패키지
- `NetworkService`
  - 네트워크 통신을 위한 메서드 정의
- `NetworkServiceImpl`
  - NetworkService 인터페이스 구현
- `DataReceivedListener`
  - 네트워크에서 데이터 수신 시 호출되는 콜백 메서드 정의

### 5. 이벤트 및 리스너 패키지
- `GameEventListener`
  - 게임 이벤트를 처리하기 위한 콜백 메서드 정의
- `GameErrorListener`
  - 게임 중 발생하는 에러나 예외 상황을 처리하는 콜백 메서드 정의
- `OnItemSelectedListener`
  - 아이템 선택 이벤트 처리를 위한 콜백 메서드 정의