package cc.co.llabor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class JdavServlet extends net.sf.webdav.WebdavServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		super.doGet(  req,   resp); 
	}
}
