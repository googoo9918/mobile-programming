package kr.co.example.mobileprogramming.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.co.example.mobileprogramming.controller.GameController;
import kr.co.example.mobileprogramming.model.Board;
import kr.co.example.mobileprogramming.model.Card;
import kr.co.example.mobileprogramming.model.CardType;
import kr.co.example.mobileprogramming.model.Difficulty;
import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.GameResult;
import kr.co.example.mobileprogramming.model.ItemCard;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;
import kr.co.example.mobileprogramming.network.NetworkService;
import kr.co.example.mobileprogramming.network.NetworkServiceImpl;
import kr.co.example.mobleprogramming.R;



public class GameActivity extends AppCompatActivity {
    private GameController gameController;

    private TextView roundText;
    private TextView difficultyText;
    private TextView modeText;
    private TextView timeTextView;       // 1인용 모드에서 사용
    private TextView elapsedTimeTextView; // 2인용 모드에서 사용

    private static final int BOARD_SIZE = 36; // 6x6 보드
    private List<Integer> cardIds;
    private List<Card> boardCards;

    private int roundInfo;
    private String difficultyInfo;
    private int modeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        roundInfo = intent.getIntExtra("ROUND", 3);
        difficultyInfo = intent.getStringExtra("DIFFICULTY");
        modeInfo = intent.getIntExtra("MODE", 1);

        // 모드에 따라 다른 레이아웃 사용
        if (modeInfo == 1) {
            setContentView(R.layout.activity_game_singleplayer);
        } else {
            setContentView(R.layout.activity_game_multiplayer);
        }

        initializeUI();
        initializeGameLogic();
    }

    private void initializeUI() {
        if (modeInfo == 1) {
            // 1인용 UI 참조
            roundText = findViewById(R.id.chooseround);
            difficultyText = findViewById(R.id.choosedifficulty);
            modeText = findViewById(R.id.choosemode);
            timeTextView = findViewById(R.id.timeTextView);

            roundText.setText("라운드: " + roundInfo);
            difficultyText.setText("난이도: " + difficultyInfo);
            modeText.setText("게임 모드: " + modeInfo + "인용");

        } else {
            // 2인용 UI 참조
            TextView roundTextMulti = findViewById(R.id.chooseround_multi);
            TextView modeTextMulti = findViewById(R.id.choosemode_multi);
            elapsedTimeTextView = findViewById(R.id.elapsedTimeTextView);

            roundTextMulti.setText("라운드: " + roundInfo);
            modeTextMulti.setText("게임 모드: " + modeInfo + "인용");
        }
    }

    private void initializeGameLogic() {
        Difficulty diffEnum;
        try {
            diffEnum = Difficulty.valueOf(difficultyInfo.toUpperCase());
        } catch (Exception e) {
            diffEnum = Difficulty.NORMAL;
        }

        Player player1 = new Player("Player 1");
        Player player2 = (modeInfo == 2) ? new Player("Player 2") : null;

        GameManager gameManager = new GameManager(diffEnum, roundInfo, player1, player2);
        NetworkService networkService = new NetworkServiceImpl();

        initializeCardIds();
        setupBoard();
        gameManager.initializeBoard(boardCards);

        gameController = new GameController(this, gameManager, networkService);
        gameManager.startGame();
    }

    private void initializeCardIds() {
        cardIds = new ArrayList<>();
        Field[] drawableFields = R.drawable.class.getFields();
        for (Field field : drawableFields) {
            if (field.getName().startsWith("card_")) {
                try {
                    cardIds.add(field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupBoard() {
        boardCards = new ArrayList<>();
        Collections.shuffle(cardIds);
        List<Integer> selectedCards = cardIds.subList(0, BOARD_SIZE / 2);

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);

        for (Integer cardId : selectedCards) {
            boardCards.add(new Card(cardId, CardType.NORMAL));
            boardCards.add(new Card(cardId, CardType.NORMAL));
        }
        Collections.shuffle(boardCards);
    }

    public void displayCards() {
        GridLayout gridLayout = findViewById(R.id.cardGrid);
        gridLayout.removeAllViews();
        gridLayout.setRowCount(6);
        gridLayout.setColumnCount(6);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int cardSize = (screenWidth - 150) / 6;

        for (int i = 0; i < 36; i++) {
            FrameLayout cardFrame = new FrameLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardSize;
            params.height = cardSize;
            params.setMargins(4, 4, 4, 4);

            ImageView cardImage = new ImageView(this);
            cardImage.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            cardImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

            cardImage.setImageResource(R.drawable.back);

            Card currentCard = boardCards.get(i);
            cardImage.setTag(currentCard);

            if (currentCard.isFlipped()) {
                cardImage.setImageResource(currentCard.getId());
            } else {
                cardImage.setImageResource(R.drawable.back);
            }

            final int cardIndex = i;
            cardImage.setOnClickListener(v -> gameController.onCardSelected(cardIndex));

            cardFrame.addView(cardImage);
            cardFrame.setLayoutParams(params);
            gridLayout.addView(cardFrame);
        }
    }

    public void initializeGameBoard(Board board) {
        // 필요 시 추가 초기화
    }

    public void updateCard(int position, Card card) {
        GridLayout gridLayout = findViewById(R.id.cardGrid);
        FrameLayout cardFrame = (FrameLayout) gridLayout.getChildAt(position);
        ImageView cardImage = (ImageView) cardFrame.getChildAt(0);

        if (card.isFlipped()) {
            cardImage.setImageResource(card.getId());
        } else {
            cardImage.setImageResource(R.drawable.back);
        }
    }

    public void showItemDialog(List<ItemEffect> items) {
        ItemDialogFragment dialog = new ItemDialogFragment(items, gameController);
        dialog.show(getSupportFragmentManager(), "ItemDialog");
    }

    public void updatePlayerItems(List<ItemEffect> items) {
        // 모드 별로 필요하다면 구현
    }

    public void updateCurrentPlayer(Player player) {
        // 2인용일 경우 현재 플레이어 표시를 변경할 수도 있음
    }

    public void showMatch(int position1, int position2) {
        // 매칭 연출
    }

    public void showItemAcquired(ItemCard itemCard) {
        // 아이템 획득 알림
    }

    public void navigateToResultActivity(GameResult gameResult) {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
        finish();
    }

    public void showErrorDialog(String title, String message) {
        // 에러 다이얼로그
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void refreshUI() {
        GridLayout gridLayout = findViewById(R.id.cardGrid);
        if (gridLayout == null) return;

        int totalCards = boardCards.size();
        for (int i = 0; i < totalCards; i++) {
            Card card = boardCards.get(i);
            FrameLayout cardFrame = (FrameLayout) gridLayout.getChildAt(i);
            if (cardFrame == null) continue;

            ImageView cardImage = (ImageView) cardFrame.getChildAt(0);
            if (cardImage == null) continue;

            if (card.isFlipped()) {
                cardImage.setImageResource(card.getId());
                cardImage.setTag("flipped");
            } else {
                cardImage.setImageResource(R.drawable.back);
                cardImage.setTag("back");
            }
        }
    }

    // 1인용 모드에서 남은 시간 업데이트
    public void updateTimeUI(long remainingMillis) {
        if (modeInfo == 1 && timeTextView != null) {
            int seconds = (int) (remainingMillis / 1000) % 60;
            int minutes = (int) (remainingMillis / 1000) / 60;
            timeTextView.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    // 2인용 모드에서 경과 시간 업데이트 (필요하다면 GameController에서 호출)
    public void updateElapsedTimeUI(long elapsedMillis) {
        if (modeInfo == 2 && elapsedTimeTextView != null) {
            int seconds = (int) (elapsedMillis / 1000) % 60;
            int minutes = (int) (elapsedMillis / 1000) / 60;
            elapsedTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }
}

