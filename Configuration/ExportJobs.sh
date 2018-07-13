CONTINUOUSMON_JOB1="continuousMon_Job1"
CONTINUOUSMON_JOB2="continuousMon_Job2"
CONTINUOUSMON_JOB3="continuousMon_Job3"
CONTINUOUSMON_JOB4="continuousMon_Job4"
CONTINUOUSMON_JOB5="continuousMon_Job5"
CONTINUOUSMON_JOB6="continuousMon_Job6"
CONTINUOUSMON_JOB7="continuousMon_Job7"
CONTINUOUSMON_JOB8="continuousMon_Job8"

function exportJob() {
echo ""
echo "Exporting Jenkins Project: $4" 
echo "    Running: java -jar $JENKINS_CLI -s $1 get-job $4 --username $2 --password $3"
echo "    Saving:  $4.xml"
java -jar $JENKINS_CLI -s $1 get-job $4 --username $2 --password $3 > $4.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
exportJob $1 $2 $3 $CONTINUOUSMON_JOB1
exportJob $1 $2 $3 $CONTINUOUSMON_JOB2
exportJob $1 $2 $3 $CONTINUOUSMON_JOB3
exportJob $1 $2 $3 $CONTINUOUSMON_JOB4
exportJob $1 $2 $3 $CONTINUOUSMON_JOB5
exportJob $1 $2 $3 $CONTINUOUSMON_JOB6
exportJob $1 $2 $3 $CONTINUOUSMON_JOB7
exportJob $1 $2 $3 $CONTINUOUSMON_JOB8

