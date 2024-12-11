package kr.co.example.mobileprogramming.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
import kr.co.example.mobileprogramming.model.ItemType;
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
    private TextView timeTextView;       // 싱글플레이 시간 표시
    private TextView elapsedTimeTextView; // 멀티플레이 시간 표시

    // 싱글플레이 correct/wrong 표시
    private TextView correctCountTextView;
    private TextView wrongCountTextView;

    // 멀티플레이 correct/wrong 표시
    private TextView player1CorrectTextView;
    private TextView player1WrongTextView;
    private TextView player2CorrectTextView;
    private TextView player2WrongTextView;

    private static final int BOARD_SIZE = 36;
    private List<Integer> cardIds;
    private List<Card> boardCards;

    private int roundInfo;
    private int totalRounds;
    private String difficultyInfo;
    private int modeInfo;

    private View pauseOverlay;
    private ImageButton pauseButton;
    private Button resumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        roundInfo = intent.getIntExtra("ROUND", 1);
        difficultyInfo = intent.getStringExtra("DIFFICULTY");
        modeInfo = intent.getIntExtra("MODE", 1);
        totalRounds = intent.getIntExtra("TOTAL_ROUNDS", 1);

        if (modeInfo == 1) {
            setContentView(R.layout.activity_game_singleplayer);
        } else {
            setContentView(R.layout.activity_game_multiplayer);
        }

        initializeUI();
        Difficulty diffEnum;
        try {
            diffEnum = Difficulty.valueOf(difficultyInfo.toUpperCase());
        } catch (Exception e) {
            diffEnum = Difficulty.NORMAL;
        }

        Player player1 = new Player("Player 1");
        Player player2 = (modeInfo == 2) ? new Player("Player 2") : null;

        GameManager gameManager = new GameManager(diffEnum, totalRounds, roundInfo, player1, player2);
        // currentRound 설정 (여기서는 roundInfo를 currentRound로 삼지는 않았지만 필요하다면 GameManager 수정)
        // gameManager.setCurrentRound(roundInfo); // 필요시 GameManager에 setter 추가.

        NetworkService networkService = new NetworkServiceImpl();

        initializeCardIds();
        setupBoard();
        gameManager.initializeBoard(boardCards);

        gameController = new GameController(this, gameManager, networkService);
        initializePauseUI();
        gameManager.startGame();
    }

    private void initializeUI() {
        if (modeInfo == 1) {
            roundText = findViewById(R.id.chooseround);
            difficultyText = findViewById(R.id.choosedifficulty);
            modeText = findViewById(R.id.choosemode);
            timeTextView = findViewById(R.id.timeTextView);

            correctCountTextView = findViewById(R.id.correctCountTextView);
            wrongCountTextView = findViewById(R.id.wrongCountTextView);

            roundText.setText("라운드: " + roundInfo + "/" + totalRounds);
            difficultyText.setText("난이도: " + difficultyInfo);
            modeText.setText("게임 모드: 1인용");
        } else {
            TextView roundTextMulti = findViewById(R.id.chooseround_multi);
            TextView modeTextMulti = findViewById(R.id.choosemode_multi);
            elapsedTimeTextView = findViewById(R.id.elapsedTimeTextView);

            player1CorrectTextView = findViewById(R.id.player1CorrectTextView);
            player1WrongTextView = findViewById(R.id.player1WrongTextView);
            player2CorrectTextView = findViewById(R.id.player2CorrectTextView);
            player2WrongTextView = findViewById(R.id.player2WrongTextView);

            roundTextMulti.setText("라운드: " + roundInfo + "/" + totalRounds);
            modeTextMulti.setText("게임 모드: 2인용");
        }
    }

    public void updateScoreUI(Player p1, Player p2) {
        if (modeInfo == 1) {
            if (p1 != null && correctCountTextView != null && wrongCountTextView != null) {
                correctCountTextView.setText("Correct: " + p1.getCorrectCount());
                wrongCountTextView.setText("Wrong: " + p1.getWrongCount());
            }
        } else {
            if (p1 != null && p2 != null &&
                    player1CorrectTextView != null && player1WrongTextView != null &&
                    player2CorrectTextView != null && player2WrongTextView != null) {

                player1CorrectTextView.setText("P1 Correct:" + p1.getCorrectCount());
                player1WrongTextView.setText("P1 Wrong:" + p1.getWrongCount());

                player2CorrectTextView.setText("P2 Correct:" + p2.getCorrectCount());
                player2WrongTextView.setText("P2 Wrong:" + p2.getWrongCount());
            }
        }
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

        // 모드 체크 (1인용/2인용). 2인용일 때만 아이템 카드 로직 적용
        if (modeInfo == 2) {

            List<Integer> itemCardIds = new ArrayList<>();
            itemCardIds.add(R.drawable.item_doublescore);
            itemCardIds.add(R.drawable.item_turnextension);
            itemCardIds.add(R.drawable.item_remove);
            itemCardIds.add(R.drawable.item_revealcard);
            itemCardIds.add(R.drawable.item_steal);

            //아이템 카드용 리소스 4개 선택
            Collections.shuffle(itemCardIds);
            List<Integer> selectedItemCardIds = itemCardIds.subList(0, 4);

            // 일반 카드 14쌍(28장) 선택
            Collections.shuffle(cardIds);
            List<Integer> selectedNormalCardIds = cardIds.subList(0, 14); // 14개 선택

            // boardCards에 일반 카드 14쌍 + 아이템 카드 4쌍 추가
            for (Integer normalId : selectedNormalCardIds) {
                boardCards.add(new Card(normalId, CardType.NORMAL));
                boardCards.add(new Card(normalId, CardType.NORMAL));
            }

            for (Integer itemId : selectedItemCardIds) {
                ItemType itemType = mapImageResourceToItemType(itemId);
                boardCards.add(new ItemCard(itemId, itemType));
                boardCards.add(new ItemCard(itemId, itemType));
            }

            Collections.shuffle(boardCards);

        } else {
            Collections.shuffle(cardIds);
            List<Integer> selectedCards = cardIds.subList(0, BOARD_SIZE / 2);
            for (Integer cardId : selectedCards) {
                boardCards.add(new Card(cardId, CardType.NORMAL));
                boardCards.add(new Card(cardId, CardType.NORMAL));
            }
            Collections.shuffle(boardCards);
        }
    }

    private ItemType mapImageResourceToItemType(int itemResourceId) {
        if (itemResourceId == R.drawable.item_doublescore) {
            return ItemType.DOUBLE_SCORE;
        } else if (itemResourceId == R.drawable.item_turnextension) {
            return ItemType.TURN_EXTENSION;
        } else if (itemResourceId == R.drawable.item_remove) {
            return ItemType.REMOVE_OPPONENT_ITEM;
        } else if (itemResourceId == R.drawable.item_revealcard) {
            return ItemType.REVEAL_CARD;
        } else if (itemResourceId == R.drawable.item_steal) {
            return ItemType.STEAL_ITEM;
        } else {
            throw new IllegalArgumentException("Unknown item resource id: " + itemResourceId);
        }
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
        // 아이템 다이얼로그 예: 미구현
    }

    public void updatePlayerItems(List<ItemEffect> items) {
        // 필요시 구현
    }

    public void updateCurrentPlayer(Player player) {
        // 2인용일 경우 현재 플레이어 정보 표시 가능
    }

    public void showMatch(int position1, int position2) {
        // 매칭 연출
    }

    public void showItemAcquired(ItemCard itemCard) {
        // 아이템 획득 알림
    }

    public void navigateToResultActivity(GameResult gameResult, long timeSpent) {
        // Controller에서 처리하므로 여기는 사용 안 할 수도 있음
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
            } else {
                cardImage.setImageResource(R.drawable.back);
            }
        }
    }

    public void updateTimeUI(long remainingMillis) {
        if (modeInfo == 1 && timeTextView != null) {
            int seconds = (int) (remainingMillis / 1000) % 60;
            int minutes = (int) (remainingMillis / 1000) / 60;
            timeTextView.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    public void updateElapsedTimeUI(long elapsedMillis) {
        if (modeInfo == 2 && elapsedTimeTextView != null) {
            int seconds = (int) (elapsedMillis / 1000) % 60;
            int minutes = (int) (elapsedMillis / 1000) / 60;
            elapsedTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void initializePauseUI() {
        try {
            // Pause 버튼 설정
            pauseButton = findViewById(R.id.pauseButton);
            if (pauseButton != null) {
                pauseButton.setOnClickListener(v -> {
                    if (gameController != null) {
                        gameController.pauseGame();
                    }
                });
            }

            // Pause 오버레이 설정
            pauseOverlay = getLayoutInflater().inflate(R.layout.pause_overlay, null);
            if (pauseOverlay != null) {
                resumeButton = pauseOverlay.findViewById(R.id.resumeButton);
                if (resumeButton != null) {
                    resumeButton.setOnClickListener(v -> {
                        if (gameController != null) {
                            gameController.resumeGame();
                        }
                    });
                }

                // 오버레이를 루트 레이아웃에 추가
                ViewGroup rootView = findViewById(R.id.root_layout);  // activity_game_singleplayer.xml의 최상위 레이아웃 ID
                if (rootView != null) {
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    pauseOverlay.setVisibility(View.GONE);
                    rootView.addView(pauseOverlay, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "일시정지 UI 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showPauseOverlay() {
        pauseOverlay.setVisibility(View.VISIBLE);
        pauseButton.setEnabled(false);
    }

    public void hidePauseOverlay() {
        pauseOverlay.setVisibility(View.GONE);
        pauseButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 게임이 진행 중일 때만 일시정지 실행
        if (!gameController.isPaused() && !gameController.isUserPaused() && !isFinishing()) {
            gameController.pauseGame();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 홈 버튼으로 앱이 백그라운드로 갈 때도 일시정지 상태 유지
        if (!gameController.isPaused() && !gameController.isUserPaused() && !isFinishing()) {
            gameController.pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}