import org.junit.jupiter.api.Test;

import java.util.Scanner;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import java.util.List;

public class FrontendDeveloperTests {

	/**
	 * This test verifies the content provided by the readFile() method
	 */
	@Test
	public void testReadFile() {
		TextUITester tester = new TextUITester("./songs.csv");
		Scanner in = new Scanner(System.in, "utf-8");
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.readFile();
		String output = tester.checkOutput().trim();
		String expected = "Enter path to csv file to load: Done reading file.";
		Assertions.assertEquals(expected, output, "Unmatch Results");
	}

	/**
	 * This test verifies the getValues() method
	 */
	@Test
	public void testGetValues() {
		TextUITester tester = new TextUITester("80 - 90");
		Scanner in = new Scanner(System.in);
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.getValues();
		String output = tester.checkOutput();
		Assertions.assertEquals(true, output.contains("Hey, Soul Sister"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("Love The Way You Lie"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("TiK ToK"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("Bad Romance"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("Just the Way You Are"), "Unmatch result");
	}

	/**
	 * This test verifies the setFilter() method
	 */
	@Test
	public void testSetFilter() {
		TextUITester tester = new TextUITester("85");
		Scanner in = new Scanner(System.in);
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.setFilter();
		String output = tester.checkOutput();
		Assertions.assertEquals(true, output.contains("Hey, Soul Sister"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("Love The Way You Lie"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("TiK ToK"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("Bad Romance"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("Just the Way You Are"), "Unmatch result");
	}

	/**
	 * This test verifies the topFive() method
	 */
	@Test
	public void testTopFive() {
		TextUITester tester = new TextUITester("");
		Scanner in = new Scanner(System.in);
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.topFive();
		String output = tester.checkOutput();
		Assertions.assertEquals(true, output.contains("Hey, Soul Sister"), "Unmatch result");
		Assertions.assertEquals(true, output.contains("Love The Way You Lie"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("TiK ToK"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("Bad Romance"), "Unmatch result");
		Assertions.assertEquals(false, output.contains("Just the Way You Are"), "Unmatch result");
	}

	/**
	 * This test verifies that readFile() method would successfully identify a
	 * invalid path
	 */
	@Test
	public void testInvalidPath() {
		TextUITester tester = new TextUITester("/here/song.csv");
		Scanner in = new Scanner(System.in);
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.readFile();
		String output = tester.checkOutput();
		Assertions.assertEquals(true, output.contains("Invalid File. Please enter a new path"), "Unmatch results");
	}

	/**
	 * This test verifies that getValues() method would successfully identify a
	 * invalid input
	 */
	@Test
	public void testInvalidInput() {
		TextUITester tester = new TextUITester("123 - 456 - 789");
		Scanner in = new Scanner(System.in);
		BackendInterface backend = new BackendPlaceholder(null);
		FrontendInterface front = new FrontendInterfaceImplement(in, backend);

		front.getValues();
		String output = tester.checkOutput();
		Assertions.assertEquals(true, output.contains("Invalid range format"), "Unmatch results");
	}

	/**
	 * This test verifies the function of back end getRange method
	 * 
	 * @throws IOException
	 */
	@Test
	public void IntegrationGetRange() {
		// set a range
		int min = 23;
		int max = 27;
		// initiate a back end object
		IterableRedBlackTree<SongInterface> tree = new IterableRedBlackTree<SongInterface>();
		BackendInterface backend = new Backend(tree);
		// read file using back end read data method
		try {
			backend.readData("songs.csv");
		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}
		List<String> actual = backend.getRange(min, max);
		String[] expected = { "You Lost Me", "Love Me Like You Do - From Fifty Shades Of Grey", "St Jude",
				"Dusk Till Dawn - Radio Edit", "Free Me" };
		// check size
		Assertions.assertEquals(expected.length, actual.size());
		// check each song selected
		for (int i = 0; i < expected.length; i++) {
			Assertions.assertEquals(expected[i], actual.get(i));
		}
	}

	/**
	 * This test verifies the function of back end filterEnergeticSongs method
	 * 
	 * @throws IOException
	 */
	@Test
	public void IntegrationSetFilter() throws IOException {
		// initiate a back end object
		IterableRedBlackTree<SongInterface> tree = new IterableRedBlackTree<SongInterface>();
		BackendInterface backend = new Backend(tree);
		// read file using back end read data method
		try {
			backend.readData("songs.csv");
		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}
		int minEnergy = 45;

		// case1: getRange() was not previously called, get an empty list.
		List<String> actual = backend.filterEnergeticSongs(minEnergy);
		Assertions.assertEquals(true, actual.isEmpty());

		// case2: getRange() was previously called, get a list of song titles by
		// danceability ascending order
		// set a range
		int min = 23;
		int max = 27;
		backend.getRange(min, max);
		List<String> actual2 = backend.filterEnergeticSongs(minEnergy);
		String[] expected = { "Love Me Like You Do - From Fifty Shades Of Grey", "Free Me" };
		// check size
		Assertions.assertEquals(expected.length, actual2.size());
		// check each song selected
		for (int i = 0; i < expected.length; i++) {
			Assertions.assertEquals(expected[i], actual2.get(i));
		}
	}

	/**
	 * This test verifies the function of back end readData method
	 * 
	 * @throws IOException
	 */
	@Test
	public void PartnerReadData() throws IOException {
		// initiate a back end object
		IterableRedBlackTree<SongInterface> tree = new IterableRedBlackTree<SongInterface>();
		BackendInterface backend = new Backend(tree);
		// read valid file using back end read data method
		try {
			backend.readData("songs.csv");
		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}
		// read invalid file using back end read data method
		try {
			backend.readData("hello/song.csv");
		} catch (IOException e) {
			e.printStackTrace();
			assert true;
		}

	}
	
	/**
	 * This test verifies the function of back end fiveFastest method
	 * 
	 * @throws IOException
	 */
	@Test
	public void PartnerFiveFastest() throws IOException {
		// initiate a back end object
		IterableRedBlackTree<SongInterface> tree = new IterableRedBlackTree<SongInterface>();
		BackendInterface backend = new Backend(tree);
		// read file using back end read data method
		try {
			backend.readData("songs.csv");
		} catch (IOException e) {
			e.printStackTrace();
			assert false;
		}
		
		//case 1: call fiveFastest before call getRange
		try {
			List<String> topFive = backend.fiveFastest();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			assert true;
		}
		//case 2: call fiveFastest after call getRange
		backend.getRange(20, 35);
		List<String> topFive = backend.fiveFastest();
		String[] expected = {
				"192: Shot Me Down (feat. Skylar Grey) - Radio Edit",
				"182: Hard",
				"181: How Far I'll Go - From Moana",
				"190: Animals",	
				"190: Love Me Like You Do - From Fifty Shades Of Grey"
		};
		// check size
		Assertions.assertEquals(expected.length, topFive.size());
		// check each song selected
		for (int i = 0; i < expected.length; i++) {
			Assertions.assertEquals(expected[i], topFive.get(i));
		}
	}
	
	
}
