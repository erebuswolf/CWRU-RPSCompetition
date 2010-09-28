import java.io.IOException;
import java.util.Arrays;


public class hai extends Client {

    private int lastThrow = -1;

    private int[] counts = new int[3];
    private int[] dcounts = new int[9];

	@Override
	protected synchronized void resultHandler(Result result) {
	//	System.out.printf("Result: I threw: %-10s They threw: %-10s\n",result.iThrew.name(),result.theyThrew.name());

        int thisThrow;
        
        if(result.theyThrew.name() == "scissors"){
            thisThrow = 0;
        } else if(result.theyThrew.name() == "paper"){
            thisThrow = 1;
        } else {
            thisThrow = 2;
        }

        counts[thisThrow] = counts[thisThrow] + 1;

        int dIndex = 3*lastThrow + thisThrow;

        if(lastThrow != -1){
        	dcounts[dIndex] = dcounts[dIndex] + 1;
        }
        
		lastThrow = thisThrow;
		
		return;
	}

	@Override
	protected synchronized void throwRequestHandler() {
        if(serialTest() == 1){
        	return;
        }
        if(frequencyTest() == 1){
        	return;
        }
        returnRandom();
        return;
	}

	public hai(int securePort) {
		super(securePort);
		this.name="¿Can has cheesebürger?";
        Arrays.fill(counts,33);
        Arrays.fill(dcounts,11);
	}

	public static void main(String[] args) {

		if(args.length<1){
			System.out.println("error useage: RandomThrower port");
		}
		int secure_port=Integer.parseInt(args[0]);

		hai client=new hai(secure_port);
		client.playGame();
	}

    private int frequencyTest(){
        int sum = counts[0] + counts[1] + counts[2];
        for(int i = 0; i < 3; i++){
            double percent = (double)counts[i]/sum;
            if(percent > .40){
                throwNow(i);
                System.out.println("freq");
                return 1;
            }
        }
        return 0;
    }

    private int serialTest(){
        int sum = dcounts[0] + dcounts[1] + dcounts[2] + dcounts[3] + dcounts[4] + dcounts[5] + dcounts[6] + dcounts[7] + dcounts[8];
        for(int i = 0; i < 3; i++){ // Last value
            for(int j = 0; j < 3; j++){ // This value
                double percent = (double)dcounts[3*i+j]/sum;
                if(percent > .15 && lastThrow == i){
                    throwNow(j);
                    System.out.println("serial");
                    return 1;
                }
            }
        }
        return 0;
    }

    private int returnRandom(){
        int pick=(int) (Math.random()*3);
        System.out.println("rand");
        throwNow(pick);
        return 1;
    }

    private void throwNow(int expected){
        int pick = (expected+2)%3;
        try {
			switch(pick){
			case 0:
				this.throwScissors();
				break;
			case 1:
				this.throwPaper();
				break;
			case 2:
				this.throwRock();
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
