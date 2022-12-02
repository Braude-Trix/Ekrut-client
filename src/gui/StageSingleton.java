package gui;
import javafx.stage.Stage;

public class StageSingleton {


	    private static StageSingleton single_instance = null;
	    public String s;
	    private Stage primaryStage;
	    private StageSingleton(){}
	    
	    public static StageSingleton getInstance()
	    {
	        if (single_instance == null)
	            single_instance = new StageSingleton();
	  
	        return single_instance;
	    }
	    
	    public void setStage(Stage primaryStage)
	    {
	    	this.primaryStage = primaryStage;
	    }


		public Stage getStage() {
			// TODO Auto-generated method stub
			return this.primaryStage; 
		}



	  
	    
	    
	}
	  

