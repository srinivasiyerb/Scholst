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
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */
package org.olat.portfolio.model.structel;

/**
 * Description:<br>
 * TODO: Class Description for EPStructuredMapTemplate
 * <P>
 * Initial Date: 11.06.2010 <br>
 * 
 * @author rhaag, roman.haag@frentix.com, http://www.frentix.com
 */
public class EPStructuredMapTemplate extends EPAbstractMap {

	/**
	 * 
	 */
	public EPStructuredMapTemplate() {
		//
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) { return true; }
		if (obj instanceof EPStructuredMapTemplate) { return equalsByPersistableKey((EPStructuredMapTemplate) obj); }
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
