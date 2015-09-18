
package org.opensciencegrid.gums.unix;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.LastErrorException;
import com.sun.jna.ptr.PointerByReference;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class GroupToGid {

  private static LoadingCache<String, Integer> mappingCache;

  static {

    Native.register("c");

    mappingCache = CacheBuilder.newBuilder()
         .maximumSize(1000)
         .expireAfterWrite(60, TimeUnit.SECONDS)
         .build(
              new CacheLoader<String, Integer>() {
                   public Integer load(String name) {
                                                return new Integer(getGidRaw(name));
                   }
              }
          );
  }

  public static int map(String name) throws ExecutionException
  {
    return mappingCache.get(name);
  }

  // The following are Linux-specific.
  // Theoretically, they could be runtime platform dependent, but
  // seem unlikely to change _ever_ for Linux.
  private static final int _SC_GETGR_R_SIZE_MAX = 69;
  private static final int ERANGE = 34;

  private static native NativeLong sysconf(int setting);

  private static native int getgrnam_r(String name, StructGroup group, byte[] buf, long blen, PointerByReference result) throws LastErrorException;

  private static int getGidRaw(String name)
  {
    int e;
    NativeLong initial_size = sysconf(_SC_GETGR_R_SIZE_MAX);
    long blen = initial_size.longValue();
    if (initial_size.longValue() < 1)
    {
      blen = 1024;
    }
    byte[] buf = new byte[(int)blen];
    StructGroup group = new StructGroup();
    PointerByReference result = new PointerByReference();
    e = getgrnam_r(name, group, buf, blen, result);
    while (e == ERANGE)
    {
      if (blen > 1024*1024*100) {throw new RuntimeException("Failed to get GID information, even with 100MB of buffers.");}
      blen = blen * 2;
      buf = new byte[(int)blen];
      e = getgrnam_r(name, group, buf, blen, result);
    }
    // NOTE: the various strings and allocated structs within the returned StructGroup
    // are pointers to the area within 'buf'.  Java MAY MOVE the underlying memory for buf;
    // hence, NEVER ACCESS THE POINTERS as they may be invalid as soon getgrnam_r returns
    // back to the JVM.
    //
    // DO NOT COPY/PASTE THIS CODE AND USE GR_MEM!
    //
    if (e != 0)
    {
      throw new LastErrorException(e);
    }
    if (result.getValue() == null)
    {
      return -1;
    }
    return group.gr_gid;
  }

}

