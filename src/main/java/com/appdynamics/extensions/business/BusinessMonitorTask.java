/**
 * Copyright 2014 AppDynamics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.extensions.business;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import sun.misc.IOUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class BusinessMonitorTask {
    public static final String METRIC_SEPARATOR = "|";
    private Logger logger = Logger.getLogger(BusinessMonitorTask.class);
    private Server server;
    XPathFactory xpathFactory = XPathFactory.newInstance();
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();


    public BusinessMonitorTask(Server server) {
        this.server = server;
    }

    public BusinessMetrics gatherMetricsForAServer() {
        BusinessMetrics metricsForAServer = new BusinessMetrics();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Map<String, String> metrics = Maps.newHashMap();

            Document doc = builder.parse("http://" + server.getHost() + ":" + server.getPort() + "/");

            Set<String> metricPaths = server.getMetricPaths();
            for (String metricPath : metricPaths) {
                String valueNamePair[] = metricPath.split(" ");

                XPath xpath = xpathFactory.newXPath();
                XPathExpression valueExpression = xpath.compile(valueNamePair[0]);
                XPathExpression nameExpression = xpath.compile(valueNamePair[1]);

                String value = (String) valueExpression.evaluate(doc, XPathConstants.STRING);
                String name = (String) nameExpression.evaluate(doc, XPathConstants.STRING);
                logger.info("Path:" + name + " value:" + value);
                metrics.put(name, value);
            }

            metricsForAServer.setMetrics(metrics);
            metricsForAServer.getMetrics().put(server.getDisplayName() + METRIC_SEPARATOR + MonitorConstants.METRICS_COLLECTION_STATUS,
                    MonitorConstants.SUCCESS_VALUE);
        } catch (Exception e) {
            logger.error("Exception while gathering metrics for " + server.getDisplayName(), e);
            metricsForAServer.getMetrics().put(server.getDisplayName() + METRIC_SEPARATOR + MonitorConstants.METRICS_COLLECTION_STATUS,
                    MonitorConstants.ERROR_VALUE);
        }
        return metricsForAServer;
    }

    private String getRespose () throws IOException {
        InputStream in = new URL( "http://" + server.getHost() + ":" + server.getPort() + "/" ).openStream();
        byte[] bytes = IOUtils.readFully(in, 10000, false);
        String response = new String(bytes);
        in.close();
        return response;
    }
}
