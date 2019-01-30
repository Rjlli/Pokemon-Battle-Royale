/*Ricky Li
 *Pokemon Arena
 *
 *This class will ask the user to choose 4 pokemons from the pokemons printed out
 *then it will start the battle in the loop till its finished.
 *In the loop it will call other methods from this class and pokemon class
 *after the battles are over it will tell them if they won or loss
 */

import java.util.*;
import java.io.*;

public class PokemonArena{
	private static Scanner kb = new Scanner(System.in);
	private static Random rand = new Random();
	
	//the variable win tells you if you won or not
	private static boolean win = true;
	
	//if it is the player turn or not
	private static boolean p_turn;
	
	//to check if on round is done
	private static boolean one_round;
	
	//ask them to choose the pokemon the first time 
	//and after pokemon dies
	private static boolean once = true;
	
	//o_pokemons has all of the opponent pokemons
	private static ArrayList<Pokemon> o_pokemons = new ArrayList<Pokemon>();
	
	//p_pokemons has all of the player pokemons
	private static ArrayList<Pokemon> p_pokemons = new ArrayList<Pokemon>();
	private static Pokemon cur_p_poke;
	private static Pokemon cur_o_poke;

	public static void main(String [] args)throws IOException{
		Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
	
		//num_pokemon gets the first line which is the number of pokemons
		int num_pokemon = Integer.parseInt(inFile.nextLine());
	
		//nums holds the number for the pokemon they choose to use
		ArrayList<Integer> nums = new ArrayList<Integer>();
		
		//this will make a chart of all of the pokemons with the numbers of that pokemon
		System.out.println("+---------------+--------------+");
		for(int i=0 ;i<num_pokemon; i++){
			o_pokemons.add(new Pokemon(inFile.nextLine()));
	
			//this prints out the first collumn in the chart of pokemon
			if((i+1)%2 != 0){
				System.out.printf("|%2d.%-12s|" ,i+1,o_pokemons.get(i));
				if(inFile.hasNextLine() == false){
					System.out.println("              |");
					System.out.println("+---------------+--------------+");
				}
			}
	
			//this prints out the pokemon in the second column in the chart of pokemon
			else{
				System.out.printf("%2d.%-11s|\n" ,i+1,o_pokemons.get(i));
				System.out.println("+---------------+--------------+");
			}
		}
		inFile.close();
		
		//this will ask for them to choose the pokemon
		for(int i=0; i<4; i++){
			System.out.println("\nEnter the number of the pokemon: ");
			int num = validate(kb.nextInt(),num_pokemon);
	
			//this will keep on checking if they have already choose the pokemon or
			//if it is an invalid input in till they 
			if(nums.contains(num)){
				while(nums.contains(num)){
					System.out.println("\nYou already picked this pokemon, pick another pokemon.");
					num = validate(kb.nextInt(),num_pokemon);
				}
			}
			nums.add(num);
			p_pokemons.add(o_pokemons.get(nums.get(i)-1));
			
		}
		for(int i=0; i<4; i++){
			o_pokemons.remove(o_pokemons.get(o_pokemons.indexOf(p_pokemons.get(i))));
		}
		Collections.shuffle(o_pokemons);
		
		//in this loop it will take care of the battles and calls the main methods to
		//fight each opposing pokemon
		for(int i=0; i<num_pokemon-4; i++){
	
			//this makes cur_o_poke the current opposing pokemon
			cur_o_poke = o_pokemons.get(i);
	
			//randomly chooses whos turn
			random_turn();
			one_round = p_turn;
	
			//heal all the pokemon after the battle
			for(int k = 0; k<p_pokemons.size(); k++){
				p_pokemons.get(k).heal();
				
				//this makes the pokemon(s) not disabled 
				p_pokemons.get(k).setDisable();
			}
			System.out.println("\nYou are going to be battling "+cur_o_poke+".");
			once = true;
			choose_poke();
			while(true){
	
				//if the player pokemon has fainted they have to choose another pokemon
				if(cur_p_poke.getHp() == 0){
					System.out.println("\nYour pokemon has fainted.\n");
					once = true;
					p_pokemons.remove(cur_p_poke);
	
					//this checks if all the player pokemons have fainted
					if(p_pokemons.isEmpty()){
						win = false;
						break;
					}
					choose_poke();
				}
	
				//breaks out of the loop if the opponent pokemon fainted 
				if(cur_o_poke.getHp() == 0){
					System.out.printf("\n%s, has fainted.\n",cur_o_poke);
					break;
				}
	
				//this will recharge all pokemon's energy after each round
				if(one_round == p_turn){
					for(int h = 0; h<p_pokemons.size(); h++){
						p_pokemons.get(h).recover();
					}
					cur_o_poke.recover();
				}
				battle();
				p_turn =! p_turn;
			}
		
			//this checks if all the player pokemons have fainted
			if(p_pokemons.isEmpty()){
		
				//breaks out of the loop and make winning false
				break;
			}
		}
		
		//if player win say they won
		if(win == true){
			System.out.println("\ncongratulations, you are now the Trainer Supreme!");
			System.out.println("You have defeated all of the pokemons in the arena.");
		}
	
		//if player lost say they lost
		else{
			System.out.println("All of your Pokemons have fainted, you have lost!");
		}
	}
	
	//this will check if it is a valid input for the given situation
	//when the input is valid then it exits the loop 
	public static int validate(int input,int max){
		while(input < 0 || input > max){
			System.out.println("\nInvalid Input");
			input = kb.nextInt();
		}
		return input;
	}
	
	//this lets them choose thier pokemon to use for the current battle
	public static int choose_poke(){

		//this prints out the pokemons in the party in a chart
		System.out.println("\n+--------------------------------+");
		for(int k = 0; k<p_pokemons.size(); k++){
			System.out.printf("|%d.%-13s HP %3d Energy %2d|\n",k+1,p_pokemons.get(k),p_pokemons.get(k).getHp(),p_pokemons.get(k).getEnergy());
			System.out.println("+--------------------------------+");
		}

		//this ask them to choose a pokemon or go back to the options
		//attack, retreat or pass
		if(once){
			System.out.println("\nChoose a Pokemon that you want to use for battle.");
			once = false;
		}
		else{
			System.out.println("\nChoose a Pokemon that you want to use for battle(0 to go back).");
		}
		
		int poke_num = validate(kb.nextInt(),p_pokemons.size());
		if(poke_num == 0){
			return 0;
		}

		//this makes cur_p_poke the current pokemon they choose
		cur_p_poke = p_pokemons.get(poke_num-1);
		System.out.println("\n"+cur_p_poke+", I choose you!");
		return 1;
	}
	
	//this will be the main method for the battle
	public static void battle(){

		//this say which pokemon you are fighting against
		System.out.println("\n+-----------------------------------+");
		System.out.printf("|%-14s  V S  %14s|",cur_p_poke,cur_o_poke);
		System.out.println("\n+-----------------+-----------------+");
		System.out.printf("|%-14s%3d|%-3d%14s|\n","HP",cur_p_poke.getHp(),cur_o_poke.getHp(),"HP");
		System.out.printf("|%-14s%3d|%-3d%14s|\n","Energy",cur_p_poke.getEnergy(),cur_o_poke.getEnergy(),"Energy");
		System.out.println("+-----------------+-----------------+");

		//goes to the option menu if it is your turn
		if(p_turn){
			if(cur_p_poke.getStun()){
				System.out.println("\n"+cur_p_poke+" is stunned");

				//makes the user pokemon no longer stunned
				cur_p_poke.setStun();
			}
			else{
				System.out.println("\nIt's your turn.");
				menu();
			}
		}
		else{
			System.out.println("\nIt's the enemy's turn");
			if(cur_o_poke.getStun()){
				System.out.println("\n"+cur_o_poke+" is stunned");
				cur_o_poke.setStun();
			}
			else{
				cur_o_poke.o_attack(cur_p_poke);
			}
		}
	}
	
	//this method will randomly choose
	public static void random_turn(){
		p_turn = rand.nextBoolean();
	}
	
	//this method will let them choose to attack, retreat or pass
	public static void menu(){
	
		//will keep showing menu when user chooses to go back
		while(true){
			System.out.println("\n1. Attack");
			System.out.println("2. Retreat");
			System.out.println("3. Pass");
			System.out.println("What do you want to do?");
			int choice_num = validate(kb.nextInt(),3);
			if(choice_num==1){
				if(cur_p_poke.attack(cur_o_poke)!=0){
					break;
				}
			}
			else if(choice_num==2){
				if(choose_poke()!=0){
					break;
				}			
			}
			else if(choice_num==3){
				break;
			}
		}
	}
}