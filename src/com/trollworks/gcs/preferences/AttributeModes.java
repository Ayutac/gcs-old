/*
 * Copyright (c) 1998-2014 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * version 2.0. If a copy of the MPL was not distributed with this file, You
 * can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as defined
 * by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.preferences;

import com.trollworks.toolkit.annotation.Localize;
import com.trollworks.toolkit.utility.Localization;

/** The attribute modes. */
public enum AttributeModes {
	/** Classic */
	CLASSIC() {
		@Override
		public String getLocalizedName() {
			return CLASSIC_NAME;
		}
		@Override
		public String getLocalizedDescription() {
			return CLASSIC_DESC;
		}
	},
	/** Negative Attributes (complete) */
	NEG_ATTR_C() {
		@Override
		public String getLocalizedName() {
			return NEG_ATTR_C_NAME;
		}
		@Override
		public String getLocalizedDescription() {
			return NEG_ATTR_C_DESC;
		}
	},
	/** Negative Attributes; must come after complete version (compare WeightUnits.java in the toolkit)  */
	NEG_ATTR() {
		@Override
		public String getLocalizedName() {
			return NEG_ATTR_NAME;
		}
		@Override
		public String getLocalizedDescription() {
			return NEG_ATTR_DESC;
		}
	},
	/** Disadvantages (complete) */
	DISADV_C() {
		@Override
		public String getLocalizedName() {
			return DISADV_C_NAME;
		}
		@Override
		public String getLocalizedDescription() {
			return DISADV_C_DESC;
		}
	},
	/** Disadvantages; must come after complete version (compare WeightUnits.java in the toolkit) */
	DISADV() {
		@Override
		public String getLocalizedName() {
			return DISADV_NAME;
		}
		@Override
		public String getLocalizedDescription() {
			return DISADV_DESC;
		}
	};

	@Localize("Classic")
	static String	CLASSIC_NAME;
	@Localize("Classic mode, i.e. both positive and negative attributes will be displayed in Attribute.")
	static String	CLASSIC_DESC;
	@Localize("Negative Attributes (complete)")
	static String	NEG_ATTR_C_NAME;
	@Localize("Same as Negative Attributes, but will count in the attribute (dis)advantages to (negative) attributes instead of counting them to (dis)advantages.")
	static String	NEG_ATTR_C_DESC;
	@Localize("Negative Attributes")
	static String	NEG_ATTR_NAME;
	@Localize("Negative Attributes will be displayed seperated from positive attributes.")
	static String	NEG_ATTR_DESC;
	@Localize("Disadvantages (complete)")
	static String	DISADV_C_NAME;
	@Localize("Same as Disadvantages, but counts attributes into advantages as well.")
	static String	DISADV_C_DESC;
	@Localize("Disadvantages")
	static String	DISADV_NAME;
	@Localize("Count negative attributes into disadvantages.")
	static String	DISADV_DESC;
	// How would I combine name and description?
	/*
	@Localize("{0} {1}")
	static String	FORMAT;
	@Localize("%s (%s)")
	static String	DESCRIPTION_FORMAT;
	*/

	static {
		Localization.initialize();
	}

	private AttributeModes() {}
	
	public String getLocalizedName() {return null;}
	
	public String getLocalizedDescription() {return null;}

	// Do I still need this?
	/*
	@Override
	public String getAbbreviation() {
		return name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return String.format(DESCRIPTION_FORMAT, getLocalizedName(), getAbbreviation());
	}
	*/
}
