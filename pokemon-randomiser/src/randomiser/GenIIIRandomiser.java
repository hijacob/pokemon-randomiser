package randomiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	Map<Short,Short> oneToOneMap;
	Map<String,Short> nameToIndex;
	String[] indexToName;
	short[] pkmnindices;
	String[] pkmnnames;
	
	public GenIIIRandomiser(){
		super();
		oneToOneMap = new HashMap<Short,Short>();
		nameToIndex = new HashMap<String,Short>();
		indexToName = new String[0x200];
		pkmnindices = new short[413];
		pkmnnames = new String[413];
		loadNames();
		ArrayList<Short> indices = new ArrayList<Short>(0x200);
		for(short i=0; i<indexToName.length; i++)
			if(indexToName[i] != null){
				indices.add(i);
			}
		
		for(int i=0; !indices.isEmpty(); i++){
			int j = rand.nextInt(indices.size());
			short replacement = indices.get(j);
			indices.remove(j);
			oneToOneMap.put((short)i, replacement);
		}
		
	}
	
	private void loadNames(){
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("pokeindices3.txt")));
			int i=0;
			while(true){
				String line = r.readLine();
				if(line == null)
					break;
				String[] s = line.split("\t");
				int index = Integer.parseInt(s[0], 16);
				indexToName[index] = s[1];
				nameToIndex.put(s[1], (short)index);
				pkmnindices[i] = (short)index;
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
	
	public short getReplacement(short pkmn){
		switch(mode){
		case Random:
			return pkmnindices[rand.nextInt(pkmnindices.length)];
		case OneToOne:
			return oneToOneMap.get(pkmn);
		case Mew:
			return nameToIndex.get("Mew");
		default:
			System.err.println("Unhandled randomisation mode");
			return pkmnindices[rand.nextInt(pkmnindices.length)];
		}
	}

	void randomiseWildArea(int offset){
		int end = offset;
		int start = readInt(offset+4) - 0x8000000;
		for(int i=start; i<end; i+=4){
			//rom[i] - lowest level
			//rom[i+1] - highest level
			writeShort(i+2, getReplacement(readShort(i+2)));
		}
	}
	
	void randomiseTrainer(int offset){
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
	
	void randomiseTrainerPokemon(int offset, int nPkmn, boolean moves){
		if(moves)
			for(int i=0; i<nPkmn; i++){
				//readShort(offset+16*i) - AI smarts
				//readShort(offset+16*i+2) - level
				writeShort(offset+16*i+4, getReplacement(readShort(offset+16*i+4)));
				//readShort(offset+16*i+6) - item
				for(int j=0; j<4; j++)
					if(movesets == movesetsMode.Random)
						writeShort(offset+16*i+2*j,(short)(rand.nextInt(0x162)+1));
			}
		else
			for(int i=0; i<nPkmn; i++){
				//readShort(offset+8*i) - AI smarts
				//readShort(offset+8*i+2) - level
				writeShort(offset+8*i+4, getReplacement(readShort(offset+8*i+4)));
				//readShort(offset+8*i+6) - item
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
