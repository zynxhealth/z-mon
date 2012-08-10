package jenkins.plugins.zmon;
import hudson.Plugin;
import hudson.model.ListView;
import hudson.model.Job;
import hudson.model.Hudson;
public class ZMonView extends ListView{
  public ZMonView (String name) {
    super(name);
  }
}
