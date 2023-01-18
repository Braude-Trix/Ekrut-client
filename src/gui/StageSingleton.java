package gui;

import javafx.stage.Stage;

/**
 * @author gal
 *stage singleton class, implements the Singleton design pattern - working with only one stage as main stage.
 */
public class StageSingleton {

	private static StageSingleton single_instance = null;
	private Stage primaryStage;

	private StageSingleton() {
	}

	/**
	 * if stage created - return it, else - create new stage.
	 * @return - instance of stage
	 */
	public static StageSingleton getInstance() {
		if (single_instance == null)
			single_instance = new StageSingleton();

		return single_instance;
	}

	/**
	 * set stage - switch between stages.
	 * @param primaryStage - set to this stage
	 */
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * getStage - returns current stage instance
	 * @return - current stage
	 */
	public Stage getStage() {
		return this.primaryStage;
	}
}
