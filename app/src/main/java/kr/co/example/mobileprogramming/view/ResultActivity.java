package kr.co.example.mobileprogramming.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.example.mobleprogramming.R;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView difficultyText = findViewById(R.id.difficultyResult);
        TextView timeText = findViewById(R.id.timeSpentResult);
        TextView infoText = findViewById(R.id.infoResult);
        Button nextRoundButton = findViewById(R.id.nextRoundButton);

        Intent intent = getIntent();
        int mode = intent.getIntExtra("MODE", 1);
        String difficulty = intent.getStringExtra("DIFFICULTY");
        long timeSpent = intent.getLongExtra("TIME_SPENT", 0);
        int totalRounds = intent.getIntExtra("TOTAL_ROUNDS", 1);
        int currentRound = intent.getIntExtra("CURRENT_ROUND", 1);

        difficultyText.setText("난이도: " + difficulty);
        int seconds = (int) (timeSpent / 1000) % 60;
        int minutes = (int) (timeSpent / 1000) / 60;

        if (mode == 1) {
            int correct = intent.getIntExtra("CORRECT", 0);
            int wrong = intent.getIntExtra("WRONG", 0);
            infoText.setText("맞힌 횟수: " + correct + ", 틀린 횟수: " + wrong);
            timeText.setText(String.format("걸린 시간: %02d:%02d", minutes, seconds));
        } else {
            String winnerName = intent.getStringExtra("WINNER_NAME");
            int winnerCorrect = intent.getIntExtra("WINNER_CORRECT", 0);
            int winnerWrong = intent.getIntExtra("WINNER_WRONG", 0);
            infoText.setText("승자: " + winnerName + "\n맞힌 횟수: " + winnerCorrect + ", 틀린 횟수: " + winnerWrong);
            timeText.setText("");
        }

        if (currentRound < totalRounds) {
            // 다음 라운드로 이동
            nextRoundButton.setVisibility(View.VISIBLE);
            nextRoundButton.setText("다음 라운드로");
            nextRoundButton.setOnClickListener(v -> {
                Intent nextIntent = new Intent(this, GameActivity.class);
                nextIntent.putExtra("ROUND", currentRound + 1);
                nextIntent.putExtra("TOTAL_ROUNDS", totalRounds);
                nextIntent.putExtra("DIFFICULTY", difficulty);
                nextIntent.putExtra("MODE", mode);
                startActivity(nextIntent);
                finish();
            });
        } else {
            // 마지막 라운드이면 처음 화면으로 돌아가는 버튼 표시
            nextRoundButton.setVisibility(View.VISIBLE);
            nextRoundButton.setText("처음 화면으로");
            nextRoundButton.setOnClickListener(v -> {
                Intent mainIntent = new Intent(this, GameSettingActivity.class);
                startActivity(mainIntent);
                finish();
            });
        }
    }
}
