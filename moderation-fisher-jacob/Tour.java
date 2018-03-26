/**
 * Jacob Fisher <jf9260@bard.edu>
 * September 29, 2017
 * CMSC 201
 * Lab 4: Traveling Salesperson
 * Collaboration Statement: I (worked alone) on this assignment
 *                          [with assistance from (Kieth O'Hara) Z].
 */
import edu.princeton.cs.algs4.*;

/**
 * In order to not use twoOpt one can dash out the line in main:
 * t.twoOpt; as well as: t.prevFix.
 */

public class Tour
{
	/**
	 * The structure that holds onto the point and points to the next
	 */ 
	private class Node 
	{
		Point item; 
		Node prev; // previous node
		Node next;
	}

	Node head;
	int p_count = 0;

	/**
	 * Creates an empty Tour
	 */
	public Tour(){}

	/**
	 * Takes in values and spits them out as-is
	 */
	public void insert(Point p)
	{
		if(head == null)
		{
			head = new Node(); 
			head.item = p;
			head.next = null;
		}
		else
		{
			Node current = head;
			Node prev = head;

			while(current.next != null)
				current=current.next;

			current.next = new Node();
			current.next.prev = current;
			current.next.item = p;
			current.next.next = null;
		}
		++p_count;
	}

	/**
	 * Insert p using nearest neighbor heuristic
	 */
	public void insertNearest(Point p)
	{   
		// if there is no head makes one and fills it
		// adds a node to return to at the end
		if(head == null)
		{
			head = new Node(); 
			Node last = new Node();
			head.item = p;
			last.item = p;
			head.next = last;
			head.prev = null;
			last.next = null;
		}
		// makes the next node after head
		else if(head.next == null)
		{
			head.next = new Node();
			head.next.prev = head;
			head.next.item = p;
			head.next.next = null;
		}
		// sorts and organizes all other input points
		else
		{
			Node closest_stop = head.next; // node for hanging onto nearest stops
			Node stop_before = head; // node for hanging onto the stop before 
			Node current = head; // primary traversing node
			Node tail = head; // one step behind current
			Node input = new Node(); // creates a node with point p and holds onto
			input.item = p;          // it until it has the best place to put it

			double small_distance = current.item.distanceTo(input.item);

			// sorts through one location at a time to see where to put 'input'
			while(current.next != null)
			{ 
				current = current.next;

				double current_distance = current.item.distanceTo(input.item);

				if(small_distance > current_distance)
				{
					small_distance = current_distance;
					closest_stop = current;
					stop_before = tail;
				} 
				tail = tail.next;
			}
			stop_before.next = input;
			input.next = closest_stop;
		}
		++p_count;
	}

	/**
	 * Insert p using smallest increase in overall avg distance heuristic
	 * Follows the same convensions as insertNearest unless stated otherwise
	 */
	public void insertSmallest(Point p)
	{
		if(head == null)
		{
			head = new Node(); 
			Node last = new Node();
			head.item = p;
			last.item = p;
			head.next = last;
			head.prev = null;
			last.next = null;
		}
		else if(head.next == null)
		{
			head.next = new Node();
			head.next.item = p;
			head.next.next = null;
		}
		else
		{
			Node smallest_inc = head.next;
			Node stop_before = head;
			Node current = head;
			Node tail = head;
			Node input = new Node();
			input.item = p;

			double d1 = head.item.distanceTo(input.item);
			double d2 = input.item.distanceTo(head.next.item);
			double d3 = head.item.distanceTo(head.next.item);

			double small_distance = distance();
			double next_distance = distance()+d1+d2-d3;

			double difference = next_distance-small_distance;
			while(current.next != null)
			{
				current = current.next;

				double dis1 = tail.item.distanceTo(input.item);
				double dis2 = input.item.distanceTo(current.item);
				double dis3 = tail.item.distanceTo(current.item);

				double current_distance = dis1+dis2+small_distance-dis3;
				double current_difference = current_distance - small_distance;

				// compares the difference instead of the smallest incrementation
				if(difference > current_difference) 
				{
					difference = current_difference;
					small_distance = current_distance;
					smallest_inc = current;
					stop_before = tail;
				} 
				tail = tail.next;
			}
			stop_before.next = input;
			input.next = smallest_inc;
		}
		++p_count;
	}

	/**
	 * Goes through list and fixes previous pointers
	 * for more complicated input methods.
	 */
	public void prevFix()
	{
		Node current = head.next;
		Node previous = head;
		while(current != null)
		{
			current.prev = previous;
			current = current.next;
			previous = previous.next;
		}
	}

	/**
	 * Two-opt is a refinement algorithm for going through the 
	 * constructed path and seeing if there are any better choices.
	 */
	public void twoOpt()
	{ 
		System.out.println("WITH TWO_OPT");
		// Improvement counter
		int imprv = 0;

		// if there is no head makes one and fills it
		// adds a node to return to at the end
		if(head == null)
		{
			System.out.println("NOTHING TO FIX\n");
			return;
		}
		// makes the next node after head
		else if(head.next == null)
		{
			System.out.println("NOTHING TO FIX\n");
			return;
		}
		// sorts and organizes all other input points
		else
		{

			Node c; // our current position
			Node n; // the next position

			do
			{
				imprv = 0;
				double best_dist = distance(); // our current distance
				for(c = head.next; c.next.next != null; c = c.next)
				{
					for(n = c.next; n.next != null; n = n.next)
					{
						swap(c, n);
						double new_dist = distance(); // post swap distance
						if(new_dist < best_dist)
						{
							best_dist = new_dist;
							++imprv;
						}
						else if(new_dist > best_dist)
						{
							swap(c, n); // swap it back
						}

					}
				}
				System.out.println("imprv = " + imprv);
			}
			while(imprv != 0);
		}
	}

	/**
	 * A helper method for TwoOpt that swaps the two methods.
	 * Unlike usual swapping methods, this method flips the 
	 * entire section from x to y.
	 *
	 * @param x The first node with which we're swapping
	 * @param y The last node with which we're swapping
	 */
	public void swap(Node x, Node y)
	{
		int dist = 1;
		Node i = x;
		while(i != y)
		{
			i = i.next;
			++dist;
		}
		dist /= 2;
		int count = 0;
		while(count < dist)
		{
			Point temp = x.item;
			x.item = y.item;
			y.item = temp;
			x = x.next;
			y = y.prev;
			++count;
		}
	}

	/**
	 * Returns the total distance of the tour
	 */
	public double distance()
	{
		double distance = 0;
		for(Node n = head; n != null; n = n.next)
		{
			double d = 0;
			if(n.next!=null)
				d = n.item.distanceTo(n.next.item);
			distance += d;
		}
		return distance;
	}

	/**
	 * Returns the number of points on the tour
	 */
	public int size()
	{ 
		return p_count;
	}

	/**
	 * Draws the tour to StdDraw
	 */
	public void show()
	{
		Node n;
		for(n = head; n != null; n = n.next)
		{
			if(n.next == null)
				System.out.println(n.item);
			else
				System.out.print(n.item + "->");
		}

		for(int i = 0; i < 10; ++i)
			System.out.println();

		Node m;
		for(m = head; m.next != null; m = m.next);
		for(; m != null; m = m.prev)
		{
			if(m.prev == null)
				System.out.println(m.item);
			else
				System.out.print(m.item + "<-");
		}

		for(n = head; n != null; n = n.next)
		{
			n.item.draw();
			if(n.next!=null)
				n.item.drawTo(n.next.item);
		}
	}

	public static void main(String[] args)
	{
		Tour t = new Tour();

		double total = 0;
		Stopwatch timer = new Stopwatch();

		int w = StdIn.readInt();
		int h = StdIn.readInt();
		StdDraw.setCanvasSize(w, h);
		StdDraw.setXscale(0, w);
		StdDraw.setYscale(0, h);
		StdDraw.setPenRadius(0.005);

		while(!StdIn.isEmpty())
		{  
			double x = StdIn.readDouble();
			double y = StdIn.readDouble();
			Point p = new Point(x, y);
			// put t.chosen_method(p) here
			//t.insert(p);
			t.insertSmallest(p);  // smallest overall avg heuristic
			//t.insertNearest(p); // nearest neighbor heuristic
		}
		t.prevFix();
		t.twoOpt();
		t.show();
		System.out.println("Amount of points: " + t.size());
		System.out.println("Total distance: " + t.distance());
		StdOut.println("elapsed time = " + timer.elapsedTime());
	}
}
