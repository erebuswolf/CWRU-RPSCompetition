import java.io.*;
import java.util.*;

public class inky extends Client {
    
    class StrategyDetector {
        int strategy = 0;
        private boolean beatsLast = false;
        public float beatsLastCertainty = 0.01f;
        int myLast = -1;
        int theirLast = -1;

        public StrategyDetector() {
            super();
        }

        public void update(int mine, int theirs) {
            if (myLast >= 0 && theirLast >= 0) {
                if (theirs == (myLast + 1) % 3) beatsLastCertainty += 0.05f;
                if (beatsLastCertainty > 0.5f && beatsLastCertainty - 0.05f <= 0.5f) {
                    System.out.println("OMG IT WORKED HOORAY");
                }
            }
            myLast = mine;
            theirLast = theirs;
        }

        public boolean certain() {
            return beatsLastCertainty > 0.5;
        }

        public int advice() {
            return (myLast + 2) % 3;
        }
    }
    
    private int lastThrow = -1;
    private int[][] p;
    private Random rand;
    private StrategyDetector strat;
    
    public inky(int securePort) {
		super(securePort);
        rand = new Random();
        strat = new StrategyDetector();
        p = new int[3][3];
		this.name="inky";
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
        System.out.println("inky confused");
    }

	@Override
	protected synchronized void resultHandler(Result result) {
        int r = c(result.theyThrew);
        strat.update(c(result.iThrew), r);
        System.out.println("they threw " + r);
        if (lastThrow >= 0 && r >= 0) p[lastThrow][r] += 1;
        lastThrow = r;
	}

	@Override
	protected synchronized void throwRequestHandler() {
		try {
		    if(strat.certain()) {
		        zap(strat.advice());
		    } else {
		        for(int i=0; i<p.length; i++) {
    		        for(int j=0; j<p[i].length; j++) {
    		            System.out.print(p[i][j] + " ");
    		        }
    		        System.out.println();
    		    }
    		    if (lastThrow >= 0) {
    		        int max = 0;
    		        for (int i=0; i<p[lastThrow].length; i++)
    		            if (p[lastThrow][i] > max) max = p[lastThrow][i];
    		        if (max == 0) {
    		            System.out.println("max(" + lastThrow + ")=0, choosing random");
    		            zap(rand.nextInt(3));
    		            return;
    		        }
    		        int numOptions = 0;
    		        for (int i=0; i<p[lastThrow].length; i++)
    		            if (p[lastThrow][i] == max) numOptions += 1;
    		        System.out.println(numOptions + " options");
    		        numOptions = rand.nextInt(numOptions);
    		        System.out.println("chose " + numOptions + "nth");
    		        int i = 0;
    		        do {
    		            if (p[lastThrow][i] == max) {
    		                if (numOptions == 0) {
    		                    System.out.println(lastThrow + ", " + i);
    		                    zap(i+1);
    		                    return;
    		                } else {
    		                    System.out.println("i am confuse");
    		                }
    		                numOptions -= 1;
    		            }
    		            i += 1;
    		        } while (numOptions >= 0);
    		    } else {
    		        System.out.println("no clue, throwing a rand");
    		        zap(rand.nextInt(3));
    		    }
		    }
		} catch (IOException e) {
		    System.out.println("inky sad");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: inky port");
		}
		int secure_port=Integer.parseInt(args[0]);

		inky client=new inky(secure_port);
		client.playGame();
	}

}
