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

    /**
   * Notify Hudson we're implementing a new View
   * @author jrenaut
   */
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

    public String getBuildTime() {return getJobName("zMon_Build"); }
    public String getDeployTime() { return getJobName("zMon_Deploy"); }
    public String getTestsTime() { return getJobName("zMon_Test"); }
    public String getMatureTime() {return getJobName("zMon_Mature"); }
    public String getRegressionTime() { return getJobName("zMon_Regression"); }

    public String getBuildTimeUnit() {return getTimeUnit("zMon_Build"); }
    public String getDeployTimeUnit() { return getTimeUnit("zMon_Deploy"); }
    public String getTestsTimeUnit() { return getTimeUnit("zMon_Test"); }
    public String getMatureTimeUnit() {return getTimeUnit("zMon_Mature"); }
    public String getRegressionTimeUnit() { return getTimeUnit("zMon_Regression"); }

    public String getBuildSinceLastRun() { return getSinceLastRun("zMon_Build"); }
    public String getDeploySinceLastRun() { return getSinceLastRun("zMon_Deploy"); }
    public String getTestsSinceLastRun() { return getSinceLastRun("zMon_Test"); }
    public String getMatureSinceLastRun() { return getSinceLastRun("zMon_Mature"); }
    public String getRegressionSinceLastRun() { return getSinceLastRun("zMon_Regression"); }

    public String getBuildLastRunPassFail() { return getLastRunPassFail("zMon_Build"); }
    public String getDeployLastRunPassFail() { return getLastRunPassFail("zMon_Deploy"); }
    public String getTestsLastRunPassFail() { return getLastRunPassFail("zMon_Test"); }
    public String getMatureLastRunPassFail() { return getLastRunPassFail("zMon_Mature"); }
    public String getRegressionLastRunPassFail() { return getLastRunPassFail("zMon_Regression"); }

    public String getBuildLastRunStatus() { return getLastRunStatus("zMon_Build"); }
    public String getDeployLastRunStatus() { return getLastRunStatus("zMon_Deploy"); }
    public String getTestsLastRunStatus() { return getLastRunStatus("zMon_Test"); }
    public String getMatureLastRunStatus() { return getLastRunStatus("zMon_Mature"); }
    public String getRegressionLastRunStatus() { return getLastRunStatus("zMon_Regression"); }

    public String getBuildStatus() { return getStatus("zMon_Build"); }
    public String getDeployStatus() { return getStatus("zMon_Deploy"); }
    public String getTestsStatus() { return getStatus("zMon_Test"); }
    public String getMatureStatus() { return getStatus("zMon_Mature"); }
    public String getRegressionStatus() { return getStatus("zMon_Regression"); }

    public String getBuildStatus2() { return getStatus2("zMon_Build"); }
    public String getDeployStatus2() { return getStatus2("zMon_Deploy"); }
    public String getTestsStatus2() { return getStatus2("zMon_Test"); }
    public String getMatureStatus2() { return getStatus2("zMon_Mature"); }
    public String getRegressionStatus2() { return getStatus2("zMon_Regression"); }


    private Long getBuildDuration(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return (System.currentTimeMillis() - tli.getLastBuild().getTimeInMillis()) / 60000;
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

    private String getJobName(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return String.valueOf(tli.getLastBuild().getDuration()/60000);
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
