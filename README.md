# Business Monitoring Extension

This extension works only with the standalone machine agent.

## Use Case
Many times legacy systems are hard to instrument using agents. However getting monitoring data from them into AppDynamics helps in getting the bigger picture. It might also be the case that there are existing monitoring systems which are pulling data from legacy systems and they need a faster replacement.

Task of getting these custom metrics from the legacy systems is made easier by this business-monitoring-extension. Steps involved are fairly simple
1) Expose metricable data as XML over HTTP from the legacy system with your choice of technology.
2) Configuring XPATHs for discovering metric name and metric value.


##Installation
1. Run 'mvn clean install' from the business-monitoring-extension directory and find the BusinessMonitor.zip in the "target" folder.
2. Unzip as "BusinessMonitor" and copy the "BusinessMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`

## Configuration ##
Note : Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a [yaml validator](http://yamllint.com/)

1. Configure the connectivity to legacy system by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/RedisMonitor/`. 
2. Specify the XPATH list for metric names and values

   For eg.
   ```
        # List of Commerce Servers where to pull data from
        servers:
         - host: "localhost"
           port: 9999
           password: "SJ5b2m7d1$354"
           displayName: "Market Place"

        # These are the xPath Expression, path refers to the metric name, content the metric value
        metricPaths: [
           /Metrics/AuctionDuration/text() /Metrics/AuctionDuration/@name ,
           /Metrics/NumberOfParticipants/text() /Metrics/NumberOfParticipants/@name,
           /Metrics/TransactionAmount/text() /Metrics/TransactionAmount/@name
         ]
         
         #prefix used to show up metrics in AppDynamics Dashboard
         metricPrefix:  "Custom Metrics|Commerce Performance|"# List of Redis servers
   ```

3. Configure the path to the config.yml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/BusinessMonitor/` directory. Below is the sample

     ```
     <task-arguments>
         <!-- config file-->
         <argument name="config-file" is-required="true" default-value="monitors/BusinessMonitor/config.yml" />
          ....
     </task-arguments>
    ```

Note : By default, a Machine agent or a AppServer agent can send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned [here](http://docs.appdynamics.com/display/PRO14S/Metrics+Limits).
For eg.  
```    
    java -Dappdynamics.agent.maxMetrics=2500 -jar machineagent.jar
```

## Contributing
Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/business-monitoring-extension).

##Community [PLEASE FIX THIS]
Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/eXchange/Redis---Monitoring-Extension/idi-p/4505) community.

##Support
For any questions or feature request, please contact [AppDynamics Support](mailto:help@appdynamics.com).

