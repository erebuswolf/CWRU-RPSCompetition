// Gary Doran
public class R2 extends AbstractRandomThrower {

	public R2(int securePort) {
		super(securePort);
		name = "r2";
	}
	
	public static void main(String[] args) {
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		Client client = new R2(Integer.parseInt(args[0]));
		client.playGame();
	}

}
