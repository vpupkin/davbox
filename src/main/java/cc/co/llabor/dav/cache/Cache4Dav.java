package cc.co.llabor.dav.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream; 
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cc.co.llabor.dav.AbstractTransactionalDaver;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheManager;
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
		String subCacheNameTmp = this.file.getName()+folderUri;
		final Cache cacheTmp = cc.co.llabor.cache.Manager.getCache(subCacheNameTmp, true);
		System.out.println("cache/dir created: {"+subCacheNameTmp+"}  -->["+cacheTmp+"]");
	}
	public void createResource(ITransaction transaction, String resourceUri) {
		store.put(resourceUri.substring(1), "@created"+System.currentTimeMillis()+"...");
	} 
	
	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		StringBuffer sb = new StringBuffer();
		try{
			int bufSize = content.available();
			bufSize = bufSize == 0? 4096:bufSize; 
			byte[] buf = new byte[bufSize];
			
			for (int readed = content.read(buf);readed>0;readed = content.read(buf)){
				String strTmp = new String(buf, 0, readed);
				sb.append(strTmp);
			}
			Object value = sb.toString() ;
			store.put(resourceUri.substring(1), value );
		}catch(IOException  e){
			e.printStackTrace();
		}
		return sb.length();
	}
 
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		final String cacheNameTmp = this.file.getName()+folderUri;
		final Cache cacheTmp = cc.co.llabor.cache.Manager.getCache(cacheNameTmp);
 
		final Set<File> fileKeySet = cacheTmp.keySet();	
		final Set<String> retval = new HashSet<String>();
		for(File nTmp :fileKeySet){
			String nameTmp = nTmp.getName();
			retval.add(nameTmp);
		}
		return  retval.toArray(new String[]{});
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		Object o = store.get(resourceUri.substring(1));
		InputStream retval = null;
		retval = o  instanceof InputStream ? (InputStream)o:	new ByteArrayInputStream((""+o).getBytes());
		return retval;
	}

	public long getResourceLength(ITransaction transaction, String path) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 10.11.2010");
		else {
		return 0;
		}
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) {  
		StoredObject retval = null;
		uri = (""+"").equals( uri )? "/":uri;
		try{
			Object valTmp = store.get(uri.substring(1));
			if (valTmp != null){
				retval = new StoredObject();
				retval.setFolder(false);
				retval.setResourceLength(111);
				retval.setLastModified(new Date());
				retval.setCreationDate( new Date());
				return retval;
			}
			Set keysTmp = null;
			String cacheNameTmp = this.file.getName()+uri;
			cacheNameTmp = cacheNameTmp.endsWith("/")? cacheNameTmp .substring(0,cacheNameTmp .lastIndexOf("/")):cacheNameTmp ;
			final Cache cacheTmp = cc.co.llabor.cache.Manager.getCache(cacheNameTmp, false);
			try{
				keysTmp = cacheTmp.keySet();
			}catch(Exception e){e.printStackTrace();}
			if (!"/".equals(uri)) { // file or DIR?
				File setBase = ((File)store.keySet().toArray()[0]).getParentFile();
				File file2checkTmp = new File(setBase,uri.substring(1));  
					if (file2checkTmp. isDirectory()){ 
						retval = new KeySetObject(keysTmp);// IS DIRECTORTY! 								
					}else{ // YAHOO! - founde the url as plain file
						retval = new StoredObject();
						retval.setFolder(false);
						String cachedO = ""+""+store.get(uri.substring(1));
						retval.setResourceLength(cachedO.length());
						retval.setNullResource(false) ;
						retval.setCreationDate(new Date());
						retval.setLastModified(new Date());								
					}  
			}else{
				retval = new KeySetObject(keysTmp);
				//retval.setFolder(true); // ROOT! - all done by KeySetObject
				//retval.setNullResource(false) ;
				//retval.setCreationDate(new Date());
				//retval.setLastModified(new Date());
			}
		}catch(NullPointerException e){
			retval = null;	  // have to be created!
			return retval;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			retval = null;					
		}catch(StringIndexOutOfBoundsException e){
			retval = null;				
		} 
		if (retval == null){ // TO NULLOBJ
			retval = new StoredObject ();
			retval.setFolder(false);
			retval.setNullResource(true) ;
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


 