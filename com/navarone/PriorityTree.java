package com.navarone;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * @author Craig
 *
 */
public class PriorityTree {


	PUnit seed;
	int size;
	int maxSize;
	int tempsize;
	private volatile boolean flip;

	/**Constructor
	 * @param maxSize
	 * @param seed2
	 */
	PriorityTree(int maxSize2, String seed2){
		seed = new PUnit(seed2, null);
		maxSize = maxSize2;
		size = 1;
	}


	/**Returns true if put succeeds, false otherwise.
	 * @param url, childurl
	 * @return
	 */
	public synchronized boolean put(String parentUrl, String childUrl){
		if(size == maxSize)
			return false;
		flip = false;
		putRecurse(seed, parentUrl, childUrl);
		//If parent not found, add to seed
		if(!flip)
			seed.units.add(new PUnit(childUrl,seed));
		size++;
		return true;
	}

	/**Private recursive put method
	 * @param unit
	 * @param parentUrl
	 * @param childUrl
	 */
	private synchronized void putRecurse(PUnit unit, String parentUrl, String childUrl){
		if(!flip){
			if(unit.url.equalsIgnoreCase(parentUrl)){
				unit.units.add(new PUnit(childUrl, unit));
				flip = true;
				return;
			}
			else
				for(int i=0;i<unit.units.size() && !flip; i++)
					putRecurse(unit.units.get(i), parentUrl, childUrl);
		}
	}

	/**Get function
	 * @param bfs
	 * @return
	 */
	public synchronized String get(boolean bfs){
		String url = null;
		if(bfs)
			url = getBFS(seed);
		else{
			tempsize = size;
			url = getDFS(seed);
		}
		if(url == null && seed.units.size()>0){
			PUnit p = seed.units.get(0);
			seed.units.addAll(p.units);
			seed.units.remove(p);
			return p.url;
		}			
		return null;
	}

	/**Returns URL if tree non-empty,
	 * null otherwise.
	 * Uses DFS
	 * @param PUnit p
	 * @return
	 */
	private synchronized String getDFS(PUnit p){
		String url2 = null;
		if(tempsize == 1 && p.parent != null){
			PUnit parent = p.getParent();
			parent.units.addAll(p.units);
			parent.units.remove(p);
			return p.url;
		}else
			for(int i =0; i<p.units.size(); i++){
				tempsize--;
				getDFS(p.units.get(i));
			}
		return url2;
	}

	/**Returns URL if tree non-empty,
	 * null otherwise.
	 * Uses BFS
	 * @param punit p
	 * @return
	 */
	public synchronized String getBFS(PUnit p){		
		Queue<PUnit> queue = new ArrayBlockingQueue<PUnit>(size);
		queue.add(p);
		int mysize = size;
		while(!queue.isEmpty()){
			p = queue.poll();
			mysize--;
			//Traverse entire tree BFS
			if(mysize == 1){	
				PUnit parent = p.getParent();
				parent.units.addAll(p.units);
				parent.units.remove(p);
				return p.url;				
			}						
			for(int i=0; i< p.units.size();i++)
				queue.add(p.units.get(i));
		}		
		return null;
	}


	/**Returns true if Tree full
	 * @return
	 */
	public synchronized boolean isFull(){
		return size == maxSize;
	}

	/**Get seed element
	 * @return seed
	 */
	public PUnit getSeed(){
		return seed;
	}


	/**Get size
	 * @return size
	 */
	public int getSize() {
		return size;
	}
}
