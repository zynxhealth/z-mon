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

function importJob() {
echo ""
echo "Importing Jenkins Project: $1"
echo "    Running: java -jar $JENKINS_CLI -s $JENKINS_MASTER create-job $1 "
echo "    Loading:  $1.xml"
java -jar $JENKINS_CLI -s $JENKINS_MASTER create-job $1 < $1.xml
}

echo "Your Jenkins CLI is located at: $JENKINS_CLI"
echo "Your Jenkins Master is: $JENKINS_MASTER"
importJob $CONTINUOUSMON_JOB1
importJob $CONTINUOUSMON_JOB2
importJob $CONTINUOUSMON_JOB3
importJob $CONTINUOUSMON_JOB4
importJob $CONTINUOUSMON_JOB5
importJob $CONTINUOUSMON_JOB6
importJob $CONTINUOUSMON_JOB7
importJob $CONTINUOUSMON_JOB8

