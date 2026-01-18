package none.cgutils.agenda;

public class AgendaConstants {
	public static final String DEFAULT_PLUGIN_NAME = "Demo Agenda";
	public static final String PLUGIN_ID = "none.cgutils.intellij.plugins.agenda";
	public static final String DEFAULT_INCOMPLETE = "🔵"; // U+1F535
	public static final String DEFAULT_COMPLETED = "✅";   // U+2705
	public static final String DEFAULT_AGENDA_FILENAME = "Agenda.txt";
	public static final String DEFAULT_SETTINGS_HINT = "Leave blank to use defaults (" +
			DEFAULT_INCOMPLETE + ", " +
			DEFAULT_COMPLETED + ", and file:" +
			DEFAULT_AGENDA_FILENAME + ")";
	public static final String INCOMPLETE_ICON_LABEL = "Incomplete icon:";
	public static final String COMPLETED_ICON_LABEL = "Completed icon:";
	public static final String AGENDA_FILE_NAME_LABEL = "Agenda file name:";
	public static final String KEYMAP_HINT = "Find the KeyMap for this plugin in " +
			"Settings > Keymap > Toggle Agenda Item";
}
