package kr.co.example.mobileprogramming.controller;

import android.content.Intent;
import android.os.Handler;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;
import kr.co.example.mobileprogramming.events.OnItemSelectedListener;
import kr.co.example.mobileprogramming.model.Card;
import kr.co.example.mobileprogramming.model.CardType;
import kr.co.example.mobileprogramming.model.GameManager;
import kr.co.example.mobileprogramming.model.GameResult;
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

    private long timeRemaining;
    private long initialTime;
    private long elapsedTime;
    private boolean isSinglePlayer;

    public GameController(GameActivity gameActivity, GameManager gameManager, NetworkService networkService) {
        this.gameActivity = gameActivity;
        this.gameManager = gameManager;
        this.networkService = networkService;

        this.gameManager.setGameEventListener(this);
        this.gameManager.setGameErrorListener(this);

        this.networkService.setDataReceivedListener(this);
        this.networkService.connect();

        isSinglePlayer = (gameManager.getPlayer2() == null);

        setupTimer();
    }

    private void setupTimer() {
        if (isSinglePlayer) {
            switch (gameManager.getDifficulty()) {
                case EASY:
                    initialTime = 5 * 60 * 1000; //5분
                    break;
                case NORMAL:
                    initialTime = 3 * 60 * 1000; //3분
                    break;
                case HARD:
                    initialTime = 60 * 1000; //1분
                    break;
                default:
                    initialTime = 3 * 60 * 1000;
            }
            timeRemaining = initialTime;
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
            updateScoreUI();
            if (gameManager.getSelectedCards().size() == 2) {
                new Handler().postDelayed(() -> {
                    gameActivity.refreshUI();
                    updateScoreUI();
                    checkGameOverCondition();
                }, 1000);
            } else {
                checkGameOverCondition();
            }
        } else {
            onInvalidMove("선택한 카드를 뒤집을 수 없습니다.");
        }
    }

    // 모드별 점수 UI 갱신
    private void updateScoreUI() {
        Player p1 = gameManager.getPlayer1();
        Player p2 = gameManager.getPlayer2();
        gameActivity.updateScoreUI(p1, p2);
    }

    private void checkGameOverCondition() {
        if (gameManager.isAllCardsMatched()) {
            onGameOver();
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
            case EASY: return 10000; //10초
            case NORMAL: return 50000; //5초
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
                if (card.isFlipped() && !card.isMatched()) {
                    card.flip();
                }
            }
            gameActivity.refreshUI();
        }, revealTime);
    }

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
        long timeSpent;
        if (isSinglePlayer) {
            timeSpent = initialTime - timeRemaining;
        } else {
            timeSpent = elapsedTime;
        }
        GameResult result = gameManager.getGameResult();
        Player winner = result.getWinner();

        Intent intent = new Intent(gameActivity, kr.co.example.mobileprogramming.view.ResultActivity.class);
        intent.putExtra("MODE", isSinglePlayer ? 1 : 2);
        intent.putExtra("DIFFICULTY", gameManager.getDifficulty().name());
        intent.putExtra("TIME_SPENT", timeSpent);
        intent.putExtra("TOTAL_ROUNDS", gameManager.getTotalRounds());
        intent.putExtra("CURRENT_ROUND", gameManager.getCurrentRound());

        if (isSinglePlayer) {
            // 1인용: currentPlayer는 player1
            Player p = gameManager.getPlayer1();
            intent.putExtra("CORRECT", p.getCorrectCount());
            intent.putExtra("WRONG", p.getWrongCount());
        } else {
            // 2인용: 승자 정보
            intent.putExtra("WINNER_NAME", winner.getName());
            intent.putExtra("WINNER_CORRECT", winner.getCorrectCount());
            intent.putExtra("WINNER_WRONG", winner.getWrongCount());
        }

        gameActivity.startActivity(intent);
        gameActivity.finish();
    }

    @Override
    public void onGameStateUpdated() {
        gameActivity.refreshUI();
    }

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


