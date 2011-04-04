package cc.co.llabor.jdoAsCache;
 

import net.sf.jsr107cache.Cache;

import ws.rdd.jdo.Blob; 

/** 
 * <b>Description:File-fantom at the JDO-world </b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  Feb 24, 2011::10:51:22 PM<br> 
 */
public class File extends Blob{
 
	private static final long serialVersionUID = -4081445548747019554L;
	private String cachename2;
	private String keyStr;

	public File(byte[] data) {
		super(data); 
	}

	public File(String cachename2, String keyStr) {
		super(null);
		this.cachename2 = cachename2;
		this.keyStr = keyStr;
		
	}

	public File(Blob blobTmp, boolean readOnly) {
		super(blobTmp.getData());
	}

	public String getCachename2() { 
			return cachename2;
	}

	public void setCachename2(String cachename2) {
		this.cachename2 = cachename2;
	}

	public String getKeyStr() { 
			return keyStr;
	}

	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}

	public boolean exists() {
		Cache cache = JDOCache.getInstance( cachename2);
		Object o = cache.peek(this.getName());
		return o != null;
		
	}

	public void renameTo(File dest) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 24, 2011");
		else {
		}
	}

	public void delete() {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since Feb 24, 2011");
		else {
		}
	}

	public Blob asBlob() {
		Blob retval = new Blob(this.getData());
		retval .setName(this.getName());
		return retval;
	}

}


 