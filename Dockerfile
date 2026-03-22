FROM tomcat:10.1-jdk21
COPY target/airline-booking-system-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]