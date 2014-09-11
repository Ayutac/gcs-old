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

package com.trollworks.gcs.menu.edit;

import com.trollworks.gcs.character.CharacterSheet;
import com.trollworks.gcs.character.GURPSCharacter;
import com.trollworks.toolkit.annotation.Localize;
import com.trollworks.toolkit.ui.menu.Command;
import com.trollworks.toolkit.utility.Localization;

import java.awt.event.ActionEvent;

/** Provides the "Include Conscious/Death Check Rolls command. */
public class AddDeathCheckRollsCommand extends Command {
	@Localize("Include Conscious/Death Check Rolls")
	private static String								ADD_DEATH_CHECK_ROLLS;

	static {
		Localization.initialize();
	}

	/** The action command this command will issue. */
	public static final String							CMD_ADD_DEATH_CHECK_ROLLS	= "AddDeathCheckRoll";			//$NON-NLS-1$

	/** The singleton {@link AddDeathCheckRollsCommand}. */
	public static final AddDeathCheckRollsCommand	INSTANCE						= new AddDeathCheckRollsCommand();

	private AddDeathCheckRollsCommand() {
		super(ADD_DEATH_CHECK_ROLLS, CMD_ADD_DEATH_CHECK_ROLLS);
	}

	@Override
	public void adjust() {
		CharacterSheet sheet = getTarget(CharacterSheet.class);
		if (sheet != null) {
			setEnabled(true);
			setMarked(sheet.getCharacter().includeDeathCheckRolls());
		} else {
			setEnabled(false);
			setMarked(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		CharacterSheet sheet = getTarget(CharacterSheet.class);
		if (sheet != null) {
			GURPSCharacter character = sheet.getCharacter();
			character.setIncludeDeathCheckRolls(!character.includeKickBoots());
		}
	}
}