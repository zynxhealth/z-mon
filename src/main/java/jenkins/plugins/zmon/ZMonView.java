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

  @Override
  protected void submit(StaplerRequest req) throws ServletException,
        Descriptor.FormException, IOException {
        super.submit(req);
  }

    public String getTeamName() {
        if (this.teamName == null) {
            this.teamName = "Team Z-Mon";
        }
        return  this.teamName;
    }


    public String getBuildTime() {return getJobName("zMon_Build"); }
    public String getDeployTime() { return getJobName("zMon_Deploy"); }
    public String getTestsTime() { return getJobName("zMon_Test"); }
    public String getMatureJobName() {return getJobName("zMon_Mature"); }
    public String getRegressionJobName() { return getJobName("zMon_Regression"); }

    public String getBuildTimeUnit() {return getTimeUnit(); }
    public String getDeployTimeUnit() { return getTimeUnit(); }
    public String getTestsTimeUnit() { return getTimeUnit(); }
    public String getMatureTimeUnit() {return getTimeUnit(); }
    public String getRegressionTimeUnit() { return getTimeUnit(); }

    public String getBuildSinceLastRun() { return getSinceLastRun("zMon_Build"); }
    public String getDeploySinceLastRun() { return getSinceLastRun("zMon_Deploy"); }
    public String getTestsSinceLastRun() { return getSinceLastRun("zMon_Test"); }
    public String getMaturedSinceLastRun() { return getSinceLastRun("zMon_Mature"); }
    public String getRegressionSinceLastRun() { return getSinceLastRun("zMon_Regression"); }

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


    public String getSinceLastRun(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return String.valueOf((System.currentTimeMillis() - tli.getLastBuild().getTimeInMillis()) / 60000) + " mins";
    }

    private String getJobName(String jobName) {
        Project tli = (Project)(Hudson.getInstance().getItem(jobName));
        return String.valueOf(tli.getLastBuild().getDuration()/60000);
    }

    public String getTimeUnit() {
       return "mins";
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
