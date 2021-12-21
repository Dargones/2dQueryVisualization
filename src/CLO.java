import org.apache.commons.cli.*;

import java.awt.*;

/** Parsing Command Line Options */
public class CLO { // command line options

    public final boolean gui; // whether to launch the GUI
    public final String file; // contains points/queries. Incompatible with GUI.
    public final Dimension resolution; // resolution of GUI screen
    public final int performance; // do a performance test for this many pts

    private static final Dimension RESOLUTION_DEFAULT =
            new Dimension(1800, 1000);

    public CLO(String[] args) {
        Options options = new Options();

        Option gui = new Option("gui", false,"Launch the GUI. Does " +
                "not work with -file or -performance; " +
                "GUI is designed for small examples");
        gui.setRequired(false);
        options.addOption(gui);

        Option performance = new Option("performance", true,
                "Test the performance of the implementation on " +
                        " a set of points of this size. Does not work " +
                        "with -file or -gui. Should be >= 1000");
        performance.setRequired(false);
        performance.setType(Number.class);
        options.addOption(performance);

        Option file = new Option("f", "file", true, "File with the set of " +
                "points on first line: x1,y2 x2,y2 ... and with two query " +
                "points on each following line. Coordinates must be integers.");
        file.setRequired(false);
        file.setType(String.class);
        options.addOption(file);

        Option resolution = new Option("r", "resolution", true,
                "Size of the GUI window. Default is " +
                        RESOLUTION_DEFAULT.width + "x" +
                        RESOLUTION_DEFAULT.height);
        resolution.setRequired(false);
        resolution.setType(String.class);
        options.addOption(resolution);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }

        this.gui = cmd.hasOption("gui");
        this.performance = cmd.hasOption("performance") ?
                Integer.parseInt(cmd.getOptionValue("performance")) :
                -1;
        this.file = cmd.hasOption("file") ? cmd.getOptionValue("file") : null;
        this.resolution = cmd.hasOption("resolution") ?
                parseResolution(cmd.getOptionValue("resolution")) :
                RESOLUTION_DEFAULT;

        if (cmd.hasOption("performance") && (this.performance < 100)) {
            System.out.println("Performance tests should be conducted for at " +
                    "least 100 point");
            System.exit(0);
        }

        if ((this.gui && this.file != null) ||
                (this.performance != -1 && this.file != null) ||
                (this.gui && this.performance != -1)) {
            System.out.println("-gui, -file, and -performance are pariwise " +
                    "incompatible. Use one.");
            System.exit(0);

        }
        if (!this.gui && this.file == null && this.performance == -1) {
            System.out.println("Use one of -gui, -file, or -performance");
            System.exit(0);
        }
    }

    public Dimension parseResolution(String res) {
        int width = 0, height = 0;
        String message = "Please specify resolution as WIDTHxHEIGHT, " +
                "where WIDTH and HEIGHT are integers.";
        String[] parts = res.split("x");
        if (parts.length != 2) {
            System.out.println(message);
            System.exit(0);
        }
        try {
            width = Integer.parseInt(parts[0]);
            height = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e2) {
            System.out.println(message);
            System.exit(0);
        }
        return new Dimension(width, height);
    }

}