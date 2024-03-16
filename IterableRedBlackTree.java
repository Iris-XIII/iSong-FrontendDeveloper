import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class IterableRedBlackTree<T extends Comparable<T>>
    extends RedBlackTree<T> implements IterableSortedCollection<T> {

	private Comparable<T> startPoint = new Comparable<T>() {
        public int compareTo(T obj) {
            return -1;
        }
    };//private instance of startPoint
	
	/**
	 * Default start point set to a value that is earlier/smaller than any value 
	 * in any possible IterableRedBlackTree. Define this object to contain a 
	 * compareTo method that always returns -1 independent of what it is being 
	 * compared to. If null is ever passed to the setIterationStartPoint method 
	 * as an argument, then this instance field should return to being a value 
	 * that is always smaller than any possible value in the tree.
	 * 
	 * @param startPoint the starting point assigned
	 */
    public void setIterationStartPoint(Comparable<T> startPoint) {
    		this.startPoint = startPoint;
    }
    
    /**
     * Instantiate and return a new RBTIterator object
     */
    public Iterator<T> iterator() { 
    	return new RBTIterator(root, this.startPoint); 
    }
    
    private static class RBTIterator<R> implements Iterator<R> {
    
    private Comparable<R> startPoint;
    //This stack will be used by the iterator to help track the parts of the 
    //tree that are left to iterate through in future calls of the iterator's next method.
    private Stack<Node<R>> stack;
    
    /**
     * Construct a RBTIterator
     * @param root - root of the empty stack of Node<T>
     * @param startPoint - the indicated starting point of iteration
     */
	public RBTIterator(Node<R> root, Comparable<R> startPoint) {
		this.startPoint = startPoint;
		stack = new Stack<>();
		buildStackHelper(root); //call the builStackHelper to build the stack
		
	}
	
	/**
	 * This method:
	 * 
	 * 1) find the next data value stored in a tree (or subtree) that is 
	 * bigger than or equal to the specified start point
	 * 
	 * 2) to build up the stack of ancestor nodes that are larger than or 
	 * equal to the start point, so that those nodes' data can be visited in the future.
	 * 
	 * @param node - node pass in to build the stack
	 */
	private void buildStackHelper(Node<R> node) {
		//base case: null reference is passed as the node argument
		if(node == null) {
			return;
		}
		
		//When the data within the node argument < the start value,
		//Recursively call on the node's right subtree (find value > start point)
		if(((Comparable<R>) startPoint).compareTo(node.data) > 0) {
			buildStackHelper(node.down[1]);
		}
		//When data of node >= start value
		//Recursive call continue to build the stack through this node's left sub-tree
		else {
			stack.push(node);//data push to stack
			buildStackHelper(node.down[0]);
		}
	}
	
	/**
	 * check whether stack has next value
	 */
	public boolean hasNext() { 
		return !stack.isEmpty();
	}
	
	/**
	 * if stack empty, throw exception
	 * else pop the next node in the stack and continue build with stack
	 */
	public R next() { 
		if(stack.isEmpty() || !hasNext()) {
			throw new NoSuchElementException("Can't call next() cuz Stack is empty.");
		}
		Node<R> nextNode = stack.pop();
		buildStackHelper(nextNode.down[1]);
		return nextNode.data;
	}
    }
    
    
    /**
	 * Performs a naive insertion into a binary search tree: adding the new node in
	 * a leaf position within the tree. After this insertion, no attempt is made to
	 * restructure or balance the tree.
	 * 
	 * @param node the new node to be inserted
	 * @return true if the value was inserted, false if is was in the tree already
	 * @throws NullPointerException when the provided node is null
	 */
    @Override
	protected boolean insertHelper(Node<T> newNode) throws NullPointerException {
		if (newNode == null)
			throw new NullPointerException("new node cannot be null");

		if (this.root == null) {
			// add first node to an empty tree
			root = newNode;
			size++;
			return true;
		} else {
			// insert into subtree
			Node<T> current = this.root;
			while (true) {
				int compare = newNode.data.compareTo(current.data);
//				if (compare == 0) {
//					return false;
//				} else 
				if (compare <= 0) {
					// insert in left subtree
					if (current.down[0] == null) {
						// empty space to insert into
						current.down[0] = newNode;
						newNode.up = current;
						this.size++;
						return true;
					} else {
						// no empty space, keep moving down the tree
						current = current.down[0];
					}
				} else {
					// insert in right subtree
					if (current.down[1] == null) {
						// empty space to insert into
						current.down[1] = newNode;
						newNode.up = current;
						this.size++;
						return true;
					} else {
						// no empty space, keep moving down the tree
						current = current.down[1];
					}
				}
			}
		}
	}
    
    
    /**
     * This test examine the situation which trees containing different kinds of 
     * Comparable data, such as Integers vs Strings
     */
    @Test
    public void testDifferentDataType() {
    	//create a tree with integers
    	IterableRedBlackTree<Integer> intTree = new IterableRedBlackTree<Integer>();
    	intTree.insert(1);
    	intTree.insert(5);
    	intTree.insert(2);
    	//check size
    	Assertions.assertEquals(3, intTree.size());
    	//iterate through the tree
    	Iterator<Integer> intIt = intTree.iterator();
    	int[] expected = { 1, 2, 5};
    	int i = 0;
    	while (intIt.hasNext()) {
    		Assertions.assertEquals(expected[i], intIt.next());
    		i++;
    	}
  	
    	//create a tree with Strings
    	IterableRedBlackTree<String> stringTree = new IterableRedBlackTree<String>();
    	stringTree.insert("Hellow");
    	stringTree.insert("Monday and Tues");
    	stringTree.insert("flower's");
    	//check size
    	Assertions.assertEquals(3, stringTree.size());
    	//iterate through the tree
    	Iterator<String> stringIt = stringTree.iterator();
    	String[] expected2 = { "Hellow", "Monday and Tues", "flower's"};
    	int j = 0;
    	while (stringIt.hasNext()) {
    		Assertions.assertEquals(expected2[j], stringIt.next());
    		j++;
    	}
    }
    
    /**
     * This test examine the situation which some trees containing duplicate values, 
     * and others that do not 
     */
    @Test
    public void testDuplicate() {
    	//create a tree with integers
    	IterableRedBlackTree<Integer> intTree = new IterableRedBlackTree<Integer>();
    	//insert duplicate values
    	intTree.insert(1);
    	intTree.insert(1);
    	intTree.insert(2);
    	intTree.insert(1);
    	intTree.insert(2);
    	//check size
    	Assertions.assertEquals(5, intTree.size());
    	//iterate through the tree
    	Iterator<Integer> intIt = intTree.iterator();
    	int[] expected = { 1, 1, 1, 2, 2};
    	int i = 0;
    	while (intIt.hasNext()) {
    		Assertions.assertEquals(expected[i], intIt.next());
    		i++;
    	}
    }
    
    /**
     * This test examine the situation which some iterators created with a specified 
     * starting point and others without the specific starting point
     */
    @Test
    public void testStartingPoint() {
    	//create a tree with integers
    	IterableRedBlackTree<Integer> intTree = new IterableRedBlackTree<Integer>();
    	//insert duplicate values
    	intTree.insert(1);
    	intTree.insert(3);
    	intTree.insert(2);
    	intTree.insert(5);
    	intTree.insert(6);
    	//check size
    	Assertions.assertEquals(5, intTree.size());
    	//iterate through the tree from start point 5
    	intTree.setIterationStartPoint(5);
    	Iterator<Integer> intIt = intTree.iterator();
    	int[] expected = { 5, 6};
    	int i = 0;
    	while (intIt.hasNext()) {
    		Assertions.assertEquals(expected[i], intIt.next());
    		i++;
    	}
    }
}
