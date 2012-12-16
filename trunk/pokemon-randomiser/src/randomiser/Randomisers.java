package randomiser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		add(new GenIIIRandomiser());
	}
	
	public version getVersion(String fileName) throws IOException{
		byte[] header = new byte[400];
		InputStream reader = new FileInputStream(fileName);
		reader.read(header, 0, 400);
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
		
		String v2 = new String(Arrays.copyOfRange(header, 0xa0, 0xac));
		
		if(v2.startsWith("POKEMON RUBY")){
			return version.Ruby;
		} else if(v2.startsWith("POKEMON SAPP")){
			return version.Sapphire;
		} else if(v2.startsWith("POKEMON EMER")){
			return version.Emerald;
		} else if(v2.startsWith("POKEMON FIRE")){
			return version.Fire;
		} else if(v2.startsWith("POKEMON LEAF")){
			return version.Leaf;
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
