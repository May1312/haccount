FROM wxa
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
RUN rm -rf /usr/local/tomcat/webapps/ROOT/*
ADD target/jeecg.war /usr/local/tomcat/
#RUN unzip -u -o /usr/local/tomcat/jeecg.war -d /usr/local/tomcat/webapps/ROOT/
RUN mv /usr/local/tomcat/jeecg.war /usr/local/tomcat/webapps/ROOT.war
