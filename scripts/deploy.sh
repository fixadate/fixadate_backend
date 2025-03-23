#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/fixadate
cd $REPOSITORY

APP_NAME=fixadate
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

P8081_PID=$(sudo netstat -ntlp | grep :8081 | awk '{print $7}' | cut -d'/' -f1) # Port ID  where 8081 is on
P8082_PID=$(sudo netstat -ntlp | grep :8082 | awk '{print $7}' | cut -d'/' -f1) # Port ID  where 8082 is on

P8081_URL="http://3.37.141.38:8081/v1/healthcheck" # health check url when port 8081 initialize
P8082_URL="http://3.37.141.38:8082/v1/healthcheck" # health check url when port 8082 initialize

NEW_PID=0

# 0. old version running
# 1. start new version on other port
# 2. remove previous port-forwarding rule and add new rule
# 3. kill old version server

if [ -z "$P8081_PID" ] && [ -z "$P8082_PID" ] # if neither 8081 and 8082 is not using
then
  NEW_PID=8081 # newly opening sever port
  echo "> Deploy - $JAR_PATH " #simple message
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 & #server initializing command
  # nohup: ignore messages from following command
  # >: send and save messages to following command
  # 2>&1 &: logging both normal(1) and error(2) messages and send it to background

  for RETRY in {1..10} # for 10 times
  do
    STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}\n" $P8081_URL) # get response code from health check url above
    if [ "$STATUS_CODE" -eq 200 ]; then # if response is 200 OK
      break
    elif [ ${RETRY} -eq 10 ] # if retry cycle exceed 10, end the whole script
    then
      echo "> Health Check Fail"
      exit 1
    fi
    echo "> Health Check Retry..."
    sleep 10 # wait 10 seconds and retry
  done

  #Port forwarding
  sudo sed -i "s/server 127\.0\.0\.1:[0-9]\+/server 127\.0\.0\.1:$NEW_PID/" /etc/nginx/nginx.conf
  echo "Modifing Nginx Configuration"

  sudo nginx -t
	sudo systemctl reload nginx

elif [ -z "$P8082_PID" ] # if only 8082 is missing = just 8081 is running
then
  NEW_PID=8082
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

  for RETRY in {1..10}
  do
    STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}\n" $P8082_URL)
    if [ "$STATUS_CODE" -eq 200 ]; then
      break
    elif [ ${RETRY} -eq 10 ]
    then
      echo "> Health Check Fail"
      exit 1
    fi
    echo "> Health Check Retry..."
    sleep 10
  done

  sudo sed -i "s/server 127\.0\.0\.1:[0-9]\+/server 127\.0\.0\.1:$NEW_PID/" /etc/nginx/nginx.conf
  echo "Modifing Nginx Configuration"

  sudo nginx -t
  sudo systemctl reload nginx

  sudo kill -9 $P8081_PID

elif [ -z "$P8081_PID" ] # if only 8081 is missing = just 8082 is running
then
  NEW_PID=8081
  echo "> Deploy - $JAR_PATH "
  nohup java -jar $JAR_PATH --server.port=$NEW_PID > /home/ubuntu/deploy.log 2>&1 &

  for RETRY in {1..10}
  do
    STATUS_CODE=$(curl -o /dev/null -s -w "%{http_code}\n" $P8081_URL)
    if [ "$STATUS_CODE" -eq 200 ]; then
      break
    elif [ ${RETRY} -eq 10 ]
    then
      echo "> Health Check Fail"
      exit 1
    fi
    echo "> Health Check Retry..."
    sleep 10
  done

  sudo sed -i "s/server 127\.0\.0\.1:[0-9]\+/server 127\.0\.0\.1:$NEW_PID/" /etc/nginx/nginx.conf
  echo "Modifing Nginx Configuration"

  sudo nginx -t
  sudo systemctl reload nginx

  sudo kill -9 $P8082_PID
fi