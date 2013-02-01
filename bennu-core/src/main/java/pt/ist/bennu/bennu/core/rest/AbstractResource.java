package pt.ist.bennu.bennu.core.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.bennu.bennu.core.rest.serializer.Deserializer;
import pt.ist.bennu.bennu.core.rest.serializer.JsonSerializer;
import pt.ist.bennu.bennu.core.rest.serializer.Serializer;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.security.Authenticate;
import pt.ist.bennu.core.security.UserView;
import pt.ist.bennu.core.util.ConfigurationManager;
import pt.ist.bennu.core.util.ConfigurationManager.CasConfig;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

public abstract class AbstractResource {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);

	private static Serializer SERIALIZER;
	private static Deserializer DESERIALIZER;

	static {
		setSerializer(new JsonSerializer());

	}

	@Context
	HttpServletRequest request;

	protected User login(String username, String password, boolean checkPassword) {
		return Authenticate.login(request.getSession(true), username, password, checkPassword);
	}

	protected CasConfig getCasConfig() {
		if (request != null) {
			return ConfigurationManager.getCasConfig(request.getServerName());
		} else {
			return null;
		}
	}

	protected CasConfigContext getCasConfigContext() {
		return new CasConfigContext(getCasConfig(), request);
	}

	public class CasConfigContext {

		private final CasConfig casConfig;
		private final HttpServletRequest request;

		public CasConfigContext(CasConfig casConfig, HttpServletRequest request) {
			this.casConfig = casConfig;
			this.request = request;
		}

		public CasConfig getCasConfig() {
			return casConfig;
		}

		public HttpServletRequest getRequest() {
			return request;
		}
	}

	protected boolean isCasEnabled() {
		CasConfig casConfig = getCasConfig();
		if (casConfig != null) {
			return casConfig.isCasEnabled();
		} else {
			return false;
		}
	}

	protected <T extends DomainObject> T readDomainObject(final String externalId) {
		boolean error = false;
		try {
			T obj = AbstractDomainObject.fromExternalId(externalId);
			if (obj == null) {
				error = true;
			} else {
				return obj;
			}
		} catch (Throwable t) {
			error = true;
		} finally {
			if (error) {
				throw new RestException(BennuRestError.RESOURCE_NOT_FOUND);
			}
		}
		LOG.error("Unreachable code");
		return null;
	}

	protected User verifyAndGetRequestAuthor() {
		User user = UserView.getUser();
		if (user == null) {
			throw new RestException(BennuRestError.UNAUTHORIZED);
		} else {
			return user;
		}
	}

	protected static void setSerializer(Serializer serializer) {
		serializer.setDelegate(SERIALIZER);
		SERIALIZER = serializer;
	}

	protected static void setDeserializer(Deserializer delegate) {
		delegate.setDelegate(DESERIALIZER);
		DESERIALIZER = delegate;
	}

	protected String serialize(Object object) {
		return SERIALIZER.serialize(object);
	}

	protected String serialize(Object object, String collectionKey) {
		return SERIALIZER.serialize(object, collectionKey);
	}

	protected String serializeFromExternalId(String externalId) {
		return SERIALIZER.serialize(readDomainObject(externalId));
	}

	protected <T> T deserialize(String jsonString, Class<T> type) {
		return DESERIALIZER.deserialize(jsonString, type);
	}

	protected <T> T deserialize(String jsonString, Class<T> type, String externalId) {
		return DESERIALIZER.deserialize(jsonString, type, externalId);
	}

	protected <T extends DomainObject> URI getURIFor(T domainObject, String relativePath) {
		try {
			return new URI("http", getHost(), relativePath + "/" + domainObject.getExternalId(), null);
		} catch (URISyntaxException e) {
			throw new RestException(BennuRestError.CANNOT_CREATE_ENTITY);
		}
	}

	protected String getHost() {
		return request.getServerName();
	}

}