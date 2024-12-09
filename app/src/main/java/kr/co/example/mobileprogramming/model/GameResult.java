package kr.co.example.mobileprogramming.model;

public class GameResult {
    private Player winner;
    private int player1Score;
    private int player2Score;

    public GameResult(Player winner, int player1Score, int player2Score) {
        this.winner = winner;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public Player getWinner() {
        return winner;
    }
}

