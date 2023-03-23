package da.nzse.engine;

public interface GameViewEventListener {
    void onGameOver(GameView gameView);
    void onScoreChange(long currentScore);
}
