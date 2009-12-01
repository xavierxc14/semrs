package com.googlecode.semrs.model;



import org.apache.commons.lang.builder.ToStringBuilder;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://semrs.googlecode.com/")
public class Module implements Persistable{
	
	private String id;

	private String category;
	
	private String title;

	private String iconPath;
	
	private String thumbPath;
	
	private Integer order;

	
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Override
	public String getUri() {
		return this.getClass().getAnnotation(Namespace.class).value() + this.getClass().getName()+"/";
	}

	public String toString(){
		  return ToStringBuilder.reflectionToString(this);
	}
	

}
