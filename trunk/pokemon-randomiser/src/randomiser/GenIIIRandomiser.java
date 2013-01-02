package randomiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenIIIRandomiser extends Randomiser {

	private final int[] RStarters = {0x3F76C4, 0x3F76C6, 0x3F76C8};
	private final int[] SStarters = {0x3F771C, 0x3F771E, 0x3F7720};
	private final int[] EStarters = {0x5B1DF8, 0x5B1DFA, 0x5B1DFC};
	private final int[] FStarters = {0x169BB5, 0x169D82, 0x169DB8};
	private final int[] LStarters = {0x169B91, 0x169D5E, 0x169D94};
	
	private final int RWildOffset = 0x39D454;	
	private final int SWildOffset = 0x39D29C;
	private final int EWildOffset = 0x552D48;
	private final int FWildOffset = 0x3C9CB8;
	private final int LWildOffset = 0x3C9AF4;
	
	private final int RTrainersStart = 0x1F0524;
	private final int RTrainersEnd = 0x1F716B;
	private final int STrainersStart = 0x1F04B4;
	private final int STrainersEnd = 0x1F70FB;
	private final int ETrainersStart = 0x310058;
	private final int ETrainersEnd = 0x3185C7;
	private final int FTrainersStart = 0x23EAF0;
	private final int FTrainersEnd = 0x245EDF;
	private final int LTrainersStart = 0x23EACC;
	private final int LTrainersEnd = 0x245EBB;
	
	private final int RPokemonMovesOffset = 0x201928; //table = 207BC8
	private final int SPokemonMovesOffset = 0x2018B8; //table = 207B58
	private final int EPokemonMovesOffset = 0x3230DC; //table = 32937C
	private final int FPokemonMovesOffset = 0x257494; //table = 25D7B4
	private final int F649PokemonMovesOffset = 0x7498EA; //table = 726990
	private final int LPokemonMovesOffset = 0x257470; //table = 25D794
	
	//8 bytes after pointer for pokemon 0 = offset
	private final int RTMCompatibilityOffset = 0x1FD0F8;
	private final int STMCompatibilityOffset = 0x1FD088;
	private final int ETMCompatibilityOffset = 0x31E8A0;
	private final int FTMCompatibilityOffset = 0x252BD0;
	private final int F649TMCompatibilityOffset = 0x73ECF4;
	private final int LTMCompatibilityOffset = 0x252BAC;
	
	private final int RTMsOffset = 0x376504;
	private final int STMsOffset = 0x376494;
	private final int ETMsOffset = 0x616040;
	private final int FTMsOffset = 0x45A80C;
	private final int F649TMsOffset = 0x45A5A4;
	private final int LTMsOffset = 0x45A22C;
	
	private final int RTMItemOffset = 0x3C8710;
	private final int STMItemOffset = 0x3C8768;
	private final int ETMItemOffset = 0x586B4C;
	private final int FTMItemOffset = 0x3DE1D4;
	private final int F649TMItemOffset = 0x74F274;
	private final int LTMItemOffset = 0x3DE010;
	
	private final int RMoveDescriptionOffset = 0x3C09D8;
	private final int SMoveDescriptionOffset = 0x3C0A30;
	private final int EMoveDescriptionOffset = 0x61C524;
	private final int FMoveDescriptionOffset = 0x4886E8;
	private final int F649MoveDescriptionOffset = 0x746E28;
	private final int LMoveDescriptionOffset = 0x487FC4;
	
	private final int FLHackProtectionOffset = 0x1D3F0;
	private final int EHackProtectionOffset = 0x45C74;
	
	Map<Short,Short> oneToOneMap;
	Map<String,Short> nameToIndex;
	String[] indexToName;
	ArrayList<Short> pkmnindices;
	String[] pkmnnames;
	boolean UseGen5Pokemon;
	
	public GenIIIRandomiser(){
		super();
		UseGen5Pokemon = false;
		init();
	}
	
	@Override
	public void SetUse649Mode(boolean value)
	{
		if(UseGen5Pokemon != value)
		{
			UseGen5Pokemon = value;
			init();
		}
	}
	
	@Override
	public boolean Supports649Mode()
	{
		return true;
	}
	
	private void init()
	{
		oneToOneMap = new HashMap<Short,Short>();
		nameToIndex = new HashMap<String,Short>();
		indexToName = new String[650];
		pkmnindices = new ArrayList<Short>(650);
		pkmnnames = new String[650];
		loadNames();
		ArrayList<Short> tmpindices1 = new ArrayList<Short>(650);
		ArrayList<Short> tmpindices2 = new ArrayList<Short>(650);
		for(short i=0; i<indexToName.length; i++)
			if(indexToName[i] != null){
				tmpindices1.add(i);
				tmpindices2.add(i);
			}
		
		for(; !tmpindices1.isEmpty();){
			int j = rand.nextInt(tmpindices1.size());
			short replacement = tmpindices1.get(j);
			tmpindices1.remove(j);
			short index = tmpindices2.remove(0);
			oneToOneMap.put(index, replacement);
		}
	}
	
	private void loadNames(){
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(UseGen5Pokemon ? "pokeindices3-649.txt" : "pokeindices3.txt")));
			int i=0;
			while(true){
				String line = r.readLine();
				if(line == null)
					break;
				String[] s = line.split("\t");
				int index = Integer.parseInt(s[0], 16);
				indexToName[index] = s[1];
				nameToIndex.put(s[1], (short)index);
				pkmnindices.add((short)index);
				pkmnnames[i++] = s[1];
			}
			
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	
	
	@Override
	public boolean isCompatibleVersion(version game) {
		switch(game)
		{
		case Ruby:
		case Sapphire:
		case Emerald:
		case Fire:
		case Leaf:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void randomise() {
		
		if(game==version.Emerald){
			removeHackProtection(EHackProtectionOffset);
		} else if(game==version.Fire || game==version.Leaf){
			removeHackProtection(FLHackProtectionOffset);
		}
		
		
		if(starters != startersMode.Default){
			int[] offsets;
			if(game==version.Ruby){
				offsets = RStarters;
			} else if(game==version.Sapphire){
				offsets = SStarters;
			} else if(game==version.Emerald){
				offsets = EStarters;
			} else if(game==version.Fire){
				offsets = FStarters;
			} else{
				offsets = LStarters;
			}
			
			if(starters == startersMode.Random){
				for(int offset: offsets){
					writeShort(offset, getReplacement(readShort(offset)));
				}
			} else if(starters == startersMode.Custom){
				for(int i=0; i<3; i++){
					writeShort(offsets[i], nameToIndex.get(customStarters[i]));
				}
			}
		}
		
		if(movesets){
			int offset;
			if(game==version.Ruby){
				offset = RPokemonMovesOffset;
			} else if(game==version.Sapphire){
				offset = SPokemonMovesOffset;
			} else if(game==version.Emerald){
				offset = EPokemonMovesOffset;
			} else if(game==version.Fire){
				if(UseGen5Pokemon)
					offset = F649PokemonMovesOffset;
				else
					offset = FPokemonMovesOffset;
			} else {
				offset = LPokemonMovesOffset;
			}
			
			randomisePokemonMoves(offset);
		}
		
		if(tmcompatibility)
		{
			int offset;
			if(game==version.Ruby){
				offset = RTMCompatibilityOffset;
			} else if(game==version.Sapphire){
				offset = STMCompatibilityOffset;
			} else if(game==version.Emerald){
				offset = ETMCompatibilityOffset;
			} else if(game==version.Fire){
				if(UseGen5Pokemon)
					offset = F649TMCompatibilityOffset;
				else
					offset = FTMCompatibilityOffset;
			} else {
				offset = LTMCompatibilityOffset;
			}
			
			randomiseTMCompatibility(offset);
		}
		
		if(tms){
			int offset, TMItemOffset, MoveDescriptionTableOffset;
			if(game==version.Ruby){
				offset = RTMsOffset;
				TMItemOffset = RTMItemOffset;
				MoveDescriptionTableOffset = RMoveDescriptionOffset;
			} else if(game==version.Sapphire){
				offset = STMsOffset;
				TMItemOffset = STMItemOffset;
				MoveDescriptionTableOffset = SMoveDescriptionOffset;
			} else if(game==version.Emerald){
				offset = ETMsOffset;
				TMItemOffset = ETMItemOffset;
				MoveDescriptionTableOffset = EMoveDescriptionOffset;
			} else if(game==version.Fire){
				if(UseGen5Pokemon) {
					offset = F649TMsOffset;
					TMItemOffset = F649TMItemOffset;
					MoveDescriptionTableOffset = F649MoveDescriptionOffset;
				} else {
					offset = FTMsOffset;
					TMItemOffset = FTMItemOffset;
					MoveDescriptionTableOffset = FMoveDescriptionOffset;
				}
			} else {
				offset = LTMsOffset;
				TMItemOffset = LTMItemOffset;
				MoveDescriptionTableOffset = LMoveDescriptionOffset;
			}
			
			randomiseTMs(offset, TMItemOffset, MoveDescriptionTableOffset);
		}
		
		if(wild){
			int offset;
			if(game==version.Ruby){
				offset = RWildOffset;
			} else if(game==version.Sapphire){
				offset = SWildOffset;
			} else if(game==version.Emerald){
				offset = EWildOffset;
			} else if(game==version.Fire){
				offset = FWildOffset;
			} else{
				offset = LWildOffset;
			}
			
			while(readShort(offset) != (short)(0xFFFF)){
				offset += 4;
				for(int i=0; i<4; i++){
					if(readInt(offset) != 0){
						randomiseWildArea(readInt(offset) - 0x8000000);
					}
					offset += 4;
				}
			}
		}
		
		if(trainers){
			int start, end;
			if(game==version.Ruby){
				start = RTrainersStart; end = RTrainersEnd;
			} else if(game==version.Sapphire){
				start = STrainersStart; end = STrainersEnd;
			}else if(game==version.Emerald){
				start = ETrainersStart; end = ETrainersEnd;
			} else if(game==version.Fire){
				start = FTrainersStart; end = FTrainersEnd;
			} else{
				start = LTrainersStart; end = LTrainersEnd;
			}
			
			for(int offset = start; offset < end; offset += 40){
				randomiseTrainer(offset);
			}
		}
	}
	
	private void removeHackProtection(int offset){
		for(int i=0; i<4; ++i){
			rom[offset+i] = 0;
		}
		for(int i=0; i<4; ++i){
			rom[offset+0x12+i] = 0;
		}
	}
	
	private short getReplacement(short pkmn){
		switch(mode){
		case Random:
			return pkmnindices.get(rand.nextInt(pkmnindices.size()));
		case OneToOne:
			Short replacement =  oneToOneMap.get(pkmn);
			if(replacement != null)
			{
				return replacement;
			}
			else
			{
				System.err.println("No replacement found for index " + (pkmn&0xFFFF));
				return pkmnindices.get(rand.nextInt(pkmnindices.size()));
			}
		case Mew:
			return nameToIndex.get("Mew");
		default:
			System.err.println("Unhandled randomisation mode");
			return pkmnindices.get(rand.nextInt(pkmnindices.size()));
		}
	}
	
	private short getRandomMove(){
		return (short)(rand.nextInt(354)+1);
	}

	private void randomiseWildArea(int offset){
		int end = offset;
		int start = readInt(offset+4) - 0x8000000;
		for(int i=start; i<end; i+=4){
			//rom[i] - lowest level
			//rom[i+1] - highest level
			writeShort(i+2, getReplacement(readShort(i+2)));
		}
	}
	
	private void randomiseTrainer(int offset){
		int pkmnformat = rom[offset]; //format&1 = moves, format&2 = items
		// rom[offset+1] - trainer class
		// rom[offset+2] - gender, intro music
		// rom[offset+3] - trainer sprite
		// rom[offset+4]..rom[offset+15] - name
		// readShort(offset+16)..readShort(offset+22) - items
		// rom[offset+24] - double battle flag
		// rom[offset+25]..rom[offset+31] - ?
		int nPkmn = rom[offset+32];
		// rom[offset+33]..rom[offset+35] - ?
		int pkmnOffset = readInt(offset+36) - 0x8000000;
		
		randomiseTrainerPokemon(pkmnOffset, nPkmn, (pkmnformat&1)==1);
	}
	
	private void randomiseTrainerPokemon(int offset, int nPkmn, boolean moves){
		if(moves)
			for(int i=0; i<nPkmn; i++){
				//readShort(offset+16*i) - AI smarts
				//readShort(offset+16*i+2) - level
				writeShort(offset+16*i+4, getReplacement(readShort(offset+16*i+4)));
				//readShort(offset+16*i+6) - item
				for(int j=0; j<4; j++)
					if(trainerMovesets == movesetsMode.Random)
						writeShort(offset+16*i+2*j+8,(short)(rand.nextInt(0x162)+1));
			}
		else
			for(int i=0; i<nPkmn; i++){
				//readShort(offset+8*i) - AI smarts
				//readShort(offset+8*i+2) - level
				writeShort(offset+8*i+4, getReplacement(readShort(offset+8*i+4)));
				//readShort(offset+8*i+6) - item
			}
	}
	
	private void randomisePokemonMoves(int offset){
		for(int i=0; i<pkmnindices.get(pkmnindices.size()-1); ++i){
			Set<Short> moves = new HashSet<Short>();
			while(readShort(offset) != (short)(-1)){
				short s = readShort(offset);
				int level = s>>9;
				short move = (short)(s%512);
				do{
					move = getRandomMove();
				} while(moves.contains(move));
				moves.add(move);
				s = (short)((level<<9) + move);
				writeShort(offset, s);
				offset += 2;
			}
			offset += 2;
		}
	}
	
	private void randomiseTMCompatibility(int offset){
		for(int i=0; i<pkmnindices.get(pkmnindices.size()-1); ++i){
			for(int j=0; j<58/8; j++){
				rom[offset++] = (byte)rand.nextInt(256);
			}
			rom[offset++] = (byte)rand.nextInt(4);
		}
	}
	
	private void randomiseTMs(int offset, int TMItemOffset, int MoveDescriptionTableOffset){
		Set<Short> moves = new HashSet<Short>();
		for(int i=0; i<50; ++i){
			short move;
			do{
				move = getRandomMove();
			} while(moves.contains(move));
			writeShort(offset+2*i, move);
			//tm offset = offset + (i-1)*44 //tm description pointer = tm offset + 20
			//move i description pointer = offset + (i-1)*4
			writeShort(TMItemOffset + i*44 + 20, readShort(MoveDescriptionTableOffset + (move-1)*4));
		}
	}
	
	@Override
	public String[] getNames() {
		return pkmnnames.clone();
	}

	@Override
	public String[] currentStarters() {
		if(rom != null){
			int[] offsets;
			if(game==version.Ruby){
				offsets = RStarters;
			} else if(game==version.Sapphire){
				offsets = SStarters;
			} else if(game==version.Emerald){
				offsets = EStarters;
			} else if(game==version.Fire){
				offsets = FStarters;
			} else{
				offsets = LStarters;
			}
			
			String[] ret = {
					indexToName[readShort(offsets[0])],
					indexToName[readShort(offsets[1])],
					indexToName[readShort(offsets[2])]
			};
			return ret;
		}
		return null;
	}

}
