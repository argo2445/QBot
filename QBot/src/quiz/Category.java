package quiz;

public class Category {
	private int databaseID;
	private String description;

	public Category(int databaseID, String description) {
		this.databaseID = databaseID;
		this.description = description;
	}

	/**
	 * @return the databaseID
	 */
	public int getDatabaseID() {
		return databaseID;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}
