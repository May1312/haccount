#!/usr/bin/env bash
#编译+部署fnjz_test站点

#需要配置如下参数
# 项目路径, 在Execute Shell中配置项目路径, pwd 就可以获得该项目路径
# export PROJ_PATH=这个jenkins任务在部署机器上的路径

# 输入你的环境上tomcat的全路径
# export TOMCAT_APP_PATH=tomcat在部署机器上的路径

### base 函数


echo "删除deploy目录"

rm -rf /usr/mydocker/tomcat_hbird/webapps/*

mv $PROJ_PATH/target/jeecg.war /usr/mydocker/tomcat_hbird/webapps/

cd /usr/mydocker/tomcat_hbird/webapps/

echo "解压jeecg.war"
unzip jeecg.war
rm -rf jeecg.war

echo "检查docker tomcat_hbird是否运行"
docker ps -a | grep hbird
if [ $? -ne 0 ]
then
echo "不存在此容器，待创建"
docker run --name hbird -d  -p 8201:8080 -v  /usr/mydocker/tomcat_hbird/webapps/:/usr/local/tomcat/webapps/ROOT/   -e TZ="Asia/Shanghai" -v /etc/localtime:/etc/localtime:ro --privileged=true tomcat:7-jre8
echo "创建完成 占用外部8080端口"
else
echo "重启hbird"
fi
docker restart hbird
echo "部署完成，开启日志"
#docker logs -f tomcat_hbird
