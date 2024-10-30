package kr.co.example.mobileprogramming.network;

import kr.co.example.mobileprogramming.model.GameState;

public interface DataReceivedListener {
    void onDataReceived(GameState gameState);
    void onConnectionClosed();
}
