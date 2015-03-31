package jenkins.plugins.zmon;

import hudson.Extension;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.io.IOException;

public class ZMonView extends ListView {
	private final int MILLISECONDS_IN_A_MINUTE = 60000;
	private final double MINUTES_IN_AN_HOUR = 60.0;


    @DataBoundConstructor
	public ZMonView(String name) {
		super(name);
	}

	@Extension
	public static final class ZMonViewDescriptor extends ViewDescriptor {

		@Override
		public String getDisplayName() {
			return "CFPB Monitor";
		}
	}

	public String getTeamName() { return teamName; }
	public String getTeamLogoURL() { return teamLogoURL; }
	public String getRefresh() { return refresh; }

	public String getActualNameJob1() { return actualNameJob1; }
	public String getActualNameJob2() { return actualNameJob2; }
	public String getActualNameJob3() { return actualNameJob3; }
	public String getActualNameJob4() { return actualNameJob4; }
	public String getActualNameJob5() { return actualNameJob5; }

	public String getDisplayNameJob1() { return displayNameJob1; }
	public String getDisplayNameJob2() { return displayNameJob2; }
	public String getDisplayNameJob3() { return displayNameJob3; }
	public String getDisplayNameJob4() { return displayNameJob4; }
	public String getDisplayNameJob5() { return displayNameJob5; }

	private String teamName;
	private String teamLogoURL;

	private String actualNameJob1;
	private String actualNameJob2;
	private String actualNameJob3;
	private String actualNameJob4;
	private String actualNameJob5;

	private String displayNameJob1;
	private String displayNameJob2;
	private String displayNameJob3;
	private String displayNameJob4;
	private String displayNameJob5;

	private String refresh;

	@Override
	protected void submit(StaplerRequest req) throws ServletException,
	Descriptor.FormException, IOException {
		super.submit(req);
		this.teamName = (req.getParameter("teamName") != null) ? req.getParameter("teamName") : "Team Name";
		this.teamLogoURL = (req.getParameter("teamLogoURL") != null) ? req.getParameter("teamLogoURL") : "";

		this.actualNameJob1 = (req.getParameter("actualNameJob1") != null) ? req.getParameter("actualNameJob1") : "";
		this.actualNameJob2 = (req.getParameter("actualNameJob2") != null) ? req.getParameter("actualNameJob2") : "";
		this.actualNameJob3 = (req.getParameter("actualNameJob3") != null) ? req.getParameter("actualNameJob3") : "";
		this.actualNameJob4 = (req.getParameter("actualNameJob4") != null) ? req.getParameter("actualNameJob4") : "";
		this.actualNameJob5 = (req.getParameter("actualNameJob5") != null) ? req.getParameter("actualNameJob5") : "";

		this.displayNameJob1 = (req.getParameter("displayNameJob1") != null) ? req.getParameter("displayNameJob1") : "Job 1";
		this.displayNameJob2 = (req.getParameter("displayNameJob2") != null) ? req.getParameter("displayNameJob2") : "Job 2";
		this.displayNameJob3 = (req.getParameter("displayNameJob3") != null) ? req.getParameter("displayNameJob3") : "Job 3";
		this.displayNameJob4 = (req.getParameter("displayNameJob4") != null) ? req.getParameter("displayNameJob4") : "Job 4";
		this.displayNameJob5 = (req.getParameter("displayNameJob5") != null) ? req.getParameter("displayNameJob5") : "Job 5";

		this.refresh = (req.getParameter("refresh") != null) ? req.getParameter("refresh") : "10";
	}

	public String getBuildNumber() { return String.valueOf((int) getLastBuild(actualNameJob1).number); }

	public String getJob1Time() { return getCurrentBuildDuration(actualNameJob1); }
	public String getJob2Time() { return getCurrentBuildDuration(actualNameJob2); }
	public String getJob3Time() { return getCurrentBuildDuration(actualNameJob3); }
	public String getJob4Time() { return getCurrentBuildDuration(actualNameJob4); }
	public String getJob5Time() { return getCurrentBuildDuration(actualNameJob5); }

	public String getJob1PercentCompleted() { return getPercentCompleted(actualNameJob1); }
	public String getJob2PercentCompleted() { return getPercentCompleted(actualNameJob2); }
	public String getJob3PercentCompleted() { return getPercentCompleted(actualNameJob3); }
	public String getJob4PercentCompleted() { return getPercentCompleted(actualNameJob4); }
	public String getJob5PercentCompleted() { return getPercentCompleted(actualNameJob5); }

	public String getJob1SinceLastRun() { return getTimeElapsedSinceLastRun(actualNameJob1); }
	public String getJob2SinceLastRun() { return getTimeElapsedSinceLastRun(actualNameJob2); }
	public String getJob3SinceLastRun() { return getTimeElapsedSinceLastRun(actualNameJob3); }
	public String getJob4SinceLastRun() { return getTimeElapsedSinceLastRun(actualNameJob4); }
	public String getJob5SinceLastRun() { return getTimeElapsedSinceLastRun(actualNameJob5); }

	public String getJob1LastRunPassFailAborted() { return getLastRunPassFailAborted(actualNameJob1); }
	public String getJob2LastRunPassFailAborted() { return getLastRunPassFailAborted(actualNameJob2); }
	public String getJob3LastRunPassFailAborted() { return getLastRunPassFailAborted(actualNameJob3); }
	public String getJob4LastRunPassFailAborted() { return getLastRunPassFailAborted(actualNameJob4); }
	public String getJob5LastRunPassFailAborted() { return getLastRunPassFailAborted(actualNameJob5); }

	public String getJob1LastRunStatus() { return getLastRunStatus(actualNameJob1); }
	public String getJob2LastRunStatus() { return getLastRunStatus(actualNameJob2); }
	public String getJob3LastRunStatus() { return getLastRunStatus(actualNameJob3); }
	public String getJob4LastRunStatus() { return getLastRunStatus(actualNameJob4); }
	public String getJob5LastRunStatus() { return getLastRunStatus(actualNameJob5); }

    public String getJob1Url() { return getBuildUrl(actualNameJob1); }
    public String getJob2Url() { return getBuildUrl(actualNameJob2);}
    public String getJob3Url() { return getBuildUrl(actualNameJob3); }
	public String getJob4Url() { return getBuildUrl(actualNameJob4); }
	public String getJob5Url() { return getBuildUrl(actualNameJob5); }

	public String getJob1Status() { return getStatus(actualNameJob1); }
	public String getJob2Status() { return getStatus(actualNameJob2); }
	public String getJob3Status() { return getStatus(actualNameJob3); }
	public String getJob4Status() { return getStatus(actualNameJob4); }
	public String getJob5Status() { return getStatus(actualNameJob5); }

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
    private String getBuildUrl(String jobName){
        String buildUrl=(Hudson.getInstance().getItem(jobName).getShortUrl());
        return buildUrl;
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

	private String getLastRunPassFailAborted(String jobName) {
		String lastBuildStatusSummary = getLastRunStatus(jobName).toLowerCase();
		if ((lastBuildStatusSummary.equals("stable")) || (lastBuildStatusSummary.equals("back to normal"))) {
			return "pass";
		} else if (lastBuildStatusSummary.equals("aborted")) {
			return "aborted";
		} 
		return "fail";
	}

	private String getLastRunStatus(String jobName) {
		return getLastBuild(jobName).getBuildStatusSummary().message.toString();
	}

	private String getStatus(String jobName) {
		AbstractProject tli = (AbstractProject) (Hudson.getInstance().getItem(jobName));

		if (tli.isBuilding()) {
			return "running";
		} else {
			if (tli.getLastBuild().getResult().toString().equalsIgnoreCase("success")) {
				return "passed";
			} else if (tli.getLastBuild().getResult().toString().equalsIgnoreCase("aborted")) {
				return "aborted";
			} else {
				return "failed";
			}
		}
	}

	private String convertDurationToDisplay(long durationInMillis) {
		long durationInMins = durationInMillis / MILLISECONDS_IN_A_MINUTE;
		if (durationInMins > MINUTES_IN_AN_HOUR) {
			return "<strong>" + String.valueOf(round(durationInMins / MINUTES_IN_AN_HOUR, 2, BigDecimal.ROUND_HALF_UP)) + "</strong> hours";
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
