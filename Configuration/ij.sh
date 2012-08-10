JOB_ZMON_BUILD="zMon_Build"
JOB_ZMON_DEPLOY="zMon_Deploy"
JOB_ZMON_TEST="zMon_Test"
JOB_ZMON_MATURE="zMon_Mature"
JOB_ZMON_REGRESSION="zMon_Regression"
JENKINS_CLI="/c/_dev/z-mon/target/work/webapp/WEB-INF/jenkins-cli.jar"
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
importJob $JOB_ZMON_REGRESSION
importJob $JOB_ZMON_MATURE
importJob $JOB_ZMON_TEST
importJob $JOB_ZMON_DEPLOY
importJob $JOB_ZMON_BUILD

