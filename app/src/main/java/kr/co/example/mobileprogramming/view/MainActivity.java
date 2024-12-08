package kr.co.example.mobileprogramming.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kr.co.example.mobleprogramming.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);

        //Game Start 버튼 클릭 시 GameSettingActivity 로 화면 전환
        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameSettingActivity.class);
            startActivity(intent);
        });
    }
}

