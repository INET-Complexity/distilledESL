package agents;

import contracts.Shares;

public interface CanIssueShares {
    double getNetAssetValue();
    int getnShares();
    Shares issueShares(Agent owner, int quantity);

}