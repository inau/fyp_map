package view;

import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.PlainDocument;
import javax.swing.JTextField;

/**
 * Custom autocompletion component for Swing. Is essentially a ComboBox with modifications.
 * The box stores data necessary for a smooth autocompletion feature. 
 * In order for the box to work, the user must call the addCustomListeners() method after adding the box to 
 * some content. 
 * @author Stahl
 *
 */
@SuppressWarnings(value = { "serial" })
public class SearchBox extends JComboBox implements DocumentListener, MouseListener, ActionListener
{
	static int bugcount = 0;
	static int buglimit = 50;
	Searchable s; 
	JTextField editable;
	RouteEvent.RouteType type;
	Object initialField;
	
	/**
	 * Constructor for the autocompletion box. It takes a model of the data, an interface Searchable
	 * and a RouteType that enables using more than one SearchBox in an interface.
	 * @param model the ComboBoxModel that models the data.
	 * @param s An object implementing Searchable (for more information please review the interface).
	 * @param type A RouteType enum determining which part of the interface the combobox should define.
	 */
	public SearchBox(ComboBoxModel model, Searchable s, RouteEvent.RouteType type)
	{
		super(model);
		initialField = model.getElementAt(0);
		this.s = s;
		this.type = type;
		setEditable(true);
		setPreferredSize(new Dimension(200,35));
	}

	/**
	 * Listener method which searches for new entries to the combobox with the topmost item selected as prefix.
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		if(bugcount == buglimit) { reset(); return; }
		if(editable.getText().length() < 2) return;
		for(int i=1;i<getModel().getSize();i++)
		{
			String ss =editable.getText();
			String that = getItemAt(i).toString();
			if(ss.equalsIgnoreCase(that)) return;
		}
		s.search(new RouteEvent(editable.getText(),type));
		bugcount++;
	}


	/**--	<	
	 * Listener method which searches for new entries to the combobox with the topmost item selected as prefix.
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		if(bugcount == buglimit) { reset(); return; }
		if(editable.getText().length() < 2) 
			return;
		s.search(new RouteEvent(editable.getText(),type));
	}
	
	
	
	
	@Override
	public void changedUpdate(DocumentEvent e) {
	}
	
	/**
	 * CustomListeners that enable the autocompletion feature. Adds:
	 * a DocumentListener that is key-sensititive to the editable text field of the box.
	 * A mouseListener that reacts to clicking on the box.
	 */
	public void addCustomListeners() {
		editable = (JTextField) getEditor().getEditorComponent();
		editable.getDocument().addDocumentListener(this);
		editable.addMouseListener(this);
		addActionListener(this);
	}
	
	
	
	/**
	 * Listener method. Clears the selected item if it corresponds to the initial text in the box.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		Object comp = getModel().getSelectedItem();
		if(comp.equals(initialField)) { setSelectedItem(""); }
	}
	
	/**
	 * When the user uses mouse keys, the item displayed shouldn't change. 
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		s.readOnly(new RouteEvent(editable.getText(),type));
	}

	/**
	 * Implementation specific. Does not do anything
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Implementation specific. Does not do anything
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Implementation specific. Does not do anything
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void reset()
	{
		bugcount = 0;
		removeActionListener(this);
		setEditor(new MetalComboBoxEditor.UIResource());
		editable.getDocument().removeDocumentListener(this);
		editable.setDocument(new PlainDocument());
		editable.removeMouseListener(this);
		addCustomListeners();
		editable.grabFocus();
	}
	
}
