 
package cc.co.llabor.davbox.watchdog; 

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.co.llabor.jdo.RRD_JDOHelper;

 
 
  
public class InitService extends HttpServlet  {

	public static final String SYSTEM_EXIT_RESETED =   ExitTrappedException.isReseted()? "YES":"no";
	
	public void destroy() {
		try{
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
		}catch(Throwable e){
			log.error("destroy {1} throwed {2}", this.getClass(), e);
		}
		
	}

	protected void finalize() throws Throwable {
		ExitTrappedException.rollbackSystemExitCall();
	}

	/**
	 * @author vipup
	 */
	private static final long serialVersionUID = 1233581542793591103L;
	static final Logger log = LoggerFactory.getLogger(InitService.class);

	public void init() throws ServletException {
		ExitTrappedException.forbidSystemExitCall() ;
		log .info( "Persistence init...");
		try{
			Object pm = //RRD_JDOHelper.getInstance().getPMF();
				"PM init ignored... ";
			log .info( "done! "+pm);
		}catch (Exception e) {
			log.error("error.", e);
		}
		
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log .info( "Persistence init...");
		try{
			Object pm = RRD_JDOHelper.getInstance().getPMF();
				//"PM init ignored... ";
			log .info( "done! "+pm);
		}catch (Exception e) {
			log.error("error.", e);
		}
	}
 
}
