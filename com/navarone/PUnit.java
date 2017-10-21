package com.navarone;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Craig
 *
 */
public class PUnit {
	
	PUnit parent;
	String url;
	List<PUnit> units;
	
	/**Constructor
	 * @param url2
	 * @param parent2
	 */
	PUnit(String url2, PUnit parent2){
		parent = parent2;
		url = url2;
		units = new ArrayList<PUnit>();		
	}
		
	
	/**Get parent, null if seed
	 * @return parent
	 */
	PUnit getParent(){
		return parent;
	}
	
}
