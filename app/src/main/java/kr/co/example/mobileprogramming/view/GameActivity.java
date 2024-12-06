package kr.co.example.mobileprogramming.view;

import kr.co.example.mobileprogramming.model.CardType;
import kr.co.example.mobleprogramming.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.reflect.Field;

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
//    private RecyclerView gameBoard;
//    private TextView scoreTextView;
//    private Button itemUseButton;

    private TextView roundText;
    private TextView difficultyText;
    private TextView modeText;

    private static final int BOARD_SIZE = 36; // 6x6 보드
    private static final int PAIRS_COUNT = 16; // 필요한 페어 수

    private List<Integer> cardIds; // 드로어블 리소스 ID 목록
    private List<Card> boardCards; // 보드에 배치될 카드 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int roundInfo = intent.getIntExtra("ROUND", 3);
        String difficultyInfo = intent.getStringExtra("DIFFICULTY");
        int modeInfo = intent.getIntExtra("MODE", 1);

        roundText = findViewById(R.id.chooseround);
        difficultyText = findViewById(R.id.choosedifficulty);
        modeText = findViewById(R.id.choosemode);

        roundText.setText("라운드: " + String.valueOf(roundInfo));
        difficultyText.setText("난이도: " + difficultyInfo);
        modeText.setText("게임 모드: " + String.valueOf(modeInfo)+"인용");

        // UI 요소 초기화
//        gameBoard = findViewById(R.id.game_board);
//        scoreTextView = findViewById(R.id.score_text_view);
//        itemUseButton = findViewById(R.id.item_use_button);

        // 게임 설정 정보 가져오기 (예: Intent로부터)
        Difficulty difficulty = Difficulty.NORMAL;
//        int totalRounds = 5;
        Player player1 = new Player("Player 1");
        //Player player2 = new Player("Player 2");
        Player player2 = (modeInfo == 2) ? new Player("Player 2") : null;

        // GameManager 및 NetworkService 생성
        GameManager gameManager = new GameManager(Difficulty.NORMAL, roundInfo, player1, player2); // fix difficulty NORMAL
        NetworkService networkService = new NetworkServiceImpl();

        initializeCardIds();
        setupBoard();
        gameManager.initializeBoard(boardCards);

        // GameController 생성
        gameController = new GameController(this, gameManager, networkService);

        gameManager.startGame();

        // 카드 클릭 이벤트 처리
        // 카드 어댑터를 설정하고 클릭 리스너에서 gameController.onCardSelected(position) 호출
    }

    private void initializeCardIds() {
        // drawable 폴더의 모든 카드 이미지 리소스 ID를 리스트에 추가
        cardIds = new ArrayList<>();
        Field[] drawableFields = R.drawable.class.getFields();
        for (Field field : drawableFields) {
            if (field.getName().startsWith("card_")) { // card_로 시작하는 리소스만 선택
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
//        boardCards = new Integer[BOARD_SIZE];
//        Arrays.fill(boardCards, null);

        // 카드 중 18개(36/2)를 랜덤하게 선택하도록 수정
        Collections.shuffle(cardIds);
        List<Integer> selectedCards = cardIds.subList(0, BOARD_SIZE/2);

        // 선택된 카드들을 두 번씩 보드에 랜덤하게 배치
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);

//        int positionIndex = 0;
        for (Integer cardId : selectedCards) {
            // 각 카드를 두 번 배치
            boardCards.add(new Card(cardId, CardType.NORMAL)); // 첫 번째 카드
            boardCards.add(new Card(cardId, CardType.NORMAL)); // 두 번째 카드

//            boardCards[positions.get(positionIndex)].id = cardId;
//            boardCards[positions.get(positionIndex + 1)].id = cardId;
//            positionIndex += 2;
        }
        Collections.shuffle(boardCards);

    }

    public void displayCards() {
        GridLayout gridLayout = findViewById(R.id.cardGrid);
        gridLayout.removeAllViews();

        gridLayout.setRowCount(6);
        gridLayout.setColumnCount(6);
        int index = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int cardSize = (screenWidth - 150) / 6;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                FrameLayout cardFrame = new FrameLayout(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cardSize;
                params.height = cardSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.setMargins(4, 4, 4, 4);

                ImageView cardImage = new ImageView(this);
                cardImage.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));
                cardImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

                cardImage.setImageResource(R.drawable.back);

                Card currentCard = boardCards.get(index);
                cardImage.setTag(currentCard);

                if (currentCard.isFlipped()) {
                    cardImage.setImageResource(currentCard.getId()); // 앞면 이미지
                } else {
                    cardImage.setImageResource(R.drawable.back); // 뒷면 이미지
                }

                final int cardIndex = index;
//                cardImage.setOnClickListener(v -> onCardClick(cardImage, cardIndex));
                cardImage.setOnClickListener(v -> gameController.onCardSelected(cardIndex));

                cardFrame.addView(cardImage);
                cardFrame.setLayoutParams(params);
                gridLayout.addView(cardFrame);

                index++;
            }
        }
    }

    private void onCardClick(ImageView cardImage, int position) {
        // 카드 클릭 시 카드를 앞면으로 뒤집기
        Card currentCard = boardCards.get(position);
        Integer cardId = currentCard.getId();

        if (cardId != null) {
            cardImage.setImageResource(cardId);
        }
        // 여기에 게임 로직 추가 (매칭 확인 등)
    }

    // UI 업데이트 메서드
    public void initializeGameBoard(Board board) {
        // 게임 보드 초기화
    }

    public void updateCard(int position, Card card) {
        // 카드 상태 업데이트
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
        Log.d("GameActivity", "Refreshing UI...");

        GridLayout gridLayout = findViewById(R.id.cardGrid);

        Log.d("GameActivity", "Before displayCards - GridLayout child count: " + gridLayout.getChildCount());

        // Check if GridLayout is initialized
        if (gridLayout == null) {
            Log.e("GameActivity", "GridLayout is not initialized.");
            return;
        }

        int totalCards = boardCards.size();

        for (int i = 0; i < totalCards; i++) {
            Card card = boardCards.get(i);
            FrameLayout cardFrame = (FrameLayout) gridLayout.getChildAt(i);

            if (cardFrame == null) {
                Log.e("GameActivity", "Card frame at index " + i + " is null.");
                continue;
            }

            ImageView cardImage = (ImageView) cardFrame.getChildAt(0);
            if (cardImage == null) {
                Log.e("GameActivity", "Card image at index " + i + " is null.");
                continue;
            }

            Object tag = cardImage.getTag();
            if (card.isFlipped()) {
                // Only update if the card is not already flipped
                if (tag == null || !tag.equals("flipped")) {
                    cardImage.setImageResource(card.getId());
                    cardImage.setTag("flipped");
                    Log.d("GameActivity", "Card at index " + i + " is now flipped.");
                }
            } else {
                // Only update if the card is not already back
                if (tag == null || !tag.equals("back")) {
                    cardImage.setImageResource(R.drawable.back);
                    cardImage.setTag("back");
                    Log.d("GameActivity", "Card at index " + i + " is now back.");
                }
            }
        }
    }


}
