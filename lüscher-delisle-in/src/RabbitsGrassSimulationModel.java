import uchicago.src.reflector.RangePropertyDescriptor;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenHistogram;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.util.SimUtilities;


import java.awt.*;
import java.util.ArrayList;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 *
 * @author 
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {

	private static final int GRID_SIZE=50;
	private static final int NUM_INIT_RABBITS = 10;
	private static final int NUM_INIT_GRASS=1000;
	private static final int GRASS_GROWTH_RATE = 500;
	private static final int BIRTH_THRESHOLD = 300;
	private static final int GRASS_BASE_ENERGY = 3;
	private static final int LIFE_AT_BIRTH = 150;
	private static final int BABY_DELIVERY_ENERGY = 150;
	private static final int MAXIMAL_GRASS_STACKING = 3;
	private static final int RABBIT_ENERGY_CONSUMPTION = 3;

	private int gridSize = GRID_SIZE;
	private int numInitRabbits = NUM_INIT_RABBITS;
	private int numInitGrass = NUM_INIT_GRASS;
	private int grassGrowthRate = GRASS_GROWTH_RATE;
	private int birthThreshold = BIRTH_THRESHOLD;
	private int grassEnergy = GRASS_BASE_ENERGY;
	private int lifeAtBirth = LIFE_AT_BIRTH;
	private int babyDeliveryEnergy = BABY_DELIVERY_ENERGY;
	private int maxGrassStack = MAXIMAL_GRASS_STACKING;
	private int rabbitEnergyConsumption = RABBIT_ENERGY_CONSUMPTION;



	private Schedule schedule;

	private RabbitsGrassSimulationSpace space;
	private ArrayList<RabbitsGrassSimulationAgent> rabbit_list;

	private DisplaySurface displaySurf;
	private OpenSequenceGraph amountOfGrassInSpace;
	private OpenHistogram agentWealthDistribution;

	class GrassInSpace implements DataSource, Sequence {

		public Object execute() {
			return getSValue();
		}

		public double getSValue() {
			return (double) space.get_total_grass();
		}
	}

	class AgentsInSpace implements DataSource, Sequence {

		public Object execute() {
			return getSValue();
		}

		public double getSValue() {
			return (double) rabbit_list.size();
		}
	}

	public static void main(String[] args) {

		System.out.println("Rabbit skeleton");

		SimInit init = new SimInit();
		RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
		// Do "not" modify the following lines of parsing arguments
		if (args.length == 0) // by default, you don't use parameter file nor batch mode
			init.loadModel(model, "", false);
		else
			init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));

	}

	public void begin(){
		buildModel();
		buildSchedule();
		buildDisplay();

		displaySurf.display();
		amountOfGrassInSpace.display();
	}

	public void buildModel(){

		space = new RabbitsGrassSimulationSpace(gridSize, maxGrassStack);
		space.add_new_grass(numInitGrass);

			for(int i = 0; i < numInitRabbits; i++){
			if(rabbit_list.size() < gridSize * gridSize) {
				add_rabbit();
			}
		}

	}

	public void buildSchedule(){
		System.out.println("Running BuildSchedule");

		class CarryDropStep extends BasicAction {
			public void execute() {
				space.add_new_grass(grassGrowthRate);

				SimUtilities.shuffle(rabbit_list);
				for (RabbitsGrassSimulationAgent rabbitsGrassSimulationAgent : rabbit_list) {
					((RabbitsGrassSimulationAgent) rabbitsGrassSimulationAgent).step();
				}

				give_birth_or_die();
				displaySurf.updateDisplay();       }
		}

		schedule.scheduleActionBeginning(0, new CarryDropStep());

		class CarryDropCountLiving extends BasicAction {
			public void execute(){
				count_rabbits();
			}
		}

		schedule.scheduleActionAtInterval(10, new CarryDropCountLiving());


		class CarryDropUpdateGrassInSpace extends BasicAction {
			public void execute(){
				amountOfGrassInSpace.step();
			}
		}

		schedule.scheduleActionAtInterval(1, new CarryDropUpdateGrassInSpace());

	}

	public void buildDisplay(){

		ColorMap map = new ColorMap();

		for(int i = 1; i<5; i++){
			map.mapColor(i, new Color((int) 150-50*(i-1), 255, 50));
		}
		for(int i = 5; i<9; i++){
			map.mapColor(i, new Color(0, (int) 255 - (i-4)*50, 0));
		}
		map.mapColor(0, new Color(222,184,135));

		Value2DDisplay displayGrass =
				new Value2DDisplay(space.get_grass_space(), map);

		Object2DDisplay displayAgents = new Object2DDisplay(space.get_rabbit_space());
		displayAgents.setObjectList(rabbit_list);

		displaySurf.addDisplayableProbeable(displayGrass, "Grass");
		displaySurf.addDisplayableProbeable(displayAgents, "Agents");

		amountOfGrassInSpace.addSequence("#grass ", new GrassInSpace());
		amountOfGrassInSpace.addSequence("#rabbits", new AgentsInSpace());
		

	}

	public String[] getInitParam() {
		// TODO Auto-generated method stub
		// Parameters to be set by users via the Repast UI slider bar
		// Do "not" modify the parameters names provided in the skeleton code, you can add more if you want
		String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "GrassEnergy", "LifeAtBirth", "BabyDeliveryEnergy", "MaxGrassStack"};
		return params;
	}

	private void add_rabbit(){
		int n_x = (int) (Math.random() * (gridSize));
		int n_y = (int) (Math.random() * (gridSize));
		if(!space.get_rabbit_at(n_x, n_y)) {
			RabbitsGrassSimulationAgent new_rabbit = new RabbitsGrassSimulationAgent(lifeAtBirth, grassEnergy, rabbitEnergyConsumption);
			space.add_new_rabbit(new_rabbit, n_x, n_y);
			rabbit_list.add(new_rabbit);

		}
	}

	public String getName() {
		return "Carry And Drop";
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setup() {
		space = null;
		rabbit_list = new ArrayList<RabbitsGrassSimulationAgent>();
		schedule = new Schedule(1);

		//tear down and displays
		if (displaySurf != null){
			displaySurf.dispose();
		}
		displaySurf = null;

		if (amountOfGrassInSpace != null){
			amountOfGrassInSpace.dispose();
		}
		amountOfGrassInSpace = null;


		//creates a sliders which has to be ints.
		RangePropertyDescriptor b = new RangePropertyDescriptor("GridSize", 0, 200, gridSize);
		descriptors.put("GridSize", b);
		RangePropertyDescriptor a = new RangePropertyDescriptor("NumInitRabbits", 0, 100, numInitRabbits);
		descriptors.put("NumInitRabbits", a);
		RangePropertyDescriptor d = new RangePropertyDescriptor("NumInitGrass", 0, 5000, numInitGrass);
		descriptors.put("NumInitGrass", d);
		RangePropertyDescriptor h = new RangePropertyDescriptor("GrassGrowthRate", 0, 1000, grassGrowthRate);
		descriptors.put("GrassGrowthRate", h);
		RangePropertyDescriptor e = new RangePropertyDescriptor("BirthThreshold", 0, 1200, birthThreshold);
		descriptors.put("BirthThreshold", e);
		RangePropertyDescriptor f = new RangePropertyDescriptor("GrassEnergy", 0, 21, grassEnergy);
		descriptors.put("GrassEnergy", f);
		RangePropertyDescriptor g = new RangePropertyDescriptor("LifeAtBirth", 0, 300, lifeAtBirth);
		descriptors.put("LifeAtBirth", g);
		RangePropertyDescriptor i = new RangePropertyDescriptor("BabyDeliveryEnergy", 0, 300, babyDeliveryEnergy);
		descriptors.put("BabyDeliveryEnergy", i);
		RangePropertyDescriptor j = new RangePropertyDescriptor("MaxGrassStack", 1, 10, maxGrassStack);
		descriptors.put("MaxGrassStack", j);


		// Create Displays
		displaySurf = new DisplaySurface(this, "Carry Drop Model Window 1");


		amountOfGrassInSpace = new OpenSequenceGraph("Population Evolution",this);

		// Register Displays
		registerDisplaySurface("Carry Drop Model Window 1", displaySurf);
		this.registerMediaProducer("Plot", amountOfGrassInSpace);

	}

	//returns and prints number of rabbits in space
	private int count_rabbits(){
		int rabbit_nbr = 0;
		for(int i = 0; i < rabbit_list.size(); i++){
			RabbitsGrassSimulationAgent rabbit = (RabbitsGrassSimulationAgent) rabbit_list.get(i);
			if(rabbit.get_life() > 0) rabbit_nbr++;
		}
		System.out.println("there are: " + rabbit_nbr+ " rabbits");

		return rabbit_nbr;
	}

	/**
	 * For each rabbit in the simulation, looks at its energy and decides
	 * what to do with it:
	 * 1) Rabbit energy is over the birth threshold and thus said rabbit gives birth and looses some energy
	 * 2) Rabbit energy is non positive and thus the rabbit is removed from simulation
	 * 3) Rabbit energy is greater than 0 but less than the birth threshold, nothing happens
	 */
	private void give_birth_or_die(){
		for(int i = (rabbit_list.size() - 1); i >= 0 ; i--){
			RabbitsGrassSimulationAgent rabbit =  rabbit_list.get(i);
			if(rabbit.get_life() > birthThreshold){
				add_rabbit();
				rabbit.decrease_life(babyDeliveryEnergy);
			}
		}
		for(int i = (rabbit_list.size() - 1); i >= 0 ; i--){
			RabbitsGrassSimulationAgent rabbit = rabbit_list.get(i);
			if(rabbit.get_life() <= 0){
				space.remove_rabbit_at(rabbit.getX(), rabbit.getY());
				rabbit_list.remove(i);
			}
		}
	}

	public int getBirthThreshold(){
		return birthThreshold;
	}

	public void setBirthThreshold(int birth_thres){
		birthThreshold = birth_thres;
	}

	public int getNumInitRabbits(){
		return numInitRabbits;
	}

	public void setNumInitRabbits(int rabbit_num){ numInitRabbits = rabbit_num; }

	public int getGridSize(){
		return gridSize;
	}

	public void setGridSize(int size){
		gridSize = size;
	}

	public int getGrassGrowthRate() {
		return grassGrowthRate;
	}

	public void setGrassGrowthRate(int g_rate) {
		grassGrowthRate = g_rate;
	}

	public int getNumInitGrass() {
		return numInitGrass;
	}

	public void setNumInitGrass(int grass_init) {
		numInitGrass = grass_init;
	}

	public int getGrassEnergy(){
		return grassEnergy;
	}

	public void setGrassEnergy(int grass_en){
		grassEnergy=grass_en;
	}

	public int getLifeAtBirth() {
		return lifeAtBirth;
	}

	public void setLifeAtBirth(int lifeAtBirth) {
		this.lifeAtBirth = lifeAtBirth;
	}

	public  int getBabyDeliveryEnergy() {
		return babyDeliveryEnergy;
	}

	public void setBabyDeliveryEnergy(int babyDeliveryEnergy) {
		this.babyDeliveryEnergy = babyDeliveryEnergy;
	}

	public int getMaxGrassStack() {
		return maxGrassStack;
	}

	public void setMaxGrassStack(int maxGrassStack) {
		this.maxGrassStack = maxGrassStack;
	}
}
