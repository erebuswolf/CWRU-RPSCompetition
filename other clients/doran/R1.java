// Gary Doran
public class R1 extends AbstractRandomThrower {

	public R1(int securePort) {
		super(securePort);
		name = "r1";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		Client client = new R1(Integer.parseInt(args[0]));
		client.playGame();
	}

}
