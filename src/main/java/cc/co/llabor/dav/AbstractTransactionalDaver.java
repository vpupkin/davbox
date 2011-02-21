package cc.co.llabor.dav;

/** 
 * <b>Description:TODO</b>
 * @author      vipup<br>
 * <br>
 * <b>Copyright:</b>     Copyright (c) 2006-2008 Monster AG <br>
 * <b>Company:</b>       Monster AG  <br>
 * 
 * Creation:  10.11.2010::19:15:06<br> 
 */
import java.security.Principal;


import net.sf.webdav.ITransaction; 
import net.sf.webdav.IWebdavStore;

public abstract class AbstractTransactionalDaver implements IWebdavStore{
	MyTransaction transaction;
	public AbstractTransactionalDaver() {
		super();
	}

	public void checkAuthentication(ITransaction transaction) {
		//TODO ok 
	}

	
	public final ITransaction begin(Principal principal) {
		if (principal == null) return null; // no principal - no transaction
		if (this.transaction  == null){
			//setUp();
			this.transaction = new MyTransaction(principal);
		} 			
		else
			throw new RuntimeException("started transaction is not finished!");
		return transaction; 
	}

	public final void commit(ITransaction transaction) {
		tearDown(); 
		this.transaction = null; 
	}

	protected abstract  void tearDown() ;

	/**
	 * @author vipup
	 * @param transaction
	 */
	protected final void checkTR(ITransaction transaction) {
		if (transaction == null) return; // TODO for read-only is not a problem
		if (this.transaction != transaction) throw new RuntimeException("started transaction is not the same with given!" +this.transaction +"!="+transaction );
	}

	public final void rollback(ITransaction transaction) {
		tearDown();
		this.transaction = null; 
		
	}

}
