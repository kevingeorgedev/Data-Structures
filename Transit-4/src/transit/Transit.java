package transit;

import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered linked
 * list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	private TNode trainZero; // a reference to the zero node in the train layer

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */ 
	public Transit() { trainZero = null; }

	/* 
	 * Default constructor used by the driver and Autolab. 
	 * DO NOT use in your code.
	 * DO NOT remove from this file
	 */
	public Transit(TNode tz) { trainZero = tz; }
	
	/*
	 * Getter method for trainZero
	 *
	 * DO NOT remove from this file.
	 */
	public TNode getTrainZero () {
		return trainZero;
	}

	/**
	 * Makes a layered linked list representing the given arrays of train stations, bus
	 * stops, and walking locations. Each layer begins with a location of 0, even though
	 * the arrays don't contain the value 0. Store the zero node in the train layer in
	 * the instance variable trainZero.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops Int array listing all the bus stops
	 * @param locations Int array listing all the walking locations (always increments by 1)
	 */
	
	public void makeList(int[] trainStations, int[] busStops, int[] locations) {
		TNode walkHead = new TNode(), walkPtr = walkHead;
		for(int i = 1; i <= locations.length; ++i){
			walkPtr.setNext(new TNode(i));
			walkPtr = walkPtr.getNext();
		}
		walkPtr = walkHead;
		TNode busHead = new TNode(), busPtr = busHead;
		addLayer(busPtr, walkPtr, busStops);
		trainZero = new TNode();
		TNode trainPtr = trainZero;
		walkPtr = walkHead;
		busPtr = busHead;
		addLayer(trainPtr, busPtr, trainStations);
	}

	public void addLayer(TNode ptr1, TNode ptr2, int[] ptr1List){
		for(int i = 0; i <= ptr1List.length; ++i){
			if(ptr1.getLocation() == ptr2.getLocation())
			ptr1.setDown(ptr2);
			else if(ptr1.getLocation() > ptr2.getLocation()){
				while(ptr2 != null){
					if(ptr1.getLocation() == ptr2.getLocation()){
						ptr1.setDown(ptr2);
						break;
					}
					ptr2 = ptr2.getNext();
				}
				if(i == ptr1List.length) break;
			}
			ptr1.setNext(new TNode(ptr1List[i]));
			ptr1 = ptr1.getNext();
		}
	}
	
	/**
	 * Modifies the layered list to remove the given train station but NOT its associated
	 * bus stop or walking location. Do nothing if the train station doesn't exist
	 * 
	 * @param station The location of the train station to remove
	 */
	public void removeTrainStation(int station) {
	    TNode trainPtr = trainZero.getNext();
		TNode prev = trainZero;
		while(trainPtr != null){
			if(trainPtr.getLocation() == station){
				prev.setNext(trainPtr.getNext());
			}
			prev = trainPtr;
			trainPtr = trainPtr.getNext();
		}
	}

	/**
	 * Modifies the layered list to add a new bus stop at the specified location. Do nothing
	 * if there is no corresponding walking location.
	 * 
	 * @param busStop The location of the bus stop to add
	 */
	public void addBusStop(int busStop) {
	    TNode busPtr = trainZero.getDown();
		TNode walkNode = busPtr.getDown();

		while(busPtr.getNext() != null){
			if(busPtr.getLocation() == busStop) return;
			else if(busPtr.getNext().getLocation() > busStop) break;
			busPtr = busPtr.getNext();
		}

		if(busPtr.getNext() == null){
			busPtr.setNext(new TNode(busStop));
			busPtr = busPtr.getNext();
		} else {
			TNode tmp = busPtr.getNext();
			busPtr.setNext(new TNode(busStop, tmp, null));
			busPtr = busPtr.getNext();
		}

		while(walkNode.getNext() != null){
			if(walkNode.getLocation() == busStop) break;
			walkNode = walkNode.getNext();
		}

		if(walkNode.getLocation() != busStop){
			for(int i = walkNode.getLocation() + 1; i <= busStop; ++i){
				walkNode.setNext(new TNode(i));
				walkNode = walkNode.getNext();
			}
		}

		busPtr.setDown(walkNode);
	}
	
	/**
	 * Determines the optimal path to get to a given destination in the walking layer, and 
	 * collects all the nodes which are visited in this path into an arraylist. 
	 * 
	 * @param destination An int representing the destination
	 * @return
	 */
	public ArrayList<TNode> bestPath(int destination) {
		TNode ptr = trainZero;
		ArrayList<TNode> path = new ArrayList<TNode>();
		while(ptr != null && ptr.getLocation() <= destination){
			path.add(ptr);
			if(ptr.getLocation() == destination && ptr.getDown() == null)
				break;
			if(ptr.getNext() == null || ptr.getNext().getLocation() > destination){
				ptr = ptr.getDown();
				continue;
			}
			ptr = ptr.getNext();
		}
		if(ptr == null) return path;
		while(ptr.getDown() != null){
			path.add(ptr.getDown());
			ptr = ptr.getDown();
		}
		return path;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the same
	 * locations and connections, but every node is a NEW node.
	 * 
	 * @return A reference to the train zero node of a deep copy
	 */
	public TNode duplicate() {
		TNode ptr = trainZero.getDown().getDown();
		TNode walkHead = new TNode(), whPtr = walkHead;
		TNode busHead = new TNode(), bhPtr = busHead;
		TNode trainHead = new TNode(), thPtr = trainHead;
		
		while(ptr.getNext() != null){
			whPtr.setNext(new TNode(ptr.getNext().getLocation()));
			whPtr = whPtr.getNext();
			ptr = ptr.getNext();
		}

		ptr = trainZero.getDown();
		whPtr = walkHead;
		busHead.setDown(walkHead);
		traverseSet(ptr, bhPtr, whPtr);

		ptr = trainZero;
		bhPtr = busHead;
		trainHead.setDown(busHead);

		traverseSet(ptr, thPtr, bhPtr);
		return trainHead;
	}

	public void traverseSet(TNode ptr, TNode ptr2, TNode ptr3){
		while(ptr.getNext() != null){
			ptr2.setNext(new TNode(ptr.getNext().getLocation()));
			while(ptr3.getNext().getLocation() <= ptr2.getNext().getLocation() && ptr3 != null){
				if(ptr3.getNext().getLocation() == ptr2.getNext().getLocation()){
					ptr2.getNext().setDown(ptr3.getNext());
					break;
				}
				ptr3 = ptr3.getNext();
			}
			ptr = ptr.getNext();
			ptr2 = ptr2.getNext();
		}
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param scooterStops An int array representing where the scooter stops are located
	 */
	public void addScooter(int[] scooterStops) {
		TNode busHead = trainZero.getDown(), bhPtr = busHead;
		TNode walkHead = busHead.getDown(), whPtr = walkHead;
		TNode scootHead = new TNode(0), shPtr2 = scootHead, shPtr = shPtr2;
		scootHead.setDown(walkHead);
		busHead.setDown(scootHead);
		
		for(int i = 0; i < scooterStops.length; i++){
			scootHead.setNext(new TNode(scooterStops[i]));
			scootHead = scootHead.getNext();
		}
		
		for(int i = 0; i < scooterStops.length; i++){
			while(bhPtr != null){
				if(bhPtr.getLocation() == shPtr.getLocation()){
					bhPtr.setDown(shPtr);
					break;
				}
				shPtr = shPtr.getNext();
			}
			if(bhPtr.getNext() == null) break;
			bhPtr = bhPtr.getNext();
		}
		bhPtr = busHead;
		busHead.setDown(shPtr2);
		shPtr = busHead.getDown();
		for(int i = 0; i <= scooterStops.length; i++){
			while(shPtr != null){
				if(shPtr.getLocation() == whPtr.getLocation()){
					shPtr.setDown(whPtr);
					break;
				}
				whPtr = whPtr.getNext();
			}
			shPtr = shPtr.getNext();
		}
		shPtr2.setDown(walkHead);
		busHead.getDown().setDown(walkHead);
		busHead.setDown(shPtr2);
		trainZero.setDown(busHead);
		
	}

	/**
	 * Used by the driver to display the layered linked list. 
	 * DO NOT edit.
	 */
	public void printList() {
		// Traverse the starts of the layers, then the layers within
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// Output the location, then prepare for the arrow to the next
				StdOut.print(horizPtr.getLocation());
				if (horizPtr.getNext() == null) break;
				
				// Spacing is determined by the numbers in the walking layer
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print("--");
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print("-");
				}
				StdOut.print("->");
			}

			// Prepare for vertical lines
			if (vertPtr.getDown() == null) break;
			StdOut.println();
			
			TNode downPtr = vertPtr.getDown();
			// Reset horizPtr, and output a | under each number
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				while (downPtr.getLocation() < horizPtr.getLocation()) downPtr = downPtr.getNext();
				if (downPtr.getLocation() == horizPtr.getLocation() && horizPtr.getDown() == downPtr) StdOut.print("|");
				else StdOut.print(" ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
	
	/**
	 * Used by the driver to display best path. 
	 * DO NOT edit.
	 */
	public void printBestPath(int destination) {
		ArrayList<TNode> path = bestPath(destination);
		for (TNode vertPtr = trainZero; vertPtr != null; vertPtr = vertPtr.getDown()) {
			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the number if this node is in the path, otherwise spaces
				if (path.contains(horizPtr)) StdOut.print(horizPtr.getLocation());
				else {
					int numLen = String.valueOf(horizPtr.getLocation()).length();
					for (int i = 0; i < numLen; i++) StdOut.print(" ");
				}
				if (horizPtr.getNext() == null) break;
				
				// ONLY print the edge if both ends are in the path, otherwise spaces
				String separator = (path.contains(horizPtr) && path.contains(horizPtr.getNext())) ? ">" : " ";
				for (int i = horizPtr.getLocation()+1; i < horizPtr.getNext().getLocation(); i++) {
					StdOut.print(separator + separator);
					
					int numLen = String.valueOf(i).length();
					for (int j = 0; j < numLen; j++) StdOut.print(separator);
				}

				StdOut.print(separator + separator);
			}
			
			if (vertPtr.getDown() == null) break;
			StdOut.println();

			for (TNode horizPtr = vertPtr; horizPtr != null; horizPtr = horizPtr.getNext()) {
				// ONLY print the vertical edge if both ends are in the path, otherwise space
				StdOut.print((path.contains(horizPtr) && path.contains(horizPtr.getDown())) ? "V" : " ");
				int numLen = String.valueOf(horizPtr.getLocation()).length();
				for (int j = 0; j < numLen-1; j++) StdOut.print(" ");
				
				if (horizPtr.getNext() == null) break;
				
				for (int i = horizPtr.getLocation()+1; i <= horizPtr.getNext().getLocation(); i++) {
					StdOut.print("  ");

					if (i != horizPtr.getNext().getLocation()) {
						numLen = String.valueOf(i).length();
						for (int j = 0; j < numLen; j++) StdOut.print(" ");
					}
				}
			}
			StdOut.println();
		}
		StdOut.println();
	}
}
