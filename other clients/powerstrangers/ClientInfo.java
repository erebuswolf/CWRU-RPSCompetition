/**
 * 
 * @author Jesse Fish
 *
 */

public class ClientInfo {
	String name;
	public byte garbage,rock,paper,scissors;
	byte [] requestSignal;
	byte client_signature;
	byte[] shutdownSignal;
	byte resultSignature;
	static final int clientInfoSize=10;
	
	//these fields are only used by the server
	RPSThrow lastThrow=RPSThrow.garbage;
	long submitTime;
	public byte getByteForThrow(RPSThrow thrown){
		if(thrown==RPSThrow.paper){
			return this.paper;
		}else if(thrown==RPSThrow.rock){
			return this.rock;
		}else if(thrown==RPSThrow.scissors){
			return this.scissors;
		}
		return this.garbage;
	}
	
	public RPSThrow getThrowForByte(byte value){
		if(value==this.paper){
			return RPSThrow.paper;
		}else if(value==this.scissors){
			return RPSThrow.scissors;
		}else if(value==this.rock){
			return RPSThrow.rock;
		}
		return RPSThrow.garbage;
	}
	
	ClientInfo(){
		requestSignal=new byte[2];
		shutdownSignal=new byte[2];
	}
	
	public void print(){
		System.out.println("name: "+name);
		System.out.println("garbage: "+garbage);
		System.out.println("rock: "+rock);
		System.out.println("paper: "+paper);
		System.out.println("scissors: "+scissors);
		System.out.println("request: "+requestSignal[0]+" "+requestSignal[1]);
		System.out.println("client_signature: "+client_signature);
		System.out.println("shutdownSignal: "+shutdownSignal[0] + " "+ shutdownSignal[1]);
		System.out.println("resultSignature: "+resultSignature);	
	}
	
	public byte[] getBytes(){
		byte[] infoBytes=new byte[10];

		infoBytes[0]=garbage;
		infoBytes[1]=rock;
		infoBytes[2]=paper;
		infoBytes[3]=scissors;
		infoBytes[4]=requestSignal[0];
		infoBytes[5]=requestSignal[1];
		infoBytes[6]=client_signature;
		infoBytes[7]=shutdownSignal[0];
		infoBytes[8]=shutdownSignal[1];
		infoBytes[9]=resultSignature;
		return infoBytes;
	}
	
	public void buildFromBytes(byte[] infoBytes){
		garbage=infoBytes[0];
		rock=infoBytes[1];
		paper=infoBytes[2];
		scissors=infoBytes[3];
		requestSignal[0]=infoBytes[4];
		requestSignal[1]=infoBytes[5];
		client_signature=infoBytes[6];
		shutdownSignal[0]=infoBytes[7];
		shutdownSignal[1]=infoBytes[8];
		resultSignature=infoBytes[9];
	}
}
