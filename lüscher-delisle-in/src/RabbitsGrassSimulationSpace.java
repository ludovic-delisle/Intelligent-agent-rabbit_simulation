import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.SimUtilities;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

public class RabbitsGrassSimulationSpace {

    private Object2DGrid grass_space;
    private Object2DGrid rabbit_space;
    private int grass_stack;

    public RabbitsGrassSimulationSpace(int size, int maxGrassStack){
        grass_space = new Object2DGrid(size, size);
        rabbit_space = new Object2DGrid(size, size);
        grass_stack=maxGrassStack;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                grass_space.putObjectAt(i,j,0);
            }
        }
    }

    /**
     * used when a rabbit tries to eat
     * @param x coordinate x
     * @param y coordinate y
     * @return the amount of grass at position (x,y)
     */
    public int get_grass_at(int x, int y){
        int a;
        a= ((Integer) grass_space.getObjectAt(x,y)).intValue();
        return a;
    }

    /**
     * used for the plots
     * @return the total amount of grass in the simulation space
     */
    public int get_total_grass(){
        int total_grass = 0;
        for(int i = 0; i < grass_space.getSizeX(); i++){
            for(int j = 0; j < grass_space.getSizeY(); j++){
                if(get_grass_at(i, j)!=0) total_grass+=1;
            }
        }
        return total_grass;
    }

    /**
     * used when a rabbit tries to move to position (x, y) or when a rabbit is born
     * @param x coordinate x
     * @param y coordinate y
     * @return whether or not there is already a rabbit at the position (x, y)
     */
    public boolean get_rabbit_at(int x, int y){
        boolean rabbit_value=false;
        if(rabbit_space.getObjectAt(x,y)!= null){
            rabbit_value = true;
        }
        return rabbit_value;
    }

    /**
     * adds a given number of grass to add to the simulation space
     * @param nbr_of_new_grass the number of grass to add
     */
    public void add_new_grass(int nbr_of_new_grass){
        for(int i = 0; i < nbr_of_new_grass; i++){
            int x = (int) (Math.random() * (grass_space.getSizeX()));
            int y = (int) (Math.random() * (grass_space.getSizeY()));
            int a = get_grass_at(x, y) +1;
            if (a<=grass_stack) {
                grass_space.putObjectAt(x, y, a);
            }
        }
    }

    /**
     * Adds a new rabbit in the simulation
     * @param rabbit new rabbit we're adding
     * @param x coordinate x of new rabbit
     * @param y coordinate y of new rabbit
     */
    public void add_new_rabbit(RabbitsGrassSimulationAgent rabbit, int x, int y){
        rabbit_space.putObjectAt(x,y,rabbit);
        rabbit.set_x(x);
        rabbit.set_y(y);
        rabbit.set_space(this);
    }

    /**
     *
     * @return a grid with the amount of grass at each spot
     */
    public Object2DGrid get_grass_space(){
        return grass_space;
    }

    /**
     *
     * @return a grid with rabbits
     */
    public Object2DGrid get_rabbit_space(){
        return rabbit_space;
    }

    /**
     * removes the rabbit at coordinate (x,y)
     * @param x coordinate x
     * @param y coordinate y
     */
    public void remove_rabbit_at(int x, int y){
        rabbit_space.putObjectAt(x, y, null);
    }

    /**
     * moves the rabbit at coordinate (x, y) to coordinate (new_x, new_y)
     * @param x current x coordinate
     * @param y current y coordinate
     * @param new_x new x coordinate
     * @param new_y new y coordinate
     */
    public void move_rabbit_to(int x, int y, int new_x, int new_y){
        RabbitsGrassSimulationAgent rabbit = (RabbitsGrassSimulationAgent) rabbit_space.getObjectAt(x, y);
        rabbit_space.putObjectAt(x, y, null);
        rabbit_space.putObjectAt(new_x, new_y, rabbit);
    }

    /**
     * removes grass at coordinate (x, y) after a rabbit has eaten it
     * @param x coordinate x
     * @param y coordinate y
     */
    public void grass_eaten(int x, int y){
        grass_space.putObjectAt(x,y,0);
    }

}

