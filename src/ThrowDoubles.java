import java.io.IOException;

public class ThrowDoubles extends Client {
	
	State state = State.p;
	
	@Override
	protected synchronized void resultHandler(Result result) {
		//System.out.println("Back.");
	}
	
	@Override
	protected synchronized void throwRequestHandler() {
		try {
			switch (state) {
			case p:
				this.throwPaper();
				state = State.pp;
				break;
			case pp:
				this.throwPaper();
				state = State.r;
				break;
			case r:
				this.throwRock();
				state = State.rr;
				break;
			case rr:
				this.throwRock();
				state = State.s;
				break;
			case s:
				this.throwScissors();
				state = State.ss;
				break;
			case ss:
				this.throwScissors();
				state = State.p;
				break;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ThrowDoubles(int securePort) {
		super(securePort);
		this.name="Double Thrower";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		ThrowDoubles client=new ThrowDoubles(secure_port);
		client.playGame();
	}
	
	public enum State { r, rr, p, pp, s, ss };
	
	
	
}
