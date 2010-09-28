// Gary Doran
public class R3 extends AbstractRandomThrower {

	public R3(int securePort) {
		super(securePort);
		name = "r3";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		Client client = new R3(Integer.parseInt(args[0]));
		client.playGame();
	}

}
