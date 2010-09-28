import java.io.Serializable;
import java.util.ArrayList;

public class MarkovHistories implements Serializable{

	public ArrayList<MarkovHistory> enemyList;
	
	public MarkovHistories(){
		this.enemyList = new ArrayList<MarkovHistory>(1);	
	}
	
	public MarkovHistories(ArrayList<MarkovHistory> inList){
		this.enemyList = inList;
	}
	
}