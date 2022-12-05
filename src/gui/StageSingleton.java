package gui;

import javafx.stage.Stage;

public class StageSingleton {
//stage singletone class, implements the Singletone design pattern - working with only one stage as main stage.

	private static StageSingleton single_instance = null;
	private Stage primaryStage;

	private StageSingleton() {
	}

	public static StageSingleton getInstance() {
		// if stage created - return it, else - create new stage.
		if (single_instance == null)
			single_instance = new StageSingleton();

		return single_instance;
	}

//	    switch between stages.
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

//getStage - returns current stage instance
	public Stage getStage() {
		return this.primaryStage;
	}

}
