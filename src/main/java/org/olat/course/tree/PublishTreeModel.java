/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.course.tree;

import org.olat.core.gui.components.tree.GenericTreeModel;
import org.olat.core.gui.components.tree.GenericTreeNode;
import org.olat.core.gui.components.tree.TreeNode;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.nodes.INode;
import org.olat.core.util.tree.INodeFilter;
import org.olat.course.Structure;

/**
 * Initial Date: Jun 3, 2004
 * 
 * @author Mike Stock
 */
public class PublishTreeModel extends GenericTreeModel implements INodeFilter {

	private final Structure currentRunStructure;

	/**
	 * Build a new publish tree model. Node proxies in the model get the ident's of their correspondents in the CourseEditorTreeModel.
	 * 
	 * @param cetm
	 */
	public PublishTreeModel(final CourseEditorTreeModel cetm, final Structure runStructure, final Translator translator) {
		// build InsertModel (copy of this Structure with all possible
		// insert-positions)
		final GenericTreeNode gtn = new GenericTreeNode();
		gtn.setAccessible(false);
		gtn.setTitle("");
		setRootNode(gtn);
		currentRunStructure = runStructure;

		final CourseEditorTreeNode cnRoot = (CourseEditorTreeNode) cetm.getRootNode();
		gtn.addChild(buildNode(cnRoot, false, false, false));
	}

	private GenericTreeNode buildNode(final CourseEditorTreeNode cetn, boolean parentIsNew, boolean parentIsDeleted, boolean parentIsMoved) {
		final GenericTreeNode gtn = new GenericTreeNode();
		gtn.setIdent(cetn.getIdent());
		gtn.setTitle(cetn.getTitle());
		gtn.setAltText(cetn.getAltText());
		gtn.setIconCssClass(cetn.getIconCssClass());

		if (parentIsNew || parentIsDeleted || parentIsMoved) {
			gtn.setAccessible(false);
		} else {
			gtn.setAccessible(cetn.hasPublishableChanges());
		}
		gtn.setCssClass(cetn.getCssClass());

		final int childcnt = cetn.getChildCount();
		if (childcnt > 0) {
			for (int i = 0; i < childcnt; i++) {
				parentIsNew = parentIsNew || cetn.isNewnode();
				parentIsDeleted = parentIsDeleted || cetn.isDeleted();
				parentIsMoved = parentIsMoved || isMoved(cetn);
				final GenericTreeNode childNode = buildNode((CourseEditorTreeNode) cetn.getChildAt(i), parentIsNew, parentIsDeleted, parentIsMoved);
				// if this is the first new node, enable it
				if (!parentIsNew && cetn.isNewnode()) {
					childNode.setAccessible(true);
				}
				// if this is the first deleted node, enable it
				if (!parentIsDeleted && cetn.isDeleted()) {
					childNode.setAccessible(true);
				}
				// if this is the first moved node, enable it
				if (!parentIsMoved && isMoved(cetn)) {
					childNode.setAccessible(true);
				}
				gtn.insert(childNode, i);
			}
		}
		return gtn;
	}

	public boolean isMoved(final CourseEditorTreeNode cetn) {
		if (cetn.isNewnode() || cetn.isDeleted()) {
			return false;
		} else if (currentRunStructure.getNode(cetn.getCourseNode().getIdent()) == null) {
			// No course node in runstructure
			return true;
		} else {
			final INode node = currentRunStructure.getNode(cetn.getCourseNode().getIdent());
			final String runPath = getPositionPathFor(node);
			final String editorPath = getPositionPathFor(cetn);
			return (!(runPath.equals(editorPath))) && cetn.isDirty(); // TODO: pb: Review : cetn.isDirty() added by chg to FIX OLAT-1662
		}
	}

	private String getPositionPathFor(final INode node) {
		String path = "";
		INode parent = node.getParent();
		if (parent == null) {
			path = "__root__";
		}
		while (parent != null) {
			path += parent.getIdent() + ":" + node.getPosition() + "]";
			parent = parent.getParent();
		}
		return path;
	}

	/**
	 * Check if this publish tree node has any publishable changes.
	 */
	public boolean hasPublishableChanges() {
		return recursiveHasPublishableChanges(getRootNode());
	}

	private boolean recursiveHasPublishableChanges(final TreeNode currentNode) {
		if (currentNode.isAccessible()) { return true; }
		for (int i = 0; i < currentNode.getChildCount(); i++) {
			if (recursiveHasPublishableChanges((TreeNode) currentNode.getChildAt(i))) { return true; }
		}
		return false;
	}

	@Override
	public boolean accept(final INode node) {
		final TreeNode tn = (TreeNode) node;
		return tn.isAccessible();
	}
}
