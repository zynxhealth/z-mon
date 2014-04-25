ZMON_JOB1="zMon_Job1"
ZMON_JOB2="zMon_Job2"
ZMON_JOB3="zMon_Job3"
ZMON_JOB4="zMon_Job4"
ZMON_JOB5="zMon_Job5"

JENKINS_CLI="../target/work/webapp/WEB-INF/jenkins-cli.jar"
JENKINS_MASTER="http://localhost:8080"

function exportJob() {
echo ""
echo "Exporting Jenkins Project: $1" 
echo "    Running: java -jar $JENKINS_CLI -s $JENKINS_MASTER get-job $1"
echo "    Saving:  $1.xml"
java -jar $JENKINS_CLI -s $JENKINS_MASTER get-job $1 > $1.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
exportJob $ZMON_JOB1
exportJob $ZMON_JOB2
exportJob $ZMON_JOB3
exportJob $ZMON_JOB4
exportJob $ZMON_JOB5

