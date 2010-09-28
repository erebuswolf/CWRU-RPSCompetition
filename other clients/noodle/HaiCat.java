import java.io.IOException;

public class HaiCat extends Client {

	String Noodle;
	@Override
	protected synchronized void resultHandler(Result result) {
	}

	@Override
	protected synchronized void throwRequestHandler() {
		try {
			/* Attempt a haiku */  // Must first say equals when "=" appears
			String lol = null;
			this.throwRock(); // Pebble Throw!
			Noodle = "Mrow";
			/* End Haiku */
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HaiCat(int securePort) {
		super(securePort);
		this.name="HaiCat";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		HaiCat client=new HaiCat(secure_port);
		client.playGame();
	}
}
