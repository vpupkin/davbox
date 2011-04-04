package cc.co.llabor.jdoAsCache;

import java.io.IOException;
import java.io.OutputStream;   
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
   
import cc.co.llabor.cache.MemoryFileItem;
import cc.co.llabor.cache.MemoryFileItemFactory; 
import net.sf.jsr107cache.Cache; 
import net.sf.jsr107cache.CacheListener;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  14.04.2010::10:50:13<br> 
 */
public class MemoryFileDAO {
	static final Logger log = LoggerFactory.getLogger(MemoryFileDAO.class);
	 private String cachename;
	 
	 private List<CacheListener> listeners = new ArrayList<CacheListener>();
	 
	 public MemoryFileDAO(String cachename) {
		this.cachename = cachename;
	}

	public static MemoryFileDAO getInstance(String cachename){
		return new MemoryFileDAO( cachename );
	}
	
	 public Date getLastModified(String name) throws IOException{
		 MemoryFileItem f = get(name);
		 return f.getDate_created();
	 }
	 
	 public Date getCreationDate(String name) throws IOException{
		 MemoryFileItem f = get(name);
		 return f.getDate_created();
	 }
	 	
	
	private static Cache managerGetCache(){
		return managerGetCache("DEFAULT_JDO_CACHE");
	}
	
	private static Cache managerGetCache(String cachename2) {
		return JDOCache.getInstance(cachename2);
	}
	 
	public boolean isDir (String name) throws IOException{
		Cache cache = managerGetCache(this.cachename); 
		Object o = cache.get(name+"/.!"+"");
		return (o instanceof Properties );
	}
	

	public MemoryFileItem get (String name) throws IOException{
			Cache cache = managerGetCache(this.cachename);

			MemoryFileItem retval = null;
			Object o = cache.get(name);
			if (o instanceof String){
				retval = MemoryFileItemFactory.getInstance().createItem(name, "text/plain",false,name);///new MemoryFileItem (name,"text/plain",false,name, 0);
				retval.getOutputStream().write( ((String)o).getBytes());
				retval.flush();
			}
			if (o instanceof MemoryFileItem){
				retval = (MemoryFileItem) o;
				if(retval.getSize()==1 ){
					//= retval.?retval:retval; // TODO assumes : if size == 1byte =>> chunked store. Refactor!
					log.warn( "TODO:"+retval);
				}
				 
				
			}
			if (retval ==null){ // try to restore parts
				  for (int i=0;cache.get(name+"::"+i)!=null;){
					 MemoryFileItem next = (MemoryFileItem)cache.get(name+"::"+i);				 
					 if (i==0 ){
						 retval = MemoryFileItemFactory.getInstance().createItem(next.getFieldName() , next.getContentType() ,false,next.getName());//new MemoryFileItem (next.fileName,next.contentType,next.isFormField(),next.fileName, 0);
					 }
					 retval.getOutputStream().write(next.get());
					 i++;
				 }
				 if (retval !=null)
					  retval.flush();
			 }
			 return retval;
		 }	
             
	public String mkdir(MemoryFileItem item) throws IOException {
			Cache cache = managerGetCache(cachename);
			String name = item.getName();
			name +="/.!";
			name = name.replace("//", "/");
			Object oldTmp = cache.get(name);
			if (oldTmp != null) {
				cache.remove(name);
			}else{
				oldTmp= new Properties(); 
			}
			if (!(oldTmp instanceof Properties)){
				oldTmp= new Properties();  // forget any kind of content and replace by empty DIR
			}
			Properties dir = (Properties) oldTmp;
			// creating date
			dir.setProperty(".",""+System.currentTimeMillis());
			
			cache.put(name, dir );
			int beginIndex = 0;
			int endIndex = name.substring(0,name.length()-3).lastIndexOf("/");
			// update parent
			final String parentName = name.substring(beginIndex, endIndex)+"/.!";
			Properties parent = (Properties)cache.get(parentName);
			parent = parent==null?new Properties():parent;
			parent.put(name, ""+System.currentTimeMillis());
			cache.remove(parentName);
			cache.put(parentName, parent );
			
			return name;
	 }

	public String put(MemoryFileItem item) throws IOException {
		Cache cache = managerGetCache(cachename);
		String name = item.getName();
		Object oldTmp = cache.get(name);
		if (oldTmp != null) {
			cache.remove(name);
		}
		byte[] bs = item.get();

		if (bs.length < MAX_SIZE) {
			cache.put(name, item);
		} else { // SPLIT
			int done = 0;
			for (int i = 0; done < bs.length; i++) {
				String nameTmp = item.getName() + "::" + i;
				MemoryFileItem itemNext = MemoryFileItemFactory.getInstance().createItem(item.getFieldName() , item.getContentType() ,false,item.getName());//new MemoryFileItem(item.fileName,						item.contentType, item.isFormField(), item.fileName, 0);
				OutputStream outputStream = itemNext.getOutputStream();
				outputStream.write(bs, done, Math.min(MAX_BUFF_SIZE, bs.length
						- done));
				itemNext.flush();
				done += MAX_BUFF_SIZE;
				try {
					cache.put(nameTmp, itemNext);
				} catch (Throwable e) {
					log.debug("SPLIT", e );
					throw new IOException(e.getMessage());
				}
			}
		}
		for (CacheListener l : listeners) {
			Object key = name;
			l.onPut(key);
		}
		addToList(name);
		return name;
	}

	private synchronized void addToList(String name) {
		if (list.indexOf(name) == -1)
			list.add(name);
		// update parent
		int beginIndex = 0;
		int endIndex = name.substring(0,name.length()-3).lastIndexOf("/");
		Cache cache = managerGetCache(cachename);
		final String parentName = name.substring(beginIndex, endIndex)+"/.!";
		Properties parent = (Properties)cache.get(parentName);
		parent = parent==null?new Properties():parent;
		String pureName = name.substring(endIndex+1);
		parent.put(pureName, ""+System.currentTimeMillis());
		// replace
		cache.remove(parentName);
		cache.put(parentName, parent );
	}
	
	
	/**
	 * @deprecated use getInstance()
	 * 
	 * 
	 * @author vipup
	 * @param name
	 * @return
	 * @throws IOException
	 */
	 static MemoryFileItem _get (String name) throws IOException{
		Cache cache = getMyCache();
		MemoryFileItem retval = (MemoryFileItem) cache.get(name);
		 if (retval ==null){ // try to restore parts
			  for (int i=0;cache.get(name+"::"+i)!=null;){
				 MemoryFileItem next = (MemoryFileItem)cache.get(name+"::"+i);				 
				 if (i==0 ){
					 retval =MemoryFileItemFactory.getInstance().createItem(next.getFieldName() , next.getContentType() ,false,next.getName());//// new MemoryFileItem (next.fileName,next.contentType,next.isFormField(),next.fileName, 0);
				 }
				 retval.getOutputStream().write(next.get());
				 i++;
			 }
			 retval.flush();
		 }
		 return retval;
	 }

	private static Cache getMyCache() {
		return managerGetCache();
	}
	 static int MAX_SIZE = 30*1024;
	 static int MAX_BUFF_SIZE = MAX_SIZE;

	 /**
	  * 
	  * @deprecated
	  * 
	  * @author vipup
	  * @param item
	  * @return
	  * @throws IOException
	  */
	 static String _put (MemoryFileItem  item) throws IOException{
		 Cache cache = getMyCache();
		 String name = item.getName();
		 byte[] bs = item.get();
		 
		if (bs.length < MAX_SIZE){
			 cache.put(name,item);
		 }else{ //SPLIT
			 int done = 0;
			 for (int i=0;done<bs.length ;i++){
				 String nameTmp = item.getName()+"::"+i;
				 MemoryFileItem itemNext =  MemoryFileItemFactory.getInstance().createItem(item.getFieldName() , item.getContentType() ,false,item.getName());////new MemoryFileItem (item.fileName,item.contentType,item.isFormField(),item.fileName, 0);
				 OutputStream outputStream = itemNext.getOutputStream();
				 outputStream.write(bs, done,Math.min( MAX_BUFF_SIZE, bs.length-done ));
				 itemNext.flush();
				 done += MAX_BUFF_SIZE;
				 try{
					 cache.put(nameTmp,itemNext);
				 }catch(Throwable e){
					 log.debug("SPLIT", e );
					 throw new IOException (e.getMessage());
				 }
			 }
		 }
		 return name;
	 }

	List<String> list = new ArrayList<String>();
	final String[] retval = new String[] { "" };
	{
		list.add(".");
	}

	public String[] list(String folderUri) {
		//list.add("222");list.add(".");list.remove(0);
		list.clear();
		Set<String> retvalTmp = new HashSet<String>();
		Properties dir = null;
		final String dirTmp = (folderUri+"/.!").replace("//","/");
		try{
			Cache cache = managerGetCache(cachename); //Manager.getCache("SCRIPTSTORE/ABC")
			dir = (Properties)cache.get(dirTmp);//reserved for Dir-content
			for (String item: dir.keySet() .toArray(new String[]{})){
				if (item.endsWith("/.!")){
					final String dirName = item;
					//dirName = dirName.substring(1,item.length()-3);
					list.add(dirName);
					continue;//fix for old bugs
				}
				
				list.add(item);
			}
		}catch(	NullPointerException e){
			dir = new Properties();
			Cache cache =managerGetCache(cachename); 
			// creating date
			dir.setProperty(".",""+System.currentTimeMillis());
			cache.put(dirTmp, dir );
		}catch(Throwable e){
			log.debug("LIST", e );
		}
		
		for (String nameTmp:list.toArray(retval)){
			//  after DEL name is NULL 
			if (nameTmp == null) continue;
			nameTmp = nameTmp.replace("//", "/");
			Object o = null;
			try {
				if (nameTmp.endsWith("/.!")){
					Cache cache = managerGetCache(this.cachename);
 
					MemoryFileItem retval = null;
					o = cache.get(nameTmp);					
				}else{
					 String fullPath = folderUri +"/"+	nameTmp;
					 o = get(fullPath.replace("//", "/"));
				}

				if (null == o && !".".equals(nameTmp)){
					list.remove(nameTmp);
				}else if (o instanceof Properties){
					final int beginIndex = "/".equals( folderUri )? 1:folderUri.length()+1;
					final String dirName = nameTmp.substring(beginIndex,nameTmp.length()-3);
					retvalTmp.add(dirName);//+".."
				}else{
					final String fileName = nameTmp;//.substring(1)
					retvalTmp.add(fileName);
				}
				
			} catch (IOException e) {
				log.error( "??----"+nameTmp,e );
				 
			} catch (Throwable e) {
				log.error( "??----"+nameTmp,e );
				 
			}				
		}
		return retvalTmp.toArray(retval);
	}

	public Object delete(MemoryFileItem toDel) {
		Cache cache = managerGetCache(cachename);
		String name2Del = toDel.getName();
		Object o = cache .remove(name2Del );
		remFromList(toDel.getName());
		return o;
		 
	}

	private void remFromList(String name) {
		if (list.indexOf(name) == -1)
			list.add(name);
		// update parent
		int beginIndex = 0;
		int endIndex = name.substring(0,name.length()-3).lastIndexOf("/");
		Cache cache = managerGetCache(cachename);
		final String parentName = name.substring(beginIndex, endIndex)+"/.!";
		Properties parent = (Properties)cache.get(parentName);
		parent = parent==null?new Properties():parent;
		String pureName = name.substring(endIndex+1);
		parent.remove( pureName ); // "doc"
		parent.remove( name ); // /ffgtk-0.7.91/doc
		parent.remove( name +"/.!" ); // /ffgtk-0.7.91/doc
		// replace
		cache.remove(parentName);
		cache.put(parentName, parent );
	}

	public void delDir(String uri) {
		Cache cache = managerGetCache(this.cachename);
		String name = uri;
 
		log.info( "<delete>\n\t<dir name=\'"+name+"\'/>... ");
		Object o = cache.remove(name+"/.!"+"");
		log.info( o+"\n</delete>");		
		remFromList(name);
		 
	}


}


 