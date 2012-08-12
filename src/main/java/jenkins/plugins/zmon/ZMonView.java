package jenkins.plugins.zmon;
import hudson.Extension;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;


public class ZMonView extends ListView{

    private String teamName;

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
  public String getBuildJobName(){
    return buildJobName; 
  }
  public String getDeployJobName(){
    return deployJobName; 
  }
  public String getFastTestJob(){
    return fastTestJob; 
  }
  public String getFastTestDisplay(){
    return fastTestDisplay; 
  }
  public String getMediumTestJob(){
    return mediumTestJob; 
  }
  public String getMediumTestDisplay(){
    return mediumTestDisplay; 
  }
  public String getSlowTestJob(){
    return slowTestJob; 
  }
  public String getSlowTestDisplay(){
    return slowTestDisplay; 
  }


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
        this.buildJobName = req.getParameter("buildJobName");
        this.deployJobName = req.getParameter("deployJobName");
        this.fastTestJob = req.getParameter("fastTestJob");
        this.fastTestDisplay = req.getParameter("fastTestDisplay");
        this.mediumTestJob = req.getParameter("mediumTestJob");
        this.mediumTestDisplay = req.getParameter("mediumTestDisplay");
        this.slowTestJob = req.getParameter("slowTestJob");
        this.slowTestDisplay = req.getParameter("slowTestDisplay");




  }

    public String getTeamName() {
        if (this.teamName == null) {
            this.teamName = "the anonymous";
        }
        return  this.teamName;
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


    private Long getBuildDuration(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return (System.currentTimeMillis() - tli.getLastBuild().getTimeInMillis()) / millisecondsInAMinute;
    }

    public String getSinceLastRun(String jobName) {
        Long duration = getBuildDuration(jobName);
        return String.valueOf(duration) + " min" + ((duration == 1) ? "" : "s");
    }

    private String getLastRunPassFail(String jobName) {
        return "";
    }

    private String getLastRunStatus(String jobName) {
        return "unknown";
    }
    private final int millisecondsInAMinute = 60000;
    private String getLastBuildDuration(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return String.valueOf(tli.getLastBuild().getDuration()/millisecondsInAMinute);
    }

    public String getTimeUnit(String jobName) {
        return "min" + ((getBuildDuration(jobName) == 1) ? "" : "s");
  }

  public String getStatus(String jobName) {
      Project tli = (Project)(Hudson.getInstance().getItem(jobName));

      if (tli.isBuilding()) {
          return "running";
      }
      else {
          if (tli.getLastBuild().getBuildStatusSummary().message.toString().equalsIgnoreCase("stable")) {
              return "passed";
          }
          else {
              return "failed";
          }
      }
  }

  public String getStatus2(String jobName) {
      Project tli = (Project)(Hudson.getInstance().getItem(jobName));
      if (tli.isBuilding()) {
          return "running-time";
      }
      else {
          return "";
      }
  }
}
