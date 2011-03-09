package cc.co.llabor.davbox.watchdog; 

import java.util.logging.Logger; 
/**
 * <b>Description:TODO</b>
 * 
 * @author vipup<br>
 *         <br>
 *         <b>Copyright:</b> Copyright (c) 2006-2008 Monster AG <br>
 *         <b>Company:</b> Monster AG <br>
 * 
 * Creation: 03.09.2010::09:57:16<br>
 */
public class ExitTrappedException extends SecurityException {
	/**
	 * @author vipup
	 */
	private static final long serialVersionUID = -4173081033038672726L; 

	static final Logger log = Logger.getLogger(""+ExitTrappedException.class.getName());
	private static SecurityManager currentSM = null;
	private static boolean reseted = false;
	
	public static final boolean ENABLED = false;
	
	static {
		if (ENABLED)
		try{
			currentSM = System.getSecurityManager();
			forbidSystemExitCall(); 
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	public static void forbidSystemExitCall() {
		if (!ENABLED) return;
		currentSM = new NoWayToExitSecMan(currentSM);
		try{ 
				System.setSecurityManager(currentSM);
				reseted = true;
				log.info( "--------- exitVM WILL BE IGNORED! -------------");				
 		}catch(Throwable e){
			String msg = e.getMessage();
			log.warning(msg);
			e.printStackTrace();
		}
	}
	public static void rollbackSystemExitCall() { 
		if (!ENABLED) return;
		try{
			if (currentSM !=null) 
			if (currentSM  instanceof NoWayToExitSecMan){
				System.setSecurityManager(currentSM = ((NoWayToExitSecMan)currentSM).getSmOld());
				reseted = false;
				log.info( "--------- exitVM rolled back! -------------");	
			} 
		}catch(Throwable e){
			String msg = e.getMessage();
			log.warning(msg);
			e.printStackTrace();
		}		
	}
	public static boolean isReseted() {
			return reseted;
	}
}
