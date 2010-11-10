package cc.co.llabor.dav.cache;

import java.io.File;
import java.io.InputStream; 
import java.util.Date;
import java.util.Set;

import cc.co.llabor.dav.AbstractTransactionalDaver;

import net.sf.jsr107cache.Cache;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  10.11.2010::15:18:15<br> 
 */
public  class Cache4Dav extends AbstractTransactionalDaver implements IWebdavStore {

	private File file;
	Cache  store  = null;

	public Cache4Dav(File filePar){
		this.file = filePar; 
		this.store = cc.co.llabor.cache.Manager.getCache(this.file.getName());		
		 
	}	
	public void removeObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		}
	}
	public void createResource(ITransaction transaction, String resourceUri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		}
	} 
	
	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		return 0;
		}
	}
 
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		return  ( String[] )this.store.keySet().toArray(new String[]{});
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		return null;
		}
	}

	public long getResourceLength(ITransaction transaction, String path) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		return 0;
		}
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) { 
		Set keysTmp = this.store.keySet(); 
		KeySetObject retval = null;
		if (!"/".equals(uri)) {
			keysTmp = cc.co.llabor.cache.Manager.getCache(this.file.getName()+uri).keySet();
			retval = new KeySetObject(keysTmp);
			retval.setFolder(keysTmp.size()>1);
			retval.setNullResource(false) ;
			retval.setCreationDate(new Date());
			retval.setLastModified(new Date());			
		}else{
			retval = new KeySetObject(keysTmp);
			retval.setFolder(keysTmp.size()>1);
			retval.setNullResource(false) ;
			retval.setCreationDate(new Date());
			retval.setLastModified(new Date());
		}
		
		return retval; 
	}


	@Override
	protected void tearDown() {
		// TODO Auto-generated method stub
		 
	}

}


 