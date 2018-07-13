CONTINUOUSMON_JOB1="continuousMon_Job1"
CONTINUOUSMON_JOB2="continuousMon_Job2"
CONTINUOUSMON_JOB3="continuousMon_Job3"
CONTINUOUSMON_JOB4="continuousMon_Job4"
CONTINUOUSMON_JOB5="continuousMon_Job5"
CONTINUOUSMON_JOB6="continuousMon_Job6"
CONTINUOUSMON_JOB7="continuousMon_Job7"
CONTINUOUSMON_JOB8="continuousMon_Job8"

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
exportJob $CONTINUOUSMON_JOB1
exportJob $CONTINUOUSMON_JOB2
exportJob $CONTINUOUSMON_JOB3
exportJob $CONTINUOUSMON_JOB4
exportJob $CONTINUOUSMON_JOB5
exportJob $CONTINUOUSMON_JOB6
exportJob $CONTINUOUSMON_JOB7
exportJob $CONTINUOUSMON_JOB8

