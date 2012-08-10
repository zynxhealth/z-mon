package jenkins.plugins.zmon;
import hudson.Extension;
import hudson.Plugin;
import hudson.model.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;


public class ZMonView extends ListView{

    private Integer numColumns = 1;
    @DataBoundConstructor
    public ZMonView(String name, Integer numColumns) {
        super(name);
        this.numColumns = numColumns != null ? numColumns : 1;
    }
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

}
