#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/fixadate
cd $REPOSITORY

APP_NAME=fixadate
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

P8081_PID=$(sudo netstat -ntlp | grep :8081 | awk '{print $7}' | cut -d'/' -f1)
P8082_PID=$(sudo netstat -ntlp | grep :8082 | awk '{print $7}' | cut -d'/' -f1)

P8081_URL="http://3.37.141.38:8081/v1/member/nickname"
P8082_URL="http://3.37.141.38:8082/v1/member/nickname"

NEW_PID=0

if [ -z "$P8081_PID" ] && [ -z "$P8082_PID" ]
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

  STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}\n" $URL)
    if [ "$STATUS_CODE" -eq 200 ]; then
      echo "Service is healthy (HTTP 200)"
      sudo iptables -t nat -L PREROUTING -v -n --line-numbers
      sudo iptables -t nat -D PREROUTING 1

    fi
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