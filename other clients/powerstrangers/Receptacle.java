import java.util.Random;

class Receptacle extends Client{@Override protected void resultHandler(Result result){}protected void throwRequestHandler(){Random r=new Random();try{switch(r.nextInt(3)){case 0:this.throwRock();break;case 1:this.throwPaper();break;case 2:this.throwScissors();break;}}catch(Exception ex){}}public Receptacle(int securePort){super(securePort);this.name="Receptacle";}public static void main(String[] args){int secure_port=Integer.parseInt(args[0]);Receptacle client=new Receptacle(secure_port);client.playGame();}}