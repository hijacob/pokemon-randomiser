package data;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
	public int index;
	public String name;
	public List<LevelUpMove> levelupMoves;
	
	public Pokemon(int index){
		this.index = index;
	}
	
	public List<Move> getMoves(int level){
		List<Move> moves = new ArrayList<Move>();
		
		for(LevelUpMove m : levelupMoves){
			if(m.level <= level){
				if(!moves.contains(m.move)) //some moves are listed twice in the standard movesets
					moves.add(m.move);
			}
		}
		
		return moves;
	}
	
	public List<Move> getDefaultMoves(int level){
		List<Move> moves = getMoves(level);
		
		if(moves.size() <= 4)
			return moves;
		else
			return moves.subList(moves.size()-4, moves.size());
	}
}
