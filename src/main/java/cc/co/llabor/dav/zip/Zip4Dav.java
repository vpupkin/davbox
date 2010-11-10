package cc.co.llabor.dav.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File; 
import java.io.IOException;
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.Date; 
import java.util.List;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import cc.co.llabor.dav.AbstractTransactionalDaver;


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
public class Zip4Dav extends AbstractTransactionalDaver implements IWebdavStore {
	 
	private File file; 

	public void createFolder(ITransaction transaction, String folderUri) {
 
		try {
			ZipInputStream in = getInZip();
			for ( ZipEntry e = in .getNextEntry();e != null ; e = in.getNextEntry()){
				if (e.getName() == folderUri ) return;
			}
			
			ZipEntry e = new ZipEntry(folderUri) ;
			
			// TODO Crete ////////////////////////
			this.out.putNextEntry(e );this.out.closeEntry();
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
			out.putNextEntry(newE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		checkTR(transaction);
		if (!resourceUri.equals(newE.getName())) throw new RuntimeException("new Entry is not created! try to create first the entrry with name " +resourceUri );
		newE.setComment(contentType  +" ,"+ characterEncoding);
		try {
			int lenTmp = 0;
 			for ( int nextTmp = content.read( ); nextTmp != -1 ;	nextTmp = content.read( )){
 				out. write(nextTmp);
 				lenTmp++;
 			}
 			out.closeEntry();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newE.getSize();
	}	
	
	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		List<String> childList = new ArrayList<String>();
		ZipInputStream in = getInZip(); 
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
		ZipInputStream in = getInZip();

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
	


	public long getResourceLength(ITransaction transaction, String path) {
		checkTR(transaction); 
		ZipInputStream in = getInZip();

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
		ZipInputStream in = getInZip();
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

	public Zip4Dav(File e){
		this.file = e;
		setUp(); 
	}
	ByteArrayOutputStream bout  ;
	ZipOutputStream out  ; 
	
	private void setUp() {
		 bout = new ByteArrayOutputStream();
		 out =  new ZipOutputStream(bout );  
	}

	/**
	 * @author vipup
	 * @throws IOException 
	 */
	private ZipInputStream getInZip() {
		ZipInputStream zipTmp  = null; // new FileInputStream(this.file);
		if (this.bout.toByteArray().length >0 ){
			try{
				// close LOCAL_OUT && fin to BA
				/*out.flush();out.close();*/bout.flush();bout.close();
				final byte[] baTmp = bout.toByteArray();
				final ByteArrayInputStream baInTmp = new ByteArrayInputStream (baTmp);
				zipTmp = new ZipInputStream(baInTmp);
				// init LOCAL_OUT
				setUp();
				zipTmp = copyZip(zipTmp);				
			}catch (Exception e) {
				e.printStackTrace( );
			}
		}else{
			// check stored Data
			//zipTmp= this.bout.toByteArray().length > 0?new ByteArrayInputStream (this.bout.toByteArray()):zipTmp;
			// In As FullPath   
			//zipTmp  = zipTmp ==null? new ZipInputStream( this.getClass().getClassLoader().getResourceAsStream(this.file.getAbsolutePath()  )):zipTmp;////this.file.getPath()   +
			// In As Name		
			zipTmp  = zipTmp ==null? new ZipInputStream( this.getClass().getClassLoader().getResourceAsStream(this.file.getName()   )):zipTmp;////this.file.getPath()   +
			// sync with LOCAL_OUT
			try {
				zipTmp = copyZip(zipTmp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return zipTmp;
		
	}
	// copy zipIn to zipIn && zipOut
	private ZipInputStream copyZip(ZipInputStream zipPar) throws IOException {
		// copy full zip
		ByteArrayOutputStream boutTmp = new ByteArrayOutputStream();
		ZipOutputStream outTmp = new ZipOutputStream(boutTmp ); 
		 
		for(ZipEntry zeTmp = zipPar.getNextEntry();zeTmp != null; zeTmp = zipPar.getNextEntry() )
		try{
			ZipEntry newZE = new ZipEntry(zeTmp.getName());
			byte b[] = new byte[(int)zeTmp.getSize()];
			zipPar.read(b); 
			// put to TMP
			outTmp.putNextEntry(newZE);
			outTmp.write(b);
			outTmp.closeEntry();
			// cc to LOCAL_OUT 
			this.out.putNextEntry(newZE);
			this.out.write(b);
			this.out.closeEntry();
		}catch(Exception e){e.printStackTrace();}
		outTmp.close();
		zipPar.close();
		
		final byte[] baTnp = boutTmp.toByteArray();
		final ByteArrayInputStream baIntmp = new ByteArrayInputStream (baTnp);
		zipPar = new ZipInputStream(baIntmp);
		return zipPar;
	}
	
	protected void tearDown() { 
		// TODO
	}
}


 