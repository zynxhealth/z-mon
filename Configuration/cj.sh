ZMON_JOB1="zMon_Job1"
ZMON_JOB2="zMon_Job2"
ZMON_JOB3="zMon_Job3"
ZMON_JOB4="zMon_Job4"
ZMON_JOB5="zMon_Job5"

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
cleanJob $ZMON_JOB1
cleanJob $ZMON_JOB2
cleanJob $ZMON_JOB3
cleanJob $ZMON_JOB4
cleanJob $ZMON_JOB5

