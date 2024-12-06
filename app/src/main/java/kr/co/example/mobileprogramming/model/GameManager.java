package kr.co.example.mobileprogramming.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;

public class GameManager {
    private int currentRound;
    private int totalRounds;
    private Difficulty difficulty;
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private GameEventListener gameEventListener;
    private GameErrorListener gameErrorListener;

    public GameManager(Difficulty difficulty, int totalRounds, Player player1, Player player2) {
        this.difficulty = difficulty;
        this.totalRounds = totalRounds;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.currentRound = 1;
    }

    public void initializeBoard(List<Card> boardCards) {
        // 난이도에 따라 보드 크기 및 카드 생성
        int rows;
        int columns;

        switch (difficulty) {
            case EASY:
                rows = 5;
                columns = 5;
                break;
            case HARD:
                rows = 7;
                columns = 7;
                break;
            case NORMAL:
            default:
                rows = 6;
                columns = 6;
                break;
        }

        this.board = new Board(rows, columns, boardCards);
    }

    public boolean flipCard(int position) {
        boolean success = board.flipCard(position);
        if (success) {
            Card card = board.getCardAt(position);
            if (gameEventListener != null) {
                gameEventListener.onCardFlipped(position, card);
            }
            // 추가 로직 (매칭 확인 등)
        } else {
            if (gameErrorListener != null) {
                gameErrorListener.onInvalidMove("선택한 카드를 뒤집을 수 없습니다.");
            }
        }
        return success;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void setGameErrorListener(GameErrorListener listener) {
        this.gameErrorListener = listener;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameResult getGameResult() {
        // 게임 결과를 계산하여 반환
        int player1Score = player1.getScore();
        int player2Score = player2.getScore();
        Player winner = (player1Score > player2Score) ? player1 : player2;
        return new GameResult(winner, player1Score, player2Score);
    }

    public void updateGameState(GameState gameState) {
        // 수신된 게임 상태로 현재 상태를 업데이트
        // 예: 보드 상태, 플레이어 점수, 현재 플레이어 등
    }

    public Board getBoard() {
        return board;
    }
}
