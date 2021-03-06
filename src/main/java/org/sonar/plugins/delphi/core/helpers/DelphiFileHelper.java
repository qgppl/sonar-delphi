/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions and Fabricio Colombo
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 * Fabricio Colombo (fabricio.colombo.mva@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.core.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.plugins.delphi.DelphiPlugin;
import org.sonar.plugins.delphi.utils.DelphiUtils;

/**
 * Helper class doing file operations
 */
public class DelphiFileHelper {

  private final Settings settings;
  private final FileSystem fs;

  /**
   * ctor
   * 
   * @param settings Settings
   */
  public DelphiFileHelper(Settings settings, FileSystem fs) {
    this.settings = settings;
    this.fs = fs;
  }

  /**
   * Is file in excluded list?
   * 
   * @param delphiFile File to check
   * @param excludedSources Excluded paths
   * @return True if file is excluded, false otherwise
   */
  public boolean isExcluded(String fileName, List<File> excludedSources) {
    if (excludedSources == null) {
      return false;
    }
    for (File excludedDir : excludedSources) {
      String normalizedFileName = DelphiUtils.normalizeFileName(fileName.toLowerCase());
      String excludedDirNormalizedPath = DelphiUtils.normalizeFileName(excludedDir.getAbsolutePath()
        .toLowerCase());
      if (normalizedFileName.startsWith(excludedDirNormalizedPath)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Is file excluded?
   * 
   * @param delphiFile File to check
   * @param excludedSources List of excluded sources
   * @return True if file is excluded, false otherwise
   */
  public boolean isExcluded(File delphiFile, List<File> excludedSources) {
    return isExcluded(delphiFile.getAbsolutePath(), excludedSources);
  }

  /**
   * Gets code coverage excluded directories
   * 
   * @return List of excluded directories, empty list if none
   */
  public List<File> getCodeCoverageExcludedDirectories(Project project) {
    List<File> list = new ArrayList<File>();

    String[] sources = settings.getStringArray(DelphiPlugin.CC_EXCLUDED_KEY);
    if (sources == null || sources.length == 0) {
      return list;
    }
    for (String path : sources) {
      if (StringUtils.isEmpty(path)) {
        continue;
      }
      File excluded = DelphiUtils.resolveAbsolutePath(fs.baseDir().getAbsolutePath(), path.trim());
      if (!excluded.exists()) {
        DelphiUtils.LOG.warn("Excluded code coverage path does not exist: " + excluded.getAbsolutePath());
      } else if (!excluded.isDirectory()) {
        DelphiUtils.LOG.warn("Excluded code coverage path is not a directory: " + excluded.getAbsolutePath());
      } else {
        list.add(excluded);
      }
    }
    return list;
  }

}
