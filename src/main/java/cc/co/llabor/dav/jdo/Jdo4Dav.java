package cc.co.llabor.dav.jdo;

import java.io.InputStream;

import javax.jdo.PersistenceManager;

import ws.rdd.jdo.RRD_JDOHelper;

import java.io.IOException; 
import java.util.List; 
import javax.jdo.Query;

import ws.rdd.jdo.Blob; 
 

import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject; 
import cc.co.llabor.dav.AbstractTransactionalDaver;

/** 
 * <b>Description:TODO</b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  Feb 16, 2011::10:03:47 PM<br> 
 */
public class Jdo4Dav extends AbstractTransactionalDaver implements IWebdavStore
{
	@Override
	protected void tearDown() {
		// TODO Auto-generated method stub
		if (1==2)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) {
		if (1==2)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		}
 	}
		

	public void createResource(ITransaction transaction, String resourceUri) {
		// TODO Auto-generated method stub
		if (1==2)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		}
	}

	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		return null;
		}
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		return null;
		}
	}

	public long getResourceLength(ITransaction transaction, String path) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		return 0;
		}
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		return null;
		}
	}

	public void removeObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		}
	}

	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 16, 2011");
		else {
		return 0;
		}
	}

	/**
	 * Removes the storage with the given ID from the memory.
	 *
	 * @param id Storage ID
	 * @return True, if the storage with the given ID is deleted, false otherwise.
	 */
	protected boolean delete(String id) {
		if (exists(id)) {
			PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF().getPersistenceManager();
			pm.deletePersistent( id);
			pm.close();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Method to determine if a memory storage with the given ID already exists.
	 *
	 * @param id Memory storage ID.
	 * @return True, if such storage exists, false otherwise.
	 */ 
	protected synchronized boolean exists(String id) {
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF().getPersistenceManager();
	    Query query = pm.newQuery(Blob.class);
	    query.setFilter("name == nameParam");
	    query.setOrdering("createDate desc");
	    query.declareParameters("String nameParam");
	
	    try {
	        List<Blob> results = (List<Blob>) query.execute( id );
	        if (results.iterator().hasNext()) {
	            return true;
	        } else {
	            return false;
	        }
	    } finally {
	        query.closeAll();
	        pm.close();
	    }
	
	}
 
	protected synchronized Blob getById(String id) {
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF().getPersistenceManager();
	    Query query = pm.newQuery(Blob.class);
	    query.setFilter("name == nameParam");
	    query.setOrdering("createDate desc");
	    query.declareParameters("String nameParam");
	    Blob retval = null;
	    try {
	        
			List<Blob> results = (List<Blob>) query.execute( id );
	        if (results.iterator().hasNext()) {
	        	retval =  results.iterator().next();
	        } 
	    } finally {
	        query.closeAll();
	        pm.close();
	        
	    }
	    return retval;
	
	}

 

	/**
	 * Creates RrdMemoryBackend object.
	 *
	 * @param id	   Since this backend holds all data in memory, this argument is interpreted
	 *                 as an ID for this memory-based storage.
	 * @param readOnly This parameter is ignored
	 * @return RrdMemoryBackend object which handles all I/O operations
	 * @throws IOException Thrown in case of I/O error.
	 */
	/* (non-Javadoc)
	 * @see org.jrobin.core.RrdBackendFactory#open(java.lang.String, boolean)
	 */
	protected synchronized DavBackend open(String id, boolean readOnly)
			throws IOException {
		DavBackend backend;
		PersistenceManager pm = RRD_JDOHelper.getInstance().getPMF().getPersistenceManager();
		Blob blobTmp ;
		if (exists(id)) {
			blobTmp = getById( id);
			backend = new DavBackend (blobTmp,  readOnly);
		}
		else {
			blobTmp = new Blob("".getBytes());
			blobTmp .setName(id); 
			pm.makePersistent(blobTmp);				
			backend = new DavBackend(blobTmp ,readOnly);
		}
		pm.close();
		return backend;
	}
}


 