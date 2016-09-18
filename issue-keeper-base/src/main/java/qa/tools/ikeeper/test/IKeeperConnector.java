package qa.tools.ikeeper.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.tools.ikeeper.IKeeperInterceptor;
import qa.tools.ikeeper.action.DoNothing;
import qa.tools.ikeeper.action.IAction;
import qa.tools.ikeeper.client.ITrackerClient;

public class IKeeperConnector {

    private static final Logger LOG = LoggerFactory.getLogger(IKeeperConnector.class);

    protected static final String confPropFileName = "ikeeperConfiguration.properties";
    protected static final String envPropFileName = "ikeeperEnvironment.properties";

    protected ITrackerClient[] clients;
    protected IKeeperInterceptor interceptor = new IKeeperInterceptor();
    protected Map<String, String> environmentProperties = new HashMap<String, String>();

    protected static Map<String, String> configurationProperties = new HashMap<String, String>();
    protected static Map<String, String> permanentEnvironmentProperties = new HashMap<String, String>();

    protected static String testVersion;
    protected static List<String> versionsOrder = new ArrayList<String>();

    protected static IAction action = new DoNothing();

    static {
        readConfigurationProperties();
        readEnvironmentProperties();
    }

    public IKeeperConnector(ITrackerClient... clients) {
        this(null, clients);
    }

    public IKeeperConnector(String testVersion, ITrackerClient... clients) {
        IKeeperConnector.testVersion = testVersion;
        this.clients = clients;
        environmentProperties.putAll(permanentEnvironmentProperties);
        interceptor.setEnabled(Boolean.valueOf(configurationProperties.getOrDefault("ikeeper.run", "true")));
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
