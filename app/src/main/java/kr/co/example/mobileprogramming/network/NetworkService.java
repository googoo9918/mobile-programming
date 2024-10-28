package kr.co.example.mobileprogramming.network;

import kr.co.example.mobileprogramming.model.GameState;

public interface NetworkService {
    void connect();
    void disconnect();
    void sendData(GameState gameState);
    void setDataReceivedListener(DataReceivedListener listener);
}

