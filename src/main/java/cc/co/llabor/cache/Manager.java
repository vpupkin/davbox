package cc.co.llabor.cache;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;
 

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
	static final Logger log = Logger.getLogger(Manager.class.getName());
	// try to load default cache conf
	static Object gooTmp = null;	
	static{ 
		log.info( "<INITCACHE>");
		 try{
			 gooTmp = System.getProperty("com.google.appengine.runtime.version");
		 }catch(Throwable e){
			 log.warning(e.getMessage());
			 e.printStackTrace();
		 }
		 
		 try{
			 
			 if (gooTmp==null){
				 //net.sf.jsr107cache.CacheFactory=ws.rrd.cache.BasicCacheFactory
				 log.info("<NOGAE/>" );

				 try{
					 //java.io.InputStream in = MemoryFileCache.class.getClassLoader().getResourceAsStream("META-INF/services/net.sf.jsr107cache.CacheFactory");
					 //java.io.InputStream in = MemoryFileCache.class.getClassLoader().getResourceAsStream("jcache.properties");
					 log.info("<rename src='!net.sf.jsr107cache.CacheFactory' dst='net.sf.jsr107cache.CacheFactory'> "  );
					 String fNameTmp = MemoryFileCache.class.getClassLoader().getResource("META-INF/services/!net.sf.jsr107cache.CacheFactory").toString();
					 fNameTmp = fNameTmp.replace("%20", " ");
					 fNameTmp = fNameTmp.replace("file:/", "");
					 //if (myNewCacheSettings.exists())
					 File reserveConfigFile = new File (fNameTmp) ;
					 File newFile4Cfg = new File(fNameTmp.replace("s/!ne", "s/ne"));
					 java.io.InputStream in = null;
					 if (newFile4Cfg .exists() ){
						 log.warning("<!!!EXIST!!!>");
						 in = new FileInputStream(newFile4Cfg);
					 }else if (reserveConfigFile.exists()){
						 reserveConfigFile.renameTo(newFile4Cfg);
						 in = new FileInputStream(newFile4Cfg);
						 log.info("<compleete>");
					 }else{
						 in = MemoryFileCache.class.getClassLoader().getResourceAsStream("META-INF/services/net.sf.jsr107cache.CacheFactory");
						 String cpLocation = MemoryFileCache.class.getClassLoader().getResource("META-INF/services/net.sf.jsr107cache.CacheFactory").toString();
						 log.info("<init src='"+cpLocation+"'>");
					 }
					 log.info("</rename>");
					 
					 Properties prTmp = new Properties();
					 log.info("<init>");
					 prTmp.load(in);
					 
					 log.info(""+prTmp);
					 
					 String key = "net.sf.jsr107cache.CacheFactory";
					 String val = prTmp.getProperty(key);
					 
					 final String propertyBak = System.getProperty(key);
					 log.info( " <clear.System.property name='"+key +"'><from>"+propertyBak+"</from><to>"+val+"</to>");
					 try{
						 System.clearProperty( key );
					 }catch(Exception e){
						 log.warning(e.getMessage());
					 }
						 
					 log.info( " </clear.System.property>");
					 in.close();
					 log.info("</init>");
				 }catch(NullPointerException e){
					//-Dcccache
					 log.severe( e.getMessage( )+"!  -Dcccache  ??? "  ); 
					 throw e;
				 }catch(Exception e){
					 log.severe( e.getMessage() ); 
					 e.printStackTrace();
				 } 
			 }else{
				 log.info("<GAE v"+gooTmp+ " CM="+CacheManager.getInstance() +"/>");
			 }
		 }catch(Throwable e){
			 log.severe(  e.getMessage() ); 
			 e.printStackTrace();
		 }
		 log.info("</INITCACHE>");
	}
      	
	/**
	 * @deprecated use named getCache instead
	 * @author vipup
	 * @return
	 */
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
		boolean isGAE = null != gooTmp;
		isGAE = false; // soo - doch nicht "always" 
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
				log.severe(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} 
		return  retval; 
	}

}


 