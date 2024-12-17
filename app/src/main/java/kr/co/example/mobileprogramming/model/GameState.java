package kr.co.example.mobileprogramming.model;

import java.io.Serializable;

public class GameState implements Serializable {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private int currentRound;

    private String roomId;
    private String state; // waiting or playing

    public GameState() {}

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

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public void setCurrentRound(int round) {
            currentRound = round;
    }
    public String getRoomId() { return roomId; }

    public String getState() { return state; }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setState(boolean isPlaying) {
        state = isPlaying ? "playing" : "waiting";
    }
}

