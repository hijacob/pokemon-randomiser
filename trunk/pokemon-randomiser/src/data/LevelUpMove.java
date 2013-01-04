package data;

public class LevelUpMove {
	public Move move;
	public int level;
	
	public LevelUpMove(int level, int moveIndex){
		this.level = level;
		this.move = new Move(moveIndex);
	}
	
	public LevelUpMove(int level, Move move){
		this.level = level;
		this.move = move;
	}
}
