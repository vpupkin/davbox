package cc.co.llabor.dav.cache;
  
import java.io.File; 
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Set;
 
import cc.co.llabor.cache.FileCache;
import cc.co.llabor.cache.Manager;
import cc.co.llabor.cache.MemoryFileCache;
import cc.co.llabor.cache.MemoryFileItem;
import cc.co.llabor.cache.MemoryFileItemFactory;
import cc.co.llabor.dav.AbstractTransactionalDaver;
  
import net.sf.jsr107cache.Cache;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

/** 
 * <b>Provide Dav-Interface _generic_ org.apache.commons.fileupload.FileItem into 
 * virtual Cache-backended DirStructure </b>
 * 
 * possible backend are GAE-memcache, jsr-170 spec, e.t.c.
 * 
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  10.11.2010::15:18:15<br> 
 */
public  class Cache4Dav extends AbstractTransactionalDaver implements IWebdavStore  {

	  
	List<String> storeKeys = new ArrayList<String>();

	MemoryFileCache memFS;
	File file ;  	
	
	public Cache4Dav(File filePar){
		this.file = filePar;
		final String name = filePar.getName();
		memFS = MemoryFileCache.getInstance(name);
 
		
	}	
	public void removeObject(ITransaction transaction, String uri) { 
		try{
			MemoryFileItem toDel = memFS.get(uri); 
			System.out.println("<delete><file name=\'"+toDel.getName()+"\'/>... ");
			toDel.delete();
			Object retval = memFS.delete(toDel);
			System.out.println(retval+"</delete>");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) { 
		System.out.println("cache/dir created: {"+folderUri+"}  -->["+file.getAbsolutePath()+"]");
		final MemoryFileItemFactory instance = MemoryFileItemFactory.getInstance();
		String contentType = "DIR";
		boolean isFormField = false;
		String fieldName = folderUri;
		String fileName = folderUri;
		MemoryFileItem toStore = instance.createItem(fieldName, contentType, isFormField, fileName);
		try {
			memFS.mkdir(toStore);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void createResource(ITransaction transaction, String resourceUri) {
		final MemoryFileItemFactory instance = MemoryFileItemFactory.getInstance();
		String contentType = null;
		boolean isFormField = false;
		String fieldName = resourceUri;
		String fileName = resourceUri;
		MemoryFileItem toStore = instance.createItem(fieldName, contentType, isFormField, fileName);
		try {
			memFS.put(toStore);
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
			retval = memFS.get(resourceUri);
			retval.setContentType("text");//contentType
			retval.setContentType("ascii");//characterEncoding
			retval.setContentInputStream(content);	
			// reStore at the cache
			memFS.put(retval);
			return retval.getSize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return 0;

	}
 
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
 		String[] list = memFS.list(folderUri);
		return  list;
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		MemoryFileItem o;
		InputStream retval = null;
		try {
			o = memFS.get(resourceUri);
			retval = o.getInputStream(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return retval;
	}

	public long getResourceLength(ITransaction transaction, String path) {
		MemoryFileItem o;
		long retval = -1;
		try {
			o = memFS.get(path);
			retval = o.getSize(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) {  
		StoredObject retval = null;
		Set keysTmp = null;
		uri = ("").equals( uri )? "/":uri;
		//final String keyTmp = uri.substring(1);
		try{
			if ("/".equals(uri)||"/.".equals(uri) || uri.endsWith("/.")){
				retval = new KeySetObject(keysTmp);
				return retval;
			}else if (memFS.isDir(uri) || uri.endsWith("..") ){
				retval = new DavStoredObject(uri);
				retval.setNullResource(false);
				retval.setFolder(true);
				retval.setCreationDate(new Date());
				retval.setLastModified(new Date());
				
				return retval;
			}
			Object valTmp = memFS.get( uri.replace("//", "/") );
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


 