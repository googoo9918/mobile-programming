package kr.co.example.mobileprogramming.model;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;

public class GameManager {
    private int currentRound;
    private int totalRounds;
    private Difficulty difficulty;
    private Board board;
    private Player player1;
    private Player player2;  // null in single-player mode
    private Player currentPlayer;
    private List<Pair<Integer, Card>> selectedCards; // 카드 위치와 객체를 저장
    private boolean extendTurnFlag = false;
    private GameEventListener gameEventListener;
    private GameErrorListener gameErrorListener;

    public GameManager(Difficulty difficulty, int totalRounds, Player player1, Player player2) {
        this.difficulty = difficulty;
        this.totalRounds = totalRounds;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.currentRound = 1;
        this.selectedCards = new ArrayList<>();
    }

    public void startGame() {
        if (gameEventListener != null) {
            gameEventListener.onGameStarted();
        }
    }

    public void initializeBoard(List<Card> boardCards) {
        // 난이도에 따라 보드 크기 및 카드 생성
        int rows;
        int columns;
        switch (difficulty) {
            case EASY:
                rows = 5;
                columns = 5;
                break;
            case HARD:
                rows = 7;
                columns = 7;
                break;
            case NORMAL:
            default:
                rows = 6;
                columns = 6;
                break;
        }

        this.board = new Board(rows, columns, boardCards);
    }

    public boolean flipCard(int position) {
        boolean success = board.flipCard(position);
        if (success) {
            Card card = board.getCardAt(position);
            if (gameEventListener != null) {
                gameEventListener.onCardFlipped(position, card);
            }
            // 추가 로직 (매칭 확인 등)
        } else {
            if (gameErrorListener != null) {
                gameErrorListener.onInvalidMove("선택한 카드를 뒤집을 수 없습니다.");
            }
        }
        return success;
    }

    public boolean selectCard(int position) {
        Card selectedCard = board.getCardAt(position);

        // 유효성 검사: 이미 선택된 카드인지 또는 두 장이 이미 선택된 상태인지 확인
        if (selectedCard == null || selectedCard.isFlipped() || selectedCards.size() >= 2) {
            return false; // 유효하지 않은 선택
        }

        selectedCards.add(new Pair<>(position, selectedCard)); // 위치와 카드 객체 저장
        selectedCard.flip(); // 카드 뒤집기
        gameEventListener.onCardFlipped(position, selectedCard);

        if (selectedCards.size() == 2) {
            processTurn(); // 두 장이 선택되면 턴 처리
        }

        return true; // 선택 성공
    }

    private void processTurn() {
        if (selectedCards.size() != 2) return; // 두 장이 선택되지 않으면 처리하지 않음
        Log.d("ProcessTurn", "cards selected" + selectedCards);

        Card card1 = selectedCards.get(0).second;
        Card card2 = selectedCards.get(1).second;

        if(card1.getId() == card2.getId()) {
            card1.setMatched();
            card2.setMatched();

            if(card1.getType() == CardType.ITEM) {
                ItemCard itemCard = (ItemCard) card1;
                ItemEffect itemEffect = itemCard.createItemEffect(); // Get the corresponding effect
                currentPlayer.addItemEffect(itemEffect);

                if (gameEventListener != null) {
                    gameEventListener.onItemAcquired(itemCard);
                }
            }

            currentPlayer.addScore(1);

            if (gameEventListener != null) {
                gameEventListener.onMatchFound(selectedCards.get(0).first, selectedCards.get(1).first);
            }
            selectedCards.clear();
        }
        else {
            // 매칭 실패: 딜레이 후 카드를 닫음
            new android.os.Handler().postDelayed(() -> {
                if (selectedCards.isEmpty()) {
                    Log.e("ProcessTurn", "selectedCards is empty during delay");
                    return;
                }

                card1.flip(); // 첫 번째 카드 닫기
                card2.flip(); // 두 번째 카드 닫기

                // UI 갱신
                if (gameEventListener != null) {
                    gameEventListener.onCardFlipped(selectedCards.get(0).first, card1);
                    gameEventListener.onCardFlipped(selectedCards.get(1).first, card2);
                }

                // 턴 넘기기
                switchTurn();
                selectedCards.clear();
            }, 500); // 0.5초 후 카드 닫기
        }
    }

    private void switchTurn() {
        if(extendTurnFlag) {
            extendTurnFlag = false;
            return;
        }

        if (player2 == null) {
            currentPlayer = player1; // Single-player mode
        } else {
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
            if (gameEventListener != null) {
                gameEventListener.onTurnChanged(currentPlayer);
            }
        }
        Log.d("Game", "Turn switched to " + currentPlayer.getName());
        Log.d("Game", "player 1 " + player1.getScore());
        Log.d("Game", "player 2 " + player2.getScore());
    }

    public void notifyCardFlipped(int position, Card card) {
        if (gameEventListener != null) {
            gameEventListener.onCardFlipped(position, card);
        }
    }

    public void setExtendTurnFlag(boolean extendTurnFlag) {
        this.extendTurnFlag = extendTurnFlag;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void setGameErrorListener(GameErrorListener listener) {
        this.gameErrorListener = listener;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public GameResult getGameResult() {
        // 게임 결과를 계산하여 반환
        int player1Score = player1.getScore();
        int player2Score = player2.getScore();
        Player winner = (player1Score > player2Score) ? player1 : player2;
        return new GameResult(winner, player1Score, player2Score);
    }

    public void updateGameState(GameState gameState) {
        // 수신된 게임 상태로 현재 상태를 업데이트
        // 예: 보드 상태, 플레이어 점수, 현재 플레이어 등
    }

    public Board getBoard() {
        return board;
    }

    public List<Pair<Integer, Card>> getSelectedCards() {
        return new ArrayList<>(selectedCards); // 선택된 카드 리스트 복사본 반환
    }

    public Player getOpponent(Player currentPlayer) {
        if (player2 == null) {
            return null;
        }
        return (currentPlayer == player1) ? player2 : player1;
    }

    public boolean isAllCardsMatched() {
        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

}
