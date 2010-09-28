public class MarkovHistory{
	
	public MarkovHistory(String opponent_name){
		this.chainCount = initializeChain();
		this.opponent_name = opponent_name;
	}
	
	public MarkovHistory(String opponent_name, int[][] opponentHist){
		this.chainCount = opponentHist;
		this.opponent_name = opponent_name;
	}
	
	public int[][] chainCount = new int[ThrowChain.values().length][RPSThrow.values().length];
	public String opponent_name;
	
	public int[][] initializeChain(){		

		int[][] hist = new int[ThrowChain.values().length][RPSThrow.values().length];
		
		// Initialize the history to zeros.. This should only happen if the opponent has no history.
		for(int i=0; i<ThrowChain.values().length; i++){
			for(int j=0; j<RPSThrow.values().length; j++){
				hist[i][j] = 1;
			}
		}
		return hist;
	}
}