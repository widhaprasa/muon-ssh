package muon.app.ui.components.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SessionInfo extends NamedItem implements Serializable {
    private String host, user, localFolder, remoteFolder;
    private int port = 22;
    private List<String> favouriteRemoteFolders = new ArrayList<>();
    private List<String> favouriteLocalFolders = new ArrayList<>();
    private String privateKeyFile;
    private int proxyPort = 8080;
    private String proxyHost, proxyUser, proxyPassword;
    private int proxyType = 0;
    private boolean useJumpHosts = false;
    private JumpType jumpType = JumpType.TcpForwarding;
    private List<HopEntry> jumpHosts = new ArrayList<>();
    private List<PortForwardingRule> portForwardingRules = new ArrayList<>();

    private String password;

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the localFolder
     */
    public String getLocalFolder() {
        return localFolder;
    }

    /**
     * @param localFolder the localFolder to set
     */
    public void setLocalFolder(String localFolder) {
        this.localFolder = localFolder;
    }

    /**
     * @return the remoteFolder
     */
    public String getRemoteFolder() {
        return remoteFolder;
    }

    /**
     * @param remoteFolder the remoteFolder to set
     */
    public void setRemoteFolder(String remoteFolder) {
        this.remoteFolder = remoteFolder;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the favouriteFolders
     */
    public List<String> getFavouriteRemoteFolders() {
        return favouriteRemoteFolders;
    }

    /**
     * @param favouriteFolders the favouriteFolders to set
     */
    public void setFavouriteRemoteFolders(List<String> favouriteFolders) {
        this.favouriteRemoteFolders = favouriteFolders;
    }

    /**
     * @return the privateKeyFile
     */
    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    /**
     * @param privateKeyFile the privateKeyFile to set
     */
    public void setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
    }

    public SessionInfo copy() {
        SessionInfo info = new SessionInfo();
        info.setId(UUID.randomUUID().toString());
        info.setHost(this.host);
        info.setPort(this.port);
        info.getFavouriteRemoteFolders().addAll(favouriteRemoteFolders);
        info.getFavouriteLocalFolders().addAll(favouriteLocalFolders);
        info.setLocalFolder(this.localFolder);
        info.setRemoteFolder(this.remoteFolder);
        info.setPassword(this.password);
        info.setPrivateKeyFile(privateKeyFile);
        info.setUser(user);
        info.setName(name);
        return info;
    }

    public static SessionInfo fromURI(URI uri) {

        if ("m2mrem".equals(uri.getScheme())) {
            SessionInfo info = new SessionInfo();

            // Parse host and port
            String host = uri.getHost();
            int port = uri.getPort();
            if (port == -1) {
                port = 22;
            }
            String name = host + ':' + port;
            info.setName(name);
            info.setHost(host);
            info.setPort(port);

            // Parse query
            String query = uri.getQuery();
            Map<String, String> queryMap = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] token = query.split("&");
                for (String t : token) {
                    int i = t.indexOf("=");
                    queryMap.put(URLDecoder.decode(t.substring(0, i), StandardCharsets.UTF_8),
                            URLDecoder.decode(t.substring(i + 1), StandardCharsets.UTF_8));
                }
            }

            // Parse username and password
            if (queryMap.containsKey("u")) {
                String b = queryMap.get("u");
                String user = null;
                try {
                    user = new String(Base64.getDecoder().decode(b));
                } catch (IllegalArgumentException ignored) {
                }

                if (user != null) {
                    info.setUser(user);
                    if (queryMap.containsKey("p")) {
                        b = queryMap.get("p");
                        try {
                            String password = new String(Base64.getDecoder().decode(b));
                            info.setPassword(password);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
            return info;
        }
        return null;
    }

    /**
     * @return the favouriteLocalFolders
     */
    public List<String> getFavouriteLocalFolders() {
        return favouriteLocalFolders;
    }

    /**
     * @param favouriteLocalFolders the favouriteLocalFolders to set
     */
    public void setFavouriteLocalFolders(List<String> favouriteLocalFolders) {
        this.favouriteLocalFolders = favouriteLocalFolders;
    }

    /**
     * @return the proxyPort
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort the proxyPort to set
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return the proxyHost
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost the proxyHost to set
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return the proxyUser
     */
    public String getProxyUser() {
        return proxyUser;
    }

    /**
     * @param proxyUser the proxyUser to set
     */
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    /**
     * @return the proxyPassword
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * @param proxyPassword the proxyPassword to set
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * @return the proxyType
     */
    public int getProxyType() {
        return proxyType;
    }

    /**
     * @param proxyType the proxyType to set
     */
    public void setProxyType(int proxyType) {
        this.proxyType = proxyType;
    }

    public boolean isUseJumpHosts() {
        return useJumpHosts;
    }

    public void setUseJumpHosts(boolean useJumpHosts) {
        this.useJumpHosts = useJumpHosts;
    }

    public JumpType getJumpType() {
        return jumpType;
    }

    public void setJumpType(JumpType jumpType) {
        this.jumpType = jumpType;
    }

    public List<HopEntry> getJumpHosts() {
        return jumpHosts;
    }

    public void setJumpHosts(List<HopEntry> jumpHosts) {
        this.jumpHosts = jumpHosts;
    }

    public List<PortForwardingRule> getPortForwardingRules() {
        return portForwardingRules;
    }

    public void setPortForwardingRules(List<PortForwardingRule> portForwardingRules) {
        this.portForwardingRules = portForwardingRules;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionInfo that = (SessionInfo) o;
        return Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, user, localFolder, remoteFolder, port, favouriteRemoteFolders, favouriteLocalFolders, privateKeyFile, proxyPort, proxyHost, proxyUser, proxyPassword, proxyType, useJumpHosts, jumpType, jumpHosts, portForwardingRules, password);
    }

    public enum JumpType {
        TcpForwarding, PortForwarding
    }
}
