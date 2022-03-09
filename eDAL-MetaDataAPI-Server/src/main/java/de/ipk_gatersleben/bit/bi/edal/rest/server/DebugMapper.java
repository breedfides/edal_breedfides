package de.ipk_gatersleben.bit.bi.edal.rest.server;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DebugMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable t) {
    	System.err.println(t.toString());
        t.printStackTrace();
        return Response.serverError()
            .entity(t.toString())
            .build();
    }
}