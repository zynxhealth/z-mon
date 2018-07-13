#!/usr/bin/env bash
CONTINUOUSMON_JOB1="continuousMon_Job1"
CONTINUOUSMON_JOB2="continuousMon_Job2"
CONTINUOUSMON_JOB3="continuousMon_Job3"
CONTINUOUSMON_JOB4="continuousMon_Job4"
CONTINUOUSMON_JOB5="continuousMon_Job5"
CONTINUOUSMON_JOB6="continuousMon_Job6"
CONTINUOUSMON_JOB7="continuousMon_Job7"
CONTINUOUSMON_JOB8="continuousMon_Job8"

JENKINS_CLI="../target/work/webapp/WEB-INF/jenkins-cli.jar"
JENKINS_MASTER="http://localhost:8080"

function cleanJob() {
echo ""
echo "Cleaning Jenkins Project: $1"
echo "    Running: java -jar $JENKINS_CLI -s $JENKINS_MASTER delete-job $1"
java -jar $JENKINS_CLI -s $JENKINS_MASTER delete-job $1
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $1"
cleanJob $CONTINUOUSMON_JOB1
cleanJob $CONTINUOUSMON_JOB2
cleanJob $CONTINUOUSMON_JOB3
cleanJob $CONTINUOUSMON_JOB4
cleanJob $CONTINUOUSMON_JOB5
cleanJob $CONTINUOUSMON_JOB6
cleanJob $CONTINUOUSMON_JOB7
cleanJob $CONTINUOUSMON_JOB8

