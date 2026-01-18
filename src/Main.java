import java.io.*;
import java.util.Locale;

/**
 * Main entry point for GigMatch Pro platform.
 */
public class Main {
    private static Platform platform = new Platform();

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                processCommand(line, writer);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer) throws IOException {
        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "register_customer":
                    result = platform.registerCustomer(parts[1]);
                    break;

                case "register_freelancer":
                    result = platform.registerFreelancer(
                            parts[1], parts[2],
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6]),
                            Integer.parseInt(parts[7]),
                            Integer.parseInt(parts[8])
                    );
                    break;

                case "request_job":
                    result = platform.requestJob(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;

                case "employ_freelancer":
                    result = platform.employFreelancer(parts[1], parts[2]);
                    break;

                case "complete_and_rate":
                    result = platform.completeAndRate(parts[1], Integer.parseInt(parts[2]));
                    break;

                case "cancel_by_freelancer":
                    result = platform.cancelByFreelancer(parts[1]);
                    break;

                case "cancel_by_customer":
                    result = platform.cancelByCustomer(parts[1], parts[2]);
                    break;

                case "blacklist":
                    result = platform.blacklist(parts[1], parts[2]);
                    break;

                case "unblacklist":
                    result = platform.unblacklist(parts[1], parts[2]);
                    break;

                case "change_service":
                    result = platform.changeService(parts[1], parts[2], Integer.parseInt(parts[3]));
                    break;

                case "simulate_month":
                    result = platform.simulateMonth();
                    break;

                case "query_freelancer":
                    result = platform.queryFreelancer(parts[1]);
                    break;

                case "query_customer":
                    result = platform.queryCustomer(parts[1]);
                    break;

                case "update_skill":
                    result = platform.updateSkill(
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6])
                    );
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();

        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }
    }
}