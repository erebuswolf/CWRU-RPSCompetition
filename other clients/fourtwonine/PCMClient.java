import java.io.IOException;
import java.util.*;
import java.lang.Comparable;

/**
 * PCMClient
 *
 * Matt and Will
 * 
 * Team 429
 */

/*------------------------------------------------------------------------------
 * Choice
 *----------------------------------------------------------------------------*/
class Choice implements Comparable
{
	public int score = 0;

	public void didWin(){ ++score; }
	public void didLose(){ --score; }
	
	public int compareTo(Object c){
		if (this.score < ((Choice)c).score)
			return 1;
		else if (this.score > ((Choice)c).score)
			return -1;
		else
			return 0;
	}
	
	public String toString(){
		return String.format("%s %d", super.toString(), score);
	}
}

/*------------------------------------------------------------------------------
 * Choices
 *----------------------------------------------------------------------------*/
class Choices<T extends Choice>
{
	public ArrayList<T> choices;
	
	public Choices(){ this.choices = new ArrayList<T>(); }
	
	public T get(){ return get(0); }
	
	public T get(int index){
		sort();
		try {
			return choices.get(index);
		} catch(Exception e){
			return choices.get(0);
		}
	}
	
	public void add(T item){
		sort();
		choices.add(item);
	}
	
	public int firstIndexOf(T item){ return choices.indexOf(item); }
	
	protected void sort(){
		Collections.sort(choices);
	}
}

/*------------------------------------------------------------------------------
 * OpponetPredictor
 * 
 * Trys to predict what the opponet will do
 *----------------------------------------------------------------------------*/
abstract class OpponetPredictor extends Choice
{	
	abstract public RPSThrow predict();
}

/*------------------------------------------------------------------------------
 * HistoryBasedOpponetPredictor
 * 
 * Uses the history to predict the opponet
 *----------------------------------------------------------------------------*/
class HistoryBasedOpponetPredictor extends OpponetPredictor
{
	protected ArrayList<RPSThrow> history;
	
	public HistoryBasedOpponetPredictor(){}
	
	public HistoryBasedOpponetPredictor(ArrayList<RPSThrow>history){
		setHistory(history);
	}
	
	public void setHistory(ArrayList<RPSThrow>history){ this.history = history; }
	
	public RPSThrow predict(){ return RPSThrow.garbage; }
}

/*------------------------------------------------------------------------------
 * RandFirstOpponetPredictor
 * 
 * Randomly predicts what the opponets next move will be
 *----------------------------------------------------------------------------*/
class RandFirstOpponetPredictor extends OpponetPredictor
{
	public RPSThrow predict(){
		switch((int)(Math.random() * 3)){
			case 0:		return RPSThrow.scissors;
			case 1:		return RPSThrow.paper;
			case 2:		return RPSThrow.rock;
			default: 	return RPSThrow.garbage;
		}
	}
}

/*------------------------------------------------------------------------------
 * HistoryFirstOpponetPredictor
 * 
 * Predicts the opponets first move using the history
 * 
 * TODO: currently falls back to random while it should fallback to the next
 * best prediction
 *----------------------------------------------------------------------------*/
class HistoryFirstOpponetPredictor extends HistoryBasedOpponetPredictor
{
	public HistoryFirstOpponetPredictor(ArrayList<RPSThrow>history){
		super(history);
	}
	
	public RPSThrow predict(){
		int lastIndex = history.size() - 2;
		int bestLen = 0, bestIndex = -1;
		
		for (int i = lastIndex; i >= 0; --i){
			int lenFoundMatch = 0;
			int g = i;
			
			while (--g >= 0 && history.get(g) == history.get(lastIndex - lenFoundMatch))
				++lenFoundMatch;
			
			if (lenFoundMatch > bestLen)
				bestIndex = i;
		}
		
		if (bestIndex != -1)
			return history.get(bestIndex + 1);
		else
			return new RandFirstOpponetPredictor().predict();
	}
}

/*------------------------------------------------------------------------------
 * Strategy
 * 
 * Examines what we think the opponet will play and chooses a how to respond 
 * in order to win. Switching Strategies prevents predictable patterns in
 * responses.
 *----------------------------------------------------------------------------*/
abstract class Strategy extends Choice
{
 	abstract public RPSThrow get(RPSThrow theirThrow);
}

/*------------------------------------------------------------------------------
 * OppositeStrategy
 * 
 * Chooses the winning mode of the predicted move
 *----------------------------------------------------------------------------*/
class OppositeStrategy extends Strategy
{
	public RPSThrow get(RPSThrow theirThrow){
		switch (theirThrow){
			case rock:		return RPSThrow.paper;
			case paper:		return RPSThrow.scissors;
			case scissors:	return RPSThrow.rock;
			default: 		return RPSThrow.garbage;
		}
	}
}

/*------------------------------------------------------------------------------
 * OppositeOppositeStrategy
 * 
 * Chooses the winning mode of the OppositeStrategy
 *----------------------------------------------------------------------------*/
class OppositeOppositeStrategy extends Strategy
{
	public RPSThrow get(RPSThrow theirThrow){
		return new OppositeStrategy().get((new OppositeStrategy().get(theirThrow)));
	}
}

/*------------------------------------------------------------------------------
 * OppositeOppositeOppositeStrategy
 * 
 * Chooses the winning mode of the OppositeOppositeStrategy
 *----------------------------------------------------------------------------*/
class OppositeOppositeOppositeStrategy extends Strategy
{
	public RPSThrow get(RPSThrow theirThrow){
		return new OppositeStrategy().get((new OppositeOppositeStrategy().get(theirThrow)));
	}
}

/*------------------------------------------------------------------------------
 * PCMClient
 * 
 * The main client
 *----------------------------------------------------------------------------*/
public class PCMClient extends Client
{
	// instance variables ---->
	protected Choices<OpponetPredictor> firstOpponetPredictors = new Choices<OpponetPredictor>();
	protected Choices<OpponetPredictor> opponetPredictionPredictors = new Choices<OpponetPredictor>();
	
	protected Choices<Strategy> strategies = new Choices<Strategy>();
	
	// ----> history ---->
	protected ArrayList<RPSThrow> myHistory 	= new ArrayList<RPSThrow>();
	protected ArrayList<RPSThrow> theirHistory	= new ArrayList<RPSThrow>();
	
	
	@Override
	protected synchronized void resultHandler(Result result){
		int win = 0;
		RPSThrow iThrew = result.iThrew, theyThrew  = result.theyThrew;
		
		myHistory.add(iThrew);
		theirHistory.add(theyThrew);
		
		// determine the result of the match. {win, lose, tie}
		if (iThrew != theyThrew){
			switch (iThrew)
			{
			case rock:
				switch (theyThrew){
					case paper:			win = -1; 	break;
					case scissors:		win = 1;	break;
				}
				break;
			case paper:
				switch (theyThrew){
					case rock:			win = 1; 	break;
					case scissors:		win = -1;	break;
				}
				break;
			case scissors:
			default:
				switch (theyThrew){
					case rock:			win = -1; 	break;
					case paper:			win = 1;	break;
				}
				break;
			}
		}
		
		switch (win){
			case 0: // tie
				break;
			case 1: // win
				firstOpponetPredictors.get().didWin();
				strategies.get().didWin();
				break;
			case -1: // loose
				firstOpponetPredictors.get().didLose();
				strategies.get().didLose();
				break;
		}
	}

	@Override
	protected synchronized void throwRequestHandler() {
		RPSThrow theirThrow = firstOpponetPredictors.get().predict();
		RPSThrow ourThrow = strategies.get().get(theirThrow);

		try {
			switch (ourThrow){
				case rock:		throwRock(); 		break;
				case paper:		throwPaper();		break;
				case scissors:	throwScissors();	break;
				default: 		throwRock();		break;
			}
		} catch (Exception e){
			
		}
	}

	public PCMClient(int securePort) {
		super(securePort);
		this.name = "Pinecones on Mars";
		
		// firstOpponetPredictors ---->
		firstOpponetPredictors.add(new RandFirstOpponetPredictor());
		firstOpponetPredictors.add(new HistoryFirstOpponetPredictor(theirHistory));

		// strategies ---->
		strategies.add(new OppositeStrategy());
	}

	public static void main(String[] args){
		if (args.length < 1)
			System.out.println("error useage: PCMClient port");
		
		int secure_port = Integer.parseInt(args[0]);

		PCMClient client = new PCMClient(secure_port);
		client.playGame();
	}	
}