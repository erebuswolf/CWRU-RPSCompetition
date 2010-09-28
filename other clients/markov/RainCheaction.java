import java.io.IOException;
import java.util.Random;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class RainCheaction extends Client {
	
	ThrowChain state = ThrowChain.pp;
	
	MarkovHistory history = new MarkovHistory(this.opponent_name);
	MarkovHistories histories;
	boolean histExists = false;
	int histNum;
	RPSThrow lastThrow = RPSThrow.paper;
	
	protected String filepath = "markov.hist";
	
	@Override
	protected synchronized void resultHandler(Result result) {
		
		try{
		// record what they threw
		this.history.chainCount[state.ordinal()][result.theyThrew.ordinal()]++;
		}
		catch(NullPointerException ex){
			//ex.printStackTrace();
		}
		//System.out.println("they threw: "+ result.theyThrew.toString());
		// advance state
		this.state = advanceState(this.lastThrow.toString(), result.theyThrew.toString());
		this.lastThrow = result.theyThrew;
	}

	protected ThrowChain advanceState(String lastThrow, String currentThrow){
		
		ThrowChain nextState = null;
		
		if(lastThrow == "rock"){
			if(currentThrow == "rock") 
				nextState = ThrowChain.rr;
			else if(currentThrow == "paper")
				nextState = ThrowChain.rp;
			else
				nextState = ThrowChain.rs; 
		}
		else if(lastThrow == "paper"){
			if(currentThrow == "rock") 
				nextState = ThrowChain.pr;
			else if(currentThrow == "paper")
				nextState = ThrowChain.pp;
			else
				nextState = ThrowChain.ps; 
		}
		else{
			if(currentThrow == "rock") 
				nextState = ThrowChain.pr;
			else if(currentThrow == "paper")
				nextState = ThrowChain.pp;
			else
				nextState = ThrowChain.ps; 
		}
		return nextState;
		
	}
	
	protected RPSThrow pickThrow(double rockOdds, double paperOdds, double scissorOdds){
		
		RPSThrow throwme;
		
		Random gen = new Random();
		
		double rand = gen.nextDouble();
		
		if(rand < rockOdds){
			throwme = RPSThrow.paper;
		}
		else if(rand < rockOdds + paperOdds){
			throwme = RPSThrow.scissors;
		}
		else{
			throwme = RPSThrow.rock;
		}
		
		return throwme;
	}
	
	protected synchronized void throwRequestHandler() {

		RPSThrow throwMe;
		
		//System.out.print("State: "+this.state.toString()+" ");
		
		try{
		double numRock = this.history.chainCount[state.ordinal()][RPSThrow.rock.ordinal()];
		double numPaper = this.history.chainCount[state.ordinal()][RPSThrow.paper.ordinal()];
		double numScissor = this.history.chainCount[state.ordinal()][RPSThrow.scissors.ordinal()];
		
		double totalThrows = numRock + numPaper + numScissor;
		
		double rockOdds = numRock/totalThrows; 
		double paperOdds = numPaper/totalThrows;
		double scissorOdds = numScissor/totalThrows;
		
		throwMe = pickThrow(rockOdds, paperOdds, scissorOdds);
		//System.out.println("rockOdds: "+rockOdds+" paperOdds: "+paperOdds+" scisOdds: "+scissorOdds + " Throwing: "+throwMe.toString());
		}
		catch(NullPointerException ex){
			//ex.printStackTrace();
			throwMe = RPSThrow.rock;
		}
		
		try {
			
			switch(throwMe){
				case rock: this.throwRock();
				case paper: this.throwPaper();
				case scissors: this.throwScissors();
			}
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RainCheaction(int securePort) {
		super(securePort);
		this.name="ChainReaction";
		MarkovHistory temp = new MarkovHistory(this.opponent_name);//initHistory(this.opponent_name);
		if (temp != null){
			this.history = temp;
		}
		else{
			this.history = new MarkovHistory(this.opponent_name);
		}
	}
	
	protected MarkovHistory initHistory(String name){
		
		MarkovHistory history = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try{
			fis = new FileInputStream(filepath);
			ois = new ObjectInputStream(fis);
			this.histories = (MarkovHistories)ois.readObject(); 
			ois.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		catch(NullPointerException ex){
			ex.printStackTrace();
		}
		
		boolean enemyNotFound = true;
		int count = 0;
		
		if(this.histories == null){
			this.histories = new MarkovHistories();
		}
		else{
			while (enemyNotFound && count < this.histories.enemyList.size()){
				
				if (this.histories.enemyList.get(count).opponent_name == this.opponent_name){
					enemyNotFound = false;
					this.histExists = true;
					this.histNum = count;
					history = this.histories.enemyList.get(count);
				}
			}
		}
				
		return history;	
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		RainCheaction client=new RainCheaction(secure_port);
		client.playGame();
	}

	protected void shutdown(){
		gameON=false;
		s.close();
		//saveHistory();
		System.out.println("game over, shutting down");
	}
	
	private void saveHistory() {

		if(this.histExists){
			this.histories.enemyList.set(this.histNum, this.history);
		}
		else{
			this.histories.enemyList.add(this.history);
		}
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = new FileOutputStream(filepath);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this.histories);
			oos.close();
		}
		catch(IOException ex){
			ex.printStackTrace();	
		}
	}	
}