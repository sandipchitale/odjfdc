package opendotjavafordotclass;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ODJFODCPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	static final String DO_NOT_CLOSE_DOT_CLASS = "DO_NOT_CLOSE_DOT_CLASS";
	static final String DO_NOT_OPEN_DOT_JAVA = "DO_NOT_OPEN_DOT_JAVA";

	public ODJFODCPreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "OpenDotJavaForDotClass"));
		setDescription("Open .java for .class Preferences");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(DO_NOT_CLOSE_DOT_CLASS, "Do not close .&class", getFieldEditorParent()));
		addField(new BooleanFieldEditor(DO_NOT_OPEN_DOT_JAVA, "Do not open .&java", getFieldEditorParent()));
	}

}
