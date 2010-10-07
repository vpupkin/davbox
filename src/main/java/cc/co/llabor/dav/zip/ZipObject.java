package cc.co.llabor.dav.zip;

import java.util.Date;
import java.util.zip.ZipEntry;

import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  07.10.2010::19:13:21<br> 
 */
public class ZipObject extends StoredObject {

	private ZipEntry zEntry;

	public ZipObject(ZipEntry e) {
		this.zEntry = e;
	}

	@Override
	public Date getCreationDate() {
		return new Date(zEntry.getTime());
	}

	@Override
	public Date getLastModified() {
		return new Date(zEntry.getTime());
	}

	@Override
	public long getResourceLength() {
		return zEntry.getSize() ;
	}

	@Override
	public boolean isFolder() {
		return zEntry.isDirectory();
	}

	@Override
	public boolean isNullResource() {
		return zEntry == null;
	}

	@Override
	public boolean isResource() {
		return zEntry != null;
	}

	@Override
	public void setCreationDate(Date c) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		super.setCreationDate(c);
		}
	}

	@Override
	public void setFolder(boolean f) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		super.setFolder(f);
		}
	}

	@Override
	public void setLastModified(Date d) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		super.setLastModified(d);
		}
	}

	@Override
	public void setNullResource(boolean f) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		super.setNullResource(f);
		}
	}

	@Override
	public void setResourceLength(long l) {
		// TODO Auto-generated method stub
		if (1==1)throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		super.setResourceLength(l);
		}
	}

}


 