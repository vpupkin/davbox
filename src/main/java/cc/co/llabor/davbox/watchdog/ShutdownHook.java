package cc.co.llabor.davbox.watchdog;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * <b>Description: register Actor for exitVM-state.</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  20.09.2010::18:53:54<br> 
 */
public class ShutdownHook implements  ServletContextListener{

	static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);
	KerberThread me = null;
	public void contextDestroyed(ServletContextEvent arg0) {
		me.letMeOut();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try{
			me = new KerberThread();
			Runtime.getRuntime().addShutdownHook(me);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

}


 