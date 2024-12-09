package kr.co.example.mobileprogramming.controller;

import android.os.Handler;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;
import kr.co.example.mobileprogramming.events.OnItemSelectedListener;
import kr.co.example.mobileprogramming.model.Card;
import kr.co.example.mobileprogramming.model.CardType;
import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.GameState;
import kr.co.example.mobileprogramming.model.ItemCard;
import kr.co.example.mobileprogramming.model.ItemType;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.network.DataReceivedListener;
import kr.co.example.mobileprogramming.network.NetworkService;
import kr.co.example.mobileprogramming.view.GameActivity;

public class GameController implements GameEventListener, GameErrorListener, OnItemSelectedListener, DataReceivedListener {
    private GameActivity gameActivity;
    private GameManager gameManager;
    private NetworkService networkService;

    private Handler gameTimerHandler = new Handler();
    private Runnable gameTimerRunnable;

    private long timeRemaining;    // 싱글플레이 모드용 남은 시간
    private long elapsedTime = 0;  // 멀티플레이 모드용 경과 시간

    private boolean isSinglePlayer;

    public GameController(GameActivity gameActivity, GameManager gameManager, NetworkService networkService) {
        this.gameActivity = gameActivity;
        this.gameManager = gameManager;
        this.networkService = networkService;

        this.gameManager.setGameEventListener(this);
        this.gameManager.setGameErrorListener(this);

        this.networkService.setDataReceivedListener(this);
        this.networkService.connect();

        // 모드 판별
        Player player2 = gameManager.getOpponent(gameManager.getCurrentPlayer());
        isSinglePlayer = (player2 == null);

        setupTimer();
    }

    private void setupTimer() {
        if (isSinglePlayer) {
            // 난이도에 따른 남은 시간 설정
            switch (gameManager.getDifficulty()) {
                case EASY:
                    timeRemaining = 5 * 60 * 1000; // 5분
                    break;
                case NORMAL:
                    timeRemaining = 3 * 60 * 1000; // 3분
                    break;
                case HARD:
                    timeRemaining = 60 * 1000; // 1분
                    break;
                default:
                    timeRemaining = 3 * 60 * 1000;
            }

            gameTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    timeRemaining -= 1000;
                    if (timeRemaining <= 0) {
                        onGameOver();
                    } else {
                        gameActivity.updateTimeUI(timeRemaining);
                        gameTimerHandler.postDelayed(this, 1000);
                    }
                }
            };
        } else {
            // 멀티플레이 모드: 경과 시간 표시 (예: 게임 시작부터 흘러가는 시간)
            elapsedTime = 0;
            gameTimerRunnable = new Runnable() {
                @Override
                public void run() {
                    elapsedTime += 1000;
                    gameActivity.updateElapsedTimeUI(elapsedTime);
                    gameTimerHandler.postDelayed(this, 1000);
                }
            };
        }
    }

    private void startGameTimer() {
        gameTimerHandler.post(gameTimerRunnable);
    }

    private void stopGameTimer() {
        gameTimerHandler.removeCallbacks(gameTimerRunnable);
    }

    public void onCardSelected(int position) {
        boolean success = gameManager.selectCard(position);
        if (success) {
            gameActivity.refreshUI();
            // 카드 두 장 선택 후 매칭 결과 대기 (싱글/멀티 공통 로직)
            if (gameManager.getSelectedCards().size() == 2) {
                new Handler().postDelayed(() -> {
                    gameActivity.refreshUI();
                    checkGameOverCondition();
                }, 1000);
            } else {
                gameActivity.refreshUI();
                checkGameOverCondition();
            }
        } else {
            onInvalidMove("선택한 카드를 뒤집을 수 없습니다.");
        }
    }

    public void onItemUseRequested() {
        gameActivity.showItemDialog(gameManager.getCurrentPlayer().getItems());
    }

    @Override
    public void onItemSelected(ItemType itemType) {
        Player currentPlayer = gameManager.getCurrentPlayer();
        boolean used = currentPlayer.useItem(itemType, gameManager);
        if (used) {
            gameActivity.updatePlayerItems(currentPlayer.getItems());
        } else {
            onGameLogicError("아이템을 사용할 수 없습니다.");
        }
    }

    private int getRevealTime() {
        switch (gameManager.getDifficulty()) {
            case EASY: return 10000; //10초`
            case NORMAL: return 5000; //5초
            case HARD: return 3000; //3초
            default: return 1000;
        }
    }

    public void revealAllCardsTemporarily() {
        for (int i = 0; i < gameManager.getBoard().getCards().size(); i++) {
            Card card = gameManager.getBoard().getCardAt(i);
            if (!card.isFlipped() && card.getType() == CardType.NORMAL) {
                card.flip();
            }
        }

        gameActivity.refreshUI();

        int revealTime = getRevealTime();
        new Handler().postDelayed(() -> {
            for (int i = 0; i < gameManager.getBoard().getCards().size(); i++) {
                Card card = gameManager.getBoard().getCardAt(i);
                // 매칭되지 않은 카드만 다시 뒤집기
                if (card.isFlipped() && !card.isMatched()) {
                    card.flip();
                }
            }
            gameActivity.refreshUI();
        }, revealTime);
    }

    private void checkGameOverCondition() {
        // 모든 카드 매칭 여부 확인
        if (gameManager.isAllCardsMatched()) {
            onGameOver();
        }
    }

    // GameEventListener 구현
    @Override
    public void onGameStarted() {
        gameActivity.initializeGameBoard(gameManager.getBoard());
        gameActivity.displayCards();
        revealAllCardsTemporarily();
        startGameTimer();
    }

    @Override
    public void onCardFlipped(int position, Card card) {
        gameActivity.refreshUI();
    }

    @Override
    public void onMatchFound(int position1, int position2) {
        gameActivity.showMatch(position1, position2);
    }

    @Override
    public void onItemAcquired(ItemCard itemCard) {
        gameActivity.showItemAcquired(itemCard);
    }

    @Override
    public void onTurnChanged(Player currentPlayer) {
        gameActivity.updateCurrentPlayer(currentPlayer);
    }

    @Override
    public void onGameOver() {
        stopGameTimer();
        gameActivity.navigateToResultActivity(gameManager.getGameResult());
    }

    @Override
    public void onGameStateUpdated() {
        gameActivity.refreshUI();
    }

    // GameErrorListener 구현
    @Override
    public void onNetworkError(String message) {
        gameActivity.showErrorDialog("네트워크 오류", message);
    }

    @Override
    public void onGameLogicError(String message) {
        gameActivity.showErrorDialog("게임 오류", message);
    }

    @Override
    public void onInvalidMove(String message) {
        gameActivity.showToast(message);
    }

    // DataReceivedListener 구현
    @Override
    public void onDataReceived(GameState gameState) {
        gameManager.updateGameState(gameState);
        gameActivity.refreshUI();
    }

    @Override
    public void onConnectionClosed() {
        onNetworkError("서버와의 연결이 종료되었습니다.");
    }

    public void onDestroy() {
        stopGameTimer();
        networkService.disconnect();
    }
}


