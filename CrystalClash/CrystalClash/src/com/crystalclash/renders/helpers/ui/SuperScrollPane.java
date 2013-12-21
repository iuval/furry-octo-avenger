package com.crystalclash.renders.helpers.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.crystalclash.renders.helpers.ResourceHelper;

public class SuperScrollPane extends Group {
	private Image refreshMessagePull;
	private Image refreshMessageRelease;
	private boolean isTryingToRefresh = false;
	private boolean showPullDown = false;
	private boolean showRelease = false;
	private float pullDistance = 0;
	private float releaseDistance = 0;
	public ScrollPane scrollPane;
	private SuperScrollPaneRefreshCallback onRefresh;

	public SuperScrollPane(Actor widget, SuperScrollPaneRefreshCallback onRefresh) {
		scrollPane = new ScrollPane(widget);
		scrollPane.setFillParent(true);
		addActor(scrollPane);
		this.onRefresh = onRefresh;
	}

	public void load() {
		refreshMessagePull = new Image(ResourceHelper.getTexture("menu/refresh_list/refresh_message_pull"));
		refreshMessagePull.setVisible(false);
		addActor(refreshMessagePull);

		pullDistance = refreshMessagePull.getHeight();
		releaseDistance = pullDistance * 2;

		refreshMessageRelease = new Image(ResourceHelper.getTexture("menu/refresh_list/refresh_message_release"));
		refreshMessageRelease.setVisible(false);
		addActor(refreshMessageRelease);
	}

	@Override
	public void act(float delta) {
		if (scrollPane.isPanning()) {
			if (!isTryingToRefresh && scrollPane.getScrollY() < -pullDistance) {
				showPullDown = true;
				showRelease = false;
				isTryingToRefresh = true;
			} else if (isTryingToRefresh && scrollPane.getScrollY() < -releaseDistance) {
				showPullDown = false;
				showRelease = true;
			} else if (isTryingToRefresh && scrollPane.getScrollY() > -releaseDistance) {
				if (isTryingToRefresh && scrollPane.getScrollY() > -pullDistance) {
					isTryingToRefresh = false;
				} else {
					showPullDown = true;
					showRelease = false;
				}
			}
		} else {
			if (isTryingToRefresh) {
				if (showRelease) {
					if (onRefresh != null) {
						onRefresh.refresh();
					}
				}
				isTryingToRefresh = false;
			}
		}

		if (isTryingToRefresh) {
			if (showRelease) {
				refreshMessageRelease.setY(scrollPane.getWidget().getTop());
				refreshMessageRelease.setVisible(true);
				refreshMessagePull.setVisible(false);
			} else if (showPullDown) {
				refreshMessagePull.setY(scrollPane.getWidget().getTop());
				refreshMessagePull.setVisible(true);
				refreshMessageRelease.setVisible(false);
			}
		} else {
			refreshMessagePull.setVisible(false);
			refreshMessageRelease.setVisible(false);
		}
		super.act(delta);
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		scrollPane.setBounds(x, y, width, height);
		super.setBounds(x, y, width, height);
	}
}
