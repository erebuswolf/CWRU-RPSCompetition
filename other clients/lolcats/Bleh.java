// Team Schrodinger's Lolcats

import java.io.IOException;

public class Bleh extends Client {
	
	// Variables to track opponent's throws (goes two back)
	private static final int numGames = 1000;
	public int gameNum = 0;
	public static RPSThrow[] theirResults;
	public static RPSThrow[] myResults;
	public static int[][][] rpsCount;
	
	// Go through list of previous throws and count
	public void updateParams(){
		int[][][] rps = new int[3][3][3];
		
		RPSThrow[] moves = new RPSThrow[3]; // for reference
		moves[0] = RPSThrow.rock; moves[1] = RPSThrow.paper; moves[2] = RPSThrow.scissors;
		
		for (int g=2; g<theirResults.length; g++)
			for (int i=0; i<moves.length; i++)
				for (int j=0; j<moves.length; j++)
					for (int k=0; k<moves.length; k++) {
						try {
						if(theirResults[g] == moves[k] && theirResults[g-1] == moves[j] && theirResults[g-2] == moves[i])
							rps[i][j][k]++;
						} catch (Exception e) {
						}
					}
		
		// copy to rpsCount
		for (int i=0; i<rpsCount.length; i++)
			for (int j=0; j<rpsCount[i].length; j++)
				for (int k=0; k<rpsCount[i].length; k++)
					rpsCount[i][j][k] = rps[i][j][k];
	}
	
	// Find the most likely based on the last two throws
	public int findHighest(){
		int last=0, laster=0;
		RPSThrow tlast = RPSThrow.garbage, tlaster = RPSThrow.garbage;
		
		if (gameNum > 1)
			tlast = theirResults[gameNum-1];
		if (gameNum > 2)
			tlaster = theirResults[gameNum-2];
		
		switch(tlast){
			case rock: last = 0; break;
			case paper: last = 1; break;
			case scissors: last = 2; break;
			default: last = (int)Math.random()*2;
		}
		switch(tlaster){
			case rock: laster = 0; break;
			case paper: laster = 1; break;
			case scissors: laster = 2; break;
			default: last = (int)Math.random()*2;
		}
		
		// given their last two throws...
		double probs = 0.0;
		int highest = 0;
		for (int i=0; i<rpsCount.length; i++)
		{
			double temp = ((double)rpsCount[laster][last][i]) / (rpsCount[laster][last][0] + rpsCount[laster][last][1] + rpsCount[laster][last][2]);
			if (temp > probs) {
				probs = temp;
				highest = i;
			}
			else if (temp == probs)
			{
				int tiebreaker = (int)Math.random()*2;
				if (tiebreaker == 0)
					highest = i;
			}
		}
		return highest;
	}
	
	@Override
	protected synchronized void resultHandler(Result result) {
		// keep track of what they threw
		theirResults[gameNum] = result.theyThrew;
		updateParams();
		gameNum++;
	}

	@Override
	protected synchronized void throwRequestHandler() {
		try {
			int s = findHighest(); // This is the prediction for the opponent: 0=rock, 1=paper, 2=scissors
			switch(s){
				case 0: this.throwPaper(); break;
				case 1: this.throwScissors(); break;
				case 2: this.throwRock(); break;
				default: System.out.println("FAILSAUCE! s=" + Integer.toString(s)); this.throwPaper();
			}
			this.throwRock();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bleh(int securePort) {
		super(securePort);
		this.name="Bleh";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error usage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		Bleh client=new Bleh(secure_port);
		
		// Initialize arrays
		theirResults = new RPSThrow[numGames];
		for (int i=0; i<numGames; i++)
			theirResults[i] = RPSThrow.garbage;
		
		rpsCount = new int[3][3][3]; // count of each possible combination
		for (int i=0; i<3; i++)
			for (int j=0; j<3; j++)
				for (int k=0; k<3; k++)
					rpsCount[i][j][k] = 0;
					
		client.playGame();  // this loops around until it is told to shut down
	}
}
