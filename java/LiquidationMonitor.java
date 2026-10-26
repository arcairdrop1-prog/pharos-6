import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LiquidationMonitor {

    // Configurable Alert Thresholds (Requirement 02)
    private static final double WARNING_HEALTH_FACTOR = 1.30;
    private static final double CRITICAL_HEALTH_FACTOR = 1.10;
    private static final double LIQUIDATION_THRESHOLD = 1.00;

    // Data structure for Lending Position (Requirement 01)
    public static class LendingPosition {
        double collateralUSD;
        double debtUSD;
        double liquidationThresholdRatio; // e.g., 0.80 for 80% LTV threshold

        public LendingPosition(double collateralUSD, double debtUSD, double liquidationThresholdRatio) {
            this.collateralUSD = collateralUSD;
            this.debtUSD = debtUSD;
            this.liquidationThresholdRatio = liquidationThresholdRatio;
        }

        // Calculates Health Factor = (Collateral * Liquidation Threshold) / Debt (Requirement 01)
        public double calculateHealthFactor() {
            if (debtUSD <= 0) return 999.0; // Safe position with no debt
            return (collateralUSD * liquidationThresholdRatio) / debtUSD;
        }
    }

    // Requirement 01: Reads collateral, debt, and Health Factor from the chain position
    public void monitorPosition(LendingPosition position) {
        double healthFactor = position.calculateHealthFactor();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("\n------------------------------------------------------------");
        System.out.printf("[%s] POSITION STATUS READ:\n", timestamp);
        System.out.printf("   - Collateral: $%.2f USD\n", position.collateralUSD);
        System.out.printf("   - Debt: $%.2f USD\n", position.debtUSD);
        System.out.printf("   - Health Factor (HF): %.2f\n", healthFactor);

        evaluateAndAlert(position, healthFactor, timestamp);
    }

    // Requirements 02, 03 & 04: Evaluates threshold, sends timestamped alerts, and suggests action
    private void evaluateAndAlert(LendingPosition position, double healthFactor, String timestamp) {
        if (healthFactor <= CRITICAL_HEALTH_FACTOR) {
            // Requirement 02: Critical Alert at <= 1.1
            // Requirement 04: Concrete Action Suggestion
            double neededCollateral = ((position.debtUSD * WARNING_HEALTH_FACTOR) / position.liquidationThresholdRatio) - position.collateralUSD;
            double neededDebtRepayment = position.debtUSD - ((position.collateralUSD * position.liquidationThresholdRatio) / WARNING_HEALTH_FACTOR);

            String message = String.format(
                "🚨 [CRITICAL ALERT - %s]\n" +
                "Health Factor dropped to %.2f! IMMEDIATE LIQUIDATION RISK!\n" +
                "💡 RECOMMENDED ACTION: Deposit $%.2f USD more collateral OR repay $%.2f USD of debt to restore HF to safe level (1.30).",
                timestamp, healthFactor, Math.max(0, neededCollateral), Math.max(0, neededDebtRepayment)
            );

            sendConsoleAlert(message);

        } else if (healthFactor <= WARNING_HEALTH_FACTOR) {
            // Requirement 02: Warning Alert at <= 1.3
            double neededCollateral = ((position.debtUSD * WARNING_HEALTH_FACTOR) / position.liquidationThresholdRatio) - position.collateralUSD;

            String message = String.format(
                "⚠️ [WARNING ALERT - %s]\n" +
                "Health Factor is at %.2f. Position approaching risk zone.\n" +
                "💡 RECOMMENDED ACTION: Add $%.2f USD in collateral to increase your buffer before market volatility strikes.",
                timestamp, healthFactor, Math.max(0, neededCollateral)
            );

            sendConsoleAlert(message);

        } else {
            System.out.printf("[%s] ✅ Position Healthy (HF: %.2f > Safe Limit 1.30). No action required.\n", timestamp, healthFactor);
        }
    }

    // Requirement 03: Sends alerts via Console with timestamps (simulating Telegram/Discord webhook logs)
    private void sendConsoleAlert(String alertMessage) {
        System.out.println(">>> ALERT DISPATCHED TO NOTIFICATION CHANNEL <<<");
        System.out.println(alertMessage);
        System.out.println("------------------------------------------------------------");
    }

    public static void main(String[] args) {
        LiquidationMonitor monitor = new LiquidationMonitor();

        System.out.println("=== STARTING LIQUIDATION MONITOR AGENT (TESTNET SIMULATION) ===");

        // Scenario 1: Safe Position
        System.out.println("\n--- Testing Scenario 1: Safe Position ---");
        LendingPosition safePos = new LendingPosition(10000.0, 5000.0, 0.80); // HF = 1.60
        monitor.monitorPosition(safePos);

        // Scenario 2: Warning Threshold (HF <= 1.3)
        System.out.println("\n--- Testing Scenario 2: Warning Threshold ---");
        LendingPosition warningPos = new LendingPosition(10000.0, 6400.0, 0.80); // HF = 1.25
        monitor.monitorPosition(warningPos);

        // Scenario 3: Critical Threshold (HF <= 1.1)
        System.out.println("\n--- Testing Scenario 3: Critical Threshold ---");
        LendingPosition criticalPos = new LendingPosition(10000.0, 7500.0, 0.80); // HF = 1.06
        monitor.monitorPosition(criticalPos);
    }
}
