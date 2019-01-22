package cn.jinjing.inter.util;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private static int UPDATE_MS = Integer.parseInt(ConfigUtil.getProperties("limit_update_ms"));
	
	/**
	 * Destruction of the servlet. <br>
	 */
	@Override
	public void destroy()
	{
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	@Override
	public void init() throws ServletException
	{
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new UpdateLimit(), 0, UPDATE_MS, TimeUnit.SECONDS);
	}
}
