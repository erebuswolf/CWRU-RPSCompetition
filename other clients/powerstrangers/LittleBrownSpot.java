import java.io.IOException;
import java.util.HashMap;
import java.util.Random;


public class LittleBrownSpot extends Client {

	private int histogram[] = new int[4]; 
	private long num_throws = 0;
	private static Random r = new Random(System.currentTimeMillis());
	
	@Override
	protected synchronized void resultHandler(Result result) {
		switch(result.theyThrew){
		case rock:
			histogram[0]++;
			break;
		case paper:
			histogram[1]++;
			break;
		case scissors:
			histogram[2]++;
			break;
		default:
			histogram[3]++;
			break;
		}
		num_throws++;
	}

	@Override
	protected synchronized void throwRequestHandler() {
		long start = System.currentTimeMillis();
		double d = r.nextDouble();
		try{
			
			if(num_throws == 0) this.throwRock();
			else{
				double r_c = (double)histogram[0] / num_throws;
				double p_c = (double)histogram[1] / num_throws;
				double s_c = (double)histogram[2] / num_throws;
			
			
				if(d < r_c){
					this.throwPaper();
				}
				else if(d < r_c + p_c){
					this.throwScissors();
				}
				else if(d < r_c + p_c + s_c){
					this.throwRock();
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		long end = System.currentTimeMillis();
		
		System.out.println("Played in " + (end - start) + " ms.");
	}
	
	public LittleBrownSpot(int securePort){
		super(securePort);
		this.name="Poop";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		LittleBrownSpot client=new LittleBrownSpot(secure_port);
		client.playGame();
	}

}
