package kr.co.example.mobileprogramming.network;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import kr.co.example.mobileprogramming.model.Board;
import kr.co.example.mobileprogramming.model.Difficulty;
import kr.co.example.mobileprogramming.model.GameState;
import kr.co.example.mobileprogramming.model.Player;
import kr.co.example.mobileprogramming.view.GameActivity;

public class NetworkServiceImpl implements NetworkService {

    private DatabaseReference gamesRef;

    private String roomId;
    public boolean isPlayer1;

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void sendData(GameState gameState) {
    }

    @Override
    public void setDataReceivedListener(DataReceivedListener listener) {
    }

    public void connect(int roundInfo, String difficultyInfo, Consumer<Boolean> callback) {
        gamesRef = FirebaseDatabase.getInstance().getReference("games");

        findExistingRoom(roundInfo, difficultyInfo, existingRoomId -> {
            if (existingRoomId != null) {
                roomId = existingRoomId;
                isPlayer1 = false; // 기존 방에 참여자는 Player 2
                joinExistingRoom(existingRoomId, success -> {
                    if (success) {
                        Log.d("Firebase", "기존 방에 합류: " + existingRoomId);
                        callback.accept(true);
                    } else {
                        Log.e("Firebase", "기존 방에 합류 실패");
                        callback.accept(false);
                    }
                });
            } else {
                createNewRoom(roundInfo, difficultyInfo, success -> {
                    if (success) {
                        Log.d("Firebase", "새로운 방 생성: " + roomId);
                        callback.accept(true);
                    } else {
                        Log.e("Firebase", "새로운 방 생성 실패");
                        callback.accept(false);
                    }
                });
            }
        });
    }

    private void findExistingRoom(int roundInfo, String difficultyInfo, Consumer<String> callback) {
        gamesRef.orderByChild("state").equalTo("waiting").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> roomData = (Map<String, Object>) snapshot.getValue();
                    if (roomData != null) {
                        int roomRound = ((Long) roomData.get("roundInfo")).intValue();
                        String roomDifficulty = (String) roomData.get("difficultyInfo");

                        if (roomRound == roundInfo && roomDifficulty.equals(difficultyInfo)) {
                            callback.accept(snapshot.getKey()); // 적합한 방 ID 반환
                            return;
                        }
                    }
                }
                callback.accept(null); // 적합한 방이 없으면 null 반환
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to find existing room", databaseError.toException());
                callback.accept(null);
            }
        });
    }


    private void joinExistingRoom(String roomId, Consumer<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("state", "playing"); // 상태를 플레이 중으로 변경

        gamesRef.child(roomId).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Joined existing room: " + roomId);
                        callback.accept(true);
                    } else {
                        Log.e("Firebase", "Failed to join room", task.getException());
                        callback.accept(false);
                    }
                });
    }

    private void createNewRoom(int roundInfo, String difficultyInfo, Consumer<Boolean> callback) {
        String newRoomId = gamesRef.push().getKey();
        if (newRoomId == null) {
            Log.e("Firebase", "Failed to generate room ID");
            callback.accept(false);
            return;
        }

        roomId = newRoomId;
        isPlayer1 = true;

        Map<String, Object> roomData = new HashMap<>();
        roomData.put("roomId", newRoomId);
        roomData.put("isPlayer1", true);
        roomData.put("state", "waiting");
        roomData.put("roundInfo", roundInfo);
        roomData.put("difficultyInfo", difficultyInfo);

        gamesRef.child(newRoomId).setValue(roomData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Created new room: " + newRoomId);
                        callback.accept(true);
                    } else {
                        Log.e("Firebase", "Failed to create room", task.getException());
                        callback.accept(false);
                    }
                });
    }
}
