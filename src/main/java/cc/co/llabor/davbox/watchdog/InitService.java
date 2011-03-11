 
package cc.co.llabor.davbox.watchdog; 

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.rdd.jdo.RRD_JDOHelper;
 
 
  
public class InitService extends HttpServlet  {

	public static final String SYSTEM_EXIT_RESETED =   ExitTrappedException.isReseted()? "YES":"no";
	
	public void destroy() {
		log .info( "stop persistence ...");
		PersistenceManagerFactory pmf = RRD_JDOHelper.getInstance().getPMF();
		PersistenceManager pm = pmf.getPersistenceManager();
		log .info( "..TR...");
		//pm.currentTransaction().rollback();
		log .info( "..PM...");
		pm.flush();
		log .info( "..//...");
		pm.close();
		log .info( "..PMF...");
		pmf.close();
		log .info( "compleete.");
		
	}

	protected void finalize() throws Throwable {
		ExitTrappedException.rollbackSystemExitCall();
	}

	/**
	 * @author vipup
	 */
	private static final long serialVersionUID = 1233581542793591103L;
	static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

	public void init() throws ServletException {
		ExitTrappedException.forbidSystemExitCall() ;
		log .info( "Persistence init...");
		PersistenceManagerFactory pm = RRD_JDOHelper.getInstance().getPMF();
		log .info( "done! "+pm);
	}
 
}
