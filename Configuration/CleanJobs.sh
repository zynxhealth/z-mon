CONTINUOUSMON_JOB1="continuousMon_Job1"
CONTINUOUSMON_JOB2="continuousMon_Job2"
CONTINUOUSMON_JOB3="continuousMon_Job3"
CONTINUOUSMON_JOB4="continuousMon_Job4"
CONTINUOUSMON_JOB5="continuousMon_Job5"
CONTINUOUSMON_JOB6="continuousMon_Job6"
CONTINUOUSMON_JOB7="continuousMon_Job7"
CONTINUOUSMON_JOB8="continuousMon_Job8"

function importJob() {
echo ""
echo "Importing Jenkins Project: $4" 
echo "    Running: java -jar $JENKINS_CLI -s $1 delete-job $4 --username $2 --password $3"
java -jar $JENKINS_CLI -s $1 delete-job $4 --username $2 --password $3
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
importJob $1 $2 $3 $CONTINUOUSMON_JOB1
importJob $1 $2 $3 $CONTINUOUSMON_JOB2
importJob $1 $2 $3 $CONTINUOUSMON_JOB3
importJob $1 $2 $3 $CONTINUOUSMON_JOB4
importJob $1 $2 $3 $CONTINUOUSMON_JOB5
importJob $1 $2 $3 $CONTINUOUSMON_JOB6
importJob $1 $2 $3 $CONTINUOUSMON_JOB7
importJob $1 $2 $3 $CONTINUOUSMON_JOB8

