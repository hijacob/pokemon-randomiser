package randomiser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import data.LevelUpMove;
import data.Move;
import data.Pokemon;

import ec.util.MersenneTwister;

public abstract class Randomiser {

	protected Random rand;
	protected byte[] rom;
	protected boolean wild, trainers, movesets, tmcompatibility, evolutions, tms;
	protected String[] customStarters;
	protected randomiseMode mode;
	protected version game;
	protected startersMode starters;
	protected movesetsMode trainerMovesets;
	
	public enum randomiseMode{OneToOne, Random, Mew};
	public enum startersMode{Default, Random, Custom};
	public enum movesetsMode{Unchanged, Random, Default, RandomLegal};
	public enum version{Unknown,
		Red,Blue,Yellow,
		Gold,Silver,Crystal,
		Ruby,Sapphire,Emerald,
		Fire,Leaf,
		Diamond,Pearl,Platinum,
		Heart,Soul,
		Black,White,
		Black2,White2
	};
	
	public static String versionToString(version v){
		switch(v){
		case Unknown: return "Unknown ROM";
		case Red: return "Pokemon Red";
		case Blue: return "Pokemon Blue";
		case Yellow: return "Pokemon Yellow";
		case Gold: return "Pokemon Gold";
		case Silver: return "Pokemon Silver";
		case Crystal: return "Pokemon Crystal";
		case Ruby: return "Pokemon Ruby";
		case Sapphire: return "Pokemon Sapphire";
		case Emerald: return "Pokemon Emerald";
		case Fire: return "Pokemon Fire Red";
		case Leaf: return "Pokemon Leaf Green";
		case Diamond: return "Pokemon Diamond";
		case Pearl: return "Pokemon Pearl";
		case Platinum: return "Pokemon Platinum";
		case Heart: return "Pokemon Heart Gold";
		case Soul: return "Pokemon Soul Silver";
		case Black: return "Pokemon Black";
		case White: return "Pokemon White";
		case Black2: return "Pokemon Black 2";
		case White2: return "Pokemon White 2";
		default: return "Corrupt version enum value";
		}
	}
	
	public Randomiser(){
		rom = null;
		wild = trainers = evolutions = movesets = tmcompatibility = tms = false;
		customStarters = new String[0];
		mode = randomiseMode.Random;
		starters = startersMode.Default;
		trainerMovesets = movesetsMode.Unchanged;
		rand = new MersenneTwister(System.nanoTime());
	}
	
	public abstract boolean isCompatibleVersion(version game);
	
	public abstract void randomise();
	
	public abstract String[] getNames();
	
	public void readRom(String filePath) throws IOException{
			FileInputStream instream = new FileInputStream(filePath);
			rom = new byte[instream.available()];
			instream.read(rom,0,rom.length);
			instream.close();
	}
	
	public void writeRom(String filePath) throws IOException{
		FileOutputStream outstream = new FileOutputStream(filePath);
		outstream.write(rom);
		outstream.close();
	}
	
	protected List<Move> getTrainerMoves(movesetsMode mode, int pokemon, int level){
		List<LevelUpMove> moveset = readPokemonMoves(pokemon);
		Pokemon tmpPokemon = new Pokemon(pokemon);
		tmpPokemon.levelupMoves = moveset;
		
		if(mode == movesetsMode.Default){
			return tmpPokemon.getDefaultMoves(level);
		}
		
		List<Move> possibleMoves = tmpPokemon.getMoves(level);
		
		if(mode == movesetsMode.RandomLegal){
			List<Move> moves = new ArrayList<Move>();
			for(int i=0; i<4; i++)
				if(!possibleMoves.isEmpty())
					moves.add(possibleMoves.remove(rand.nextInt(possibleMoves.size())));
			return moves;
		}
		
		return new ArrayList<Move>();
	}
	
	protected abstract List<LevelUpMove> readPokemonMoves(int pokemon);

	public void randomiseWildPokemon(boolean val){
		wild = val;
	}
	
	public void randomiseTrainers(boolean val){
		trainers = val;
	}
	
	public void randomiseTMs(boolean val){
		tms = val;
	}
	
	public void setStartersMode(startersMode mode){
		starters = mode;
	}
	
	public void setMovesetsMode(boolean val){
		movesets = val;
	}
	
	public void setEvolutionsMode(boolean val){
		evolutions = val;
	}
	
	public void randomiseTMCompatibility(boolean val){
		tmcompatibility = val;
	}
	
	public void customiseStarters(String[] starters){
		customStarters = starters;
	}
	
	public abstract String[] currentStarters();
	
	public void setTrainerMovesets(movesetsMode val){
		trainerMovesets = val;
	}
	
	public void setMode(randomiseMode val){
		mode = val;
	}
	
	public void setVersion(version v){
		game = v;
	}
	
	protected short readShort(int offset){
		return (short) ((rom[offset]&0xFF) + ((rom[offset+1]&0xFF) << 8));
	}
	
	protected void writeShort(int offset, short val){
		rom[offset] = (byte)(val & 0xFF);
		rom[offset+1] = (byte)((val & 0xFF00) >> 8);
	}
	
	protected int readInt(int offset){
		return (readShort(offset) & 0xFFFF) + ((readShort(offset+2) & 0xFFFF) << 16);
	}
	
	public void SetUse649Mode(boolean value)
	{
		//Do nothing by default
	}
	
	public boolean Supports649Mode()
	{
		return false;
	}
}
