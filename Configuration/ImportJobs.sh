ZMON_JOB1="zMon_Job1"
ZMON_JOB2="zMon_Job2"
ZMON_JOB3="zMon_Job3"
ZMON_JOB4="zMon_Job4"
ZMON_JOB5="zMon_Job5"

function importJob() {
echo ""
echo "Importing Jenkins Project: $4" 
echo "    Running: java -jar $JENKINS_CLI -s $1 create-job $4 --username $2 --password $3"
echo "    Loading:  $4.xml"
java -jar $JENKINS_CLI -s $1 create-job $4 --username $2 --password $3 < $4.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
importJob $1 $2 $3 $ZMON_JOB1
importJob $1 $2 $3 $ZMON_JOB2
importJob $1 $2 $3 $ZMON_JOB3
importJob $1 $2 $3 $ZMON_JOB4
importJob $1 $2 $3 $ZMON_JOB5

