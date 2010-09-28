import java.io.*;import java.net.*;
public class Headache 
{
protected String name="Headache";
		protected String opponent_name;
		protected void throwRock() throws IOException
{
				DatagramPacket rock = new DatagramPacket(new byte[] {info.rock,info.client_signature},2,group, multicast_port);
		s.send(rock);}protected void throwPaper() throws IOException{
DatagramPacket paper = new DatagramPacket(new byte[] {info.paper,info.client_signature},2,group, multicast_port);
		s.send(paper);
	}protected void throwScissors() throws IOException{DatagramPacket scissors = new DatagramPacket(new byte[] {info.scissors,info.client_signature},2,group, multicast_port);s.send(scissors);}protected synchronized void throwRequestHandler() {int pick=(int) (Math.random()*3);
			try {
switch(pick){case 0:this.throwScissors();break;case 1:	this.throwPaper();break;case 2:this.throwRock();break;
			}
} catch (IOException e) {e.printStackTrace();	}     					}
	protected void resultHandler(Result result){}private class ThrowSpawner extends Thread{
		public ThrowSpawner(){}		public void run() {
			throwRequestHandler();	}}	private class ResultSpawner extends Thread{		Result result;
		public ResultSpawner(Result result){
			this.result=result;
}
		public void run() {			resultHandler(result);
}}
	protected class Result{	public RPSThrow iThrew;	public RPSThrow theyThrew;		public Result(){
					}
		public Result(RPSThrow iThrew, RPSThrow theyThrew){			this.iThrew=iThrew;	this.theyThrew=theyThrew;
		}
}private ClientInfo info=new ClientInfo();	private int secure_port;private String broadcast_ip;
				private int multicast_port;	private InetAddress group=null;
private MulticastSocket s=null;	private boolean gameON=true;	public Headache(int secure_port){		this.secure_port=secure_port;		this.name="Headache";	}	protected void getSecureInfo(){
		Socket secure_socket = null;		OutputStream out = null;
		InputStream in = null;		try {
			secure_socket = new Socket("127.0.0.1", secure_port);			out = secure_socket.getOutputStream();
			in = secure_socket.getInputStream();		} catch (UnknownHostException e) {
						e.printStackTrace();			System.exit(1);
} 											catch (IOException e) {
			e.printStackTrace();System.exit(1);
}		try {	out.write(Math.min(this.name.getBytes().length,255));			out.write(this.name.getBytes(), 0, Math.min(this.name.getBytes().length,255));
			int name_bytes_length=Network.getByte(in);byte []name_bytes=Network.getBytesOfLength(in,name_bytes_length);this.opponent_name=new String(name_bytes);info.name=this.name;int broadcast_bytes_length=Network.getByte(in);			broadcast_ip=new String(Network.getBytesOfLength(in, broadcast_bytes_length));	
			System.out.println("broadcast "+broadcast_ip);
			int port_length=Network.getByte(in);
	byte[] portIntbytes=((Network.getBytesOfLength(in, port_length)));
		System.out.println(("port bytes"));
			for(int i=0;i<portIntbytes.length;i++){{
	System.out.print(portIntbytes[i]+" ");			}}	System.out.println();			multicast_port=Network.byteArrayToInt(portIntbytes);
			System.out.println("port is "+this.multicast_port);		byte [] infoBytes=Network.getBytesOfLength(in, ClientInfo.clientInfoSize);			info.buildFromBytes(infoBytes);			info.print();
			System.out.println();			System.out.println("My oppenent is "+opponent_name);
		} catch (IOException e1) {			// TODO Auto-generated catch block
			e1.printStackTrace();
}		try {
			out.close();in.close();
		secure_socket.close();
} catch (IOException e) {// TODO Auto-generated catch block
			e.printStackTrace();}	}
private void connectToMulticast(){
	try {			group = InetAddress.getByName(broadcast_ip);
	s = new MulticastSocket(multicast_port);			s.joinGroup(group);
	System.out.println("joined multicast group");		} catch (UnknownHostException e1) {
						e1.printStackTrace();
} catch (IOException e) {
e.printStackTrace();		}	}
private void shutdown(){
		gameON=false;		s.close();		System.out.println("game over, shutting down");
	}
	public void playGame(){		this.getSecureInfo();
this.connectToMulticast();		byte[] buf = new byte[4];
		while(gameON){
	DatagramPacket recv = new DatagramPacket(buf, buf.length);
			try {
			s.receive(recv);	
				int data_len=recv.getLength();				if(data_len==2){
if(recv.getData()[0]==info.requestSignal[0]&& recv.getData()[1]==info.requestSignal[1]){
						ThrowSpawner throwSpawner=new ThrowSpawner();	throwSpawner.start();
	}
	else if(recv.getData()[0]==info.shutdownSignal[0]&&recv.getData()[1]==info.shutdownSignal[1]){
						shutdown();	}
				}
	else if(data_len==3){
		if(recv.getData()[2]==info.resultSignature){		try {
							Result result=processResult(recv.getData());
	ResultSpawner resultSpawner=new ResultSpawner(result);
resultSpawner.start();
						} catch (Exception e) {
		e.printStackTrace();
}
}
}
} catch (IOException e) 
{
				e.printStackTrace();
}	
}
}		private Result processResult(byte [] resultPacket) throws Exception
{
		Result result=new Result();
		if(resultPacket[0]==info.garbage)
{
			result.iThrew=RPSThrow.garbage;
}
		else if(resultPacket[0]==info.rock)
{
			result.iThrew=RPSThrow.rock;
}
else if(resultPacket[0]==info.paper)
{
	result.iThrew=RPSThrow.paper;	
}else if(resultPacket[0]==info.scissors){
			result.iThrew=RPSThrow.scissors;
}else{
System.out.println("");
		throw new Exception("");
}
		if(resultPacket[1]==info.garbage){
		result.theyThrew=RPSThrow.garbage;
}
	else if(resultPacket[1]==info.rock){	result.theyThrew=RPSThrow.rock;	}else if(resultPacket[1]==info.paper){
			result.theyThrew=RPSThrow.paper;		}else if(resultPacket[1]==info.scissors){
			result.theyThrew=RPSThrow.scissors;		}else{
System.out.println("BAD RESULT PACKET");
			throw new Exception("Bad result packet");		
}
		return result;	}public static void main(String[] args) {		if(args.length<1){
			System.out.println("error useage: RandomThrower port");	
}
		int secure_port=Integer.parseInt(args[0]);
Headache client=new Headache(secure_port);
		client.playGame();
}

}
