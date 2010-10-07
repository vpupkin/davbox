package cc.co.llabor.dav;

import java.io.File;
import java.io.InputStream;
import java.security.Principal;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.dropbox.client.Authenticator;
import com.dropbox.client.DropboxClient; 
import com.dropbox.client.DropboxException;
import com.dropbox.client.Util;

import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;

/** 
 * <b>Description:TODO</b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 OXSEED AG <br>
 * <b>Company:</b>       OXSEED AG  <br>
 * 
 * Creation:  Oct 6, 2010::12:24:09 AM<br> 
 */
public class MyDav implements IWebdavStore {

    public static Map config ;
    public Authenticator auth = null;
    DropboxClient client = null;

  	
	
	private File file;

	public MyDav(File e){
		this.file = e;
		setUp(); 
	}
	
    public void setUp()  
    {
    	
        try {
        	
            InputStream inTmp = MyDav.class.getResourceAsStream("/config/testing.json");
			config = Authenticator.loadConfig(inTmp );
            auth = new Authenticator(config);
            String url = auth.retrieveRequestToken(null);
	    System.out.println("Url is: " + url);
            Util.authorizeForm(url, (String)config.get("testing_user"), (String)config.get("testing_password"));
            auth.retrieveAccessToken("");
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Total failure initializing the authenticator.";

        }    	
        assert auth != null : "Auth didn't get configured.";
        this.client = new DropboxClient(this.config, auth);
        //this.client.fileDelete("sandbox", "/tests", null);
    }
    
    
	public ITransaction begin(Principal principal) {
		// TODO Auto-generated method stub
		final Principal p = new Principal(){

			public String getName() {
				// TODO Auto-generated method stub
				if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
				else {
				return "user";
				}
			}};
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			return new ITransaction(){

				public Principal getPrincipal() {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					return p;
					}
				}};
		}
	}

	public void checkAuthentication(ITransaction transaction) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			// TODO check
		}
	}

	public void commit(ITransaction transaction) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			// TODO Auto-generated method stub
		}
	}

	public void createFolder(ITransaction transaction, String folderUri) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
		}
	}

	public void createResource(ITransaction transaction, String resourceUri) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
		}
	}

	public String[] getChildrenNames(ITransaction transaction, String folderUri) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet  implemented since Oct 6, 2010");
		else {
			return new String[]{"a","a5","a4","a3","a2"};
		}
	}

	public InputStream getResourceContent(ITransaction transaction,
			String resourceUri) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			return null;
		}
	}

	public long getResourceLength(ITransaction transaction, String path) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			return 0;
		}
	}

	public StoredObject getStoredObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			final File f = this.file;
			StoredObject s = new StoredObject(){

				@Override
				public Date getCreationDate() {
					 
					String root = file.getName();
					 
			        HttpResponse resp;
					try {
						resp = client.getFile("sandbox", "/"); 
				        assert resp != null : "Should get a valid response.";
				        int status = resp.getStatusLine().getStatusCode();	
				        assert status == 200 : "Should get valid status code:" + status;
				        HttpEntity cTmp = resp.getEntity();
				        System.out.println(cTmp);
					} catch (DropboxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				        
					return new Date();
					 
				}

				@Override
				public Date getLastModified() {
					 
					return new Date(); 
				}

				@Override
				public long getResourceLength() {
					return 111;
				}

				@Override
				public boolean isFolder() {
					return true;
				}

				@Override
				public boolean isNullResource() {
					return false;
				}

				@Override
				public boolean isResource() {
					return true;
				}

				@Override
				public void setCreationDate(Date c) {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					super.setCreationDate(c);
					}
				}

				@Override
				public void setFolder(boolean f) {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					super.setFolder(f);
					}
				}

				@Override
				public void setLastModified(Date d) {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					super.setLastModified(d);
					}
				}

				@Override
				public void setNullResource(boolean f) {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					super.setNullResource(f);
					}
				}

				@Override
				public void setResourceLength(long l) {
					// TODO Auto-generated method stub
					if (1==2)throw new RuntimeException("not yet implemented since Oct 6, 2010");
					else {
					super.setResourceLength(l);
					}
				}};
			return  s;
		}
	}

	public void removeObject(ITransaction transaction, String uri) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
		}
	}

	public void rollback(ITransaction transaction) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
		}
	}

	public long setResourceContent(ITransaction transaction,
			String resourceUri, InputStream content, String contentType,
			String characterEncoding) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 6, 2010");
		else {
			return 0;
		}
	}

}


 