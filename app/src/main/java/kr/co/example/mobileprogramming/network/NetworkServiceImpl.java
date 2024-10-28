package kr.co.example.mobileprogramming.network;

import kr.co.example.mobileprogramming.model.GameState;

public class NetworkServiceImpl implements NetworkService {
    private DataReceivedListener dataReceivedListener;

    @Override
    public void connect() {
        // Todo: 서버 연결 로직 구현
    }

    @Override
    public void disconnect() {
        // Todo: 서버 연결 해제 로직
    }

    @Override
    public void sendData(GameState gameState) {
        // Todo: 데이터 전송 로직
    }

    @Override
    public void setDataReceivedListener(DataReceivedListener listener) {
        this.dataReceivedListener = listener;
    }
}
