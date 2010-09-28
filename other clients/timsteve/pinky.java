//tim|steve

import java.io.*;
import java.util.*;

public class pinky extends Client {
    
    class StrategyDetector {
        int strategy = 0;
        private boolean beatsLast = false;
        public float beatsLastCertainty = 0.01f;
        int myLast = -1;
        int theirLast = -1;
        int totalRounds = 0;
        int theirWins = 0;
        boolean holyFuck = false;
        Random rand;

        public StrategyDetector() {
            super();
            rand = new Random();
        }

        public void update(int mine, int theirs) {
            if (myLast >= 0 && theirLast >= 0) {
                totalRounds += 1;
                if (theirs == (myLast + 1) % 3) {
                    beatsLastCertainty += 0.05f;
                    theirWins += 1;
                } else {
                    beatsLastCertainty = 0;
                }
                if (beatsLastCertainty > 0.5f && beatsLastCertainty - 0.05f <= 0.5f) {
                    System.out.println("Switching to BeatLast beater");
                }
                if (totalRounds > 70 && (float)theirWins/(float)totalRounds > 0.4) {
                    holyFuck = true;
                }
                if (holyFuck) {
                    System.out.println("HOLY FUCKING SHIT");
                }
            }
            myLast = mine;
            theirLast = theirs;
        }

        public boolean certain() {
            return (beatsLastCertainty > 0.5 || holyFuck);
        }

        public int advice() {
            if (holyFuck) return rand.nextInt(3);
            return (myLast + 2) % 3;
        }
    }

	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: pinky port");
		}
		int secure_port=Integer.parseInt(args[0]);

		pinky client=new pinky(secure_port);
		client.playGame();
	}
    
    private int lastThrow = -1;
    private int[][] p;
    private Random rand;
    private StrategyDetector strat;
    LinkedList<String> moveLog;
    
    public pinky(int securePort) {
		super(securePort);
        rand = new Random();
        strat = new StrategyDetector();
        p = new int[3][3];
		this.name="pinky";
		moveLog = new LinkedList<String>();
	}
    
    private int c(RPSThrow r) {
        switch(r) {
            case rock:
                return 0;
            case paper:
                return 1;
            case scissors:
                return 2;
        }
        return -1;
    }
    
    private void zap(int t) throws IOException {
        t = t % 3;
        System.out.println("I throw " + t);
        System.out.println("-------");
        switch(t) {
            case 0:
                this.throwRock();
                return;
            case 1:
                this.throwPaper();
                return;
            case 2:
                this.throwScissors();
                return;
        }
        System.out.println("pinky confused");
    }

	@Override
	protected synchronized void resultHandler(Result result) {
        int r = c(result.theyThrew);
        moveLog.add(new Integer(r).toString());
        strat.update(c(result.iThrew), r);
        System.out.println("they threw " + r);
        if (lastThrow >= 0 && r >= 0) p[lastThrow][r] += 1;
        lastThrow = r;
	}

	@Override
	protected synchronized void throwRequestHandler() {
		try {
		    if(strat.certain()) {
		        int t = strat.advice();
		        System.out.println("phoned a friend: " + t);
		        zap(t);
		    } else {
		        zap(getMove());
		    }
		} catch (IOException e) {
		    System.out.println("pinky sad");
			e.printStackTrace();
		}
	}
    
    public int getMove() {
        for(int i=0; i<p.length; i++) {
	        for(int j=0; j<p[i].length; j++) {
	            System.out.print(p[i][j] + " ");
	        }
	        System.out.println();
	    }
	    if (lastThrow >= 0) {
	        int[] deezNuts = p[lastThrow];
	        int spread = 0;
	        for (int i=0; i<deezNuts.length; i++)
	            spread += deezNuts[i] + 1;
	        int which = rand.nextInt(spread);
	        for(int i=0; i<2; i++) {
	            if (which <= deezNuts[i]) {
	                return i+1;
                }
		        spread -= deezNuts[i]+1;
	        }
	        return 2;
	    } else {
	        System.out.println("no clue, throwing a rand");
	        return rand.nextInt(3);
	    }
    }
    
    @Override
    protected void shutdown() {
        ListIterator itr = moveLog.listIterator();
        while(itr.hasNext()) System.out.println(itr.next());
        super.shutdown();
    }
}
