package cc.co.llabor.dav.cache;
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.co.llabor.cache.BinaryContent;
import cc.co.llabor.cache.Manager;
import cc.co.llabor.dav.AbstractTransactionalDaver;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheListener; 
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
public  class Cache4Dav extends AbstractTransactionalDaver implements IWebdavStore, CacheListener {

	private File file;
	Cache  store  = null;
	List<String> storeKeys = new ArrayList<String>();

	public Cache4Dav(File filePar){
		this.file = filePar; 
		this.store = Manager.getCache(this.file.getName());		
		 
	}	
	public void removeObject(ITransaction transaction, String uri) { 
		try{
			File toDel = new File( ((File)store.keySet().toArray()[0]).getCanonicalFile().getParentFile() ,uri); 
			System.out.println("<delete><file name=\'"+toDel.getAbsolutePath()+"\'/>... ");
			toDel.delete();
			System.out.println("</delete>");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) {
		String subCacheNameTmp = this.file.getName()+folderUri;
		final Cache cacheTmp = Manager.getCache(subCacheNameTmp, true);
		System.out.println("cache/dir created: {"+subCacheNameTmp+"}  -->["+cacheTmp+"]");
	}
	public void createResource(ITransaction transaction, String resourceUri) {
		store.put(resourceUri.substring(1), "@created"+System.currentTimeMillis()+"...");
	} 
	
	public long setResourceContent(ITransaction transaction,
		String resourceUri, InputStream content, String contentType,
		String characterEncoding) {
		BinaryContent toStore = new BinaryContent(content, contentType, characterEncoding );
		Object retval = store.put(resourceUri.substring(1), toStore );
  
		return retval == null?-1:retval.toString().length();
	}
 
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		final String cacheNameTmp = this.file.getName()+folderUri;
		final Cache cacheTmp = Manager.getCache(cacheNameTmp);
		final Set<String> retval = new HashSet<String>();
		cacheTmp.addListener(this);
		try{
			final Set<File> fileKeySet = cacheTmp.keySet();	
			for(File nTmp :fileKeySet){
				String nameTmp = nTmp.getName();
				retval.add(nameTmp);
			}
		}catch(UnsupportedOperationException e){
			// TODO GAE - empty list 
			e.printStackTrace();
			retval.addAll(storeKeys);
		}
		return  retval.toArray(new String[]{});
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		Object o = store.get(resourceUri.substring(1));
		InputStream retval = null;
		retval = (o  instanceof byte[]) ?	new ByteArrayInputStream((byte[])o):null;
		retval = retval == null? (o  instanceof InputStream) ? (InputStream)o:	new ByteArrayInputStream((""+o).getBytes()):retval;
		
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
		Set keysTmp = null;
		uri = (""+"").equals( uri )? "/":uri;
		final String keyTmp = uri.substring(1);
		try{
			if ("/".equals(uri)){
				retval = new KeySetObject(keysTmp);
				return retval;
			}
			Object valTmp = store.get(keyTmp);store.getCacheEntry(keyTmp);
			if (valTmp != null){
				retval = new StoredObject();
				retval.setFolder(false);
				int lenTmp = 0;
				if (valTmp instanceof byte[]) {
					lenTmp = ((byte[])valTmp).length;
				}else  if (valTmp instanceof String) {
					lenTmp = ((String)valTmp).length();
				}else{
					System.out.println(valTmp);
				}
				retval.setResourceLength(lenTmp); 
				retval.setLastModified(new Date());
				retval.setCreationDate( new Date());
				return retval;
			}
			
			String cacheNameTmp = this.file.getName()+uri;
			cacheNameTmp = cacheNameTmp.endsWith("/")? cacheNameTmp .substring(0,cacheNameTmp .lastIndexOf("/")):cacheNameTmp ;
			final Cache cacheTmp = Manager.getCache(cacheNameTmp, false);
			try{
				keysTmp = cacheTmp.keySet();
			}catch(java.lang.UnsupportedOperationException e){
				e.printStackTrace();
				cacheTmp.put(keyTmp, ""+System.currentTimeMillis());
			}catch(Exception e){
				e.printStackTrace();
			}
			if (!"/".equals(uri)) { // file or DIR?
				File setBase = ((File)store.keySet().toArray()[0]).getParentFile();
				File file2checkTmp = new File(setBase,keyTmp);  
					if (file2checkTmp. isDirectory()){ 
						retval = new KeySetObject(keysTmp);// IS DIRECTORTY!
					}else if (file2checkTmp. exists()){ 
						// feel file
						retval = new StoredObject ();
						retval.setNullResource(false) ;
						retval.setFolder(false);
						retval.setCreationDate(new Date());
						retval.setLastModified(new Date());								
					}else{ // looks like new resouce have to be created
						return null;							
					}  
			} 
		}catch(NullPointerException e){
			e.printStackTrace();
			retval = null;	  // have to be created!
			return retval;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			retval = null;	 // have to be created!
			return retval; 
		}catch(java.lang.UnsupportedOperationException e){ // GAE not support LIST_of_cache
			store.put(keyTmp, ""+System.currentTimeMillis());
			storeKeys.add(keyTmp);
			retval.setNullResource(false) ;
			retval.setCreationDate(new Date());
			retval.setLastModified(new Date());			
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
	public void onClear() {
		System.out.println("onClear");
	}
	public void onEvict(Object key) {
		System.out.println("onEvict"+key);
	}
	public void onLoad(Object key) {
		System.out.println("onLoad"+key);
	}
	public void onPut(Object key) {
		System.out.println("onPut"+key);
		storeKeys.add(""+key);
	}
	public void onRemove(Object key) {
		System.out.println("onRemove"+key);
		storeKeys.remove( ""+key);
	}

}


 