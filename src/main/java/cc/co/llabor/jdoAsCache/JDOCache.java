package cc.co.llabor.jdoAsCache;

 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream; 
import java.io.Serializable; 
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction; 

import cc.co.llabor.jdo.Blob;
import cc.co.llabor.jdo.RRD_JDOHelper;

 

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheEntry;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheListener;
import net.sf.jsr107cache.CacheStatistics;

/**
 * <b>Description:TODO</b>
 * 
 * @author gennady<br>
 *         <br>
 *         <b>Copyright:</b> Copyright (c) 2006-2008 Monster AG <br>
 *         <b>Company:</b> Monster AG <br>
 * 
 * Creation: Feb 24, 2011::10:03:46 PM<br>
 */
public class JDOCache implements Cache {

	private String cachename2;

	RRD_JDOHelper helper;

	public JDOCache(String cachename2) {
		this.cachename2 = cachename2;
		helper = RRD_JDOHelper.getInstance();
		cacheReg.put(cachename2, this);
	}

	public static final String NAMESPACE = "namespace";
	private static final Logger log = Logger
			.getLogger(JDOCache.class.getName());

	private static final Hashtable<String, Cache> cacheReg = new Hashtable<String, Cache>();

	public void addListener(CacheListener arg0) {
		// TODO implement
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public boolean containsKey(Object id) {
		final Object object = get(id);
		return object != null;
	}

	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return false;
		}
	}

	public Set entrySet() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
	}

	public void evict() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
		}
	}

	final static int MAX_TRY = 3;
	int tryCount = 0; 
	public Object get(Object key) {
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF()
				.getPersistenceManager();
		Query query = pm.newQuery(Blob.class);
		query.setFilter("name == nameParam");
		//query.setOrdering("createDate desc");
		query.declareParameters("String nameParam");
		Object retval = null;
		try {
			List<Blob> results = (List<Blob>) query.execute(key);
			final Iterator<Blob> iterator = results.iterator();
			if (iterator.hasNext()) {
				final Blob next = iterator.next();
				retval = blob2obj(key, next);
				return retval;
			} else {
				return null;
			}
		} catch (com.google.appengine.api.datastore.DatastoreNeedIndexException e ){
			if (tryCount <MAX_TRY){//http://www.mail-archive.com/google-appengine@googlegroups.com/msg13904.html
				tryCount ++;
				log.warning("index waiting #"+tryCount+"...");
				try{
					Thread.sleep(200);
					return get(key);
				}catch(Throwable e1){}
			}else{
				throw e;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			query.closeAll();
			pm.close();
		}
		return retval;
	}

	private Object blob2obj(Object key, Blob next) throws IOException,
			ClassNotFoundException {

		final byte[] data = next.getData();new String(data);
		InputStream fis = new ByteArrayInputStream(data);

		Object retval;
		if (("" + key).endsWith(".properties") || ("" + key).endsWith(".!")) {
			retval = new Properties();
			((Properties) retval).load(fis);
		} else if (("" + key).endsWith(".js") || ("" + key).endsWith(".xml")) {
			retval = fis;
		} else {
			ObjectInputStream ois = new ObjectInputStream(fis);
			retval = ois.readObject();
			fis.close();
		}
		return retval;
	}

	public Map getAll(Collection arg0) throws CacheException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
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
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return false;
		}
	}

	public Set keySet() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
	}

	public void load(Object arg0) throws CacheException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
		}
	}

	public void loadAll(Collection arg0) throws CacheException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
		}
	}

	public Object peek(Object key) {
		// TODO impement HIT-rate
		return this.get(key);
	}

	String toName(Object key) {
		String retval = "" + key; 
		retval = retval.replace("%2F", "/");
		String from2[][] = { { ":", "=..=" },
				// {"\\", "=slash="},
				{ "\n", "=!n!=" }, { "\b", "=!b!=" }, { "\t", "=!T!=" },
				// {"/", "=!s!="},
				{ "\"", "=!!=" }, { "*", "=!X!=" }, { "?", "=!Q!=" },
				{ "&", "=!A!=" }, { "\'", "=!=" } };
		for (String[] from2to : from2) {
			retval = retval.replace(from2to[0], from2to[1]);
		}
		return retval;
	}

	boolean exists(Object id) {
		return containsKey(id);
	}

	Blob getById(String id) {
		final Object o = get(id);
		return obj2blob(o);
	}

	private File obj2blob(Object arg1) {
		ByteArrayOutputStream fout = new ByteArrayOutputStream();
		try {

			if (arg1 instanceof Properties) {
				Properties prps = (Properties) arg1;
				prps.store(fout, "CacheEntry stored at "
						+ System.currentTimeMillis());
				fout.close();

			} else if (arg1 instanceof InputStream) {
				InputStream content = (InputStream) arg1;
				int bufSize = content.available();
				bufSize = bufSize == 0 ? 4096 : bufSize;
				byte[] buf = new byte[bufSize];
				for (int readed = content.read(buf); readed > 0; readed = content
						.read(buf)) {
					fout.write(buf, 0, readed);
				}
				fout.close();
				// //Serializable
			} else if (arg1 instanceof Serializable) {
				ObjectOutputStream oout = new ObjectOutputStream(fout);
				oout.writeObject(arg1);
				oout.flush();
				fout.close();
			} else {
				ObjectOutputStream wr = new ObjectOutputStream(fout);
				wr.writeObject(arg1);
				wr.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File retval = new File(fout.toByteArray());
		return retval;

	}

	void createFile(String id, boolean readOnly) {
		File backend;
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF()
				.getPersistenceManager();
		Blob blobTmp;
		if (exists(id)) {
			blobTmp = getById(id);
			backend = new File(blobTmp, readOnly);// RrdJdoBackend
		} else {
			blobTmp = new Blob("".getBytes());
			blobTmp.setName(id);
			pm.makePersistent(blobTmp);
			backend = new File(blobTmp, readOnly);// RrdJdoBackend
		}
		pm.close();
	}

	public Object put(Object key, Object arg1) {
		File fileTmp =  obj2blob(arg1); 
		PersistenceManagerFactory pmf = RRD_JDOHelper.getInstance().getPMF();
		PersistenceManager pm = pmf.getPersistenceManager();
		Object id  = key;
		if ( 1==2 && exists(id)) {
			Object val =  get(id);
			remove(key);
			put(key, arg1 );
			System.out.println("exist:"+val);
		} else {
			fileTmp .setName( ""+id); 
			Transaction tx=pm.currentTransaction();
			 try{ 
//					Blob blobTmp = new Blob(fileTmp.getData());
//					blobTmp .setName (fileTmp.getName()  );
					// value-strategy 
					//javax.jdo.JDOFatalUserException: An object of class "cc.co.llabor.jdo.Blob" uses SingleFieldIdentity using the field "key" yet this field has not had its value set! Either set the field manually, or set a value-strategy for that field.
					System.out.println("Persisting ..."); 
					tx.begin(); 
					pm.makePersistent(fileTmp.asBlob() );
		            tx.commit();
		            System.out.println("Done!");
		            System.out.println("Check.. ");
		            check(pm);
		     }finally{
	                if (tx.isActive())
	                {
	                	 System.out.println("Failed! Rollback.");
	                    tx.rollback();
	                }
                pm.close();
            }
		}
		 
		return arg1;

	}
	
	
	void check(PersistenceManager pm ){
        // Basic Extent of all Products
        //PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            System.out.println("Retrieving Extent for Products");
            Extent e = pm.getExtent(Blob.class, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                Blob obj = (Blob)iter.next();
                log.fine( ">  " + obj.getName() +obj.getKey() +"::"+obj.getData() );
            }
            tx.commit();
        }
        catch (Exception e)
        {
            System.out.println("Exception thrown during retrieval of Extent : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
             
        }
	}

	private File createFile(Object key) {
		String keyStr = null;
		keyStr = toName(key);
		File retval = new File(this.cachename2, keyStr);
		return retval;
	}

	public void putAll(Map arg0) {
		for (Iterator it = arg0.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			Object val = arg0.get(key);
			this.put(key, val);
		}
	}

	public Object remove(Object key) {
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF()
				.getPersistenceManager();
		Query query = pm.newQuery(Blob.class);
		query.setFilter("name == nameParam");
		//query.setOrdering("createDate desc");
		query.declareParameters("String nameParam");
		Object retval = null;
		try {
			List<Blob> results = (List<Blob>) query.execute(key);
			final Iterator<Blob> iterator = results.iterator();
			for (;iterator.hasNext();) {
				final Blob next = iterator.next();
				pm.deletePersistent(next);
			}

		} finally {
			query.closeAll();
			pm.close();
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
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return 0;
		}
	}

	public Collection values() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 14.04.2010");
		else {
			return null;
		}
	}
	

	public static Cache getInstance(String cachename22) {
		Cache retval = cacheReg.get(cachename22);
		if (retval == null){
			retval = new JDOCache(cachename22);
		}
		return retval;
	}

}
