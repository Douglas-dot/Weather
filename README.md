# Weather

1. Install the local SpringMVC environment
2. Clone project to local,use local tomcat(ver:apache-tomcat-7.0.86) with port set 9090
3. Start and run: http://localhost:9090/Weather_war_exploded/weather/report

Business scenario:

The front-end page uses JSP + Ajax to call the back-end API
The city address is configured in dictionary-configuration.xml
Dynamically display the address on the page by reading the configuration file
Call the weather service API to refresh the weather when the city changes
Typical SpringMVC project can be used in ERP implementation of most enterprises
