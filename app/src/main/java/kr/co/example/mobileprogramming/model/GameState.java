package kr.co.example.mobileprogramming.model;

import java.io.Serializable;

public class GameState implements Serializable {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int currentRound;

    public GameState(Board board, Player player1, Player player2, Player currentPlayer, int currentRound) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.currentRound = currentRound;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    // 필요한 경우 Setter 메서드 추가
}

