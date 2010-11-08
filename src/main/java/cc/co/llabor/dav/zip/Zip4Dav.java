package cc.co.llabor.dav.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  07.10.2010::18:18:04<br> 
 */
public class Zip4Dav implements IWebdavStore {
	
	ZipFile f = null;
	private File file;
	private MyTransaction transaction;
	{
		try {
			f = new ZipFile("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ITransaction begin(Principal principal) {
		if (principal == null) return null; // no principal - no transaction
		if (this.transaction  == null){
			setUp();
			this.transaction = new MyTransaction(principal);
		} 			
		else
			throw new RuntimeException("started transaction is not finished!");
		return transaction; 
	}

	public void checkAuthentication(ITransaction transaction) {
		// ok 
	}

	public void commit(ITransaction transaction) {
		tearDown();
		this.transaction  = null;
		// ok 
	}



	public void createFolder(ITransaction transaction, String folderUri) {
 
		try {
			for ( ZipEntry e = in.getNextEntry();e != null ; e = in.getNextEntry()){
				if (e.getName() == folderUri ) return;
			}
			// TODO Crete ////////////////////////
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// active Entry for current transaction  - the only one entry at the time can be processed
	ZipEntry newE = null;
	public void createResource(ITransaction transaction, String resourceUri) {
		checkTR(transaction);
		newE = new ZipEntry(resourceUri);
		try {
			out.closeEntry();
			
			out.putNextEntry(newE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	/**
	 * @author vipup
	 * @param transaction
	 */
	private void checkTR(ITransaction transaction) {
		if (transaction == null) return; // TODO for read-only is not a problem
		if (this.transaction != transaction) throw new RuntimeException("started transaction is not the same with given!" +this.transaction +"!="+transaction );
	}

	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		List<String> childList = new ArrayList<String>();
		resIn = getInZip();
		in = new ZipInputStream(resIn);
		int level = 0;
		try {
			for (ZipEntry e = in.getNextEntry(); e != null; e = in
					.getNextEntry()) {
				String itemName = "/"+ e.getName();
				if (itemName.indexOf(folderUri)<0)continue;
				int suffixStart = folderUri.length() ;
				String suffix = itemName.substring( suffixStart );
				suffix = suffix.indexOf("/")==0?suffix.substring(1):suffix;
				suffix = suffix.indexOf("/")>0?suffix.substring(0,suffix.indexOf("/")):suffix;
				 
				if (!childList.contains(suffix )){
					childList.add(suffix);
				}
				if (1==2){
								try {
									suffix = itemName.substring(folderUri.length());
								}catch(Exception e1){}
								if (itemName.startsWith(folderUri) && suffix.indexOf("/")<0) {
									childList.add(itemName);
									System.out.println("CCCCCCCCCCCCILD:"+itemName);
								}else{
									String dir = "/"+suffix.substring(0, suffix.indexOf("/") );
									if (childList.indexOf(dir) < 0){
										childList.add(dir);
										System.out.println("DDDDDDDDDDDDILD:"+dir);
									}
									
									
								}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return childList.toArray(new String[]{});
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		checkTR(transaction);
		resIn = getInZip();
		in = new ZipInputStream(resIn);

		try {
			for (ZipEntry e = in.getNextEntry(); e != null; e = in
					.getNextEntry()) {
				String itemName = "/"+e.getName();
				if (itemName.equals( (resourceUri) ) ) {
					return in;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	
	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		checkTR(transaction);
		if (!resourceUri.equals(newE.getName())) throw new RuntimeException("new Entry is not created! try to create first the entrry with name " +resourceUri );
		newE.setComment(contentType  +" ,"+ characterEncoding);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] extra;
		try {
			extra = new byte[content.available()];
			content.read(extra ) ;			
			newE.setExtra(extra );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newE.getSize();
	}	

	public long getResourceLength(ITransaction transaction, String path) {
		checkTR(transaction);
		resIn = getInZip();
		in = new ZipInputStream(resIn);

		try {
			for (ZipEntry e = in.getNextEntry(); e != null; e = in
					.getNextEntry()) {
				String itemName = e.getName();
				if (itemName.equals( (path) ) ) {
					return e.getSize();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) {
		checkTR(transaction);
		resIn = getInZip();
		in = new ZipInputStream(resIn);
		if ("/".equals(uri)){
			StoredObject rootTmp =  new StoredObject() ;
			rootTmp .setFolder(true);
			rootTmp .setLastModified( new Date() );
			rootTmp .setCreationDate( new Date() ); 
			return rootTmp ;
		}
		
		StoredObject retval = null;
		try {
			for (ZipEntry e = in.getNextEntry(); e != null; e = in.getNextEntry()) {
				String itemName = e.getName();
				itemName = "/"+itemName;
				if (itemName.equals(uri)){
					retval = new ZipObject(e);
					break;
				}
				int sPosTmp =itemName.indexOf(uri);
				if (sPosTmp == -1) continue;
				String suffixTmp = itemName.substring(sPosTmp);
				if (suffixTmp.length() >0 ) { // dir!
					retval = new StoredObject( );
					retval .setFolder(true);
					retval .setLastModified( new Date() );
					retval .setCreationDate( new Date() ); 					
					break;
				}else{
					System.out.println("ignred >"+itemName);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;	
	}

 

	public void removeObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since 07.10.2010");
		else {
		}
	}

	public void rollback(ITransaction transaction) {
		// TODO same with tearDown
		try {
			in.close(); 
			in = null;
			out.close();
			// TODO store Zip ???
			out = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public Zip4Dav(File e){
		this.file = e;
		setUp(); 
	}
	OutputStream bout  ;
	ZipOutputStream out  ;
	InputStream resIn ;
	ZipInputStream in ;
	
	private void setUp() {
		 bout = new ByteArrayOutputStream();
		 out =  new ZipOutputStream(bout );
		 resIn =  getInZip();
		 in =  new ZipInputStream( resIn   );
	}

	/**
	 * @author vipup
	 */
	private InputStream getInZip() {
		InputStream retval  = this.getClass().getClassLoader().getResourceAsStream("gaevfs-0.3.zip");////this.file.getPath()   +
		return retval; 
	}
	private void tearDown() {
		try {
			in.close(); 
			in = null;
			out.close();
			// TODO store Zip 
			out = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}


 