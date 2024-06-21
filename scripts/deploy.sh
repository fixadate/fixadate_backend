#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/fixadate
cd $REPOSITORY

APP_NAME=fixadate
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(sudo pgrep -f $APP_NAME)

NEW_PID=0

if [ -z ${CURRENT_PID} ]
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &
  
elif [ ${CURRENT_PID} -eq 8081 ]
then
  NEW_PID=8082
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

elif [ ${CURRENT_PID} -eq 8082 ]
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &
fi