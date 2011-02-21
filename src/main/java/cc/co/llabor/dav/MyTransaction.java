package cc.co.llabor.dav;

import java.security.Principal;

import net.sf.webdav.ITransaction;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  07.10.2010::18:19:16<br> 
 */
public class MyTransaction implements ITransaction {

	private Principal principal;

	public MyTransaction(Principal principal) {
		this.principal = principal;
	}

	public Principal getPrincipal() { 
			return principal;
		 
	}

}


 