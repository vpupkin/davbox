package cc.co.llabor.davbox.watchdog;
/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  20.09.2010::20:11:44<br> 
 */
final class NoWayToExitSecMan extends SecurityManager {
	SecurityManager  smOld;
	public NoWayToExitSecMan(SecurityManager smOld) {
		this.smOld = smOld;
	}

	public void checkPermission(java.security.Permission permission) {
		if ("exitVM".equals(permission.getName())) {
			ExitTrappedException.log.warning("exitVM IGNORED!");
			//throw new ExitTrappedException();					
		}else if (smOld!=null){
			smOld.checkPermission(permission);
		}
	}

	public SecurityManager getSmOld() { 
			return smOld;
	}
}

 