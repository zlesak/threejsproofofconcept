package cz.uhk.zlesak.threejslearningapp.application.clients.interfaces;


/**
 * Interface for API client configuration.
 * Provides a method to get the base URL for API requests.
 * The base URL is determined based on whether the application is running with Hotswap Agent.
 * If Hotswap Agent is detected, it returns a local URL; otherwise, it returns
 * a URL pointing to the backend service.
 */
public interface IApiClient {
    static String getBaseUrl() {
        boolean isHotswap = java.lang.management.ManagementFactory.getRuntimeMXBean()
                .getInputArguments().stream()
                .anyMatch(arg -> arg.contains("hotswap-agent.jar"));
        if (isHotswap) {
            return "http://localhost:8080/api/";
        }
        return "http://kotlin-backend:8080/api/";
    }

    static  String getLocalBaseBeUrl() {
        return "http://localhost:8080/api/";
    }

}

