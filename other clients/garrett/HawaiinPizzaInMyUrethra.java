import java.io.IOException;
import java.util.Random;

public class HawaiinPizzaInMyUrethra extends Client {

   private int        round_num    = 0;
   private RPSThrow[] their_throws = new RPSThrow[1000];
   private RPSThrow[] my_throws    = new RPSThrow[1000];
   private int        strategy     = 0;
   private Random     rand         = new Random();

   @Override
   protected synchronized void resultHandler(Result result) {
      their_throws[round_num] = result.theyThrew;
      my_throws[round_num]    = result.iThrew;
      checkConstant();
      round_num++;
   }

   private void checkConstant()
   {
      // Check to see if they are throwing constant
      int num_paper    = 0;
      int num_rock     = 0;
      int num_scissors = 0;
      for (int i=0; i<=round_num; i++)
      {
         switch ( their_throws[i] )
         {
            case paper:    num_paper++;    break;
            case rock:     num_rock++;     break; 
            case scissors: num_scissors++; break;
         }
      }
      if ( num_paper    >= round_num-1 ) { strategy = 1; return; }
      if ( num_rock     >= round_num-1 ) { strategy = 2; return; }
      if ( num_scissors >= round_num-1 ) { strategy = 3; return; }
         
      // If you are here, they are not throwing constant
      
      // Check to see if they are favoring one hand
      if ( num_paper >= round_num*.75 )
      {
         strategy = 1;
         return;
      }
      else if ( num_rock >= round_num*.75 )
      {
         strategy = 2;
         return;
      }
      else if ( num_scissors >= round_num*.75 )
      {
         strategy = 3;
         return;
      }

      strategy = 0;
   }

   private void makeThrow()
   {
      switch ( strategy )
      {
         case 0: throwStrat();                    break;
         case 1: catchThrow( RPSThrow.scissors ); break;
         case 2: catchThrow( RPSThrow.paper );    break;
         case 3: catchThrow( RPSThrow.rock );     break;
         default: throwRandom();                  break;
      }
   }

   private void throwRandom()
   {
      switch ( rand.nextInt(2) )
      {
         case 0: catchThrow( RPSThrow.rock );     break;
         case 1: catchThrow( RPSThrow.paper );    break;
         case 2: catchThrow( RPSThrow.scissors ); break;
      }
   }

   private void throwStrat()
   {
      if ( round_num <= 50 )
         catchThrow( RPSThrow.paper );
      else if ( round_num <= 150 )
         catchThrow( RPSThrow.rock  );
      else if ( round_num <= 450 )
         catchThrow( RPSThrow.scissors );
      else if ( round_num <= 650 )
         catchThrow( RPSThrow.paper );
      else
         throwRandom();
   }

   private void catchThrow( RPSThrow throw_this )
   {
      if ( throw_this == RPSThrow.paper )
      {
         try { this.throwPaper(); } catch (IOException e) { e.printStackTrace(); }
      }
      else if ( throw_this == RPSThrow.rock )
      {
         try { this.throwRock(); } catch (IOException e) { e.printStackTrace(); }
      }
      else
      {
         try { this.throwScissors(); } catch (IOException e) { e.printStackTrace(); }
      }
   }

   @Override
   protected synchronized void throwRequestHandler() {
      makeThrow();
      // try { makeThrow(); } catch (IOException e) { e.printStackTrace(); }
   }

   public HawaiinPizzaInMyUrethra(int securePort) {
      super(securePort);
      this.name="HawaiinPizzaInMyUrethra";
   }

   public static void main(String[] args){
      if (args.length < 1) {
         System.out.println("error useage: Client port");
      }
      int secure_port = Integer.parseInt(args[0]);
      HawaiinPizzaInMyUrethra client = new HawaiinPizzaInMyUrethra(secure_port);
      client.setUp(); //Check *** 
      client.playGame();
   }

   public void setUp()
   {
      for (int i=0; i<their_throws.length; i++)
         their_throws[i] = null;

      for (int i=0; i<my_throws.length; i++)
         my_throws[i] = null;
   }
}
