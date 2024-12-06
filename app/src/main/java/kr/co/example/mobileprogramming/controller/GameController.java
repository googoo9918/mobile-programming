package kr.co.example.mobileprogramming.controller;

import android.util.Log;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;
import kr.co.example.mobileprogramming.events.OnItemSelectedListener;
import kr.co.example.mobileprogramming.model.Card;
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

    public GameController(GameActivity gameActivity, GameManager gameManager, NetworkService networkService) {
        this.gameActivity = gameActivity;
        this.gameManager = gameManager;
        this.networkService = networkService;

        this.gameManager.setGameEventListener(this);
        this.gameManager.setGameErrorListener(this);
        this.networkService.setDataReceivedListener(this);

        this.networkService.connect();
    }

    // 사용자 입력 처리 메서드
    public void onCardSelected(int position) {
        boolean success = gameManager.flipCard(position);
        if (success) {
            Card card = gameManager.getBoard().getCardAt(position);
            gameActivity.updateCard(position, card);
            Log.d("GameAController","Card flipped at" + position);
        }
        else {
            Log.d("GameAController","Card flipped failed at" + position);
            onInvalidMove("선택한 카드를 뒤집을 수 없습니다.");
        }
    }

    public void onItemUseRequested() {
        gameActivity.showItemDialog(gameManager.getCurrentPlayer().getItems());
    }

    // OnItemSelectedListener 구현 메서드
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
            case EASY: return 2000;  // 1초
            case NORMAL: return 1000; // 0.5초
            case HARD: return 500;   // 0.3초
            default: return 500;
        }
    }

    public void revealAllCardsTemporarily() {
        Log.d("Controller", "reveal card called");
        for (int i = 0; i < gameManager.getBoard().getCards().size(); i++) {
            Card card = gameManager.getBoard().getCardAt(i);
            if (!card.isFlipped()) {
                card.flip();
            }
        }
        gameActivity.displayCards();

        int revealTime = getRevealTime(); // 난이도별 공개 시간
        new android.os.Handler().postDelayed(() -> {
            for (int i = 0; i < gameManager.getBoard().getCards().size(); i++) {
                Card card = gameManager.getBoard().getCardAt(i);
                if (card.isFlipped()) {
                    card.flip();
                }
            }
            gameActivity.displayCards();
        }, revealTime);
    }


    // GameEventListener 구현 메서드
    @Override
    public void onGameStarted() {
        gameActivity.initializeGameBoard(gameManager.getBoard());
        revealAllCardsTemporarily();
        Log.d("Controller", "game started");
    }

    @Override
    public void onCardFlipped(int position, Card card) {
        gameActivity.updateCard(position, card);
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
        gameActivity.navigateToResultActivity(gameManager.getGameResult());
    }

    // GameErrorListener 구현 메서드
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

    // DataReceivedListener 구현 메서드
    @Override
    public void onDataReceived(GameState gameState) {
        gameManager.updateGameState(gameState);
        gameActivity.refreshUI();
    }

    @Override
    public void onConnectionClosed() {
        onNetworkError("서버와의 연결이 종료되었습니다.");
    }

    @Override
    public void onGameStateUpdated() {
        gameActivity.refreshUI();
    }

    public void onDestroy() {
        networkService.disconnect();
    }
}

