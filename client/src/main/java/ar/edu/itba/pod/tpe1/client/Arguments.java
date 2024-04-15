package ar.edu.itba.pod.tpe1.client;

public class Arguments {
    private String ip = "localhost";
    private Integer port = 0;

    public String getIp() {
        return ip;
    }
    public Integer getPort() {
        return port;
    }

    public void parseServerAddress(String serverAddress) {
        String[] parts = serverAddress.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid server address");
        }
        //make regex to match localhost and or ip address
        if (!parts[0].matches("^(localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$")) {
            throw new IllegalArgumentException("Invalid IP address");
        }
        if (!parts[1].matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Invalid port");
        }

        this.ip = parts[0];
        this.port = Integer.parseInt(parts[1]);
    }

    public String getServerAddress() {
        return ip + ":" + port;
    }
    public void setServerAddress(String serverAddress) {
        parseServerAddress(serverAddress);
    }
}
