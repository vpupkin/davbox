package cc.co.llabor.dav.cache;
  
import java.io.File; 
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import cc.co.llabor.cache.FileCache;
import cc.co.llabor.cache.Manager;
import cc.co.llabor.cache.MemoryFileCache;
import cc.co.llabor.cache.MemoryFileItem;
import cc.co.llabor.cache.MemoryFileItemFactory;
import cc.co.llabor.dav.AbstractTransactionalDaver;
import cc.co.llabor.davbox.watchdog.ShutdownHook;
  
import net.sf.jsr107cache.Cache;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.exceptions.ObjectNotFoundException;

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

	static final Logger log = LoggerFactory.getLogger(Cache4Dav.class);
	
	private static void System_out_println(Object s){
		System_out_println(""+ s);
	}
	private static void System_out_println(String s){
		log.debug(s);
	}
	
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
			if (memFS.isDir(uri)){
				memFS.delDir(uri);
			}else{
				// delete file
				
					MemoryFileItem toDel = memFS.get(uri); 
					if (toDel==null)return;
					System_out_println("<delete>\n\t<file name=\'"+toDel.getName()+"\'/>... ");
					toDel.delete();
					Object retval = memFS.delete(toDel);
					System_out_println(retval+"\n</delete>");
					return;				
			}
		}catch (Exception e) {
			// ?? dir
			e.printStackTrace();
			//memFS.delDir(uri);
		}

	}

	public void createFolder(ITransaction transaction, String folderUri) { 
		System_out_println("cache/dir created: {"+folderUri+"}  -->["+file.getAbsolutePath()+"]");
		final MemoryFileItemFactory instance = MemoryFileItemFactory.getInstance();
		String contentType = "DIR";
		boolean isFormField = false;
		folderUri = folderUri.replace("//", "/");
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
		resourceUri = resourceUri.replace("//", "/");
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
 		String[] listNoDot = new String[list.length-1];
 		int i=0;
 		try{
	 		for (String val:list){
	 			if (!".".equals( val )){
	 				listNoDot [i++]=val;
	 			}
	 		}
 		}catch(java.lang.ArrayIndexOutOfBoundsException e){
 			e.printStackTrace();
 		}
		return  listNoDot;
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
			Date creationDate = new Date();
			try{
				creationDate = memFS.getCreationDate(uri );
			}catch(Exception e){
				e.printStackTrace();
			}
			if ("/".equals(uri)||"/.".equals(uri) || uri.endsWith("/.")){
				retval = new KeySetObject(keysTmp);
				return retval;
			}else if (memFS.isDir(uri) || uri.endsWith("..") ){
				retval = new DavStoredObject(uri);
				retval.setNullResource(false);
				retval.setFolder(true); 
				retval.setCreationDate(creationDate);
				retval.setLastModified(creationDate);
				
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
					System_out_println(valTmp);
				}
				retval.setResourceLength(lenTmp); 
				retval.setLastModified(creationDate);
				retval.setCreationDate(creationDate);
				return retval;
			}else if (1==2){ 
				throw new ObjectNotFoundException ();
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
		System_out_println("onClear");
	}
	public void onEvict(Object key) {
		System_out_println("onEvict"+key);
	}
	public void onLoad(Object key) {
		System_out_println("onLoad"+key);
	}
	public void onPut(Object key) {
		System_out_println("onPut"+key);
		storeKeys.add(""+key);
	}
	public void onRemove(Object key) {
		System_out_println("onRemove"+key);
		storeKeys.remove( ""+key);
	}

}


 