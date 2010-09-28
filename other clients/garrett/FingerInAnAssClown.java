import java.io.IOException;
import java.util.Random;

public class FingerInAnAssClown extends Client {

   private int        round_num    = 0;
   private RPSThrow[] their_throws = new RPSThrow[1000];
   private RPSThrow[] my_throws    = new RPSThrow[1000];
   private int        strategy     = 0;
   private Random     rand         = new Random();

   @Override
   protected synchronized void resultHandler(Result result) {
      their_throws[round_num] = result.theyThrew;
      my_throws[round_num]    = result.iThrew;
      chooseStrategy();
      round_num++;
   }

   private void chooseStrategy()
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
      
      // Check to see if they are beating the last throw
      if ( round_num > 11 )
      {
         int beat_last_count = 0;
         // Check if the last 10 throws were "beat last"
         for ( int i=round_num; i>round_num-10; i-- )
         {
            RPSThrow their_throw       = their_throws[i];
            RPSThrow my_previous_throw = my_throws[i-1];

            // if ( getOpposite( my_previous_throw ) == RPSThrow.garbage ) { strategy = 0; return; };
            if ( their_throw == getOpposite( my_previous_throw ) )
               beat_last_count++;
         }
         if ( beat_last_count >= 9 )
         {
            strategy = 5;
            return;
         }
      }

      // If nothing else, choose beat last
      strategy = 4;
   }

   private void makeThrow()
   {
      switch ( strategy )
      {
         case 0: throwRandom();                   break;
         case 1: catchThrow( RPSThrow.scissors ); break;
         case 2: catchThrow( RPSThrow.paper );    break;
         case 3: catchThrow( RPSThrow.rock );     break;
         case 4: throwBeatLast();                 break;
         case 5: throwBeatBeatLast();             break;

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

   private void throwBeatLast()
   {
      switch ( their_throws[round_num-1] ) //Check ***
      {
         case paper:    catchThrow( RPSThrow.scissors );  break;
         case rock:     catchThrow( RPSThrow.paper    );  break;
         case scissors: catchThrow( RPSThrow.rock     );  break;
      }
   }

   private void throwBeatBeatLast()
   {
      switch ( getOpposite( my_throws[round_num-1]  ) )
      {
         case paper:    catchThrow( RPSThrow.scissors );  break;
         case rock:     catchThrow( RPSThrow.paper    );  break;
         case scissors: catchThrow( RPSThrow.rock     );  break;
      }
   }

   private RPSThrow getOpposite( RPSThrow some_throw )
   {
      switch ( some_throw )
      {
         case paper:    return RPSThrow.scissors; 
         case rock:     return RPSThrow.paper;   
         case scissors: return RPSThrow.rock;   
      }
      
      return RPSThrow.garbage;
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
      printGoatse();
      // try { makeThrow(); } catch (IOException e) { e.printStackTrace(); }
   }

   public FingerInAnAssClown(int securePort) {
      super(securePort);
      this.name="FingerInAnAssClown";
   }

   public static void main(String[] args){
      if (args.length < 1) {
         System.out.println("error useage: Client port");
      }
      int secure_port = Integer.parseInt(args[0]);
      FingerInAnAssClown client = new FingerInAnAssClown(secure_port);
      client.setUp(); //Check *** 
      client.playGame();
   }

   public void setUp()
   {
      for (int i=0; i<their_throws.length; i++)
         their_throws[i] = null;

      for (int i=0; i<my_throws.length; i++)
         my_throws[i] = null;

      printGoatse();
   }


   private void printGoatse()
   {
      System.out.println(" /     \\             \\            /    \\       ");
      System.out.println("|       |             \\          |      |      ");
      System.out.println("|       `.             |         |       :     ");
      System.out.println("`        |             |        \\|       |     ");
      System.out.println(" \\       | /       /  \\\\\\   --__ \\\\       :    ");
      System.out.println("  \\      \\/   _--~~          ~--__| \\     |    ");
      System.out.println("   \\      \\_-~                    ~-_\\    |    ");
      System.out.println("    \\_     \\        _.--------.______\\|   |    ");
      System.out.println("      \\     \\______// _ ___ _ (_(__>  \\   |    ");
      System.out.println("       \\   .  C ___)  ______ (_(____>  |  /    ");
      System.out.println("       /\\ |   C ____)/      \\ (_____>  |_/     ");
      System.out.println("      / /\\|   C_____)       |  (___>   /  \\    ");
      System.out.println("     |   (   _C_____)\\______/  // _/ /     \\   ");
      System.out.println("     |    \\  |__   \\\\_________// (__/       |  ");
      System.out.println("    | \\    \\____)   `----   --'             |  ");
      System.out.println("    |  \\_          ___\\       /_          _/ | ");
      System.out.println("   |              /    |     |  \\            | ");
      System.out.println("   |             |    /       \\  \\           | ");
      System.out.println("   |          / /    |         |  \\           |");
      System.out.println("   |         / /      \\__/\\___/    |          |");
      System.out.println("  |           /        |    |       |         |");
      System.out.println("  |          |         |    |       |         |");
   }
}
