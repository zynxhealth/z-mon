package jenkins.plugins.monitor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.Run;
import hudson.model.StreamBuildListener;
import hudson.model.TopLevelItem;
import hudson.model.ViewDescriptor;
import hudson.model.listeners.RunListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

public class ContinuousMonitorView extends ListView {

    private final static Logger logger = Logger.getLogger(ContinuousMonitorView.class);

    private static final String MANAGED_PLUGINS_PLUGIN_NAME = "Continuous-Monitor";
    private static final String JENKINS_TEST_ENVIRONMENT_SYSTEM_VARIABLE = "Test_Environment";
    private final int MILLISECONDS_IN_A_MINUTE = 60000;
    private final double MINUTES_IN_AN_HOUR = 60.0;

    private String teamName;
    private String teamLogoURL;
    private String environmentVars;

    private String actualNameJob1;
    private String actualNameJob2;
    private String actualNameJob3;
    private String actualNameJob4;
    private String actualNameJob5;
    private String actualNameJob6;
    private String actualNameJob7;
    private String actualNameJob8;

    private String displayNameJob1;
    private String displayNameJob2;
    private String displayNameJob3;
    private String displayNameJob4;
    private String displayNameJob5;
    private String displayNameJob6;
    private String displayNameJob7;
    private String displayNameJob8;

    private String refresh;

    @DataBoundConstructor
    public ContinuousMonitorView(String name) {
        super(name);
    }

    @Extension
    public static final class ContinuousMonitorViewDescriptor extends ViewDescriptor {

        @Override
        public String getDisplayName() {
            return MANAGED_PLUGINS_PLUGIN_NAME;
        }

    }

    @Override
    protected void submit(StaplerRequest req) throws ServletException,
            Descriptor.FormException, IOException {

        super.submit(req);
        this.teamName = (req.getParameter("teamName") != null) ? req.getParameter("teamName") : "Team Name";
        this.teamLogoURL = (req.getParameter("teamLogoURL") != null) ? req.getParameter("teamLogoURL") : "";
        this.environmentVars = (req.getParameter("environmentVars") != null) ? req.getParameter("environmentVars") : "";

        this.actualNameJob1 = (req.getParameter("actualNameJob1") != null) ? req.getParameter("actualNameJob1") : "";
        this.actualNameJob2 = (req.getParameter("actualNameJob2") != null) ? req.getParameter("actualNameJob2") : "";
        this.actualNameJob3 = (req.getParameter("actualNameJob3") != null) ? req.getParameter("actualNameJob3") : "";
        this.actualNameJob4 = (req.getParameter("actualNameJob4") != null) ? req.getParameter("actualNameJob4") : "";
        this.actualNameJob5 = (req.getParameter("actualNameJob5") != null) ? req.getParameter("actualNameJob5") : "";
        this.actualNameJob6 = (req.getParameter("actualNameJob6") != null) ? req.getParameter("actualNameJob6") : "";
        this.actualNameJob7 = (req.getParameter("actualNameJob7") != null) ? req.getParameter("actualNameJob7") : "";
        this.actualNameJob8 = (req.getParameter("actualNameJob8") != null) ? req.getParameter("actualNameJob8") : "";

        this.displayNameJob1 = (req.getParameter("displayNameJob1") != null) ? req.getParameter("displayNameJob1") : "Job 1";
        this.displayNameJob2 = (req.getParameter("displayNameJob2") != null) ? req.getParameter("displayNameJob2") : "Job 2";
        this.displayNameJob3 = (req.getParameter("displayNameJob3") != null) ? req.getParameter("displayNameJob3") : "Job 3";
        this.displayNameJob4 = (req.getParameter("displayNameJob4") != null) ? req.getParameter("displayNameJob4") : "Job 4";
        this.displayNameJob5 = (req.getParameter("displayNameJob5") != null) ? req.getParameter("displayNameJob5") : "Job 5";
        this.displayNameJob6 = (req.getParameter("displayNameJob6") != null) ? req.getParameter("displayNameJob6") : "Job 6";
        this.displayNameJob7 = (req.getParameter("displayNameJob7") != null) ? req.getParameter("displayNameJob7") : "Job 7";
        this.displayNameJob8 = (req.getParameter("displayNameJob8") != null) ? req.getParameter("displayNameJob8") : "Job 8";

        this.refresh = (req.getParameter("refresh") != null) ? req.getParameter("refresh") : "10";
    }

    /**
     * Get last job execution environment variable
     *
     * @return last run environment information
     */
    private String getLastEnvironmentVars() {

        StringBuilder envVariablesBuilder = new StringBuilder();
        EnvVars envVars = getLastRunEnvVariables(actualNameJob1);
        for (String envVar : envVars.keySet()) {
            envVariablesBuilder.append(envVar);
            envVariablesBuilder.append(" ");
        }
        return envVariablesBuilder.toString();
    }

    /**
     * Get last test environment name defined in the system variable Test_Environment
     *
     * @param jobName Jenkins job
     * @return last test environment name (dev, test, prod)
     */
    private String getLastTestEnv(final String jobName) {

        EnvVars envVars = getLastRunEnvVariables(jobName);
        for (String envVar : envVars.keySet()) {
            if (StringUtils.containsIgnoreCase(envVar, JENKINS_TEST_ENVIRONMENT_SYSTEM_VARIABLE)) {
                return "ENV: " + envVars.get(envVar);
            }
        }
        return "";
    }

    /**
     * Get current build duration for a Jenkins job
     *
     * @param jobName Jenkins job
     * @return build duration for a Jenkins job
     */
    private String getCurrentBuildDuration(final String jobName) {

        AbstractProject tli = findItemByJobName(jobName);
        if (tli.getLastBuild().isBuilding()) {
            return convertDurationToDisplay((System.currentTimeMillis() - tli.getLastBuild().getTimeInMillis()));
        } else {
            return convertDurationToDisplay(tli.getLastBuild().getDuration());
        }
    }

    /**
     * Get the percentage completed of a Jenkins job execution duration
     *
     * @param jobName Jenkins job name
     * @return percentage completed of entire job execution duration
     */
    private String getPercentCompleted(final String jobName) {

        AbstractProject tli = findItemByJobName(jobName);
        AbstractBuild lastBuild = tli.getLastBuild();
        long percentCompleted = 0;
        long duration;
        long estimatedDuration;

        if (lastBuild.isBuilding()) {
            duration = System.currentTimeMillis() - lastBuild.getTimeInMillis();
            estimatedDuration = lastBuild.getEstimatedDuration();

            if (estimatedDuration == -1) {
                percentCompleted = 0;
            } else {
                if (duration <= estimatedDuration) {
                    percentCompleted = (long) ((double) duration / (double) estimatedDuration * 100);
                } else {
                    percentCompleted = 100;
                }
            }
        }

        return String.valueOf(percentCompleted) + "%";
    }

    /**
     * Get the time elapsed since the last run of the Jenkins job
     *
     * @param jobName Jenkins job name
     * @return time elapsed since last run in .ms
     */
    private String getTimeElapsedSinceLastRun(final String jobName) {
        return convertDurationToDisplay((System.currentTimeMillis() - getLastBuild(jobName).getTimeInMillis()));
    }

    /**
     * Get build URL
     *
     * @param jobName Jenkins job name
     * @return build URL
     */
    private String getBuildUrl(final String jobName) {

        AbstractProject tli = findItemByJobName(jobName);
        return tli.getShortUrl();
    }

    /**
     * Get last build
     *
     * @param jobName Jenkins job name
     * @return last build
     */
    private AbstractBuild getLastBuild(final String jobName) {

        AbstractProject tli = findItemByJobName(jobName);
        AbstractBuild lastBuild = tli.getLastBuild();
        if (lastBuild.isBuilding()) {
            return (AbstractBuild) tli.getBuilds().get(1);
        } else {
            return lastBuild;
        }
    }

    /**
     * Find job by job name wherever it may be in the stack
     *
     * @param jobName desired Jenkins job to find
     * @return matching Jenkins job
     */
    private AbstractProject findItemByJobName(String jobName) {

        List<TopLevelItem> allTopLevelItems = Hudson.getActiveInstance().getItems();
        for (TopLevelItem allTopLevelItem : allTopLevelItems) {

            Collection<Job> secondLevelJobs = (Collection<Job>) allTopLevelItem.getAllJobs();
            for (Job secondLevelJob : secondLevelJobs) {
                if (StringUtils.containsIgnoreCase(secondLevelJob.getName(), jobName)) {
                    logger.debug(String.format("Found Job Name Match.  Expected='%s' :: Actual='%s'", jobName, secondLevelJob.getName()));
                    return (AbstractProject) secondLevelJob;
                }
            }
        }
        return null;
    }

    /**
     * Get last job status
     *
     * @param jobName Jenkins job name
     * @return last job status
     */
    private String getLastRunPassFailAborted(final String jobName) {

        String lastBuildStatusSummary = getLastRunStatus(jobName).toLowerCase();
        if ((lastBuildStatusSummary.equals(Status.STABLE_STATUS)) || (lastBuildStatusSummary.equals(Status.BACK_TO_NORMAL_STATUS))) {
            return Status.PASS_STATUS;
        } else if (lastBuildStatusSummary.equals(Status.ABORTED_STATUS)) {
            return Status.ABORTED_STATUS;
        }
        return Status.FAIL_STATUS;
    }

    /**
     * Get last Jenkins job run status
     *
     * @param jobName Jenkins job name
     * @return last job status
     */
    private String getLastRunStatus(final String jobName) {

        return getLastBuild(jobName).getBuildStatusSummary().message.toString();
    }

    /**
     * Get last job execution environment variables
     *
     * @param jobName Jenkins job name
     * @return last job execution environment variables
     */
    private EnvVars getLastRunEnvVariables(final String jobName) {

        StreamBuildListener listener;
        try {
            Computer e = Computer.currentComputer();
            Charset charset = null;
            if (e != null) {
                charset = e.getDefaultCharset();
            }
            Object e1 = new FileOutputStream(getLastBuild(jobName).getLogFile());
            listener = new StreamBuildListener((OutputStream) e1, charset);
            Run build = getLastBuild(jobName);
            RunListener.fireStarted(build, listener);
            return getLastBuild(jobName).getEnvironment(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the current status for a Jenkins job
     *
     * @param jobName Jenkins job name
     * @return current job status
     */
    private String getStatus(final String jobName) {

        AbstractProject tli = findItemByJobName(jobName);
        if (tli.isBuilding()) {
            return Status.RUNNING_STATUS;
        } else {
            if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(String.valueOf(tli.getLastBuild().getResult()), Status.SUCCESS_STATUS)) {
                return Status.PASSED_STATUS;
            } else if (tli.getLastBuild().getResult().toString().equalsIgnoreCase(Status.ABORTED_STATUS)) {
                return Status.ABORTED_STATUS;
            } else {
                return Status.FAILED_STATUS;
            }
        }
    }

    /**
     * Convert the duration in MS to readable format
     *
     * @param durationInMillis job duration in MS
     * @return HTML used for reporting job duration
     */
    private String convertDurationToDisplay(final long durationInMillis) {

        long durationInMinutes = durationInMillis / MILLISECONDS_IN_A_MINUTE;
        if (durationInMinutes > MINUTES_IN_AN_HOUR) {
            return "<strong>" + String.valueOf(round(durationInMinutes / MINUTES_IN_AN_HOUR, 2, BigDecimal.ROUND_HALF_UP)) + "</strong> hours";
        } else {
            return "<strong>" + String.valueOf(durationInMinutes) + "</strong> min" + ((durationInMinutes == 1) ? "" : "s");
        }
    }

    /**
     * Round the number of minutes in an hour up
     *
     * @param unRounded    un-rounded minutes
     * @param precision    level of precision
     * @param roundingMode mode to round up/down
     * @return
     */
    private static double round(double unRounded, int precision, int roundingMode) {

        BigDecimal bd = new BigDecimal(unRounded);
        BigDecimal rounded = bd.setScale(precision, roundingMode);
        return rounded.doubleValue();
    }

    public String getEnvironmentVarsCompleted() {
        return getLastEnvironmentVars();
    }

    public String getJob1LastTestEnv() {
        return getLastTestEnv(actualNameJob1);
    }

    public String getJob2LastTestEnv() {
        return getLastTestEnv(actualNameJob2);
    }

    public String getJob3LastTestEnv() {
        return getLastTestEnv(actualNameJob3);
    }

    public String getJob4LastTestEnv() {
        return getLastTestEnv(actualNameJob4);
    }

    public String getJob5LastTestEnv() {
        return getLastTestEnv(actualNameJob5);
    }

    public String getJob6LastTestEnv() {
        return getLastTestEnv(actualNameJob6);
    }

    public String getJob7LastTestEnv() {
        return getLastTestEnv(actualNameJob7);
    }

    public String getJob8LastTestEnv() {
        return getLastTestEnv(actualNameJob8);
    }

    public String getBuildNumber() {
        return String.valueOf((int) getLastBuild(actualNameJob1).number);
    }

    public String getJob1Time() {
        return getCurrentBuildDuration(actualNameJob1);
    }

    public String getJob2Time() {
        return getCurrentBuildDuration(actualNameJob2);
    }

    public String getJob3Time() {
        return getCurrentBuildDuration(actualNameJob3);
    }

    public String getJob4Time() {
        return getCurrentBuildDuration(actualNameJob4);
    }

    public String getJob5Time() {
        return getCurrentBuildDuration(actualNameJob5);
    }

    public String getJob6Time() {
        return getCurrentBuildDuration(actualNameJob6);
    }

    public String getJob7Time() {
        return getCurrentBuildDuration(actualNameJob7);
    }

    public String getJob8Time() {
        return getCurrentBuildDuration(actualNameJob8);
    }

    public String getJob1PercentCompleted() {
        return getPercentCompleted(actualNameJob1);
    }

    public String getJob2PercentCompleted() {
        return getPercentCompleted(actualNameJob2);
    }

    public String getJob3PercentCompleted() {
        return getPercentCompleted(actualNameJob3);
    }

    public String getJob4PercentCompleted() {
        return getPercentCompleted(actualNameJob4);
    }

    public String getJob5PercentCompleted() {
        return getPercentCompleted(actualNameJob5);
    }

    public String getJob6PercentCompleted() {
        return getPercentCompleted(actualNameJob6);
    }

    public String getJob7PercentCompleted() {
        return getPercentCompleted(actualNameJob7);
    }

    public String getJob8PercentCompleted() {
        return getPercentCompleted(actualNameJob8);
    }

    public String getJob1SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob1);
    }

    public String getJob2SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob2);
    }

    public String getJob3SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob3);
    }

    public String getJob4SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob4);
    }

    public String getJob5SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob5);
    }

    public String getJob6SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob6);
    }

    public String getJob7SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob7);
    }

    public String getJob8SinceLastRun() {
        return getTimeElapsedSinceLastRun(actualNameJob8);
    }

    public String getJob1LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob1);
    }

    public String getJob2LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob2);
    }

    public String getJob3LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob3);
    }

    public String getJob4LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob4);
    }

    public String getJob5LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob5);
    }

    public String getJob6LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob6);
    }

    public String getJob7LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob7);
    }

    public String getJob8LastRunPassFailAborted() {
        return getLastRunPassFailAborted(actualNameJob8);
    }

    public String getJob1LastRunStatus() {
        return getLastRunStatus(actualNameJob1);
    }

    public String getJob2LastRunStatus() {
        return getLastRunStatus(actualNameJob2);
    }

    public String getJob3LastRunStatus() {
        return getLastRunStatus(actualNameJob3);
    }

    public String getJob4LastRunStatus() {
        return getLastRunStatus(actualNameJob4);
    }

    public String getJob5LastRunStatus() {
        return getLastRunStatus(actualNameJob5);
    }

    public String getJob6LastRunStatus() {
        return getLastRunStatus(actualNameJob6);
    }

    public String getJob7LastRunStatus() {
        return getLastRunStatus(actualNameJob7);
    }

    public String getJob8LastRunStatus() {
        return getLastRunStatus(actualNameJob8);
    }

    public String getJob1Url() {
        return getBuildUrl(actualNameJob1);
    }

    public String getJob2Url() {
        return getBuildUrl(actualNameJob2);
    }

    public String getJob3Url() {
        return getBuildUrl(actualNameJob3);
    }

    public String getJob4Url() {
        return getBuildUrl(actualNameJob4);
    }

    public String getJob5Url() {
        return getBuildUrl(actualNameJob5);
    }

    public String getJob6Url() {
        return getBuildUrl(actualNameJob6);
    }

    public String getJob7Url() {
        return getBuildUrl(actualNameJob7);
    }

    public String getJob8Url() {
        return getBuildUrl(actualNameJob8);
    }

    public String getJob1Status() {
        return getStatus(actualNameJob1);
    }

    public String getJob2Status() {
        return getStatus(actualNameJob2);
    }

    public String getJob3Status() {
        return getStatus(actualNameJob3);
    }

    public String getJob4Status() {
        return getStatus(actualNameJob4);
    }

    public String getJob5Status() {
        return getStatus(actualNameJob5);
    }

    public String getJob6Status() {
        return getStatus(actualNameJob6);
    }

    public String getJob7Status() {
        return getStatus(actualNameJob7);
    }

    public String getJob8Status() {
        return getStatus(actualNameJob8);
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLogoURL() {
        return teamLogoURL;
    }

    public String getEnvironmentVars() {
        return environmentVars;
    }

    public String getRefresh() {
        return refresh;
    }

    public String getActualNameJob1() {
        return actualNameJob1;
    }

    public String getActualNameJob2() {
        return actualNameJob2;
    }

    public String getActualNameJob3() {
        return actualNameJob3;
    }

    public String getActualNameJob4() {
        return actualNameJob4;
    }

    public String getActualNameJob5() {
        return actualNameJob5;
    }

    public String getActualNameJob6() {
        return actualNameJob6;
    }

    public String getDisplayNameJob1() {
        return displayNameJob1;
    }

    public String getDisplayNameJob2() {
        return displayNameJob2;
    }

    public String getDisplayNameJob3() {
        return displayNameJob3;
    }

    public String getDisplayNameJob4() {
        return displayNameJob4;
    }

    public String getDisplayNameJob5() {
        return displayNameJob5;
    }

    public String getDisplayNameJob6() {
        return displayNameJob6;
    }

    public String getDisplayNameJob7() {
        return displayNameJob7;
    }

    public String getDisplayNameJob8() {
        return displayNameJob8;
    }

}