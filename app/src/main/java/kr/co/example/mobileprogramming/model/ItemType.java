package kr.co.example.mobileprogramming.model;

public enum ItemType {
    //한 턴 연장
    TURN_EXTENSION,
    //점수 두배
    DOUBLE_SCORE,
    //상대방 아이템 카드 1장 삭제
    REMOVE_OPPONENT_ITEM,
    //그림 한 장 미리보기
    REVEAL_CARD,
    //상대방 아이템 카드 1장 가져오기
    STEAL_ITEM,
    //폭탄1(기본점수x, 점수-1)
    BOMB_MINUS_ONE,
    //폭탄2(아이템 모두 잃기)
    BOMB_LOSE_ALL_ITEMS
}

