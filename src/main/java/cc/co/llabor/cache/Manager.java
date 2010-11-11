package cc.co.llabor.cache;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
 

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  26.08.2010::20:14:35<br> 
 */
public class Manager {
	
	static{
		 // try to load default cache conf
		 Object gooTmp = null;
		 try{
			 gooTmp = System.getProperty("com.google.appengine.runtime.version");
		 }catch(Throwable e){e.printStackTrace();}
		 
		 try{
			 if (gooTmp==null){
				 //net.sf.jsr107cache.CacheFactory=ws.rrd.cache.BasicCacheFactory
				 try{
					 //java.io.InputStream in = MemoryFileCache.class.getClassLoader().getResourceAsStream("META-INF/services/net.sf.jsr107cache.CacheFactory");
					 //java.io.InputStream in = MemoryFileCache.class.getClassLoader().getResourceAsStream("jcache.properties");
					 String fNameTmp = MemoryFileCache.class.getClassLoader().getResource("META-INF/services/!net.sf.jsr107cache.CacheFactory").toString();
					 fNameTmp = fNameTmp.replace("%20", " ");
					 fNameTmp = fNameTmp.replace("file:/", "");
					 //if (myNewCacheSettings.exists())
					 File myNewCacheSettings = new File (fNameTmp) ;
					 File newFile4Cfg = new File(fNameTmp.replace("s/!ne", "s/ne"));
					 myNewCacheSettings.renameTo(newFile4Cfg);
					 java.io.InputStream in = new FileInputStream(newFile4Cfg);
					 Properties prTmp = new Properties();
					 prTmp.load(in);
					 String key = "net.sf.jsr107cache.CacheFactory";
					 String val = prTmp.getProperty(key);
					 System.out.println(key +" = "+val);
					 System.out.println("cleaning "+key+" == {" +System.clearProperty( key )+"}");
				 }catch(Exception e){
					 e.printStackTrace();
				 } 
			 }else{
				 System.out.println("GAE v"+gooTmp+ " CM="+CacheManager.getInstance());
			 }
		 }catch(Throwable e){
			 e.printStackTrace();
		 }
	}
      	
	
	public static Cache getCache()   {
		String nsTmp = ".defaultcache";
		try{
			throw new RuntimeException("");
		}catch(Throwable  e){
			nsTmp = "Hx0"+e.getStackTrace()[2] ;//getCache
		}
		nsTmp = MemoryFileCache.class.getName();
		return getCache(nsTmp);
	}

	public static Cache getCache(String cacheNS)   { 
		
		return getCache( cacheNS, true) ;
	}

	public static Cache getCache(String cacheNS, boolean createIfNotExists) {
		if (cacheNS == null)return getCache("DEFAULT");
		
		CacheManager cm = CacheManager.getInstance();
		Cache retval = cm.getCache (cacheNS);
		boolean isGAE = null != System.getProperty("com.google.appengine.runtime.version");
		if ( ( retval == null && ( isGAE || createIfNotExists ) )) // for GAE - always create
		synchronized (CacheManager.class) { 
			if (retval == null)
			try {
				CacheFactory cacheFactory;
				cacheFactory = cm.getCacheFactory();
				Properties props = new Properties();
				props.put(NS.NAMESPACE, cacheNS );
				Cache cacheTmp;
				cacheTmp = cacheFactory.createCache(props);
				cm.registerCache(cacheNS, cacheTmp);
				retval = cacheTmp;
			} catch (CacheException e) { 
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} 
		return  retval; 
	}

}


 