package kr.co.example.mobileprogramming.view;

import static android.os.Build.VERSION_CODES.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private TextView resultTextView;
    private Button restartButton;
    private Button mainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // UI 요소 초기화
        resultTextView = findViewById(R.id.result_text_view);
        restartButton = findViewById(R.id.restart_button);
        mainMenuButton = findViewById(R.id.main_menu_button);

        // 결과 데이터 표시
        // Intent에서 게임 결과를 받아와서 resultTextView에 표시

        // 버튼 이벤트 처리
        restartButton.setOnClickListener(v -> {
            // 게임 재시작 로직
        });

        mainMenuButton.setOnClickListener(v -> {
            // 메인 메뉴로 이동
        });
    }
}
