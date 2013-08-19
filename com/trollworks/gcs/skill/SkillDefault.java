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
 * Portions created by the Initial Developer are Copyright (C) 1998-2002,
 * 2005-2011 the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK ***** */

package com.trollworks.gcs.skill;

import com.trollworks.gcs.widgets.outline.ListRow;
import com.trollworks.ttk.utility.LocalizedMessages;
import com.trollworks.ttk.xml.XMLNodeType;
import com.trollworks.ttk.xml.XMLReader;
import com.trollworks.ttk.xml.XMLWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/** Describes a skill default. */
public class SkillDefault {
	/** The XML tag. */
	public static final String	TAG_ROOT			= "default";		//$NON-NLS-1$
	/** The tag used for the type. */
	public static final String	TAG_TYPE			= "type";			//$NON-NLS-1$
	/** The tag used for the skill name. */
	public static final String	TAG_NAME			= "name";			//$NON-NLS-1$
	/** The tag used for the skill specialization. */
	public static final String	TAG_SPECIALIZATION	= "specialization"; //$NON-NLS-1$
	/** The tag used for the modifier. */
	public static final String	TAG_MODIFIER		= "modifier";		//$NON-NLS-1$
	private static final String	EMPTY				= "";				//$NON-NLS-1$
	private static String		MSG_PARRY;
	private static String		MSG_BLOCK;
	private SkillDefaultType	mType;
	private String				mName;
	private String				mSpecialization;
	private int					mModifier;
	private int					mLevel;
	private int					mAdjLevel;
	private int					mPoints;

	static {
		LocalizedMessages.initialize(SkillDefault.class);
	}

	/**
	 * Creates a new skill default.
	 * 
	 * @param type The type of default.
	 * @param name The name of the skill to default from. Pass in <code>null</code> if type is not
	 *            skill-based.
	 * @param specialization The specialization of the skill. Pass in <code>null</code> if this does
	 *            not default from a skill or the skill doesn't require a specialization.
	 * @param modifier The modifier to use.
	 */
	public SkillDefault(SkillDefaultType type, String name, String specialization, int modifier) {
		setType(type);
		setName(name);
		setSpecialization(specialization);
		setModifier(modifier);
	}

	/**
	 * Creates a clone of the specified skill default.
	 * 
	 * @param other The skill default to clone.
	 */
	public SkillDefault(SkillDefault other) {
		mType = other.mType;
		mName = other.mName;
		mSpecialization = other.mSpecialization;
		mModifier = other.mModifier;
	}

	/**
	 * Creates a skill default.
	 * 
	 * @param reader The XML reader to use.
	 */
	public SkillDefault(XMLReader reader) throws IOException {
		String marker = reader.getMarker();

		mType = SkillDefaultType.Skill;
		mName = EMPTY;
		mSpecialization = EMPTY;
		mModifier = 0;

		do {
			if (reader.next() == XMLNodeType.START_TAG) {
				String name = reader.getName();

				if (TAG_TYPE.equals(name)) {
					setType(SkillDefaultType.getByName(reader.readText()));
				} else if (TAG_NAME.equals(name)) {
					setName(reader.readText());
				} else if (TAG_SPECIALIZATION.equals(name)) {
					setSpecialization(reader.readText());
				} else if (TAG_MODIFIER.equals(name)) {
					setModifier(reader.readInteger(0));
				} else {
					reader.skipTag(name);
				}
			}
		} while (reader.withinMarker(marker));
	}

	/** @return The current level of this default. Temporary storage only. */
	public int getLevel() {
		return mLevel;
	}

	/**
	 * @param level Sets the current level of this default. Temporary storage only.
	 */
	public void setLevel(int level) {
		mLevel = level;
	}

	/** @return The current level of this default. Temporary storage only. */
	public int getAdjLevel() {
		return mAdjLevel;
	}

	/**
	 * @param level Sets the current level of this default. Temporary storage only.
	 */
	public void setAdjLevel(int level) {
		mAdjLevel = level;
	}

	/**
	 * @return The current points provided by this default. Temporary storage only.
	 */
	public int getPoints() {
		return mPoints;
	}

	/**
	 * @param points Sets the current points provided by this default. Temporary storage only.
	 */
	public void setPoints(int points) {
		mPoints = points;
	}

	/**
	 * Saves the skill default.
	 * 
	 * @param out The XML writer to use.
	 */
	public void save(XMLWriter out) {
		out.startSimpleTagEOL(TAG_ROOT);
		out.simpleTag(TAG_TYPE, mType.name());
		if (mType.isSkillBased()) {
			out.simpleTagNotEmpty(TAG_NAME, mName);
			out.simpleTagNotEmpty(TAG_SPECIALIZATION, mSpecialization);
		}
		out.simpleTag(TAG_MODIFIER, mModifier);
		out.endTagEOL(TAG_ROOT, true);
	}

	/** @return The type of default. */
	public SkillDefaultType getType() {
		return mType;
	}

	/** @param type The new type. */
	public void setType(SkillDefaultType type) {
		mType = type;
	}

	/** @return The full name of the skill to default from. */
	public String getFullName() {
		if (mType.isSkillBased()) {
			StringBuilder builder = new StringBuilder();
			builder.append(mName);
			if (mSpecialization.length() > 0) {
				builder.append(" ("); //$NON-NLS-1$
				builder.append(mSpecialization);
				builder.append(')');
			}
			if (mType == SkillDefaultType.Parry) {
				builder.append(MSG_PARRY);
			} else if (mType == SkillDefaultType.Block) {
				builder.append(MSG_BLOCK);
			}
			return builder.toString();
		}
		return mType.toString();
	}

	/**
	 * @return The name of the skill to default from. Only valid when {@link #getType()} returns a
	 *         {@link SkillDefaultType} whose {@link SkillDefaultType#isSkillBased()} method returns
	 *         <code>true</code>.
	 */
	public String getName() {
		return mName;
	}

	/** @param name The new name. */
	public void setName(String name) {
		mName = name != null ? name : EMPTY;
	}

	/**
	 * @return The specialization of the skill to default from. Only valid when {@link #getType()}
	 *         returns a {@link SkillDefaultType} whose {@link SkillDefaultType#isSkillBased()}
	 *         method returns <code>true</code>.
	 */
	public String getSpecialization() {
		return mSpecialization;
	}

	/** @param specialization The new specialization. */
	public void setSpecialization(String specialization) {
		mSpecialization = specialization != null ? specialization : EMPTY;
	}

	/** @return The modifier. */
	public int getModifier() {
		return mModifier;
	}

	/** @param modifier The new modifier. */
	public void setModifier(int modifier) {
		mModifier = modifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof SkillDefault) {
			SkillDefault sd = (SkillDefault) obj;
			return mType == sd.mType && mModifier == sd.mModifier && mName.equals(sd.mName) && mSpecialization.equals(sd.mSpecialization);
		}
		return false;
	}

	/** @param set The nameable keys. */
	public void fillWithNameableKeys(HashSet<String> set) {
		ListRow.extractNameables(set, getName());
		ListRow.extractNameables(set, getSpecialization());
	}

	/** @param map The map of nameable keys to names to apply. */
	public void applyNameableKeys(HashMap<String, String> map) {
		setName(ListRow.nameNameables(map, getName()));
		setSpecialization(ListRow.nameNameables(map, getSpecialization()));
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(getFullName());
		if (mModifier > 0) {
			buffer.append(" + "); //$NON-NLS-1$
			buffer.append(mModifier);
		} else if (mModifier < 0) {
			buffer.append(" - "); //$NON-NLS-1$
			buffer.append(-mModifier);
		}
		return buffer.toString();
	}
}
