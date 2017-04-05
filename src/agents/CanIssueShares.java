package agents;

import contracts.Shares;

public interface CanIssueShares {
    double getNetAssetValue();
    int getnShares();
    Shares issueShares(StressAgent owner, int quantity);

}