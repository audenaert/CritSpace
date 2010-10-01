/* Created on       Aug 17, 2010
 * Last Modified on $Date: $
 * $Revision: $
 * $Log: $
 *
 * Copyright Neal Audenaert
 *
 * ALL RIGHTS RESERVED. 
 */
package org.idch.critspace.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.idch.critspace.persist.CritspaceRepository;
import org.idch.persist.RepositoryAccessException;
import org.idch.util.LogService;

@SuppressWarnings("serial")
public class CritspaceServlet extends HttpServlet {
    
    private static final String LOGGER = CritspaceServlet.class.getName();
    
    public static final int BAD_REQ        = HttpServletResponse.SC_BAD_REQUEST;
    public static final int NOT_FOUND      = HttpServletResponse.SC_NOT_FOUND;
    public static final int INTERNAL_ERROR = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    public static final int NOT_ALLOWED    = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
    public static final int CONFLICT       = HttpServletResponse.SC_CONFLICT;
    
    public static final int CREATED        = HttpServletResponse.SC_CREATED;
    public static final int OK             = HttpServletResponse.SC_OK;

    protected static CritspaceRepository s_repo;
    
    /**
     * 
     */
    public void init() throws ServletException {
        if (s_repo != null)
            return;

        try {
            s_repo = CritspaceRepository.get();
            if (!s_repo.probe()) {
                // try to create the repository, if we can't find it.
                LogService.logWarn("Critspace repository not yet initialized. " +
                        "Trying to create it now.", LOGGER);
                
                if (!s_repo.create()) {
                    s_repo = null;
                    String msg = "Could not initialize CritspaceRepository database.";
                    LogService.logError(msg, LOGGER);
                    throw new ServletException(msg);
                }
            }
        } catch (RepositoryAccessException rae) {
            String errmsg = "Could not load CritspaceRepository.";
            throw new ServletException(errmsg, rae);
        } 
    }
    
    /**
     * 
     * @param strId
     * @param resp
     * @return
     * @throws IOException
     */
    protected long getId(String strId, HttpServletResponse resp)
            throws IOException {
        try { 
            return Long.parseLong(strId);
        } catch (NumberFormatException nfe) {
            String errmsg = "Invalid id supplied (" + strId + ").";
            resp.sendError(BAD_REQ, warn(errmsg, nfe));
            throw nfe;
        }
    }
    
    protected String error(String msg, Throwable e) {
        if (e == null) {
            LogService.logError(msg, LOGGER);
        } else {
            LogService.logError(msg, LOGGER, e);
            msg += " Message: " + e.getMessage();
        }
        
        return msg;
    }
    
    protected String warn(String msg, Throwable e) {
        if (e == null) {
            LogService.logWarn(msg, LOGGER);
        } else {
            LogService.logWarn(msg, LOGGER, e);
            msg += " Message: " + e.getMessage();
        }
        
        return msg;
    }
}
