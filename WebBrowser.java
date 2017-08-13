
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WebBrowser {

	// declare the editorPane, the framework for all other panels
	private static JEditorPane editorPane;

	/**
	 * This listener is called when the user clicks on a link on the web
	 * browser.
	 * 
	 * @author Paul
	 *
	 */
	private static class ActivatedHyperlinkListener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
			HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
			final URL url = hyperlinkEvent.getURL();

			// if the type of event is enter
			if (type == HyperlinkEvent.EventType.ENTERED) {
			}
			// if the event is activated
			else if (type == HyperlinkEvent.EventType.ACTIVATED) {

				// retrieve document
				Document doc = editorPane.getDocument();

				// try to set page to URL
				try {
					urlBar.setText(url.toExternalForm());
					editorPane.setPage(url);

					// check if the URL already exists in the list of URLs in
					// the URL object to not add it again if it exists
					if (!currentNode.urlAlreadyExists(url.toExternalForm())) {
						// create the new Node and back its back object the
						// previous currentNode
						urlObject newNode = new urlObject(currentNode, url.toExternalForm());

						// make sure the urlList sub index starts at 0
						if (!currentNode.urlList.isEmpty()) {
							currentNode.changeSubIndex();
						}

						// add the new Node to the list of urls
						currentNode.add(newNode);

						currentNode = newNode;
					}

					// update the color to green and update the threads/branches
					// on the screen
					urlBar.setBackground(Color.GREEN);
					showThreads();

				} catch (IOException ioException) {
					// update the color to red and stay on the same page
					editorPane.setDocument(doc);
					urlBar.setBackground(Color.RED);
				}
			}
		}
	}

	// panel on top with the "Show List","<",">",URLtextfield,"Enter" buttons
	private static JPanel naviPanel = new JPanel();

	// this panel deals with the branched history display
	private static JPanel threadPanel = new JPanel();

	// buttons and textfields
	private static JButton goButton = new JButton("Enter");
	private static JButton refreshButton = new JButton("Refresh");
	private static JButton backButton = new JButton("<");
	private static JButton forwardButton = new JButton(">");
	private static JButton threadStateButton = new JButton("Hide List");
	private static JTextField urlBar = new JTextField("http://nba.com", 45);

	// this keeps track of the current node, starts off with nba.com
	private static urlObject currentNode = new urlObject(null, "http://nba.com");

	// this variable keeps track of index that the current node is in its parent
	// url list
	private static int indexOfCurrentBranch = 0;

	/**
	 * This Class deals with actions originating from many buttons
	 * 
	 * @author Paul
	 *
	 */
	private static class NaviAction implements ActionListener {

		/**
		 * This method deals with actions originating from many buttons
		 */
		public void actionPerformed(ActionEvent event) {

			// hide the threadPanel
			if (event.getActionCommand() == "Hide List") {
				hideThreads();
			}

			// show threadPanel
			else if (event.getActionCommand() == "Show List") {
				showThreads();
			}
			// back button
			else if (event.getActionCommand() == "<") {
				// make sure there is a back url object
				if (currentNode.getBackObj() != null) {
					String urlString = currentNode.getBackObj().getURL();

					// try to update the page accordingly
					try {
						urlBar.setText(urlString);
						editorPane.setPage(urlString);
						currentNode = currentNode.getBackObj();
						urlBar.setBackground(Color.GREEN);
						showThreads();

					}
					// respond to user that it did not work
					catch (IOException e) {
						urlBar.setBackground(Color.RED);

					}
				}
			}

			// forward button pressed
			else if (event.getActionCommand() == ">") {
				// make sure that there are possible forward urls in url list
				if (!currentNode.urlList.isEmpty()) {
					String urlString = currentNode.urlList.get(currentNode.getCurrentSubIndex()).getURL();

					// try to update page accordingly
					try {
						urlBar.setText(urlString);
						editorPane.setPage(urlString);
						currentNode = currentNode.urlList.get(currentNode.getCurrentSubIndex());
						urlBar.setBackground(Color.GREEN);
						showThreads();

					}
					// respond to error
					catch (IOException e) {
						urlBar.setBackground(Color.RED);
					}
				}

			}

			// refreshed pressed
			else if (event.getActionCommand() == "Refresh") {
				String urlString = currentNode.getURL();

				// update page accordingly
				try {
					editorPane.setPage(urlString);
					urlBar.setBackground(Color.GREEN);

				}
				// respond to error
				catch (IOException e) {
					urlBar.setBackground(Color.RED);
				}
			}

			// if enter is pressed
			else if (event.getActionCommand() == "Enter") {
				// retrieve url
				String urlString = urlBar.getText();

				// check we are on the same page
				if (!urlString.equals(currentNode.getURL())) {

					// update page accordingly
					try {
						editorPane.setPage(urlString);
						currentNode = new urlObject(null, urlString);
						urlBar.setBackground(Color.GREEN);
						showThreads();

					}
					// respond to any errors
					catch (IOException e) {
						urlBar.setBackground(Color.RED);
					}
				}
			}

			// for any action involved branched history clicks to other urls
			else {
				// the action command will be the url in this case since the
				// buttons are the urls
				String urlString = event.getActionCommand();

				// update page accordingly
				try {
					editorPane.setPage(urlString);
					currentNode = currentNode.getBackObj().urlList.get(indexOfCurrentBranch - 1);
					urlBar.setBackground(Color.GREEN);
					showThreads();

				}
				// respond to any exceptions
				catch (IOException e) {
					urlBar.setBackground(Color.RED);
				}
			}

		}
	}

	public static void main(String args[]) {

		JFrame frame = new JFrame("Web Browser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add buttons, set panel layouts, and add ActionListeners to Navi
		// Action
		naviPanel.setLayout(new FlowLayout());

		naviPanel.add(threadStateButton);
		threadStateButton.addActionListener(new NaviAction());

		naviPanel.add(backButton);
		backButton.addActionListener(new NaviAction());

		naviPanel.add(forwardButton);
		forwardButton.addActionListener(new NaviAction());

		naviPanel.add(refreshButton);
		refreshButton.addActionListener(new NaviAction());

		naviPanel.add(urlBar);
		urlBar.addActionListener(new NaviAction());

		naviPanel.add(goButton);
		goButton.addActionListener(new NaviAction());

		naviPanel.setBackground(Color.BLACK);
		urlBar.setBackground(Color.GREEN);
		frame.add(naviPanel, BorderLayout.NORTH);

		threadPanel.setLayout(new GridLayout(2, 1));
		threadPanel.setBackground(Color.GREEN);

		frame.add(threadPanel, BorderLayout.WEST);

		// start off by showing the threads/other branches for branched history
		showThreads();

		// open page for the first time
		try {
			urlBar.setBackground(Color.GREEN);
			editorPane = new JEditorPane("http://nba.com");
			editorPane.setEditable(false);

			HyperlinkListener hyperlinkListener = new ActivatedHyperlinkListener();
			editorPane.addHyperlinkListener(hyperlinkListener);
			JScrollPane scrollPane = new JScrollPane(editorPane);
			frame.add(scrollPane, BorderLayout.CENTER);
		} catch (IOException e) {
			urlBar.setBackground(Color.RED);

		}

		frame.setSize(1260, 600);
		frame.setVisible(true);
	}

	/**
	 * This method shows the branched history/ thread panel and all of its
	 * components
	 */
	private static void showThreads() {
		// clear all of previous information
		hideThreads();

		// add title and set page accordingly
		threadPanel.setVisible(true);
		threadStateButton.setText("Hide List");
		JLabel title = new JLabel("List of Other Branches");
		title.setFont(new Font("Verdana", 1, 20));
		threadPanel.add(title);

		// make sure that there is a back object for branched history to work
		if (currentNode.getBackObj() != null) {
			// show branched history only URLs if there is more than one branch
			// from the back object
			if (currentNode.getBackObj().urlList.size() != 1) {

				indexOfCurrentBranch = 0;

				// iterate through the current Nodes back object's url list to
				// retrieve other branches
				for (urlObject obj : currentNode.getBackObj().urlList) {
					// add a new button for each different branch, along with
					// its url as its title, and adding the action listener
					JButton newButton = new JButton(obj.getURL());
					newButton.addActionListener(new NaviAction());
					threadPanel.add(newButton);
					indexOfCurrentBranch++;
				}
			} else {
				indexOfCurrentBranch = 0;

			}

		}

		// change layout accordingly
		threadPanel.setLayout(new GridLayout(indexOfCurrentBranch + 1, 1));
		threadPanel.validate();

	}

	/**
	 * This method hides and wipes out all of the components of the threadPanel
	 */
	private static void hideThreads() {
		threadPanel.setVisible(false);
		threadStateButton.setText("Show List");
		threadPanel.removeAll();
	}
}

/**
 * This class contains code that manages a URL Object that: has a reference to
 * its back URL Object contains a list of URL objects that are visited after it
 * has a URL has a index that keeps track of which URL object is last seen by
 * the user in its URL object list
 * 
 * @author Paul
 *
 */
class urlObject {
	// index that keeps track of which URL object is last seen by the user in
	// its URL object list
	private int currentSubIndex = 0;

	// reference to its back URL object
	private urlObject backObj;

	// URL
	private String URL;

	// constructor with the backObj(can be null) and the URl it has
	public urlObject(urlObject backObjIn, String URLIn) {
		backObj = backObjIn;
		URL = URLIn;

	}

	// Url object
	ArrayList<urlObject> urlList = new ArrayList<urlObject>();

	/**
	 * This method adds a url object to the list of url objects
	 * 
	 * @param urlObject
	 *            the new object
	 */
	public void add(urlObject urlObject) {
		urlList.add(urlObject);
	}

	/**
	 * This method is a toString() method that prints out the contents of the
	 * URL object to help debug//not used in the program now
	 */
	public String toString() {
		String str;
		if (backObj == null) {
			str = "URL:" + URL;
		} else {
			str = "back: " + backObj.getURL() + "\n URL:" + URL;
		}

		return str;

	}

	/**
	 * This method changes the current sub index to keep track of which index of
	 * the url list the object is
	 */
	public void changeSubIndex() {
		currentSubIndex++;
	}

	/**
	 * This method checks to see if the url already exists in the list
	 * 
	 * @param URLin
	 *            the url to check
	 * @return
	 */
	public boolean urlAlreadyExists(String URLin) {
		for (urlObject o : urlList) {

			if (o.getURL().equals(URLin)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * this method returns the currentSubIndex
	 * 
	 * @return subIndex
	 */
	public int getCurrentSubIndex() {
		return currentSubIndex;
	}

	/**
	 * This method returns the back object
	 * 
	 * @return backobj
	 */
	public urlObject getBackObj() {
		return backObj;
	}

	/**
	 * This method returns the url of the url object
	 * 
	 * @return url string
	 */
	public String getURL() {
		return URL;
	}

}
