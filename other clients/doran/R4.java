// Gary Doran
public class R4 extends AbstractRandomThrower {

	public R4(int securePort) {
		super(securePort);
		name = "r4";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		Client client = new R4(Integer.parseInt(args[0]));
		client.playGame();
	}

}
