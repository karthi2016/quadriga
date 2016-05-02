package edu.asu.spring.quadriga.domain.impl.workbench;

import edu.asu.spring.quadriga.domain.workbench.IPublicPage;

public class PublicPage implements IPublicPage {
	private String publicPageId;
	private String title;
	private String description;
	private int order;
	private String projectId;

	/**
	 * retrieves the Title
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * assigns the Title
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * retrieves the description of the Given Title
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * assigns the description of the Given Title
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * retrieves the order of the Given Title
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * assigns the order of the Given Title
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getPublicPageId() {
		return publicPageId;
	}

	public void setPublicPageId(String publicPageId) {
		this.publicPageId = publicPageId;
	}

}