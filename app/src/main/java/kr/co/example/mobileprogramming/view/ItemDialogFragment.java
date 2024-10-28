package kr.co.example.mobileprogramming.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import kr.co.example.mobileprogramming.events.OnItemSelectedListener;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;

public class ItemDialogFragment extends DialogFragment {
    private List<ItemEffect> items;
    private OnItemSelectedListener listener;

    public ItemDialogFragment(List<ItemEffect> items, OnItemSelectedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 아이템 목록을 다이얼로그에 표시하고 선택 이벤트 처리
        // 예를 들어 AlertDialog.Builder를 사용하여 아이템 목록을 표시하고 선택 시 listener.onItemSelected(itemType) 호출
        return super.onCreateDialog(savedInstanceState);
    }
}
