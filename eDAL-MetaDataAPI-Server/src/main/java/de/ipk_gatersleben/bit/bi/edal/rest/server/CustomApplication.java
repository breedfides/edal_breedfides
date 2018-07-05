package de.ipk_gatersleben.bit.bi.edal.rest.server;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class CustomApplication extends ResourceConfig {
	public CustomApplication() {
		packages("de.ipk_gatersleben.bit.bi.edal.rest.server");
		register(MultiPartFeature.class);
	}
}