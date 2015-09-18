
package org.opensciencegrid.gums.unix;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

public class StructGroup extends Structure {

  public String gr_name; // Group name.
  public String gr_passwd; // Group password (?!?)
  public int gr_gid; // Group GID
  public PointerByReference gr_mem; // char**; Group members.  Likely not safe to use!

  @Override
  protected List getFieldOrder() {
    return Arrays.asList(new String[] {
      "gr_name", "gr_passwd", "gr_gid", "gr_mem",
    });
  }

}

