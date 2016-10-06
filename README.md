z-mon
=====

A jenkins build monitor plugin

How to Build and Install Z-Mon
------------------------------
1. Pull the latest z-mon source from GitHub to your local machine
2. To build the plugin to be used with Jenkins, you will need to first install
Apache Maven.  You can download it from:
  http://maven.apache.org/download.cgi
3. Navigate to the z-mon source directory on your machine. For example:
  cd ~/Projects/z-mon/
4. Edit the pom.xml file in the z-mon root directory.  You will need to change
  the version element's value, located underneath the comment
  "The version of Hudson this plugin will run on". The version number should
  match your version of Jenkins.  You can determine your version of Jenkins, by
  looking at the bottom right of a Jenkins page.
5. Run Maven on this directory. For example:
  ~/Downloads/apache-maven-3.2.1/bin/mvn clean install
6. An .hpi file should be created in the target directory. For example:
  ls -al target/
7. You can then upload the plugin via the "Advanced" tab on the Jenkins Manage
Plugins page.



To find job full names, run in Jenkins script console:
Hudson.getInstance().getJobNames()

To create monitor view click the + at the top of the tab and choose CFPB Monitor.
You can configure the view by going to the monitor and appending /configure to the end
