package com.csz.okhttp.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author caishuzhan
 */
public class CloseUtil{

  public static void close(Closeable... close){
      if (close == null) return;
      for (Closeable closeable : close) {
          if (closeable == null ) continue;
          try {
              closeable.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }
}
