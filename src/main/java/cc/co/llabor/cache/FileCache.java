package cc.co.llabor.cache;

import gnu.inet.encoding.Punycode;
import gnu.inet.encoding.PunycodeException;
 
import java.io.File; 
import java.io.FileNotFoundException; 
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream; 
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder; 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator; 
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;


 

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheEntry;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheListener;
import net.sf.jsr107cache.CacheStatistics;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  14.04.2010::14:39:39<br> 
 */
public class FileCache implements Cache {

	public static final String NAMESPACE = "namespace";
	private static final Logger log = Logger .getLogger(FileCache.class.getName());
	private File basedir ;
	private Properties props = new Properties(); 

	public FileCache(Map arg0) {
		this.props .putAll(arg0);
		String baseDirName = ".filecache";
		String namespace = toName (""+ arg0.get(NAMESPACE));
		//System.getProperty("user.home");
		basedir = new File(baseDirName+File.separator+namespace);
		if (!basedir.exists()){
			basedir.mkdirs();
			System.out.println("BASEDIR [" + basedir +"] creted.");
		}
	}

	
	public void addListener(CacheListener arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return  ;
		} 
	}

	
	public void clear() {
		Set keys = keySet();
		for( Object key: keys.toArray()){
			remove(key);
		}
		 
	}

	
	public boolean containsKey(Object arg0) {
		return new File(this.basedir, ""+arg0).exists();
	}

	
	public boolean containsValue(Object o) {
		final Collection values = this.values();
		final boolean contains = values.contains(o);
		return contains;
	}

	
	public Set entrySet() {
		Set retval= new HashSet<Object>();
		retval.addAll(this.values());
		return retval;
	}

	
	public void evict() {
		// done
	}

	
	public Object get(Object key) {
		Object retval = null;
		try {
			File fTmp = createFile(  key);
			InputStream fis;
			synchronized (Properties.class) {
				fis = newFileInputStream(fTmp);
				if ((""+key).endsWith(".properties") || (""+key).endsWith("/.!")){
					retval = new Properties();
					((Properties)retval).load(fis);
				}else if (isTextable(key)){
					retval = readFileAsText(fis);
				}else{
					try{
						ObjectInputStream ois = new ObjectInputStream(fis);
						retval = ois.readObject();
					}catch(NullPointerException e){
						e.printStackTrace();
					}catch (java.io.StreamCorruptedException e) {
						fis.close();
						fis = newFileInputStream(fTmp);
						retval = readFileAsBinary(fis);
					}
				}
				fis.close();
			}
		} catch (FileNotFoundException e) {
			// http://forums.sun.com/thread.jspa?threadID=467841
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (IOException e) {
			// TODO http://forums.sun.com/thread.jspa?threadID=467841
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			if(Level.ALL == log.getLevel() || Level.FINEST == log.getLevel() || Level.FINER == log.getLevel() || Level.FINE == log.getLevel() ) e.printStackTrace();
		} 
		return retval ;

	}


	private boolean isTextable(Object key) {
		return 1==1 && ((""+key).endsWith(".js") || (""+key).endsWith(".xml")  );
	}


	public static synchronized InputStream newFileInputStream(File fTmp)
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		InputStream fis;
		fis =(InputStream)constructorITmp.newInstance(fTmp );;
		return fis;
	}

	
	private String readFileAsText(InputStream fis) throws IOException {
		byte[] buf = new byte[fis.available()];
		int lenTmp = fis.read(buf);
		String retval = new String(buf);
		return retval;
		 
	}
	private byte[] readFileAsBinary(InputStream fis) throws IOException {
		byte[] buf = new byte[fis.available()];
		int lenTmp = fis.read(buf);
		 
		return buf;
		 
	}	


	public Map getAll(Collection arg0) throws CacheException {
		Map retval = new HashMap<String, Object>();
		for( Object key: arg0.toArray()){
			final Object value = get(key);
			retval.put( key, value);
		}		
		return retval;
	}

	
	public CacheEntry getCacheEntry(Object arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
	}

	
	public CacheStatistics getCacheStatistics() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
	}

	
	public boolean isEmpty() { 
			return size() == 0;
		 
	}

	
	public Set keySet() {
		Set<String> retval = new HashSet<String> ();
		String[] listTmp =  null; 
		listTmp = this.basedir .list();// this.basedir.getParentFile().exists()
		for (String fNameTmp :listTmp )
		try{
			File nextTmp = new File ( this.basedir, fNameTmp);
			final String theRelativePath = nextTmp.toString();
			String theName = theRelativePath.substring(this.basedir.toString().length()+1);
			retval.add(theName );
			System.out.println("+"+fNameTmp);
			
		}catch(Exception e){
			e.printStackTrace();
			this.put(".placeholder", "."+System.currentTimeMillis());
			return keySet() ; //repeat if empty 
		}
 
		return retval;
	}

	
	public void load(Object arg0) throws CacheException {
		// done
		 
	}

	
	public void loadAll(Collection arg0) throws CacheException {
		// done
	}

	
	public Object peek(Object key) { 
		// TODO impement HIT-rate
			return this.get(key);
	}

	String toName(Object key)  {
		String retval = ""+key;
		if(1==2) 
		try { 
			retval = URLEncoder.encode(retval, "UTF-8");
			if(1==2){			
						String eKey = Punycode.encode(retval);
						QuotedPrintableCodec s = new QuotedPrintableCodec();
						(new QuotedPrintableCodec()).encode(retval);
						System.out.println("'"+retval+"' -> ["+eKey+"]");
			}
		} catch (PunycodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		retval = retval.replace("%2F", "/");
		String from2[][]={
				{":", "=..="},
				//{"\\", "=slash="},
				{"\n", "=!n!="},
				{"\b", "=!b!="},
				{"\t", "=!T!="},
				//{"/", "=!s!="},
				{"\"", "=!!="},
				{"*", "=!X!="},
				{"?", "=!Q!="},
				{"&", "=!A!="},
				{"\'", "=!="} 
		};
		for (String[]from2to:from2){
			retval = retval.replace(from2to[0],from2to[1]);
		}
		return retval ;
	}
	
	
	public Object put(Object key, Object arg1) {
		Object retval =  arg1;
		try {
			
			File fileTmp = createFile(key);
			String parent = fileTmp.getParent();
			final File parentDir = new File(parent);
			parentDir.mkdirs();
			synchronized (Properties.class) {
				OutputStream fout = null;
				fout = newFileOutputStream(fileTmp);
				if (arg1 instanceof Properties) {
					
					Properties prps = (Properties) arg1;
					final long currentTimeMillis = System.currentTimeMillis();
					// prps.put(MemoryFileCache.CREATION_DATE,
					// currentTimeMillis);
					prps.store(fout, "CacheEntry stored at "
							+ currentTimeMillis);
				} else if (arg1 instanceof InputStream) {
					InputStream content = (InputStream) arg1;
					int bufSize = content.available();
					bufSize = bufSize == 0 ? 4096 : bufSize;
					byte[] buf = new byte[bufSize];
					for (int readed = content.read(buf); readed > 0; readed = content
							.read(buf)) {
						fout.write(buf, 0, readed);
					}
				} else if (arg1 instanceof MemoryFileItem){
					MemoryFileItem it1 = (MemoryFileItem)arg1;
					if (it1.getSize()>0)
					try {
						it1 .write( fout);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("");
						e.printStackTrace();
					}
					 
				} else {
					ObjectOutputStream wr = new ObjectOutputStream(fout);
					wr.writeObject(arg1);
					wr.close();
				}
				fout.flush();
				fout.close();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();//(new File(".")).getAbsoluteFile()
			return e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e;
		}
		return retval;
	}


	static Class fosClazz = null;
	static Class fisClazz = null; 
	static Constructor constructorITmp = null;
	static Constructor constructorOTmp = null;
	static {
		try {
			fosClazz =  Class.forName("java.io.FileOutputStream");
			constructorOTmp = fosClazz.getConstructor(File.class);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fisClazz =   Class.forName("java.io.FileInputStream");
			constructorITmp = fisClazz.getConstructor(File.class);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static synchronized OutputStream newFileOutputStream(File fileTmp)
			throws ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		OutputStream fout;		 
		fout = (OutputStream)constructorOTmp.newInstance(fileTmp );
		return fout;
	}

	private File createFile(Object key) {
		String keyStr = null;
		try{
			keyStr= toName(key);
		}catch (Exception e) {}
		File retval =  new File(this.basedir , keyStr); 
		return retval;
	}

	
	public void putAll(Map arg0) {
		 for(Iterator  it = arg0.keySet().iterator();it.hasNext();){
			 Object key = it.next();
			 Object val = arg0.get(key);
			 this.put( key, val );
		 }
	}

	
	public Object remove(Object key) {
		Object retval = get(key);
		File fileTmp = createFile(key);
		synchronized (Properties.class) {
			if (fileTmp.exists() && retval != null){
				File dest = createFile("~"+key);
				if(1==2)
				try {
					dest = File.createTempFile("XXX", "-");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}	
				if (2==3){
					fileTmp.renameTo(dest );
					dest.delete();
				}else{
					fileTmp.delete();
				}
			}
		}
		return retval;
	}

	
	public void removeListener(CacheListener arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
		}
	}

	
	public int size() { 
		return basedir.list().length;
		 
	}

	
	public Collection values()   {
		Set<String> all = this.keySet();
		Map<String, Object> allVals = null;
		try {
			allVals = getAll(all);
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.printStackTrace();
		}
		Collection retval =allVals.values(); 
		return retval ;
	}


	public String getBaseDir() {
		return this.basedir.toString();
	}

}


 