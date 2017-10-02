package qa.tools.ikeeper.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.tools.ikeeper.action.DoNothing;
import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.CacheClient;
import qa.tools.ikeeper.client.ITrackerClient;
import qa.tools.ikeeper.interceptor.DefaultInterceptor;
import qa.tools.ikeeper.interceptor.IKeeperInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

public class IKeeperConnector {

    private static final Logger LOG = LoggerFactory.getLogger(IKeeperConnector.class);

    protected static final String confPropFileName = "ikeeperConfiguration.properties";
    protected static final String envPropFileName = "ikeeperEnvironment.properties";

    protected ITrackerClient[] clients;
    protected IKeeperInterceptor interceptor;
    protected Map<String, String> environmentProperties = new HashMap<String, String>();

    protected static Map<String, String> configurationProperties = new HashMap<String, String>();
    protected static Map<String, String> permanentEnvironmentProperties = new HashMap<String, String>();

    protected static String testVersion;
    protected static List<String> versionsOrder = new ArrayList<String>();

    protected static IAction action = new DoNothing();

    private static Map<String, List<String>> projectStates = new HashMap<String, List<String>>();

    static {
        readConfigurationProperties();
        readEnvironmentProperties();
    }

    public IKeeperConnector(ITrackerClient... clients) {
        this(new DefaultInterceptor(loadProjectStates(Arrays.asList(clients))), clients);
    }

    public IKeeperConnector(IKeeperInterceptor interceptor, ITrackerClient... clients) {
        this.clients = clients;
        environmentProperties.putAll(permanentEnvironmentProperties);
        String ikeeperRun = configurationProperties.get("ikeeper.run");
        if (ikeeperRun == null) {
            ikeeperRun = "true";
        }
        this.interceptor = interceptor;
        interceptor.setEnabled(Boolean.valueOf(ikeeperRun));

    }

    public IKeeperConnector(String testVersion, ITrackerClient... clients) {
        this(new DefaultInterceptor(loadProjectStates(Arrays.asList(clients))), clients);
        if (testVersion != null) {
            IKeeperConnector.testVersion = testVersion;
        }
    }

    private static Map<String, List<String>> loadProjectStates(List<ITrackerClient> clients) {
        // load default project states
        List<ITrackerClient> fclients = new ArrayList<ITrackerClient>();
        for (ITrackerClient client : clients) {
            if (client instanceof CacheClient) {
                CacheClient cclient = (CacheClient) client;
                fclients.addAll(cclient.getClients());
            } else {
                fclients.add(client);
            }
        }
        for (ITrackerClient client : fclients) {
            if (!projectStates.containsKey(client.getName() + "@DEFAULT")) {
                projectStates.put(client.getName() + "@DEFAULT", client.getDefaultActionStates());
            }
        }

        return projectStates;
    }

    private static void readConfigurationProperties() {

        Properties confProps = new Properties();

        InputStream inputStream = IKeeperConnector.class.getClassLoader().getResourceAsStream(confPropFileName);

        if (inputStream != null) {
            try {
                confProps.load(inputStream);

                try {
                    String a = confProps.getProperty("action");
                    if (a != null) {
                        Class<?> c = Class.forName(a);
                        Object obj = c.newInstance();
                        if (obj instanceof IAction) {
                            action = (IAction) obj;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    LOG.error(e.getMessage());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    LOG.error(e.getMessage());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    LOG.error(e.getMessage());
                }

                String testVersion = confProps.getProperty("testVersion");
                if (testVersion != null && !testVersion.isEmpty()) {
                    IKeeperConnector.testVersion = testVersion;
                }

                String versions = confProps.getProperty("versions");
                if (versions != null && !versions.isEmpty()) {
                    for (String v : versions.split(",")) {
                        versionsOrder.add(v);
                    }
                }

                for (Object ko : confProps.keySet()) {
                    String key = (String) ko;
                    if (key.contains("@")) {
                        List<String> states = Arrays.asList(((String) confProps.get(key)).toUpperCase().split(","));
                        projectStates.put(key, states);
                    }
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        // read additional system properties

        String ikeeperRun = System.getProperty("ikeeper.run");
        if (ikeeperRun != null) {
            configurationProperties.put("ikeeper.run", ikeeperRun);
        }
    }

    private static void readEnvironmentProperties() {
        Properties envProps = new Properties();

        InputStream inputStream = IKeeperConnector.class.getClassLoader().getResourceAsStream(envPropFileName);

        if (inputStream != null) {
            try {
                envProps.load(inputStream);

                for (Entry<Object, Object> e : envProps.entrySet()) {
                    permanentEnvironmentProperties.put((String) e.getKey(), (String) e.getValue());
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    /**
     * These properties will be used in evaluating the issue constraints such as
     * container, database, jdk, et cetera.
     *
     * @param name
     * @param value
     */
    public void setEnvironmentProperty(String name, String value) {
        environmentProperties.put(name, value);
    }

    public void setEnvironmentProperties(Map<? extends String, ? extends String> envProps) {
        environmentProperties.putAll(envProps);
    }

    public static String getTestVersion() {
        return testVersion;
    }

    public static List<String> getVersionsOrder() {
        return versionsOrder;
    }
}
