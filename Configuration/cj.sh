JOB_ZMON_BUILD="zMon_Build"
JOB_ZMON_DEPLOY="zMon_Deploy"
JOB_ZMON_TEST="zMon_Test"
JOB_ZMON_MATURE="zMon_Mature"
JOB_ZMON_REGRESSION="zMon_Regression"
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
cleanJob $JOB_ZMON_REGRESSION
cleanJob $JOB_ZMON_MATURE
cleanJob $JOB_ZMON_TEST
cleanJob $JOB_ZMON_DEPLOY
cleanJob $JOB_ZMON_BUILD

