#!/bin/bash
## Author hanjindong
## UPDATE 2019-08-27
version="1.0";
appName=zxkj-auth
appVersion=1.0
fullName=$appName-$appVersion.jar
function start()
{
	count=`ps -ef |grep java|grep $fullName|wc -l`
	if [ $count != 0 ];then
		echo "Maybe $fullName is running, please check it..."
	else
		echo "The $fullName is starting..."
		nohup java -jar ./$fullName -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -Xms512M -Xmx4G > /dev/null 2>&1 &
    fi
}

function stop()
{
	appId=`ps -ef |grep java|grep $fullName|awk '{print $2}'`
	if [ -z $appId ];then
	    echo "Maybe $fullName not running, please check it..."
	else
        echo "The $fullName is stopping..."
        kill -15 $appId
	fi
}

function restart()
{
    stop
    for i in {10..1}
    do
        echo -n "$i "
        sleep 1
    done
    echo 0
    start
}

function status()
{
    appId=`ps -ef |grep java|grep $fullName|awk '{print $2}'`
	if [ -z $appId ]
	then
	    echo -e "\033[31m Not running \033[0m"
	else
	    echo -e "\033[32m Running [$appId] \033[0m"
	fi
}


function usage()
{
    echo "Usage: $0 {start|stop|restart|status|stop -f}"
    echo "Example: $0 start"
    exit 1
}

case $1 in
	start)
	start;;

	stop)
	stop;;

	restart)
	restart;;

	status)
	status;;

	*)
	usage;;
esac