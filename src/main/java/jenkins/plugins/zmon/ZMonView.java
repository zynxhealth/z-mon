package jenkins.plugins.zmon;

import hudson.Extension;
import hudson.model.*;
import hudson.tasks.junit.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import javax.servlet.ServletException;
import java.io.IOException;

public class ZMonView extends ListView {
    private final int millisecondsInAMinute = 60000;

    @DataBoundConstructor
    public ZMonView(String name) {
        super(name);
    }

  @Extension
  public static final class ZMonViewDescriptor extends ViewDescriptor {

    @Override
    public String getDisplayName() {
      return "Zynx Monitor";
    }

  }

  public String getBuildJobName(){ return buildJobName; }
  public String getDeployJobName(){ return deployJobName; }
  public String getFastTestJob(){ return fastTestJob; }
  public String getFastTestDisplay(){ return fastTestDisplay; }
  public String getMediumTestJob(){ return mediumTestJob; }
  public String getMediumTestDisplay(){ return mediumTestDisplay; }
  public String getSlowTestJob(){ return slowTestJob; }
  public String getSlowTestDisplay(){ return slowTestDisplay; }
  public String getTeamName() { return teamName; }
  public String getTeamLogoURL() { return teamLogoURL; }

  private String teamName;
  private String teamLogoURL;
  private String buildJobName; 
  private String deployJobName; 
  private String fastTestJob; 
  private String fastTestDisplay; 
  private String mediumTestJob; 
  private String mediumTestDisplay; 
  private String slowTestJob; 
  private String slowTestDisplay; 


  @Override
  protected void submit(StaplerRequest req) throws ServletException,
          Descriptor.FormException, IOException {
      super.submit(req);
      this.teamName = (req.getParameter("teamName") != null) ? req.getParameter("teamName") : "the anonymous";
      this.teamLogoURL = (req.getParameter("teamLogoURL") != null) ? req.getParameter("teamLogoURL") : "";
      this.buildJobName = (req.getParameter("buildJobName") != null) ? req.getParameter("buildJobName") : "zMon_Build";
      this.deployJobName = (req.getParameter("deployJobName") != null) ? req.getParameter("deployJobName") : "zMon_Deploy";
      this.fastTestJob = (req.getParameter("fastTestJob") != null) ? req.getParameter("fastTestJob") : "zMon_Test";
      this.fastTestDisplay = (req.getParameter("fastTestDisplay") != null) ? req.getParameter("fastTestDisplay") : "tests";
      this.mediumTestJob = (req.getParameter("mediumTestJob") != null) ? req.getParameter("mediumTestJob") : "zMon_Mature";
      this.mediumTestDisplay = (req.getParameter("mediumTestDisplay") != null) ? req.getParameter("mediumTestDisplay") : "mature";
      this.slowTestJob = (req.getParameter("slowTestJob") != null) ? req.getParameter("slowTestJob") : "zMon_Regression";
      this.slowTestDisplay = (req.getParameter("slowTestDisplay") != null) ? req.getParameter("slowTestDisplay") : "regression";
  }

    public String getBuildTime() {return getLastBuildDuration(buildJobName); }
    public String getDeployTime() { return getLastBuildDuration(deployJobName); }
    public String getTestsTime() { return getLastBuildDuration(fastTestJob); }
    public String getMatureTime() {return getLastBuildDuration(mediumTestJob); }
    public String getRegressionTime() { return getLastBuildDuration(slowTestJob); }

    public String getBuildTimeUnit() {return getTimeUnit(buildJobName); }
    public String getDeployTimeUnit() { return getTimeUnit(deployJobName); }
    public String getTestsTimeUnit() { return getTimeUnit(fastTestJob); }
    public String getMatureTimeUnit() {return getTimeUnit(mediumTestJob); }
    public String getRegressionTimeUnit() { return getTimeUnit(slowTestJob); }

    public String getBuildSinceLastRun() { return getSinceLastRun(buildJobName); }
    public String getDeploySinceLastRun() { return getSinceLastRun(deployJobName); }
    public String getTestsSinceLastRun() { return getSinceLastRun(fastTestJob); }
    public String getMatureSinceLastRun() { return getSinceLastRun(mediumTestJob); }
    public String getRegressionSinceLastRun() { return getSinceLastRun(slowTestJob); }

    public String getBuildLastRunPassFail() { return getLastRunPassFail(buildJobName); }
    public String getDeployLastRunPassFail() { return getLastRunPassFail(deployJobName); }
    public String getTestsLastRunPassFail() { return getLastRunPassFail(fastTestJob); }
    public String getMatureLastRunPassFail() { return getLastRunPassFail(mediumTestJob); }
    public String getRegressionLastRunPassFail() { return getLastRunPassFail(slowTestJob); }

    public String getBuildLastRunStatus() { return getLastRunStatus(buildJobName); }
    public String getDeployLastRunStatus() { return getLastRunStatus(deployJobName); }
    public String getTestsLastRunStatus() { return getLastRunStatus(fastTestJob); }
    public String getMatureLastRunStatus() { return getLastRunStatus(mediumTestJob); }
    public String getRegressionLastRunStatus() { return getLastRunStatus(slowTestJob); }

    public String getBuildStatus() { return getStatus(buildJobName); }
    public String getDeployStatus() { return getStatus(deployJobName); }
    public String getTestsStatus() { return getStatus(fastTestJob); }
    public String getMatureStatus() { return getStatus(mediumTestJob); }
    public String getRegressionStatus() { return getStatus(slowTestJob); }

    public String getBuildStatus2() { return getStatus2(buildJobName); }
    public String getDeployStatus2() { return getStatus2(deployJobName); }
    public String getTestsStatus2() { return getStatus2(fastTestJob); }
    public String getMatureStatus2() { return getStatus2(mediumTestJob); }
    public String getRegressionStatus2() { return getStatus2(slowTestJob); }

    public String getTestsFailed() { return getFailedTests(fastTestJob); }
    public String getMatureFailed() { return getFailedTests(mediumTestJob); }
    public String getRegressionFailed() { return getFailedTests(slowTestJob); }


    private String getLastBuildDuration(String jobName) {
      Project tli = (Project)(Hudson.getInstance().getItem(jobName));
      return String.valueOf(tli.getLastBuild().getDuration()/millisecondsInAMinute);
    }

    public String getBuildNumber() {
        return String.valueOf((int) getLastBuild(buildJobName).number);
    }

    private String getFailedTests(String jobName) {
        TestResultAction testResults = (TestResultAction) getLastBuild(jobName).getTestResultAction();

        if (testResults != null) {
            return "<strong>" + String.valueOf ( (int) ((testResults.getFailCount() / testResults.getTotalCount()) * 100)) + "%</strong> failed";
        }
        else {
            return "";
        }
    }

    private FreeStyleBuild getLastBuild(String jobName) {
        Project tli = (Project) (Hudson.getInstance().getItem(jobName));
        FreeStyleBuild lastBuild = (FreeStyleBuild) tli.getLastBuild();

        // this logic exists because getLastBuild() will return the currently running build
        if (lastBuild.isBuilding()) {
            return (FreeStyleBuild) tli.getBuilds().get(1);
        }
        else {
            return lastBuild;
        }
    }

    private Long getBuildDuration(String jobName) {
        return (System.currentTimeMillis() - getLastBuild(jobName).getTimeInMillis()) / millisecondsInAMinute;
    }

    private String getSinceLastRun(String jobName) {
        Long duration = getBuildDuration(jobName);
        return String.valueOf(duration) + " min" + ((duration == 1) ? "" : "s");
    }

    private String getLastRunPassFail(String jobName) {
        if (getLastBuild(jobName).getBuildStatusSummary().message.toString().equalsIgnoreCase("stable")) {
            return "pass";
        } else {
            return "fail";
        }
    }

    private String getLastRunStatus(String jobName) {
        return getLastBuild(jobName).getBuildStatusSummary().message.toString();
    }

    private String getTimeUnit(String jobName) {
        return "min" + ((getBuildDuration(jobName) == 1) ? "" : "s");
    }

    private String getStatus(String jobName) {
        Project tli = (Project) (Hudson.getInstance().getItem(jobName));

        if (tli.isBuilding()) {
            return "running";
        } else {
            if (tli.getLastBuild().getBuildStatusSummary().message.toString().equalsIgnoreCase("stable")) {
                return "passed";
            } else {
                return "failed";
            }
        }
    }

    private String getStatus2(String jobName) {
        Project tli = (Project) (Hudson.getInstance().getItem(jobName));
        if (tli.isBuilding()) {
            return "running-time";
        } else {
            return "";
        }
    }
}
