public abstract class AbstractRandomThrower extends Client {

	public AbstractRandomThrower(int securePort) {
		super(securePort);
	}

	@Override
	protected synchronized void resultHandler(Result result) {}

	@Override
	protected synchronized void throwRequestHandler() {
		if(opponent_name.matches("r\\d") && opponent_name.compareTo(name) > 0) {
			return;
		}
		try{
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
		}catch (Throwable t){}
	}

}
