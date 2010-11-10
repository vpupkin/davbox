package cc.co.llabor.dav.cache;

import java.util.Date;
import java.util.Set;

import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  10.11.2010::20:23:32<br> 
 */
public class KeySetObject extends StoredObject {

	private Set keys;

	public KeySetObject(Set keysTmp) {
		this.keys = keysTmp; 
		this.setNullResource(false) ;
		this.setFolder(true);
		
		this.setCreationDate(new Date());
		this.setLastModified(new Date());
		
	}

}


 