package cc.co.llabor.dav.jdo;

import ws.rdd.jdo.Blob;

/** 
 * <b>Description:TODO</b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  Feb 16, 2011::10:13:36 PM<br> 
 */
public class DavBackend {

	private Blob blobTmp;
	private boolean readOnly;

	public DavBackend(Blob blobTmp, boolean readOnly) {
		this.blobTmp = blobTmp;
		this.readOnly = readOnly;
	}

	public Blob getBlobTmp() { 
			return blobTmp;
	}

	public boolean isReadOnly() { 
			return readOnly;
	}
 

}


 