package kr.co.example.mobileprogramming.view;

import static android.os.Build.VERSION_CODES.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.example.mobileprogramming.controller.GameController;
import kr.co.example.mobileprogramming.model.Board;
import kr.co.example.mobileprogramming.model.Card;
import kr.co.example.mobileprogramming.model.Difficulty;
import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.GameResult;
import kr.co.example.mobileprogramming.model.ItemCard;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;
import kr.co.example.mobileprogramming.network.NetworkService;
import kr.co.example.mobileprogramming.network.NetworkServiceImpl;

public class GameActivity extends AppCompatActivity {
    private GameController gameController;
    private RecyclerView gameBoard;
    private TextView scoreTextView;
    private Button itemUseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UI 요소 초기화
        gameBoard = findViewById(R.id.game_board);
        scoreTextView = findViewById(R.id.score_text_view);
        itemUseButton = findViewById(R.id.item_use_button);

        // 게임 설정 정보 가져오기 (예: Intent로부터)
        Difficulty difficulty = Difficulty.NORMAL;
        int totalRounds = 5;
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        // GameManager 및 NetworkService 생성
        GameManager gameManager = new GameManager(difficulty, totalRounds, player1, player2);
        NetworkService networkService = new NetworkServiceImpl();

        // GameController 생성
        gameController = new GameController(this, gameManager, networkService);

        // 카드 클릭 이벤트 처리
        // 카드 어댑터를 설정하고 클릭 리스너에서 gameController.onCardSelected(position) 호출
    }

    // UI 업데이트 메서드
    public void initializeGameBoard(Board board) {
        // 게임 보드 초기화
    }

    public void updateCard(int position, Card card) {
        // 카드 상태 업데이트
    }

    public void showItemDialog(List<ItemEffect> items) {
        ItemDialogFragment dialog = new ItemDialogFragment(items, gameController);
        dialog.show(getSupportFragmentManager(), "ItemDialog");
    }

    public void updatePlayerItems(List<ItemEffect> items) {
        // 아이템 목록 업데이트
    }

    public void updateCurrentPlayer(Player player) {
        // 현재 플레이어 표시 업데이트
    }

    public void showMatch(int position1, int position2) {
        // 매칭된 카드 표시
    }

    public void showItemAcquired(ItemCard itemCard) {
        // 아이템 획득 알림 표시
    }

    public void navigateToResultActivity(GameResult gameResult) {
        // ResultActivity로 이동
    }

    public void showErrorDialog(String title, String message) {
        // 에러 다이얼로그 표시
    }

    public void showToast(String message) {
        // 토스트 메시지 표시
    }

    public void refreshUI() {
        // 현재 게임 상태를 기반으로 UI를 업데이트
        // 예: 게임 보드 갱신, 플레이어 점수 업데이트, 현재 플레이어 표시 등
    }

}
