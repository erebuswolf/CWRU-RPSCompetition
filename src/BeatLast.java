import java.io.IOException;

public class BeatLast extends Client {

	RPSThrow last=RPSThrow.garbage;
	public BeatLast(int secure_port) {
		super(secure_port);
		this.name="BeatLast";
	}

	@Override
	protected void throwRequestHandler() {
		try {
			switch(last){
			case rock:
				this.throwPaper();
				break;
			case paper:
				this.throwScissors();
				break;
			case scissors:
				this.throwRock();
				break;
			case garbage:
				int pick=(int) (Math.random()*3);
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
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void resultHandler(Result result) {
		last=result.theyThrew;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		BeatLast client=new BeatLast(secure_port);
		client.playGame();
	}

}
