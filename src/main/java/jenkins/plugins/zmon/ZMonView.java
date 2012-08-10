package jenkins.plugins.zmon;
import hudson.Plugin;
import hudson.model.ListView;
import hudson.model.Job;
import hudson.model.Hudson;
import hudson.model.ViewDescriptor;



public class ZMonView extends ListView{
  /**
   * C'tor<meta  />
   * @param name the name of the view
   * @param numColumns the number of columns to use on the layout (work in progress)
   */
  public ZMonView(String name) {
    super(name);
  }

  /**
   * Notify Hudson we're implementing a new View
   * @author jrenaut
   */
//  @Extension
  public static final class ZMonViewDescriptor extends ViewDescriptor {

    @Override
    public String getDisplayName() {
      return "Zynx Monitor";
    }

  }
}
