package pt.ist.bennu.dispatch.model;

import java.io.Serializable;

import pt.ist.bennu.core.util.BundleUtil;

import com.google.gson.JsonObject;

public class FunctionalityInfo implements Serializable {
	private final String bundle;

	private final String title;

	private final String description;

	private final String path;

	private final String group;

	public FunctionalityInfo(String bundle, String title, String description, String path, String group) {
		this.bundle = bundle;
		this.title = title;
		this.description = description;
		this.path = path;
		this.group = group;
	}

	public JsonObject json() {
		JsonObject json = new JsonObject();
		json.add("title", BundleUtil.getMultilanguageString(bundle, title).json());
		json.add("description", BundleUtil.getMultilanguageString(bundle, description).json());
		json.addProperty("path", path);
		json.addProperty("accessExpression", group);
		return json;
	}
}
