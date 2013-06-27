package de.bps.onyx.plugin.wsclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import de.bps.onyx.plugin.OnyxModule;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.6 in JDK 6 Generated source version: 2.1
 */
@WebServiceClient(name = "PluginService", targetNamespace = "http://server.webservice.plugin.bps.de/")
public class PluginService extends Service {

	private final static URL PLUGINSERVICE_WSDL_LOCATION;
	private final static Logger logger = Logger.getLogger(de.bps.onyx.plugin.wsclient.PluginService.class.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = de.bps.onyx.plugin.wsclient.PluginService.class.getResource(".");
			url = new URL(baseUrl, OnyxModule.getPluginWSLocation() + "?wsdl");
		} catch (final MalformedURLException e) {
			logger.warning("Failed to create URL for the wsdl Location: '" + OnyxModule.getPluginWSLocation() + "?wsdl', retrying as a local file");
			logger.warning(e.getMessage());
		}
		PLUGINSERVICE_WSDL_LOCATION = url;
	}

	public PluginService(final URL wsdlLocation, final QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public PluginService() {
		super(PLUGINSERVICE_WSDL_LOCATION, new QName("http://server.webservice.plugin.bps.de/", "PluginService"));
	}

	/**
	 * @return returns OnyxPluginServices
	 */
	@WebEndpoint(name = "OnyxPluginServicesPort")
	public OnyxPluginServices getOnyxPluginServicesPort() {
		return super.getPort(new QName("http://server.webservice.plugin.bps.de/", "OnyxPluginServicesPort"), OnyxPluginServices.class);
	}

	/**
	 * @param features A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy. Supported features not in the <code>features</code> parameter will have
	 *            their default values.
	 * @return returns OnyxPluginServices
	 */
	@WebEndpoint(name = "OnyxPluginServicesPort")
	public OnyxPluginServices getOnyxPluginServicesPort(final WebServiceFeature... features) {
		return super.getPort(new QName("http://server.webservice.plugin.bps.de/", "OnyxPluginServicesPort"), OnyxPluginServices.class, features);
	}

}
