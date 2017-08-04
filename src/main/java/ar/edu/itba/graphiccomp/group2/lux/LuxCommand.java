package ar.edu.itba.graphiccomp.group2.lux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuxCommand {

	private String _name = new String("");
	private String _description = new String("");
	private Map<String, List<String>> _params = new HashMap<String, List<String>>();
	private boolean _isEmptyOrComment;
	private boolean _executeCommand;

	public String name() {
		return _name;
	}

	public String description() {
		return _description;
	}

	public List<String> paramsList() {
		return param("");
	}

	public Map<String, List<String>> params() {
		return _params;
	}

	public String param(String name, int index) {
		return param(name).get(index);
	}

	public List<String> param(String name) {
		return _params.getOrDefault(name, Collections.emptyList());
	}

	public boolean isEmptyOrComment() {
		return _isEmptyOrComment;
	}

	public LuxCommand parse(String line) {
		_isEmptyOrComment = line.isEmpty() || line.startsWith("#");
		if (isEmptyOrComment()) {
			return this;
		}
		boolean startsWithParam = line.startsWith("\"");
		List<String> split = Arrays.asList(line.split("\\s+"));
		if (!startsWithParam) {
			_executeCommand = true;
			LuxCommand newCommand = new LuxCommand();
			newCommand._name = split.get(0);
			if (split.size() > 1) {
				newCommand._description = split.get(1);
				newCommand._params.clear();
				if (newCommand._description.startsWith("\"")) {
					newCommand._description = newCommand._description.substring(1, newCommand._description.length() - 1);
				} else {
					List<String> values = new ArrayList<String>();
					for (int i = 1; i < split.size(); i++) {
						String newVal = split.get(i);
						newVal = newVal.replace("[", "");
						newVal = newVal.replace("]", "");
						newVal = newVal.replace("\"", "");
						values.add(newVal);
					}
					newCommand._params.put("", values);
				}
			}
			return newCommand;
		} else {
			String paramName = split.get(0).substring(1, split.get(0).length()) + " " + split.get(1).substring(0, split.get(1).length() - 1);
			List<String> values = new ArrayList<String>();
			for (int i = 2; i < split.size(); i++) {
				String newVal = split.get(i);
				newVal = newVal.replace("[", "");
				newVal = newVal.replace("]", "");
				newVal = newVal.replace("\"", "");
				values.add(newVal);
			}
			_params.put(paramName, values);
			return this;
		}
	}

	public boolean executeCommand() {
		return _executeCommand;
	}
}
