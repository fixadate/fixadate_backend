#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/fixadate
cd $REPOSITORY

APP_NAME=fixadate
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

P8081_PID=$(sudo netstat -ntlp | grep :8081 | awk '{print $7}' | cut -d'/' -f1)
P8082_PID=$(sudo netstat -ntlp | grep :8082 | awk '{print $7}' | cut -d'/' -f1)


NEW_PID=0

if [ -z "$P8081_PID" ] && [ -z "$P8082_PID" ]
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

elif [ -z "$P8082_PID" ]
then
  NEW_PID=8082
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

elif [ -z "$P8081_PID" ]
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &
fi