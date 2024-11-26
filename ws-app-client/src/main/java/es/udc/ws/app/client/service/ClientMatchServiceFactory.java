package es.udc.ws.app.client.service;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

import java.lang.reflect.InvocationTargetException;

public class ClientMatchServiceFactory {

    private final static String CLASS_NAME_PARAMETER
            = "ClientMatchServiceFactory.className";
    private static Class<ClientMatchService> serviceClass = null;

    private ClientMatchServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<ClientMatchService> getServiceClass() {

        if (serviceClass == null) {
            try {
                String serviceClassName = ConfigurationParametersManager
                        .getParameter(CLASS_NAME_PARAMETER);
                serviceClass = (Class<ClientMatchService>) Class.forName(serviceClassName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return serviceClass;

    }

    public static ClientMatchService getService() {

        try {
            return (ClientMatchService) getServiceClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
