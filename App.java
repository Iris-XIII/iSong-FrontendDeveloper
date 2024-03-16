import java.util.Scanner;

/**
 * Main entry point for running the iSongify app.
 */
public class App {
    public static void main(String[] args) {
	IterableRedBlackTree<SongInterface> tree = new IterableRedBlackTree<SongInterface>(); 
	BackendInterface backend = new Backend(tree);
	Scanner in = new Scanner(System.in);
	FrontendInterface frontend = new FrontendInterfaceImplement(in,backend);

	System.out.println("Welcome to iSongify");
	System.out.println("===================");
	
	frontend.runCommandLoop();

	System.out.println();
	System.out.println("===================");
	System.out.println("Thanks, and Goodbye");
    }
}
