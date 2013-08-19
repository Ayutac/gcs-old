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

package com.trollworks.gcs.character;

import com.trollworks.gcs.app.GCSFonts;
import com.trollworks.ttk.border.BoxedDropShadowBorder;
import com.trollworks.ttk.utility.GraphicsUtilities;
import com.trollworks.ttk.utility.UIUtilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/** A standard panel with a drop shadow. */
public class DropPanel extends JPanel {
	private boolean						mOnlyReportPreferredSize;
	private HashMap<Component, Color>	mHorizontalBackgrounds;
	private HashMap<Component, Color>	mVerticalBackgrounds;
	private boolean						mPaintVerticalFirst;
	private BoxedDropShadowBorder		mBoxedDropShadowBorder;

	/**
	 * Creates a standard panel with a drop shadow.
	 * 
	 * @param layout The layout to use.
	 */
	public DropPanel(LayoutManager layout) {
		this(layout, false);
	}

	/**
	 * Creates a standard panel with a drop shadow.
	 * 
	 * @param layout The layout to use.
	 * @param onlyReportPreferredSize Whether or not minimum and maximum size is reported as
	 *            preferred size or not.
	 */
	public DropPanel(LayoutManager layout, boolean onlyReportPreferredSize) {
		this(layout, null, null, onlyReportPreferredSize);
	}

	/**
	 * Creates a standard panel with a drop shadow.
	 * 
	 * @param layout The layout to use.
	 * @param title The title to use.
	 */
	public DropPanel(LayoutManager layout, String title) {
		this(layout, title, UIManager.getFont(GCSFonts.KEY_LABEL), false);
	}

	/**
	 * Creates a standard panel with a drop shadow.
	 * 
	 * @param layout The layout to use.
	 * @param title The title to use.
	 * @param onlyReportPreferredSize Whether or not minimum and maximum size is reported as
	 *            preferred size or not.
	 */
	public DropPanel(LayoutManager layout, String title, boolean onlyReportPreferredSize) {
		this(layout, title, UIManager.getFont(GCSFonts.KEY_LABEL), onlyReportPreferredSize);
	}

	/**
	 * Creates a standard panel with a drop shadow.
	 * 
	 * @param layout The layout to use.
	 * @param title The title to use.
	 * @param font The font to use for the title.
	 * @param onlyReportPreferredSize Whether or not minimum and maximum size is reported as
	 *            preferred size or not.
	 */
	public DropPanel(LayoutManager layout, String title, Font font, boolean onlyReportPreferredSize) {
		super(layout);
		setOpaque(true);
		setBackground(Color.WHITE);
		mBoxedDropShadowBorder = new BoxedDropShadowBorder(font, title);
		setBorder(new CompoundBorder(mBoxedDropShadowBorder, new EmptyBorder(0, 2, 1, 2)));
		setAlignmentY(TOP_ALIGNMENT);
		mOnlyReportPreferredSize = onlyReportPreferredSize;
		mHorizontalBackgrounds = new HashMap<Component, Color>();
		mVerticalBackgrounds = new HashMap<Component, Color>();
	}

	@Override
	public Dimension getMinimumSize() {
		return mOnlyReportPreferredSize ? getPreferredSize() : super.getMinimumSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return mOnlyReportPreferredSize ? getPreferredSize() : super.getMaximumSize();
	}

	/**
	 * Marks an area with a specific background color. The panel specified will be used to calculate
	 * the area's top and bottom, and the background color will span the width of the drop panel.
	 * 
	 * @param panel The panel to attach the color to.
	 * @param background The color to attach.
	 */
	public void addHorizontalBackground(Component panel, Color background) {
		mHorizontalBackgrounds.put(panel, background);
	}

	/**
	 * Removes a horizontal background added with {@link #addHorizontalBackground(Component,Color)}.
	 * 
	 * @param panel The panel to remove.
	 */
	public void removeHorizontalBackground(Component panel) {
		mHorizontalBackgrounds.remove(panel);
	}

	/**
	 * Marks an area with a specific background color. The panel specified will be used to calculate
	 * the area's left and right, and the background color will span the height of the drop panel.
	 * 
	 * @param panel The panel to attach the color to.
	 * @param background The color to attach.
	 */
	public void addVerticalBackground(Component panel, Color background) {
		mVerticalBackgrounds.put(panel, background);
	}

	/**
	 * Removes a vertical background added with {@link #addVerticalBackground(Component,Color)}.
	 * 
	 * @param panel The panel to remove.
	 */
	public void removeVerticalBackground(Component panel) {
		mVerticalBackgrounds.remove(panel);
	}

	@Override
	protected void paintComponent(Graphics gc) {
		super.paintComponent(GraphicsUtilities.prepare(gc));

		Insets insets = mBoxedDropShadowBorder.getBorderInsets(this);
		Rectangle localBounds = getBounds();

		localBounds.x = insets.left;
		localBounds.y = insets.top;
		localBounds.width -= insets.left + insets.right;
		localBounds.height -= insets.top + insets.bottom;
		if (mPaintVerticalFirst) {
			paintVerticalBackgrounds(gc, localBounds);
			paintHorizontalBackgrounds(gc, localBounds);
		} else {
			paintHorizontalBackgrounds(gc, localBounds);
			paintVerticalBackgrounds(gc, localBounds);
		}
	}

	private void paintHorizontalBackgrounds(Graphics gc, Rectangle localBounds) {
		for (Entry<Component, Color> entry : mHorizontalBackgrounds.entrySet()) {
			Component panel = entry.getKey();
			Rectangle bounds = panel.getBounds();
			Container parent = panel.getParent();

			if (parent != null) {
				if (parent != this) {
					UIUtilities.convertRectangle(bounds, parent, this);
				}
				bounds.x = localBounds.x;
				bounds.width = localBounds.width;
				gc.setColor(entry.getValue());
				gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		}
	}

	private void paintVerticalBackgrounds(Graphics gc, Rectangle localBounds) {
		for (Entry<Component, Color> entry : mVerticalBackgrounds.entrySet()) {
			Component panel = entry.getKey();
			Rectangle bounds = panel.getBounds();
			Container parent = panel.getParent();

			if (parent != null) {
				if (parent != this) {
					UIUtilities.convertRectangle(bounds, parent, this);
				}
				bounds.y = localBounds.y;
				bounds.height = localBounds.height;
				gc.setColor(entry.getValue());
				gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
		}
	}

	/** @return Whether or not to paint the vertical backgrounds first. */
	public final boolean isPaintVerticalFirst() {
		return mPaintVerticalFirst;
	}

	/** @param first Whether or not to paint the vertical backgrounds first. */
	public final void setPaintVerticalFirst(boolean first) {
		mPaintVerticalFirst = first;
	}

	/** @return The {@link BoxedDropShadowBorder}. */
	public BoxedDropShadowBorder getBoxedDropShadowBorder() {
		return mBoxedDropShadowBorder;
	}

	/**
	 * @param parent The parent to use.
	 * @param character The {@link GURPSCharacter} to use.
	 * @param key The notification ID to use.
	 * @param title The title to use.
	 * @param tooltip The tooltip to use.
	 * @param alignment The horizontal field alignment to use.
	 * @return The newly created field.
	 */
	protected PageField createLabelAndField(Container parent, GURPSCharacter character, String key, String title, String tooltip, int alignment) {
		PageField field = new PageField(character, key, alignment, true, tooltip);
		parent.add(new PageLabel(title, field));
		parent.add(field);
		return field;
	}

	/**
	 * @param parent The parent to use.
	 * @param character The {@link GURPSCharacter} to use.
	 * @param key The notification ID to use.
	 * @param title The title to use.
	 * @param tooltip The tooltip to use.
	 * @param alignment The horizontal field alignment to use.
	 * @return The newly created field.
	 */
	protected PageField createLabelAndDisabledField(Container parent, GURPSCharacter character, String key, String title, String tooltip, int alignment) {
		PageField field = new PageField(character, key, alignment, false, tooltip);
		parent.add(new PageLabel(title, field));
		parent.add(field);
		return field;
	}

	/**
	 * @param parent The parent to use.
	 * @param character The {@link GURPSCharacter} to use.
	 * @param key The notification ID to use.
	 * @param tooltip The tooltip to use.
	 * @param alignment The horizontal field alignment to use.
	 * @return The newly created field.
	 */
	protected PageField createField(Container parent, GURPSCharacter character, String key, String tooltip, int alignment) {
		PageField field = new PageField(character, key, alignment, true, tooltip);
		parent.add(field);
		return field;
	}

	/**
	 * @param parent The parent to use.
	 * @param character The {@link GURPSCharacter} to use.
	 * @param key The notification ID to use.
	 * @param tooltip The tooltip to use.
	 * @param alignment The horizontal field alignment to use.
	 * @return The newly created field.
	 */
	protected PageField createDisabledField(Container parent, GURPSCharacter character, String key, String tooltip, int alignment) {
		PageField field = new PageField(character, key, alignment, false, tooltip);
		parent.add(field);
		return field;
	}

	/**
	 * @param parent The parent to use.
	 * @param title The title to use.
	 * @param tooltip The tooltip to use.
	 * @return The newly created header.
	 */
	protected PageHeader createHeader(Container parent, String title, String tooltip) {
		PageHeader header = new PageHeader(title, tooltip);
		parent.add(header);
		return header;
	}
}
