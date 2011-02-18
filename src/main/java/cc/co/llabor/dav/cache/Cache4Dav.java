package cc.co.llabor.dav.cache;
  
import java.io.File; 
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Set;
 
import cc.co.llabor.cache.MemoryFileCache;
import cc.co.llabor.cache.MemoryFileItem;
import cc.co.llabor.cache.MemoryFileItemFactory;
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

	  
	List<String> storeKeys = new ArrayList<String>();

	MemoryFileCache cache;
	File file ;  	
	
	public Cache4Dav(File filePar){
		this.file = filePar;
		cache = MemoryFileCache.getInstance(filePar.getName());
		cache.registerListener(this);
		
	}	
	public void removeObject(ITransaction transaction, String uri) { 
		try{
			MemoryFileItem toDel = cache.get(uri); 
			System.out.println("<delete><file name=\'"+toDel.getName()+"\'/>... ");
			toDel.delete();
			System.out.println("</delete>");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) { 
		System.out.println("cache/dir created: {"+folderUri+"}  -->["+file.getAbsolutePath()+"]");
	}
	public void createResource(ITransaction transaction, String resourceUri) {
		final MemoryFileItemFactory instance = MemoryFileItemFactory.getInstance();
		String contentType = null;
		boolean isFormField = false;
		String fieldName = resourceUri;
		String fileName = resourceUri;
		MemoryFileItem toStore = instance.createItem(fieldName, contentType, isFormField, fileName);
		try {
			cache.put(toStore);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 
	
	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) { 
		MemoryFileItem retval; 
		try {
			retval = cache.get(resourceUri);
			retval.setContentType(contentType);
			retval.setContentType(characterEncoding);
			retval.setContentInputStream(content);	
			// reStore at the cache
			cache.put(retval);
			return retval.getSize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return 0;

	}
 
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
 		return  cache.list(folderUri);
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		MemoryFileItem o;
		InputStream retval = null;
		try {
			o = cache.get(resourceUri);
			retval = o.getInputStream(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
			Object valTmp = cache.get( keyTmp );
			if (valTmp != null){
				retval = new StoredObject();
				retval.setFolder(false);
				int lenTmp = 0;
				if (valTmp instanceof byte[]) {
					lenTmp = ((byte[])valTmp).length;
				}else  if (valTmp instanceof String) {
					lenTmp = ((String)valTmp).length(); 
				}else  if (valTmp instanceof MemoryFileItem) {
					lenTmp = (int) ((MemoryFileItem)valTmp).getSize(); 
				}else{
					System.out.println(valTmp);
				}
				retval.setResourceLength(lenTmp); 
				retval.setLastModified(new Date());
				retval.setCreationDate( new Date());
				return retval;
			}
 
 
		}catch(NullPointerException e){
			e.printStackTrace();
			retval = null;	  // have to be created!
			return retval;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			retval = null;	 // have to be created!
			return retval;  		
		}catch(StringIndexOutOfBoundsException e){
			retval = null;				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		if (retval == null && !"/.".equals(uri)){ // TO NULLOBJ 
			retval = null;
		}else{
			retval = theNull;
		}
		
		return retval; 
	}
	static final StoredObject theNull =  getNull() ;
	private static StoredObject getNull() {
		StoredObject  nullTmp = new StoredObject ();
		nullTmp.setFolder(false);
		nullTmp.setNullResource(true) ;
		nullTmp.setCreationDate(new Date());
		nullTmp.setLastModified(new Date());
		return nullTmp;
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


 