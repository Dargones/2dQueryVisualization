package gui;

import geo.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Gui for adding points to be displayed, displaying trees, etc.*/
public class MainPanel extends JPanel implements ActionListener {

    private final PointDisplayer pointDisplayer;
    private final TreeDisplayer treeDisplayer;
    private final TreeDisplayer treapDisplayer;
    private final JComboBox treapSelector;
    private final JRadioButton[] radioButtons;

    public MainPanel(Dimension dim) {
        Font baseFont = new Font(Font.SANS_SERIF, Font.BOLD, dim.height / 40);
        setLayout(new GridLayout(2, 1));
        JPanel treePanel = new JPanel(new GridLayout(2, 1));
        treeDisplayer = new TreeDisplayer(new Dimension(dim.width,
                dim.height / 4), 3);
        treapDisplayer = new TreeDisplayer(new Dimension(dim.width,
                dim.height / 4), 5);
        treePanel.add(treeDisplayer);
        treePanel.add(treapDisplayer);
        add(treePanel);

        treapSelector = new JComboBox();

        JPanel controlPanel = new JPanel(new GridLayout(1, 2));
        // TODO: fix dimension issues!!!
        pointDisplayer = new PointDisplayer(treeDisplayer, treapDisplayer,
                treapSelector,
                new Dimension(dim.width / 2, dim.height / 2),
                new Dimension(50, (int)(50 * ((double) dim.height)/dim.width)));
        controlPanel.add(pointDisplayer);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));

        JPanel pointControl = new JPanel(new GridLayout(1, 4));
        JTextField pointControlHelp = new JTextField("Use click to:");
        pointControlHelp.setFont(baseFont);
        pointControlHelp.setEditable(false);

        JRadioButton addPoint = new JRadioButton(PointDisplayer.Mode
                .ADD.name);
        addPoint.setFont(baseFont);
        addPoint.setActionCommand(PointDisplayer.Mode.ADD.name);
        addPoint.setSelected(false);

        JRadioButton removePoint = new JRadioButton(PointDisplayer.Mode
                .REMOVE.name);
        removePoint.setFont(baseFont);
        removePoint.setActionCommand(PointDisplayer.Mode.REMOVE.name);
        removePoint.setSelected(false);

        JRadioButton queryPoint = new JRadioButton(PointDisplayer.Mode
                .QUERY.name);
        queryPoint.setFont(baseFont);
        queryPoint.setActionCommand(PointDisplayer.Mode.QUERY.name);
        queryPoint.setSelected(true);

        radioButtons = new JRadioButton[] {addPoint, removePoint, queryPoint};

        ButtonGroup group = new ButtonGroup();
        group.add(addPoint);
        group.add(removePoint);
        group.add(queryPoint);

        addPoint.addActionListener(this);
        removePoint.addActionListener(this);
        queryPoint.addActionListener(this);

        pointControl.add(pointControlHelp);
        pointControl.add(addPoint);
        pointControl.add(removePoint);
        pointControl.add(queryPoint);
        buttonPanel.add(pointControl);

        JPanel overhaulPanel = new JPanel(new GridLayout(1, 2));

        JButton clearPoints = new JButton("Clear points");
        clearPoints.setFont(baseFont);
        clearPoints.setActionCommand("Clear");
        clearPoints.addActionListener(this);
        overhaulPanel.add(clearPoints);

        JButton randomizePoints = new JButton("Randomize");
        randomizePoints.setFont(baseFont);
        randomizePoints.setActionCommand("Randomize");
        randomizePoints.addActionListener(this);
        overhaulPanel.add(randomizePoints);

        buttonPanel.add(overhaulPanel);

        JPanel treapPanel = new JPanel(new GridLayout(1, 2));
        JTextField treapPanelHelp = new JTextField("Display treap for node:");
        treapPanelHelp.setFont(baseFont);
        treapPanelHelp.setEditable(false);

        treapSelector.setFont(baseFont);
        treapSelector.setActionCommand("SelectTreap");
        treapSelector.addActionListener(this);
        treapSelector.setEditable(true);
        treapPanel.add(treapPanelHelp);
        treapPanel.add(treapSelector);
        buttonPanel.add(treapPanel);

        JPanel historyPanel = new JPanel(new GridLayout(1, 4));

        JButton prevStep = new JButton("Prev Step");
        prevStep.setFont(baseFont);
        prevStep.setActionCommand("PrevStep");
        prevStep.addActionListener(this);
        historyPanel.add(prevStep);

        JButton nextStep = new JButton("Next Step");
        nextStep.setFont(baseFont);
        nextStep.setActionCommand("NextStep");
        nextStep.addActionListener(this);
        historyPanel.add(nextStep);
        buttonPanel.add(historyPanel);

        JButton firstStep = new JButton("First Step");
        firstStep.setFont(baseFont);
        firstStep.setActionCommand("FirstStep");
        firstStep.addActionListener(this);
        historyPanel.add(firstStep);

        JButton lastStep = new JButton("Last Step");
        lastStep.setFont(baseFont);
        lastStep.setActionCommand("LastStep");
        lastStep.addActionListener(this);
        historyPanel.add(lastStep);

        controlPanel.add(buttonPanel);
        add(controlPanel);

        setFocusable(true);
    }

    public static void setupFrame (Dimension dim) {
        JFrame frame = new JFrame("InteractiveDisplay");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPanel main = new MainPanel(dim);
        main.randomizePoints(63);
        frame.getContentPane().add(main, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private void randomizePoints(int total) {
        pointDisplayer.mode = PointDisplayer.Mode.ADD;
        for (int i = 0; i < total + 2; i++) {
            geo.Point next = Point.getRandom(0,
                    pointDisplayer.planeDim.width, 0,
                    pointDisplayer.planeDim.height);
            if (i == total)
                pointDisplayer.mode = PointDisplayer.Mode.QUERY;
            pointDisplayer.editPoints(next);
        }
        for (JRadioButton radioButton:radioButtons) {
            if (radioButton.isSelected())
                pointDisplayer.mode = PointDisplayer.Mode
                        .getByName(radioButton.getActionCommand());
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "SelectTreap":
                treapDisplayer.setTree(pointDisplayer.getTreap((String)
                        treapSelector.getSelectedItem()));
                break;
            case "Preprocess":
                pointDisplayer.computeTree();
                pointDisplayer.query = new geo.Point[2];
                break;
            case "FirstStep":
                treeDisplayer.resetStep();
                treapDisplayer.resetStep();
                pointDisplayer.resetStep();
                break;
            case "LastStep":
                treeDisplayer.lastStep();
                treapDisplayer.lastStep();
                pointDisplayer.lastStep();
                break;
            case "NextStep":
                treeDisplayer.nextStep();
                treapDisplayer.nextStep();
                pointDisplayer.nextStep();
                break;
            case "PrevStep":
                treeDisplayer.previousStep();
                treapDisplayer.previousStep();
                pointDisplayer.previousStep();
                break;
            case "Clear":
                pointDisplayer.clear();
                break;
            case "Randomize":
                pointDisplayer.clear();
                randomizePoints(63);
                break;
            default:
                PointDisplayer.Mode newMode = PointDisplayer.Mode
                        .getByName(actionEvent.getActionCommand());
                if (newMode != null)
                    pointDisplayer.mode = PointDisplayer.Mode
                            .getByName(actionEvent.getActionCommand());
        }
    }
}
