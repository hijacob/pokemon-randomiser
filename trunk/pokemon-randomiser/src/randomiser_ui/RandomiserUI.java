package randomiser_ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import randomiser.Randomiser;
import randomiser.Randomisers;
import randomiser.Randomiser.movesetsMode;
import randomiser.Randomiser.randomiseMode;
import randomiser.Randomiser.startersMode;
import randomiser.Randomiser.version;

public class RandomiserUI extends JFrame {

	private static final long serialVersionUID = 3022055966117068412L;
	
	private static String version = "1.2";
	
	private static String aboutMessage =
		String.format("Pokemon Randomiser %s\n",version) +
		"\n" +
		"Supported game versions:\n"; // Added at runtime
	
	private JButton randomise;
	private JCheckBox randtrainers, randareas, randmovesets, randevolutions, randtms, Use649;

	private JRadioButton randstarters, customstarters;
	private JRadioButton onetoone, random;
	private JRadioButton unchangedmoves, randommoves;
	private JComboBox s1, s2, s3;
	
	Randomiser r;
	Randomisers rlist;
	
	private FileFilter gbFilter;
	String fileType = ".gbc";
	String folder = null;

	public static void main(String[] args) {
		JFrame app = new RandomiserUI();
		app.setVisible(true);
	}
	
	public RandomiserUI(){
		rlist = new Randomisers();
		for(version v: Randomiser.version.values())
			if(rlist.getRandomiser(v) != null)
				aboutMessage += Randomiser.versionToString(v) + "\n";
		
		setSize(500,400);
		setTitle("Pokemon Randomiser");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JMenuBar menu = new JMenuBar();
		setJMenuBar(menu);
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		menu.add(file);
		menu.add(help);
		
		JMenuItem open = new JMenuItem("Open");
		file.add(open);
		open.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				openDlg();
			}
		});
		
		JMenuItem about = new JMenuItem("About");
		help.add(about);
		about.addMouseListener(new MouseAdapter(){
			public void mouseReleased(MouseEvent e){
				JOptionPane.showMessageDialog(null, aboutMessage, "About Pokemon Randomiser", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		JPanel starters = new JPanel(), trainers = new JPanel(), wild = new JPanel(), mode = new JPanel(), data = new JPanel(), misc = new JPanel();
		
		Use649 = new JCheckBox("649 Mode");
		Use649.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				r.SetUse649Mode(Use649.isSelected());
				loadNames();
			};
		});
		Use649.setVisible(false);
		
		randtms = new JCheckBox("Randomise TMs");
		misc.add(randtms);
		misc.add(Use649);
		misc.setBorder(new TitledBorder(null, "Misc Options", TitledBorder.LEADING, TitledBorder.TOP));
		misc.setLayout(new GridLayout(0,1));
		
		randevolutions = new JCheckBox("Randomise Evolutions");
		randmovesets = new JCheckBox("Randomise Pokemon Movesets");
		data.add(randevolutions);
		data.add(randmovesets);
		data.setBorder(new TitledBorder(null, "Pokemon Options", TitledBorder.LEADING, TitledBorder.TOP));
		data.setLayout(new GridLayout(0,1));
		
		JRadioButton defaultStarters = new JRadioButton("Default Starters");
		starters.add(defaultStarters);
		randstarters = new JRadioButton("Randomise Starters");
		starters.add(randstarters);
		customstarters = new JRadioButton("Customise Starters");
		starters.add(customstarters);
		ButtonGroup startersGroup = new ButtonGroup();
		startersGroup.add(defaultStarters);
		startersGroup.add(randstarters);
		startersGroup.add(customstarters);
		defaultStarters.setSelected(true);
		s1 = new JComboBox(); s2 = new JComboBox(); s3 = new JComboBox();
		starters.add(s1);  starters.add(s2);  starters.add(s3);
		starters.setBorder(new TitledBorder(null, "Starters", TitledBorder.LEADING, TitledBorder.TOP));
		starters.setLayout(new GridLayout(0,1));
		
		customstarters.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				boolean cust = customstarters.isSelected();
				s1.setEnabled(cust);
				s2.setEnabled(cust);
				s3.setEnabled(cust);
			}
		});
		
		randtrainers = new JCheckBox("Randomise Trainers");
		trainers.add(randtrainers);
		unchangedmoves = new JRadioButton("Unchanged Movesets");
		trainers.add(unchangedmoves);
		randommoves = new JRadioButton("Random Movesets");
		trainers.add(randommoves);
		ButtonGroup moves = new ButtonGroup();
		moves.add(unchangedmoves);
		moves.add(randommoves);
		unchangedmoves.setSelected(true);
		trainers.setBorder(new TitledBorder(null, "Trainers", TitledBorder.LEADING, TitledBorder.TOP));
		trainers.setLayout(new GridLayout(0,1));
		
		randareas = new JCheckBox("Randomise Wild Pokemon");
		wild.add(randareas);
		wild.setBorder(new TitledBorder(null, "Wild Pokemon", TitledBorder.LEADING, TitledBorder.TOP));
		wild.setLayout(new GridLayout(0,1));
		
		onetoone = new JRadioButton("One to One");
		mode.add(onetoone);
		random = new JRadioButton("Random");
		mode.add(random);
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(random);
		modeGroup.add(onetoone);
		random.setSelected(true);
		mode.setBorder(new TitledBorder(null, "Randomise Mode", TitledBorder.LEADING, TitledBorder.TOP));
		mode.setLayout(new GridLayout(0,1));
		
		randomise = new JButton("Randomise");
		randomise.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				r.SetUse649Mode(Use649.isSelected());
				startersMode smode = randstarters.isSelected() ? startersMode.Random
						: customstarters.isSelected() ? startersMode.Custom : startersMode.Default;
				r.setStartersMode(smode);
				r.randomiseTrainers(randtrainers.isSelected());
				r.randomiseWildPokemon(randareas.isSelected());
				r.setEvolutionsMode(randevolutions.isSelected());
				r.setMovesetsMode(randmovesets.isSelected());
				r.randomiseTMs(randtms.isSelected());
				String[] starters = {s1.getSelectedItem().toString(), s2.getSelectedItem().toString(), s3.getSelectedItem().toString()};
				r.customiseStarters(starters);
				movesetsMode moves = unchangedmoves.isSelected() ? movesetsMode.Unchanged : movesetsMode.Random;
				r.setTrainerMovesets(moves);
				randomiseMode mode = onetoone.isSelected() ? randomiseMode.OneToOne : randomiseMode.Random;
				r.setMode(mode);
				r.randomise();
				saveDlg();
			}
		});
		

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup()
						.addComponent(starters)
						.addComponent(trainers)
						.addComponent(randomise))
					.addGroup(groupLayout.createParallelGroup()
						.addComponent(data)
						.addComponent(wild)
						.addComponent(misc)
						.addComponent(mode))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup()
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup()
							.addGroup(groupLayout.createSequentialGroup()
									.addComponent(starters)
									.addComponent(trainers))
							.addGroup(groupLayout.createSequentialGroup()
									.addComponent(data)
									.addComponent(wild)
									.addComponent(misc)
									.addComponent(mode))
							)
					.addComponent(randomise)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
		for(Component i: getContentPane().getComponents()){
			i.setEnabled(false);
			if(i.getClass() == mode.getClass()){
				JPanel p = (JPanel)i;
				for(Component j: p.getComponents()){
					j.setEnabled(false);
				}
			}
		}
		
		gbFilter = new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory())
					return true;
				String name = pathname.getName().toLowerCase();
				if(name.endsWith(".gb") || name.endsWith(".gbc") || name.endsWith(".gba"))
					return true;
				return false;
			}
			@Override
			public String getDescription() {
				return "Gameboy Files";
			}};
	}
	
	void saveDlg(){
		JFileChooser save = new JFileChooser(folder);
		save.setFileFilter(gbFilter);
		save.showSaveDialog(this);
		File rom = save.getSelectedFile();
		if(rom != null){
			try{
				String romPath = rom.getAbsolutePath();
				String romName = rom.getName();
				folder = rom.getParentFile().getAbsolutePath();
				if(romName.lastIndexOf('.') == -1)
					romPath = romPath + fileType;
				r.writeRom(romPath);
			}catch(IOException e){
				JOptionPane.showMessageDialog(this, String.format("An IOException occurred saving the file"));
			}
		}
	}
	
	void openDlg(){
		JFileChooser open = new JFileChooser(folder);
		open.setFileFilter(gbFilter);
		open.showOpenDialog(this);
		File rom = open.getSelectedFile();
		if(rom != null){
			try{
				String romPath = rom.getAbsolutePath();
				String romName = rom.getName();
				folder = rom.getParentFile().getAbsolutePath();
				fileType = romName.substring(romName.lastIndexOf('.'));
				version v = rlist.getVersion(romPath);
				r = rlist.getRandomiser(v);
				if(r != null)
				{
					r.setVersion(v);
					r.readRom(romPath);
					loadNames();
					for(Component i: getContentPane().getComponents()){
						i.setEnabled(true);
						if(i.getClass() == new JPanel().getClass()){
							JPanel p = (JPanel)i;
							for(Component j: p.getComponents()){
								j.setEnabled(true);
							}
						}
					}
					boolean cust = customstarters.isSelected();
					s1.setEnabled(cust);
					s2.setEnabled(cust);
					s3.setEnabled(cust);
					
					if(v == Randomiser.version.Fire)
					{
						Use649.setVisible(true);
					}
					else
					{
						r.SetUse649Mode(false);
						Use649.setSelected(false);
						Use649.setVisible(false);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(this, String.format("Not compatible with %s", Randomiser.versionToString(v)));
				}
			}catch(IOException e){
				JOptionPane.showMessageDialog(this, String.format("An IOException occurred opening the file"));
			}
		}
		
		
	}
	
	private void loadNames()
	{
		s1.removeAllItems();
		s2.removeAllItems();
		s3.removeAllItems();
		String[] names = r.getNames();
		for(String name: names){
			if(name!= null && !name.equals(""))
			{
				s1.addItem(name);
				s2.addItem(name);
				s3.addItem(name);
			}
		}
		
		String[] currentStarters = r.currentStarters();
		if(currentStarters != null && currentStarters.length == 3){
			s1.setSelectedItem(currentStarters[0]);
			s2.setSelectedItem(currentStarters[1]);
			s3.setSelectedItem(currentStarters[2]);
		}
	}
}
