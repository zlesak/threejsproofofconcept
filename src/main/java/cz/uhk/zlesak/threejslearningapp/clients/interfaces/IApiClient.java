package cz.uhk.zlesak.threejslearningapp.clients.interfaces;

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

}

