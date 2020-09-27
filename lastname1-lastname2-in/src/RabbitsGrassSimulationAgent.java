import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Object2DGrid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.

 * @author
 */

public class RabbitsGrassSimulationAgent implements Drawable {
	private int x;
	private int y;
	private int vx;
	private int vy;
	private int life;
	private int grassEnergy;
	private int rabbitEnergyConsumption;
	private RabbitsGrassSimulationSpace space;

	public RabbitsGrassSimulationAgent(int birth_life, int nutrition_from_grass, int rabbitEnergyConsumption){
		set_speed(); // set random directions
		life = birth_life;
		grassEnergy=nutrition_from_grass;
		this.rabbitEnergyConsumption=rabbitEnergyConsumption;
	}

	/**
	 * sets the speed of the rabbit so that it will randomly move to a spot next to him.
	 * rabbits cannot move diagonally and thus can only move in 1 direction at once (x or y)
	 */
	private void set_speed(){

		vx=0;
		vy=0;

		if(Math.random() < 0.5) {
			if(Math.random() < 0.5) {
				vx = 1;
			}
			else {
				vx = -1;
			}
		}
		else {
			if(Math.random() < 0.5) {
				vy = 1;
			}
			else {
				vy = -1;
			}
		}
	}

	/**
	 * returns a colored circle to be drawn by uchicago.src.sim.gui.Drawable
	 * While white at full energy, the less energy the rabbit has, the darker its color will be
	 * @param arg0 the graphic simulation where we want to draw our rabbits
	 */
	public void draw(SimGraphics arg0) {
		int fcolor=250;
		if(life<50){
			 fcolor=100;
		}
		else if(life<100){
			 fcolor=150;
		}
		else if(life<150){
			 fcolor=200;
		}
		else if(life<200){
			 fcolor=220;
		}
		Color color = new Color(fcolor, fcolor, fcolor);

		arg0.drawFastCircle(color);
		
	}
	//returns the energy of the rabbit
	public int get_life(){
		return life;
	}

	/**
	 * decrease the energy of the rabbit by the amount of the given parameter
	 * @param life_taken energy to take away from the rabbit
	 */
	public void decrease_life(int life_taken){
		life -= life_taken;
	}

	/**
	 * Looks at the spot where the rabbit is to see if there's a plant.
	 * If there is a plant, the rabbit eats it and retrieve its energy, the plant disappears.
	 */
	public void try_to_eat() {
		if (space.get_grass_at(x, y)>0){

			life += grassEnergy*space.get_grass_at(x, y);
			space.grass_eaten(x,y);
		}
	}

	public int getX() {return x; }

	public int getY() {return y; }

	public void set_x(int new_x) { this.x=new_x; }

	public void set_y(int new_y) { this.y=new_y; }

	/**
	 * Sets the simulation space
	 * @param rabbit_space simulation space of this rabbit
	 */
	public void set_space(RabbitsGrassSimulationSpace rabbit_space){
		space = rabbit_space;
	}

	/**
	 * gives the actions that the rabbit does at each simulation step
	 * 1) calculate a new position on the grid and check that there is no other rabbit at that position before moving
	 * 2) tries to eat
	 * 3) looses some energy
	 */
	public void step(){

		set_speed();

		int new_x = this.x + vx;
		int new_y = this.y + vy;

		Object2DGrid grid = space.get_rabbit_space();

		int size= grid.getSizeX();

		if(new_x>=size){
			new_x=0;
		}
		else if(new_x<0){
			new_x=size-1;
		}

		if(new_y>=size){
			new_y=0;
		}
		else if(new_y<0){
			new_y=size-1;
		}

		if(!space.get_rabbit_at(new_x, new_y)){
			space.move_rabbit_to(x, y, new_x, new_y);
			set_x(new_x);
			set_y(new_y);
		}
		try_to_eat();

		life-=rabbitEnergyConsumption;
	}

}
