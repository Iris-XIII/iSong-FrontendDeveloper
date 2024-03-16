// -== CS400 Spring 2024 File Header Information ==-
// Name: Iris Xu
// Email: <jxu595@wisc.edu>
// Lecturer: Gary Dahl
// Notes to Grader:N/A

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * This class is a Red Black Tree implementation
 */
public class RedBlackTree<T extends Comparable<T>> extends BinarySearchTree<T> {

	protected static class RBTNode<T> extends Node<T> {
		public boolean isBlack = false;

		public RBTNode(T data) {
			super(data);
		}

		public RBTNode<T> getUp() {
			return (RBTNode<T>) this.up;
		}

		public RBTNode<T> getDownLeft() {
			return (RBTNode<T>) this.down[0];
		}

		public RBTNode<T> getDownRight() {
			return (RBTNode<T>) this.down[1];
		}
	}
	
	/**
	 * This method resolves any red property violations that are 
	 * introduced by inserting a new node into the Red-Black Tree
	 * @param node - the newly added red node
	 */
	protected void enforceRBTreePropertiesAfterInsert(RBTNode<T> node) {
		//Case 0 --- Empty tree or only one node
		if(isEmpty()) {
			root = node;
			return;
		}
		else if(node.getUp() == null) {
			return;
		}

		//get parent, set grand and aunt to null first
		RBTNode<T> parent = node.getUp();
		RBTNode<T> grand = null;
		RBTNode<T> aunt = null;
		//get grand and aunt if there they are not null
		if(parent.getUp() != null) {
			grand = parent.getUp();
			if(parent.isRightChild() && grand.getDownLeft() != null) {
				aunt = grand.getDownLeft();
			}
			else if(!parent.isRightChild() && grand.getDownRight() != null) {
				aunt = grand.getDownRight();
			}
		}
		
		//Case 1 --- Red Aunt
		if(aunt!= null && !aunt.isBlack) {
			case1(node, parent, aunt, grand);
		}
		
		//Case 2 --- Black Aunt Line
		else if((node.isRightChild() && parent.isRightChild()) ||
				(!node.isRightChild() && !parent.isRightChild())) {
			case2(node);
		}
		
		//Case 3 --- Black Aunt Zig
		else {
			case3(node, parent, grand);
		}
		
		//double check the color rule
		while(node.getUp() != null) {
			if(!node.isBlack && !node.getUp().isBlack) {
				enforceRBTreePropertiesAfterInsert(node);
				break;
			}
			node = node.getUp();
		}
	}
	
	/**
	 * helper method 1: resolves case 1: red child, red parent and red aunt
	 * swap color of parent, aunt, and grand
	 * @param child - the newly added red node
	 * @param parent - parent node of child
	 * @param aunt - aunt node of child
	 * @param grand - grant node of child
	 */
	public void case1(RBTNode<T> child, RBTNode<T> parent, RBTNode<T> aunt, RBTNode<T> grand) {
		// swap color of parent, aunt, and grand
		parent.isBlack = true;
		aunt.isBlack = true;
		grand.isBlack = false;
	}
	
	/**
	 * helper method 2: resolves case 2: red child, red parent and black aunt, child and parent are line
	 * rotate and swap colors between grand and parent
	 * @param node - the newly added red node
	 */
	public void case2(RBTNode<T> node) {
	    RBTNode<T> parent = node.getUp();
	    if (parent == null) {
	        // no parent, can't perform case 2
	        return;
	    }
	    RBTNode<T> grand = parent.getUp();
	    if (grand == null) {
	        // no grandparent, can't perform case 2
	        return;
	    }
	    
	    // rotate and swap colors between grand and parent
	    rotate(parent, grand);
	    parent.isBlack = true;
	    grand.isBlack = false;
	}
	
	/**
	 * help method 3: resolves case3: red child, red parent and black aunt, child and parent are zigzag
	 * rotate the parent and child nodes, then resolves it as case 2
	 * @param child - the newly added red node
	 * @param parent - parent node of child
	 * @param grand - grand node of child
	 */
	public void case3(RBTNode<T> child, RBTNode<T> parent, RBTNode<T> grand) {
	    if (parent == null || grand == null) {
	        // no parent or grand, can't perform case 3
	        return;
	    }

	    // rotate the parent and child nodes
	    rotate(child, parent);
	    // pass on to case 2
	    case2(parent);
	}
	
	
	/**
	 * Inserts a new data value into the tree. This tree will not hold null
	 * references, nor duplicate data values.
	 * 
	 * @param data to be added into this binary search tree
	 * @return true if the value was inserted, false if is was in the tree already
	 * @throws NullPointerException when the provided data argument is null
	 */
	@Override
	public boolean insert(T data) throws NullPointerException {
		if (data == null)
			throw new NullPointerException("Cannot insert data value null into the tree.");
		RBTNode<T> node = new RBTNode<T>(data);
		boolean insert = this.insertHelper(node);
		if (insert) {
			enforceRBTreePropertiesAfterInsert(node);
			((RedBlackTree.RBTNode<Integer>) this.root).isBlack = true;
		}
		return insert;
	}

	/**
	 * This test verifies whether root maintain black color after insertion
	 */
	@Test
	public void testRootColor() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
		// insert node
		tree.insert(13);
		tree.insert(1);
		tree.insert(19);
		tree.insert(41);
		// check whether root is black
		Assertions.assertEquals(true, ((RedBlackTree.RBTNode<Integer>) tree.root).isBlack,
				"root isn't black after insertion");
	}

	/**
	 * This test verifies Red Aunts (Case 1): insert a red child with red aunt and
	 * red parent
	 */
	@Test
	public void testRedAuntCase1() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();

		// insert node, node with a () in comment is red
		//    2
		//   / \
		// (1) (3)
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		
		// check the color of parent and aunt
		RBTNode<Integer> rootNode = (RedBlackTree.RBTNode<Integer>) tree.root;
		boolean checkParentAuntColor = !rootNode.getDownLeft().isBlack && !rootNode.getDownRight().isBlack;
		Assertions.assertEquals(true, checkParentAuntColor, "Parent and/or Aunt isn't red, error in case 2");

		// insert the child and check the color of nodes and Level Order String
		//    2
		//   / \
		//  1   3
		//       \
		//       (4)
		tree.insert(4);
		boolean checkOrder = tree.toLevelOrderString().equals("[ 2, 1, 3, 4 ]");
		boolean checkColor = rootNode.getDownLeft().isBlack && rootNode.getDownRight().isBlack
				&& !rootNode.getDownRight().getDownRight().isBlack;
		Assertions.assertEquals(true, checkOrder && checkColor, 
				"Insertion for case 1 failed, get level order string: " + tree.toLevelOrderString());
	}

	/**
	 * This test verifies Black Aunt (Case 2): insert a red child with red parent
	 * and black aunt
	 */
	@Test
	public void testBlackAuntCase2() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
		// insert node, node with a () in comment is red
		//    2
		//   / \
		//  1   3
		//       \
		//       (4)
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);

		// insert the new child, expect the following structure
		//      2
		//     / \
		//    1   4
		//       / \
		//     (3) (5)
		tree.insert(5);
		// check color of nodes and Level Order String
		RBTNode<Integer> grand = (RedBlackTree.RBTNode<Integer>) tree.root;
		RBTNode<Integer> parent = grand.getDownRight();
		RBTNode<Integer> aunt = parent.getDownLeft();
		RBTNode<Integer> child = parent.getDownRight();
		boolean checkOrder = tree.toLevelOrderString().equals("[ 2, 1, 4, 3, 5 ]");
		boolean checkColor = parent.isBlack && !aunt.isBlack && !child.isBlack;
		Assertions.assertEquals(true, checkOrder && checkColor, 
				"Insertion for case 2 failed, get level order string: " + tree.toLevelOrderString());
	}

	/**
	 * This test verifies Black Aunt (Case 3): there is a zig when inserting red
	 * child with red parent and black aunt
	 */
	@Test
	public void testBlackAuntCase3() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
		// insert node, node with a () in comment is red
		//     2
		//    / \
		//   1   4
		//        \
		//       (6)
		tree.insert(1);
		tree.insert(2);
		tree.insert(4);
		tree.insert(6);

		// insert the zig red child, expect the following structure
		//     2
		//    / \
		//   1   5
		//      / \
		//    (4) (6)
		tree.insert(5);
		// check color of nodes and Level Order String
		RBTNode<Integer> rootNode = (RedBlackTree.RBTNode<Integer>) tree.root;
		RBTNode<Integer> child = rootNode.getDownRight();
		RBTNode<Integer> parent = child.getDownRight();
		RBTNode<Integer> grand = child.getDownLeft();
		boolean checkOrder = tree.toLevelOrderString().equals("[ 2, 1, 5, 4, 6 ]");
		boolean checkColor = child.isBlack && !parent.isBlack && !grand.isBlack;
		Assertions.assertEquals(true, checkOrder && checkColor, 
				"Insertion for case 3 failed, get level order string: " + tree.toLevelOrderString());
	}

	/**
	 * This test verifies whether red node only has black child and black parent
	 * after insertion
	 */
	@Test
	public void testColorRule() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
		// insert node
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);
		tree.insert(5);
		tree.insert(6);
		tree.insert(7);
		tree.insert(8);
		
		// pick a red node, check its color
		RBTNode<Integer> redNode = ((RedBlackTree.RBTNode<Integer>) tree.root).getDownRight();
		boolean child = !redNode.isBlack;
		Assertions.assertEquals(true, child, "The picked child suppose be RED but actually BLACK");
		
		// verifies red node only have black parent and black child 
		boolean parent = redNode.getUp().isBlack;
		boolean leftChild = redNode.getDownLeft() == null || redNode.getDownLeft().isBlack;
		boolean rightChild = redNode.getDownRight() == null || redNode.getDownRight().isBlack;
		// check the color rule
		Assertions.assertEquals(true, parent && leftChild && rightChild,
				"red node has a red parent/child, color rule violated");
	}

	/**
	 * This test verifies whether the tree maintains a balance black height after
	 * insertion
	 */
	@Test
	public void testBlackHeight() {
		// create a new tree
		RedBlackTree<Integer> tree = new RedBlackTree<Integer>();

		// insert node, node with a () in comment is red
		//     2
		//    / \
		//   1   3
		//        \
		//        (4)
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		tree.insert(4);

		// count the black height for Left subtree
		int LeftBlackHeight = 0;
		RBTNode<Integer> current = (RedBlackTree.RBTNode<Integer>) tree.root;
		while (current != null) {
			if (current.isBlack)
				LeftBlackHeight++;
			current = current.getDownLeft();
		}

		// count the black height for Left subtree
		int RightBlackHeight = 0;
		current = ((RedBlackTree.RBTNode<Integer>) tree.root);
		while (current != null) {
			if (current.isBlack)
				RightBlackHeight++;
			current = current.getDownRight();
		}
		// check balance of black height
		Assertions.assertEquals(2, LeftBlackHeight,
				"Left black Heigh: " + LeftBlackHeight + " not equals to balanced black heigh 2");
		Assertions.assertEquals(2, RightBlackHeight,
				"Right black Heigh: " + RightBlackHeight + " not equals to balanced black heigh 2");
	}
}
