package kr.co.example.mobileprogramming.network;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import kr.co.example.mobileprogramming.model.GameState;

public class NetworkServiceImpl implements NetworkService {
    private DataReceivedListener dataReceivedListener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenThread;

    // 실제 서버 IP와 포트로 수정해야 함
    private static final String SERVER_IP = "your.server.ip";
    private static final int SERVER_PORT = 12345;

    @Override
    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                listenThread = new Thread(() -> {
                    Gson gson = new Gson();
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            GameState receivedState = gson.fromJson(line, GameState.class);
                            if (dataReceivedListener != null) {
                                dataReceivedListener.onDataReceived(receivedState);
                            }
                        }
                    } catch (IOException e) {
                        // 연결 종료 시 onConnectionClosed 호출
                        if (dataReceivedListener != null) {
                            dataReceivedListener.onConnectionClosed();
                        }
                    }
                });
                listenThread.start();

            } catch (IOException e) {
                // 연결 실패 시 처리 로직 (필요하다면 onNetworkError 호출)
                if (dataReceivedListener != null && e.getMessage() != null) {
                    // dataReceivedListener.onNetworkError("서버 연결 실패: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (listenThread != null && listenThread.isAlive()) {
                listenThread.interrupt();
            }
        } catch (IOException e) {
            // 예외처리 필요시 추가
        }
    }

    @Override
    public void sendData(GameState gameState) {
        Gson gson = new Gson();
        String json = gson.toJson(gameState);
        if (out != null) {
            out.println(json);
        }
    }

    @Override
    public void setDataReceivedListener(DataReceivedListener listener) {
        this.dataReceivedListener = listener;
    }
}
