package cc.co.llabor.dav.cache;

import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  22.02.2011::18:26:04<br> 
 */
public class DavStoredObject extends StoredObject {
	String name ="";
	public DavStoredObject(String name){
		this.name= name;
	}
	public String toString(){
		String retval = "";
		retval += this.isFolder()?"DIR:":"file";
		retval += "{"+name+"}";
		retval += this.isFolder()?"["+this.getResourceLength()+"]":"[]";
		retval += this.isFolder()?"/"+this.getCreationDate()+"/":"\\"+this.getCreationDate()+"\\";
		retval += this.isFolder()?"/"+this.getLastModified()+"/":"\\"+this.getLastModified()+"\\";
		return retval;
	}

}


 