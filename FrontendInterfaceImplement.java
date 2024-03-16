import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.io.File;

/**
 * This class implements the methods in FronendInterface
 */
public class FrontendInterfaceImplement implements FrontendInterface {

	private BackendInterface backend;
	private Scanner in;
	private Integer min;
	private Integer max;
	private Integer energy;

	public FrontendInterfaceImplement(Scanner in, BackendInterface backend) {
		this.backend = backend;
		this.in = in;
	}

	/**
	 * Repeated gives the user an opportunity to issue new commands until they
	 * select Q to quit.
	 */
	@Override
	public void runCommandLoop() {
		String command = null;
		do {
			this.displayMainMenu();
			if (in.hasNextLine()) {
				command = in.nextLine().toUpperCase();

				switch (command) {
				case "R":
					this.readFile();
					break;
				case "G":
					this.getValues();
					break;
				case "F":
					this.setFilter();
					break;
				case "D":
					this.topFive();
					break;
				case "Q":
					break;
				default:
					System.out.println("Invaild command, please try again");
				}
			} else {
				break;
			}
		} while (command != null && !command.equals("Q"));
	}

	/**
	 * Displays the menu of command options to the user.
	 */
	@Override
	public void displayMainMenu() {
		String menu = """

				~~~ Command Menu ~~~
					[R]ead Data
					[G]et Songs by Danceability [min - max]
					[F]ilter New Songs (by Min Energy: none)
					[D]isplay Five Fastest
					[Q]uit
				Choose command:""";
		// keep min, max, and menu up to date
		if (min != null && max != null) {
			menu = menu.replace("min", min.toString()).replace("max", max.toString());
		}
		if (energy != null) {
			menu = menu.replace("none", energy.toString());
		}
		System.out.print(menu + " ");
	}

	/**
	 * This method run when user enter command "R" It takes user's input as a path
	 * to csv file and calls backend method readData() to retrieve the csv info
	 */
	@Override
	public void readFile() {
		System.out.print("Enter path to csv file to load: ");
		String path = in.nextLine();
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("Invalid File. Please enter a new path");
			return;
		}
		try {
			backend.readData(path);
			System.out.println("Done reading file.");
		} catch (IOException e) {
			System.out.println("Invalid csv path, fail to read");
		}
	}

	/**
	 * This method run when user enter command "G" It takes user's input as a min -
	 * max range for Danceability and calls backend method getRange to retrieve a
	 * song list within that range
	 */
	@Override
	public void getValues() {
		System.out.print("Enter range of values (MIN - MAX): ");
		String range = in.nextLine();
		String[] parts = range.split("-");
		// check for invalid input format
		if (parts.length != 2) {
			System.out.println("Invalid range format. Please enter a valid range in form of MIN - MAX!");
			return;
		}
		// check for invalid number format
		try {
			min = Integer.parseInt(parts[0].trim());
			max = Integer.parseInt(parts[1].trim());
		} catch (NumberFormatException e) {
			System.out.println(
					"Invalid number format. Please enter a valid integer for min and max in form of MIN - MAX!");
			return;
		}
		// check for a range follow the rule of min-max
		if (min > max) {
			System.out.println("Invalid input, min > max. Please enter valid range that MIN <= MAX!");
			return;
		}

		List<String> songs = backend.getRange(min, max);
		System.out.print(songs.size() + " songs found between " + min + " - " + max);
		// utilize minEnergy threshold if filterEnergeticSongs() called
		if (energy != null) {
			System.out.println(" with energy >= " + energy + ":");
		} else {
			System.out.println(":");
		}
		// print out the list of songs
		for (int i = 0; i < songs.size(); i++) {
			System.out.println(songs.get(i));
		}
	}

	/**
	 * This method run when user enter command "F" It takes user's input as the
	 * minimum energy and calls backend method filterEnergeticSongs to retrieve a
	 * song list based on that minimum.
	 */
	@Override
	public void setFilter() {
		System.out.print("Enter minimum energy: ");
		// handle exception for input
		try {
			energy = Integer.parseInt(in.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a valid integer for minimum energy");
			return;
		}
		List<String> songs = backend.filterEnergeticSongs(energy);
		// check whether it's an empty list
		if (!songs.isEmpty()) {
			System.out.println(
					songs.size() + " songs found between " + min + " - " + max + " with energy >= " + energy + ":");
			for (int i = 0; i < songs.size(); i++) {
				System.out.println(songs.get(i));
			}
		} else {
			System.out.println(
					"You haven't set a range by command [G] or No songs found from this range. 0 song retrieve by filter");
		}
	}

	/**
	 * This method run when user enter command "D" It takes print out five fastest
	 * song titles and their respective speeds by calling backend method fiveFastest
	 */
	@Override
	public void topFive() {
		try {
			List<String> songs = backend.fiveFastest();
			System.out.print("Top Five songs found between" + min + " - " + max);
			// utilize minEnergy threshold if filterEnergeticSongs() called
			if (energy != null) {
				System.out.println(" with energy >= " + energy + ":");
			} else {
				System.out.println(":");
			}
			// print out the list
			for (int i = 0; i < songs.size(); i++) {
				System.out.println(songs.get(i));
			}
		}
		// catch exception when getRange() not previously called
		catch (IllegalStateException e) {
			System.out.println("You haven't set a range by command [G]. Can't retireve the list of top five songs");
			return;
		}
	}
}
