import java.io.*;
import java.net.*;

/**
 * 
 * @author Jesse Fish
 *
 */
public abstract class Client {

	/*** only values AI contestants should worry about ***/
	
	///unique name for your client
	protected String name="Example Client";
	
	///name of your opponent, you will get this from the server
	protected String opponent_name;
	
	/*** simple methods to abstract throwing rock paper or scissors ***/
	protected void throwRock() throws IOException{
		DatagramPacket rock = new DatagramPacket(new byte[] {info.rock,info.client_signature},2,group, multicast_port);
		s.send(rock);
	}
	protected void throwPaper() throws IOException{
		DatagramPacket paper = new DatagramPacket(new byte[] {info.paper,info.client_signature},2,group, multicast_port);
		s.send(paper);
	}
	protected void throwScissors() throws IOException{
		DatagramPacket scissors = new DatagramPacket(new byte[] {info.scissors,info.client_signature},2,group, multicast_port);
		s.send(scissors);
	}

	/// abstract method called when the server requests a throw from the client
	protected abstract void throwRequestHandler();

	///abstract method called when the server gives back a result
	protected abstract void resultHandler(Result result);
	
	
	///thread object to spawn throwRequestHandler method on a new thread
	private class ThrowSpawner extends Thread{
		public ThrowSpawner(){}
		public void run() {
			throwRequestHandler();
		}
	}
	
	///thread object to spawn resultHandler method on a new thread
	private class ResultSpawner extends Thread{
		Result result;
		public ResultSpawner(Result result){
			this.result=result;
		}
		public void run() {
			resultHandler(result);
		}
	}
	
	///protected result class to store the result of a previous throw
	protected class Result{
		public RPSThrow iThrew;
		public RPSThrow theyThrew;
		public Result(){
			
		}
		public Result(RPSThrow iThrew, RPSThrow theyThrew){
			this.iThrew=iThrew;
			this.theyThrew=theyThrew;
		}
	}
	
	
	
	/*** Everything Further down AI people should be able to ignore ***/
	
	
	
	private ClientInfo info=new ClientInfo();
	private int secure_port;
	private String broadcast_ip;
	private int multicast_port;
	private InetAddress group=null;
	private MulticastSocket s=null;
	
	///flag for when the game ends
	private boolean gameON=true;
	
	public Client(int secure_port){
		this.secure_port=secure_port;
	}

	protected void getSecureInfo(){
		Socket secure_socket = null;
		OutputStream out = null;
		InputStream in = null;
		/*** set up secure socket***/
		try {
			secure_socket = new Socket("127.0.0.1", secure_port);
			out = secure_socket.getOutputStream();
			in = secure_socket.getInputStream();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: local host.");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: local host.");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			// send name and receive server info
			//System.out.println("my own name length is "+ this.name.length());
			
			out.write(Math.min(this.name.getBytes().length,255));
			out.write(this.name.getBytes(), 0, Math.min(this.name.getBytes().length,255));
			//get the opponents name from the server
			
			int name_bytes_length=Network.getByte(in);

			//System.out.println("name len "+name_bytes_length);
			byte [] name_bytes=Network.getBytesOfLength(in, name_bytes_length);
			this.opponent_name=new String(name_bytes);
			//store our name in our own client info
			info.name=this.name;

			//read broadcast IP

			int broadcast_bytes_length=Network.getByte(in);
			broadcast_ip=new String(Network.getBytesOfLength(in, broadcast_bytes_length));	
			//read broadcast port
			System.out.println("broadcast "+broadcast_ip);
			byte[] portIntbytes=Network.getBytesOfLength(in, 4);
			multicast_port=Network.byteArrayToInt(portIntbytes);
			System.out.println("port is "+this.multicast_port);
			
			byte [] infoBytes=Network.getBytesOfLength(in, ClientInfo.clientInfoSize);
			info.buildFromBytes(infoBytes);
			info.print();
			
			System.out.println();
			System.out.println("My oppenent is "+opponent_name);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*** close all sockets***/
		try {
			out.close();
			in.close();
			secure_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void connectToMulticast(){
		/**
		 * *****************Connect to multicast group********************
		 * 
		 */
		try {
			group = InetAddress.getByName(broadcast_ip);
			s = new MulticastSocket(multicast_port);
			s.joinGroup(group);
			System.out.println("joined multicast group");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void shutdown(){
		gameON=false;
		s.close();
		System.out.println("game over, shutting down");
	}
	
	
	public void playGame(){
		//get initial stuff out of the way
		this.getSecureInfo();
		this.connectToMulticast();
		
		//actual play game method stuff
		byte[] buf = new byte[4];
		while(gameON){
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			try {
				s.receive(recv);
				
				int data_len=recv.getLength();
				if(data_len==2){
					//possible request or shutdown
					if(recv.getData()[0]==info.requestSignal[0]&& recv.getData()[1]==info.requestSignal[1]){
						//handle request signal
					//	System.out.println("got a request!");
						ThrowSpawner throwSpawner=new ThrowSpawner();
						throwSpawner.start();
					}
					else if(recv.getData()[0]==info.shutdownSignal[0]&& recv.getData()[1]==info.shutdownSignal[1]){
						//handle shutdown signal
						shutdown();
					}
				}
				else if(data_len==3){
					//possible result
					if(recv.getData()[2]==info.resultSignature){
						try {
							Result result=processResult(recv.getData());

							//spawn result thread
							ResultSpawner resultSpawner=new ResultSpawner(result);
							resultSpawner.start();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Result processResult(byte [] resultPacket) throws Exception{
		Result result=new Result();
		if(resultPacket[0]==info.garbage){
			result.iThrew=RPSThrow.garbage;
		}
		else if(resultPacket[0]==info.rock){
			result.iThrew=RPSThrow.rock;
		}else if(resultPacket[0]==info.paper){
			result.iThrew=RPSThrow.paper;
		}else if(resultPacket[0]==info.scissors){
			result.iThrew=RPSThrow.scissors;
		}else{
			System.out.println("BAD RESULT PACKET");
			throw new Exception("Bad result packet");
		}
		
		if(resultPacket[1]==info.garbage){
			result.theyThrew=RPSThrow.garbage;
		}
		else if(resultPacket[1]==info.rock){
			result.theyThrew=RPSThrow.rock;
		}else if(resultPacket[1]==info.paper){
			result.theyThrew=RPSThrow.paper;
		}else if(resultPacket[1]==info.scissors){
			result.theyThrew=RPSThrow.scissors;
		}else{
			System.out.println("BAD RESULT PACKET");
			throw new Exception("Bad result packet");
		}
		return result;
	}

}
