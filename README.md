# Pharos Liquidation Monitor Agent

An intelligent DeFi agent developed in **Java** for the Pharos Agent Arena designed to monitor lending position health factors on-chain and trigger proactive liquidation warnings[cite: 5].

## 📋 Requirements Met

1. **Position On-Chain Reading (Req. 01):** Calculates and monitors collateral, debt, and Health Factor (HF) dynamically[cite: 5].
2. **Configurable Alert Thresholds (Req. 02):** Supports multi-tier alert limits (Warning at HF <= 1.3, Critical at HF <= 1.1)[cite: 5].
3. **Multi-Channel Timed Alerts (Req. 03):** Dispatches detailed, timestamped console log alerts ready for webhook integration[cite: 5].
4. **Actionable Recommendations (Req. 04):** Provides exact mathematical recommendations for each alert (e.g., "Add $X collateral or repay $Y debt")[cite: 5].

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac LiquidationMonitor.java
