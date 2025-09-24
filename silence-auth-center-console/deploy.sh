#!/bin/bash
APP_NAME="silence-auth-center-1.0.0-SNAPSHOT.jar"
LOG_FILE="app.log"

case "$1" in
    start)
        nohup java -jar $APP_NAME --spring.profiles.active=prod > $LOG_FILE 2>&1 &
        echo "应用启动成功，PID: $!"
        ;;
    stop)
        PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
        if [ -n "$PID" ]; then
            kill $PID
            echo "应用已停止"
        else
            echo "应用未运行"
        fi
        ;;
    *)
        echo "用法: $0 {start|stop}"
        exit 1
esac