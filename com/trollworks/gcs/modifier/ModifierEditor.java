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

package com.trollworks.gcs.modifier;

import static com.trollworks.gcs.modifier.ModifierEditor_LS.*;

import com.trollworks.gcs.feature.FeaturesPanel;
import com.trollworks.gcs.widgets.outline.RowEditor;
import com.trollworks.ttk.annotation.LS;
import com.trollworks.ttk.annotation.Localized;
import com.trollworks.ttk.layout.ColumnLayout;
import com.trollworks.ttk.text.NumberFilter;
import com.trollworks.ttk.text.Numbers;
import com.trollworks.ttk.text.TextUtility;
import com.trollworks.ttk.utility.UIUtilities;
import com.trollworks.ttk.widgets.LinkedLabel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@Localized({
				@LS(key = "NAME", msg = "Name"),
				@LS(key = "NAME_TOOLTIP", msg = "Name of Modifier"),
				@LS(key = "NOTES", msg = "Notes"),
				@LS(key = "NOTES_TOOLTIP", msg = "Any notes that you would like to show up in the list along with this modifier"),
				@LS(key = "NAME_CANNOT_BE_EMPTY", msg = "The name field may not be empty"),
				@LS(key = "COST", msg = "Cost"),
				@LS(key = "COST_TOOLTIP", msg = "The base cost modifier"),
				@LS(key = "LEVELS", msg = "Levels"),
				@LS(key = "LEVELS_TOOLTIP", msg = "The number of levels this modifier has"),
				@LS(key = "TOTAL_COST", msg = "Total"),
				@LS(key = "TOTAL_COST_TOOLTIP", msg = "The cost modifier's total value"),
				@LS(key = "HAS_LEVELS", msg = "{0} Per Level"),
				@LS(key = "ENABLED", msg = "Enabled"),
				@LS(key = "ENABLED_TOOLTIP", msg = "Whether this modifier has been enabled or not"),
				@LS(key = "REFERENCE", msg = "Ref"),
				@LS(key = "REFERENCE_TOOLTIP", msg = "A reference to the book and page this modifier appears\non (e.g. B22 would refer to \"Basic Set\", page 22)"),
})
/** Editor for {@link Modifier}s. */
public class ModifierEditor extends RowEditor<Modifier> implements ActionListener, DocumentListener {
	private static final String	EMPTY	= "";		//$NON-NLS-1$
	private JTextField			mNameField;
	private JCheckBox			mEnabledField;
	private JTextField			mNotesField;
	private JTextField			mReferenceField;
	private JTextField			mCostField;
	private JTextField			mLevelField;
	private JTextField			mCostModifierField;
	private FeaturesPanel		mFeatures;
	private JTabbedPane			mTabPanel;
	private JComboBox<Object>	mCostType;
	private JComboBox<Object>	mAffects;
	private int					mLastLevel;

	/**
	 * Creates a new {@link ModifierEditor}.
	 * 
	 * @param modifier The {@link Modifier} to edit.
	 */
	public ModifierEditor(Modifier modifier) {
		super(modifier);

		JPanel content = new JPanel(new ColumnLayout(2));
		JPanel fields = new JPanel(new ColumnLayout(2));
		JLabel icon;
		BufferedImage image = modifier.getImage(true);
		if (image != null) {
			icon = new JLabel(new ImageIcon(image));
		} else {
			icon = new JLabel();
		}

		JPanel wrapper = new JPanel(new ColumnLayout(2));
		mNameField = createCorrectableField(fields, wrapper, NAME, modifier.getName(), NAME_TOOLTIP);
		mEnabledField = new JCheckBox(ENABLED, modifier.isEnabled());
		mEnabledField.setToolTipText(ENABLED_TOOLTIP);
		mEnabledField.setEnabled(mIsEditable);
		wrapper.add(mEnabledField);
		fields.add(wrapper);

		createCostModifierFields(fields);

		wrapper = new JPanel(new ColumnLayout(3));
		mNotesField = createField(fields, wrapper, NOTES, modifier.getNotes(), NOTES_TOOLTIP, 0);
		mReferenceField = createField(wrapper, wrapper, REFERENCE, mRow.getReference(), REFERENCE_TOOLTIP, 6);
		fields.add(wrapper);

		icon.setVerticalAlignment(SwingConstants.TOP);
		icon.setAlignmentY(-1f);
		content.add(icon);
		content.add(fields);
		add(content);

		mTabPanel = new JTabbedPane();
		mFeatures = new FeaturesPanel(mRow, mRow.getFeatures());
		Component panel = embedEditor(mFeatures);
		mTabPanel.addTab(panel.getName(), panel);
		UIUtilities.selectTab(mTabPanel, getLastTabName());
		add(mTabPanel);
	}

	@Override
	protected boolean applyChangesSelf() {
		boolean modified = mRow.setName(mNameField.getText());

		modified |= mRow.setReference(mReferenceField.getText());
		modified |= mRow.setNotes(mNotesField.getText());
		if (getCostType() == CostType.MULTIPLIER) {
			modified |= mRow.setCostMultiplier(getCostMultiplier());
		} else {
			modified |= mRow.setCost(getCost());
		}
		if (hasLevels()) {
			modified |= mRow.setLevels(getLevels());
			modified |= mRow.setCostType(CostType.PERCENTAGE);
		} else {
			modified |= mRow.setLevels(0);
			modified |= mRow.setCostType(getCostType());
		}
		modified |= mRow.setAffects((Affects) mAffects.getSelectedItem());
		modified |= mRow.setEnabled(mEnabledField.isSelected());

		if (mFeatures != null) {
			modified |= mRow.setFeatures(mFeatures.getFeatures());
		}
		return modified;
	}

	private boolean hasLevels() {
		return mCostType.getSelectedIndex() == 0;
	}

	@Override
	public void finished() {
		if (mTabPanel != null) {
			updateLastTabName(mTabPanel.getTitleAt(mTabPanel.getSelectedIndex()));
		}
	}

	private JTextField createCorrectableField(Container labelParent, Container fieldParent, String title, String text, String tooltip) {
		JTextField field = new JTextField(text);
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		field.getDocument().addDocumentListener(this);

		LinkedLabel label = new LinkedLabel(title);
		label.setLink(field);

		labelParent.add(label);
		fieldParent.add(field);
		return field;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();
		if (src == mCostType) {
			costTypeChanged();
		} else if (src == mCostField || src == mLevelField) {
			updateCostModifier();
		}
	}

	private JTextField createField(Container labelParent, Container fieldParent, String title, String text, String tooltip, int maxChars) {
		JTextField field = new JTextField(maxChars > 0 ? TextUtility.makeFiller(maxChars, 'M') : text);

		if (maxChars > 0) {
			UIUtilities.setOnlySize(field, field.getPreferredSize());
			field.setText(text);
		}
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		labelParent.add(new LinkedLabel(title, field));
		fieldParent.add(field);
		return field;
	}

	private JScrollPane embedEditor(Container editor) {
		JScrollPane scrollPanel = new JScrollPane(editor);
		scrollPanel.setMinimumSize(new Dimension(500, 120));
		scrollPanel.setName(editor.toString());
		if (!mIsEditable) {
			UIUtilities.disableControls(editor);
		}
		return scrollPanel;
	}

	@SuppressWarnings("unused")
	private JTextField createNumberField(Container labelParent, Container fieldParent, String title, boolean allowSign, int value, String tooltip, int maxDigits) {
		JTextField field = new JTextField(TextUtility.makeFiller(maxDigits, '9') + TextUtility.makeFiller(maxDigits / 3, ',') + (allowSign ? "-" : EMPTY)); //$NON-NLS-1$

		UIUtilities.setOnlySize(field, field.getPreferredSize());
		field.setText(Numbers.format(value));
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		new NumberFilter(field, false, allowSign, true, maxDigits);
		field.addActionListener(this);
		labelParent.add(new LinkedLabel(title, field));
		fieldParent.add(field);
		return field;
	}

	@SuppressWarnings("unused")
	private JTextField createNumberField(Container labelParent, Container fieldParent, String title, double value, String tooltip, int maxDigits) {
		JTextField field = new JTextField(TextUtility.makeFiller(maxDigits, '9') + TextUtility.makeFiller(maxDigits / 3, ',') + '.');

		UIUtilities.setOnlySize(field, field.getPreferredSize());
		field.setText(Numbers.format(value));
		field.setToolTipText(tooltip);
		field.setEnabled(mIsEditable);
		new NumberFilter(field, true, false, true, maxDigits);
		field.addActionListener(this);
		labelParent.add(new LinkedLabel(title, field));
		fieldParent.add(field);
		return field;
	}

	private void createCostModifierFields(Container parent) {
		JPanel wrapper = new JPanel(new ColumnLayout(7));

		mLastLevel = mRow.getLevels();
		if (mLastLevel < 1) {
			mLastLevel = 1;
		}
		if (mRow.getCostType() == CostType.MULTIPLIER) {
			mCostField = createNumberField(parent, wrapper, COST, mRow.getCostMultiplier(), COST_TOOLTIP, 5);
		} else {
			mCostField = createNumberField(parent, wrapper, COST, true, mRow.getCost(), COST_TOOLTIP, 5);
		}
		createCostType(wrapper);
		mLevelField = createNumberField(wrapper, wrapper, LEVELS, false, mLastLevel, LEVELS_TOOLTIP, 3);
		mCostModifierField = createNumberField(wrapper, wrapper, TOTAL_COST, true, 0, TOTAL_COST_TOOLTIP, 9);
		mAffects = createComboBox(wrapper, Affects.values(), mRow.getAffects());
		mCostModifierField.setEnabled(false);
		if (!mRow.hasLevels()) {
			mLevelField.setText(EMPTY);
			mLevelField.setEnabled(false);
		}
		parent.add(wrapper);
	}

	private JComboBox<Object> createComboBox(Container parent, Object[] items, Object selection) {
		JComboBox<Object> combo = new JComboBox<>(items);
		combo.setSelectedItem(selection);
		combo.addActionListener(this);
		combo.setMaximumRowCount(items.length);
		UIUtilities.setOnlySize(combo, combo.getPreferredSize());
		parent.add(combo);
		return combo;
	}

	private void createCostType(Container parent) {
		CostType[] types = CostType.values();
		Object[] values = new Object[types.length + 1];
		values[0] = MessageFormat.format(HAS_LEVELS, CostType.PERCENTAGE.toString());
		System.arraycopy(types, 0, values, 1, types.length);
		mCostType = createComboBox(parent, values, mRow.hasLevels() ? values[0] : mRow.getCostType());
	}

	private void costTypeChanged() {
		boolean hasLevels = hasLevels();

		if (hasLevels) {
			mLevelField.setText(Numbers.format(mLastLevel));
		} else {
			mLastLevel = Numbers.getLocalizedInteger(mLevelField.getText(), 0);
			mLevelField.setText(EMPTY);
		}
		mLevelField.setEnabled(hasLevels);
		updateCostField();
		updateCostModifier();
	}

	@SuppressWarnings("unused")
	private void updateCostField() {
		if (getCostType() == CostType.MULTIPLIER) {
			new NumberFilter(mCostField, true, false, true, 5);
			mCostField.setText(Numbers.format(Math.abs(Numbers.getLocalizedDouble(mCostField.getText(), 0))));
		} else {
			new NumberFilter(mCostField, false, true, true, 5);
			mCostField.setText(Numbers.formatWithForcedSign(Numbers.getLocalizedInteger(mCostField.getText(), 0)));
		}
	}

	private void updateCostModifier() {
		boolean enabled = true;

		if (hasLevels()) {
			mCostModifierField.setText(Numbers.formatWithForcedSign(getCost() * getLevels()) + "%"); //$NON-NLS-1$
		} else {
			switch (getCostType()) {
				case PERCENTAGE:
				default:
					mCostModifierField.setText(Numbers.formatWithForcedSign(getCost()) + "%"); //$NON-NLS-1$
					break;
				case POINTS:
					mCostModifierField.setText(Numbers.formatWithForcedSign(getCost()));
					break;
				case MULTIPLIER:
					mCostModifierField.setText("x" + Numbers.format(getCostMultiplier())); //$NON-NLS-1$
					mAffects.setSelectedItem(Affects.TOTAL);
					enabled = false;
					break;
			}
		}
		mAffects.setEnabled(mIsEditable && enabled);
	}

	private CostType getCostType() {
		Object obj = mCostType.getSelectedItem();
		if (!(obj instanceof CostType)) {
			obj = CostType.PERCENTAGE;
		}
		return (CostType) obj;
	}

	private int getCost() {
		return Numbers.getLocalizedInteger(mCostField.getText(), 0);
	}

	private double getCostMultiplier() {
		return Numbers.getLocalizedDouble(mCostField.getText(), 0);
	}

	private int getLevels() {
		return Numbers.getLocalizedInteger(mLevelField.getText(), 0);
	}

	@Override
	public void changedUpdate(DocumentEvent event) {
		nameChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent event) {
		nameChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent event) {
		nameChanged();
	}

	private void nameChanged() {
		LinkedLabel.setErrorMessage(mNameField, mNameField.getText().trim().length() != 0 ? null : NAME_CANNOT_BE_EMPTY);
	}
}
