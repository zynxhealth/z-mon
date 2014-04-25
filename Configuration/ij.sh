ZMON_JOB1="zMon_Job1"
ZMON_JOB2="zMon_Job2"
ZMON_JOB3="zMon_Job3"
ZMON_JOB4="zMon_Job4"
ZMON_JOB5="zMon_Job5"

JENKINS_CLI="../target/work/webapp/WEB-INF/jenkins-cli.jar"
JENKINS_MASTER="http://localhost:8080"

function importJob() {
echo ""
echo "Importing Jenkins Project: $1"
echo "    Running: java -jar $JENKINS_CLI -s $JENKINS_MASTER create-job $1 "
echo "    Loading:  $1.xml"
java -jar $JENKINS_CLI -s $JENKINS_MASTER create-job $1 < $1.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
importJob $ZMON_JOB1
importJob $ZMON_JOB2
importJob $ZMON_JOB3
importJob $ZMON_JOB4
importJob $ZMON_JOB5

