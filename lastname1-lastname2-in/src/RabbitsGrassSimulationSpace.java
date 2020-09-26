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

    public int get_grass_at(int x, int y){
        int a;
        a= ((Integer) grass_space.getObjectAt(x,y)).intValue();
        return a;
    }

    public int get_total_grass(){
        int total_grass = 0;
        for(int i = 0; i < grass_space.getSizeX(); i++){
            for(int j = 0; j < grass_space.getSizeY(); j++){
                if(get_grass_at(i, j)!=0) total_grass+=1;
            }
        }
        return total_grass;
    }

    public boolean get_rabbit_at(int x, int y){
        boolean rabbit_value=false;
        if(rabbit_space.getObjectAt(x,y)!= null){
            rabbit_value = true;
        }
        return rabbit_value;
    }

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

    public void add_new_rabbit(RabbitsGrassSimulationAgent rabbit, int x, int y){
        rabbit_space.putObjectAt(x,y,rabbit);
        rabbit.set_x(x);
        rabbit.set_y(y);
        rabbit.set_space(this);
    }

    public Object2DGrid get_grass_space(){
        return grass_space;
    }

    public Object2DGrid get_rabbit_space(){
        return rabbit_space;
    }

    public void remove_rabbit_at(int x, int y){
        rabbit_space.putObjectAt(x, y, null);
    }

    public void move_rabbit_to(int x, int y, int new_x, int new_y){
        RabbitsGrassSimulationAgent rabbit = (RabbitsGrassSimulationAgent) rabbit_space.getObjectAt(x, y);
        rabbit_space.putObjectAt(x, y, null);
        rabbit_space.putObjectAt(new_x, new_y, rabbit);
    }

    public void grass_eaten(int x, int y){
        grass_space.putObjectAt(x,y,0);
    }

}

