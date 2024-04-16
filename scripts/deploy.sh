#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/fixadate
cd $REPOSITORY

APP_NAME=fixadate
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
LOG=$(ls /home/ubuntu/ | grep 'deploy.log' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Deploy - $JAR_PATH "
sudo nohup java -jar $JAR_PATH > $LOG 2>&1