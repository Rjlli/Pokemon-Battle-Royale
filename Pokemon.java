/*Pokemon
 *
 *This creates pokemons and holds the values of each pokemon
 *and handle the attacking and damage of the pokemon. this will
 *also handle the special moves and energy of the pokemon
 */

import java.util.*;
public class Pokemon{
	
	private Scanner kb = new Scanner(System.in);
	private Random rand = new Random();

	//num_hits is the number of times wild storm hits
	private int num_hits;

	//this will say if stun is true or false
	private boolean is_stunned;

	//this will say if disabled is true or false
	private boolean disabled;
	
	//this will say if wild card is hits or miss
	private boolean wild_card;
	
	//this will say if wild storm hits again or not or even miss
	private boolean wild_storm;
	private String name;
	private int hp;
	private int max_hp;
	private int energy;
	private String type;
	private String resistance;
	private String weakness;
	private int num_attacks;
	private ArrayList<String> attack_name = new ArrayList<String>();
	private ArrayList<Integer> energy_cost = new ArrayList<Integer>();
	private ArrayList<Integer> damage = new ArrayList<Integer>();
	private ArrayList<String> special = new ArrayList<String>();
	
	//get each stat of the pokemon and define the private variables
	public Pokemon(String stats){
		String[] items = stats.split(",");
		name = items[0];
		hp = Integer.valueOf(items[1]);
		max_hp = hp;
		energy = 50;
		type = items[2];
		resistance = items[3];
		weakness = items[4];
		num_attacks = Integer.valueOf(items[5]);
		
		//this will define the varibles for the attack, energy cost
		//damage of the attact and specials for the attack 
		for(int i=6;i<6+4*num_attacks;i+=4){
			attack_name.add(items[i]);
			energy_cost.add(Integer.valueOf(items[i+1]));
			damage.add(Integer.valueOf(items[i+2]));
			special.add(items[i+3]);
		}
	}
	
	public String toString(){
		return name;
	}
	public int getHp(){
		return hp;
	}
	public int getEnergy(){
		return energy;
	}
	public String getType(){
		return type;
	}
	public String getResistance(){
		return resistance;
	}
	public String getWeakness(){
		return weakness;
	}
	public int getNum_attacks(){
		return num_attacks;
	}
	public ArrayList<String> getAttack_name(){
		return attack_name;
	}
	public ArrayList<Integer> getEnergy_cost(){
		return energy_cost;
	}
	public ArrayList<Integer> getDamage(){
		return damage;
	}
	public ArrayList<String> getSpecial(){
		return special;
	}
	
	//this will be where they lose hp
	public void setHp(int dmg){
		hp -= dmg;
		if(hp < 0){
			hp = 0;
		}
	}
	
	//this will add 20 to their hp
	//but cant go over their max hp
	//this will reset their energy as well
	public void heal(){
		hp+=20;
		if(hp>max_hp){
			hp = max_hp;
		}
		energy = 50;
	}
	
	//thiw adds 10 energy to the energy
	//to their max 50 energy
	public void recover(){
		energy += 10;
		if(energy > 50){
			energy = 50;
		}
	}
	
	//reutrn if they are stunned or not
	public boolean getStun(){
		return is_stunned;
	}
	
	//make stunned false
	public void setStun(){
		is_stunned = false;
	}
	
	//randomly choose 50% chance of stunned or not stunned
	public void stun_chance(){
		is_stunned = rand.nextBoolean();
	}
	
	//they are no longer diabled
	public void setDisable(){
		disabled = false;
	}
	
	//they are disabled
	public void disabled(){
		disabled = true;
	}
	
	//this will recharge their energy if they pass
	public void recharge(){
		energy += 20;
		
		//keeps their energy at 50 most
		if(energy > 50){
			energy = 50;
		}
	}
	
	//this will take care of the damage of the attack and take away from the opposing enimies hp
	public int attack(Pokemon opponent){
		int choice;
		
		//it will keep asking for them to choose a move if they don't have enough energy
		//or go back to the option menu to choose another option like retreat or pass
		while(true){
			System.out.println("\n+--+----------------------+----------------------+----------------------+--------------------+");
			System.out.printf("|  |%-20s  |%-20s  |%-20s  |%-20s|\n","Attack Name","Energy Cost","Damage","Special");
			System.out.println("+--+----------------------+----------------------+----------------------+--------------------+");
			for(int i = 0; i < num_attacks; i++){
				System.out.printf("|%d.|%-20s  |%-20d  |%-20d  |%-20s|\n",i+1,attack_name.get(i),energy_cost.get(i),damage.get(i),special.get(i));
				System.out.println("+--+----------------------+----------------------+----------------------+--------------------+");
			}
			System.out.println("Pick your move(0 to go back).");
			choice = kb.nextInt();
			
			//choice as 0 represents going back to options menu
			if(choice == 0){
				break;
			}
			
			//this will check if they have enough energy to use the move or not
			if(check_energy(energy_cost.get(choice-1))){
				
				//takes away their energy
				energy-=energy_cost.get(choice-1);
				System.out.print("\n"+name+" use "+attack_name.get(choice-1)+".");
				
				//this has a chance to stun the enemy
				if(special.get(choice-1).equals("stun")){
					opponent.stun_chance();
				}
				
				//this has a chance to hit or miss the attack
				else if(special.get(choice-1).equals("wild card")){
					wild_card = rand.nextBoolean();
					if(wild_card == false){
						System.out.println("\n\n"+name+" attack has missed.");
						break;
					}
				}
				
				//they can miss the attack or keep on attacking if true
				else if(special.get(choice-1).equals("wild storm")){
					wild_storm = rand.nextBoolean();
					num_hits = 1;
					if(wild_storm == false){
						System.out.println("\n\n"+name+" attack has missed.");
						break;
					}
				
					//keeps attacking if wild storm hits, and has continuous 50% chance of hitting
					else{
						
						//this takes away from the enemies hp for the first time
						opponent.setHp(damage(damage.get(choice-1),type,opponent.getResistance(),opponent.getWeakness()));
						while(true){
							if(opponent.getHp() == 0){
								break;
							}
							wild_storm = rand.nextBoolean();
							
							//keeps on attacking if wild storm is true
							if(wild_storm){
								opponent.setHp(damage(damage.get(choice-1),type,opponent.getResistance(),opponent.getWeakness()));
								num_hits += 1;
							}
							else{
								break;
							}
						}
						if(num_hits>1){
							System.out.printf("The move has hit %d times.\n",num_hits);
						}
						wild_storm = false;
						break;
					}
				}
				
				//disable the opponent once and if they are already disabled it does not disable again
				else if(special.get(choice-1).equals("disable")){
					if(opponent.disabled == false){
						opponent.disabled();
						System.out.println("\n\n"+opponent+" has been disabled for the rest of this battle.");
					}
				}
				
				//recharges their energy
				else if(special.get(choice-1).equals("recharge")){
					recharge();
				}
				
				//puts a space for the interface to look better
				if(!special.get(choice-1).equals("disable")){
					System.out.print("\n");
				} 
				opponent.setHp(damage(damage.get(choice-1),type,opponent.getResistance(),opponent.getWeakness()));
				break;
			}
			else{
				System.out.println("\n"+name+" does not have enough energy to use this move.");
				System.out.println("Please choose another move or go back to choose another option.");
			}
		}

		if(choice == 0){
			return 0;
		}
		else{
			return 1;
		}
	}
	
	//this will choose random move for enemy pokemon which is the same as
	//how they choose a move for the player, but it will randomly choose a move
	public void o_attack(Pokemon player){
		int counter = 0;
		while(true){
			
			//this will check if they have enough energy for any moves
			int o_choice = rand.nextInt(num_attacks);
			for(int i = 0; i < num_attacks; i++){
				if(energy < energy_cost.get(i)){
					counter += 1;
				}
			}
			if(counter == num_attacks){
				System.out.println("\nThe enemy does not have enough energy to do anything.");
				break;
			}
			if(check_energy(energy_cost.get(o_choice))){
				energy-=energy_cost.get(o_choice);
				System.out.print("\n"+name+" used "+attack_name.get(o_choice)+".");
				if(special.get(o_choice).equals("stun")){
					player.stun_chance();
				}
				else if(special.get(o_choice).equals("wild card")){
					wild_card = rand.nextBoolean();
					if(wild_card == false){
						System.out.println("\n\n"+name+" attack has missed.");
						break;
					}
				}
				else if(special.get(o_choice).equals("wild storm")){
					wild_storm = rand.nextBoolean();
					num_hits = 1;
					if(wild_storm == false){
						System.out.println("\n\n"+name+" attack has missed.");
						break;
					}
					else{
						player.setHp(damage(damage.get(o_choice),type,player.getResistance(),player.getWeakness()));
						while(true){
							if(player.getHp() == 0){
								break;
							}
							wild_storm = rand.nextBoolean();
							if(wild_storm){
								player.setHp(damage(damage.get(o_choice),type,player.getResistance(),player.getWeakness()));
								num_hits += 1;
							}
							else{
								break;
							}
						}
						if(num_hits>1){
							System.out.printf("This move has hit %d times.\n", num_hits);
						}
						wild_storm = false;
						break;
					}
				}
				else if(special.get(o_choice).equals("disable")){
					if(player.disabled == false){
						player.disabled();
						System.out.println("\n\n"+player+" has been disabled for the rest of this battle.");
					}
				}
				else if(special.get(o_choice).equals("recharge")){
					recharge();
				}
				if(!special.get(o_choice).equals("disable")){
					System.out.print("\n");
				} 
				player.setHp(damage(damage.get(o_choice),type,player.getResistance(),player.getWeakness()));
				break;
			}
			else{
				System.out.print("\n"+name+" does not have enough energy.");
			}
		}
	}
	
	//this will check if they have enough energy for using the move
	public boolean check_energy(int move_cost){
		return move_cost <= energy;
	}
	
	//this will check their resistance and weakness for amount of damage
	public int damage(int dmg, String p_type, String o_resis, String o_weak){
		
		//this will take away damage when they are disabled, but can't be lower than 0
		if(disabled){
			if(dmg >= 10){
				dmg -= 10;
			}
			else{
				dmg = 0;
			}
		}
		if(p_type.equals(o_weak)){
			System.out.println("\nIt was super effective.");
			return dmg*2;
		}
		else if(p_type.equals(o_resis)){
			System.out.println("\nIt was not very effective.");
			return dmg/2;
		}
		else{
			return dmg;
		}
	}
	
}