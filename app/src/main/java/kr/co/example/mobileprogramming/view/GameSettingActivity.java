package kr.co.example.mobileprogramming.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.example.mobleprogramming.R;

public class GameSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gamesetting);

        RadioGroup roundGroup = findViewById(R.id.roundGroup);
        RadioGroup difficultyGroup = findViewById(R.id.difficultyGroup);
        RadioGroup modeGroup = findViewById(R.id.modeGroup);
        Button completeButton = findViewById(R.id.completeButton);

        completeButton.setOnClickListener(v -> {
            if (!isAllOptionsSelected(roundGroup, difficultyGroup, modeGroup)) {
                Toast.makeText(this, "모든 항목을 선택해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(createGameIntent(roundGroup, difficultyGroup, modeGroup));
        });
    }

    private boolean isAllOptionsSelected(RadioGroup roundGroup, RadioGroup difficultyGroup, RadioGroup modeGroup) {
        return roundGroup.getCheckedRadioButtonId() != -1 &&
                difficultyGroup.getCheckedRadioButtonId() != -1 &&
                modeGroup.getCheckedRadioButtonId() != -1;
    }

    private int getRoundValue(RadioGroup roundGroup) {
        int selectedRoundId = roundGroup.getCheckedRadioButtonId();
        return selectedRoundId == R.id.roundButton_three ? 3 :
                selectedRoundId == R.id.roundButton_five ? 5 : 3;
    }

    private String getDifficultyValue(RadioGroup difficultyGroup) {
        int selectedDifficultyId = difficultyGroup.getCheckedRadioButtonId();
        return selectedDifficultyId == R.id.easyButton ? "EASY" :
                selectedDifficultyId == R.id.normalButton ? "NORMAL" :
                        selectedDifficultyId == R.id.hardButton ? "HARD" : "NORMAL";
    }

    private int getModeValue(RadioGroup modeGroup) {
        int selectedModeId = modeGroup.getCheckedRadioButtonId();
        return selectedModeId == R.id.playerOneButton ? 1 :
                selectedModeId == R.id.playerTwoButton ? 2 : 1;
    }

    private Intent createGameIntent(RadioGroup roundGroup, RadioGroup difficultyGroup, RadioGroup modeGroup) {
        Intent intent = new Intent(this, GameActivity.class);
        int totalRounds = getRoundValue(roundGroup);
        int currentRound = 1; // 게임 시작 시 현재 라운드는 항상 1
        intent.putExtra("ROUND", currentRound);
        intent.putExtra("TOTAL_ROUNDS", totalRounds);
        intent.putExtra("DIFFICULTY", getDifficultyValue(difficultyGroup));
        intent.putExtra("MODE", getModeValue(modeGroup));
        return intent;
    }
}