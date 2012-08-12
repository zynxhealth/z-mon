JOB_ZMON_BUILD="zMon_Build"
JOB_ZMON_DEPLOY="zMon_Deploy"
JOB_ZMON_TEST="zMon_Test"
JOB_ZMON_MATURE="zMon_Mature"
JOB_ZMON_REGRESSION="zMon_Regression"
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
exportJob $JOB_ZMON_REGRESSION
exportJob $JOB_ZMON_MATURE
exportJob $JOB_ZMON_TEST
exportJob $JOB_ZMON_DEPLOY
exportJob $JOB_ZMON_BUILD

