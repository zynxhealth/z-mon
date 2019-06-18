package jenkins.plugins.monitor;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.ListView;
import hudson.model.ViewDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ContinuousMonitorView extends ListView {

    private final static Logger logger = Logger.getLogger(ContinuousMonitorView.class);

    private static final String MANAGED_PLUGINS_PLUGIN_NAME = "Continuous-Monitor";
    private static final String MAVEN_JOB_ENVIRONMENT_RUNTIME_VARIABLE = "test.env=";
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
     * Get last test environment name defined in the system variable Test_Environment
     *
     * @param jobName Jenkins job
     * @return last test environment name (dev, test, prod)
     */
    private String getLastTestEnv(final String jobName) {

        String environment = getLastRunEnvVariables(jobName);
        if (StringUtils.isNotEmpty(environment)) {
            return environment;
        } else {
            return "";
        }
    }

    /**
     * Get current build duration for a Jenkins job
     *
     * @param jobName Jenkins job
     * @return build duration for a Jenkins job
     */
    private String getCurrentBuildDuration(final String jobName) {

        Object itemFromJob = findItemByJobName(jobName);
        if (itemFromJob instanceof WorkflowJob) {
            WorkflowJob workflowJob = (WorkflowJob) itemFromJob;
            if (workflowJob.getLastBuild().isBuilding()) {
                return convertDurationToDisplay((System.currentTimeMillis() - workflowJob.getLastBuild().getTimeInMillis()));
            } else {
                return convertDurationToDisplay(workflowJob.getLastBuild().getDuration());
            }
        } else if (itemFromJob instanceof WorkflowMultiBranchProject) {
            WorkflowMultiBranchProject job = (WorkflowMultiBranchProject) itemFromJob;
            WorkflowRun lastBuild = getLastWorkflowRunFromMultiBranch(job);
            if (lastBuild.isBuilding()) {
                return convertDurationToDisplay((System.currentTimeMillis() - lastBuild.getTimeInMillis()));
            } else {
                return convertDurationToDisplay(lastBuild.getDuration());
            }
        } else {
            AbstractProject abstractProject = (AbstractProject) itemFromJob;
            if (abstractProject.getLastBuild().isBuilding()) {
                return convertDurationToDisplay((System.currentTimeMillis() - abstractProject.getLastBuild().getTimeInMillis()));
            } else {
                return convertDurationToDisplay(abstractProject.getLastBuild().getDuration());
            }
        }
    }

    /**
     * Get the percentage completed of a Jenkins job execution duration
     *
     * @param jobName Jenkins job name
     * @return percentage completed of entire job execution duration
     */
    private String getPercentCompleted(final String jobName) {

        long percentCompleted = 0;
        Object itemFromJob = findItemByJobName(jobName);
        if (itemFromJob instanceof WorkflowJob) {

            WorkflowJob workflowJob = (WorkflowJob) itemFromJob;
            WorkflowRun lastBuild = workflowJob.getLastBuild();
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
        } else if (itemFromJob instanceof WorkflowMultiBranchProject) {

            WorkflowMultiBranchProject job = (WorkflowMultiBranchProject) itemFromJob;
            WorkflowRun lastBuild = getLastWorkflowRunFromMultiBranch(job);
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
        } else {

            AbstractProject tli = (AbstractProject) findItemByJobName(jobName);
            AbstractBuild lastBuild = tli.getLastBuild();
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
        }
        return percentCompleted + "%";
    }

    /**
     * Get the time elapsed since the last run of the Jenkins job
     *
     * @param jobName Jenkins job name
     * @return time elapsed since last run in .ms
     */
    private String getTimeElapsedSinceLastRun(final String jobName) {

        Object lastBuild = getLastBuild(jobName);
        if (lastBuild instanceof WorkflowRun) {
            WorkflowRun workflowRun = (WorkflowRun) lastBuild;
            return convertDurationToDisplay((System.currentTimeMillis() - workflowRun.getTimeInMillis()));
        } else {
            AbstractBuild abstractBuild = (AbstractBuild) lastBuild;
            return convertDurationToDisplay((System.currentTimeMillis() - abstractBuild.getTimeInMillis()));
        }
    }

    /**
     * Get build URL.
     *
     * @param jobName Jenkins job name
     * @return build URL
     */
    private String getBuildUrl(final String jobName) {

        Object itemFromJob = findItemByJobName(jobName);
        if (itemFromJob instanceof WorkflowJob) {
            WorkflowJob workflowJob = (WorkflowJob) itemFromJob;
            return workflowJob.getShortUrl();
        } else if (itemFromJob instanceof WorkflowMultiBranchProject) {
            WorkflowMultiBranchProject job = (WorkflowMultiBranchProject) itemFromJob;
            return getShortUrl(job);
        } else {
            AbstractProject tli = (AbstractProject) findItemByJobName(jobName);
            return tli.getShortUrl();
        }
    }

    /**
     * Get the short URL for a WorkflowMultiBranchProject job.
     *
     * @param job job to get name from
     * @return build URL
     */
    public String getShortUrl(WorkflowMultiBranchProject job) {

        String prefix = job.getParent().getUrlChildPrefix();
        String subdir = Util.rawEncode(job.getName());
        return prefix.equals(".") ? subdir + '/' : prefix + '/' + subdir + '/';
    }

    /**
     * Get last build.
     *
     * @param jobName Jenkins job name
     * @return last build
     */
    private Object getLastBuild(final String jobName) {

        Object itemFromJob = findItemByJobName(jobName);
        if (itemFromJob instanceof WorkflowJob) {

            WorkflowJob workflowJob = (WorkflowJob) itemFromJob;
            WorkflowRun lastBuild = workflowJob.getLastBuild();
            if (null == lastBuild) {
                logger.debug(String.format("Unable to find last build for job named '%s", jobName));
            } else if (lastBuild.isBuilding()) {
                return workflowJob.getBuilds().getLastBuild();
            } else {
                return lastBuild;
            }
        } else if (itemFromJob instanceof WorkflowMultiBranchProject) {

            WorkflowMultiBranchProject job = (WorkflowMultiBranchProject) itemFromJob;
            WorkflowRun lastBuild = getLastWorkflowRunFromMultiBranch(job);
            if (null == lastBuild) {
                logger.debug(String.format("Unable to find last build for job named '%s", jobName));
            } else if (lastBuild.isBuilding()) {
                return lastBuild;
            } else {
                return lastBuild;
            }
        } else {

            AbstractProject abstractProject = (AbstractProject) itemFromJob;
            AbstractBuild lastBuild = abstractProject.getLastBuild();
            if (null == lastBuild) {
                logger.debug(String.format("Unable to find last build for job named '%s", jobName));
            } else if (lastBuild.isBuilding()) {
                return abstractProject.getBuilds().getLastBuild();
            } else {
                return lastBuild;
            }
        }
        return null;
    }

    /**
     * Find job by job name wherever it may be in the stack
     *
     * @param jobName desired Jenkins job to find
     * @return matching Jenkins job
     */
    private Object findItemByJobName(String jobName) {

        List<Item> allTopLevelItems = Hudson.getInstanceOrNull().getAllItems();
        for (Item allTopLevelItem : allTopLevelItems) {

            if (allTopLevelItem instanceof WorkflowMultiBranchProject) {
                if (StringUtils.containsIgnoreCase(allTopLevelItem.getName(), jobName)) {
                    logger.debug(String.format("Found WorkflowMultiBranchProject Job Name Match.  Expected='%s' :: Actual='%s'", jobName, allTopLevelItem.getName()));
                    return allTopLevelItem;
                }
            }

            Collection<Job> secondLevelJobs = (Collection<Job>) allTopLevelItem.getAllJobs();
            for (Job secondLevelJob : secondLevelJobs) {

                if (secondLevelJob instanceof WorkflowJob) {
                    if (StringUtils.containsIgnoreCase(secondLevelJob.getName(), jobName)) {
                        logger.debug(String.format("Found Workflow Job Name Match.  Expected='%s' :: Actual='%s'", jobName, secondLevelJob.getName()));
                        return secondLevelJob;
                    }
                } else {
                    if (StringUtils.containsIgnoreCase(secondLevelJob.getName(), jobName)) {
                        logger.debug(String.format("Found Abstract Job Name Match.  Expected='%s' :: Actual='%s'", jobName, secondLevelJob.getName()));
                        return secondLevelJob;
                    }
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

        Object lastBuild = getLastBuild(jobName);
        if (lastBuild instanceof WorkflowRun) {
            WorkflowRun workflowRun = (WorkflowRun) lastBuild;
            return workflowRun.getBuildStatusSummary().message;
        } else {
            AbstractBuild abstractBuild = (AbstractBuild) lastBuild;
            return abstractBuild.getBuildStatusSummary().message;
        }
    }

    /**
     * Get last job execution environment variables
     *
     * @param jobName Jenkins job name
     * @return last job execution environment variables
     */
    private String getLastRunEnvVariables(final String jobName) {

        Object lastBuild = getLastBuild(jobName);
        if (lastBuild instanceof WorkflowRun) {
            WorkflowRun workflowRun = (WorkflowRun) lastBuild;
            try (Reader initialReader = workflowRun.getLogReader()) {
                String targetString = IOUtils.toString(initialReader);
                if (StringUtils.containsIgnoreCase(targetString, MAVEN_JOB_ENVIRONMENT_RUNTIME_VARIABLE)) {
                    return StringUtils.substringBetween(targetString, MAVEN_JOB_ENVIRONMENT_RUNTIME_VARIABLE, " ").trim();
                }
            } catch (Exception e) {
                logger.debug(String.format("Unable to WorkflowRun LastRun Variable due to exception %s", e.getCause()));
            }
        } else {
            AbstractBuild abstractBuild = (AbstractBuild) lastBuild;
            try (Reader initialReader = abstractBuild.getLogReader()) {
                String targetString = IOUtils.toString(initialReader);
                if (StringUtils.containsIgnoreCase(targetString, MAVEN_JOB_ENVIRONMENT_RUNTIME_VARIABLE)) {
                    return StringUtils.substringBetween(targetString, MAVEN_JOB_ENVIRONMENT_RUNTIME_VARIABLE, " ").trim();
                }
            } catch (Exception e) {
                logger.debug(String.format("Unable to AbstractBuild LastRun Variable due to exception %s", e.getCause()));
            }
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

        Object itemFromJob = findItemByJobName(jobName);
        if (itemFromJob instanceof WorkflowJob) {
            WorkflowJob workflowJob = (WorkflowJob) itemFromJob;
            if (workflowJob.isBuilding()) {
                return Status.RUNNING_STATUS;
            } else {
                if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(String.valueOf(workflowJob.getLastBuild().getResult()), Status.SUCCESS_STATUS)) {
                    return Status.PASSED_STATUS;
                } else if (workflowJob.getLastBuild().getResult().toString().equalsIgnoreCase(Status.ABORTED_STATUS)) {
                    return Status.ABORTED_STATUS;
                } else {
                    return Status.FAILED_STATUS;
                }
            }
        } else if (itemFromJob instanceof WorkflowMultiBranchProject) {
            WorkflowMultiBranchProject job = (WorkflowMultiBranchProject) itemFromJob;
            WorkflowRun lastBuild = getLastWorkflowRunFromMultiBranch(job);
            if (lastBuild.isBuilding()) {
                return Status.RUNNING_STATUS;
            } else {
                if (StringUtils.equalsIgnoreCase(String.valueOf(lastBuild.getResult()), Status.SUCCESS_STATUS)) {
                    return Status.PASSED_STATUS;
                } else if (lastBuild.getResult().toString().equalsIgnoreCase(Status.ABORTED_STATUS)) {
                    return Status.ABORTED_STATUS;
                } else {
                    return Status.FAILED_STATUS;
                }
            }
        } else {
            AbstractProject abstractProject = (AbstractProject) itemFromJob;
            if (abstractProject.isBuilding()) {
                return Status.RUNNING_STATUS;
            } else {
                if (StringUtils.equalsIgnoreCase(String.valueOf(abstractProject.getLastBuild().getResult()), Status.SUCCESS_STATUS)) {
                    return Status.PASSED_STATUS;
                } else if (abstractProject.getLastBuild().getResult().toString().equalsIgnoreCase(Status.ABORTED_STATUS)) {
                    return Status.ABORTED_STATUS;
                } else {
                    return Status.FAILED_STATUS;
                }
            }
        }
    }

    /**
     * Returns the last build.
     *
     * @param multiBranchProject WorkflowMultiBranchProject project to interrogate
     * @return last build found
     */
    private WorkflowRun getLastWorkflowRunFromMultiBranch(WorkflowMultiBranchProject multiBranchProject) {

        WorkflowRun lastRun = null;
        List<WorkflowRun> allRuns = new ArrayList<>();

        Collection<WorkflowJob> items = multiBranchProject.getItems();
        logger.debug(String.format("Found %s branches in MultiBranch project", items.size()));

        for (WorkflowJob item : items) {
            allRuns.add(item.getLastBuild());
        }

        try {
            lastRun = allRuns.stream()
                    .max(Comparator.comparing(WorkflowRun::getTimeInMillis))
                    .orElse(allRuns.get(0));
        } catch (Exception e) {
            e.getMessage();
        }

        logger.debug(String.format("Returning last MultiBranch project found named '%s' at %s",
                lastRun.getFullDisplayName(), lastRun.getTime()));
        return lastRun;

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
            return round(durationInMinutes / MINUTES_IN_AN_HOUR, 2, BigDecimal.ROUND_HALF_UP) + " hours";
        } else {
            return durationInMinutes + " min" + ((durationInMinutes == 1) ? "" : "s");
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
        Object lastBuild = getLastBuild(actualNameJob1);
        if (lastBuild instanceof WorkflowRun) {
            WorkflowRun workflowRun = (WorkflowRun) lastBuild;
            return String.valueOf(workflowRun.number);
        } else {
            AbstractBuild abstractBuild = (AbstractBuild) lastBuild;
            return String.valueOf(abstractBuild.number);

        }
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
