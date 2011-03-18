package cc.co.llabor.davbox.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.co.llabor.dav.cache.Cache4Dav;
 
/** 
 * <b>Description: Threa to process exitVM( ..) </b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  20.09.2010::19:44:55<br> 
 */
public class KerberThread extends Thread {
	static final Logger log = LoggerFactory.getLogger(Cache4Dav.class);
	
	public void run() { 
		letMeOut();
	}

	public void letMeOut() {
		//for ex. database.close(); 
		log.info( "<!--------- KerberThread ------------/>"); 
	}
}


 