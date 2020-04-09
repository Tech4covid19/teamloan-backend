package pt.teamloan.filters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logmanager.Logger;

import com.google.common.base.Strings;

@Provider
public class HttpLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private static final Logger LOGGER = Logger.getLogger(HttpLoggingFilter.class.getName());
	private static final String LOG_REQUEST_FORMAT = "Request: {0} {1}\nHeaders: {2}\nPayload: {3}";
	private static final String LOG_RESPONSE_FORMAT = "Response: {0} {1} | Status: {2}\nHeaders: {3}\n Payload: {4}";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String method = requestContext.getMethod();
		String uri = requestContext.getUriInfo().getRequestUri().toString();
		String headers = requestContext.getHeaders().toString();
		String body = getRequestBody(requestContext);
		LOGGER.log(Level.INFO, LOG_REQUEST_FORMAT, new Object[] { method, uri, headers, body });

	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String method = requestContext.getMethod();
		String uri = requestContext.getUriInfo().getRequestUri().toString();
		String headers = responseContext.getHeaders().toString();
		String statusCode = String.valueOf(responseContext.getStatus());
		String responseBody = "";
		if (responseContext.getEntity() != null) {
			responseBody = responseContext.getEntity().toString();
		}
		LOGGER.log(Level.INFO, LOG_RESPONSE_FORMAT, new Object[] { method, uri, statusCode, headers, responseBody });
	}

	public String getRequestBody(ContainerRequestContext requestContext) throws IOException {
		String payload = "";
		if (requestContext.getEntityStream() != null) {
			payload = IOUtils.toString(requestContext.getEntityStream(), Charset.defaultCharset());

			InputStream in = IOUtils.toInputStream(payload, Charset.defaultCharset());
			requestContext.setEntityStream(in);
		}

		payload = removePasswordAttributesForLog(payload);
		return payload;
	}

	private String removePasswordAttributesForLog(String responseString) {
		return responseString.replaceAll("\"password\"( ?+)+:( ?+)+\"(.+?)\"", "\"password\":\"******\"");
	}
}
