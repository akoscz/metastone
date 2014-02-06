package net.pferdimanzug.hearthstone.analyzer.playmode.turn_log;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class GameActionEntry extends JPanel {
	private JLabel iconLabel;
	private JTextArea textArea;

	public GameActionEntry() {
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 4);
		setLayout(flowLayout);
		add(iconLabel = new JLabel());
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Arial", Font.PLAIN, 10));
		textArea.setEditable(false);
		add(textArea);
	}

	protected void setIcon(Icon icon) {
		iconLabel.setIcon(icon);
		setMaximumSize(getPreferredSize());
	}

	protected void setText(String text) {
		textArea.setText(text);
		setMaximumSize(getPreferredSize());
	}
}
