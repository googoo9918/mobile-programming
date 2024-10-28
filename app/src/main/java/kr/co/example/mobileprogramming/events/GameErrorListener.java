package kr.co.example.mobileprogramming.events;

public interface GameErrorListener {
    void onNetworkError(String message);
    void onGameLogicError(String message);
    void onInvalidMove(String message);
}

