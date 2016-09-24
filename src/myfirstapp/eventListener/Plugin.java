package myfirstapp.eventListener;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;


public class Plugin {
	private static Plugin plugin;
	private Plugin(){
		
	}
	ArrayList<IPropertyChangeListener> myListeners = new ArrayList<IPropertyChangeListener>();
	
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if (!myListeners.contains(listener))
			myListeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		myListeners.remove(listener);
	}
	public static Plugin getInstance() {
		if (plugin == null)
			plugin = new Plugin();
		return plugin;
	}
	public void initAndInvoke(ArrayList listeners, Object obj) {
		// Post Invocation, inform listeners
		for (Iterator<IPropertyChangeListener> iter = myListeners.iterator(); iter
				.hasNext();) {
			IPropertyChangeListener element = (IPropertyChangeListener) iter
					.next();
			element.propertyChange(new PropertyChangeEvent(this,
					"HelloRcpEvent", null, obj));

		}
	}
}
