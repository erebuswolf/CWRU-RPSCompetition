import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Random;



public class COCKBLOCKER extends Client{

	private int histogram[] = new int[4]; 
	private long num_throws = 0;
	private static Random r = new Random(System.currentTimeMillis());
	
	@Override
	protected synchronized void resultHandler(Result result) {
		switch(result.theyThrew){
		case rock:
			histogram[0]++;
			break;
		case paper:
			histogram[1]++;
			break;
		case scissors:
			histogram[2]++;
			break;
		default:
			histogram[3]++;
			break;
		}
		num_throws++;
	}

	@Override
	protected synchronized void throwRequestHandler() {
		
		
		try{
			
			double d = r.nextDouble();
			if(num_throws == 0) this.throwPaper();
			else{
				double r_c = (double)histogram[0] / num_throws;
				double p_c = (double)histogram[1] / num_throws;
				double s_c = (double)histogram[2] / num_throws;
				
			
				if(d < r_c){
					this.throwPaper();
				}
				else if(d < r_c + p_c){
					this.throwScissors();
				}
				else if(d < r_c + p_c + s_c){
					this.throwRock();
				}
				else{
					this.throwPaper();
				}
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}

	}
	
	public COCKBLOCKER(int securePort){
		super(securePort);
		
		
		Thread a_t = new Thread(new PortFinder(securePort - 1, securePort));
		a_t.start();
		Thread b_t = new Thread(new PortFinder(securePort + 1, securePort));
		b_t.start();
		
		this.name="COCKBLOCKER";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		COCKBLOCKER client=new COCKBLOCKER(secure_port);
		client.playGame();
	}
	
	public class PortFinder implements Runnable{
		Socket s = null;
		int p;
		int o;
		public PortFinder(int port, int orig){
			p = port;
			o = orig;
		}
		
		public void run(){
			
			System.out.println(65535 - p);
			
			if(p < 65535 && p > o){
				try{
					Thread a = new Thread(new PortFinder(p + 1, o));
					a.start();
				}
				catch(Exception ex){
					return;
				}
			}
			else if(p > 0 && p < o){
				try{
					Thread b = new Thread(new PortFinder(p - 1, o));
					b.start();
				}
				catch(Exception ex){
					return;
				}
			}
			try{
				s = new Socket("localhost", p);
			}
			catch(Exception e){
				return;
			}
			
			if(s.isConnected()){
				System.out.println("Success on port " + p);
				Thread t = new Thread(new Avalanch(s));
				t.start();
			}
			//Thread t = new Thread(new Avalanch(s));
		}
	}
	
	public class Avalanch extends Client implements Runnable{
		private Socket socket;
		private int secPort;
		@Override
		protected synchronized void resultHandler(Result result) {
			//System.out.printf("Result: I threw: %-10s They threw: %-10s\n",result.iThrew.name(),result.theyThrew.name());

			//put result handling code here, not a lot to it
			//obviously we aren't handling results
		}

		@Override
		protected synchronized void throwRequestHandler() {
			try {
				int i;
				//this.throwRock();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Avalanch(Socket s) {
			super(0);
			secPort = 0;
			this.name="Problem?";
			socket = s;
		}
		
		public void run() {
			try{
				this.playGame();
			}
			catch(Exception ex){}
		}
		
		protected void getSecureInfo(){
			Socket secure_socket = null;
			OutputStream out = null;
			InputStream in = null;
			/*** set up secure socket***/
			try {
				secure_socket = socket;
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
			catch (Exception e){}

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
				int port_length=Network.getByte(in);
				byte[] portIntbytes=Network.getBytesOfLength(in, port_length);
				
				System.out.println("port bytes");
				for(int i=0;i<portIntbytes.length;i++){
					System.out.print(portIntbytes[i]+" ");
				}
				System.out.println();
				
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
	}

}


