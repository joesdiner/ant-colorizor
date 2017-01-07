// Ant Colorizor
// v.9, released 1-7-2017
//
// PS: I'm not a regular java programmer, so I apologize if my code hurts the brains of
//     "real" java programmers, but it works and is good enough for me
package org.dogsplayingpoker.ant.PropertyHelpers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.BuildException;

public class ColorPropertyEvaluator implements PropertyHelper.PropertyEvaluator {
  private final String PROP_PREFIX = "color:";

  private static final String DISABLE_COLOR_PROP = "org.dogsplayingpoker.ant.disable-color";
  
  private final String ESC_CHAR = "\u001b";
  private final String PREFIX = "[";
  private final String SEPARATOR = ";";
  private final String TERMINATOR = "m";

  private final String BG_COLOR_PREFIX = "bg-";
  // Background color codes are 10 bigger than foreground colors
  private final int BACKGROUND_COLOR_OFFSET = 10;
  
  private final String FORMAT_RESET_PREFIX = "reset-";
  // Reset formatting codes are 20 bigger than foreground colors
  private final int FORMAT_RESET_OFFSET = 20;

  private static final Map<String, Integer> Colors;
  private static final Map<String, Integer> Formats;
  static {
      Map<String, Integer> colorMap = new HashMap<>();
      colorMap.put("black",  30);
      colorMap.put("red",    31);
      colorMap.put("green",  32);
      colorMap.put("brown",  33);
      colorMap.put("yellow", 33);
      colorMap.put("blue",   34);
      colorMap.put("purple", 35);
      colorMap.put("cyan",   36);
      colorMap.put("gray",   37);
      Colors = Collections.unmodifiableMap(colorMap);

      Map<String, Integer> formatMap = new HashMap<>();
      formatMap.put("normal",    0);
      formatMap.put("bold",      1);
      formatMap.put("dim",       2);
      formatMap.put("underline", 4);
      formatMap.put("blink",     5);
      formatMap.put("reverse",   7);
      formatMap.put("hidden",    8);
      Formats = Collections.unmodifiableMap(formatMap);
  }

  public Object evaluate(String prop, PropertyHelper helper) {
    if (prop.startsWith(PROP_PREFIX) && helper.getProject() != null) {
      String disableColor = helper.getProject().getProperty(DISABLE_COLOR_PROP);
      if (Boolean.parseBoolean(disableColor)) {
        // Globally colors are disabled, return an empty string
        return "";
      }
   
      String ansiSeqn = ESC_CHAR + PREFIX;
   
      String formatting = prop.substring(PROP_PREFIX.length());
      if (formatting.equals("reset")) {
        ansiSeqn += TERMINATOR;
        return ansiSeqn;
      }
      
      boolean formatted = false;
      boolean fgColor = false;
      boolean bgColor = false;
      boolean resetFormat = false;
      
      String[] formats = formatting.split(",");
      for (int i=0; i<formats.length; ++i) {
        String token = formats[i].trim();
        Integer code;
        
        if (i > 0) {
          ansiSeqn += SEPARATOR;
        }
        
        // Foreground Color
        code = Colors.get(token);  
        if (code != null) {
          if (fgColor) {
            throw new BuildException("Colorize double fg color: '" + token + "' in [" + prop + "]");
          }
          ansiSeqn += code;
          fgColor = true;
          continue;
        }

        // Formatting
        code = Formats.get(token);
        if (code != null) {
          if (formatted) {
            throw new BuildException("Colorize double formatting: '" + token + "' in [" + prop + "]");
          }
          ansiSeqn += code;
          formatted = true;
          continue;
        }
        
        // Background Color
        if (token.startsWith(BG_COLOR_PREFIX)) {
          String bgColorTrimmed = token.substring(BG_COLOR_PREFIX.length());
          code = Colors.get(bgColorTrimmed);
          if (bgColor) {
            throw new BuildException("Colorize double bg color: '" + token + "' in [" + prop + "]");
          }
          ansiSeqn += code + BACKGROUND_COLOR_OFFSET;
          bgColor = true;
          continue;
        }
                
        // Reset Format
        if (token.startsWith(FORMAT_RESET_PREFIX)) {
          String resetFormatTrimmed = token.substring(FORMAT_RESET_PREFIX.length());
          code = Formats.get(resetFormatTrimmed);
          if (resetFormat) {
            throw new BuildException("Colorize double reset format: '" + token + "' in [" + prop + "]");
          }
          ansiSeqn += code + FORMAT_RESET_OFFSET;
          resetFormat = true;
          continue;
        }
        
        // Perhaps a raw control code
        try {
          code = Integer.parseInt(token);
          // If control code, trust the user has provided a valid one
          ansiSeqn += code;
        }
        catch (Exception e) {
          // If we reach here the token is invalid
          throw new BuildException("Unknown colorize token: '" + token + "' in [" + prop + "]");
        }
      }
      
      ansiSeqn += TERMINATOR;
      return ansiSeqn;
    }
    else {
      // not a color prefix
      return null;
    }
  }
}
