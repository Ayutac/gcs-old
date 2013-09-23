/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is GURPS Character Sheet.
 *
 * The Initial Developer of the Original Code is Richard A. Wilkes.
 * Portions created by the Initial Developer are Copyright (C) 1998-2013 the
 * Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */

package com.trollworks.gcs.prereq;

import com.trollworks.ttk.annotation.LS;
import com.trollworks.ttk.annotation.Localized;
import com.trollworks.ttk.xml.XMLReader;
import com.trollworks.ttk.xml.XMLWriter;

@Localized({
				@LS(key = "HAS", msg = "Has"),
				@LS(key = "DOES_NOT_HAVE", msg = "Does not have"),
})
/**
 * An abstract prerequisite class for whether or not the specific item is present.
 */
public abstract class HasPrereq extends Prereq {
	/** The "has" attribute name. */
	protected static final String	ATTRIBUTE_HAS	= "has";	//$NON-NLS-1$
	private boolean					mHas;

	/**
	 * Creates a new prerequisite.
	 * 
	 * @param parent The owning prerequisite list, if any.
	 */
	public HasPrereq(PrereqList parent) {
		super(parent);
		mHas = true;
	}

	/**
	 * Creates a copy of the specified prerequisite.
	 * 
	 * @param parent The owning prerequisite list, if any.
	 * @param prereq The prerequisite to clone.
	 */
	protected HasPrereq(PrereqList parent, HasPrereq prereq) {
		super(parent);
		mHas = prereq.mHas;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof HasPrereq) {
			return mHas == ((HasPrereq) obj).mHas;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Loads the "has" attribute.
	 * 
	 * @param reader The XML reader to load from.
	 */
	protected void loadHasAttribute(XMLReader reader) {
		mHas = reader.isAttributeSet(ATTRIBUTE_HAS);
	}

	/**
	 * Writes the "has" attribute to the stream.
	 * 
	 * @param out The XML writer to use.
	 */
	protected void saveHasAttribute(XMLWriter out) {
		out.writeAttribute(ATTRIBUTE_HAS, mHas);
	}

	/**
	 * @return <code>true</code> if the specified criteria should exist, <code>false</code> if it
	 *         should not.
	 */
	public boolean has() {
		return mHas;
	}

	/**
	 * @param has <code>true</code> if the specified criteria should exist, <code>false</code> if it
	 *            should not.
	 */
	public void has(boolean has) {
		mHas = has;
	}
}
