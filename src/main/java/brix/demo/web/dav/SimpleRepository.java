package brix.demo.web.dav;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

/** 
 * <b>Description:TODO</b>
 * @author      gennady<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 OXSEED AG <br>
 * <b>Company:</b>       OXSEED AG  <br>
 * 
 * Creation:  Oct 4, 2010::9:32:07 PM<br> 
 */
public class SimpleRepository implements Repository {

	public String getDescriptor(String arg0) {
		// TODO Auto-generated method stub
		if (1 == 2)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return "yes";
		}
	}

	public String[] getDescriptorKeys() {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public Value getDescriptorValue(String arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public Value[] getDescriptorValues(String arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public boolean isSingleValueDescriptor(String arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return false;
		}
	}

	public boolean isStandardDescriptor(String arg0) {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return false;
		}
	}

	public Session login() throws LoginException, RepositoryException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public Session login(Credentials arg0) throws LoginException,
			RepositoryException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public Session login(String arg0) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

	public Session login(Credentials arg0, String arg1) throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		// TODO Auto-generated method stub
		if (1 == 1)
			throw new RuntimeException("not yet implemented since Oct 4, 2010");
		else {
			return null;
		}
	}

}


 