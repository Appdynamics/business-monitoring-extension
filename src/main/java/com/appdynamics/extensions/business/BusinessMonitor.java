package com.appdynamics.extensions.business;

import com.appdynamics.extensions.PathResolver;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Created by kiran.gangadharappa on 9/17/15.
 */
public class BusinessMonitor extends AManagedMonitor  {

    public static final String CONFIG_ARG = "config-file";
    public static final String METRIC_SEPARATOR = "|";
    private static final Logger logger = Logger.getLogger(BusinessMonitor.class);

    private final static ConfigUtil<Configuration> configUtil = new ConfigUtil<Configuration>();

    public BusinessMonitor() throws Exception {
        String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
        logger.info(msg);
        System.out.println(msg);
        CommerceServer.go();
    }

    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if (taskArguments != null) {
            logger.info("Starting BusinessMonitor Monitoring Task");
            if (logger.isDebugEnabled()) {
                logger.debug("Task Arguments Passed ::" + taskArguments);
            }
            String configFilename = getConfigFilename(taskArguments.get(CONFIG_ARG));
            try {
                Configuration config = configUtil.readConfig(configFilename, Configuration.class);
                List<BusinessMetrics> metrics = collectMetrics(config);
                printStats(config, metrics);
                logger.info("BusinessMonitor Monitoring Task completed");
                return new TaskOutput("BusinessMonitor Monitoring Task completed");
            } catch (FileNotFoundException e) {
                logger.error("Config file not found :: " + configFilename, e);
            } catch (Exception e) {
                logger.error("Metrics collection failed", e);
            }
        }
        throw new TaskExecutionException("BusinessMonitor monitoring task completed with failures.");
    }

    private List<BusinessMetrics> collectMetrics(Configuration config) {
        List<BusinessMetrics> metrics = Lists.newArrayList();
        if (config != null && config.getServers() != null) {
            for (Server server : config.getServers()) {
                BusinessMonitorTask monitorTask = new BusinessMonitorTask(server);
                metrics.add(monitorTask.gatherMetricsForAServer());
            }
        }
        return metrics;
    }

    private void printStats(Configuration config, List<BusinessMetrics> metrics) {
        for (BusinessMetrics redisMetrics : metrics) {
            StringBuilder metricPath = new StringBuilder();
            metricPath.append(config.getMetricPrefix());
            Map<String, String> metricsForAServer = redisMetrics.getMetrics();
            for (Map.Entry<String, String> entry : metricsForAServer.entrySet()) {
                printAverageAverageIndividual(metricPath.toString() + entry.getKey(), entry.getValue());
            }
        }
    }

    private void printAverageAverageIndividual(String metricPath, String metricValue) {
        printMetric(metricPath, metricValue, MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
    }

    private void printMetric(String metricPath, String metricValue, String aggregation, String timeRollup, String cluster) {
        MetricWriter metricWriter = super.getMetricWriter(metricPath, aggregation, timeRollup, cluster);
        if (metricValue != null) {
            if(logger.isDebugEnabled()) {
                logger.debug(metricPath + "   " + metricValue);
            }
            metricWriter.printMetric(metricValue);
        }
    }

    /**
     * Returns a config file name,
     *
     * @param filename
     * @return String
     */
    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
    }

    private static String getImplementationVersion() {
        return BusinessMonitor.class.getPackage().getImplementationTitle();
    }

}
