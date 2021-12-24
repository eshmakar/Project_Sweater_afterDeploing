#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/keys.pem \
    target/sweater-0.0.1-SNAPSHOT.jar \
    ec2-user@ec2-3-8-198-5.eu-west-2.compute.amazonaws.com:/home/ec2-user/

echo 'Restart server...'

ssh -i ~/.ssh/keys.pem ec2-user@ec2-3-8-198-5.eu-west-2.compute.amazonaws.com << EOF
pgrep java | xargs kill -9
nohup java -jar sweater-0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'
