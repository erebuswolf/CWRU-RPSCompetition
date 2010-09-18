import java.io.IOException;


public class Fibonazi extends Client {

	long fiba=1;
	long fibb=1;
	long fibswap;
	public Fibonazi(int secure_port) {
		super(secure_port);
		this.name="Fibonazi";
	}

	@Override
	protected void throwRequestHandler() {
		try {
			int temp=(int)Math.abs(fiba%3);
			switch(temp){
			case 0:
				this.throwRock();
				break;
			case 1:
				this.throwPaper();
				break;
			case 2:
				this.throwScissors();
				break;
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void resultHandler(Result result) {
		fibswap=fibb;
		fibb+=fiba;
		fiba=fibswap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: RandomThrower port");
		}
		int secure_port=Integer.parseInt(args[0]);

		Fibonazi client=new Fibonazi(secure_port);
		client.playGame();

	}

}
