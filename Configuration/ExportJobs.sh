JOB_ZMON_BUILD="zMon_Build"
JOB_ZMON_DEPLOY="zMon_Deploy"
JOB_ZMON_TEST="zMon_Test"
JOB_ZMON_MATURE="zMon_Mature"
JOB_ZMON_REGRESSION="zMon_Regression"

function exportJob() {
echo ""
echo "Exporting Jenkins Project: $4" 
echo "    Running: java -jar $JENKINS_CLI -s $1 get-job $4 --username $2 --password $3"
echo "    Saving:  $4.xml"
java -jar $JENKINS_CLI -s $1 get-job $4 --username $2 --password $3 > $4.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
exportJob $1 $2 $3 $JOB_ZMON_BUILD
exportJob $1 $2 $3 $JOB_ZMON_DEPLOY
exportJob $1 $2 $3 $JOB_ZMON_TEST
exportJob $1 $2 $3 $JOB_ZMON_MATURE
exportJob $1 $2 $3 $JOB_ZMON_REGRESSION

