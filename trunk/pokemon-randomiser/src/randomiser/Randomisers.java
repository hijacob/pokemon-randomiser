package randomiser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import randomiser.Randomiser.version;

public class Randomisers extends java.util.ArrayList<Randomiser> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1833670015362494130L;
	
	public Randomisers(){
		super();
		add(new GenIIRandomiser());
	}
	
	public version getVersion(String fileName) throws IOException{
		char[] header = new char[400];
		FileReader reader = new FileReader(fileName);
		reader.read(header);
		reader.close();
		
		String v1 = new String(Arrays.copyOfRange(header,0x134,0x144));
		
		if(v1.startsWith("POKEMON RED")){
			return version.Red;
		} else if(v1.startsWith("POKEMON BLUE")){
			return version.Blue;
		} else if(v1.startsWith("POKEMON YELLOW")){
			return version.Yellow;
		} else if(v1.startsWith("POKEMON_GLD")){
			return version.Gold;
		} else if(v1.startsWith("POKEMON_SLV")){
			return version.Silver;
		} else if(v1.startsWith("PM_CRYSTAL")){
			return version.Crystal;
		}
		
		return version.Unknown;
	}
	
	public Randomiser getRandomiser(version v){
		
		for(int i=0; i<size(); i++)
			if(get(i).isCompatibleVersion(v))
				return get(i);
		
		return null;
	}

}
