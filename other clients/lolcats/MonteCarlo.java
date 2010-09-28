//team Schrodinger's lolcats

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.*;
import java.math.*;

//@SuppressWarnings("unused")

import java.io.IOException;

public class MonteCarlo extends Client {

	double NumRock = 1;
	double NumScissors = 1;
	double NumPaper = 1;
	
	@Override
	protected synchronized void resultHandler(Result result) 
	{
		//System.out.printf("Result: I threw: %-10s They threw: %-10s\n",result.iThrew.name(),result.theyThrew.name());

		//put result handling code here, not a lot to it
		//obviously we aren't handling results
		if (result.theyThrew == RPSThrow.rock)
		{
			NumPaper++;
		}
		if (result.theyThrew == RPSThrow.paper)
		{
			NumScissors++;
		}
		if (result.theyThrew == RPSThrow.scissors)
		{
			NumRock++;
		}
		
	}

	@Override
	protected synchronized void throwRequestHandler() {
		try {
			//BRING THE AVALANCH
			
	
			
			World.MakeWorld(2,2, Math.PI*NumRock/(NumRock+NumPaper+NumScissors), Math.PI*(NumPaper+NumRock)/(NumPaper+NumRock+NumScissors));
			World.DoStuff();
			int Answer = World.ICanHazAnswer();
			
			if (Answer==0)
			this.throwRock();
			
			else if (Answer==1)
			this.throwPaper();
			
			else this.throwScissors();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MonteCarlo(int securePort) {
		super(securePort);
		this.name="Sparkle Pwnies 4 ever";
	}
	
	public static void main(String[] args)
	{
		
		if(args.length<1){
			System.out.println("error useage: Client port");
		}
		int secure_port=Integer.parseInt(args[0]);
		MonteCarlo client=new MonteCarlo(secure_port);
		client.playGame();
	}
	
	public static class Ball
	{
		static public Point2D.Double Position;
		void Particle (double x, double y)
		{
			Position = new Point2D.Double(x, y);
		}
		
		static void Move()
		{
			Point2D.Double Displacement = GenerateTrialMove();
		
			Point2D.Double TempPosition = new Point2D.Double(Position.x+Displacement.x, Position.y+Displacement.y);

			if (TempPosition.x>Math.PI) TempPosition.x-=Math.PI;
			if (TempPosition.x<0) TempPosition.x+=Math.PI;
			if (TempPosition.y>0)
			{
			double U1 = Physics.GetPotential(Position);
			double U2 = Physics.GetPotential(TempPosition);
			
			U1 = U1 + Physics.Gravity(Position);
			U2 = U2 + Physics.Gravity(TempPosition);
			
			double Prob = MathyStuff.Probability(U1, U2);
			
			if (Math.random() < Prob)
			{
				Position = TempPosition;
			}
			}
		}

		public static Point2D.Double GenerateTrialMove() {
			
			// TODO Auto-generated method stub
			return new Point2D.Double((Math.random()-.5)*.5, (Math.random()-.5)*.5);
		}
	}
	
	public static class Physics
	{
		static double Gravity (Point2D.Double Position)
		{
			return Position.y;
		}
		
		static double GetPotential(Point2D.Double Position)
		{
			double U = 0;
			
			for (int i = 0; i<World.Attractors.size(); i++)
			{
				if(World.Attractors.get(i).distance(Position)<.1) return 1000000;
			}
			
			for (int i = 0; i<World.Attractors.size(); i++)
			{
				U -= .05*World.Attractors.get(i).distance(Position);
			}
			
			for (int i = 0; i<World.Repellors.size(); i++)
			{
				U += .5*World.Repellors.get(i).distance(Position);
			}
			
			U+=Position.y;
			return U;
		}
	}
	
	public static class World
	{
	
		public static double RP = 0;
		public static double PS = 0;
		
		public static void DoStuff ()
		{
			long t = System.nanoTime();
			boolean done = false;
			while (done == false)
			{
			MonteCarlo.Ball.Move();
//			System.out.print("The ball is at ");
//			System.out.print(MonteCarlo.Ball.Position.x);
//			System.out.print(",");
//			System.out.print(MonteCarlo.Ball.Position.y);
//			System.out.println();
			if (MonteCarlo.Ball.Position.y<Math.E)
			{
				done = true;
			}
			}
		t-= System.nanoTime();
		System.out.println(t);
		}
		
		public static void MakeWorld(int Attractors, int Repellors, double RP, double PS)
		{
			Ball.Position = new Point2D.Double();
			Ball.Position.x = Math.random()*Math.PI;
			Ball.Position.y = Math.random()*Math.E+Math.E*11.0;
			
			for (int i = 0; i < Attractors; i++)
			{
				World.Attractors.add(NewThingy());
			}
			
			for (int i = 0; i < Repellors; i++)
			{
				World.Repellors.add(NewThingy());
			}
		}
		
		public static Point2D.Double NewThingy()
		{
			return new Point2D.Double(Math.random() * Math.PI, Math.random() * Math.E * 10.0+Math.E);
		}
		
		public static ArrayList<Point2D.Double> Attractors = new ArrayList<Point2D.Double>();
		public static ArrayList<Point2D.Double> Repellors = new ArrayList<Point2D.Double>();
		public static int ICanHazAnswer() 
		{
			if(Ball.Position.x<RP)
			{
				return 0;
			}
			if(Ball.Position.x<PS)
			{
				return 1;
			}
			return 2;
		}
	}
	
	public static class MathyStuff
	{
		public static double KT = 1;

		public static double Probability(double U1, double U2)
		{
			return Math.exp((U1-U2)/KT);
		}
	}
	

	
}
