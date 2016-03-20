package com.cdi.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sample.cdi.IAnotherService;	
import com.sample.cdi.ICommon;
import com.sample.cdi.support.ApplicationService;
import com.sample.cdi.support.LookupHelper;
import com.sample.cdi.support.Version;

/**
 * Servlet implementation class CDITestServlet
 */
@WebServlet("/CDITestServlet")
public class CDITestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
	/****
	 * Test cases.
	 * 
	 * When current version is set to Version.PRODUCTION :
	 * standard --> FirstBean;  Explicitely required in injection phase
	 * specific --> SecondBean; SecondBean is annotated with @ApplicationService(Version.PRODUCTION), 
	 * 							 so matches.
	 * 
	 * When current version is set to Version.TEST:
	 * 
	 * standard --> FirstBean;  Explicitely required in injection phase
	 * specific --> FirstBean;  SecondBean is annotated with @ApplicationService(Version.PRODUCTION), 
	 * 							so it doesn't match and BASE implementation is injected.
	 * 
	 * 
	 */
	
	@Inject @ApplicationService(Version.BASE)
	ICommon standard;
	
	@Inject @ApplicationService
	ICommon specific;
	
	@Inject @ApplicationService(Version.PRODUCTION)
	ICommon production;
	
	@Inject @ApplicationService
	IAnotherService nested;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CDITestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("\n Response (Default)     			= "+standard.getGreetings());
		response.getWriter().append("\n Response (Production)  			= "+production.getGreetings());
		response.getWriter().append("\n Response (Enviroment specific)  = "+specific.getGreetings());
		response.getWriter().append("\n Response (Nested bean specific)  = "+nested.callNestedEJB());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	static {
		LookupHelper.setCurrentVersion(Version.PRODUCTION);
	}

}
