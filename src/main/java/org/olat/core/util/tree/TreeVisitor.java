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
 * Copyright (c) 1999-2006 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.core.util.tree;

import org.olat.core.util.nodes.INode;

/**
 * Description: <br>
 * 
 * @author Felix Jost
 */
public class TreeVisitor {

	private Visitor v;
	private INode root;
	private boolean visitChildrenFirst;

	/**
	 * @param v
	 * @param root
	 * @param visitChildrenFirst
	 */
	public TreeVisitor(Visitor v, INode root, boolean visitChildrenFirst) {
		this.v = v;
		this.root = root;
		this.visitChildrenFirst = visitChildrenFirst;
	}

	/**
	 * 
	 */
	public void visitAll() {
		doVisit(root);
	}

	private void doVisit(INode node) {
		if (!visitChildrenFirst) {
			v.visit(node);
		}
		int chdCnt = node.getChildCount();
		for (int i = 0; i < chdCnt; i++) {
			INode chd = node.getChildAt(i);
			doVisit(chd);
		}
		if (visitChildrenFirst) {
			v.visit(node);
		}
	}
}