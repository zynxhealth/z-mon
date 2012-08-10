JOB_ZMON_BUILD="zMon_Build"
JOB_ZMON_DEPLOY="zMon_Deploy"
JOB_ZMON_TEST="zMon_Test"
JOB_ZMON_MATURE="zMon_Mature"
JOB_ZMON_REGRESSION="zMon_Regression"

function importJob() {
echo ""
echo "Importing Jenkins Project: $4" 
echo "    Running: java -jar $JENKINS_CLI -s $1 delete-job $4 --username $2 --password $3"
java -jar $JENKINS_CLI -s $1 delete-job $4 --username $2 --password $3
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
importJob $1 $2 $3 $JOB_ZMON_REGRESSION
importJob $1 $2 $3 $JOB_ZMON_MATURE
importJob $1 $2 $3 $JOB_ZMON_TEST
importJob $1 $2 $3 $JOB_ZMON_DEPLOY
importJob $1 $2 $3 $JOB_ZMON_BUILD

