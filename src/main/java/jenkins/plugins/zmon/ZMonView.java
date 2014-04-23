package jenkins.plugins.zmon;

import hudson.Extension;
import hudson.model.*;
import hudson.tasks.junit.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.io.IOException;

public class ZMonView extends ListView {
    private final int millisecondsInAMinute = 60000;
    private final double minutesInAnHour = 60.0;

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
    public String getBuildDisplay() { return buildDisplay; }
    public String getDeployJobName(){ return deployJobName; }
    public String getDeployDisplay() { return deployDisplay; }
    public String getFastTestJob(){ return fastTestJob; }
    public String getFastTestDisplay(){ return fastTestDisplay; }
    public String getMediumTestJob(){ return mediumTestJob; }
    public String getMediumTestDisplay(){ return mediumTestDisplay; }
    public String getSlowTestJob(){ return slowTestJob; }
    public String getSlowTestDisplay(){ return slowTestDisplay; }
    public String getTeamName() { return teamName; }
    public String getTeamLogoURL() { return teamLogoURL; }
    public String getRefresh() { return refresh; }

    private String teamName;
    private String teamLogoURL;
    private String buildJobName;
    private String buildDisplay;
    private String deployJobName;
    private String deployDisplay;
    private String fastTestJob;
    private String fastTestDisplay;
    private String mediumTestJob;
    private String mediumTestDisplay;
    private String slowTestJob;
    private String slowTestDisplay;
    private String refresh;


    @Override
    protected void submit(StaplerRequest req) throws ServletException,
          Descriptor.FormException, IOException {
      super.submit(req);
      this.teamName = (req.getParameter("teamName") != null) ? req.getParameter("teamName") : "the anonymous";
      this.teamLogoURL = (req.getParameter("teamLogoURL") != null) ? req.getParameter("teamLogoURL") : "";
      this.buildJobName = (req.getParameter("buildJobName") != null) ? req.getParameter("buildJobName") : "";
      this.buildDisplay = (req.getParameter("buildDisplay") != null) ? req.getParameter("buildDisplay") : "build";
      this.deployJobName = (req.getParameter("deployJobName") != null) ? req.getParameter("deployJobName") : "";
      this.deployDisplay = (req.getParameter("deployDisplay") != null) ? req.getParameter("deployDisplay") : "deploy";
      this.fastTestJob = (req.getParameter("fastTestJob") != null) ? req.getParameter("fastTestJob") : "";
      this.fastTestDisplay = (req.getParameter("fastTestDisplay") != null) ? req.getParameter("fastTestDisplay") : "tests";
      this.mediumTestJob = (req.getParameter("mediumTestJob") != null) ? req.getParameter("mediumTestJob") : "";
      this.mediumTestDisplay = (req.getParameter("mediumTestDisplay") != null) ? req.getParameter("mediumTestDisplay") : "mature";
      this.slowTestJob = (req.getParameter("slowTestJob") != null) ? req.getParameter("slowTestJob") : "";
      this.slowTestDisplay = (req.getParameter("slowTestDisplay") != null) ? req.getParameter("slowTestDisplay") : "regression";
      this.refresh = (req.getParameter("refresh") != null) ? req.getParameter("refresh") : "3";
    }

    public String getBuildNumber() {return String.valueOf((int) getLastBuild(buildJobName).number); }

    public String getBuildTime() {return getCurrentBuildDuration(buildJobName); }
    public String getDeployTime() { return getCurrentBuildDuration(deployJobName); }
    public String getTestsTime() { return getCurrentBuildDuration(fastTestJob); }
    public String getMatureTime() {return getCurrentBuildDuration(mediumTestJob); }
    public String getRegressionTime() { return getCurrentBuildDuration(slowTestJob); }

    public String getBuildPercentCompleted() {return getPercentCompleted(buildJobName); }
    public String getDeployPercentCompleted() { return getPercentCompleted(deployJobName); }
    public String getTestsPercentCompleted() { return getPercentCompleted(fastTestJob); }
    public String getMaturePercentCompleted() {return getPercentCompleted(mediumTestJob); }
    public String getRegressionPercentCompleted() { return getPercentCompleted(slowTestJob); }

    public String getBuildSinceLastRun() { return getTimeElapsedSinceLastRun(buildJobName); }
    public String getDeploySinceLastRun() { return getTimeElapsedSinceLastRun(deployJobName); }
    public String getTestsSinceLastRun() { return getTimeElapsedSinceLastRun(fastTestJob); }
    public String getMatureSinceLastRun() { return getTimeElapsedSinceLastRun(mediumTestJob); }
    public String getRegressionSinceLastRun() { return getTimeElapsedSinceLastRun(slowTestJob); }

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

    public String getTestsFailed() { return getFailedTests(fastTestJob); }
    public String getMatureFailed() { return getFailedTests(mediumTestJob); }
    public String getRegressionFailed() { return getFailedTests(slowTestJob); }

    private String getCurrentBuildDuration(String jobName) {
        AbstractProject tli = (AbstractProject)(Hudson.getInstance().getItem(jobName));

        if (tli.getLastBuild().isBuilding()) {
            return convertDurationToDisplay((System.currentTimeMillis() - tli.getLastBuild().getTimeInMillis()));
        }
        else {
            return convertDurationToDisplay(tli.getLastBuild().getDuration());
        }
    }

    private String getPercentCompleted(String jobName) {
        AbstractProject tli = (AbstractProject) (Hudson.getInstance().getItem(jobName));
        AbstractBuild lastBuild = (AbstractBuild) tli.getLastBuild();
        long percentCompleted = 0;
        long duration = 0;
        long estimatedDuration = 0;

        if (lastBuild.isBuilding()) {
            duration = System.currentTimeMillis() - lastBuild.getTimeInMillis();
            estimatedDuration = lastBuild.getEstimatedDuration();

            if (estimatedDuration == -1) {
                percentCompleted = 0;
            }
            else {
                if (duration <= estimatedDuration) {
                    percentCompleted = (long)((double) duration / (double) estimatedDuration * 100);
                }
                else {
                    percentCompleted = 100;
                }
            }
        }

        return String.valueOf(percentCompleted) + "%";
    }

    private String getTimeElapsedSinceLastRun(String jobName) {
        return convertDurationToDisplay((System.currentTimeMillis() - getLastBuild(jobName).getTimeInMillis()));
    }

    private String getFailedTests(String jobName) {
        TestResultAction testResults = (TestResultAction) getLastBuild(jobName).getTestResultAction();

        if (testResults != null) {
            return "<strong>" + String.valueOf ( (int) (((double) testResults.getFailCount()/ (double) testResults.getTotalCount()) * 100.0)) + "%</strong> failed";
        }
        else {
            return "";
        }
    }

    private AbstractBuild getLastBuild(String jobName) {
        AbstractProject tli = (AbstractProject) (Hudson.getInstance().getItem(jobName));
        AbstractBuild lastBuild = (AbstractBuild) tli.getLastBuild();

        // this logic exists because getLastBuild() will return the currently running build
        if (lastBuild.isBuilding()) {
            return (AbstractBuild) tli.getBuilds().get(1);
        }
        else {
            return lastBuild;
        }
    }

    private String getLastRunPassFail(String jobName) {
    	String lastBuildStatusSummary = getLastBuild(jobName).getBuildStatusSummary().message.toString();
        if ((lastBuildStatusSummary.equalsIgnoreCase("stable")) || 
    		(lastBuildStatusSummary.equalsIgnoreCase("back to normal"))){
            return "pass";
        } else {
            return "fail";
        }
    }

    private String getLastRunStatus(String jobName) {
        return getLastBuild(jobName).getBuildStatusSummary().message.toString();
    }

    private String getStatus(String jobName) {
        AbstractProject tli = (AbstractProject) (Hudson.getInstance().getItem(jobName));

        if (tli.isBuilding()) {
            return "running";
        } else {
            if (tli.getLastBuild().getResult().toString().equalsIgnoreCase("SUCCESS")) {
                return "passed";
            } else {
                return "failed";
            }
        }
    }

    private String convertDurationToDisplay(long durationInMillis) {
        long durationInMins = durationInMillis / millisecondsInAMinute;
        if (durationInMins > minutesInAnHour) {
            return "<strong>" + String.valueOf(round(durationInMins / minutesInAnHour, 2, BigDecimal.ROUND_HALF_UP)) + "</strong> hours";
        }
        else {
            return "<strong>" + String.valueOf(durationInMins) + "</strong> min" + ((durationInMins == 1) ? "" : "s");
        }
    }

    private static double round(double unrounded, int precision, int roundingMode) {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }
}
