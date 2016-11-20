/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.vcs.log.ui.history;

import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.vcs.log.VcsLogFilterCollection;
import com.intellij.vcs.log.VcsLogFilterUi;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.ui.AbstractVcsLogUi;
import com.intellij.vcs.log.ui.VcsLogColorManager;
import com.intellij.vcs.log.ui.highlighters.CurrentBranchHighlighter;
import com.intellij.vcs.log.ui.highlighters.MyCommitsHighlighter;
import com.intellij.vcs.log.ui.highlighters.VcsLogHighlighterFactory;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;
import com.intellij.vcs.log.visible.VisiblePackRefresher;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class FileHistoryUi extends AbstractVcsLogUi {
  @NotNull private static final List<String> HIGHLIGHTERS = Arrays.asList(MyCommitsHighlighter.Factory.ID,
                                                                          CurrentBranchHighlighter.Factory.ID);
  @NotNull private final FileHistoryFilterUi myFilterUi;
  @NotNull private final FileHistoryPanel myFileHistoryPanel;

  public FileHistoryUi(@NotNull VcsLogData logData,
                       @NotNull Project project,
                       @NotNull VcsLogColorManager manager,
                       @NotNull VisiblePackRefresher refresher,
                       @NotNull FilePath path) {
    super(logData, project, manager, refresher);

    myFilterUi = new FileHistoryFilterUi(path);
    myFileHistoryPanel = new FileHistoryPanel(this, logData, myVisiblePack);

    myRefresher.onFiltersChange(myFilterUi.getFilters());

    for (VcsLogHighlighterFactory factory : ContainerUtil.filter(Extensions.getExtensions(LOG_HIGHLIGHTER_FACTORY_EP, myProject),
                                                                 f -> HIGHLIGHTERS.contains(f.getId()))) {
      getTable().addHighlighter(factory.createHighlighter(logData, this));
    }
  }

  @NotNull
  @Override
  public VcsLogFilterUi getFilterUi() {
    return myFilterUi;
  }

  @Override
  public boolean areGraphActionsEnabled() {
    return false;
  }

  @Override
  public boolean isCompactReferencesView() {
    return true;
  }

  @Override
  public boolean isShowTagNames() {
    return false;
  }

  @Override
  public boolean isMultipleRoots() {
    return false;
  }

  @Override
  public boolean isShowRootNames() {
    return false;
  }

  public boolean isShowDetails() {
    return true;
  }

  @Override
  public boolean isHighlighterEnabled(@NotNull String id) {
    return HIGHLIGHTERS.contains(id);
  }

  @Override
  protected void onVisiblePackUpdated(boolean permGraphChanged) {
    myFileHistoryPanel.updateDataPack(myVisiblePack, permGraphChanged);
  }

  @NotNull
  @Override
  public VcsLogGraphTable getTable() {
    return myFileHistoryPanel.getGraphTable();
  }

  @NotNull
  @Override
  public Component getMainComponent() {
    return myFileHistoryPanel;
  }

  @Override
  protected VcsLogFilterCollection getFilters() {
    return myFilterUi.getFilters();
  }
}
