package hvmforarmcortexm.ui;

import hvmforarmcortexm.Activator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class HvmForArmCortexMPropertyPage extends PropertyPage {

	HvmForArmCortexArmProperties model;
	DataBindingContext dbc = new DataBindingContext(); 
	
	private IEclipsePreferences preferences;

	public HvmForArmCortexMPropertyPage() {
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		model = new HvmForArmCortexArmProperties();
		model.load(getPreferences());
		final Composite composite = new Composite(parent, SWT.NONE);
		addText(composite, 	"Source Files", "sourceFiles", "useDefaultMakeFile");
		addText(composite, 	"Additional C Libraries", "libraries", "useDefaultMakeFile");
		addText(composite, 	"Burn Tool", "burnTool", "useDefaultMakeFile");
		addBoolean(composite, 	"Use Alternative MakeFile", "useAlternativeMakeFile");
		addText(composite, 	"Alternative MakeFile", "alternativeMakeFile", "useAlternativeMakeFile");

		GridLayoutFactory.swtDefaults().generateLayout(composite);	
		return composite;
	}

	private void addBoolean(Composite composite, String labelText, String property) {
		// TODO Auto-generated method stub
		Label label = new Label(composite, SWT.NONE);
		Button button = new Button(composite, SWT.CHECK);
		label.setText(labelText);
		IObservableValue modelObservable = PojoObservables.observeValue(model, property);
		dbc.bindValue(SWTObservables.observeSelection(button), modelObservable, null, null);
	}

	private Control addText(final Composite composite, String labelText,
			String property, String disableProperty ) {
		Label label = new Label(composite, SWT.NONE);
		Text text = new Text(composite, SWT.BORDER);
		label.setText(labelText);
		IObservableValue modelObservable = BeansObservables.observeValue(model, property);
		dbc.bindValue(SWTObservables.observeText(text, SWT.Modify), modelObservable, null, null);
		if(disableProperty != null) {
			IObservableValue disableModelObservable = BeansObservables.observeValue(model, disableProperty);
			dbc.bindValue(SWTObservables.observeEnabled(text), disableModelObservable, null, null);
		}
		return text;
	}
	
	
	
	@Override
	public boolean performOk() {
		this.model.save(getPreferences());
		return super.performOk();
	}
	
	
	@Override
	protected void performDefaults() {
		this.model.restoreDefault(getPreferences());
	}
	
	
	public IEclipsePreferences getPreferences() {
		if(this.preferences == null) {
			ProjectScope projectScope = new ProjectScope((IProject)this.getElement().getAdapter(IProject.class));
			this.preferences = projectScope.getNode(Activator.PLUGIN_ID);
		}
		return preferences;
	}

}
